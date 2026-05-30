package net.qiuyu.horrorcooked9.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.qiuyu.horrorcooked9.config.HookMonsterRuntimeConfig;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class ExcrementEntity extends LivingEntity {
    private static final int HOOK_PULL_TICKS = 12;
    private static final double HOOK_PULL_SPEED = 1.05D;
    private static final double HOOK_PULL_FINISH_DISTANCE_SQR = 1.2D * 1.2D;
    private static final int STRENGTH_DURATION_TICKS = 20 * 60;
    private static final int MAX_STRENGTH_AMPLIFIER = 9;
    private static final Vector3f DIARRHEA_PARTICLE_COLOR = new Vector3f(46.0F / 255.0F, 46.0F / 255.0F, 46.0F / 255.0F);
    private static final DustParticleOptions DIARRHEA_PARTICLE = new DustParticleOptions(DIARRHEA_PARTICLE_COLOR, 0.85F);

    private UUID ownerUUID;
    private UUID hookPullTargetUUID;
    private int hookPullTicksRemaining;
    private int lifetime;
    private int maxLifetime;
    private float maxHealth;
    private float explosionPower;
    private float explosionDamage;
    private int weaknessDuration;

    public ExcrementEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.maxLifetime = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.EXCREMENT_LIFETIME);
        this.lifetime = this.maxLifetime;
        this.maxHealth = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_MAX_HEALTH);
        this.explosionPower = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_EXPLOSION_POWER);
        this.explosionDamage = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_EXPLOSION_DAMAGE);
        this.weaknessDuration = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.EXCREMENT_WEAKNESS_DURATION);
        this.refreshMaxHealthAttribute();
        this.setHealth(this.maxHealth);
        this.setNoGravity(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D);
    }

    private void refreshMaxHealthAttribute() {
        AttributeInstance maxHealthAttribute = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute != null && maxHealthAttribute.getBaseValue() != maxHealth) {
            maxHealthAttribute.setBaseValue(maxHealth);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        if (this.level() instanceof ServerLevel serverLevel) {
            spawnDiarrheaLikeParticles(serverLevel);
        }

        if (hookPullTicksRemaining > 0 && updateHookPullToOwner()) {
            return;
        }

        lifetime--;
        if (lifetime <= 0) {
            explode();
        }
    }

    private void spawnDiarrheaLikeParticles(ServerLevel serverLevel) {
        if ((this.tickCount & 1) != 0) {
            return;
        }
        double xSpread = this.getBbWidth() * 0.45D;
        double ySpread = this.getBbHeight() * 0.2D;
        double y = this.getY() + this.getBbHeight() * 0.45D;
        serverLevel.sendParticles(
                DIARRHEA_PARTICLE,
                this.getX(),
                y,
                this.getZ(),
                2,
                xSpread,
                ySpread,
                xSpread,
                0.005D
        );
    }

    private boolean updateHookPullToOwner() {
        if (!(this.level() instanceof ServerLevel serverLevel) || hookPullTargetUUID == null) {
            clearHookPull();
            return false;
        }

        Entity target = serverLevel.getEntity(hookPullTargetUUID);
        if (!(target instanceof HookMonsterEntity monster) || !monster.isAlive()) {
            clearHookPull();
            return false;
        }

        Vec3 destination = monster.position().add(0.0D, monster.getBbHeight() * 0.45D, 0.0D);
        Vec3 offset = destination.subtract(this.position());
        if (offset.lengthSqr() <= HOOK_PULL_FINISH_DISTANCE_SQR) {
            this.discard();
            return true;
        }

        Vec3 direction = offset.normalize();
        Vec3 pullVelocity = direction.scale(HOOK_PULL_SPEED).add(0.0D, 0.08D, 0.0D);
        this.setDeltaMovement(pullVelocity);
        this.hasImpulse = true;
        this.hurtMarked = true;
        hookPullTicksRemaining--;
        if (hookPullTicksRemaining <= 0) {
            this.discard();
            return true;
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (source.getEntity() instanceof Player) {
                this.discard();
                return true;
            }
            float newHealth = this.getHealth() - amount;
            if (newHealth <= 0) {
                this.discard();
                return true;
            }
            this.setHealth(newHealth);
        }
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void startHookPullToMonster(HookMonsterEntity monster) {
        this.hookPullTargetUUID = monster.getUUID();
        this.hookPullTicksRemaining = HOOK_PULL_TICKS;
        this.fallDistance = 0.0F;
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    private void clearHookPull() {
        this.hookPullTargetUUID = null;
        this.hookPullTicksRemaining = 0;
    }

    private void explode() {
        Level level = this.level();
        BlockPos blockPos = this.blockPosition();

        // Sound
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

        // Particles
        level.levelEvent(1023, blockPos, 0);

        // Damage entities in range (does not break blocks)
        double radius = explosionPower;
        AABB aabb = this.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != this && e.isAlive());

        for (LivingEntity target : targets) {
            if (target instanceof HookMonsterEntity) {
                continue;
            }
            if (target.distanceToSqr(this) <= radius * radius) {
                target.hurt(this.damageSources().magic(), explosionDamage);
                target.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS, weaknessDuration, 0));
            }
        }

        applyStrengthToOwner();

        this.discard();
    }

    private void applyStrengthToOwner() {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.ownerUUID == null) {
            return;
        }
        Entity owner = serverLevel.getEntity(this.ownerUUID);
        if (!(owner instanceof HookMonsterEntity hookMonster) || !hookMonster.isAlive()) {
            return;
        }

        MobEffectInstance existingStrength = hookMonster.getEffect(MobEffects.DAMAGE_BOOST);
        int nextAmplifier = existingStrength == null
                ? 0
                : Math.min(MAX_STRENGTH_AMPLIFIER, existingStrength.getAmplifier() + 1);
        hookMonster.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                STRENGTH_DURATION_TICKS,
                nextAmplifier,
                false,
                true,
                true
        ));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }
        if (hookPullTargetUUID != null) {
            tag.putUUID("HookPullTarget", hookPullTargetUUID);
            tag.putInt("HookPullTicks", hookPullTicksRemaining);
        }
        tag.putInt("Lifetime", lifetime);
        tag.putInt("MaxLifetime", maxLifetime);
        tag.putFloat("Health", this.getHealth());
        tag.putFloat("MaxHealth", maxHealth);
        tag.putFloat("ExplosionPower", explosionPower);
        tag.putFloat("ExplosionDamage", explosionDamage);
        tag.putInt("WeaknessDuration", weaknessDuration);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) {
            ownerUUID = tag.getUUID("OwnerUUID");
        }
        if (tag.hasUUID("HookPullTarget")) {
            hookPullTargetUUID = tag.getUUID("HookPullTarget");
            hookPullTicksRemaining = tag.getInt("HookPullTicks");
        } else {
            clearHookPull();
        }
        lifetime = tag.getInt("Lifetime");
        maxLifetime = tag.getInt("MaxLifetime");
        maxHealth = tag.getFloat("MaxHealth");
        refreshMaxHealthAttribute();
        explosionPower = tag.contains("ExplosionPower")
                ? tag.getFloat("ExplosionPower")
                : (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_EXPLOSION_POWER);
        explosionDamage = tag.contains("ExplosionDamage")
                ? tag.getFloat("ExplosionDamage")
                : (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_EXPLOSION_DAMAGE);
        weaknessDuration = tag.contains("WeaknessDuration")
                ? tag.getInt("WeaknessDuration")
                : HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.EXCREMENT_WEAKNESS_DURATION);
        if (tag.contains("Health")) {
            this.setHealth(Math.min(tag.getFloat("Health"), maxHealth));
        }
    }

    @Override
    public Iterable<net.minecraft.world.item.ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemBySlot(net.minecraft.world.entity.EquipmentSlot slot) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(net.minecraft.world.entity.EquipmentSlot slot, net.minecraft.world.item.ItemStack stack) {
    }

    @Override
    public net.minecraft.world.entity.HumanoidArm getMainArm() {
        return net.minecraft.world.entity.HumanoidArm.RIGHT;
    }
}
