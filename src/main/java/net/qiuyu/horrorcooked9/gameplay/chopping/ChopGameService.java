package net.qiuyu.horrorcooked9.gameplay.chopping;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;
import net.qiuyu.horrorcooked9.register.ModItems;

/**
 * 服务端权威的切菜结算入口。
 * 网络层只负责反序列化与基础校验，具体产出与耐久消耗在此完成。
 */
public final class ChopGameService {
    private ChopGameService() {
    }

    public static void handleChopResult(ServerPlayer player, BlockPos pos, ChopResult result) {
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChoppingBoardBlockEntity boardEntity) || !boardEntity.hasPlacedItem()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof Cleaver)) {
            return;
        }

        ItemStack placedItem = boardEntity.getPlacedItem();
        ChopperMinigameRecipe recipe = ChopperRecipeMatcher.findByInput(placedItem, level);
        if (recipe == null) {
            return;
        }

        ItemStack output = recipe.getResultFor(result);
        if (!output.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level,
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, output.copy());
            level.addFreshEntity(itemEntity);
        }
        boardEntity.removePlacedItem();

        consumeCleaverDurability(player, mainHand, placedItem);
    }

    private static void consumeCleaverDurability(ServerPlayer player, ItemStack cleaverStack, ItemStack placedItem) {
        if (!cleaverStack.isDamageableItem()) {
            return;
        }

        if (placedItem.is(ModItems.CRYSTAL_TOMATO.get())) {
            int remainingDurability = cleaverStack.getMaxDamage() - cleaverStack.getDamageValue();
            if (remainingDurability > 0) {
                cleaverStack.hurtAndBreak(remainingDurability, player,
                        brokenPlayer -> brokenPlayer.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
            return;
        }

        cleaverStack.hurtAndBreak(1, player,
                brokenPlayer -> brokenPlayer.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }
}
