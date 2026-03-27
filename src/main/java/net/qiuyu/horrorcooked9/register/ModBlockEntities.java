package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HorrorCooked9.MODID);

    public static final RegistryObject<BlockEntityType<ChoppingBoardBlockEntity>> CHOPPING_BOARD_BE =
            BLOCK_ENTITIES.register("chopping_board_be",
                    () -> BlockEntityType.Builder.of(ChoppingBoardBlockEntity::new, ModBlocks.CHOPPING_BOARD.get()).build(null));

    public static final RegistryObject<BlockEntityType<SaladBowlBlockEntity>> SALAD_BOWL_BE =
            BLOCK_ENTITIES.register("salad_bowl_be",
                    () -> BlockEntityType.Builder.of(SaladBowlBlockEntity::new, ModBlocks.SALAD_BOWL.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
