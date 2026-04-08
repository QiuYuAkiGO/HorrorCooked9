package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.register.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParasiticBeanSproutsItem extends Item {
    private static final int DAMAGE_INTERVAL_TICKS = 5 * 20;
    private static final int DIARRHEA_DURATION_TICKS = 60 * 20;
    private static final float DAMAGE_AMOUNT = 1.0F;

    public ParasiticBeanSproutsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return super.getName(pStack).copy().withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.1"));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.2"));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.parasitic_bean_sprouts.desc.3"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pLevel.isClientSide() || !(pEntity instanceof Player player)) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean triggerFromMainHand = mainHand.is(this) && pStack == mainHand;
        boolean triggerFromOffHand = !mainHand.is(this) && offHand.is(this) && pStack == offHand;
        if (!triggerFromMainHand && !triggerFromOffHand) {
            return;
        }

        if (player.tickCount % DAMAGE_INTERVAL_TICKS != 0) {
            return;
        }

        player.hurt(player.damageSources().magic(), DAMAGE_AMOUNT);
        player.addEffect(new MobEffectInstance(ModEffects.DIARRHEA.get(), DIARRHEA_DURATION_TICKS, 0, false, true));
    }
}
