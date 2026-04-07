package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;

public class GoldenShitItem extends Item {
    private static final int EXTEND_TICKS = 100;
    private static final FoodProperties GOLDEN_SHIT_FOOD = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2f)
            .alwaysEat()
            .fast()
            .build();

    public GoldenShitItem(Properties pProperties) {
        super(pProperties.food(GOLDEN_SHIT_FOOD));
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack result = super.finishUsingItem(pStack, pLevel, pLivingEntity);
        if (!pLevel.isClientSide) {
            extendOrApplyEffect(pLivingEntity, MobEffects.REGENERATION, EXTEND_TICKS, 1);
            extendOrApplyEffect(pLivingEntity, MobEffects.ABSORPTION, EXTEND_TICKS, 0);
        }
        return result;
    }

    private static void extendOrApplyEffect(LivingEntity livingEntity, MobEffect effect, int addDuration, int baseAmplifier) {
        MobEffectInstance current = livingEntity.getEffect(effect);
        if (current == null) {
            livingEntity.addEffect(new MobEffectInstance(effect, addDuration, baseAmplifier));
            return;
        }

        int amplifier = Math.max(current.getAmplifier(), baseAmplifier);
        int duration = current.getDuration() + addDuration;
        livingEntity.addEffect(new MobEffectInstance(
                effect,
                duration,
                amplifier,
                current.isAmbient(),
                current.isVisible(),
                current.showIcon()
        ));
    }
}
