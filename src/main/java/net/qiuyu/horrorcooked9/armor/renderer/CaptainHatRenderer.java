package net.qiuyu.horrorcooked9.armor.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.qiuyu.horrorcooked9.register.ModItems;
import org.jetbrains.annotations.NotNull;

public class CaptainHatRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static HumanoidModel<?> EMPTY_MODEL;

    public static final IClientItemExtensions CLIENT_EXTENSIONS = new IClientItemExtensions() {
        @Override
        public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                EquipmentSlot slot, HumanoidModel<?> original) {
            return getEmptyModel();
        }
    };

    public CaptainHatRenderer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmet.is(ModItems.CAPTAIN_HAT.get())) return;

        poseStack.pushPose();

        this.getParentModel().head.translateAndRotate(poseStack);

        // Same transforms as vanilla CustomHeadLayer for non-block items
        poseStack.translate(0.0, -0.25, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.625F, -0.625F, -0.625F);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity, helmet, ItemDisplayContext.HEAD, false,
                poseStack, buffer, entity.level(), packedLight,
                OverlayTexture.NO_OVERLAY, entity.getId()
        );

        poseStack.popPose();
    }

    public static HumanoidModel<?> getEmptyModel() {
        if (EMPTY_MODEL == null) {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
            root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
            EMPTY_MODEL = new HumanoidModel<>(LayerDefinition.create(mesh, 1, 1).bakeRoot());
        }
        return EMPTY_MODEL;
    }
}
