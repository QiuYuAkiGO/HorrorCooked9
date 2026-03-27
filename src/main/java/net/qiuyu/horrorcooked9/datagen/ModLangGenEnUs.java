package net.qiuyu.horrorcooked9.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModEffects;

public class ModLangGenEnUs extends LanguageProvider {
    public ModLangGenEnUs(PackOutput output, String locale) {
        super(output, HorrorCooked9.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.CHOPPING_BOARD.get(), "Chopping Board");
        add(ModItems.CLEAVER.get(), "Cleaver");
        add(ModItems.CRYSTAL_TOMATO.get(), "Crystal Tomato");
        add(ModItems.CRYSTAL_TOMATO_SLICED.get(), "Crystal Tomato Sliced");
        add(ModItems.CAPTAIN_HAT.get(), "Captain Hat");
        add(ModEffects.CAPTAIN_INSPIRATION.get(), "Captain's Inspiration");
        add("creativetab.horrorcooked9_tab", "Horror Cooked");
    }
}
