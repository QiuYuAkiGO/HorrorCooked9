package net.qiuyu.horrorcooked9.gameplay.water;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClearWaterBoilingConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.parse("horrorcooked9:gameplay/clear_water_boiling.json");
    private static final String CLASSPATH_CONFIG_PATH = "data/horrorcooked9/gameplay/clear_water_boiling.json";
    private static final BoilingData FALLBACK_DATA = new BoilingData(List.of());

    @Nullable
    private static volatile BoilingData classpathCache;

    private ClearWaterBoilingConfig() {
    }

    public static boolean isRecipeEnabled(@Nullable ResourceManager resourceManager, ResourceLocation recipeType, ResourceLocation recipeId) {
        for (ContainerEntry entry : load(resourceManager).containers()) {
            if (!"vanilla_recipe".equals(entry.kind())) {
                continue;
            }
            if (entry.recipe() == null || entry.recipeType() == null) {
                continue;
            }
            if (entry.recipe().equals(recipeId) && entry.recipeType().equals(recipeType)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<ContainerEntry> resolveCauldronContainer(@Nullable ResourceManager resourceManager) {
        for (ContainerEntry entry : load(resourceManager).containers()) {
            if ("cauldron".equals(entry.kind())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    private static BoilingData load(@Nullable ResourceManager resourceManager) {
        if (resourceManager != null) {
            Optional<BoilingData> loaded = loadFromResourceManager(resourceManager);
            if (loaded.isPresent()) {
                return loaded.get();
            }
        }
        return loadFromClasspathCached();
    }

    private static Optional<BoilingData> loadFromResourceManager(ResourceManager resourceManager) {
        Optional<Resource> resourceOptional = resourceManager.getResource(CONFIG_ID);
        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resourceOptional.get().open()) {
            return Optional.of(parse(stream));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse clear water boiling config from resource manager: {}", CONFIG_ID, e);
            return Optional.empty();
        }
    }

    private static BoilingData loadFromClasspathCached() {
        BoilingData cached = classpathCache;
        if (cached != null) {
            return cached;
        }

        synchronized (ClearWaterBoilingConfig.class) {
            if (classpathCache != null) {
                return classpathCache;
            }

            try (InputStream stream = ClearWaterBoilingConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_PATH)) {
                if (stream == null) {
                    LOGGER.warn("Clear water boiling config not found on classpath: {}", CLASSPATH_CONFIG_PATH);
                    classpathCache = FALLBACK_DATA;
                } else {
                    classpathCache = parse(stream);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load clear water boiling config from classpath: {}", CLASSPATH_CONFIG_PATH, e);
                classpathCache = FALLBACK_DATA;
            }
            return classpathCache;
        }
    }

    private static BoilingData parse(InputStream stream) throws IOException {
        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return FALLBACK_DATA;
            }
            root = element.getAsJsonObject();
        }

        List<ContainerEntry> entries = new ArrayList<>();
        if (root.has("containers") && root.get("containers").isJsonArray()) {
            JsonArray containers = root.getAsJsonArray("containers");
            for (JsonElement element : containers) {
                if (!element.isJsonObject()) {
                    continue;
                }
                ContainerEntry entry = parseContainer(element.getAsJsonObject());
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }

        return new BoilingData(List.copyOf(entries));
    }

    @Nullable
    private static ContainerEntry parseContainer(JsonObject object) {
        String id = GsonHelper.getAsString(object, "id", "");
        String kind = GsonHelper.getAsString(object, "kind", "").trim();
        if (kind.isEmpty()) {
            return null;
        }

        ResourceLocation recipeType = parseResourceLocation(GsonHelper.getAsString(object, "recipe_type", ""), null);
        ResourceLocation recipe = parseResourceLocation(GsonHelper.getAsString(object, "recipe", ""), null);
        boolean requiresHeatBelow = GsonHelper.getAsBoolean(object, "requires_heat_below", true);
        String fluidInCauldron = GsonHelper.getAsString(object, "fluid_in_cauldron", "water");
        int minLevel = Math.max(1, GsonHelper.getAsInt(object, "min_level", 1));
        return new ContainerEntry(id, kind, recipeType, recipe, requiresHeatBelow, fluidInCauldron, minLevel);
    }

    @Nullable
    private static ResourceLocation parseResourceLocation(String raw, @Nullable ResourceLocation fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return ResourceLocation.parse(raw);
        } catch (Exception e) {
            LOGGER.warn("Invalid resource location in clear water boiling config: {}", raw, e);
            return fallback;
        }
    }

    public record ContainerEntry(
            String id,
            String kind,
            @Nullable ResourceLocation recipeType,
            @Nullable ResourceLocation recipe,
            boolean requiresHeatBelow,
            String fluidInCauldron,
            int minLevel
    ) {
    }

    private record BoilingData(List<ContainerEntry> containers) {
    }
}
