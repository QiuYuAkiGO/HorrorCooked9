package net.qiuyu.horrorcooked9.network.datapack;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public final class DatapackNetworkRegistrar {
    private DatapackNetworkRegistrar() {
    }

    public static int register(SimpleChannel channel, int startId) {
        int id = startId;
        channel.messageBuilder(OpenDataPackPickerPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenDataPackPickerPacket::encode)
                .decoder(OpenDataPackPickerPacket::new)
                .consumerMainThread(OpenDataPackPickerPacket::handle)
                .add();
        channel.messageBuilder(DataPackUploadStartPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadStartPacket::encode)
                .decoder(DataPackUploadStartPacket::new)
                .consumerMainThread(DataPackUploadStartPacket::handle)
                .add();
        channel.messageBuilder(DataPackUploadChunkPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadChunkPacket::encode)
                .decoder(DataPackUploadChunkPacket::new)
                .consumerMainThread(DataPackUploadChunkPacket::handle)
                .add();
        channel.messageBuilder(DataPackUploadFinishPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadFinishPacket::encode)
                .decoder(DataPackUploadFinishPacket::new)
                .consumerMainThread(DataPackUploadFinishPacket::handle)
                .add();
        channel.messageBuilder(DataPackUploadCancelPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadCancelPacket::encode)
                .decoder(DataPackUploadCancelPacket::new)
                .consumerMainThread(DataPackUploadCancelPacket::handle)
                .add();
        return id;
    }
}
