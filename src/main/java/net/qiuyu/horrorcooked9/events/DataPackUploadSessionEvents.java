package net.qiuyu.horrorcooked9.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.network.datapack.DataPackUploadManager;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataPackUploadSessionEvents {
    private static final int SESSION_EXPIRY_CHECK_INTERVAL_TICKS = 200;

    private DataPackUploadSessionEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % SESSION_EXPIRY_CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        DataPackUploadManager.expireSessionsForPlayer(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DataPackUploadManager.cleanupSessionsForPlayer(player.getUUID());
        }
    }
}
