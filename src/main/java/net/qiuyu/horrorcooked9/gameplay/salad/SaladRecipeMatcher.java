package net.qiuyu.horrorcooked9.gameplay.salad;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SaladRecipeMatcher {
    private SaladRecipeMatcher() {
    }

    public static List<SaladBowlRecipe> findPrefixMatches(List<ItemStack> sequence, List<SaladBowlRecipe> allRecipes) {
        List<SaladBowlRecipe> matches = new ArrayList<>();
        for (SaladBowlRecipe recipe : allRecipes) {
            if (isPrefixMatch(sequence, recipe)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    public static SaladBowlRecipe findExactMatch(List<ItemStack> sequence, List<SaladBowlRecipe> allRecipes) {
        return allRecipes.stream()
                .filter(recipe -> recipe.getIngredientsInOrder().size() == sequence.size() && isPrefixMatch(sequence, recipe))
                .sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .orElse(null);
    }

    public static boolean isPrefixMatch(List<ItemStack> sequence, SaladBowlRecipe recipe) {
        if (sequence.size() > recipe.getIngredientsInOrder().size()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            if (!recipe.getIngredientsInOrder().get(i).test(sequence.get(i))) {
                return false;
            }
        }
        return true;
    }
}
