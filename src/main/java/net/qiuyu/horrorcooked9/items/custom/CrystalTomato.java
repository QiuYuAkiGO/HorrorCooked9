package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.items.ChopResult;
import net.qiuyu.horrorcooked9.items.IChoppable;
import net.qiuyu.horrorcooked9.register.ModItems;

public class CrystalTomato extends Item implements IChoppable {

    public CrystalTomato(Properties pProperties) {
        super(pProperties);
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
