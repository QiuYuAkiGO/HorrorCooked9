package net.qiuyu.horrorcooked9.blocks.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoard;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ChoppingBoardRenderer implements BlockEntityRenderer<ChoppingBoardBlockEntity> {

    public ChoppingBoardRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull ChoppingBoardBlockEntity pBlockEntity, float pPartialTick,
                       @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer,
                       int pPackedLight, int pPackedOverlay) {
        ItemStack item = pBlockEntity.getPlacedItem();
        if (item.isEmpty()) return;

        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        Direction facing = pBlockEntity.getBlockState().getValue(ChoppingBoard.FACING);

        pPoseStack.pushPose();

        // 移到方块中心
        pPoseStack.translate(0.5, 0.2, 0.5);

        // 根据方块朝向旋转
        float yRot = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f; // NORTH
        };
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        // 倒置：绕Z轴旋转180度，让刀刃朝上、刀柄朝下
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180f));

        // 平放在砧板上：绕X轴旋转90度
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90f));

        // 缩放
        pPoseStack.scale(0.6f, 0.6f, 0.6f);

        // 获取光照
        BlockPos pos = pBlockEntity.getBlockPos().above();
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int light = LightTexture.pack(blockLight, skyLight);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light,
                OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, level, 0);

        pPoseStack.popPose();
    }
}
