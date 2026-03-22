package net.qiuyu.horrorcooked9.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.register.ModItems;

public class ModItemModelGen extends ItemModelProvider {
    public static final String GENERATED = "item/generated";
    public static final String HANDHELD = "item/handheld";
    public static final String EGG_TEMPLATE = "item/template_spawn_egg";

    public ModItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HorrorCooked9.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(itemName(ModItems.CHOPPING_BOARD.get()), modLoc("block/chopping_board"));
        itemHandheldModel(ModItems.CLEAVER.get(), Cleaver.getTexture());
        itemGeneratedModel(ModItems.CRYSTAL_TOMATO.get(), resourceItem("crystal_tomato"));
        itemGeneratedModel(ModItems.CRYSTAL_TOMATO_SLICED.get(), resourceItem("crystal_tomato_sliced"));
    }

    private void eggItem(Item item) {
        withExistingParent(itemName(item),
                EGG_TEMPLATE);
    }

    public void itemGeneratedModel(Item item, ResourceLocation texture) {
        withExistingParent(itemName(item), GENERATED).texture("layer0", texture);
    }

    public void itemHandheldModel(Item item, ResourceLocation texture) {
        withExistingParent(itemName(item), HANDHELD).texture("layer0", texture);
    }

    private String itemName(Item item) {
        if (item == null) return "";
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":" + "block/" + path);
    }

    public ResourceLocation resourceItem(String path) {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":" + "item/" + path);
    }
}
