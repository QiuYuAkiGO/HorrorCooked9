package net.qiuyu.horrorcooked9.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.HorrorCooked9;

public final class ModDamageSources {
    public static final ResourceKey<DamageType> HOOK_MONSTER_HOOK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(HorrorCooked9.MODID, "hook_monster_hook")
    );

    private ModDamageSources() {
    }

    public static DamageSource hookMonsterHook(Level level, Entity directEntity, Entity causingEntity) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HOOK_MONSTER_HOOK),
                directEntity,
                causingEntity
        );
    }
}
