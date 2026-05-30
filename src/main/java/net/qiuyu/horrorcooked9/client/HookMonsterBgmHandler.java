package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import net.qiuyu.horrorcooked9.register.ModSounds;

import java.util.UUID;

public final class HookMonsterBgmHandler {
    private static final double HEARING_DISTANCE = 48.0D;
    private static final double HEARING_DISTANCE_SQR = HEARING_DISTANCE * HEARING_DISTANCE;
    private static final float BGM_VOLUME = 0.8F;

    private static HookMonsterBgmSound activeSound;
    private static UUID activeMonsterId;

    private HookMonsterBgmHandler() {
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            stopActiveSound();
            return;
        }

        if (!minecraft.player.isAlive()) {
            if (activeSound != null && !activeSound.isStopped() && activeSound.canContinue()) {
                activeSound.setVolume(0.0F);
            } else {
                stopActiveSound();
            }
            return;
        }

        if (activeSound != null && !activeSound.isStopped() && activeSound.canContinue()
                && activeSound.shouldBeAudibleFor(minecraft.player)) {
            activeSound.updateVolumeFor(minecraft.player);
            stopVanillaMusic(minecraft);
            return;
        }

        HookMonsterEntity nearestMonster = findNearestMonster(minecraft.level, minecraft.player);
        if (nearestMonster == null) {
            if (activeSound != null && !activeSound.isStopped() && activeSound.canContinue()) {
                activeSound.setVolume(0.0F);
            } else {
                stopActiveSound();
            }
            return;
        }

        UUID nearestMonsterId = nearestMonster.getUUID();
        if (activeSound != null && !activeSound.isStopped() && activeSound.canContinue() && nearestMonsterId.equals(activeMonsterId)) {
            if (activeSound.updateVolumeFor(minecraft.player) > 0.0F) {
                stopVanillaMusic(minecraft);
            }
            return;
        }

        stopActiveSound();
        activeSound = new HookMonsterBgmSound(nearestMonster);
        activeMonsterId = nearestMonsterId;
        if (activeSound.updateVolumeFor(minecraft.player) > 0.0F) {
            stopVanillaMusic(minecraft);
        }
        minecraft.getSoundManager().play(activeSound);
    }

    private static HookMonsterEntity findNearestMonster(ClientLevel level, Player player) {
        HookMonsterEntity nearestMonster = null;
        double nearestDistanceSqr = HEARING_DISTANCE_SQR;

        for (HookMonsterEntity monster : level.getEntitiesOfClass(HookMonsterEntity.class, player.getBoundingBox().inflate(HEARING_DISTANCE),
                monster -> monster.isAlive() && !monster.isRemoved())) {
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

    private static void stopVanillaMusic(Minecraft minecraft) {
        minecraft.getMusicManager().stopPlaying();
    }

    private static final class HookMonsterBgmSound extends AbstractTickableSoundInstance {
        private final HookMonsterEntity monster;

        private HookMonsterBgmSound(HookMonsterEntity monster) {
            super(ModSounds.HOOK_MONSTER_BGM.get(), SoundSource.MUSIC, RandomSource.create());
            this.monster = monster;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0F;
            this.pitch = 1.0F;
            this.relative = true;
            this.attenuation = SoundInstance.Attenuation.NONE;
            centerOnListener();
        }

        @Override
        public void tick() {
            if (!canContinue()) {
                requestStop();
                return;
            }

            centerOnListener();
        }

        private void requestStop() {
            this.stop();
        }

        private boolean canContinue() {
            return this.monster.isAlive() && !this.monster.isRemoved();
        }

        private boolean shouldBeAudibleFor(Player player) {
            return player.isAlive()
                    && player.distanceToSqr(this.monster) <= HEARING_DISTANCE_SQR
                    && hasSoundPathTo(player);
        }

        private float updateVolumeFor(Player player) {
            float nextVolume = calculateVolumeFor(player);
            setVolume(nextVolume);
            return nextVolume;
        }

        private float calculateVolumeFor(Player player) {
            if (!shouldBeAudibleFor(player)) {
                return 0.0F;
            }

            double distance = Math.sqrt(player.distanceToSqr(this.monster));
            double distanceFactor = 1.0D - Math.min(distance, HEARING_DISTANCE) / HEARING_DISTANCE;
            return (float) (BGM_VOLUME * distanceFactor);
        }

        private boolean hasSoundPathTo(Player player) {
            Vec3 playerEye = player.getEyePosition();
            Vec3 playerCenter = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);
            Vec3 monsterEye = this.monster.getEyePosition();
            Vec3 monsterCenter = this.monster.position().add(0.0D, this.monster.getBbHeight() * 0.5D, 0.0D);

            return hasClearPath(player, playerEye, monsterEye)
                    || hasClearPath(player, playerEye, monsterCenter)
                    || hasClearPath(player, playerCenter, monsterEye)
                    || hasClearPath(player, playerCenter, monsterCenter);
        }

        private boolean hasClearPath(Player player, Vec3 from, Vec3 to) {
            ClipContext context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
            return this.monster.level().clip(context).getType() == HitResult.Type.MISS;
        }

        private void setVolume(float volume) {
            this.volume = volume;
        }

        private void centerOnListener() {
            this.x = 0.0D;
            this.y = 0.0D;
            this.z = 0.0D;
        }
    }
}
