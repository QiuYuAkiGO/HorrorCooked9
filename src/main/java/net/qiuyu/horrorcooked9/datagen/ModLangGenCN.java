package net.qiuyu.horrorcooked9.datagen;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModCreativeModeTabs;
import net.qiuyu.horrorcooked9.register.ModEffects;
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
        add(ModItems.CAPTAIN_HAT.get(), "船长帽");


        add(ModEffects.CAPTAIN_INSPIRATION.get(), "船长的激励");


        add("creativetab.horrorcooked9_tab", "恐怖烹饪");
    }
}
