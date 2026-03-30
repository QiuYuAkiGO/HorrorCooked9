package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JuicerBlock extends BaseEntityBlock implements EntityBlock {
    public JuicerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new JuicerBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof JuicerBlockEntity juicer)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            return juicer.beginManualPress(player) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = juicer;
            NetworkHooks.openScreen(serverPlayer, provider, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@NotNull BlockState state,
                         @NotNull Level level,
                         @NotNull BlockPos pos,
                         @NotNull BlockState newState,
                         boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof JuicerBlockEntity juicer) {
                juicer.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                   @NotNull BlockState state,
                                                                   @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.JUICER_BE.get(), JuicerBlockEntity::serverTick);
    }
}
