package net.qiuyu.horrorcooked9.register;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.entity.custom.ExcrementEntity;
import net.qiuyu.horrorcooked9.entity.custom.HookEntity;
import net.qiuyu.horrorcooked9.entity.custom.HookMonsterEntity;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HorrorCooked9.MODID);

    public static final RegistryObject<EntityType<HookMonsterEntity>> HOOK_MONSTER =
            ENTITY_TYPES.register("hook_monster",
                    () -> EntityType.Builder.of(HookMonsterEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 2.4F)
                            .clientTrackingRange(10)
                            .build("hook_monster"));

    public static final RegistryObject<EntityType<ExcrementEntity>> EXCREMENT =
            ENTITY_TYPES.register("excrement",
                    () -> EntityType.Builder.<ExcrementEntity>of(ExcrementEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.8F)
                            .clientTrackingRange(4)
                            .build("excrement"));

    public static final RegistryObject<EntityType<HookEntity>> HOOK =
            ENTITY_TYPES.register("hook",
                    () -> EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.MISC)
                            .sized(0.3F, 0.3F)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("hook"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
