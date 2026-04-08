package net.qiuyu.horrorcooked9.gameplay.stir;

import net.minecraft.util.Mth;

/**
 * 搅拌工具对配方参数的增量修正。
 *
 * @param successChanceDelta 对基础成功率的加减值（最终会被钳制到 0~1）
 * @param stirCountDelta     对基础搅拌轮次的加减值（最终至少为 1）
 */
public record StirToolModifier(float successChanceDelta, int stirCountDelta) {
    public static final StirToolModifier ZERO = new StirToolModifier(0.0F, 0);

    /**
     * 应用成功率修正并限制在有效区间。
     */
    public float applySuccessChance(float baseChance) {
        return Mth.clamp(baseChance + successChanceDelta, 0.0F, 1.0F);
    }

    /**
     * 应用轮次修正并保证至少执行 1 轮。
     */
    public int applyStirCount(int baseStirCount) {
        return Math.max(1, baseStirCount + stirCountDelta);
    }
}
