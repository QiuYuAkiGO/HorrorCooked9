package net.qiuyu.horrorcooked9.gameplay.stir;

import net.minecraft.util.Mth;

public record StirToolModifier(float successChanceDelta, int stirCountDelta) {
    public static final StirToolModifier ZERO = new StirToolModifier(0.0F, 0);

    public float applySuccessChance(float baseChance) {
        return Mth.clamp(baseChance + successChanceDelta, 0.0F, 1.0F);
    }

    public int applyStirCount(int baseStirCount) {
        return Math.max(1, baseStirCount + stirCountDelta);
    }
}
