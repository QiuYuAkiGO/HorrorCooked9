package net.qiuyu.horrorcooked9.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HookMonsterRuntimeConfig {
    public static final String MAX_HEALTH = "maxHealth";
    public static final String ARMOR = "armor";
    public static final String MOVE_SPEED = "moveSpeed";
    public static final String EXCRETE_INTERVAL = "excreteInterval";
    public static final String HOOK_COOLDOWN = "hookCooldown";
    public static final String HOOK_MAX_RANGE = "hookMaxRange";
    public static final String PULL_STRENGTH = "pullStrength";
    public static final String DAMAGE_PERCENT = "damagePercent";
    public static final String STUN_DURATION = "stunDuration";
    public static final String EXCREMENT_LIFETIME = "excrementLifetime";
    public static final String EXCREMENT_SCATTER_RADIUS = "excrementScatterRadius";
    public static final String EXCREMENT_MAX_HEALTH = "excrementMaxHealth";
    public static final String EXCREMENT_EXPLOSION_POWER = "excrementExplosionPower";
    public static final String EXCREMENT_EXPLOSION_DAMAGE = "excrementExplosionDamage";
    public static final String EXCREMENT_WEAKNESS_DURATION = "excrementWeaknessDuration";
    public static final String HOOK_FLIGHT_SPEED = "hookFlightSpeed";

    public static final List<String> KEYS = List.of(
            MAX_HEALTH, ARMOR, MOVE_SPEED,
            EXCRETE_INTERVAL, HOOK_COOLDOWN, HOOK_MAX_RANGE,
            PULL_STRENGTH, DAMAGE_PERCENT, STUN_DURATION,
            EXCREMENT_LIFETIME, EXCREMENT_SCATTER_RADIUS,
            EXCREMENT_MAX_HEALTH, EXCREMENT_EXPLOSION_POWER,
            EXCREMENT_EXPLOSION_DAMAGE, EXCREMENT_WEAKNESS_DURATION,
            HOOK_FLIGHT_SPEED
    );

    private static final Map<String, Double> RUNTIME_OVERRIDES = new HashMap<>();

    private HookMonsterRuntimeConfig() {
    }

    public static boolean isKnownKey(String key) {
        return KEYS.contains(key);
    }

    public static boolean hasRuntimeOverride(String key) {
        return RUNTIME_OVERRIDES.containsKey(key);
    }

    public static void setRuntimeOverride(String key, double value) {
        if (!isKnownKey(key)) {
            throw new IllegalArgumentException("Unknown hook monster config key: " + key);
        }
        if (!isValueInRange(key, value)) {
            throw new IllegalArgumentException("Hook monster config value out of range: " + key + "=" + value);
        }
        RUNTIME_OVERRIDES.put(key, value);
    }

    public static boolean isValueInRange(String key, double value) {
        return value >= getMinValue(key) && value <= getMaxValue(key);
    }

    public static int resetRuntimeOverrides() {
        int count = RUNTIME_OVERRIDES.size();
        RUNTIME_OVERRIDES.clear();
        return count;
    }

    public static double getDouble(String key) {
        return RUNTIME_OVERRIDES.getOrDefault(key, getConfigDefault(key));
    }

    public static int getInt(String key) {
        return (int) Math.round(getDouble(key));
    }

    private static double getConfigDefault(String key) {
        return switch (key) {
            case MAX_HEALTH -> ModServerConfig.hookMonsterMaxHealth();
            case ARMOR -> ModServerConfig.hookMonsterArmor();
            case MOVE_SPEED -> ModServerConfig.hookMonsterMoveSpeed();
            case EXCRETE_INTERVAL -> ModServerConfig.hookMonsterExcreteInterval();
            case HOOK_COOLDOWN -> ModServerConfig.hookMonsterHookCooldown();
            case HOOK_MAX_RANGE -> ModServerConfig.hookMonsterHookMaxRange();
            case PULL_STRENGTH -> ModServerConfig.hookMonsterPullStrength();
            case DAMAGE_PERCENT -> ModServerConfig.hookMonsterDamagePercent();
            case STUN_DURATION -> ModServerConfig.hookMonsterStunDuration();
            case EXCREMENT_LIFETIME -> ModServerConfig.excrementLifetime();
            case EXCREMENT_SCATTER_RADIUS -> ModServerConfig.excrementScatterRadius();
            case EXCREMENT_MAX_HEALTH -> ModServerConfig.excrementMaxHealth();
            case EXCREMENT_EXPLOSION_POWER -> ModServerConfig.excrementExplosionPower();
            case EXCREMENT_EXPLOSION_DAMAGE -> ModServerConfig.excrementExplosionDamage();
            case EXCREMENT_WEAKNESS_DURATION -> ModServerConfig.excrementWeaknessDuration();
            case HOOK_FLIGHT_SPEED -> ModServerConfig.hookMonsterHookFlightSpeed();
            default -> throw new IllegalArgumentException("Unknown hook monster config key: " + key);
        };
    }

    private static double getMinValue(String key) {
        return switch (key) {
            case MAX_HEALTH -> 10.0D;
            case ARMOR -> 0.0D;
            case MOVE_SPEED -> 0.05D;
            case EXCRETE_INTERVAL -> 20.0D;
            case HOOK_COOLDOWN -> 10.0D;
            case HOOK_MAX_RANGE -> 1.0D;
            case PULL_STRENGTH -> 0.1D;
            case DAMAGE_PERCENT -> 0.01D;
            case STUN_DURATION -> 10.0D;
            case EXCREMENT_LIFETIME -> 20.0D;
            case EXCREMENT_SCATTER_RADIUS -> 0.5D;
            case EXCREMENT_MAX_HEALTH -> 1.0D;
            case EXCREMENT_EXPLOSION_POWER -> 2.0D;
            case EXCREMENT_EXPLOSION_DAMAGE -> 1.0D;
            case EXCREMENT_WEAKNESS_DURATION -> 20.0D;
            case HOOK_FLIGHT_SPEED -> 0.1D;
            default -> throw new IllegalArgumentException("Unknown hook monster config key: " + key);
        };
    }

    private static double getMaxValue(String key) {
        return switch (key) {
            case MAX_HEALTH -> 1000.0D;
            case ARMOR -> 30.0D;
            case MOVE_SPEED -> 1.0D;
            case EXCRETE_INTERVAL, HOOK_COOLDOWN, STUN_DURATION, EXCREMENT_LIFETIME, EXCREMENT_WEAKNESS_DURATION -> 72000.0D;
            case HOOK_MAX_RANGE -> 64.0D;
            case PULL_STRENGTH -> 10.0D;
            case DAMAGE_PERCENT -> 1.0D;
            case EXCREMENT_SCATTER_RADIUS -> 8.0D;
            case EXCREMENT_MAX_HEALTH -> 100.0D;
            case EXCREMENT_EXPLOSION_POWER -> 16.0D;
            case EXCREMENT_EXPLOSION_DAMAGE -> 40.0D;
            case HOOK_FLIGHT_SPEED -> 5.0D;
            default -> throw new IllegalArgumentException("Unknown hook monster config key: " + key);
        };
    }
}
