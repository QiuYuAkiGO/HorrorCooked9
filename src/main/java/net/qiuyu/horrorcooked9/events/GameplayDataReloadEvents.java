package net.qiuyu.horrorcooked9.events;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import net.qiuyu.horrorcooked9.gameplay.sharpen.SharpeningStoneConfig;
import net.qiuyu.horrorcooked9.gameplay.stir.StirToolBalanceConfig;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GameplayDataReloadEvents {

    private GameplayDataReloadEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                return null;
            }

            @Override
            protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                FoodRuntimeConfigs.reload(resourceManager);
                StirToolBalanceConfig.reload(resourceManager);
                SharpeningStoneConfig.reload(resourceManager);
            }
        });
    }
}
