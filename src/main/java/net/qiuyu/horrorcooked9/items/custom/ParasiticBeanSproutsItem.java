package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import net.qiuyu.horrorcooked9.register.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParasiticBeanSproutsItem extends Item {
    private static final ResourceLocation ITEM_ID = ResourceLocation.parse("horrorcooked9:parasitic_bean_sprouts");
    private static final FoodRuntimeConfigs.InventoryConsumeEffectProfile DEFAULT_PROFILE =
            new FoodRuntimeConfigs.InventoryConsumeEffectProfile(
                    true,
                    ResourceLocation.parse("horrorcooked9:diarrhea"),
                    5 * 20,
                    120 * 20,
                    4,
                    false,
                    true
            );

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

        InteractionHand triggerHand = null;
        ItemStack triggerStack = ItemStack.EMPTY;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(this)) {
            triggerHand = InteractionHand.MAIN_HAND;
            triggerStack = mainHand;
        } else {
            ItemStack offHand = player.getOffhandItem();
            if (offHand.is(this)) {
                triggerHand = InteractionHand.OFF_HAND;
                triggerStack = offHand;
            }
        }
        if (triggerHand == null || triggerStack.isEmpty()) {
            return;
        }

        int count = triggerStack.getCount();
        triggerStack.shrink(count);

        FoodRuntimeConfigs.InventoryConsumeEffectProfile profile = FoodRuntimeConfigs.resolveInventoryConsumeEffect(ITEM_ID, DEFAULT_PROFILE);
        if (!profile.enabled()) {
            return;
        }

        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(profile.effectId());
        if (effect == null) {
            effect = ModEffects.DIARRHEA.get();
        }

        int amplifier = Math.min(Math.max(count - 1, 0), profile.maxAmplifier());
        int durationTicks = Math.min(count * profile.durationPerCountTicks(), profile.maxDurationTicks());
        player.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, profile.ambient(), profile.visible()));
    }
}
