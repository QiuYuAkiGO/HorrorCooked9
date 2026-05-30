package net.qiuyu.horrorcooked9.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.config.HookMonsterRuntimeConfig;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HookMonsterEvents {
    private static final String HOOK_PULL_TARGET = HorrorCooked9.MODID + ".HookPullTarget";
    private static final String HOOK_PULL_TICKS = HorrorCooked9.MODID + ".HookPullTicks";
    private static final int DEFAULT_PULL_TICKS = 12;
    private static final double PULL_FINISH_DISTANCE_SQR = 2.25D;

    private HookMonsterEvents() {
    }

    public static void startHookPull(Player player, HookMonsterEntity monster) {
        CompoundTag data = player.getPersistentData();
        data.putUUID(HOOK_PULL_TARGET, monster.getUUID());
        data.putInt(HOOK_PULL_TICKS, DEFAULT_PULL_TICKS);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        if (!(event.player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        CompoundTag data = event.player.getPersistentData();
        if (!data.hasUUID(HOOK_PULL_TARGET) || !data.contains(HOOK_PULL_TICKS)) {
            return;
        }

        int ticks = data.getInt(HOOK_PULL_TICKS);
        Entity target = serverLevel.getEntity(data.getUUID(HOOK_PULL_TARGET));
        if (ticks <= 0 || !(target instanceof HookMonsterEntity monster) || !monster.isAlive()) {
            clearHookPull(data);
            return;
        }

        Vec3 offset = monster.position().subtract(event.player.position());
        if (offset.lengthSqr() <= PULL_FINISH_DISTANCE_SQR) {
            clearHookPull(data);
            return;
        }

        Vec3 direction = offset.normalize();
        double strength = HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.PULL_STRENGTH) * 2.0D;
        event.player.setDeltaMovement(direction.x * strength, direction.y * 0.5D + 0.3D, direction.z * strength);
        event.player.hurtMarked = true;
        event.player.hasImpulse = true;
        event.player.fallDistance = 0.0F;
        data.putInt(HOOK_PULL_TICKS, ticks - 1);
    }

    private static void clearHookPull(CompoundTag data) {
        data.remove(HOOK_PULL_TARGET);
        data.remove(HOOK_PULL_TICKS);
    }
}
