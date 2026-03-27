package net.qiuyu.horrorcooked9.gameplay.salad;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    private final NonNullList<Ingredient> ingredientsInOrder;
    private final Ingredient mixingTool;
    private final Ingredient servingContainer;
    private final ItemStack result;
    private final int servings;
    private final float mixingSuccessChance;
    private final int stirCount;

    public SaladBowlRecipe(ResourceLocation id, NonNullList<Ingredient> ingredientsInOrder, Ingredient mixingTool,
                           Ingredient servingContainer, ItemStack result, int servings, float mixingSuccessChance, int stirCount) {
        this.id = id;
        this.ingredientsInOrder = ingredientsInOrder;
        this.mixingTool = mixingTool;
        this.servingContainer = servingContainer;
        this.result = result;
        this.servings = servings;
        this.mixingSuccessChance = mixingSuccessChance;
        this.stirCount = stirCount;
    }

    public List<Ingredient> getIngredientsInOrder() {
        return ingredientsInOrder;
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
        copy.addAll(ingredientsInOrder);
        return copy;
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

            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }

            Ingredient mixingTool = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "mixing_tool"));
            Ingredient servingContainer = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "serving_container"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int servings = Math.max(1, GsonHelper.getAsInt(json, "servings", 1));
            float mixingSuccessChance = GsonHelper.getAsFloat(json, "mix_success_chance", 1.0F);
            mixingSuccessChance = Math.max(0.0F, Math.min(1.0F, mixingSuccessChance));
            int stirCount = Math.max(1, Math.min(10, GsonHelper.getAsInt(json, "stir_count", 1)));

            return new SaladBowlRecipe(recipeId, ingredients, mixingTool, servingContainer, result, servings, mixingSuccessChance, stirCount);
        }

        @Override
        public SaladBowlRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            Ingredient mixingTool = Ingredient.fromNetwork(buffer);
            Ingredient servingContainer = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int servings = buffer.readVarInt();
            float mixingSuccessChance = buffer.readFloat();
            int stirCount = buffer.readVarInt();

            return new SaladBowlRecipe(recipeId, ingredients, mixingTool, servingContainer, result, servings, mixingSuccessChance, stirCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SaladBowlRecipe recipe) {
            buffer.writeVarInt(recipe.ingredientsInOrder.size());
            for (Ingredient ingredient : recipe.ingredientsInOrder) {
                ingredient.toNetwork(buffer);
            }

            recipe.mixingTool.toNetwork(buffer);
            recipe.servingContainer.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.servings);
            buffer.writeFloat(recipe.mixingSuccessChance);
            buffer.writeVarInt(recipe.stirCount);
        }
    }
}
