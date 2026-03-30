package net.qiuyu.horrorcooked9.gameplay.chopping;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.network.gameplay.ChopResultPacket;
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
    private static final float DEFAULT_CURSOR_SPEED = 0.015F;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final float MAX_FRAME_DELTA_SECONDS = 0.05F;

    private final BlockPos boardPos;
    private final boolean hasRecipeConfig;

    // 进度条尺寸
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 20;

    // 各颜色段及其占比（红:黄:绿 = 6:3:1，总计10份）
    private final List<ChopResult> segmentColors;
    private final List<Float> segmentWidths; // 每段宽度占比(0~1)

    // 光标状态
    private float cursorPos = 0.0f; // 0~1
    private float cursorSpeed = DEFAULT_CURSOR_SPEED;
    private int cursorDirection = 1; // 1=向右, -1=向左
    private boolean stopped = false;
    private long lastCursorUpdateNanos = 0L;

    public ChopMinigameScreen(BlockPos boardPos) {
        super(Component.literal("切割小游戏"));
        this.boardPos = boardPos;
        this.segmentColors = new ArrayList<>();
        this.segmentWidths = new ArrayList<>();
        this.hasRecipeConfig = initConfigFromRecipe();
    }

    private boolean initConfigFromRecipe() {
        segmentColors.clear();
        segmentWidths.clear();
        float redRatio = 0.6F;
        float yellowRatio = 0.3F;
        float greenRatio = 0.1F;
        boolean foundRecipe = false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            BlockEntity be = mc.level.getBlockEntity(boardPos);
            if (be instanceof ChoppingBoardBlockEntity boardEntity && boardEntity.hasPlacedItem()) {
                ChopperMinigameRecipe recipe = ChopperRecipeMatcher.findByInput(boardEntity.getPlacedItem(), mc.level);
                if (recipe != null) {
                    foundRecipe = true;
                    cursorSpeed = recipe.getCursorSpeed();
                    redRatio = recipe.getRedRatio();
                    yellowRatio = recipe.getYellowRatio();
                    greenRatio = recipe.getGreenRatio();
                }
            }
        }

        segmentColors.add(ChopResult.RED);
        segmentWidths.add(redRatio);
        segmentColors.add(ChopResult.YELLOW);
        segmentWidths.add(yellowRatio);
        segmentColors.add(ChopResult.GREEN);
        segmentWidths.add(greenRatio);

        Random rng = new Random();
        for (int i = segmentColors.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Collections.swap(segmentColors, i, j);
            Collections.swap(segmentWidths, i, j);
        }
        return foundRecipe;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !stopped) {
            if (!hasRecipeConfig) {
                this.onClose();
                return true;
            }
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
        updateCursorByRenderTime();

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
        Component hint = hasRecipeConfig
                ? Component.literal("点击左键停止光标!")
                : Component.literal("该食材暂无切菜配方");
        int textWidth = this.font.width(hint);
        guiGraphics.drawString(this.font, hint, centerX - textWidth / 2, barTop - 16, 0xFFFFFFFF);
    }

    private void updateCursorByRenderTime() {
        if (stopped) {
            return;
        }

        long nowNanos = Util.getNanos();
        if (lastCursorUpdateNanos == 0L) {
            lastCursorUpdateNanos = nowNanos;
            return;
        }

        float deltaSeconds = Math.min((nowNanos - lastCursorUpdateNanos) / 1_000_000_000.0F, MAX_FRAME_DELTA_SECONDS);
        lastCursorUpdateNanos = nowNanos;

        cursorPos += cursorSpeed * TICKS_PER_SECOND * cursorDirection * deltaSeconds;
        if (cursorPos >= 1.0f) {
            cursorPos = 1.0f;
            cursorDirection = -1;
        } else if (cursorPos <= 0.0f) {
            cursorPos = 0.0f;
            cursorDirection = 1;
        }
    }
}
