package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParasiticBeanSproutsItem extends Item {
    public ParasiticBeanSproutsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return super.getName(pStack).copy().withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.1"));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.2"));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.3"));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.4"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
