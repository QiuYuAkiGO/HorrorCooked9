package net.qiuyu.horrorcooked9.gameplay.stir;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;

import java.util.List;

/**
 * 由可触发 Stir 结算的物品实现。
 * 在搅拌小游戏结束后，由具体物品根据每轮判定结果执行结算逻辑。
 */
public interface IStirrable {
    /**
     * 搅拌小游戏结算回调。
     *
     * @param level        当前世界
     * @param pos          沙拉碗位置
     * @param player       执行操作的玩家
     * @param bowlEntity   当前沙拉碗方块实体
     * @param recipe       本次匹配到的沙拉配方
     * @param roundResults 每一轮小游戏的判定结果，顺序与点击轮次一致
     */
    void onStir(Level level, BlockPos pos, Player player, SaladBowlBlockEntity bowlEntity,
                SaladBowlRecipe recipe, List<StirResult> roundResults);
}
