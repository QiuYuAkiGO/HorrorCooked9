package net.qiuyu.horrorcooked9.gameplay.chopping;

/**
 * 切割小游戏的结果枚举。
 * GREEN = 大成功, YELLOW = 成功, RED = 失败
 */
public enum ChopResult {
    GREEN,
    YELLOW,
    RED;

    public static ChopResult fromOrdinal(int ordinal) {
        ChopResult[] values = values();
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        return RED;
    }
}
