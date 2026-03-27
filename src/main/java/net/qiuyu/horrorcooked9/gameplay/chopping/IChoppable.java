package net.qiuyu.horrorcooked9.gameplay.chopping;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 实现此接口的物品放置在砧板上后，可以被玩家手持菜刀右键交互。
 * 交互前会先弹出切割小游戏界面，玩家根据光标位置获得不同的 {@link ChopResult}，
 * 然后将结果传入 {@link #onChop} 方法执行具体的切割逻辑。
 */
public interface IChoppable {

    /**
     * 当玩家完成切割小游戏后调用。
     *
     * @param level      当前世界
     * @param pos        砧板方块位置
     * @param player     执行交互的玩家
     * @param placedItem 砧板上放置的物品
     * @param result     切割小游戏的结果（GREEN=大成功, YELLOW=成功, RED=失败）
     */
    void onChop(Level level, BlockPos pos, Player player, ItemStack placedItem, ChopResult result);
}
