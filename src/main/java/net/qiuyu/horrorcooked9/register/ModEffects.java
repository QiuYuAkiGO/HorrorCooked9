package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.effects.CaptainInspiration;
import net.qiuyu.horrorcooked9.effects.DiarrheaEffect;
import net.qiuyu.horrorcooked9.effects.PineapplePowerIEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HorrorCooked9.MODID);

    public static final RegistryObject<MobEffect> CAPTAIN_INSPIRATION =
            MOB_EFFECTS.register("captain_inspiration", CaptainInspiration::new);
    public static final RegistryObject<MobEffect> DIARRHEA =
            MOB_EFFECTS.register("diarrhea", DiarrheaEffect::new);
    public static final RegistryObject<MobEffect> PINEAPPLE_POWER_I =
            MOB_EFFECTS.register("pineapple_power_i", PineapplePowerIEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
