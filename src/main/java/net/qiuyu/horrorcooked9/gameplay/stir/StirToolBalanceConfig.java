package net.qiuyu.horrorcooked9.gameplay.stir;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 读取并解析搅拌工具平衡配置。
 * <p>
 * 配置优先从当前 {@link ResourceManager} 加载（支持数据包热替换），
 * 若不可用或解析失败则回退到 classpath 内置配置，最终再回退到零修正。
 */
public final class StirToolBalanceConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/stir_tool_balance.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/stir_tool_balance.json";
    private static final BalanceData FALLBACK_DATA = new BalanceData(StirToolModifier.ZERO, Map.of());

    @Nullable
    private static volatile BalanceData classpathCache;
    @Nullable
    private static volatile BalanceData runtimeSnapshot;

    private StirToolBalanceConfig() {
    }

    /**
     * 根据配方基础搅拌次数与工具修正，得到最终需要完成的搅拌轮次。
     */
    public static int resolveEffectiveStirCount(@Nullable ResourceManager resourceManager, ItemStack toolStack, int recipeStirCount) {
        StirToolModifier modifier = resolveModifier(resourceManager, toolStack);
        return modifier.applyStirCount(recipeStirCount);
    }

    /**
     * 根据配方基础成功率与工具修正，得到最终结算成功率。
     */
    public static float resolveEffectiveSuccessChance(@Nullable ResourceManager resourceManager, ItemStack toolStack, float recipeSuccessChance) {
        StirToolModifier modifier = resolveModifier(resourceManager, toolStack);
        return modifier.applySuccessChance(recipeSuccessChance);
    }

    /**
     * 解析指定工具对应的修正值；未命中时返回 default 修正。
     */
    public static StirToolModifier resolveModifier(@Nullable ResourceManager resourceManager, ItemStack toolStack) {
        if (toolStack.isEmpty()) {
            return StirToolModifier.ZERO;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(toolStack.getItem());
        if (itemId == null) {
            return StirToolModifier.ZERO;
        }

        BalanceData data = load(resourceManager);
        return data.toolsByItemId.getOrDefault(itemId, data.defaultModifier);
    }

    public static void reload(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<BalanceData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                runtimeSnapshot = loaded.get();
                return;
            }
        }
        runtimeSnapshot = loadFromClasspathCached();
    }

    private static BalanceData load(@Nullable ResourceManager resourceManager) {
        BalanceData current = runtimeSnapshot;
        if (current != null) {
            return current;
        }
        if (resourceManager != null) {
            Optional<BalanceData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                return loaded.get();
            }
        }
        return loadFromClasspathCached();
    }

    private static Optional<BalanceData> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse stir tool balance config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static BalanceData loadFromClasspathCached() {
        BalanceData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (StirToolBalanceConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = StirToolBalanceConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Stir tool balance config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load stir tool balance config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = FALLBACK_DATA;
            }
            return classpathCache;
        }
    }

    private static BalanceData parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return FALLBACK_DATA;
            }
            root = element.getAsJsonObject();
        }

        StirToolModifier defaultModifier = StirToolModifier.ZERO;
        if (root.has("default") && root.get("default").isJsonObject()) {
            defaultModifier = parseModifier(root.getAsJsonObject("default"));
        }

        Map<ResourceLocation, StirToolModifier> toolsByItemId = new HashMap<>();
        if (root.has("tools") && root.get("tools").isJsonObject()) {
            JsonObject toolsObject = root.getAsJsonObject("tools");
            for (Map.Entry<String, JsonElement> entry : toolsObject.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                try {
                    ResourceLocation itemId = ResourceLocation.parse(entry.getKey());
                    StirToolModifier modifier = parseModifier(entry.getValue().getAsJsonObject());
                    toolsByItemId.put(itemId, modifier);
                } catch (Exception ignored) {
                    LOGGER.warn("Invalid stir tool item id in config: {}", entry.getKey());
                }
            }
        }

        return new BalanceData(defaultModifier, Map.copyOf(toolsByItemId));
    }

    private static StirToolModifier parseModifier(JsonObject object) {
        float chanceDelta = GsonHelper.getAsFloat(object, "success_chance_delta", 0.0F);
        int stirCountDelta = GsonHelper.getAsInt(object, "stir_count_delta", 0);
        return new StirToolModifier(chanceDelta, stirCountDelta);
    }

    /**
     * 解析后的配置快照，包含默认修正与按物品 ID 映射的修正表。
     */
    private record BalanceData(StirToolModifier defaultModifier, Map<ResourceLocation, StirToolModifier> toolsByItemId) {
    }
}
