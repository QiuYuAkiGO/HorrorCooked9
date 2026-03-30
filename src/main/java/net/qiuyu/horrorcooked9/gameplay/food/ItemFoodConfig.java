package net.qiuyu.horrorcooked9.gameplay.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ItemFoodConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/item_foods.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/item_foods.json";
    private static final FoodProfile DEFAULT_PROFILE = new FoodProfile(false, 0, 0.0F, false, List.of());
    private static final FoodData FALLBACK_DATA = new FoodData(DEFAULT_PROFILE, Map.of());

    @Nullable
    private static volatile FoodData classpathCache;

    private ItemFoodConfig() {
    }

    public static FoodProfile resolveProfile(@Nullable ResourceManager resourceManager, ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            return DEFAULT_PROFILE;
        }
        return resolveProfile(resourceManager, itemId);
    }

    public static FoodProfile resolveProfile(@Nullable ResourceManager resourceManager, ResourceLocation itemId) {
        FoodData data = load(resourceManager);
        return data.itemsById.getOrDefault(itemId, data.defaultProfile);
    }

    @Nullable
    public static FoodProperties resolveFoodProperties(@Nullable ResourceManager resourceManager, ItemStack stack) {
        FoodProfile profile = resolveProfile(resourceManager, stack);
        if (!profile.edible) {
            return null;
        }

        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(Math.max(0, profile.nutrition))
                .saturationMod(Math.max(0.0F, profile.saturationModifier));

        if (profile.alwaysEdible) {
            builder.alwaysEat();
        }

        for (FoodEffectProfile foodEffect : profile.effects) {
            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(foodEffect.effectId);
            if (mobEffect == null) {
                continue;
            }
            builder.effect(() -> new MobEffectInstance(
                    mobEffect,
                    Math.max(1, foodEffect.durationTicks),
                    Math.max(0, foodEffect.amplifier)
            ), clampProbability(foodEffect.probability));
        }

        return builder.build();
    }

    private static float clampProbability(float probability) {
        return Math.max(0.0F, Math.min(1.0F, probability));
    }

    private static FoodData load(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<FoodData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                return loaded.get();
            }
        }
        return loadFromClasspathCached();
    }

    private static Optional<FoodData> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse food config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static FoodData loadFromClasspathCached() {
        FoodData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (ItemFoodConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = ItemFoodConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Food config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load food config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = FALLBACK_DATA;
            }
            return classpathCache;
        }
    }

    private static FoodData parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return FALLBACK_DATA;
            }
            root = element.getAsJsonObject();
        }

        FoodProfile defaultProfile = DEFAULT_PROFILE;
        if (root.has("default") && root.get("default").isJsonObject()) {
            defaultProfile = parseProfile(root.getAsJsonObject("default"));
        }

        Map<ResourceLocation, FoodProfile> itemProfiles = new HashMap<>();
        if (root.has("items") && root.get("items").isJsonObject()) {
            JsonObject itemsObject = root.getAsJsonObject("items");
            for (Map.Entry<String, JsonElement> entry : itemsObject.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                try {
                    ResourceLocation itemId = ResourceLocation.parse(entry.getKey());
                    FoodProfile profile = parseProfile(entry.getValue().getAsJsonObject());
                    itemProfiles.put(itemId, profile);
                } catch (Exception e) {
                    LOGGER.warn("Invalid item id in food config: {}", entry.getKey(), e);
                }
            }
        }

        return new FoodData(defaultProfile, Map.copyOf(itemProfiles));
    }

    private static FoodProfile parseProfile(JsonObject object) {
        boolean edible = GsonHelper.getAsBoolean(object, "edible", false);
        int nutrition = Math.max(0, GsonHelper.getAsInt(object, "nutrition", 0));
        float saturationModifier = Math.max(0.0F, GsonHelper.getAsFloat(object, "saturation_modifier", 0.0F));
        boolean alwaysEdible = GsonHelper.getAsBoolean(object, "always_edible", false);

        List<FoodEffectProfile> effects = new ArrayList<>();
        if (object.has("effects") && object.get("effects").isJsonArray()) {
            JsonArray effectArray = object.getAsJsonArray("effects");
            for (JsonElement effectElement : effectArray) {
                if (!effectElement.isJsonObject()) {
                    continue;
                }
                JsonObject effectObject = effectElement.getAsJsonObject();
                String effectIdRaw = GsonHelper.getAsString(effectObject, "id", "");
                if (effectIdRaw.isEmpty()) {
                    continue;
                }
                try {
                    ResourceLocation effectId = ResourceLocation.parse(effectIdRaw);
                    int durationTicks = Math.max(1, GsonHelper.getAsInt(effectObject, "duration_ticks", 100));
                    int amplifier = Math.max(0, GsonHelper.getAsInt(effectObject, "amplifier", 0));
                    float probability = clampProbability(GsonHelper.getAsFloat(effectObject, "probability", 1.0F));
                    effects.add(new FoodEffectProfile(effectId, durationTicks, amplifier, probability));
                } catch (Exception e) {
                    LOGGER.warn("Invalid food effect id in config: {}", effectIdRaw, e);
                }
            }
        }

        return new FoodProfile(edible, nutrition, saturationModifier, alwaysEdible, List.copyOf(effects));
    }

    public record FoodProfile(
            boolean edible,
            int nutrition,
            float saturationModifier,
            boolean alwaysEdible,
            List<FoodEffectProfile> effects
    ) {
    }

    public record FoodEffectProfile(ResourceLocation effectId, int durationTicks, int amplifier, float probability) {
    }

    private record FoodData(FoodProfile defaultProfile, Map<ResourceLocation, FoodProfile> itemsById) {
    }
}
