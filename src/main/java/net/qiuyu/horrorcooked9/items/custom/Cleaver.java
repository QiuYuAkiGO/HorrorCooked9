package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public class Cleaver extends Item {

    public Cleaver(Properties pProperties) {
        super(pProperties);
    }

    public static ResourceLocation getTexture() {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":item/cleaver");
    }
}
