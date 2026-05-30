package net.qiuyu.horrorcooked9.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.qiuyu.horrorcooked9.config.HookMonsterRuntimeConfig;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class MonsterConfigCommand {

    private static final SimpleCommandExceptionType UNKNOWN_KEY = new SimpleCommandExceptionType(
            Component.literal("Unknown config key. Use /horrorCooked monster config query to list all keys."));
    private static final SimpleCommandExceptionType INVALID_VALUE = new SimpleCommandExceptionType(
            Component.literal("Invalid value. Must be a number within the config key range."));

    private MonsterConfigCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("horrorCooked")
                        .then(literal("monster")
                                .then(literal("config")
                                        .requires(source -> source.hasPermission(2))
                                        .then(literal("query")
                                                .then(argument("key", StringArgumentType.word())
                                                        .suggests((ctx, builder) ->
                                                                SharedSuggestionProvider.suggest(HookMonsterRuntimeConfig.KEYS, builder))
                                                        .executes(MonsterConfigCommand::queryValue))
                                                .executes(MonsterConfigCommand::queryAll))
                                        .then(literal("set")
                                                .then(argument("key", StringArgumentType.word())
                                                        .suggests((ctx, builder) ->
                                                                SharedSuggestionProvider.suggest(HookMonsterRuntimeConfig.KEYS, builder))
                                                        .then(argument("value", StringArgumentType.greedyString())
                                                                .executes(MonsterConfigCommand::setValue))))
                                        .then(literal("reset")
                                                .executes(MonsterConfigCommand::resetAll)))));
    }

    private static int queryAll(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        source.sendSuccess(() -> Component.literal("=== Hook Monster Config ==="), false);
        for (String key : HookMonsterRuntimeConfig.KEYS) {
            double value = getEffectiveValue(key);
            boolean overridden = HookMonsterRuntimeConfig.hasRuntimeOverride(key);
            String marker = overridden ? " [RUNTIME]" : "";
            source.sendSuccess(() -> Component.literal(
                    key + " = " + formatValue(key, value) + marker), false);
        }
        return 1;
    }

    private static int queryValue(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String key = StringArgumentType.getString(ctx, "key");
        if (!HookMonsterRuntimeConfig.isKnownKey(key)) throw UNKNOWN_KEY.create();
        double value = getEffectiveValue(key);
        boolean overridden = HookMonsterRuntimeConfig.hasRuntimeOverride(key);
        String marker = overridden ? " (runtime override)" : " (from config)";
        ctx.getSource().sendSuccess(() -> Component.literal(
                key + " = " + formatValue(key, value) + " " + marker), false);
        return 1;
    }

    private static int setValue(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String key = StringArgumentType.getString(ctx, "key");
        if (!HookMonsterRuntimeConfig.isKnownKey(key)) throw UNKNOWN_KEY.create();
        String valueStr = StringArgumentType.getString(ctx, "value");
        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            throw INVALID_VALUE.create();
        }
        if (!HookMonsterRuntimeConfig.isValueInRange(key, value)) {
            throw INVALID_VALUE.create();
        }
        HookMonsterRuntimeConfig.setRuntimeOverride(key, value);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Set " + key + " = " + formatValue(key, value) + " (runtime override, resets on server restart)"), true);
        return 1;
    }

    private static int resetAll(CommandContext<CommandSourceStack> ctx) {
        int count = HookMonsterRuntimeConfig.resetRuntimeOverrides();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reset " + count + " runtime override(s). All values now use config defaults."), true);
        return count;
    }

    private static double getEffectiveValue(String key) {
        return HookMonsterRuntimeConfig.getDouble(key);
    }

    /** Check if a runtime override exists for the given key. */
    public static boolean hasRuntimeOverride(String key) {
        return HookMonsterRuntimeConfig.hasRuntimeOverride(key);
    }

    /**
     * Get the effective value for a key, considering runtime overrides.
     * Entity code should use this instead of accessing ModServerConfig directly
     * if it wants runtime-overridable values.
     */
    public static double getEffective(String key) {
        return HookMonsterRuntimeConfig.getDouble(key);
    }

    public static int getEffectiveInt(String key) {
        return HookMonsterRuntimeConfig.getInt(key);
    }

    private static String formatValue(String key, double value) {
        if (key.equals("damagePercent")) {
            return String.format("%.0f%%", value * 100);
        }
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }
}
