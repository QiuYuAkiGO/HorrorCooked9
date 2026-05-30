package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.FoodRuntimeConfigs;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FarRealmGemBowlItem extends AbstractMultiUseBowlItem {
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
    protected int resolveBarColor(ItemStack pStack) {
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

    @Override
    protected int resolveFoodUses(ItemStack stack) {
        return FoodRuntimeConfigs.resolveUses(ITEM_ID, DEFAULT_FOOD_USES);
    }
}
