package net.qiuyu.horrorcooked9.gameplay.stir;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;

import java.util.List;

/**
 * 由可触发 Stir 结算的物品实现。
 */
public interface IStirrable {
    void onStir(Level level, BlockPos pos, Player player, SaladBowlBlockEntity bowlEntity,
                SaladBowlRecipe recipe, List<StirResult> roundResults);
}
