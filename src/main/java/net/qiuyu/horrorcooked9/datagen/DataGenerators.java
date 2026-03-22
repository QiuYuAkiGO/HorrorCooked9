package net.qiuyu.horrorcooked9.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID,bus=Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(),new ModItemModelGen(output, helper));
        generator.addProvider(event.includeClient(),new ModLangGenEN(output,"en_us"));
        generator.addProvider(event.includeClient(),new ModLangGenCN(output,"zh_cn"));
        generator.addProvider(event.includeClient(),new ModBlockModelGen(output,helper));

    }
}
