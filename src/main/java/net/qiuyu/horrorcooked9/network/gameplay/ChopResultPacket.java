package net.qiuyu.horrorcooked9.network.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopResult;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopperMinigameRecipe;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopperRecipeMatcher;

import java.util.function.Supplier;

/**
 * 客户端 -> 服务端：发送切割小游戏结果
 */
public class ChopResultPacket {

    private final BlockPos pos;
    private final int resultOrdinal;

    public ChopResultPacket(BlockPos pos, ChopResult result) {
        this.pos = pos;
        this.resultOrdinal = result.ordinal();
    }

    public ChopResultPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.resultOrdinal = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(resultOrdinal);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level();
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ChoppingBoardBlockEntity boardEntity)) return;

            if (!boardEntity.hasPlacedItem()) return;

            ItemStack placedItem = boardEntity.getPlacedItem();
            ChopResult result = ChopResult.fromOrdinal(resultOrdinal);
            ChopperMinigameRecipe recipe = ChopperRecipeMatcher.findByInput(placedItem, level);
            if (recipe == null) {
                return;
            }

            ItemStack output = recipe.getResultFor(result);
            if (!output.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, output);
                level.addFreshEntity(itemEntity);
            }
            boardEntity.removePlacedItem();

            // 消耗菜刀耐久
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof net.qiuyu.horrorcooked9.items.custom.Cleaver) {
                mainHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        });
        ctx.setPacketHandled(true);
    }
}
