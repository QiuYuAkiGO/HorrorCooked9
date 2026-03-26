package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.qiuyu.horrorcooked9.items.ChopResult;
import net.qiuyu.horrorcooked9.network.gameplay.ChopResultPacket;
import net.qiuyu.horrorcooked9.register.ModNetworking;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 切割小游戏界面：进度条包含红色、黄色、绿色三个区域，比例为 6:3:1（红:黄:绿），
 * 位置随机排列，光标来回滑动，玩家点击左键停止光标并返回对应的 ChopResult。
 */
public class ChopMinigameScreen extends Screen {

    private final BlockPos boardPos;

    // 进度条尺寸
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 20;

    // 各颜色段及其占比（红:黄:绿 = 6:3:1，总计10份）
    private final List<ChopResult> segmentColors;
    private final List<Float> segmentWidths; // 每段宽度占比(0~1)

    // 光标状态
    private float cursorPos = 0.0f; // 0~1
    private final float cursorSpeed = 0.015f;
    private int cursorDirection = 1; // 1=向右, -1=向左
    private boolean stopped = false;

    public ChopMinigameScreen(BlockPos boardPos) {
        super(Component.literal("切割小游戏"));
        this.boardPos = boardPos;

        // 红:黄:绿 = 6:3:1
        segmentColors = new ArrayList<>();
        segmentWidths = new ArrayList<>();
        segmentColors.add(ChopResult.RED);    segmentWidths.add(0.6f);
        segmentColors.add(ChopResult.YELLOW); segmentWidths.add(0.3f);
        segmentColors.add(ChopResult.GREEN);  segmentWidths.add(0.1f);

        // 随机排列位置（颜色和宽度一起打乱）
        Random rng = new Random();
        for (int i = segmentColors.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Collections.swap(segmentColors, i, j);
            Collections.swap(segmentWidths, i, j);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!stopped) {
            cursorPos += cursorSpeed * cursorDirection;
            if (cursorPos >= 1.0f) {
                cursorPos = 1.0f;
                cursorDirection = -1;
            } else if (cursorPos <= 0.0f) {
                cursorPos = 0.0f;
                cursorDirection = 1;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !stopped) {
            stopped = true;
            ChopResult result = getResultFromCursor();
            // 发送结果到服务端
            ModNetworking.CHANNEL.sendToServer(new ChopResultPacket(boardPos, result));
            // 关闭界面
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private ChopResult getResultFromCursor() {
        float acc = 0;
        for (int i = 0; i < segmentColors.size(); i++) {
            acc += segmentWidths.get(i);
            if (cursorPos < acc) return segmentColors.get(i);
        }
        return segmentColors.get(segmentColors.size() - 1);
    }

    private int getColorForResult(ChopResult result) {
        return switch (result) {
            case GREEN -> 0xFF00FF00;
            case YELLOW -> 0xFFFFFF00;
            case RED -> 0xFFFF0000;
        };
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 不绘制背景暗化
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int barLeft = centerX - BAR_WIDTH / 2;
        int barTop = centerY - BAR_HEIGHT / 2;

        // 绘制进度条背景（黑色边框）
        guiGraphics.fill(barLeft - 2, barTop - 2, barLeft + BAR_WIDTH + 2, barTop + BAR_HEIGHT + 2, 0xFF000000);

        // 绘制三个颜色区域（比例 6:3:1）
        float offset = 0;
        for (int i = 0; i < segmentColors.size(); i++) {
            int left = barLeft + (int)(BAR_WIDTH * offset);
            offset += segmentWidths.get(i);
            int right = (i == segmentColors.size() - 1) ? barLeft + BAR_WIDTH : barLeft + (int)(BAR_WIDTH * offset);
            guiGraphics.fill(left, barTop, right, barTop + BAR_HEIGHT, getColorForResult(segmentColors.get(i)));
        }

        // 绘制光标（白色竖线）
        int cursorX = barLeft + (int)(BAR_WIDTH * cursorPos);
        guiGraphics.fill(cursorX - 1, barTop - 4, cursorX + 1, barTop + BAR_HEIGHT + 4, 0xFFFFFFFF);

        // 绘制提示文字
        Component hint = Component.literal("点击左键停止光标!");
        int textWidth = this.font.width(hint);
        guiGraphics.drawString(this.font, hint, centerX - textWidth / 2, barTop - 16, 0xFFFFFFFF);
    }
}
