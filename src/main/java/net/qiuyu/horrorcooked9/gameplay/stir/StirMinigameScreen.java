package net.qiuyu.horrorcooked9.gameplay.stir;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.qiuyu.horrorcooked9.network.ModNetworking;
import net.qiuyu.horrorcooked9.network.gameplay.StirResultPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stir 转盘小游戏：
 * 玩家在指针固定位置点击左键，连续完成多轮判定后统一提交到服务端。
 */
public class    StirMinigameScreen extends Screen {

    private static final int OUTER_RADIUS = 64;
    private static final int INNER_RADIUS = 18;
    private static final float ANGULAR_SPEED = 5.0F; // degree / tick
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final float MAX_FRAME_DELTA_SECONDS = 0.05F;
    private static final float POINTER_ANGLE = 270.0F; // 12点方向

    private final BlockPos bowlPos;
    private final int requiredStirCount;
    private final List<StirResult> segmentResults = new ArrayList<>();
    private final List<Float> segmentRatios = new ArrayList<>();
    private final List<StirResult> roundResults = new ArrayList<>();
    private final RandomSource random = RandomSource.create();

    private float currentAngle = 0.0F;
    private boolean submitted = false;
    private long lastAngleUpdateNanos = 0L;

    public StirMinigameScreen(BlockPos bowlPos, int requiredStirCount) {
        super(Component.literal("Stir Minigame"));
        this.bowlPos = bowlPos;
        this.requiredStirCount = Math.max(1, requiredStirCount);
        resetWheelSegments();
        this.currentAngle = random.nextFloat() * 360.0F;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !submitted) {
            StirResult result = getResultAtPointer();
            roundResults.add(result);
            if (roundResults.size() >= requiredStirCount) {
                submitted = true;
                ModNetworking.CHANNEL.sendToServer(new StirResultPacket(bowlPos, roundResults));
                this.onClose();
            } else {
                resetWheelSegments();
                currentAngle = random.nextFloat() * 360.0F;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void resetWheelSegments() {
        segmentResults.clear();
        segmentRatios.clear();
        segmentResults.add(StirResult.RED);
        segmentRatios.add(0.6F);
        segmentResults.add(StirResult.YELLOW);
        segmentRatios.add(0.3F);
        segmentResults.add(StirResult.GREEN);
        segmentRatios.add(0.1F);

        for (int i = segmentResults.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(segmentResults, i, j);
            Collections.swap(segmentRatios, i, j);
        }
    }

    private StirResult getResultAtPointer() {
        float localAngle = normalizeAngle(POINTER_ANGLE - currentAngle);
        return getResultForAngle(localAngle);
    }

    private StirResult getResultForAngle(float angle) {
        float normalized = normalizeAngle(angle);
        float acc = 0.0F;
        for (int i = 0; i < segmentResults.size(); i++) {
            acc += segmentRatios.get(i) * 360.0F;
            if (normalized < acc) {
                return segmentResults.get(i);
            }
        }
        return segmentResults.get(segmentResults.size() - 1);
    }

    private static float normalizeAngle(float angle) {
        float normalized = angle % 360.0F;
        if (normalized < 0.0F) {
            normalized += 360.0F;
        }
        return normalized;
    }

    private static int colorFor(StirResult result) {
        return switch (result) {
            case GREEN -> 0xFF1FD655;
            case YELLOW -> 0xFFE6D83A;
            case RED -> 0xFFE04949;
        };
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateAngleByRenderTime();
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int cx = this.width / 2;
        int cy = this.height / 2 + 10;

        // 外边框
        guiGraphics.fill(cx - OUTER_RADIUS - 2, cy - OUTER_RADIUS - 2, cx + OUTER_RADIUS + 2, cy + OUTER_RADIUS + 2, 0xAA000000);

        // 用极坐标采样填充转盘
        for (int angle = 0; angle < 360; angle++) {
            float localAngle = normalizeAngle(angle - currentAngle);
            StirResult segment = getResultForAngle(localAngle);
            int color = colorFor(segment);
            double rad = Math.toRadians(angle);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            for (int r = INNER_RADIUS; r <= OUTER_RADIUS; r++) {
                int x = cx + (int) Math.round(cos * r);
                int y = cy + (int) Math.round(sin * r);
                guiGraphics.fill(x, y, x + 1, y + 1, color);
            }
        }

        // 固定指针（上方）
        int px = cx;
        int py = cy - OUTER_RADIUS - 10;
        guiGraphics.fill(px - 3, py, px + 3, py + 8, 0xFFFFFFFF);
        guiGraphics.fill(px - 1, py + 8, px + 1, py + 12, 0xFFFFFFFF);

        // 中心点
        guiGraphics.fill(cx - 3, cy - 3, cx + 3, cy + 3, 0xFFFFFFFF);

        int round = Math.min(roundResults.size() + 1, requiredStirCount);
        Component title = Component.literal("搅拌时机判定");
        Component roundText = Component.literal("轮次: " + round + "/" + requiredStirCount);
        Component hint = Component.literal("在指针命中绿色区域时点击左键");

        guiGraphics.drawString(this.font, title, cx - this.font.width(title) / 2, cy - OUTER_RADIUS - 32, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, roundText, cx - this.font.width(roundText) / 2, cy - OUTER_RADIUS - 20, 0xFFE0E0E0);
        guiGraphics.drawString(this.font, hint, cx - this.font.width(hint) / 2, cy + OUTER_RADIUS + 10, 0xFFE0E0E0);
    }

    private void updateAngleByRenderTime() {
        if (submitted) {
            return;
        }

        long nowNanos = Util.getNanos();
        if (lastAngleUpdateNanos == 0L) {
            lastAngleUpdateNanos = nowNanos;
            return;
        }

        float deltaSeconds = Math.min((nowNanos - lastAngleUpdateNanos) / 1_000_000_000.0F, MAX_FRAME_DELTA_SECONDS);
        lastAngleUpdateNanos = nowNanos;
        currentAngle = normalizeAngle(currentAngle + ANGULAR_SPEED * TICKS_PER_SECOND * deltaSeconds);
    }
}
