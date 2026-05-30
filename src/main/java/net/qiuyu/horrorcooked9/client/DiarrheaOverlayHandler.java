package net.qiuyu.horrorcooked9.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModEffects;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DiarrheaOverlayHandler {
    private static final int SHAKE_DURATION_TICKS = 20;
    private static final int MAX_OVERLAY_ALPHA = 180;
    private static final int BASE_OVERLAY_ALPHA = 90;
    private static final int AMPLIFIER_ALPHA_STEP = 20;
    private static final float YAW_WAVE_SPEED = 1.8F;
    private static final float PITCH_WAVE_SPEED = 2.2F;
    private static final float YAW_SHAKE_SCALE = 2.5F;
    private static final float PITCH_SHAKE_SCALE = 1.6F;

    private static int shakeTicks = 0;
    private static boolean wasDiarrheaProcActive = false;

    private DiarrheaOverlayHandler() {
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        if (!minecraft.player.hasEffect(ModEffects.DIARRHEA.get())) {
            return;
        }
        MobEffectInstance slowness = minecraft.player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        MobEffectInstance fatigue = minecraft.player.getEffect(MobEffects.DIG_SLOWDOWN);
        boolean diarrheaProcActive = slowness != null && slowness.getAmplifier() >= 6;
        if (diarrheaProcActive && !wasDiarrheaProcActive) {
            shakeTicks = SHAKE_DURATION_TICKS;
        }
        wasDiarrheaProcActive = diarrheaProcActive;
        if (slowness == null && fatigue == null) {
            return;
        }

        int amplifier = minecraft.player.getEffect(ModEffects.DIARRHEA.get()).getAmplifier();
        int alpha = Math.min(MAX_OVERLAY_ALPHA, BASE_OVERLAY_ALPHA + amplifier * AMPLIFIER_ALPHA_STEP);
        int color = (alpha << 24);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        guiGraphics.fill(0, 0, width, height, color);
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeTicks <= 0) {
            return;
        }

        float progress = shakeTicks / (float) SHAKE_DURATION_TICKS;
        float tickTime = (float) (SHAKE_DURATION_TICKS - shakeTicks + event.getPartialTick());
        float wave = Mth.sin(tickTime * YAW_WAVE_SPEED);
        float yawOffset = wave * YAW_SHAKE_SCALE * progress;
        float pitchOffset = Mth.cos(tickTime * PITCH_WAVE_SPEED) * PITCH_SHAKE_SCALE * progress;

        event.setYaw(event.getYaw() + yawOffset);
        event.setPitch(event.getPitch() + pitchOffset);
        shakeTicks--;
    }
}
