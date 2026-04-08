package net.qiuyu.horrorcooked9.network.gameplay;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public final class GameplayNetworkRegistrar {
    private GameplayNetworkRegistrar() {
    }

    public static int register(SimpleChannel channel, int startId) {
        int id = startId;
        channel.messageBuilder(ChopResultPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChopResultPacket::encode)
                .decoder(ChopResultPacket::new)
                .consumerMainThread(ChopResultPacket::handle)
                .add();
        channel.messageBuilder(StirResultPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(StirResultPacket::encode)
                .decoder(StirResultPacket::new)
                .consumerMainThread(StirResultPacket::handle)
                .add();
        return id;
    }
}
