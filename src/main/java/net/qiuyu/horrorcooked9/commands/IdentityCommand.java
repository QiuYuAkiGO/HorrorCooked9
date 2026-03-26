package net.qiuyu.horrorcooked9.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class IdentityCommand {
    private static final String IDENTITY_FIELD = "identity_name";
    private static final String LIMIT_FIELD = "limit_num";

    private static final DynamicCommandExceptionType READABLE_ERROR = new DynamicCommandExceptionType(
            message -> Component.literal(String.valueOf(message))
    );

    private IdentityCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("identity")
                        .then(literal("give")
                                .requires(source -> source.hasPermission(2))
                                .then(argument("playerTag", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            Set<String> playerTags = context.getSource().getServer().getPlayerList().getPlayers().stream()
                                                    .flatMap(player -> player.getTags().stream())
                                                    .collect(Collectors.toSet());
                                            return SharedSuggestionProvider.suggest(playerTags, builder);
                                        })
                                        .then(argument("configJson", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        List.of(
                                                                "{\"identity_name\":{\"captain\":1,\"cook\":3},\"limit_num\":{\"captain\":\"..1\"}}",
                                                                "{\"identity_name\":{\"captain\":2,\"cook\":2},\"limit_num\":{\"captain\":\"1..2\",\"cook\":\"1..\"}}"
                                                        ),
                                                        builder
                                                ))
                                                .executes(IdentityCommand::executeGive)
                                        )
                                )
                        )
        );
    }

    private static int executeGive(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String playerTag = StringArgumentType.getString(context, "playerTag");
        String configJson = StringArgumentType.getString(context, "configJson");

        if (configJson == null || configJson.trim().isEmpty()) {
            throw fail("JSON字符串不能为空。示例：{\"identity_name\":{\"captain\":3},\"limit_num\":{\"captain\":\"..1\"}}");
        }

        List<ServerPlayer> candidates = source.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.getTags().contains(playerTag))
                .collect(Collectors.toCollection(ArrayList::new));

        if (candidates.isEmpty()) {
            throw fail("未找到带有tag '" + playerTag + "' 的在线玩家。");
        }

        IdentityConfig config = parseAndValidateConfig(configJson, candidates.size());
        Map<String, Integer> counts = allocateByWeightWithLimits(config.identityWeights(), config.limits(), candidates.size());
        applyAssignments(candidates, config.identityWeights().keySet(), counts);

        String statText = counts.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(
                "身份分配完成：tag=" + playerTag + "，玩家数=" + candidates.size() + "，结果[" + statText + "]"
        ), false);
        return candidates.size();
    }

    private static IdentityConfig parseAndValidateConfig(String configJson, int playerCount) throws CommandSyntaxException {
        JsonObject root = parseRootObject(configJson);
        JsonObject identityObject = getRequiredObject(root, IDENTITY_FIELD);
        JsonObject limitObject = root.has(LIMIT_FIELD) ? getRequiredObject(root, LIMIT_FIELD) : new JsonObject();

        if (identityObject.entrySet().isEmpty()) {
            throw fail("字段 '" + IDENTITY_FIELD + "' 不能为空，至少需要定义一个身份及其权重。");
        }

        Map<String, Integer> identityWeights = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : identityObject.entrySet()) {
            String identity = entry.getKey();
            int weight = parsePositiveInt(entry.getValue(), "identity_name." + identity);
            if (weight <= 0) {
                throw fail("字段 'identity_name." + identity + "' 必须为正整数。");
            }
            identityWeights.put(identity, weight);
        }

        Map<String, IntRange> explicitLimits = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : limitObject.entrySet()) {
            String identity = entry.getKey();
            if (!identityWeights.containsKey(identity)) {
                throw fail("字段 'limit_num." + identity + "' 未在 'identity_name' 中定义。");
            }
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString()) {
                throw fail("字段 'limit_num." + identity + "' 必须是字符串区间，如 '..1' 或 '1..3'。");
            }
            String rangeText = entry.getValue().getAsString();
            IntRange range = parseRange(rangeText, "limit_num." + identity);
            if (range.min() < 0) {
                throw fail("字段 'limit_num." + identity + "' 下限不能小于0。");
            }
            explicitLimits.put(identity, range);
        }

        Map<String, IntRange> effectiveLimits = new LinkedHashMap<>();
        long sumMin = 0L;
        long sumMax = 0L;
        for (String identity : identityWeights.keySet()) {
            IntRange rawRange = explicitLimits.getOrDefault(identity, new IntRange(0, Integer.MAX_VALUE));
            int cappedMax = rawRange.max() == Integer.MAX_VALUE ? playerCount : Math.min(rawRange.max(), playerCount);
            IntRange effectiveRange = new IntRange(rawRange.min(), cappedMax);
            if (effectiveRange.min() > effectiveRange.max()) {
                throw fail("字段 'limit_num." + identity + "' 不可满足：下限大于上限。");
            }
            effectiveLimits.put(identity, effectiveRange);
            sumMin += effectiveRange.min();
            sumMax += effectiveRange.max();
        }

        if (sumMin > playerCount) {
            throw fail("配置不可满足：所有身份下限之和为 " + sumMin + "，但候选玩家只有 " + playerCount + " 人。");
        }
        if (sumMax < playerCount) {
            throw fail("配置不可满足：所有身份上限之和为 " + sumMax + "，无法覆盖 " + playerCount + " 名玩家。");
        }

        return new IdentityConfig(identityWeights, effectiveLimits);
    }

    private static JsonObject parseRootObject(String configJson) throws CommandSyntaxException {
        try {
            JsonElement rootElement = JsonParser.parseString(configJson);
            if (!rootElement.isJsonObject()) {
                throw fail("JSON根节点必须是对象，例如 {\"identity_name\":{...},\"limit_num\":{...}}");
            }
            return rootElement.getAsJsonObject();
        } catch (JsonParseException ex) {
            String detail = ex.getMessage();
            if (detail == null || detail.isEmpty()) {
                detail = "未知语法错误";
            }
            throw fail("JSON解析失败，请检查语法。错误信息：" + detail);
        }
    }

    private static JsonObject getRequiredObject(JsonObject root, String fieldName) throws CommandSyntaxException {
        if (!root.has(fieldName)) {
            throw fail("缺少必填字段 '" + fieldName + "'。");
        }
        JsonElement element = root.get(fieldName);
        if (!element.isJsonObject()) {
            throw fail("字段 '" + fieldName + "' 必须是对象。");
        }
        return element.getAsJsonObject();
    }

    private static int parsePositiveInt(JsonElement element, String fieldPath) throws CommandSyntaxException {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw fail("字段 '" + fieldPath + "' 必须是数字。");
        }
        try {
            return element.getAsInt();
        } catch (NumberFormatException ex) {
            throw fail("字段 '" + fieldPath + "' 不是有效整数。");
        }
    }

    private static IntRange parseRange(String rangeText, String fieldPath) throws CommandSyntaxException {
        String trimmed = rangeText == null ? "" : rangeText.trim();
        if (trimmed.isEmpty()) {
            throw fail("字段 '" + fieldPath + "' 不能为空，支持 '..n'、'm..'、'm..n'、'n'。");
        }

        int separatorIndex = trimmed.indexOf("..");
        int min;
        int max;

        if (separatorIndex < 0) {
            try {
                min = Integer.parseInt(trimmed);
                max = min;
            } catch (NumberFormatException ex) {
                throw fail("字段 '" + fieldPath + "' 格式错误：'" + rangeText + "'。示例：'..1'、'1..3'。");
            }
            return new IntRange(min, max);
        }

        String minText = trimmed.substring(0, separatorIndex).trim();
        String maxText = trimmed.substring(separatorIndex + 2).trim();
        if (minText.isEmpty() && maxText.isEmpty()) {
            throw fail("字段 '" + fieldPath + "' 格式错误：不能写成 '..'。");
        }

        try {
            min = minText.isEmpty() ? 0 : Integer.parseInt(minText);
            max = maxText.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxText);
        } catch (NumberFormatException ex) {
            throw fail("字段 '" + fieldPath + "' 格式错误：'" + rangeText + "'。示例：'..1'、'1..3'。");
        }

        if (min > max) {
            throw fail("字段 '" + fieldPath + "' 格式错误：下限不能大于上限。");
        }
        return new IntRange(min, max);
    }

    private static Map<String, Integer> allocateByWeightWithLimits(Map<String, Integer> identityWeights,
                                                                   Map<String, IntRange> limits,
                                                                   int playerCount) throws CommandSyntaxException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String identity : identityWeights.keySet()) {
            counts.put(identity, limits.get(identity).min());
        }

        int assigned = counts.values().stream().mapToInt(Integer::intValue).sum();
        int remaining = playerCount - assigned;

        while (remaining > 0) {
            List<String> candidates = new ArrayList<>();
            for (String identity : identityWeights.keySet()) {
                if (counts.get(identity) < limits.get(identity).max()) {
                    candidates.add(identity);
                }
            }

            if (candidates.isEmpty()) {
                throw fail("配置不可满足：达到各身份上限后仍有 " + remaining + " 名玩家未分配。");
            }

            String pickedIdentity = pickWeightedRandom(candidates, identityWeights);
            counts.put(pickedIdentity, counts.get(pickedIdentity) + 1);
            remaining--;
        }
        return counts;
    }

    private static String pickWeightedRandom(List<String> identities, Map<String, Integer> identityWeights) {
        long totalWeight = 0L;
        for (String identity : identities) {
            totalWeight += identityWeights.get(identity);
        }

        long random = ThreadLocalRandom.current().nextLong(totalWeight);
        long cumulative = 0L;
        for (String identity : identities) {
            cumulative += identityWeights.get(identity);
            if (random < cumulative) {
                return identity;
            }
        }
        return identities.get(identities.size() - 1);
    }

    private static void applyAssignments(List<ServerPlayer> players, Set<String> identityTags, Map<String, Integer> counts) {
        List<String> assignedIdentities = new ArrayList<>(players.size());
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                assignedIdentities.add(entry.getKey());
            }
        }

        Collections.shuffle(players, ThreadLocalRandom.current());
        Collections.shuffle(assignedIdentities, ThreadLocalRandom.current());

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            for (String identityTag : identityTags) {
                player.removeTag(identityTag);
            }
            player.addTag(assignedIdentities.get(i));
        }
    }

    private static CommandSyntaxException fail(String message) {
        return READABLE_ERROR.create(message);
    }

    private record IdentityConfig(Map<String, Integer> identityWeights, Map<String, IntRange> limits) {
    }

    private record IntRange(int min, int max) {
    }
}
