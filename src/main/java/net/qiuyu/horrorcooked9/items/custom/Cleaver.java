package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.sharpen.SharpeningStoneConfig;
import net.qiuyu.horrorcooked9.register.ModTags;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;

public class Cleaver extends Item {
    private static final String SHARPEN_DURATION_TAG = "HC9SharpenUseDuration";

    public Cleaver(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack mainHand = pPlayer.getItemInHand(pUsedHand);
        if (pUsedHand != InteractionHand.MAIN_HAND || mainHand != pPlayer.getMainHandItem()) {
            return super.use(pLevel, pPlayer, pUsedHand);
        }

        ItemStack offhand = pPlayer.getOffhandItem();
        if (!canSharpen(pLevel, mainHand, offhand)) {
            clearSharpenDuration(mainHand);
            return super.use(pLevel, pPlayer, pUsedHand);
        }

        ResourceManager resourceManager = getResourceManager(pLevel);
        int useDuration = SharpeningStoneConfig.resolveUseDurationTicks(resourceManager, offhand);
        mainHand.getOrCreateTag().putInt(SHARPEN_DURATION_TAG, useDuration);
        pPlayer.startUsingItem(InteractionHand.MAIN_HAND);
        return InteractionResultHolder.consume(mainHand);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        if (pStack.hasTag() && pStack.getTag() != null && pStack.getTag().contains(SHARPEN_DURATION_TAG)) {
            return Math.max(1, pStack.getTag().getInt(SHARPEN_DURATION_TAG));
        }
        return 100;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, Level pLevel, @NotNull LivingEntity pLivingEntity) {
        try {
            if (pLevel.isClientSide() || !(pLivingEntity instanceof Player player)) {
                return pStack;
            }

            ItemStack mainHand = player.getMainHandItem();
            if (mainHand != pStack || !mainHand.isDamageableItem() || mainHand.getDamageValue() <= 0) {
                return pStack;
            }

            ItemStack stoneStack = player.getOffhandItem();
            if (!stoneStack.is(ModTags.Items.SHARPENING_STONES)) {
                return pStack;
            }

            ResourceManager resourceManager = getResourceManager(pLevel);
            int totalRepair = SharpeningStoneConfig.resolveRepairAmount(resourceManager, stoneStack, pStack);
            if (totalRepair <= 0) {
                return pStack;
            }

            int newDamage = Math.max(0, pStack.getDamageValue() - totalRepair);
            pStack.setDamageValue(newDamage);

            int stoneDamage = SharpeningStoneConfig.resolveStoneDamage(resourceManager, stoneStack);
            if (stoneDamage > 0 && stoneStack.isDamageableItem()) {
                stoneStack.hurtAndBreak(stoneDamage, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(InteractionHand.OFF_HAND));
            }

            pLevel.playSound(null, player.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.85F, 1.0F);
            return pStack;
        } finally {
            clearSharpenDuration(pStack);
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged) {
        clearSharpenDuration(pStack);
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
    }

    private static boolean canSharpen(Level level, ItemStack cleaverStack, ItemStack stoneStack) {
        if (cleaverStack.isEmpty() || !cleaverStack.isDamageableItem() || cleaverStack.getDamageValue() <= 0) {
            return false;
        }
        if (!stoneStack.is(ModTags.Items.SHARPENING_STONES)) {
            return false;
        }

        ResourceManager resourceManager = getResourceManager(level);
        int totalRepair = SharpeningStoneConfig.resolveRepairAmount(resourceManager, stoneStack, cleaverStack);
        return totalRepair > 0;
    }

    @Nullable
    private static ResourceManager getResourceManager(Level level) {
        return level.getServer() != null ? level.getServer().getResourceManager() : null;
    }

    private static void clearSharpenDuration(ItemStack stack) {
        stack.removeTagKey(SHARPEN_DURATION_TAG);
    }

    public static ResourceLocation getTexture() {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":item/cleaver");
    }
}
