package net.qiuyu.horrorcooked9.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModServerConfig {

    public static final ForgeConfigSpec SPEC;

    // Datapack upload
    public static final ForgeConfigSpec.BooleanValue ENABLE_DATAPACK_UPLOAD;
    public static final ForgeConfigSpec.LongValue MAX_UPLOAD_SIZE_MB;

    // Hook Monster
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_ARMOR;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_MOVE_SPEED;
    public static final ForgeConfigSpec.IntValue HOOK_MONSTER_EXCRETE_INTERVAL;
    public static final ForgeConfigSpec.IntValue HOOK_MONSTER_HOOK_COOLDOWN;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_HOOK_MAX_RANGE;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_PULL_STRENGTH;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_DAMAGE_PERCENT;
    public static final ForgeConfigSpec.IntValue HOOK_MONSTER_STUN_DURATION;
    public static final ForgeConfigSpec.IntValue HOOK_MONSTER_EXCREMENT_LIFETIME;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_EXCREMENT_SCATTER_RADIUS;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_EXCREMENT_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_EXCREMENT_EXPLOSION_POWER;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_EXCREMENT_EXPLOSION_DAMAGE;
    public static final ForgeConfigSpec.IntValue HOOK_MONSTER_EXCREMENT_WEAKNESS_DURATION;
    public static final ForgeConfigSpec.DoubleValue HOOK_MONSTER_HOOK_FLIGHT_SPEED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("datapackUpload");
        ENABLE_DATAPACK_UPLOAD = builder
                .comment("Whether /datapack upload is enabled on dedicated servers.")
                .define("enabled", true);
        MAX_UPLOAD_SIZE_MB = builder
                .comment("Maximum uploaded datapack archive size in MB.")
                .defineInRange("maxUploadSizeMb", 64L, 1L, 512L);
        builder.pop();

        builder.push("hookMonster");
        HOOK_MONSTER_MAX_HEALTH = builder
                .comment("Maximum health of the hook monster.")
                .defineInRange("maxHealth", 80.0D, 10.0D, 1000.0D);
        HOOK_MONSTER_ARMOR = builder
                .comment("Armor value of the hook monster.")
                .defineInRange("armor", 6.0D, 0.0D, 30.0D);
        HOOK_MONSTER_MOVE_SPEED = builder
                .comment("Movement speed of the hook monster.")
                .defineInRange("moveSpeed", 0.25D, 0.05D, 1.0D);
        HOOK_MONSTER_EXCRETE_INTERVAL = builder
                .comment("Interval in ticks between excrement drops.")
                .defineInRange("excreteInterval", 200, 20, 72000);
        HOOK_MONSTER_HOOK_COOLDOWN = builder
                .comment("Cooldown in ticks between hook attacks.")
                .defineInRange("hookCooldown", 60, 10, 72000);
        HOOK_MONSTER_HOOK_MAX_RANGE = builder
                .comment("Maximum range of the hook attack in blocks.")
                .defineInRange("hookMaxRange", 16.0D, 1.0D, 64.0D);
        HOOK_MONSTER_PULL_STRENGTH = builder
                .comment("Pull strength multiplier applied to the hooked entity.")
                .defineInRange("pullStrength", 1.5D, 0.1D, 10.0D);
        HOOK_MONSTER_DAMAGE_PERCENT = builder
                .comment("Percentage of max health dealt as damage (0.0-1.0).")
                .defineInRange("damagePercent", 0.15D, 0.01D, 1.0D);
        HOOK_MONSTER_STUN_DURATION = builder
                .comment("Stun duration in ticks when hooking own excrement.")
                .defineInRange("stunDuration", 60, 10, 72000);
        HOOK_MONSTER_EXCREMENT_LIFETIME = builder
                .comment("Lifetime of excrement entities in ticks (explodes on expiry).")
                .defineInRange("excrementLifetime", 200, 20, 72000);
        HOOK_MONSTER_EXCREMENT_SCATTER_RADIUS = builder
                .comment("Scatter radius for excrement placement.")
                .defineInRange("excrementScatterRadius", 2.0D, 0.5D, 8.0D);
        HOOK_MONSTER_EXCREMENT_MAX_HEALTH = builder
                .comment("Maximum health of excrement entities.")
                .defineInRange("excrementMaxHealth", 10.0D, 1.0D, 100.0D);
        HOOK_MONSTER_EXCREMENT_EXPLOSION_POWER = builder
                .comment("Explosion radius when excrement expires without being destroyed.")
                .defineInRange("excrementExplosionPower", 4.0D, 2.0D, 16.0D);
        HOOK_MONSTER_EXCREMENT_EXPLOSION_DAMAGE = builder
                .comment("Damage dealt by excrement explosion in half-hearts.")
                .defineInRange("excrementExplosionDamage", 8.0D, 1.0D, 40.0D);
        HOOK_MONSTER_EXCREMENT_WEAKNESS_DURATION = builder
                .comment("Weakness effect duration after excrement explosion in ticks.")
                .defineInRange("excrementWeaknessDuration", 600, 20, 72000);
        HOOK_MONSTER_HOOK_FLIGHT_SPEED = builder
                .comment("Flight speed of the hook projectile in blocks per tick.")
                .defineInRange("hookFlightSpeed", 1.0D, 0.1D, 5.0D);
        builder.pop();

        SPEC = builder.build();
    }

    private ModServerConfig() {
    }

    // Datapack accessors
    public static boolean isDatapackUploadEnabled() {
        return ENABLE_DATAPACK_UPLOAD.get();
    }

    public static long getMaxUploadSizeBytes() {
        return MAX_UPLOAD_SIZE_MB.get() * 1024L * 1024L;
    }

    // Hook Monster accessors
    public static double hookMonsterMaxHealth() { return HOOK_MONSTER_MAX_HEALTH.get(); }
    public static double hookMonsterArmor() { return HOOK_MONSTER_ARMOR.get(); }
    public static double hookMonsterMoveSpeed() { return HOOK_MONSTER_MOVE_SPEED.get(); }
    public static int hookMonsterExcreteInterval() { return HOOK_MONSTER_EXCRETE_INTERVAL.get(); }
    public static int hookMonsterHookCooldown() { return HOOK_MONSTER_HOOK_COOLDOWN.get(); }
    public static double hookMonsterHookMaxRange() { return HOOK_MONSTER_HOOK_MAX_RANGE.get(); }
    public static double hookMonsterPullStrength() { return HOOK_MONSTER_PULL_STRENGTH.get(); }
    public static double hookMonsterDamagePercent() { return HOOK_MONSTER_DAMAGE_PERCENT.get(); }
    public static int hookMonsterStunDuration() { return HOOK_MONSTER_STUN_DURATION.get(); }
    public static int excrementLifetime() { return HOOK_MONSTER_EXCREMENT_LIFETIME.get(); }
    public static double excrementScatterRadius() { return HOOK_MONSTER_EXCREMENT_SCATTER_RADIUS.get(); }
    public static double excrementMaxHealth() { return HOOK_MONSTER_EXCREMENT_MAX_HEALTH.get(); }
    public static double excrementExplosionPower() { return HOOK_MONSTER_EXCREMENT_EXPLOSION_POWER.get(); }
    public static double excrementExplosionDamage() { return HOOK_MONSTER_EXCREMENT_EXPLOSION_DAMAGE.get(); }
    public static int excrementWeaknessDuration() { return HOOK_MONSTER_EXCREMENT_WEAKNESS_DURATION.get(); }
    public static double hookMonsterHookFlightSpeed() { return HOOK_MONSTER_HOOK_FLIGHT_SPEED.get(); }
}
