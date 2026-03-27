package net.qiuyu.horrorcooked9.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.network.datapack.DatapackNetworkRegistrar;
import net.qiuyu.horrorcooked9.network.gameplay.GameplayNetworkRegistrar;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.parse(HorrorCooked9.MODID + ":" + "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        id = GameplayNetworkRegistrar.register(CHANNEL, id);
        DatapackNetworkRegistrar.register(CHANNEL, id);
    }
}
