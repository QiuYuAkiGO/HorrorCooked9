package net.qiuyu.horrorcooked9.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladEatReturnHelper;

import java.util.List;

/**
 * 沙拉成品食用完成时的服务端事件：向玩家返还取餐时消耗的盛装容器（见 {@link net.qiuyu.horrorcooked9.gameplay.salad.SaladEatReturnHelper}）。
 * 创造模式不返还，避免刷物。
 */
@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaladEatReturnEvents {

    /**
     * 在 {@link LivingEntityUseItemEvent.Finish} 中处理：仅服务端、非创造、可食用物品，
     * 且能匹配到沙拉配方结果时，返还 {@code serving_container} 代表物（不返还搅拌工具）。
     */
    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        if (player.getAbilities().instabuild) {
            return;
        }

        ItemStack consumed = event.getItem();
        if (consumed.isEmpty() || !consumed.isEdible()) {
            return;
        }

        SaladBowlRecipe recipe = SaladEatReturnHelper.findRecipeByResult(level, consumed);
        if (recipe == null) {
            return;
        }

        List<ItemStack> returns = SaladEatReturnHelper.buildReturnStacks(recipe);
        for (ItemStack stack : returns) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }
}
