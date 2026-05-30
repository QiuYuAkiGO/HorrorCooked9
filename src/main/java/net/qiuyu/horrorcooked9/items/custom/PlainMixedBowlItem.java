package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlainMixedBowlItem extends AbstractMultiUseBowlItem {
    public static final int DEFAULT_FOOD_USES = 4;

    public PlainMixedBowlItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected int resolveFoodUses(ItemStack stack) {
        return DEFAULT_FOOD_USES;
    }

    @Override
    protected int resolveBarColor(ItemStack pStack) {
        return 0xC79A57;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.colorful_palette.desc.1").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.colorful_palette.desc.2").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.colorful_palette.desc.3").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.colorful_palette.desc.4").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
