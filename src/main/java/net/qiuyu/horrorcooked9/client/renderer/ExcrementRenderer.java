package net.qiuyu.horrorcooked9.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.qiuyu.horrorcooked9.entity.custom.ExcrementEntity;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;

public class ExcrementRenderer extends EntityRenderer<ExcrementEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public ExcrementRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.25F;
    }

    @Override
    public void render(ExcrementEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.25, 0.0, -0.25);

        // Scale down as lifetime decreases (shrinking animation)
        float lifeFraction = (float) entity.getLifetime() / Math.max(1, entity.getMaxLifetime());
        float scale = Math.max(0.2F, lifeFraction);
        poseStack.scale(scale, scale, scale);

        // Random rotation based on entity ID
        float rotation = (entity.getId() * 7) % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        this.blockRenderer.renderSingleBlock(
                Blocks.DIRT.defaultBlockState(),
                poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ExcrementEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
