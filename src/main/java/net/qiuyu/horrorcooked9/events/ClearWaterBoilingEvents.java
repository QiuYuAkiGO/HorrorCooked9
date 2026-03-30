package net.qiuyu.horrorcooked9.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.water.ClearWaterBoilingConfig;
import net.qiuyu.horrorcooked9.register.ModItems;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClearWaterBoilingEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack heldStack = event.getEntity().getItemInHand(event.getHand());
        if (!heldStack.is(ModItems.FILTERED_WATER_BUCKET.get())) {
            return;
        }

        ResourceManager resourceManager = level.getServer() != null ? level.getServer().getResourceManager() : null;
        ClearWaterBoilingConfig.ContainerEntry container = ClearWaterBoilingConfig.resolveCauldronContainer(resourceManager).orElse(null);
        if (container == null || !"water".equalsIgnoreCase(container.fluidInCauldron())) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.WATER_CAULDRON)) {
            return;
        }

        int waterLevel = state.getValue(LayeredCauldronBlock.LEVEL);
        if (waterLevel < container.minLevel()) {
            return;
        }

        if (container.requiresHeatBelow() && !isValidHeatSource(level, pos.below())) {
            return;
        }

        if (event.getEntity().getAbilities().instabuild) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (waterLevel <= 1) {
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
        } else {
            level.setBlock(pos, state.setValue(BlockStateProperties.LEVEL_CAULDRON, waterLevel - 1), 3);
        }

        event.getEntity().setItemInHand(event.getHand(), new ItemStack(ModItems.CLEAR_WATER_BUCKET.get()));
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.6F, 1.0F);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean isValidHeatSource(Level level, BlockPos heatPos) {
        BlockState heatState = level.getBlockState(heatPos);
        return heatState.is(Blocks.FIRE)
                || heatState.is(Blocks.SOUL_FIRE)
                || heatState.is(Blocks.MAGMA_BLOCK)
                || heatState.is(Blocks.LAVA)
                || heatState.is(Blocks.CAMPFIRE)
                || heatState.is(Blocks.SOUL_CAMPFIRE);
    }
}
