package net.qiuyu.horrorcooked9.gameplay.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.qiuyu.horrorcooked9.register.ModEffects;

public final class ModItemFoodFactory {
    private ModItemFoodFactory() {
    }

    public static FoodProperties twinCorpseParasiticSalad() {
        return new FoodProperties.Builder()
                .nutrition(7)
                .saturationMod(0.9f)
                .effect(() -> new MobEffectInstance(ModEffects.DIARRHEA.get(), 240 * 20, 0), 1.0f)
                .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 30 * 20, 0), 1.0f)
                .build();
    }

    public static FoodProperties brackenSproutsBowl() {
        return new FoodProperties.Builder()
                .nutrition(8)
                .saturationMod(0.8f)
                .build();
    }

    public static FoodProperties tranquilMixedRice() {
        return new FoodProperties.Builder()
                .nutrition(6)
                .saturationMod(0.8f)
                .build();
    }

    public static FoodProperties colorfulPalette() {
        return new FoodProperties.Builder()
                .nutrition(6)
                .saturationMod(0.8f)
                .build();
    }

    public static FoodProperties tranquility() {
        return new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.1f)
                .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 10 * 20, 0), 1.0f)
                .build();
    }

    public static FoodProperties kalePuree() {
        return new FoodProperties.Builder()
                .nutrition(3)
                .saturationMod(0.35f)
                .build();
    }

    public static FoodProperties roastedPineappleChunks() {
        return new FoodProperties.Builder()
                .nutrition(5)
                .saturationMod(0.7f)
                .build();
    }

    public static FoodProperties pineappleFriedRice() {
        return new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(0.6f)
                .build();
    }
}
