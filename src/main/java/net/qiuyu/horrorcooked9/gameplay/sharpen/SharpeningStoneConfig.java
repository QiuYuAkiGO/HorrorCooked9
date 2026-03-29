package net.qiuyu.horrorcooked9.gameplay.sharpen;

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
 * 读取并解析磨刀石配置。
 * <p>
 * 配置优先从当前 {@link ResourceManager} 加载（支持数据包热替换），
 * 若不可用或解析失败则回退到 classpath 内置配置，最终再回退到零修正。
 */
public final class SharpeningStoneConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/sharpening_stones.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/sharpening_stones.json";
    private static final StoneProfile ZERO_PROFILE = new StoneProfile(0, 0.0F, 0, 0.0F, 100);
    private static final BalanceData FALLBACK_DATA = new BalanceData(ZERO_PROFILE, Map.of());

    @Nullable
    private static volatile BalanceData classpathCache;

    private SharpeningStoneConfig() {
    }

    public static int resolveUseDurationTicks(@Nullable ResourceManager resourceManager, ItemStack stoneStack) {
        StoneProfile profile = resolveProfile(resourceManager, stoneStack);
        return Math.max(1, profile.useDurationTicks);
    }

    public static int resolveRepairAmount(@Nullable ResourceManager resourceManager, ItemStack stoneStack, ItemStack cleaverStack) {
        if (cleaverStack.isEmpty() || !cleaverStack.isDamageableItem()) {
            return 0;
        }
        StoneProfile profile = resolveProfile(resourceManager, stoneStack);
        int fixedRepair = Math.max(0, profile.repairAmount);
        int percentRepair = Math.max(0, (int) Math.floor(cleaverStack.getMaxDamage() * Math.max(0.0F, profile.repairPercentOfCleaverMax)));
        return fixedRepair + percentRepair;
    }

    public static int resolveStoneDamage(@Nullable ResourceManager resourceManager, ItemStack stoneStack) {
        StoneProfile profile = resolveProfile(resourceManager, stoneStack);
        int fixedDamage = Math.max(0, profile.stoneDamagePerUse);
        if (!stoneStack.isDamageableItem()) {
            return fixedDamage;
        }
        int percentDamage = Math.max(0, (int) Math.ceil(stoneStack.getMaxDamage() * Math.max(0.0F, profile.stoneDamagePercentOfMax)));
        return fixedDamage + percentDamage;
    }

    public static StoneProfile resolveProfile(@Nullable ResourceManager resourceManager, ItemStack stoneStack) {
        if (stoneStack.isEmpty()) {
            return ZERO_PROFILE;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stoneStack.getItem());
        if (itemId == null) {
            return ZERO_PROFILE;
        }

        BalanceData data = load(resourceManager);
        return data.stonesByItemId.getOrDefault(itemId, data.defaultProfile);
    }

    private static BalanceData load(@Nullable ResourceManager resourceManager) {
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
            LOGGER.warn("Failed to parse sharpening stone config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static BalanceData loadFromClasspathCached() {
        BalanceData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (SharpeningStoneConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = SharpeningStoneConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Sharpening stone config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load sharpening stone config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
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

        StoneProfile defaultProfile = ZERO_PROFILE;
        if (root.has("default") && root.get("default").isJsonObject()) {
            defaultProfile = parseProfile(root.getAsJsonObject("default"));
        }

        Map<ResourceLocation, StoneProfile> stonesByItemId = new HashMap<>();
        if (root.has("stones") && root.get("stones").isJsonObject()) {
            JsonObject stonesObject = root.getAsJsonObject("stones");
            for (Map.Entry<String, JsonElement> entry : stonesObject.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                try {
                    ResourceLocation itemId = ResourceLocation.parse(entry.getKey());
                    StoneProfile profile = parseProfile(entry.getValue().getAsJsonObject());
                    stonesByItemId.put(itemId, profile);
                } catch (Exception ignored) {
                    LOGGER.warn("Invalid sharpening stone item id in config: {}", entry.getKey());
                }
            }
        }

        return new BalanceData(defaultProfile, Map.copyOf(stonesByItemId));
    }

    private static StoneProfile parseProfile(JsonObject object) {
        int repairAmount = GsonHelper.getAsInt(object, "repair_amount", 0);
        float repairPercent = GsonHelper.getAsFloat(object, "repair_percent_of_cleaver_max", 0.0F);
        int stoneDamagePerUse = GsonHelper.getAsInt(object, "stone_damage_per_use", 0);
        float stoneDamagePercent = GsonHelper.getAsFloat(object, "stone_damage_percent_of_max", 0.0F);
        int useDurationTicks = Math.max(1, GsonHelper.getAsInt(object, "use_duration_ticks", 100));
        return new StoneProfile(repairAmount, repairPercent, stoneDamagePerUse, stoneDamagePercent, useDurationTicks);
    }

    public record StoneProfile(
            int repairAmount,
            float repairPercentOfCleaverMax,
            int stoneDamagePerUse,
            float stoneDamagePercentOfMax,
            int useDurationTicks
    ) {
    }

    private record BalanceData(StoneProfile defaultProfile, Map<ResourceLocation, StoneProfile> stonesByItemId) {
    }
}
