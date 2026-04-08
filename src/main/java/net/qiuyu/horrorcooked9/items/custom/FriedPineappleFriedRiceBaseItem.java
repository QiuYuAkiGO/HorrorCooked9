package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FriedPineappleFriedRiceBaseItem extends Item {
    public FriedPineappleFriedRiceBaseItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }
}
