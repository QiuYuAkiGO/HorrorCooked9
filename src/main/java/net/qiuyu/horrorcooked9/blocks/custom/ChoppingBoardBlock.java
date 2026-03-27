package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qiuyu.horrorcooked9.client.ClientHelper;
import net.qiuyu.horrorcooked9.gameplay.chopping.IChoppable;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChoppingBoardBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // 碰撞箱定义 —— Block.box(minX, minY, minZ, maxX, maxY, maxZ)
    // 每个参数的范围为 0~16，对应方块内的 0~1 格（16 = 1格 = 1个方块）
    // 当前值：底面全覆盖，高度为 1（即 1/16 方块高）
    // 如需调整碰撞箱，直接修改下面 6 个数值即可
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 2.5, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 2.5, 16);
    private static final VoxelShape SHAPE_WEST  = Block.box(0, 0, 0, 16, 2.5, 16);
    private static final VoxelShape SHAPE_EAST  = Block.box(0, 0, 0, 16, 2.5, 16);

    public ChoppingBoardBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ChoppingBoardBlockEntity(pPos, pState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    // 右键交互：放置或取下 Cleaver
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof ChoppingBoardBlockEntity boardEntity)) {
            return InteractionResult.PASS;
        }
        if (!pLevel.getBlockState(pPos.below()).is(ModBlocks.FOODWORKS_TABLE.get())) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = pPlayer.getItemInHand(pHand);

        if (boardEntity.hasPlacedItem()) {
            // 手持菜刀右键：打开切割小游戏
            if (heldItem.getItem() instanceof Cleaver) {
                ItemStack placedItem = boardEntity.getPlacedItem();
                if (placedItem.getItem() instanceof IChoppable) {
                    if (pLevel.isClientSide()) {
                        ClientHelper.openChopMinigame(pPos);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if (pLevel.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            // 取下物品
            ItemStack removed = boardEntity.removePlacedItem();
            if (!pPlayer.getInventory().add(removed)) {
                pPlayer.drop(removed, false);
            }
            return InteractionResult.CONSUME;
        } else if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() instanceof Cleaver) {
            // 主手持菜刀时，优先将副手的IChoppable物品放入砧板
            ItemStack offhandItem = pPlayer.getOffhandItem();
            if (offhandItem.getItem() instanceof IChoppable && offhandItem.is(ModTags.Items.CHOPPER_PLACEABLE)) {
                ItemStack toPlace = offhandItem.copy();
                toPlace.setCount(1);
                boardEntity.setPlacedItem(toPlace);
                offhandItem.shrink(1);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        } else if (heldItem.is(ModTags.Items.CHOPPER_PLACEABLE)) {
            // 放置可放置物品
            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            boardEntity.setPlacedItem(toPlace);
            heldItem.shrink(1);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // 方块被破坏时掉落放置的物品
    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof ChoppingBoardBlockEntity boardEntity && boardEntity.hasPlacedItem()) {
                ItemStack item = boardEntity.removePlacedItem();
                ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, item);
                pLevel.addFreshEntity(itemEntity);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    // 根据方块朝向返回对应的碰撞箱形状
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            case EAST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };
    }

    // 注册方块状态属性（FACING：水平朝向）
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    // 放置时自动面向玩家（取玩家水平朝向的反方向）
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}
