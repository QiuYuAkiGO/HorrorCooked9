package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import net.qiuyu.horrorcooked9.gameplay.food.MultiUseFoodData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FarRealmGemBowlItem extends Item {
    private static final ResourceLocation ITEM_ID = ResourceLocation.parse("horrorcooked9:far_realm_gem_bowl");
    private static final int DEFAULT_FOOD_USES = 2;
    private static final int DEFAULT_BAR_COLOR = 0x4CD3FF;

    public FarRealmGemBowlItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return super.getName(pStack).copy().withStyle(ChatFormatting.GOLD);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack singleUseStack = pStack.copyWithCount(1);
        super.finishUsingItem(singleUseStack, pLevel, pLivingEntity);

        if (pLivingEntity instanceof Player player && player.getAbilities().instabuild) {
            return pStack;
        }

        int remaining = MultiUseFoodData.consumeOne(pStack, resolveFoodUses());
        if (remaining <= 0) {
            pStack.shrink(1);
            ItemStack container = new ItemStack(Items.BOWL);
            if (pStack.isEmpty()) {
                return container;
            }
            if (pLivingEntity instanceof Player player && !player.getInventory().add(container)) {
                player.drop(container, false);
            }
        }
        return pStack;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        int total = MultiUseFoodData.getTotalUses(pStack, resolveFoodUses());
        int remaining = MultiUseFoodData.getRemainingUses(pStack, resolveFoodUses());
        return total > 1 && remaining > 0;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int total = MultiUseFoodData.getTotalUses(pStack, resolveFoodUses());
        int remaining = MultiUseFoodData.getRemainingUses(pStack, resolveFoodUses());
        return Math.round(13.0F * ((float) remaining / (float) total));
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return FoodRuntimeConfigs.resolveBarColor(ITEM_ID, DEFAULT_BAR_COLOR);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.far_realm_gem_bowl.desc.1").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.far_realm_gem_bowl.desc.2").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.far_realm_gem_bowl.desc.3").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.far_realm_gem_bowl.desc.4").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    private static int resolveFoodUses() {
        return FoodRuntimeConfigs.resolveUses(ITEM_ID, DEFAULT_FOOD_USES);
    }
}
