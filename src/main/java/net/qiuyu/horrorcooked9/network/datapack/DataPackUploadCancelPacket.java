package net.qiuyu.horrorcooked9.network.datapack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DataPackUploadCancelPacket {

    private final UUID sessionId;
    private final String reason;

    public DataPackUploadCancelPacket(UUID sessionId, String reason) {
        this.sessionId = sessionId;
        this.reason = reason;
    }

    public DataPackUploadCancelPacket(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.reason = buf.readUtf(256);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeUtf(reason == null ? "" : reason, 256);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            DataPackUploadManager.cancelUpload(player, sessionId, reason);
        });
        ctx.setPacketHandled(true);
    }
}
