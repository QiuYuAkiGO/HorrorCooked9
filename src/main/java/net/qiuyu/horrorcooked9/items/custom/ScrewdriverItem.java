package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!canToggleTrapdoor(level.getBlockState(pos))) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        return tryToggleTrapdoor(level, pos, player, context.getHand(), context.getItemInHand())
                ? InteractionResult.CONSUME
                : InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.horrorcooked9.screwdriver.desc.1")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public static boolean canToggleTrapdoor(BlockState state) {
        return state.is(BlockTags.TRAPDOORS) && state.hasProperty(BlockStateProperties.OPEN);
    }

    public static boolean tryToggleTrapdoor(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        BlockState state = level.getBlockState(pos);
        if (!canToggleTrapdoor(state) || !isAdventurePlayer(player)) {
            return false;
        }

        level.setBlock(pos, state.cycle(BlockStateProperties.OPEN), Block.UPDATE_ALL);
        stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
        return true;
    }

    private static boolean isAdventurePlayer(Player player) {
        return player instanceof ServerPlayer serverPlayer
                && serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE;
    }

    public static ResourceLocation getTexture() {
        return ResourceLocation.parse(HorrorCooked9.MODID + ":item/screwdriver");
    }
}
