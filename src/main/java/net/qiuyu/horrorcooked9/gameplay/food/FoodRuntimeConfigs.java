package net.qiuyu.horrorcooked9.gameplay.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.food.FoodProperties;
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
 * Runtime food-related gameplay config loaded from datapack JSON.
 * <p>
 * The loader keeps an immutable snapshot in memory and falls back to built-in classpath defaults.
 */
public final class FoodRuntimeConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/item_foods.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/item_foods.json";

    private static final int DEFAULT_MAX_FOOD_LEVEL = 20;

    private static final DiarrheaEventProfile DEFAULT_DIARRHEA_EVENTS = new DiarrheaEventProfile(
            200,
            40,
            40,
            0.75F,
            2.0D,
            0.4D,
            0.2D,
            0.2D
    );

    private static final FoodRuntimeProfile EMPTY_ITEM_PROFILE = new FoodRuntimeProfile(
            null,
            null,
            null,
            null,
            InventoryConsumeEffectProfile.disabled()
    );

    private static final Snapshot EMPTY_SNAPSHOT = new Snapshot(Map.of(), DEFAULT_DIARRHEA_EVENTS);

    @Nullable
    private static volatile Snapshot classpathCache;

    private static volatile Snapshot runtimeSnapshot = EMPTY_SNAPSHOT;

    private FoodRuntimeConfigs() {
    }

    public static void reload(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<Snapshot> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                runtimeSnapshot = loaded.get();
                LOGGER.info("Loaded food runtime config from datapack: {}", CONFIG_ID);
                return;
            }
        }
        runtimeSnapshot = loadFromClasspathCached();
        LOGGER.info("Using built-in food runtime config fallback: {}", CLASSPATH_CONFIG_PATH);
    }

    public static Snapshot current() {
        Snapshot snapshot = runtimeSnapshot;
        if (snapshot == EMPTY_SNAPSHOT) {
            snapshot = loadFromClasspathCached();
            runtimeSnapshot = snapshot;
        }
        return snapshot;
    }

    public static FoodProperties resolveRegistrationFoodProperties(ResourceLocation itemId, int fallbackNutrition, float fallbackSaturation) {
        FoodRuntimeProfile profile = current().itemsById().get(itemId);
        int nutrition = profile != null && profile.nutrition() != null ? sanitizeNutrition(profile.nutrition()) : sanitizeNutrition(fallbackNutrition);
        float saturation = profile != null && profile.saturationModifier() != null ? sanitizeSaturation(profile.saturationModifier()) : sanitizeSaturation(fallbackSaturation);
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build();
    }

    public static int resolveUses(ResourceLocation itemId, int fallbackUses) {
        FoodRuntimeProfile profile = current().itemsById().get(itemId);
        if (profile == null || profile.uses() == null) {
            return Math.max(1, fallbackUses);
        }
        return Math.max(1, profile.uses());
    }

    public static int resolveBarColor(ResourceLocation itemId, int fallbackColor) {
        FoodRuntimeProfile profile = current().itemsById().get(itemId);
        if (profile == null || profile.barColor() == null) {
            return fallbackColor;
        }
        return profile.barColor();
    }

    public static InventoryConsumeEffectProfile resolveInventoryConsumeEffect(ResourceLocation itemId, InventoryConsumeEffectProfile fallback) {
        FoodRuntimeProfile profile = current().itemsById().get(itemId);
        if (profile == null) {
            return fallback;
        }
        return profile.inventoryConsumeEffect();
    }

    public static DiarrheaEventProfile resolveDiarrheaEvents() {
        return current().diarrheaEvents();
    }

    public static Optional<FoodRuntimeProfile> resolveProfile(ResourceLocation itemId) {
        return Optional.ofNullable(current().itemsById().get(itemId));
    }

    private static Optional<Snapshot> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse food runtime config from datapack: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static Snapshot loadFromClasspathCached() {
        Snapshot cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (FoodRuntimeConfigs.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = FoodRuntimeConfigs.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Food runtime config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = EMPTY_SNAPSHOT;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load food runtime config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = EMPTY_SNAPSHOT;
            }
            return classpathCache;
        }
    }

    private static Snapshot parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return EMPTY_SNAPSHOT;
            }
            root = element.getAsJsonObject();
        }

        Map<ResourceLocation, FoodRuntimeProfile> itemsById = new HashMap<>();
        if (root.has("items") && root.get("items").isJsonObject()) {
            JsonObject items = root.getAsJsonObject("items");
            for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                try {
                    ResourceLocation itemId = ResourceLocation.parse(entry.getKey());
                    if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
                        LOGGER.warn("Unknown item id in item_foods.json: {}", itemId);
                    }
                    itemsById.put(itemId, parseItemProfile(itemId, entry.getValue().getAsJsonObject()));
                } catch (Exception ignored) {
                    LOGGER.warn("Invalid item id in item_foods.json: {}", entry.getKey());
                }
            }
        }

        DiarrheaEventProfile diarrheaEvents = DEFAULT_DIARRHEA_EVENTS;
        if (root.has("diarrhea_events") && root.get("diarrhea_events").isJsonObject()) {
            diarrheaEvents = parseDiarrheaEvents(root.getAsJsonObject("diarrhea_events"));
        }

        return new Snapshot(Map.copyOf(itemsById), diarrheaEvents);
    }

    private static FoodRuntimeProfile parseItemProfile(ResourceLocation itemId, JsonObject object) {
        Integer nutrition = object.has("nutrition") ? sanitizeNutrition(GsonHelper.getAsInt(object, "nutrition")) : null;
        Float saturationModifier = object.has("saturation_mod") ? sanitizeSaturation(GsonHelper.getAsFloat(object, "saturation_mod")) : null;
        Integer uses = object.has("uses") ? Math.max(1, GsonHelper.getAsInt(object, "uses")) : null;
        Integer barColor = parseOptionalColor(object.get("bar_color"));

        InventoryConsumeEffectProfile inventoryConsumeEffect = InventoryConsumeEffectProfile.disabled();
        if (object.has("inventory_consume_effect") && object.get("inventory_consume_effect").isJsonObject()) {
            inventoryConsumeEffect = parseInventoryConsumeEffect(itemId, object.getAsJsonObject("inventory_consume_effect"));
        }

        return new FoodRuntimeProfile(nutrition, saturationModifier, uses, barColor, inventoryConsumeEffect);
    }

    private static InventoryConsumeEffectProfile parseInventoryConsumeEffect(ResourceLocation itemId, JsonObject object) {
        boolean enabled = GsonHelper.getAsBoolean(object, "enabled", true);
        ResourceLocation effectId = parseResourceLocationOrDefault(object, "effect", ResourceLocation.parse("horrorcooked9:diarrhea"));
        int durationPerCountTicks = Math.max(1, GsonHelper.getAsInt(object, "duration_per_count_ticks", 5 * 20));
        int maxDurationTicks = Math.max(durationPerCountTicks, GsonHelper.getAsInt(object, "max_duration_ticks", 120 * 20));
        int maxAmplifier = Math.max(0, GsonHelper.getAsInt(object, "max_amplifier", 4));
        boolean ambient = GsonHelper.getAsBoolean(object, "ambient", false);
        boolean visible = GsonHelper.getAsBoolean(object, "visible", true);

        if (!ForgeRegistries.MOB_EFFECTS.containsKey(effectId)) {
            LOGGER.warn("Unknown effect id in inventory_consume_effect for {}: {}", itemId, effectId);
        }

        return new InventoryConsumeEffectProfile(
                enabled,
                effectId,
                durationPerCountTicks,
                maxDurationTicks,
                maxAmplifier,
                ambient,
                visible
        );
    }

    private static DiarrheaEventProfile parseDiarrheaEvents(JsonObject object) {
        int procIntervalTicks = Math.max(1, GsonHelper.getAsInt(object, "proc_interval_ticks", DEFAULT_DIARRHEA_EVENTS.procIntervalTicks()));
        int basePenaltyDurationTicks = Math.max(1, GsonHelper.getAsInt(object, "base_penalty_duration_ticks", DEFAULT_DIARRHEA_EVENTS.basePenaltyDurationTicks()));
        int stopDurationTicks = Math.max(1, GsonHelper.getAsInt(object, "stop_duration_ticks", DEFAULT_DIARRHEA_EVENTS.stopDurationTicks()));
        float soundOnlyChance = clamp01(GsonHelper.getAsFloat(object, "sound_only_chance", DEFAULT_DIARRHEA_EVENTS.soundOnlyChance()));
        double dropDistance = Math.max(0.0D, GsonHelper.getAsDouble(object, "drop_distance", DEFAULT_DIARRHEA_EVENTS.dropDistance()));
        double dropVerticalOffset = GsonHelper.getAsDouble(object, "drop_vertical_offset", DEFAULT_DIARRHEA_EVENTS.dropVerticalOffset());
        double dropSpeed = Math.max(0.0D, GsonHelper.getAsDouble(object, "drop_speed", DEFAULT_DIARRHEA_EVENTS.dropSpeed()));
        double dropUpwardSpeed = Math.max(0.0D, GsonHelper.getAsDouble(object, "drop_upward_speed", DEFAULT_DIARRHEA_EVENTS.dropUpwardSpeed()));

        return new DiarrheaEventProfile(
                procIntervalTicks,
                basePenaltyDurationTicks,
                stopDurationTicks,
                soundOnlyChance,
                dropDistance,
                dropVerticalOffset,
                dropSpeed,
                dropUpwardSpeed
        );
    }

    @Nullable
    private static Integer parseOptionalColor(@Nullable JsonElement colorElement) {
        if (colorElement == null || colorElement.isJsonNull()) {
            return null;
        }
        try {
            if (colorElement.isJsonPrimitive() && colorElement.getAsJsonPrimitive().isNumber()) {
                return colorElement.getAsInt() & 0xFFFFFF;
            }
            String raw = colorElement.getAsString().trim();
            if (raw.startsWith("#")) {
                raw = raw.substring(1);
            }
            return Integer.parseInt(raw, 16) & 0xFFFFFF;
        } catch (Exception ignored) {
            LOGGER.warn("Invalid bar_color value in item_foods.json: {}", colorElement);
            return null;
        }
    }

    private static ResourceLocation parseResourceLocationOrDefault(JsonObject object, String key, ResourceLocation fallback) {
        if (!object.has(key)) {
            return fallback;
        }
        try {
            return ResourceLocation.parse(GsonHelper.getAsString(object, key));
        } catch (Exception ignored) {
            LOGGER.warn("Invalid resource location for key '{}' in item_foods.json", key);
            return fallback;
        }
    }

    private static int sanitizeNutrition(int nutrition) {
        return Math.max(0, Math.min(DEFAULT_MAX_FOOD_LEVEL, nutrition));
    }

    private static float sanitizeSaturation(float saturation) {
        return Math.max(0.0F, Math.min(2.0F, saturation));
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public record Snapshot(Map<ResourceLocation, FoodRuntimeProfile> itemsById, DiarrheaEventProfile diarrheaEvents) {
    }

    public record FoodRuntimeProfile(
            @Nullable Integer nutrition,
            @Nullable Float saturationModifier,
            @Nullable Integer uses,
            @Nullable Integer barColor,
            InventoryConsumeEffectProfile inventoryConsumeEffect
    ) {
        public FoodRuntimeProfile {
            if (inventoryConsumeEffect == null) {
                inventoryConsumeEffect = InventoryConsumeEffectProfile.disabled();
            }
        }
    }

    public record InventoryConsumeEffectProfile(
            boolean enabled,
            ResourceLocation effectId,
            int durationPerCountTicks,
            int maxDurationTicks,
            int maxAmplifier,
            boolean ambient,
            boolean visible
    ) {
        public static InventoryConsumeEffectProfile disabled() {
            return new InventoryConsumeEffectProfile(false, ResourceLocation.parse("horrorcooked9:diarrhea"), 5 * 20, 120 * 20, 4, false, true);
        }
    }

    public record DiarrheaEventProfile(
            int procIntervalTicks,
            int basePenaltyDurationTicks,
            int stopDurationTicks,
            float soundOnlyChance,
            double dropDistance,
            double dropVerticalOffset,
            double dropSpeed,
            double dropUpwardSpeed
    ) {
    }
}
