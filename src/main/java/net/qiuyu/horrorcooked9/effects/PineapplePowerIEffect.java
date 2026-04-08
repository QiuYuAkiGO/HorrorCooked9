package net.qiuyu.horrorcooked9.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PineapplePowerIEffect extends MobEffect {
    private static final String MAX_HEALTH_UUID = "4f7a3f47-8fd7-4e14-9308-1ddbf6df9606";

    public PineapplePowerIEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD166);
        this.addAttributeModifier(Attributes.MAX_HEALTH, MAX_HEALTH_UUID, 3.0D, AttributeModifier.Operation.ADDITION);
    }
}
