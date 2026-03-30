package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.gameplay.butchery.ButcheryConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CarcassItem extends Item {
    public CarcassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(pStack.getItem());
        if (itemId != null) {
            pTooltipComponents.add(Component.translatable("item." + itemId.getNamespace() + "." + itemId.getPath() + ".desc.1")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack carcassStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide()) {
            return InteractionResultHolder.success(carcassStack);
        }

        ResourceManager resourceManager = pLevel.getServer() != null ? pLevel.getServer().getResourceManager() : null;
        ButcheryConfig.InteractionProfile interaction = ButcheryConfig.resolveInteraction(resourceManager);
        ItemStack toolStack = resolveHarvestToolStack(pPlayer, pUsedHand, interaction);
        if (toolStack.isEmpty()) {
            return InteractionResultHolder.pass(carcassStack);
        }

        Optional<ButcheryConfig.CarcassHarvestProfile> profileOptional = ButcheryConfig.resolveHarvestProfile(resourceManager, carcassStack);
        if (profileOptional.isEmpty()) {
            return InteractionResultHolder.pass(carcassStack);
        }

        List<ItemStack> drops = ButcheryConfig.rollCarcassDrops(profileOptional.get(), pLevel.getRandom());
        if (drops.isEmpty()) {
            return InteractionResultHolder.pass(carcassStack);
        }

        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                pPlayer.spawnAtLocation(drop);
            }
        }

        carcassStack.shrink(1);
        damageToolIfNeeded(pPlayer, toolStack, interaction);
        if (interaction.harvestCooldownTicks() > 0) {
            pPlayer.getCooldowns().addCooldown(this, interaction.harvestCooldownTicks());
        }

        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(interaction.harvestSound());
        if (soundEvent != null) {
            pLevel.playSound(null, pPlayer.blockPosition(), soundEvent, SoundSource.PLAYERS, 0.9F, 1.0F);
        }

        return InteractionResultHolder.consume(carcassStack);
    }

    private static ItemStack resolveHarvestToolStack(Player player, InteractionHand usedHand, ButcheryConfig.InteractionProfile interaction) {
        TagKey<Item> toolTag = TagKey.create(Registries.ITEM, interaction.harvestToolItemTag());
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (interaction.requireHarvestToolInOffhand()) {
            if (usedHand == InteractionHand.MAIN_HAND && offHand.is(toolTag)) {
                return offHand;
            }
            if (usedHand == InteractionHand.OFF_HAND && mainHand.is(toolTag)) {
                return mainHand;
            }
            return ItemStack.EMPTY;
        }

        if (mainHand.is(toolTag) && usedHand != InteractionHand.MAIN_HAND) {
            return mainHand;
        }
        if (offHand.is(toolTag) && usedHand != InteractionHand.OFF_HAND) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    private static void damageToolIfNeeded(Player player, ItemStack toolStack, ButcheryConfig.InteractionProfile interaction) {
        int durabilityCost = interaction.harvestToolDamagePerUse();
        if (durabilityCost <= 0 || !toolStack.isDamageableItem()) {
            return;
        }

        InteractionHand breakHand = toolStack == player.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        toolStack.hurtAndBreak(durabilityCost, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(breakHand));
    }
}
