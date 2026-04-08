package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.stir.IStirrable;
import net.qiuyu.horrorcooked9.gameplay.stir.StirResult;
import net.qiuyu.horrorcooked9.gameplay.stir.StirToolBalanceConfig;

import java.util.List;

public class SaladBowlItem extends BlockItem implements IStirrable {
    public SaladBowlItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void onStir(Level level, BlockPos pos, Player player, SaladBowlBlockEntity bowlEntity,
                       SaladBowlRecipe recipe, List<StirResult> roundResults) {
        StirResult finalResult = evaluateFinalResult(roundResults);

        float effectiveSuccessChance = StirToolBalanceConfig.resolveEffectiveSuccessChance(
                level.getServer() != null ? level.getServer().getResourceManager() : null,
                player.getMainHandItem(),
                recipe.getMixingSuccessChance()
        );
        boolean successByChance = level.random.nextFloat() <= effectiveSuccessChance;
        if (!successByChance || finalResult == StirResult.RED) {
            dropItems(level, pos, bowlEntity.dumpIngredientsAndReset());
            consumeMixingToolDurability(player);
            return;
        }

        bowlEntity.markOneStirPhaseCompleted();
        if (recipe.canCompleteNow(bowlEntity.getAddedIngredients(), bowlEntity.getCompletedStirPhases())) {
            bowlEntity.completeWith(recipe);
            if (finalResult == StirResult.GREEN) {
                bowlEntity.addBonusServings(1);
            }
        }

        consumeMixingToolDurability(player);
    }

    private StirResult evaluateFinalResult(List<StirResult> roundResults) {
        if (roundResults.isEmpty()) {
            return StirResult.RED;
        }

        int score = 0;
        for (StirResult result : roundResults) {
            score += switch (result) {
                case GREEN -> 2;
                case YELLOW -> 1;
                case RED -> 0;
            };
        }

        int maxScore = roundResults.size() * 2;
        float ratio = (float) score / (float) maxScore;
        if (ratio >= 0.85F) {
            return StirResult.GREEN;
        }
        if (ratio >= 0.5F) {
            return StirResult.YELLOW;
        }
        return StirResult.RED;
    }

    private void consumeMixingToolDurability(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isDamageableItem()) {
            mainHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
    }

    private void dropItems(Level level, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (stack.isEmpty()) {
                continue;
            }
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
            level.addFreshEntity(itemEntity);
        }
    }
}
