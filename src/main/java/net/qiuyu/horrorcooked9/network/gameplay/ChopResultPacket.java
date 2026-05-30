package net.qiuyu.horrorcooked9.network.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopGameService;
import net.qiuyu.horrorcooked9.gameplay.chopping.ChopResult;
import net.qiuyu.horrorcooked9.items.custom.Cleaver;

import java.util.function.Supplier;

/**
 * 客户端 -> 服务端：发送切割小游戏结果
 */
public class ChopResultPacket {
    private static final double MAX_INTERACTION_DISTANCE_SQR = 64.0D;

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
            if (player == null) {
                return;
            }

            Level level = player.level();
            if (!isInteractionAllowed(player, level)) {
                return;
            }

            ItemStack mainHand = player.getMainHandItem();
            if (!(mainHand.getItem() instanceof Cleaver)) {
                return;
            }

            ChopGameService.handleChopResult(player, pos, ChopResult.fromOrdinal(resultOrdinal));
        });
        ctx.setPacketHandled(true);
    }

    private boolean isInteractionAllowed(ServerPlayer player, Level level) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)
                <= MAX_INTERACTION_DISTANCE_SQR;
    }
}
