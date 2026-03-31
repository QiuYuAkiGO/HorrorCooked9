package net.qiuyu.horrorcooked9.network.datapack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.qiuyu.horrorcooked9.common.ClientRuntimeBridge;

import java.util.function.Supplier;

public class OpenDataPackPickerPacket {

    private final long maxUploadBytes;

    public OpenDataPackPickerPacket(long maxUploadBytes) {
        this.maxUploadBytes = maxUploadBytes;
    }

    public OpenDataPackPickerPacket(FriendlyByteBuf buf) {
        this.maxUploadBytes = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(maxUploadBytes);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientRuntimeBridge.openDataPackPicker(maxUploadBytes)));
        ctx.setPacketHandled(true);
    }
}
