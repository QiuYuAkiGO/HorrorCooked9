package net.qiuyu.horrorcooked9.network.develop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DataPackUploadFinishPacket {

    private final UUID sessionId;

    public DataPackUploadFinishPacket(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public DataPackUploadFinishPacket(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            DataPackUploadManager.finishUpload(player, sessionId);
        });
        ctx.setPacketHandled(true);
    }
}
