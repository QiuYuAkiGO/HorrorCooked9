package net.qiuyu.horrorcooked9.events;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID)
public class FoodworksTableTransformEvents {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) {
            return;
        }

        BlockPos placedPos = event.getPos();
        BlockState placedState = event.getPlacedBlock();

        if (placedState.is(Blocks.CRAFTING_TABLE)) {
            tryTransformFoodworksTable(level, placedPos);
            return;
        }

        if (placedState.is(BlockTags.WOOL_CARPETS)) {
            tryTransformFoodworksTable(level, placedPos.below());
        }
    }

    private static void tryTransformFoodworksTable(Level level, BlockPos tablePos) {
        if (!level.getBlockState(tablePos).is(Blocks.CRAFTING_TABLE)) {
            return;
        }

        BlockPos carpetPos = tablePos.above();
        BlockState carpetState = level.getBlockState(carpetPos);
        if (!carpetState.is(BlockTags.WOOL_CARPETS)) {
            return;
        }

        level.setBlock(tablePos, ModBlocks.FOODWORKS_TABLE.get().defaultBlockState(), 3);
        level.removeBlock(carpetPos, false);
    }
}
