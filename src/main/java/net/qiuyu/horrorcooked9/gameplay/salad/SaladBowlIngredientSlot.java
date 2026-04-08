package net.qiuyu.horrorcooked9.gameplay.salad;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * 沙拉碗配方中的单个食材槽位。
 *
 * @param ingredient 食材匹配规则
 * @param repeatable 是否可重复，当前仅允许最后一个槽位为 true
 * @param min        可重复时的最小数量（含）
 * @param max        可重复时的最大数量（含）
 */
public record SaladBowlIngredientSlot(Ingredient ingredient, boolean repeatable, int min, int max) {

    public SaladBowlIngredientSlot {
        if (ingredient == null || ingredient.isEmpty()) {
            throw new IllegalArgumentException("ingredient cannot be null or empty");
        }
        if (!repeatable) {
            min = 1;
            max = 1;
        } else {
            min = Math.max(1, min);
            max = Math.max(min, max);
        }
    }
}
