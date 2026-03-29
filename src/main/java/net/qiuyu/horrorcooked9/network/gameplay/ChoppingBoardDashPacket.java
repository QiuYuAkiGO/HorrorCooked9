package net.qiuyu.horrorcooked9.network.gameplay;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.qiuyu.horrorcooked9.items.custom.ChoppingBoardItem;

import java.util.List;
import java.util.function.Supplier;

public class ChoppingBoardDashPacket {
    private static final int DASH_COOLDOWN_TICKS = 60;
    private static final double DASH_DISTANCE = 4.0D;
    private static final float DASH_DAMAGE = 3.0F;

    public ChoppingBoardDashPacket() {
    }

    public ChoppingBoardDashPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }

            ItemStack using = player.getUseItem();
            if (!player.isUsingItem() || !(using.getItem() instanceof ChoppingBoardItem)) {
                return;
            }
            if (player.getCooldowns().isOnCooldown(using.getItem())) {
                return;
            }

            Vec3 startPos = player.position();
            Vec3 dashDir = getDashDirection(player);
            Vec3 endPos = moveWithCollision(player, dashDir, DASH_DISTANCE);
            player.hurtMarked = true;

            damageEnemiesOnPath(player, startPos, endPos);
            player.getCooldowns().addCooldown(using.getItem(), DASH_COOLDOWN_TICKS);
        });
        ctx.setPacketHandled(true);
    }

    private static Vec3 getDashDirection(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            return Vec3.directionFromRotation(0.0F, player.getYRot()).normalize();
        }
        return horizontal.normalize();
    }

    private static Vec3 moveWithCollision(ServerPlayer player, Vec3 dir, double distance) {
        Vec3 start = player.position();
        Vec3 best = start;
        int steps = 8;
        for (int i = 1; i <= steps; i++) {
            double d = distance * (i / (double) steps);
            Vec3 candidate = start.add(dir.scale(d));
            AABB movedBox = player.getBoundingBox().move(candidate.subtract(start));
            if (player.level().noCollision(player, movedBox)) {
                best = new Vec3(candidate.x, player.getY(), candidate.z);
            } else {
                break;
            }
        }
        player.teleportTo(best.x, best.y, best.z);
        return best;
    }

    private static void damageEnemiesOnPath(ServerPlayer player, Vec3 start, Vec3 end) {
        Level level = player.level();
        AABB checkBox = new AABB(start, end).inflate(1.2D, 0.8D, 1.2D);
        Vec3 segment = end.subtract(start);
        double segLenSq = Math.max(1.0E-6D, segment.lengthSqr());

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, checkBox,
                e -> e.isAlive() && e != player && isEnemy(e, player));
        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().subtract(start);
            double t = Mth.clamp(toTarget.dot(segment) / segLenSq, 0.0D, 1.0D);
            Vec3 closest = start.add(segment.scale(t));
            if (target.position().distanceToSqr(closest) > 1.44D) {
                continue;
            }
            target.hurt(player.damageSources().playerAttack(player), DASH_DAMAGE);
        }
    }

    private static boolean isEnemy(LivingEntity entity, ServerPlayer player) {
        if (entity instanceof Enemy) {
            return true;
        }
        return entity instanceof Mob mob && mob.getTarget() == player;
    }
}
