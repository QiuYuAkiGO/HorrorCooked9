package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.items.custom.ChoppingBoardItem;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.network.gameplay.ChoppingBoardDashPacket;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChoppingBoardDashClientHandler {
    private ChoppingBoardDashClientHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        ItemStack useItem = player.getUseItem();
        if (!player.isUsingItem() || !(useItem.getItem() instanceof ChoppingBoardItem)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(useItem.getItem())) {
            return;
        }

        while (mc.options.keyAttack.consumeClick()) {
            ModNetworking.CHANNEL.sendToServer(new ChoppingBoardDashPacket());
            break;
        }
    }
}
