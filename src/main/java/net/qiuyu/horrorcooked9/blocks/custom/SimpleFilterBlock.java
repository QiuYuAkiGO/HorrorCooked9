package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleFilterBlock extends BaseEntityBlock implements EntityBlock {
    public SimpleFilterBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SimpleFilterBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SimpleFilterBlockEntity filterEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack heldStack = player.getItemInHand(hand);
        if (!heldStack.is(Items.CHARCOAL)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (filterEntity.insertOneCharcoal()) {
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SimpleFilterBlockEntity filterEntity) {
                filterEntity.dropContents();
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
        return createTickerHelper(type, ModBlockEntities.SIMPLE_FILTER_BE.get(), SimpleFilterBlockEntity::serverTick);
    }
}
