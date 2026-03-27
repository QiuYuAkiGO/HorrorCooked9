package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopResult;
import net.qiuyu.horrorcooked9.gameplay.chopping.IChoppable;
import net.qiuyu.horrorcooked9.register.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrystalTomato extends Item implements IChoppable {

    public CrystalTomato(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return super.getName(pStack).copy().withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.crystal_tomato.desc.1").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.crystal_tomato.desc.2").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.crystal_tomato.desc.3").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void onChop(Level level, BlockPos pos, Player player, ItemStack placedItem, ChopResult result) {
        int count = switch (result) {
            case GREEN -> 4;
            case YELLOW -> 2;
            case RED -> 1;
        };

        for (int i = 0; i < count; i++) {
            ItemStack slice = new ItemStack(ModItems.CRYSTAL_TOMATO_SLICED.get());
            ItemEntity itemEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, slice);
            level.addFreshEntity(itemEntity);
        }
    }
}
