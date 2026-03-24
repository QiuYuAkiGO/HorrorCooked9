package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.effects.CaptainInspiration;

public class ModEffects {
    public static final DeferredRegister<MobEffect> ITEMS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HorrorCooked9.MODID);

    public static final RegistryObject<MobEffect> CAPTAIN_INSPIRATION =
            ITEMS.register("captain_inspiration", CaptainInspiration::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
