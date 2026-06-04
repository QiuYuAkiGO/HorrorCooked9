package net.qiuyu.horrorcooked9.items.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PummelerItem extends Item {
    public static final float ATTACK_DAMAGE_BONUS = 1.0F;
    public static final float OFFHAND_BASE_DAMAGE = 2.0F;
    public static final int CRIT_EFFECT_DURATION_TICKS = 5 * 20;
    public static final int MAX_CRIT_COUNT = 4;

    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("cdb780f2-451a-40d0-b235-0f57db6b09a2");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("778ec4cc-40c7-4b6e-860b-58fb6711d627");
    private static final double ATTACK_SPEED_MODIFIER = -2.4D;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public PummelerItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                ATTACK_DAMAGE_UUID,
                "Pummeler attack damage",
                ATTACK_DAMAGE_BONUS,
                AttributeModifier.Operation.ADDITION
        ));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                ATTACK_SPEED_UUID,
                "Pummeler attack speed",
                ATTACK_SPEED_MODIFIER,
                AttributeModifier.Operation.ADDITION
        ));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            damagePummeler(stack, player, InteractionHand.MAIN_HAND);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.horrorcooked9.pummeler.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.horrorcooked9.pummeler.desc.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.horrorcooked9.pummeler.desc.3").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public static boolean isPummeler(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.PUMMELER.get());
    }

    public static boolean isDualWielding(Player player) {
        return isPummeler(player.getMainHandItem()) && isPummeler(player.getOffhandItem());
    }

    public static int getCritCount(Player player) {
        MobEffectInstance instance = player.getEffect(ModEffects.PUMMELER_CRIT_BOOST.get());
        if (instance == null) {
            return 0;
        }
        return Math.min(instance.getAmplifier() + 1, MAX_CRIT_COUNT);
    }

    public static float getCritMultiplier(Player player) {
        return Math.min(1.5F + 0.5F * getCritCount(player), 3.0F);
    }

    public static void advanceCritBoost(Player player) {
        int nextCount = Math.min(getCritCount(player) + 1, MAX_CRIT_COUNT);
        player.addEffect(new MobEffectInstance(
                ModEffects.PUMMELER_CRIT_BOOST.get(),
                CRIT_EFFECT_DURATION_TICKS,
                nextCount - 1,
                false,
                false
        ));
    }

    public static void refreshCritBoost(Player player) {
        MobEffectInstance instance = player.getEffect(ModEffects.PUMMELER_CRIT_BOOST.get());
        if (instance == null) {
            return;
        }
        player.addEffect(new MobEffectInstance(
                ModEffects.PUMMELER_CRIT_BOOST.get(),
                CRIT_EFFECT_DURATION_TICKS,
                instance.getAmplifier(),
                false,
                false
        ));
    }

    public static void clearCritBoost(Player player) {
        player.removeEffect(ModEffects.PUMMELER_CRIT_BOOST.get());
    }

    public static void damagePummeler(ItemStack stack, Player player, InteractionHand hand) {
        stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
    }

    public static ResourceLocation getTexture() {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":item/pummeler");
    }
}
