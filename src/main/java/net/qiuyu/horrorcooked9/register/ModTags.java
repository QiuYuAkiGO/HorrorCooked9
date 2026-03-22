package net.qiuyu.horrorcooked9.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> CHOPPER_PLACEABLE = tag("chopper_placeable");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.parse(HorrorCooked9.MODID + ":" + name));
        }
    }
}
