package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import net.qiuyu.horrorcooked9.register.ModSounds;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HookMonsterBgmHandler {
    private static final double HEARING_DISTANCE_SQR = 48.0D * 48.0D;
    private static final float BGM_VOLUME = 0.8F;

    private static HookMonsterBgmSound activeSound;
    private static UUID activeMonsterId;

    private HookMonsterBgmHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            stopActiveSound();
            return;
        }

        HookMonsterEntity nearestMonster = findNearestMonster(minecraft.level, minecraft.player);
        if (nearestMonster == null) {
            stopActiveSound();
            return;
        }

        UUID nearestMonsterId = nearestMonster.getUUID();
        if (activeSound == null || activeSound.isStopped() || !nearestMonsterId.equals(activeMonsterId)) {
            stopActiveSound();
            activeSound = new HookMonsterBgmSound(nearestMonster);
            activeMonsterId = nearestMonsterId;
            minecraft.getSoundManager().play(activeSound);
        }
    }

    private static HookMonsterEntity findNearestMonster(ClientLevel level, Player player) {
        HookMonsterEntity nearestMonster = null;
        double nearestDistanceSqr = HEARING_DISTANCE_SQR;

        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof HookMonsterEntity monster) || !monster.isAlive() || monster.isRemoved()) {
                continue;
            }

            double distanceSqr = player.distanceToSqr(monster);
            if (distanceSqr <= nearestDistanceSqr) {
                nearestDistanceSqr = distanceSqr;
                nearestMonster = monster;
            }
        }

        return nearestMonster;
    }

    private static void stopActiveSound() {
        if (activeSound != null && !activeSound.isStopped()) {
            activeSound.requestStop();
        }
        activeSound = null;
        activeMonsterId = null;
    }

    private static final class HookMonsterBgmSound extends AbstractTickableSoundInstance {
        private final HookMonsterEntity monster;

        private HookMonsterBgmSound(HookMonsterEntity monster) {
            super(ModSounds.HOOK_MONSTER_BGM.get(), SoundSource.MUSIC, RandomSource.create());
            this.monster = monster;
            this.looping = true;
            this.delay = 0;
            this.volume = BGM_VOLUME;
            this.pitch = 1.0F;
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            updatePosition();
        }

        @Override
        public void tick() {
            if (!this.monster.isAlive() || this.monster.isRemoved()) {
                requestStop();
                return;
            }

            updatePosition();
        }

        private void requestStop() {
            this.stop();
        }

        private void updatePosition() {
            this.x = this.monster.getX();
            this.y = this.monster.getY();
            this.z = this.monster.getZ();
        }
    }
}
