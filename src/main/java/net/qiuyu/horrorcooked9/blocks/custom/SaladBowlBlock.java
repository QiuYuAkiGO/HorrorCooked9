package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qiuyu.horrorcooked9.client.ClientHelper;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladRecipeMatcher;
import net.qiuyu.horrorcooked9.gameplay.stir.StirToolBalanceConfig;
import net.qiuyu.horrorcooked9.register.ModRecipes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SaladBowlBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);

    public SaladBowlBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new SaladBowlBlockEntity(pPos, pState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof SaladBowlBlockEntity bowlEntity)) {
            return InteractionResult.PASS;
        }
        if (!pLevel.getBlockState(pPos.below()).is(ModBlocks.FOODWORKS_TABLE.get())) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        List<SaladBowlRecipe> recipes = pLevel.getRecipeManager().getAllRecipesFor(ModRecipes.SALAD_BOWL_TYPE.get());

        if (recipes.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (bowlEntity.isCompleted()) {
            if (pLevel.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            return handleServe(pLevel, pPos, pPlayer, heldItem, bowlEntity, recipes);
        }

        if (heldItem.isEmpty()) {
            return InteractionResult.PASS;
        }

        List<ItemStack> currentSequence = new ArrayList<>(bowlEntity.getAddedIngredients());
        SaladBowlRecipe exactRecipe = SaladRecipeMatcher.findExactMatch(currentSequence, recipes);
        if (exactRecipe != null && exactRecipe.getMixingTool().test(heldItem)) {
            int requiredStirCount = StirToolBalanceConfig.resolveEffectiveStirCount(
                    pLevel.getServer() != null ? pLevel.getServer().getResourceManager() : null,
                    heldItem,
                    exactRecipe.getStirCount()
            );
            if (pLevel.isClientSide()) {
                ClientHelper.openStirMinigame(pPos, requiredStirCount);
            }
            return InteractionResult.CONSUME;
        }

        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack ingredientToAdd = heldItem.copy();
        ingredientToAdd.setCount(1);
        List<ItemStack> nextSequence = new ArrayList<>(currentSequence);
        nextSequence.add(ingredientToAdd);
        List<SaladBowlRecipe> candidates = SaladRecipeMatcher.findPrefixMatches(nextSequence, recipes);

        if (candidates.isEmpty()) {
            List<ItemStack> dropped = bowlEntity.dumpIngredientsAndReset();
            dropped.add(ingredientToAdd);
            if (!pPlayer.getAbilities().instabuild) {
                 heldItem.shrink(1);
            }
            dropItems(pLevel, pPos, dropped);
            return InteractionResult.CONSUME;
        }

        bowlEntity.addIngredient(ingredientToAdd);
        SaladBowlRecipe nextExact = SaladRecipeMatcher.findExactMatch(nextSequence, recipes);
        bowlEntity.setCurrentRecipeId(nextExact != null ? nextExact.getId() : null);

        if (!pPlayer.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private InteractionResult handleServe(Level pLevel, BlockPos pPos, Player player, ItemStack heldItem,
                                          SaladBowlBlockEntity bowlEntity, List<SaladBowlRecipe> recipes) {
        SaladBowlRecipe recipe = resolveCompletedRecipe(bowlEntity, recipes);
        if (recipe == null) {
            bowlEntity.resetAll();
            return InteractionResult.CONSUME;
        }

        if (!recipe.getServingContainer().test(heldItem)) {
            return InteractionResult.PASS;
        }

        ItemStack serving = bowlEntity.getResultStack();
        if (serving.isEmpty()) {
            bowlEntity.resetAll();
            return InteractionResult.CONSUME;
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        if (!player.getInventory().add(serving.copy())) {
            player.drop(serving.copy(), false);
        }

        bowlEntity.consumeOneServingOrReset();
        return InteractionResult.CONSUME;
    }

    @Nullable
    private SaladBowlRecipe resolveCompletedRecipe(SaladBowlBlockEntity bowlEntity, List<SaladBowlRecipe> recipes) {
        if (bowlEntity.getCurrentRecipeId() == null) {
            return SaladRecipeMatcher.findExactMatch(bowlEntity.getAddedIngredients(), recipes);
        }

        for (SaladBowlRecipe recipe : recipes) {
            if (recipe.getId().equals(bowlEntity.getCurrentRecipeId())) {
                return recipe;
            }
        }
        return null;
    }

    private void dropItems(Level level, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (stack.isEmpty()) continue;
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
            level.addFreshEntity(itemEntity);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof SaladBowlBlockEntity bowlEntity) {
                dropItems(pLevel, pPos, bowlEntity.getBreakDrops());
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos,
                                        @NotNull CollisionContext pContext) {
        return SHAPE;
    }
}
