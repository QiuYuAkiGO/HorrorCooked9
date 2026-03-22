package net.qiuyu.horrorcooked9.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.qiuyu.horrorcooked9.HorrorCooked9;

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
    }
}
