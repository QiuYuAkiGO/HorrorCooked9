package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.MultiUseFoodData;

public abstract class AbstractMultiUseBowlItem extends Item {
    protected AbstractMultiUseBowlItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack singleUseStack = stack.copyWithCount(1);
        super.finishUsingItem(singleUseStack, level, livingEntity);
        afterSingleUseConsumed(singleUseStack, level, livingEntity);

        if (livingEntity instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }

        int remaining = MultiUseFoodData.consumeOne(stack, resolveFoodUses(stack));
        if (remaining > 0) {
            return stack;
        }

        stack.shrink(1);
        ItemStack container = new ItemStack(Items.BOWL);
        if (stack.isEmpty()) {
            return container;
        }
        if (livingEntity instanceof Player player && !player.getInventory().add(container)) {
            player.drop(container, false);
        }
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        int total = MultiUseFoodData.getTotalUses(stack, resolveFoodUses(stack));
        int remaining = MultiUseFoodData.getRemainingUses(stack, resolveFoodUses(stack));
        return total > 1 && remaining > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int total = MultiUseFoodData.getTotalUses(stack, resolveFoodUses(stack));
        int remaining = MultiUseFoodData.getRemainingUses(stack, resolveFoodUses(stack));
        return Math.round(13.0F * ((float) remaining / (float) total));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return resolveBarColor(stack);
    }

    protected void afterSingleUseConsumed(ItemStack singleUseStack, Level level, LivingEntity livingEntity) {
        // optional override for side effects
    }

    protected abstract int resolveFoodUses(ItemStack stack);

    protected abstract int resolveBarColor(ItemStack stack);
}
