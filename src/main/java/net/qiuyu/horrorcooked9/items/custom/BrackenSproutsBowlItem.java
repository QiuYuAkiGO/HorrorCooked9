package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.register.ModEffects;

public class BrackenSproutsBowlItem extends AbstractMultiUseBowlItem {
    public static final int DEFAULT_FOOD_USES = 2;
    private static final int DIARRHEA_DURATION_TICKS = 240 * 20;
    private static final int BLINDNESS_DURATION_TICKS = 30 * 20;

    public BrackenSproutsBowlItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void afterSingleUseConsumed(ItemStack singleUseStack, Level level, LivingEntity livingEntity) {
        if (level.isClientSide()) {
            return;
        }
        livingEntity.addEffect(new MobEffectInstance(ModEffects.DIARRHEA.get(), DIARRHEA_DURATION_TICKS, 0, false, true));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_DURATION_TICKS, 0, false, true));
    }

    @Override
    protected int resolveFoodUses(ItemStack stack) {
        return DEFAULT_FOOD_USES;
    }

    @Override
    protected int resolveBarColor(ItemStack pStack) {
        return 0x7FAF3F;
    }
}
