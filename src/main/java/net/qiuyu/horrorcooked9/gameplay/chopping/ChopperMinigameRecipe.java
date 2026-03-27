package net.qiuyu.horrorcooked9.gameplay.chopping;

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

/**
 * 切菜小游戏配方：
 * 定义输入匹配、判定难度（速度/占比）和不同判定结果对应的产物。
 */
public class ChopperMinigameRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final float cursorSpeed;
    private final float greenRatio;
    private final float yellowRatio;
    private final float redRatio;
    private final ItemStack greenResult;
    private final ItemStack yellowResult;
    private final ItemStack redResult;

    public ChopperMinigameRecipe(ResourceLocation id,
                                 Ingredient input,
                                 float cursorSpeed,
                                 float greenRatio,
                                 float yellowRatio,
                                 float redRatio,
                                 ItemStack greenResult,
                                 ItemStack yellowResult,
                                 ItemStack redResult) {
        this.id = id;
        this.input = input;
        this.cursorSpeed = Math.max(0.0001F, cursorSpeed);

        float safeGreen = Math.max(0.0F, greenRatio);
        float safeYellow = Math.max(0.0F, yellowRatio);
        float safeRed = Math.max(0.0F, redRatio);
        float sum = safeGreen + safeYellow + safeRed;
        if (sum <= 0.0F) {
            throw new IllegalArgumentException("ChopperMinigameRecipe ratios sum must be > 0 for recipe " + id);
        }
        this.greenRatio = safeGreen / sum;
        this.yellowRatio = safeYellow / sum;
        this.redRatio = safeRed / sum;

        this.greenResult = greenResult.copy();
        this.yellowResult = yellowResult.copy();
        this.redResult = redResult.copy();
    }

    public Ingredient getInput() {
        return input;
    }

    public float getCursorSpeed() {
        return cursorSpeed;
    }

    public float getGreenRatio() {
        return greenRatio;
    }

    public float getYellowRatio() {
        return yellowRatio;
    }

    public float getRedRatio() {
        return redRatio;
    }

    public float getRatioFor(ChopResult result) {
        return switch (result) {
            case GREEN -> greenRatio;
            case YELLOW -> yellowRatio;
            case RED -> redRatio;
        };
    }

    public ItemStack getResultFor(ChopResult result) {
        return switch (result) {
            case GREEN -> greenResult.copy();
            case YELLOW -> yellowResult.copy();
            case RED -> redResult.copy();
        };
    }

    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull net.minecraft.core.RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess pRegistryAccess) {
        return greenResult.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHOPPER_MINIGAME_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.CHOPPER_MINIGAME_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public static class Serializer implements RecipeSerializer<ChopperMinigameRecipe> {
        @Override
        public @NotNull ChopperMinigameRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));

            JsonObject difficulty = GsonHelper.getAsJsonObject(json, "difficulty");
            float cursorSpeed = GsonHelper.getAsFloat(difficulty, "cursor_speed", 0.015F);
            float greenRatio = GsonHelper.getAsFloat(difficulty, "green_ratio", 0.1F);
            float yellowRatio = GsonHelper.getAsFloat(difficulty, "yellow_ratio", 0.3F);
            float redRatio = GsonHelper.getAsFloat(difficulty, "red_ratio", 0.6F);

            JsonObject results = GsonHelper.getAsJsonObject(json, "results");
            ItemStack greenResult = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(results, "green"));
            ItemStack yellowResult = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(results, "yellow"));
            ItemStack redResult = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(results, "red"));

            return new ChopperMinigameRecipe(
                    recipeId, input, cursorSpeed, greenRatio, yellowRatio, redRatio, greenResult, yellowResult, redResult
            );
        }

        @Override
        public ChopperMinigameRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            float cursorSpeed = buffer.readFloat();
            float greenRatio = buffer.readFloat();
            float yellowRatio = buffer.readFloat();
            float redRatio = buffer.readFloat();
            ItemStack greenResult = buffer.readItem();
            ItemStack yellowResult = buffer.readItem();
            ItemStack redResult = buffer.readItem();

            return new ChopperMinigameRecipe(
                    recipeId, input, cursorSpeed, greenRatio, yellowRatio, redRatio, greenResult, yellowResult, redResult
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ChopperMinigameRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFloat(recipe.cursorSpeed);
            buffer.writeFloat(recipe.greenRatio);
            buffer.writeFloat(recipe.yellowRatio);
            buffer.writeFloat(recipe.redRatio);
            buffer.writeItem(recipe.greenResult);
            buffer.writeItem(recipe.yellowResult);
            buffer.writeItem(recipe.redResult);
        }
    }
}
