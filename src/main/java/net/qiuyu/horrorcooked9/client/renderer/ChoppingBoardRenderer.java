package net.qiuyu.horrorcooked9.client.renderer;

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
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlock;
import net.qiuyu.horrorcooked9.blocks.custom.ChoppingBoardBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ChoppingBoardRenderer implements BlockEntityRenderer<ChoppingBoardBlockEntity> {
    private static final float ITEM_TRANSLATE_XZ = 0.5F;
    private static final float ITEM_TRANSLATE_Y = 0.2F;
    private static final float ITEM_ROTATE_Z = 180.0F;
    private static final float ITEM_ROTATE_X = 90.0F;
    private static final float ITEM_SCALE = 0.6F;

    private static final float FACING_ROTATE_SOUTH = 180.0F;
    private static final float FACING_ROTATE_WEST = 90.0F;
    private static final float FACING_ROTATE_EAST = -90.0F;

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

        Direction facing = pBlockEntity.getBlockState().getValue(ChoppingBoardBlock.FACING);

        pPoseStack.pushPose();

        pPoseStack.translate(ITEM_TRANSLATE_XZ, ITEM_TRANSLATE_Y, ITEM_TRANSLATE_XZ);

        float yRot = switch (facing) {
            case SOUTH -> FACING_ROTATE_SOUTH;
            case WEST -> FACING_ROTATE_WEST;
            case EAST -> FACING_ROTATE_EAST;
            default -> 0F; // NORTH
        };
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        pPoseStack.mulPose(Axis.ZP.rotationDegrees(ITEM_ROTATE_Z));

        pPoseStack.mulPose(Axis.XP.rotationDegrees(ITEM_ROTATE_X));

        pPoseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

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
