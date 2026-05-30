package net.qiuyu.horrorcooked9.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.qiuyu.horrorcooked9.config.HookMonsterRuntimeConfig;
import net.qiuyu.horrorcooked9.register.ModEntities;
import net.qiuyu.horrorcooked9.register.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.lang.ref.WeakReference;

public class HookMonsterEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final double HOOK_MIN_RANGE_SQR = 9.0D;
    private static final int EXCREMENT_BURST_MIN = 2;
    private static final int EXCREMENT_BURST_MAX = 4;
    private static final double EXCREMENT_SCATTER_MULTIPLIER = 1.75D;
    private static final double EXCREMENT_SCATTER_MIN_RADIUS = 2.5D;
    private static final double EXCREMENT_THROW_UPWARD = 0.34D;
    private static final double EXCREMENT_THROW_FORWARD = 0.22D;

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("animation.hook_monster.idle");
    private static final RawAnimation ANIM_WALK = RawAnimation.begin().thenLoop("animation.hook_monster.walk");
    private static final RawAnimation ANIM_STUN = RawAnimation.begin().thenLoop("animation.hook_monster.stun");
    private static final RawAnimation ANIM_ATTACK = RawAnimation.begin().thenPlay("animation.hook_monster.attack");

    private int excreteTimer;
    private int hookCooldown;
    private boolean isStunned;
    private int stunTickRemaining;
    @Nullable
    private WeakReference<ExcrementEntity> ownedExcrement;

    public HookMonsterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.excreteTimer = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.EXCRETE_INTERVAL);
        this.hookCooldown = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.HOOK_COOLDOWN);
        this.xpReward = 25;
        this.refreshRuntimeAttributes();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new HookMonsterMeleeGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Raider.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        refreshRuntimeAttributes();

        if (isStunned) {
            this.getNavigation().stop();
            stunTickRemaining--;
            if (stunTickRemaining <= 0) {
                isStunned = false;
            }
            // Stun particles above head
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        this.getX(), this.getY() + this.getBbHeight() + 0.3, this.getZ(),
                        2, 0.25, 0.15, 0.25, 0.1);
            }
            return;
        }

        // Excrete timer
        excreteTimer--;
        if (excreteTimer <= 0) {
            performExcrete();
            excreteTimer = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.EXCRETE_INTERVAL);
        }

        if (hookCooldown > 0) {
            hookCooldown--;
        }

        // Hook attack
        LivingEntity target = this.getTarget();
        if (target != null && hookCooldown <= 0 && canFireHookAt(target)) {
            fireHook(target);
        }
    }

    private void refreshRuntimeAttributes() {
        setAttributeBaseValue(Attributes.MAX_HEALTH, HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.MAX_HEALTH));
        setAttributeBaseValue(Attributes.ARMOR, HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.ARMOR));
        setAttributeBaseValue(Attributes.MOVEMENT_SPEED, HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.MOVE_SPEED));
        setAttributeBaseValue(Attributes.FOLLOW_RANGE, HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.HOOK_MAX_RANGE));
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void setAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null && instance.getBaseValue() != value) {
            instance.setBaseValue(value);
        }
    }

    private boolean canFireHookAt(LivingEntity target) {
        double maxRange = HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.HOOK_MAX_RANGE);
        double distanceSqr = this.distanceToSqr(target);
        return target.isAlive()
                && distanceSqr <= maxRange * maxRange
                && distanceSqr >= HOOK_MIN_RANGE_SQR
                && this.getSensing().hasLineOfSight(target);
    }

    private void performExcrete() {
        int spawnCount = EXCREMENT_BURST_MIN + this.random.nextInt(EXCREMENT_BURST_MAX - EXCREMENT_BURST_MIN + 1);
        double baseRadius = HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.EXCREMENT_SCATTER_RADIUS);
        double expandedRadius = Math.max(EXCREMENT_SCATTER_MIN_RADIUS, baseRadius * EXCREMENT_SCATTER_MULTIPLIER);
        double step = (Math.PI * 2.0D) / spawnCount;
        double baseAngle = this.random.nextDouble() * Math.PI * 2.0D;

        for (int i = 0; i < spawnCount; i++) {
            ExcrementEntity excrement = ModEntities.EXCREMENT.get().create(this.level());
            if (excrement == null) {
                continue;
            }

            double angle = baseAngle + step * i + (this.random.nextDouble() - 0.5D) * 0.45D;
            double distance = expandedRadius * (0.7D + this.random.nextDouble() * 0.3D);
            double targetX = this.getX() + Math.cos(angle) * distance;
            double targetZ = this.getZ() + Math.sin(angle) * distance;

            double spawnX = this.getX();
            double spawnY = this.getY() + 0.4D;
            double spawnZ = this.getZ();
            excrement.moveTo(spawnX, spawnY, spawnZ, this.random.nextFloat() * 360.0F, 0.0F);
            excrement.setOwnerUUID(this.getUUID());

            Vec3 horizontal = new Vec3(targetX - spawnX, 0.0D, targetZ - spawnZ).normalize();
            double speed = EXCREMENT_THROW_FORWARD + this.random.nextDouble() * 0.08D;
            excrement.setDeltaMovement(horizontal.x * speed, EXCREMENT_THROW_UPWARD + this.random.nextDouble() * 0.12D, horizontal.z * speed);

            this.level().addFreshEntity(excrement);
            this.ownedExcrement = new WeakReference<>(excrement);
        }
    }

    public void fireHook(LivingEntity target) {
        if (this.level().isClientSide || target == null) return;
        if (!canFireHookAt(target)) return;

        HookEntity hook = ModEntities.HOOK.get().create(this.level());
        if (hook != null) {
            Vec3 look = this.getLookAngle();
            double spawnOffset = Math.max(this.getBbWidth() * 0.55D, 0.6D);
            double spawnX = this.getX() + look.x * spawnOffset;
            double spawnY = this.getEyeY() - 0.15D;
            double spawnZ = this.getZ() + look.z * spawnOffset;
            hook.setPos(spawnX, spawnY, spawnZ);
            hook.setOwner(this);
            float flightSpeed = (float) HookMonsterRuntimeConfig.getDouble(HookMonsterRuntimeConfig.HOOK_FLIGHT_SPEED);
            hook.shootTowards(target.getX(), target.getY(0.65D), target.getZ(), flightSpeed);
            this.level().addFreshEntity(hook);
            this.hookCooldown = HookMonsterRuntimeConfig.getInt(HookMonsterRuntimeConfig.HOOK_COOLDOWN);
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ExcreteTimer", excreteTimer);
        tag.putInt("HookCooldown", hookCooldown);
        tag.putBoolean("IsStunned", isStunned);
        tag.putInt("StunTickRemaining", stunTickRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        excreteTimer = tag.getInt("ExcreteTimer");
        hookCooldown = tag.getInt("HookCooldown");
        isStunned = tag.getBoolean("IsStunned");
        stunTickRemaining = tag.getInt("StunTickRemaining");
    }

    // GeoEntity implementation

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<HookMonsterEntity> state) {
        if (this.isStunned) {
            return state.setAndContinue(ANIM_STUN);
        }
        if (this.swinging) {
            return state.setAndContinue(ANIM_ATTACK);
        }
        if (state.isMoving()) {
            return state.setAndContinue(ANIM_WALK);
        }
        return state.setAndContinue(ANIM_IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPushable() {
        return !isStunned;
    }

    @Override
    public boolean isImmobile() {
        return isStunned;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    // Getters and setters

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned, int durationTicks) {
        boolean wasStunned = this.isStunned;
        this.isStunned = stunned;
        this.stunTickRemaining = durationTicks;
        if (stunned) {
            this.setTarget(null);
            if (!wasStunned && !this.level().isClientSide && this.isAlive()) {
                this.spawnAtLocation(new ItemStack(ModItems.FERTILIZER.get(), 1));
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(damageSource, looting, recentlyHit);
        int count = 3 + this.random.nextInt(3);
        this.spawnAtLocation(new ItemStack(ModItems.FERTILIZER.get(), count));
    }

    public int getExcreteTimer() {
        return excreteTimer;
    }

    public void setExcreteTimer(int timer) {
        this.excreteTimer = timer;
    }

    public int getHookCooldown() {
        return hookCooldown;
    }

    public void setHookCooldown(int cooldown) {
        this.hookCooldown = cooldown;
    }

    private static class HookMonsterMeleeGoal extends MeleeAttackGoal {
        private final HookMonsterEntity hookMonster;

        public HookMonsterMeleeGoal(HookMonsterEntity hookMonster, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(hookMonster, speedModifier, followingTargetEvenIfNotSeen);
            this.hookMonster = hookMonster;
        }

        @Override
        public boolean canUse() {
            return !this.hookMonster.isStunned() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.hookMonster.isStunned() && super.canContinueToUse();
        }
    }
}
