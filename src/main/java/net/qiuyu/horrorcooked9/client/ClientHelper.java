package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * 客户端辅助类，用于在客户端打开切割小游戏界面。
 */
public class ClientHelper {

    public static void openChopMinigame(BlockPos pos) {
        Minecraft.getInstance().setScreen(new ChopMinigameScreen(pos));
    }
}
