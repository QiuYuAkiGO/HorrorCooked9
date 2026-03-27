package net.qiuyu.horrorcooked9.gameplay.stir;

/**
 * Stir 小游戏单轮判定结果。
 */
public enum StirResult {
    GREEN,
    YELLOW,
    RED;

    public static StirResult fromOrdinal(int ordinal) {
        StirResult[] values = values();
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        return RED;
    }
}
