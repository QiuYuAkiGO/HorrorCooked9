package net.qiuyu.horrorcooked9.datagen;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModItems;

public class ModLangGenCN extends LanguageProvider {
    public ModLangGenCN(PackOutput output, String locale) {
        super(output, HorrorCooked9.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.CHOPPING_BOARD.get(), "砧板");
        add(ModItems.CLEAVER.get(), "切肉刀");
        add(ModItems.CRYSTAL_TOMATO.get(), "水晶番茄");
        add(ModItems.CRYSTAL_TOMATO_SLICED.get(), "水晶番茄片");
        // 护甲
//        add(ModItems.MEDICARE_HELMET.get(),"爱心医疗部『帽子』");
//        add("tooltip.horror9.medicare.line1", "爱心医疗部的部员，能够出色的兼顾到设施的一切，");
//        add("curios.identifier.halo", "光环");
    }
}
