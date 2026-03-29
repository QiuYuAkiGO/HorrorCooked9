package net.qiuyu.horrorcooked9.gameplay.salad;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.register.ModRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 沙拉成品食用后的返还逻辑辅助类。
 * <p>
 * 取餐时在 {@link net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlock} 中会消耗盛装容器（{@code serving_container}）。
 * 玩家吃完对应沙拉成品后，此处按配方返还<strong>盛装容器</strong>的代表物（通常为 1 个碗），
 * <strong>不</strong>返还搅拌工具（{@code mixing_tool}）。
 * <p>
 * 当 {@code serving_container} 为物品标签时，代表物取 {@link Ingredient#getItems()} 的第一个候选，
 * 顺序取决于注册表/标签展开，未必与 tag JSON 中列举顺序一致。
 */
public final class SaladEatReturnHelper {
    private SaladEatReturnHelper() {
    }

    /**
     * 根据食用完成的物品，查找产出该物品的沙拉配方。
     * 若有多条配方产出相同结果，按配方 ID 字典序稳定选取。
     */
    @Nullable
    public static SaladBowlRecipe findRecipeByResult(Level level, ItemStack consumedStack) {
        if (consumedStack.isEmpty()) {
            return null;
        }

        return level.getRecipeManager().getAllRecipesFor(ModRecipes.SALAD_BOWL_TYPE.get()).stream()
                .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getResultStack(), consumedStack))
                .sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从配方中推导食用后返还的物品：仅 1 个 {@code serving_container} 代表物（不返还 {@code mixing_tool}）。
     */
    public static List<ItemStack> buildReturnStacks(SaladBowlRecipe recipe) {
        List<ItemStack> returns = new ArrayList<>(1);
        addRepresentativeItem(returns, recipe.getServingContainer());
        return returns;
    }

    /** 将 {@link Ingredient} 展开后的第一个候选复制为数量 1 并加入列表；无候选或空栈则跳过。 */
    private static void addRepresentativeItem(List<ItemStack> result, Ingredient ingredient) {
        ItemStack[] candidates = ingredient.getItems();
        if (candidates.length <= 0) {
            return;
        }

        ItemStack stack = candidates[0].copy();
        if (stack.isEmpty()) {
            return;
        }
        stack.setCount(1);
        result.add(stack);
    }
}
