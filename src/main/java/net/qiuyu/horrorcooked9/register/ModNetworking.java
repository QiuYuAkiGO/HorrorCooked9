package net.qiuyu.horrorcooked9.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.network.develop.*;
import net.qiuyu.horrorcooked9.network.gameplay.ChopResultPacket;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.parse(HorrorCooked9.MODID+ ":" + "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(ChopResultPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChopResultPacket::encode)
                .decoder(ChopResultPacket::new)
                .consumerMainThread(ChopResultPacket::handle)
                .add();
        CHANNEL.messageBuilder(OpenDataPackPickerPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenDataPackPickerPacket::encode)
                .decoder(OpenDataPackPickerPacket::new)
                .consumerMainThread(OpenDataPackPickerPacket::handle)
                .add();
        CHANNEL.messageBuilder(DataPackUploadStartPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadStartPacket::encode)
                .decoder(DataPackUploadStartPacket::new)
                .consumerMainThread(DataPackUploadStartPacket::handle)
                .add();
        CHANNEL.messageBuilder(DataPackUploadChunkPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadChunkPacket::encode)
                .decoder(DataPackUploadChunkPacket::new)
                .consumerMainThread(DataPackUploadChunkPacket::handle)
                .add();
        CHANNEL.messageBuilder(DataPackUploadFinishPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadFinishPacket::encode)
                .decoder(DataPackUploadFinishPacket::new)
                .consumerMainThread(DataPackUploadFinishPacket::handle)
                .add();
        CHANNEL.messageBuilder(DataPackUploadCancelPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DataPackUploadCancelPacket::encode)
                .decoder(DataPackUploadCancelPacket::new)
                .consumerMainThread(DataPackUploadCancelPacket::handle)
                .add();
    }
}
