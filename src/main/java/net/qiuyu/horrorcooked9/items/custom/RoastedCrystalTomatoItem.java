package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoastedCrystalTomatoItem extends Item {
    public RoastedCrystalTomatoItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return super.getName(pStack).copy().withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.roasted_crystal_tomato.desc.1")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.roasted_crystal_tomato.desc.2")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.roasted_crystal_tomato.desc.3")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(
                Component.empty()
                        .append(Component.translatable("item.horrorcooked9.roasted_crystal_tomato.desc.4")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC))
                        .append(Component.translatable("item.horrorcooked9.roasted_crystal_tomato.desc.4.bold")
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC, ChatFormatting.BOLD))
        );
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
