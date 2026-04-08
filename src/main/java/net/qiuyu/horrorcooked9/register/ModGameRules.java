package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public final class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_SHELTER9_SUPPORT = GameRules.register(
            "enableShelter9Support",
            GameRules.Category.MISC,
            GameRules.BooleanValue.create(false)
    );

    private ModGameRules() {
    }

    public static void bootstrap() {
        // Intentionally empty. Calling this method ensures class loading and gamerule registration.
    }

    public static boolean isShelter9SupportEnabled(Level level) {
        return level.getGameRules().getBoolean(ENABLE_SHELTER9_SUPPORT);
    }
}
