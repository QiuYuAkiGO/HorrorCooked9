package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.qiuyu.horrorcooked9.gameplay.food.ItemFoodConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurableFoodItem extends Item {
    private static final FoodProperties DUMMY_FOOD = new FoodProperties.Builder().nutrition(0).saturationMod(0.0F).build();

    public ConfigurableFoodItem(Properties pProperties) {
        super(pProperties.food(DUMMY_FOOD));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ResourceManager resourceManager = pLevel.getServer() != null ? pLevel.getServer().getResourceManager() : null;
        ItemFoodConfig.FoodProfile profile = ItemFoodConfig.resolveProfile(resourceManager, stack);
        if (!profile.edible()) {
            return InteractionResultHolder.pass(stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public @NotNull FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        ResourceManager resourceManager = null;
        if (entity != null && entity.level().getServer() != null) {
            resourceManager = entity.level().getServer().getResourceManager();
        }

        FoodProperties properties = ItemFoodConfig.resolveFoodProperties(resourceManager, stack);
        return properties != null ? properties : DUMMY_FOOD;
    }
}
