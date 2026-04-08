package net.qiuyu.horrorcooked9.network.datapack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DataPackUploadChunkPacket {

    private final UUID sessionId;
    private final int chunkIndex;
    private final byte[] chunkData;

    public DataPackUploadChunkPacket(UUID sessionId, int chunkIndex, byte[] chunkData) {
        this.sessionId = sessionId;
        this.chunkIndex = chunkIndex;
        this.chunkData = chunkData;
    }

    public DataPackUploadChunkPacket(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.chunkIndex = buf.readInt();
        this.chunkData = buf.readByteArray();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeInt(chunkIndex);
        buf.writeByteArray(chunkData);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            DataPackUploadManager.appendChunk(player, sessionId, chunkIndex, chunkData);
        });
        ctx.setPacketHandled(true);
    }
}
