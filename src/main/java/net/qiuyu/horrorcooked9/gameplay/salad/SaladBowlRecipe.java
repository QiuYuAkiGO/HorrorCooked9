package net.qiuyu.horrorcooked9.gameplay.salad;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.MultiUseFoodData;
import net.qiuyu.horrorcooked9.register.ModRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 沙拉碗配方定义。
 * <p>
 * 该配方不参与传统工作台匹配，实际匹配由沙拉碗交互逻辑按 {@code ingredientsInOrder}
 * 进行前缀/精确判断；此类主要承载配方数据并负责序列化。
 */
public class SaladBowlRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<SaladBowlIngredientSlot> ingredientSlots;
    private final Ingredient mixingTool;
    private final Ingredient servingContainer;
    private final ItemStack result;
    private final int servings;
    private final boolean servingsMatchRepeatableCount;
    private final float mixingSuccessChance;
    private final int stirCount;

    public SaladBowlRecipe(ResourceLocation id, NonNullList<SaladBowlIngredientSlot> ingredientSlots, Ingredient mixingTool,
                           Ingredient servingContainer, ItemStack result, int servings, boolean servingsMatchRepeatableCount,
                           float mixingSuccessChance, int stirCount) {
        this.id = id;
        this.ingredientSlots = ingredientSlots;
        this.mixingTool = mixingTool;
        this.servingContainer = servingContainer;
        this.result = result;
        this.servings = servings;
        this.servingsMatchRepeatableCount = servingsMatchRepeatableCount;
        this.mixingSuccessChance = mixingSuccessChance;
        this.stirCount = stirCount;

        validateRepeatableSlotPlacement(id, ingredientSlots);
    }

    public List<SaladBowlIngredientSlot> getIngredientSlots() {
        return ingredientSlots;
    }

    public Ingredient getMixingTool() {
        return mixingTool;
    }

    public Ingredient getServingContainer() {
        return servingContainer;
    }

    public ItemStack getResultStack() {
        return result.copy();
    }

    public int getServings() {
        return servings;
    }

    public boolean isServingsMatchRepeatableCount() {
        return servingsMatchRepeatableCount;
    }

    public float getMixingSuccessChance() {
        return mixingSuccessChance;
    }

    public int getStirCount() {
        return stirCount;
    }

    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull net.minecraft.core.RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.SALAD_BOWL_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.SALAD_BOWL_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> copy = NonNullList.create();
        for (SaladBowlIngredientSlot slot : ingredientSlots) {
            copy.add(slot.ingredient());
        }
        return copy;
    }

    public int resolveServingsForCompletedSequence(List<ItemStack> sequence) {
        if (!servingsMatchRepeatableCount) {
            return servings;
        }
        if (ingredientSlots.isEmpty()) {
            return servings;
        }
        SaladBowlIngredientSlot lastSlot = ingredientSlots.get(ingredientSlots.size() - 1);
        if (!lastSlot.repeatable()) {
            return servings;
        }
        int fixedCount = ingredientSlots.size() - 1;
        int repeatCount = Math.max(0, sequence.size() - fixedCount);
        repeatCount = Math.max(lastSlot.min(), Math.min(lastSlot.max(), repeatCount));
        return Math.max(1, repeatCount);
    }

    private static void validateRepeatableSlotPlacement(ResourceLocation recipeId, List<SaladBowlIngredientSlot> slots) {
        boolean foundRepeatable = false;
        for (int i = 0; i < slots.size(); i++) {
            boolean repeatable = slots.get(i).repeatable();
            if (repeatable) {
                if (i != slots.size() - 1) {
                    throw new IllegalArgumentException("repeatable slot must be the last ingredient slot for recipe " + recipeId);
                }
                foundRepeatable = true;
            } else if (foundRepeatable) {
                throw new IllegalArgumentException("non-repeatable slot cannot appear after repeatable slot for recipe " + recipeId);
            }
        }
    }

    /**
     * 沙拉碗配方的 JSON/网络序列化器。
     */
    public static class Serializer implements RecipeSerializer<SaladBowlRecipe> {
        @Override
        public @NotNull SaladBowlRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients_in_order");
            if (ingredientsJson.isEmpty()) {
                throw new IllegalArgumentException("ingredients_in_order cannot be empty for recipe " + recipeId);
            }

            NonNullList<SaladBowlIngredientSlot> ingredientSlots = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++) {
                JsonElement slotElement = ingredientsJson.get(i);
                if (!slotElement.isJsonObject()) {
                    throw new IllegalArgumentException("ingredients_in_order slot must be an object for recipe " + recipeId);
                }
                JsonObject slotObject = slotElement.getAsJsonObject();
                Ingredient ingredient = parseIngredientForSlot(slotObject);
                boolean repeatable = GsonHelper.getAsBoolean(slotObject, "repeatable", false);
                int min = Math.max(1, GsonHelper.getAsInt(slotObject, "min", 1));
                int max = Math.max(min, GsonHelper.getAsInt(slotObject, "max", 64));
                ingredientSlots.add(new SaladBowlIngredientSlot(ingredient, repeatable, min, max));
            }

            Ingredient mixingTool = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "mixing_tool"));
            Ingredient servingContainer = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "serving_container"));
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = ShapedRecipe.itemStackFromJson(resultJson);
            if (resultJson.has("food_uses")) {
                int foodUses = Math.max(1, GsonHelper.getAsInt(resultJson, "food_uses", 1));
                MultiUseFoodData.initialize(result, foodUses);
            }
            int servings = Math.max(1, GsonHelper.getAsInt(json, "servings", 1));
            boolean servingsMatchRepeatableCount = GsonHelper.getAsBoolean(json, "servings_match_repeatable_count", false);
            float mixingSuccessChance = GsonHelper.getAsFloat(json, "mix_success_chance", 1.0F);
            mixingSuccessChance = Math.max(0.0F, Math.min(1.0F, mixingSuccessChance));
            int stirCount = Math.max(1, Math.min(10, GsonHelper.getAsInt(json, "stir_count", 1)));

            return new SaladBowlRecipe(recipeId, ingredientSlots, mixingTool, servingContainer, result, servings,
                    servingsMatchRepeatableCount, mixingSuccessChance, stirCount);
        }

        @Override
        public SaladBowlRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int slotCount = buffer.readVarInt();
            NonNullList<SaladBowlIngredientSlot> ingredientSlots = NonNullList.create();
            for (int i = 0; i < slotCount; i++) {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                boolean repeatable = buffer.readBoolean();
                int min = buffer.readVarInt();
                int max = buffer.readVarInt();
                ingredientSlots.add(new SaladBowlIngredientSlot(ingredient, repeatable, min, max));
            }

            Ingredient mixingTool = Ingredient.fromNetwork(buffer);
            Ingredient servingContainer = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int servings = buffer.readVarInt();
            boolean servingsMatchRepeatableCount = buffer.readBoolean();
            float mixingSuccessChance = buffer.readFloat();
            int stirCount = buffer.readVarInt();

            return new SaladBowlRecipe(recipeId, ingredientSlots, mixingTool, servingContainer, result, servings,
                    servingsMatchRepeatableCount, mixingSuccessChance, stirCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SaladBowlRecipe recipe) {
            buffer.writeVarInt(recipe.ingredientSlots.size());
            for (SaladBowlIngredientSlot slot : recipe.ingredientSlots) {
                slot.ingredient().toNetwork(buffer);
                buffer.writeBoolean(slot.repeatable());
                buffer.writeVarInt(slot.min());
                buffer.writeVarInt(slot.max());
            }

            recipe.mixingTool.toNetwork(buffer);
            recipe.servingContainer.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.servings);
            buffer.writeBoolean(recipe.servingsMatchRepeatableCount);
            buffer.writeFloat(recipe.mixingSuccessChance);
            buffer.writeVarInt(recipe.stirCount);
        }

        private Ingredient parseIngredientForSlot(JsonObject slotObject) {
            if (slotObject.has("ingredient")) {
                return Ingredient.fromJson(slotObject.get("ingredient"));
            }

            JsonObject ingredientJson = new JsonObject();
            if (slotObject.has("item")) {
                ingredientJson.add("item", slotObject.get("item"));
            } else if (slotObject.has("tag")) {
                ingredientJson.add("tag", slotObject.get("tag"));
            } else {
                throw new IllegalArgumentException("ingredient slot must contain item/tag or nested ingredient object");
            }
            return Ingredient.fromJson(ingredientJson);
        }
    }
}
