package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.stir.IStirrable;
import net.qiuyu.horrorcooked9.gameplay.stir.StirResult;

import java.util.List;

public class SaladBowlItem extends BlockItem implements IStirrable {
    public SaladBowlItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void onStir(Level level, BlockPos pos, Player player, SaladBowlBlockEntity bowlEntity,
                       SaladBowlRecipe recipe, List<StirResult> roundResults) {
        bowlEntity.applyStirResult(level, pos, player, recipe, roundResults);
    }
}
