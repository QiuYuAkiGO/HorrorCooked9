package net.qiuyu.horrorcooked9.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CaptainInspiration extends MobEffect {
//    private final ResourceLocation KEY = ResourceLocation.parse("horrorcooked9:captain_inspiration");
    public CaptainInspiration() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635",
                0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
