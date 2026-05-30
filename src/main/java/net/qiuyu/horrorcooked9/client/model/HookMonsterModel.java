package net.qiuyu.horrorcooked9.client.model;

import net.minecraft.resources.ResourceLocation;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;
import software.bernie.geckolib.model.GeoModel;

public class HookMonsterModel extends GeoModel<HookMonsterEntity> {

    @Override
    public ResourceLocation getModelResource(HookMonsterEntity animatable) {
        return new ResourceLocation(HorrorCooked9.MODID, "geo/hook_monster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HookMonsterEntity animatable) {
        return new ResourceLocation(HorrorCooked9.MODID, "textures/entity/hook_monster.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HookMonsterEntity animatable) {
        return new ResourceLocation(HorrorCooked9.MODID, "animations/hook_monster.animation.json");
    }
}
