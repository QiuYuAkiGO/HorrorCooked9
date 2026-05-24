package net.qiuyu.horrorcooked9.events;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.items.custom.ScrewdriverItem;
import net.qiuyu.horrorcooked9.register.ModItems;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ScrewdriverInteractEvents {
    private ScrewdriverInteractEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(ModItems.SCREWDRIVER.get())) {
            return;
        }

        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(event.getPos());
        if (!ScrewdriverItem.canToggleTrapdoor(state)) {
            return;
        }

        if (ScrewdriverItem.tryToggleTrapdoor(level, event.getPos(), event.getEntity(), event.getHand(), event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }
}
