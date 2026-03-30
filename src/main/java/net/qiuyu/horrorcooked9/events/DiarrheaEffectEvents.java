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
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModItems;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DiarrheaEffectEvents {
    /** Translation key describing periodic diarrhea logic in {@link #onPlayerTick} (tooltip/guide). */
    public static final String LANG_EVENTS_DESC = "effect.horrorcooked9.diarrhea.events.desc.1";

    private static final int PROC_INTERVAL_TICKS = 200;
    private static final int BASE_PENALTY_DURATION_TICKS = 40;
    private static final int STOP_DURATION_TICKS = 40;
    private static final float SOUND_ONLY_CHANCE = 0.75F;

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

        if (player.tickCount % PROC_INTERVAL_TICKS != 0) {
            return;
        }

        float roll = player.getRandom().nextFloat();
        if (roll < SOUND_ONLY_CHANCE) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        int amplifier = diarrhea.getAmplifier();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BASE_PENALTY_DURATION_TICKS, amplifier, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, BASE_PENALTY_DURATION_TICKS, amplifier, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STOP_DURATION_TICKS, 6, false, true));
        player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.2F);

        Vec3 backward = player.getLookAngle().multiply(-1.0D, 0.0D, -1.0D);
        if (backward.lengthSqr() < 1.0E-4D) {
            backward = new Vec3(0.0D, 0.0D, 1.0D);
        }
        backward = backward.normalize();

        double spawnX = player.getX() + backward.x * 2.0D;
        double spawnY = player.getY() + 0.4D;
        double spawnZ = player.getZ() + backward.z * 2.0D;

        ItemEntity shitDrop = new ItemEntity(player.level(), spawnX, spawnY, spawnZ, new ItemStack(ModItems.SHIT.get()));
        shitDrop.setDeltaMovement(backward.scale(0.2D).add(0.0D, 0.2D, 0.0D));
        player.level().addFreshEntity(shitDrop);
    }
}
