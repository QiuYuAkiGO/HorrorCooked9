package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
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
import net.qiuyu.horrorcooked9.common.ClientRuntimeBridge;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladRecipeMatcher;
import net.qiuyu.horrorcooked9.gameplay.stir.StirToolBalanceConfig;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModRecipes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
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
        if (pPlayer.isShiftKeyDown()) {
            return handleSneakPickup(pLevel, pPos, pPlayer, pHand, pHit, bowlEntity);
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
        SaladBowlRecipe stirRecipe = resolveStirRecipe(bowlEntity, currentSequence, recipes);
        boolean exactNow = stirRecipe != null && SaladRecipeMatcher.isExactMatch(currentSequence, stirRecipe);
        if (stirRecipe != null
                && stirRecipe.requiresStirNow(currentSequence.size(), bowlEntity.getCompletedStirPhases(), exactNow)
                && stirRecipe.getMixingTool().test(heldItem)) {
            int requiredStirCount = StirToolBalanceConfig.resolveEffectiveStirCount(
                    pLevel.getServer() != null ? pLevel.getServer().getResourceManager() : null,
                    heldItem,
                    stirRecipe.getStirCount()
            );
            if (pLevel.isClientSide()) {
                ClientRuntimeBridge.openStirMinigame(pPos, requiredStirCount);
            }
            return InteractionResult.CONSUME;
        }

        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (isLockedByPendingStir(bowlEntity, currentSequence, recipes)) {
            return InteractionResult.CONSUME;
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
        bowlEntity.setCurrentRecipeId(resolveTrackedRecipeId(nextSequence, candidates));

        if (!pPlayer.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private InteractionResult handleSneakPickup(Level level, BlockPos pos, Player player, InteractionHand hand,
                                                BlockHitResult hitResult, SaladBowlBlockEntity bowlEntity) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (!player.getAbilities().mayBuild || !player.mayUseItemAt(pos, hitResult.getDirection(), heldItem)) {
            return InteractionResult.PASS;
        }

        List<ItemStack> drops = new ArrayList<>();
        if (bowlEntity.isCompleted()) {
            ItemStack serving = bowlEntity.getResultStack();
            if (!serving.isEmpty()) {
                int servings = bowlEntity.getRemainingServings();
                for (int i = 0; i < servings; i++) {
                    drops.add(serving.copy());
                }
            }
            bowlEntity.resetAll();
        } else {
            drops.addAll(bowlEntity.dumpIngredientsAndReset());
        }

        dropItems(level, pos, drops);
        level.removeBlock(pos, false);

        ItemStack bowlToReturn = new ItemStack(ModItems.SALAD_BOWL.get());
        if (!player.getInventory().add(bowlToReturn)) {
            player.drop(bowlToReturn, false);
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

    @Nullable
    private SaladBowlRecipe resolveStirRecipe(SaladBowlBlockEntity bowlEntity, List<ItemStack> currentSequence,
                                              List<SaladBowlRecipe> recipes) {
        SaladBowlRecipe exact = SaladRecipeMatcher.findExactMatch(currentSequence, recipes);
        if (exact != null) {
            return exact;
        }

        if (bowlEntity.getCurrentRecipeId() != null) {
            for (SaladBowlRecipe recipe : recipes) {
                if (recipe.getId().equals(bowlEntity.getCurrentRecipeId())
                        && SaladRecipeMatcher.isPrefixMatch(currentSequence, recipe)) {
                    return recipe;
                }
            }
        }

        return SaladRecipeMatcher.findPrefixMatches(currentSequence, recipes).stream()
                .sorted(Comparator.comparingInt((SaladBowlRecipe recipe) -> recipe.getIngredientSlots().size())
                        .thenComparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .orElse(null);
    }

    private boolean isLockedByPendingStir(SaladBowlBlockEntity bowlEntity, List<ItemStack> currentSequence, List<SaladBowlRecipe> recipes) {
        SaladBowlRecipe tracked = resolveStirRecipe(bowlEntity, currentSequence, recipes);
        if (tracked == null || !tracked.hasCustomStirCheckpoints()) {
            return false;
        }
        boolean exactNow = SaladRecipeMatcher.isExactMatch(currentSequence, tracked);
        return tracked.requiresStirNow(currentSequence.size(), bowlEntity.getCompletedStirPhases(), exactNow);
    }

    @Nullable
    private ResourceLocation resolveTrackedRecipeId(List<ItemStack> sequence, List<SaladBowlRecipe> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        SaladBowlRecipe exact = SaladRecipeMatcher.findExactMatch(sequence, candidates);
        if (exact != null) {
            return exact.getId();
        }
        return candidates.stream()
                .sorted(Comparator.comparingInt((SaladBowlRecipe recipe) -> recipe.getIngredientSlots().size())
                        .thenComparing(recipe -> recipe.getId().toString()))
                .findFirst()
                .map(SaladBowlRecipe::getId)
                .orElse(null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos,
                                        @NotNull CollisionContext pContext) {
        return SHAPE;
    }
}
