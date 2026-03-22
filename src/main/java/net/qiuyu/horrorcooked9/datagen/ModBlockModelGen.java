package net.qiuyu.horrorcooked9.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;


public class ModBlockModelGen extends BlockStateProvider {

    public ModBlockModelGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, HorrorCooked9.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.CHOPPING_BOARD.get(),
                models().withExistingParent("chopping_board",
                        ResourceLocation.parse(HorrorCooked9.MODID + ":block/chopping_board")));
    }
    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = key(block);
        return ResourceLocation.parse(name.getNamespace() + ":" + ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }
    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}
