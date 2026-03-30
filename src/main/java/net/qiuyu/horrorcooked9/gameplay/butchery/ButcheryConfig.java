package net.qiuyu.horrorcooked9.gameplay.butchery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ButcheryConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/butchery.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/butchery.json";
    private static final ResourceLocation DEFAULT_KILL_TAG = ResourceLocation.parse("horrorcooked9:slaughter_weapons");
    private static final ResourceLocation DEFAULT_HARVEST_TAG = ResourceLocation.parse("horrorcooked9:harvest_tools");
    private static final ResourceLocation DEFAULT_SOUND = ResourceLocation.parse("minecraft:item.axe.strip");
    private static final InteractionProfile DEFAULT_INTERACTION = new InteractionProfile(
            DEFAULT_KILL_TAG,
            DEFAULT_HARVEST_TAG,
            true,
            8,
            1,
            DEFAULT_SOUND
    );
    private static final ButcheryData FALLBACK_DATA = new ButcheryData(DEFAULT_INTERACTION, Map.of(), Map.of());

    @Nullable
    private static volatile ButcheryData classpathCache;

    private ButcheryConfig() {
    }

    public static InteractionProfile resolveInteraction(@Nullable ResourceManager resourceManager) {
        return load(resourceManager).interactionProfile;
    }

    public static Optional<EntityProfile> resolveEntityProfile(@Nullable ResourceManager resourceManager, EntityType<?> entityType) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (entityId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(load(resourceManager).entitiesByType.get(entityId));
    }

    public static Optional<CarcassHarvestProfile> resolveHarvestProfile(@Nullable ResourceManager resourceManager, ItemStack carcassStack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(carcassStack.getItem());
        if (itemId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(load(resourceManager).carcassHarvestByItemId.get(itemId));
    }

    public static boolean isKillWeapon(@Nullable ResourceManager resourceManager, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        InteractionProfile interaction = resolveInteraction(resourceManager);
        return stack.is(TagKey.create(Registries.ITEM, interaction.killWeaponItemTag));
    }

    public static boolean isHarvestTool(@Nullable ResourceManager resourceManager, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        InteractionProfile interaction = resolveInteraction(resourceManager);
        return stack.is(TagKey.create(Registries.ITEM, interaction.harvestToolItemTag));
    }

    public static List<ItemStack> rollEntityExtraDrops(EntityProfile entityProfile, RandomSource random) {
        int rolls = rollRange(random, entityProfile.extraDropRollsMin, entityProfile.extraDropRollsMax);
        return rollWeightedDrops(entityProfile.extraDrops, rolls, random);
    }

    public static List<ItemStack> rollCarcassDrops(CarcassHarvestProfile harvestProfile, RandomSource random) {
        int rolls = rollRange(random, harvestProfile.rollsMin, harvestProfile.rollsMax);
        return rollWeightedDrops(harvestProfile.drops, rolls, random);
    }

    private static List<ItemStack> rollWeightedDrops(List<WeightedDrop> entries, int rolls, RandomSource random) {
        List<ItemStack> result = new ArrayList<>();
        if (entries.isEmpty() || rolls <= 0) {
            return result;
        }

        int totalWeight = 0;
        for (WeightedDrop entry : entries) {
            totalWeight += Math.max(0, entry.weight);
        }
        if (totalWeight <= 0) {
            return result;
        }

        for (int i = 0; i < rolls; i++) {
            int pick = random.nextInt(totalWeight);
            int running = 0;
            WeightedDrop selected = null;
            for (WeightedDrop entry : entries) {
                running += Math.max(0, entry.weight);
                if (pick < running) {
                    selected = entry;
                    break;
                }
            }
            if (selected == null) {
                continue;
            }

            Item item = ForgeRegistries.ITEMS.getValue(selected.itemId);
            if (item == null) {
                continue;
            }
            int count = rollRange(random, selected.min, selected.max);
            if (count > 0) {
                result.add(new ItemStack(item, count));
            }
        }
        return result;
    }

    private static int rollRange(RandomSource random, int min, int max) {
        int safeMin = Math.max(0, min);
        int safeMax = Math.max(safeMin, max);
        if (safeMax == safeMin) {
            return safeMin;
        }
        return safeMin + random.nextInt(safeMax - safeMin + 1);
    }

    private static ButcheryData load(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<ButcheryData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                return loaded.get();
            }
        }
        return loadFromClasspathCached();
    }

    private static Optional<ButcheryData> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse butchery config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static ButcheryData loadFromClasspathCached() {
        ButcheryData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (ButcheryConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = ButcheryConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Butchery config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load butchery config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = FALLBACK_DATA;
            }
            return classpathCache;
        }
    }

    private static ButcheryData parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return FALLBACK_DATA;
            }
            root = element.getAsJsonObject();
        }

        InteractionProfile interactionProfile = DEFAULT_INTERACTION;
        if (root.has("interaction") && root.get("interaction").isJsonObject()) {
            interactionProfile = parseInteraction(root.getAsJsonObject("interaction"));
        }

        Map<ResourceLocation, EntityProfile> entitiesByType = parseEntities(root);
        Map<ResourceLocation, CarcassHarvestProfile> carcassProfiles = parseCarcassHarvest(root);
        return new ButcheryData(interactionProfile, Map.copyOf(entitiesByType), Map.copyOf(carcassProfiles));
    }

    private static InteractionProfile parseInteraction(JsonObject object) {
        ResourceLocation killTag = parseResourceLocation(
                GsonHelper.getAsString(object, "kill_weapon_item_tag", DEFAULT_KILL_TAG.toString()),
                DEFAULT_KILL_TAG
        );
        ResourceLocation harvestTag = parseResourceLocation(
                GsonHelper.getAsString(object, "harvest_tool_item_tag", DEFAULT_HARVEST_TAG.toString()),
                DEFAULT_HARVEST_TAG
        );
        boolean requireOffhand = GsonHelper.getAsBoolean(object, "require_harvest_tool_in_offhand", true);
        int cooldown = Math.max(0, GsonHelper.getAsInt(object, "harvest_cooldown_ticks", 8));
        int durabilityCost = Math.max(0, GsonHelper.getAsInt(object, "harvest_tool_damage_per_use", 1));
        ResourceLocation soundId = parseResourceLocation(
                GsonHelper.getAsString(object, "harvest_sound", DEFAULT_SOUND.toString()),
                DEFAULT_SOUND
        );
        return new InteractionProfile(killTag, harvestTag, requireOffhand, cooldown, durabilityCost, soundId);
    }

    private static Map<ResourceLocation, EntityProfile> parseEntities(JsonObject root) {
        Map<ResourceLocation, EntityProfile> entitiesByType = new HashMap<>();
        if (!root.has("entities") || !root.get("entities").isJsonObject()) {
            return entitiesByType;
        }

        JsonObject entitiesObject = root.getAsJsonObject("entities");
        for (Map.Entry<String, JsonElement> entry : entitiesObject.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            try {
                ResourceLocation entityId = ResourceLocation.parse(entry.getKey());
                entitiesByType.put(entityId, parseEntityProfile(entry.getValue().getAsJsonObject()));
            } catch (Exception e) {
                LOGGER.warn("Invalid entity id in butchery config: {}", entry.getKey(), e);
            }
        }
        return entitiesByType;
    }

    private static EntityProfile parseEntityProfile(JsonObject object) {
        boolean enabled = GsonHelper.getAsBoolean(object, "enabled", true);
        boolean spawnCarcass = GsonHelper.getAsBoolean(object, "spawn_carcass", true);
        ResourceLocation carcassItem = parseResourceLocation(GsonHelper.getAsString(object, "carcass_item", ""), null);
        int carcassMin = Math.max(0, GsonHelper.getAsInt(object, "carcass_count_min", 1));
        int carcassMax = Math.max(carcassMin, GsonHelper.getAsInt(object, "carcass_count_max", carcassMin));
        boolean removeVanillaRawMeat = GsonHelper.getAsBoolean(object, "remove_vanilla_raw_meat", true);
        Set<ResourceLocation> removeItems = parseResourceLocationSet(object, "remove_items");

        int extraDropRollsMin = Math.max(0, GsonHelper.getAsInt(object, "extra_drop_rolls_min", 0));
        int extraDropRollsMax = Math.max(extraDropRollsMin, GsonHelper.getAsInt(object, "extra_drop_rolls_max", extraDropRollsMin));
        List<WeightedDrop> extraDrops = parseWeightedDrops(object, "extra_drops");

        return new EntityProfile(
                enabled,
                spawnCarcass,
                carcassItem,
                carcassMin,
                carcassMax,
                removeVanillaRawMeat,
                Set.copyOf(removeItems),
                extraDropRollsMin,
                extraDropRollsMax,
                List.copyOf(extraDrops)
        );
    }

    private static Map<ResourceLocation, CarcassHarvestProfile> parseCarcassHarvest(JsonObject root) {
        Map<ResourceLocation, CarcassHarvestProfile> profiles = new HashMap<>();
        if (!root.has("carcass_harvest") || !root.get("carcass_harvest").isJsonObject()) {
            return profiles;
        }

        JsonObject harvestObject = root.getAsJsonObject("carcass_harvest");
        for (Map.Entry<String, JsonElement> entry : harvestObject.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            try {
                ResourceLocation carcassId = ResourceLocation.parse(entry.getKey());
                profiles.put(carcassId, parseCarcassProfile(entry.getValue().getAsJsonObject()));
            } catch (Exception e) {
                LOGGER.warn("Invalid carcass id in butchery config: {}", entry.getKey(), e);
            }
        }
        return profiles;
    }

    private static CarcassHarvestProfile parseCarcassProfile(JsonObject object) {
        int rollsMin = Math.max(0, GsonHelper.getAsInt(object, "rolls_min", 1));
        int rollsMax = Math.max(rollsMin, GsonHelper.getAsInt(object, "rolls_max", rollsMin));
        List<WeightedDrop> drops = parseWeightedDrops(object, "drops");
        return new CarcassHarvestProfile(rollsMin, rollsMax, List.copyOf(drops));
    }

    private static List<WeightedDrop> parseWeightedDrops(JsonObject object, String fieldName) {
        List<WeightedDrop> drops = new ArrayList<>();
        if (!object.has(fieldName) || !object.get(fieldName).isJsonArray()) {
            return drops;
        }
        JsonArray array = object.getAsJsonArray(fieldName);
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject dropObject = element.getAsJsonObject();
            ResourceLocation itemId = parseResourceLocation(GsonHelper.getAsString(dropObject, "item", ""), null);
            if (itemId == null) {
                continue;
            }
            int min = Math.max(0, GsonHelper.getAsInt(dropObject, "min", 1));
            int max = Math.max(min, GsonHelper.getAsInt(dropObject, "max", min));
            int weight = Math.max(0, GsonHelper.getAsInt(dropObject, "weight", 1));
            drops.add(new WeightedDrop(itemId, min, max, weight));
        }
        return drops;
    }

    private static Set<ResourceLocation> parseResourceLocationSet(JsonObject object, String fieldName) {
        Set<ResourceLocation> results = new HashSet<>();
        if (!object.has(fieldName) || !object.get(fieldName).isJsonArray()) {
            return results;
        }
        JsonArray array = object.getAsJsonArray(fieldName);
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                continue;
            }
            ResourceLocation id = parseResourceLocation(element.getAsString(), null);
            if (id != null) {
                results.add(id);
            }
        }
        return results;
    }

    @Nullable
    private static ResourceLocation parseResourceLocation(String raw, @Nullable ResourceLocation fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return ResourceLocation.parse(raw);
        } catch (Exception e) {
            LOGGER.warn("Invalid resource location in butchery config: {}", raw, e);
            return fallback;
        }
    }

    public record InteractionProfile(
            ResourceLocation killWeaponItemTag,
            ResourceLocation harvestToolItemTag,
            boolean requireHarvestToolInOffhand,
            int harvestCooldownTicks,
            int harvestToolDamagePerUse,
            ResourceLocation harvestSound
    ) {
    }

    public record EntityProfile(
            boolean enabled,
            boolean spawnCarcass,
            @Nullable ResourceLocation carcassItem,
            int carcassCountMin,
            int carcassCountMax,
            boolean removeVanillaRawMeat,
            Set<ResourceLocation> removeItems,
            int extraDropRollsMin,
            int extraDropRollsMax,
            List<WeightedDrop> extraDrops
    ) {
    }

    public record CarcassHarvestProfile(int rollsMin, int rollsMax, List<WeightedDrop> drops) {
    }

    public record WeightedDrop(ResourceLocation itemId, int min, int max, int weight) {
    }

    private record ButcheryData(
            InteractionProfile interactionProfile,
            Map<ResourceLocation, EntityProfile> entitiesByType,
            Map<ResourceLocation, CarcassHarvestProfile> carcassHarvestByItemId
    ) {
    }
}
