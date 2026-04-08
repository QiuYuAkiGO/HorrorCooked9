package net.qiuyu.horrorcooked9.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;

import java.util.Optional;

/**
 * Applies runtime nutrition/saturation overrides after edible items finish consumption.
 */
@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FoodConsumeRuntimeEvents {

    private FoodConsumeRuntimeEvents() {
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        ItemStack consumed = event.getItem();
        if (consumed.isEmpty() || !consumed.isEdible()) {
            return;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(consumed.getItem());
        if (itemId == null) {
            return;
        }

        Optional<FoodRuntimeConfigs.FoodRuntimeProfile> profileOptional = FoodRuntimeConfigs.resolveProfile(itemId);
        if (profileOptional.isEmpty()) {
            return;
        }

        FoodProperties baseFood = consumed.getFoodProperties(player);
        if (baseFood == null) {
            return;
        }

        FoodRuntimeConfigs.FoodRuntimeProfile profile = profileOptional.get();
        if (profile.nutrition() == null && profile.saturationModifier() == null) {
            return;
        }

        int baseNutrition = baseFood.getNutrition();
        float baseSaturationMod = baseFood.getSaturationModifier();
        int targetNutrition = profile.nutrition() != null ? profile.nutrition() : baseNutrition;
        float targetSaturationMod = profile.saturationModifier() != null ? profile.saturationModifier() : baseSaturationMod;

        int deltaNutrition = targetNutrition - baseNutrition;
        float baseSaturationGain = baseNutrition * baseSaturationMod * 2.0F;
        float targetSaturationGain = targetNutrition * targetSaturationMod * 2.0F;
        float deltaSaturation = targetSaturationGain - baseSaturationGain;

        if (deltaNutrition == 0 && Math.abs(deltaSaturation) < 1.0E-4F) {
            return;
        }

        FoodData data = player.getFoodData();
        int adjustedFood = Mth.clamp(data.getFoodLevel() + deltaNutrition, 0, 20);
        float adjustedSaturation = Mth.clamp(data.getSaturationLevel() + deltaSaturation, 0.0F, adjustedFood);
        data.setFoodLevel(adjustedFood);
        data.setSaturation(adjustedSaturation);
    }
}
