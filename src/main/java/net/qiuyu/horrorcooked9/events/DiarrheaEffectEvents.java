package net.qiuyu.horrorcooked9.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModItems;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DiarrheaEffectEvents {
    /** Translation key describing periodic diarrhea logic in {@link #onPlayerTick} (tooltip/guide). */
    public static final String LANG_EVENTS_DESC = "effect.horrorcooked9.diarrhea.events.desc.1";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }

        MobEffectInstance diarrhea = player.getEffect(ModEffects.DIARRHEA.get());
        if (diarrhea == null) {
            return;
        }

        FoodRuntimeConfigs.DiarrheaEventProfile profile = FoodRuntimeConfigs.resolveDiarrheaEvents();
        if (player.tickCount % profile.procIntervalTicks() != 0) {
            return;
        }

        float roll = player.getRandom().nextFloat();
        if (roll < profile.soundOnlyChance()) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        int amplifier = diarrhea.getAmplifier();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, profile.basePenaltyDurationTicks(), amplifier, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, profile.basePenaltyDurationTicks(), amplifier, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, profile.stopDurationTicks(), 6, false, true));
        player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.2F);

        Vec3 backward = player.getLookAngle().multiply(-1.0D, 0.0D, -1.0D);
        if (backward.lengthSqr() < 1.0E-4D) {
            backward = new Vec3(0.0D, 0.0D, 1.0D);
        }
        backward = backward.normalize();

        double spawnX = player.getX() + backward.x * profile.dropDistance();
        double spawnY = player.getY() + profile.dropVerticalOffset();
        double spawnZ = player.getZ() + backward.z * profile.dropDistance();

        ItemEntity shitDrop = new ItemEntity(player.level(), spawnX, spawnY, spawnZ, new ItemStack(ModItems.SHIT.get()));
        shitDrop.setDeltaMovement(backward.scale(profile.dropSpeed()).add(0.0D, profile.dropUpwardSpeed(), 0.0D));
        player.level().addFreshEntity(shitDrop);
    }
}
