package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TranquilMixedRiceItem extends AbstractMultiUseBowlItem {
    public static final int DEFAULT_FOOD_USES = 3;
    private static final float HEAL_AMOUNT = 4.0F;

    public TranquilMixedRiceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void afterSingleUseConsumed(ItemStack singleUseStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            livingEntity.heal(HEAL_AMOUNT);
        }
    }

    @Override
    protected int resolveFoodUses(ItemStack stack) {
        return DEFAULT_FOOD_USES;
    }

    @Override
    protected int resolveBarColor(ItemStack pStack) {
        return 0x62A678;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.tranquil_mixed_rice.desc.1").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.tranquil_mixed_rice.desc.2").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.tranquil_mixed_rice.desc.3").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
