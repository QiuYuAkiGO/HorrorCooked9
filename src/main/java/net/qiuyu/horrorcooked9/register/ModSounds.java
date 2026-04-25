package net.qiuyu.horrorcooked9.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HorrorCooked9.MODID);

    public static final RegistryObject<SoundEvent> HOOK_MONSTER_BGM =
            SOUND_EVENTS.register("hook_monster_bgm",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(HorrorCooked9.MODID, "hook_monster_bgm")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
