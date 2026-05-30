package net.qiuyu.horrorcooked9.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.qiuyu.horrorcooked9.config.HookMonsterRuntimeConfig;
import net.qiuyu.horrorcooked9.events.HookMonsterEvents;
import net.qiuyu.horrorcooked9.register.ModDamageSources;

public class HookEntity extends Projectile {

    private static final EntityDataAccessor<Float> DATA_FLIGHT_SPEED =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.FLOAT);

    private float pullStrength;
    private float damagePercent;

    public HookEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.pullStrength = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.PULL_STRENGTH);
        this.damagePercent = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.DAMAGE_PERCENT);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FLIGHT_SPEED, 1.0F);
    }

    public void setFlightSpeed(float speed) {
        this.entityData.set(DATA_FLIGHT_SPEED, speed);
    }

    public float getFlightSpeed() {
        return this.entityData.get(DATA_FLIGHT_SPEED);
    }

    public void setPullStrength(float strength) {
        this.pullStrength = strength;
    }

    public void setDamagePercent(float percent) {
        this.damagePercent = percent;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() > 1.0E-7D) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
        }

        if (this.isRemoved()) {
            return;
        }

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        this.updateRotationFromMovement(movement);
        this.checkInsideBlocks();

        if (!this.level().isClientSide && this.tickCount > 100) {
            // Despawn after 5 seconds (100 ticks) as a safety measure
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();
        if (entity == owner) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    private void updateRotationFromMovement(Vec3 movement) {
        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        if (horizontal <= 1.0E-7D && Math.abs(movement.y) <= 1.0E-7D) {
            return;
        }
        float yaw = (float) (Mth.atan2(movement.x, movement.z) * (180.0F / (float) Math.PI));
        float pitch = (float) (Mth.atan2(movement.y, horizontal) * (180.0F / (float) Math.PI));
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) return;

        if (result.getEntity() instanceof ExcrementEntity excrement) {
            // Hook hit own excrement — stun the monster
            if (this.getOwner() instanceof HookMonsterEntity monster) {
                if (excrement.getOwnerUUID() != null &&
                        excrement.getOwnerUUID().equals(monster.getUUID())) {
                    monster.setStunned(true, HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.STUN_DURATION));
                    monster.removeEffect(MobEffects.DAMAGE_BOOST);
                    excrement.startHookPullToMonster(monster);
                }
            }
        } else if (result.getEntity() instanceof Player player) {
            // Pull player to monster at extreme speed
            if (this.getOwner() instanceof HookMonsterEntity monster) {
                Vec3 pullDir = monster.position().subtract(player.position()).normalize();
                double strength = pullStrength * 2.0; // extreme pull
                player.setDeltaMovement(pullDir.x * strength, pullDir.y * 0.5 + 0.3, pullDir.z * strength);
                player.hurtMarked = true;
                player.hasImpulse = true;
                HookMonsterEvents.startHookPull(player, monster);

                // Repeated server-side velocity in HookMonsterEvents keeps control locked briefly.
                float damage = player.getMaxHealth() * damagePercent;
                player.hurt(ModDamageSources.hookMonsterHook(this.level(), this, monster), damage);
            }
        }

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Hit a block — retract, no effect
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    public void shootTowards(LivingEntity target, float flightSpeed) {
        this.shootTowards(target.getX(), target.getY(0.65D), target.getZ(), flightSpeed);
    }

    public void shootTowards(double targetX, double targetY, double targetZ, float flightSpeed) {
        Vec3 direction = new Vec3(targetX - this.getX(), targetY - this.getY(), targetZ - this.getZ()).normalize();
        this.setDeltaMovement(direction.x * flightSpeed, direction.y * flightSpeed, direction.z * flightSpeed);
        this.setFlightSpeed(flightSpeed);
        this.updateRotationFromMovement(this.getDeltaMovement());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("PullStrength", pullStrength);
        tag.putFloat("DamagePercent", damagePercent);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        pullStrength = tag.getFloat("PullStrength");
        damagePercent = tag.getFloat("DamagePercent");
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
