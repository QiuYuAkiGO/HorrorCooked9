package net.qiuyu.horrorcooked9.gameplay.salad;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 沙拉配方匹配工具类。
 * 提供按当前投料序列进行前缀匹配和完整匹配的静态方法。
 */
public final class SaladRecipeMatcher {
    private SaladRecipeMatcher() {
    }

    /**
     * 返回所有与当前投料序列前缀一致的候选配方。
     */
    public static List<SaladBowlRecipe> findPrefixMatches(List<ItemStack> sequence, List<SaladBowlRecipe> allRecipes) {
        List<SaladBowlRecipe> matches = new ArrayList<>();
        for (SaladBowlRecipe recipe : allRecipes) {
            if (isPrefixMatch(sequence, recipe)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * 返回与当前投料序列完全一致的配方；若有多个同等候选，按配方 ID 字典序稳定选取。
     */
    public static SaladBowlRecipe findExactMatch(List<ItemStack> sequence, List<SaladBowlRecipe> allRecipes) {
        return allRecipes.stream()
                .filter(recipe -> recipe.getIngredientsInOrder().size() == sequence.size() && isPrefixMatch(sequence, recipe))
                .sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断当前投料序列是否为目标配方的前缀。
     */
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
