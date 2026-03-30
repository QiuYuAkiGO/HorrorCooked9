package net.qiuyu.horrorcooked9.gameplay.juicing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
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

public final class JuicingConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/juicing.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/juicing.json";

    private static final Defaults DEFAULTS = new Defaults(4000, 250, false, 16, 10);
    private static final JuicingData FALLBACK_DATA = new JuicingData(DEFAULTS, Map.of(), Map.of(), Map.of());

    @Nullable
    private static volatile JuicingData classpathCache;

    private JuicingConfig() {
    }

    public static Defaults defaults(@Nullable ResourceManager resourceManager) {
        return load(resourceManager).defaults;
    }

    public static Optional<Entry> resolveByFruit(@Nullable ResourceManager resourceManager, ResourceLocation fruitId) {
        return Optional.ofNullable(load(resourceManager).entriesByFruit.get(fruitId));
    }

    public static Optional<Entry> resolveByFruit(@Nullable ResourceManager resourceManager, ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            return Optional.empty();
        }
        return resolveByFruit(resourceManager, itemId);
    }

    public static Optional<ResourceLocation> resolveBottleItem(@Nullable ResourceManager resourceManager, Fluid fluid) {
        ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
        if (fluidId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(load(resourceManager).bottleByFluid.get(fluidId));
    }

    public static Optional<ResourceLocation> resolveFruitByFluid(@Nullable ResourceManager resourceManager, Fluid fluid) {
        ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
        if (fluidId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(load(resourceManager).fruitByFluid.get(fluidId));
    }

    public static List<ItemStack> rollByproducts(Entry entry, RandomSource random) {
        List<ItemStack> result = new ArrayList<>();
        if (entry.byproducts.isEmpty()) {
            return result;
        }

        int totalWeight = 0;
        for (WeightedDrop drop : entry.byproducts) {
            totalWeight += Math.max(0, drop.weight);
        }
        if (totalWeight <= 0) {
            return result;
        }

        int pick = random.nextInt(totalWeight);
        int running = 0;
        for (WeightedDrop drop : entry.byproducts) {
            running += Math.max(0, drop.weight);
            if (pick >= running) {
                continue;
            }

            Item item = ForgeRegistries.ITEMS.getValue(drop.itemId);
            if (item == null) {
                return result;
            }
            int count = rollRange(random, drop.min, drop.max);
            if (count > 0) {
                result.add(new ItemStack(item, count));
            }
            return result;
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

    private static JuicingData load(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<JuicingData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                return loaded.get();
            }
        }
        return loadFromClasspathCached();
    }

    private static Optional<JuicingData> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse juicing config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static JuicingData loadFromClasspathCached() {
        JuicingData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (JuicingConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = JuicingConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Juicing config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load juicing config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = FALLBACK_DATA;
            }
            return classpathCache;
        }
    }

    private static JuicingData parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return FALLBACK_DATA;
            }
            root = element.getAsJsonObject();
        }

        Defaults defaults = parseDefaults(root);
        Map<ResourceLocation, Entry> entriesByFruit = parseEntries(root);
        Map<ResourceLocation, ResourceLocation> bottleByFluid = new HashMap<>();
        Map<ResourceLocation, ResourceLocation> fruitByFluid = new HashMap<>();
        for (Entry entry : entriesByFruit.values()) {
            fruitByFluid.putIfAbsent(entry.fluidId, entry.fruitId);
            if (entry.bottleItemId != null) {
                bottleByFluid.putIfAbsent(entry.fluidId, entry.bottleItemId);
            }
        }

        return new JuicingData(
                defaults,
                Map.copyOf(entriesByFruit),
                Map.copyOf(bottleByFluid),
                Map.copyOf(fruitByFluid)
        );
    }

    private static Defaults parseDefaults(JsonObject root) {
        if (!root.has("defaults") || !root.get("defaults").isJsonObject()) {
            return DEFAULTS;
        }
        JsonObject object = root.getAsJsonObject("defaults");
        int tankCapacityMb = Math.max(1000, GsonHelper.getAsInt(object, "tank_capacity_mb", DEFAULTS.tankCapacityMb));
        int bottleAmountMb = Math.max(50, GsonHelper.getAsInt(object, "bottle_amount_mb", DEFAULTS.bottleAmountMb));
        boolean allowMixing = GsonHelper.getAsBoolean(object, "allow_mixing", DEFAULTS.allowMixing);
        int createMinimumSpeed = Math.max(1, GsonHelper.getAsInt(object, "create_minimum_speed", DEFAULTS.createMinimumSpeed));
        int interruptedResetTicks = Math.max(0, GsonHelper.getAsInt(object, "manual_interrupted_reset_ticks", DEFAULTS.manualInterruptedResetTicks));
        return new Defaults(tankCapacityMb, bottleAmountMb, allowMixing, createMinimumSpeed, interruptedResetTicks);
    }

    private static Map<ResourceLocation, Entry> parseEntries(JsonObject root) {
        Map<ResourceLocation, Entry> entries = new HashMap<>();
        if (!root.has("entries") || !root.get("entries").isJsonArray()) {
            return entries;
        }

        JsonArray array = root.getAsJsonArray("entries");
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject object = element.getAsJsonObject();
            ResourceLocation fruitId = parseResourceLocation(GsonHelper.getAsString(object, "fruit", ""), null);
            ResourceLocation fluidId = parseResourceLocation(GsonHelper.getAsString(object, "fluid", ""), null);
            if (fruitId == null || fluidId == null) {
                continue;
            }

            int pulpTotalMb = Math.max(50, GsonHelper.getAsInt(object, "pulp_total_mb", 250));
            int manualHoldTicks = Math.max(1, GsonHelper.getAsInt(object, "manual_hold_ticks", 20));
            int manualFluidMb = Math.max(1, GsonHelper.getAsInt(object, "manual_fluid_mb", 50));
            int autoStepMb = Math.max(1, GsonHelper.getAsInt(object, "auto_step_mb", 25));
            ResourceLocation bottleItemId = parseResourceLocation(GsonHelper.getAsString(object, "bottle_item", ""), null);
            List<WeightedDrop> byproducts = parseWeightedDrops(object, "byproducts");

            entries.put(fruitId, new Entry(
                    fruitId,
                    fluidId,
                    pulpTotalMb,
                    manualHoldTicks,
                    manualFluidMb,
                    autoStepMb,
                    bottleItemId,
                    List.copyOf(byproducts)
            ));
        }
        return entries;
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

    @Nullable
    private static ResourceLocation parseResourceLocation(String raw, @Nullable ResourceLocation fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return ResourceLocation.parse(raw);
        } catch (Exception e) {
            LOGGER.warn("Invalid resource location in juicing config: {}", raw, e);
            return fallback;
        }
    }

    public record Defaults(
            int tankCapacityMb,
            int bottleAmountMb,
            boolean allowMixing,
            int createMinimumSpeed,
            int manualInterruptedResetTicks
    ) {
    }

    public record Entry(
            ResourceLocation fruitId,
            ResourceLocation fluidId,
            int pulpTotalMb,
            int manualHoldTicks,
            int manualFluidMb,
            int autoStepMb,
            @Nullable ResourceLocation bottleItemId,
            List<WeightedDrop> byproducts
    ) {
    }

    public record WeightedDrop(ResourceLocation itemId, int min, int max, int weight) {
    }

    private record JuicingData(
            Defaults defaults,
            Map<ResourceLocation, Entry> entriesByFruit,
            Map<ResourceLocation, ResourceLocation> bottleByFluid,
            Map<ResourceLocation, ResourceLocation> fruitByFluid
    ) {
    }
}
