package net.qiuyu.horrorcooked9.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class SaladBowlRenderer implements BlockEntityRenderer<SaladBowlBlockEntity> {

    private static final float INGREDIENT_BASE_HEIGHT = 0.11f;
    private static final float INGREDIENT_HEIGHT_STEP = 0.0015f;
    private static final float INGREDIENT_ICON_SCALE = 0.36f;

    private static final float LIQUID_MAX_HEIGHT = 0.20f;
    private static final float LIQUID_MIN_HEIGHT = 0.04f;
    private static final float LIQUID_APOTHEM = 0.25f;
    private static final int OCTAGON_SIDES = 8;
    private static final int LIQUID_ALPHA = 180;
    private static final int DEFAULT_LIQUID_COLOR = 0x8B6914;

    private static final RenderType LIQUID_RENDER_TYPE = LiquidRenderHelper.SALAD_LIQUID;

    public SaladBowlRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull SaladBowlBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int light = computeLight(level, blockEntity.getBlockPos().above());

        if (blockEntity.isCompleted()) {
            renderCompletedState(blockEntity, partialTick, poseStack, buffer, itemRenderer, level, light);
            return;
        }

        renderIngredients(blockEntity.getAddedIngredients(), blockEntity.getBlockPos(),
                poseStack, buffer, itemRenderer, level, light, false);
    }

    private void renderCompletedState(SaladBowlBlockEntity blockEntity, float partialTick,
                                      PoseStack poseStack, MultiBufferSource buffer,
                                      ItemRenderer itemRenderer, Level level, int light) {
        List<ItemStack> ingredients = blockEntity.getAddedIngredients();

        int initial = blockEntity.getInitialServings();
        int remaining = blockEntity.getRemainingServings();
        float ratio = initial > 0 ? Mth.clamp((float) remaining / initial, 0f, 1f) : 1f;
        float liquidHeight = Mth.lerp(ratio, LIQUID_MIN_HEIGHT, LIQUID_MAX_HEIGHT);

        int liquidColor = computeLiquidColorForCompleted(blockEntity, ingredients);

        renderLiquidDisc(blockEntity, partialTick, poseStack, buffer, light, liquidHeight, liquidColor);

        renderIngredients(ingredients, blockEntity.getBlockPos(), poseStack, buffer,
                itemRenderer, level, light, true);
    }

    private void renderIngredients(List<ItemStack> ingredients, BlockPos pos, PoseStack poseStack,
                                   MultiBufferSource buffer, ItemRenderer itemRenderer,
                                   Level level, int light, boolean shuffled) {
        if (ingredients.isEmpty()) return;

        long posSeed = pos.asLong();

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) continue;

            float offsetX, offsetZ;
            if (shuffled) {
                long seed = posSeed * 31L + i * 7919L;
                float angle = pseudoRandomFloat(seed, 0, Mth.TWO_PI);
                float radius = pseudoRandomFloat(seed + 13, 0.02f, 0.14f);
                offsetX = Mth.cos(angle) * radius;
                offsetZ = Mth.sin(angle) * radius;
            } else {
                float angleRad = Mth.DEG_TO_RAD * (137.5f * i);
                float radius = Math.min(0.16f, 0.03f + 0.012f * i);
                offsetX = Mth.cos(angleRad) * radius;
                offsetZ = Mth.sin(angleRad) * radius;
            }

            float y = INGREDIENT_BASE_HEIGHT + INGREDIENT_HEIGHT_STEP * i;
            if (shuffled) y += 0.04f;

            poseStack.pushPose();
            poseStack.translate(0.5f + offsetX, y, 0.5f + offsetZ);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(INGREDIENT_ICON_SCALE, INGREDIENT_ICON_SCALE, INGREDIENT_ICON_SCALE);

            itemRenderer.renderStatic(ingredient, ItemDisplayContext.FIXED, light,
                    OverlayTexture.NO_OVERLAY, poseStack, buffer, level, i);
            poseStack.popPose();
        }
    }

    private void renderLiquidDisc(SaladBowlBlockEntity blockEntity, float partialTick,
                                  PoseStack poseStack, MultiBufferSource buffer,
                                  int light, float baseHeight, int mixedColor) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        float time = (level.getGameTime() + partialTick) * 0.08f;
        long posSeed = blockEntity.getBlockPos().asLong();
        float phaseOffset = (posSeed & 0xFF) * 0.1f;

        int r = (mixedColor >> 16) & 0xFF;
        int g = (mixedColor >> 8) & 0xFF;
        int b = mixedColor & 0xFF;

        VertexConsumer consumer = buffer.getBuffer(LIQUID_RENDER_TYPE);
        Matrix4f pose = poseStack.last().pose();

        float vertexRadius = LIQUID_APOTHEM / Mth.cos(Mth.PI / OCTAGON_SIDES);
        float centerWobble = Mth.sin(time + phaseOffset) * 0.003f;
        float angleStep = Mth.TWO_PI / OCTAGON_SIDES;
        float angleOffset = Mth.PI / OCTAGON_SIDES;

        for (int i = 0; i < OCTAGON_SIDES; i++) {
            float a1 = angleStep * i + angleOffset;
            float a2 = angleStep * (i + 1) + angleOffset;
            float w1 = Mth.sin(time * (1.0f + i * 0.05f) + phaseOffset + i) * 0.004f;
            float w2 = Mth.sin(time * (1.0f + (i + 1) * 0.05f) + phaseOffset + i + 1) * 0.004f;

            consumer.vertex(pose, 0.5f, baseHeight + centerWobble, 0.5f)
                    .color(r, g, b, LIQUID_ALPHA).uv2(light).endVertex();
            consumer.vertex(pose, 0.5f + Mth.cos(a1) * vertexRadius, baseHeight + w1, 0.5f + Mth.sin(a1) * vertexRadius)
                    .color(r, g, b, LIQUID_ALPHA).uv2(light).endVertex();
            consumer.vertex(pose, 0.5f + Mth.cos(a2) * vertexRadius, baseHeight + w2, 0.5f + Mth.sin(a2) * vertexRadius)
                    .color(r, g, b, LIQUID_ALPHA).uv2(light).endVertex();
        }
    }

    /**
     * 完成态液体颜色与最终产物一致（物品着色 / 纹理主色）；无法解析时回退为配料混合色。
     */
    private int computeLiquidColorForCompleted(SaladBowlBlockEntity blockEntity, List<ItemStack> ingredients) {
        ItemStack result = blockEntity.getResultStack();
        if (!result.isEmpty()) {
            ItemColors itemColors = Minecraft.getInstance().getItemColors();
            int color = getIngredientColor(result, itemColors);
            if (color != -1) {
                return boostLowBrightness(color);
            }
        }
        return computeMixedColor(ingredients);
    }

    private int boostLowBrightness(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int brightness = Math.max(r, Math.max(g, b));
        if (brightness < 60) {
            float boost = 60f / Math.max(brightness, 1);
            r = Math.min(255, (int) (r * boost));
            g = Math.min(255, (int) (g * boost));
            b = Math.min(255, (int) (b * boost));
        }
        return (r << 16) | (g << 8) | b;
    }

    private int computeMixedColor(List<ItemStack> ingredients) {
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        long totalR = 0, totalG = 0, totalB = 0;
        int count = 0;

        for (ItemStack stack : ingredients) {
            if (stack.isEmpty()) continue;
            int color = getIngredientColor(stack, itemColors);
            if (color == -1) continue;
            totalR += (color >> 16) & 0xFF;
            totalG += (color >> 8) & 0xFF;
            totalB += color & 0xFF;
            count++;
        }

        if (count == 0) return DEFAULT_LIQUID_COLOR;

        int r = (int) (totalR / count);
        int g = (int) (totalG / count);
        int b = (int) (totalB / count);

        return boostLowBrightness((r << 16) | (g << 8) | b);
    }

    private int getIngredientColor(ItemStack stack, ItemColors itemColors) {
        int tint = itemColors.getColor(stack, 0);
        if (tint != -1 && tint != 0xFFFFFF) {
            return tint;
        }
        try {
            return sampleTextureColor(stack);
        } catch (Exception ignored) {
        }
        return hashBasedColor(stack);
    }

    @SuppressWarnings("deprecation")
    private int sampleTextureColor(ItemStack stack) {
        BakedModel model = Minecraft.getInstance().getItemRenderer()
                .getModel(stack, null, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon();
        if (sprite == null) return -1;

        int w = sprite.contents().width();
        int h = sprite.contents().height();
        long totalR = 0, totalG = 0, totalB = 0;
        int pixelCount = 0;

        int step = Math.max(1, Math.min(w, h) / 4);
        for (int x = 0; x < w; x += step) {
            for (int y = 0; y < h; y += step) {
                int pixel = sprite.getPixelRGBA(0, x, y);
                int a = (pixel >> 24) & 0xFF;
                if (a < 128) continue;
                totalR += pixel & 0xFF;
                totalG += (pixel >> 8) & 0xFF;
                totalB += (pixel >> 16) & 0xFF;
                pixelCount++;
            }
        }

        if (pixelCount == 0) return -1;

        int r = (int) (totalR / pixelCount);
        int g = (int) (totalG / pixelCount);
        int b = (int) (totalB / pixelCount);
        return (r << 16) | (g << 8) | b;
    }

    private int hashBasedColor(ItemStack stack) {
        int hash = stack.getItem().getDescriptionId().hashCode();
        int r = Math.max(60, (hash >> 16) & 0xFF);
        int g = Math.max(60, (hash >> 8) & 0xFF);
        int b = Math.max(60, hash & 0xFF);
        return (r << 16) | (g << 8) | b;
    }

    private float pseudoRandomFloat(long seed, float min, float max) {
        seed ^= (seed >>> 33);
        seed *= 0xff51afd7ed558ccdL;
        seed ^= (seed >>> 33);
        float t = ((int) (seed & 0x7FFFFFFF)) / (float) Integer.MAX_VALUE;
        return min + t * (max - min);
    }

    private int computeLight(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    @SuppressWarnings("all")
    private static final class LiquidRenderHelper extends RenderType {
        private LiquidRenderHelper() {
            super("dummy", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
                    256, false, true, () -> {}, () -> {});
            throw new UnsupportedOperationException();
        }

        static final RenderType SALAD_LIQUID = create(
                "horrorcooked9_salad_liquid",
                DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
                VertexFormat.Mode.TRIANGLES,
                256, false, true,
                CompositeState.builder()
                        .setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false)
        );
    }
}
