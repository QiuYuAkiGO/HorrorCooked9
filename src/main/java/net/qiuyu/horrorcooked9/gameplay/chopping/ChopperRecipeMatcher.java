package net.qiuyu.horrorcooked9.gameplay.chopping;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.register.ModRecipes;

import java.util.Comparator;
import java.util.List;

/**
 * 切菜小游戏配方匹配工具。
 */
public final class ChopperRecipeMatcher {
    private ChopperRecipeMatcher() {
    }

    public static ChopperMinigameRecipe findByInput(ItemStack input, Level level) {
        if (input.isEmpty()) {
            return null;
        }

        List<ChopperMinigameRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.CHOPPER_MINIGAME_TYPE.get());
        return recipes.stream()
                .filter(recipe -> recipe.getInput().test(input))
                .sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .orElse(null);
    }
}
