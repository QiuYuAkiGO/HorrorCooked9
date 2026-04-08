package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.MultiUseFoodData;
import net.qiuyu.horrorcooked9.register.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PineappleFriedRiceItem extends Item {
    public static final int DEFAULT_FOOD_USES = 2;
    private static final int PINEAPPLE_POWER_DURATION_TICKS = 30 * 20;

    public PineappleFriedRiceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack singleUseStack = pStack.copyWithCount(1);
        super.finishUsingItem(singleUseStack, pLevel, pLivingEntity);

        if (!pLevel.isClientSide()) {
            pLivingEntity.addEffect(new MobEffectInstance(ModEffects.PINEAPPLE_POWER_I.get(), PINEAPPLE_POWER_DURATION_TICKS, 0, false, true));
        }

        if (pLivingEntity instanceof Player player && player.getAbilities().instabuild) {
            return pStack;
        }

        int remaining = MultiUseFoodData.consumeOne(pStack, DEFAULT_FOOD_USES);
        if (remaining <= 0) {
            pStack.shrink(1);
            ItemStack container = new ItemStack(Items.BOWL);
            if (pStack.isEmpty()) {
                return container;
            }
            if (pLivingEntity instanceof Player player && !player.getInventory().add(container)) {
                player.drop(container, false);
            }
        }
        return pStack;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        int total = MultiUseFoodData.getTotalUses(pStack, DEFAULT_FOOD_USES);
        int remaining = MultiUseFoodData.getRemainingUses(pStack, DEFAULT_FOOD_USES);
        return total > 1 && remaining > 0;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int total = MultiUseFoodData.getTotalUses(pStack, DEFAULT_FOOD_USES);
        int remaining = MultiUseFoodData.getRemainingUses(pStack, DEFAULT_FOOD_USES);
        return Math.round(13.0F * ((float) remaining / (float) total));
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return 0xF5C242;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.pineapple_fried_rice.desc.1").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.pineapple_fried_rice.desc.2").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
