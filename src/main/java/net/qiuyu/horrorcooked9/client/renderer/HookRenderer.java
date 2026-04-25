package net.qiuyu.horrorcooked9.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.entity.custom.HookEntity;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HookRenderer extends EntityRenderer<HookEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(HorrorCooked9.MODID, "textures/entity/hook.png");

    public HookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HookEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Render hook projectile as a small diamond shape
        poseStack.pushPose();
        poseStack.translate(0, 0.125, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        float size = 0.15F;
        // Simple diamond/quad render
        consumer.vertex(matrix, -size, -size, 0).color(0x4A, 0x4A, 0x4A, 0xFF).uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
        consumer.vertex(matrix, size, -size, 0).color(0x4A, 0x4A, 0x4A, 0xFF).uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
        consumer.vertex(matrix, size, size, 0).color(0x4A, 0x4A, 0x4A, 0xFF).uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
        consumer.vertex(matrix, -size, size, 0).color(0x4A, 0x4A, 0x4A, 0xFF).uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 0, 1).endVertex();

        poseStack.popPose();

        // Render chain between monster and hook
        Entity owner = entity.getOwner();
        if (owner instanceof HookMonsterEntity monster) {
            renderChain(monster, entity, partialTick, poseStack, bufferSource, packedLight);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderChain(HookMonsterEntity monster, HookEntity hook, float partialTick,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        Vec3 monsterPos = monster.getPosition(partialTick);
        Vec3 hookPos = hook.getPosition(partialTick);

        float dx = (float) (hookPos.x - monsterPos.x);
        float dy = (float) (hookPos.y - monsterPos.y + monster.getEyeHeight() - 0.4);
        float dz = (float) (hookPos.z - monsterPos.z);
        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        // Segmented bezier chain effect
        int segments = (int) (dist * 4);
        if (segments < 4) segments = 4;

        Vec3 start = monsterPos.add(0, monster.getEyeHeight() - 0.4, 0);
        Vec3 control = start.add(hookPos.subtract(start).scale(0.5)).add(0, -dist * 0.2, 0);

        Vec3 prev = start;
        for (int i = 1; i <= segments; i++) {
            float t = i / (float) segments;
            // Quadratic bezier
            float u = 1 - t;
            Vec3 point = start.scale(u * u)
                    .add(control.scale(2 * u * t))
                    .add(hookPos.scale(t * t));

            // Draw line segment in world space
            float r1 = (float) (prev.x - hookPos.x);
            float r2 = (float) (prev.y - hookPos.y + 0.125);
            float r3 = (float) (prev.z - hookPos.z);

            consumer.vertex(matrix, r1, r2, r3)
                    .color(0x60, 0x40, 0x30, 0xE0)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, r1 + (float) (point.x - prev.x), r2 + (float) (point.y - prev.y),
                            r3 + (float) (point.z - prev.z))
                    .color(0x60, 0x40, 0x30, 0xE0)
                    .normal(normal, 0, 1, 0).endVertex();

            prev = point;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(HookEntity entity) {
        return TEXTURE;
    }
}
