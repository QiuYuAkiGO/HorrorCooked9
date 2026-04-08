package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.qiuyu.horrorcooked9.client.screen.ChopMinigameScreen;
import net.qiuyu.horrorcooked9.client.screen.StirMinigameScreen;

/**
 * 客户端辅助类，用于在客户端打开切割小游戏界面。
 */
public class ClientHelper {

    public static void openChopMinigame(BlockPos pos) {
        Minecraft.getInstance().setScreen(new ChopMinigameScreen(pos));
    }

    public static void openStirMinigame(BlockPos pos, int requiredStirCount) {
        Minecraft.getInstance().setScreen(new StirMinigameScreen(pos, requiredStirCount));
    }
}
