package net.qiuyu.horrorcooked9.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModEffects;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PineapplePowerEvents {
    private static final float RETALIATE_DAMAGE = 3.0F;
    private static final ThreadLocal<Boolean> APPLYING_RETALIATE_DAMAGE = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (APPLYING_RETALIATE_DAMAGE.get()) {
            return;
        }

        LivingEntity victim = event.getEntity();
        if (!victim.hasEffect(ModEffects.PINEAPPLE_POWER_I.get())) {
            return;
        }

        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();
        if (!(directEntity instanceof LivingEntity attacker)) {
            return;
        }
        if (attacker == victim) {
            return;
        }

        try {
            APPLYING_RETALIATE_DAMAGE.set(true);
            attacker.hurt(attacker.damageSources().magic(), RETALIATE_DAMAGE);
        } finally {
            APPLYING_RETALIATE_DAMAGE.set(false);
        }
    }
}
