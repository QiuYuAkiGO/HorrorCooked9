package net.qiuyu.horrorcooked9.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.qiuyu.horrorcooked9.client.model.HookMonsterModel;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HookMonsterRenderer extends GeoEntityRenderer<HookMonsterEntity> {

    public HookMonsterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HookMonsterModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public void render(HookMonsterEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.isStunned()) {
            // Slight shake effect while stunned
            float shake = (float) Math.sin(entity.tickCount * 0.5) * 0.1F;
            poseStack.translate(shake, 0, 0);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
