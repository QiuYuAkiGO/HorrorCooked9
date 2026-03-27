package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlock;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HorrorCooked9.MODID);

    public static final RegistryObject<Block> CHOPPING_BOARD = BLOCKS.register("chopping_board",
            () -> new ChoppingBoardBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).noOcclusion()));

    public static final RegistryObject<Block> SALAD_BOWL = BLOCKS.register("salad_bowl",
            () -> new SaladBowlBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(0.8F).noOcclusion()));

public static final RegistryObject<Block> FOODWORKS_TABLE = BLOCKS.register("foodworks_table",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).strength(2.5F)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
