package net.qiuyu.horrorcooked9.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.items.custom.PummelerItem;
import net.qiuyu.horrorcooked9.register.ModItems;

import java.util.Comparator;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PummelerCombatEvents {
    private static final double OFFHAND_ATTACK_REACH = 3.0D;
    private static final int OFFHAND_ATTACK_COOLDOWN_TICKS = 10;

    private PummelerCombatEvents() {
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (!event.isVanillaCritical() || !PummelerItem.isDualWielding(player)) {
            return;
        }
        if (!PummelerItem.isPummeler(player.getMainHandItem())) {
            return;
        }

        event.setDamageModifier(PummelerItem.getCritMultiplier(player));
        PummelerItem.advanceCritBoost(player);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        Player player = event.getEntity();
        if (tryOffhandAttack(player, target)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }

        Player player = event.getEntity();
        Optional<LivingEntity> target = findOffhandTarget(player);
        if (target.isPresent() && tryOffhandAttack(player, target.get())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof Player victim) {
            PummelerItem.clearCritBoost(victim);
        }
        if (event.getSource().getEntity() instanceof Player attacker
                && event.getSource().getDirectEntity() == attacker
                && PummelerItem.isDualWielding(attacker)) {
            PummelerItem.refreshCritBoost(attacker);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        if (!PummelerItem.isDualWielding(event.player)) {
            PummelerItem.clearCritBoost(event.player);
        }
    }

    private static boolean tryOffhandAttack(Player player, LivingEntity target) {
        ItemStack offhand = player.getOffhandItem();
        if (!PummelerItem.isPummeler(offhand) || !target.isAlive() || target == player) {
            return false;
        }
        if (player.getCooldowns().isOnCooldown(ModItems.PUMMELER.get())) {
            return false;
        }
        if (player.distanceToSqr(target) > OFFHAND_ATTACK_REACH * OFFHAND_ATTACK_REACH) {
            return false;
        }
        if (!target.isAttackable() || !player.hasLineOfSight(target)) {
            return false;
        }
        if (player.level().isClientSide()) {
            return true;
        }
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) {
            return false;
        }

        float damage = PummelerItem.OFFHAND_BASE_DAMAGE;
        boolean critical = isOffhandCritical(player);
        if (critical && PummelerItem.isDualWielding(player)) {
            damage *= PummelerItem.getCritMultiplier(player);
        }

        boolean hurt = target.hurt(player.damageSources().playerAttack(player), damage);
        if (!hurt) {
            player.getCooldowns().addCooldown(ModItems.PUMMELER.get(), OFFHAND_ATTACK_COOLDOWN_TICKS);
            return false;
        }

        PummelerItem.damagePummeler(offhand, player, InteractionHand.OFF_HAND);
        if (critical && PummelerItem.isDualWielding(player)) {
            PummelerItem.advanceCritBoost(player);
        } else if (PummelerItem.isDualWielding(player)) {
            PummelerItem.refreshCritBoost(player);
        }
        player.getCooldowns().addCooldown(ModItems.PUMMELER.get(), OFFHAND_ATTACK_COOLDOWN_TICKS);
        player.swing(InteractionHand.OFF_HAND, true);
        return true;
    }

    private static boolean isOffhandCritical(Player player) {
        return player.getAttackStrengthScale(0.5F) > 0.9F
                && player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.isPassenger()
                && !player.isSprinting();
    }

    private static Optional<LivingEntity> findOffhandTarget(Player player) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(OFFHAND_ATTACK_REACH));
        double blockDistanceSqr = getBlockHitDistanceSqr(player, start, end);
        AABB searchArea = new AABB(
                Math.min(start.x, end.x),
                Math.min(start.y, end.y),
                Math.min(start.z, end.z),
                Math.max(start.x, end.x),
                Math.max(start.y, end.y),
                Math.max(start.z, end.z)
        ).inflate(1.0D);

        return player.level().getEntitiesOfClass(LivingEntity.class, searchArea, entity -> canHit(player, entity))
                .stream()
                .map(entity -> new TargetCandidate(entity, distanceAlongRay(entity, start, end)))
                .filter(candidate -> candidate.distance().isPresent())
                .filter(candidate -> candidate.distance().get() <= blockDistanceSqr + 1.0E-7D)
                .min(Comparator.comparingDouble(candidate -> candidate.distance().get()))
                .map(TargetCandidate::entity);
    }

    private static boolean canHit(Player player, Entity entity) {
        return entity != player && entity.isAlive() && entity.isPickable() && !entity.isSpectator();
    }

    private static Optional<Double> distanceAlongRay(Entity entity, Vec3 start, Vec3 end) {
        return entity.getBoundingBox()
                .inflate(entity.getPickRadius())
                .clip(start, end)
                .map(hit -> start.distanceToSqr(hit));
    }

    private static double getBlockHitDistanceSqr(Player player, Vec3 start, Vec3 end) {
        HitResult hitResult = player.level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        return hitResult.getType() == HitResult.Type.MISS ? OFFHAND_ATTACK_REACH * OFFHAND_ATTACK_REACH : start.distanceToSqr(hitResult.getLocation());
    }

    private record TargetCandidate(LivingEntity entity, Optional<Double> distance) {
    }
}
