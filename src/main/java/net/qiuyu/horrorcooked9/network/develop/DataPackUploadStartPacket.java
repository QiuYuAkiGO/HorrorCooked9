package net.qiuyu.horrorcooked9.network.develop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DataPackUploadStartPacket {

    private final UUID sessionId;
    private final String packName;
    private final int totalSize;
    private final int totalChunks;
    private final String sha256;

    public DataPackUploadStartPacket(UUID sessionId, String packName, int totalSize, int totalChunks, String sha256) {
        this.sessionId = sessionId;
        this.packName = packName;
        this.totalSize = totalSize;
        this.totalChunks = totalChunks;
        this.sha256 = sha256;
    }

    public DataPackUploadStartPacket(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.packName = buf.readUtf(128);
        this.totalSize = buf.readInt();
        this.totalChunks = buf.readInt();
        this.sha256 = buf.readUtf(128);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeUtf(packName, 128);
        buf.writeInt(totalSize);
        buf.writeInt(totalChunks);
        buf.writeUtf(sha256, 128);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            DataPackUploadManager.startUpload(player, sessionId, packName, totalSize, totalChunks, sha256);
        });
        ctx.setPacketHandled(true);
    }
}
