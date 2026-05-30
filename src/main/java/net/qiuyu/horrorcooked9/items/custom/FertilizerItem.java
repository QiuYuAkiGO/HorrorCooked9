package net.qiuyu.horrorcooked9.items.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FertilizerItem extends Item {
    private static final int MULTIPLIER = 5;

    public FertilizerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropBlock cropBlock)) {
            return InteractionResult.PASS;
        }
        if (cropBlock.isMaxAge(state)) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockState matureState = cropBlock.getStateForAge(cropBlock.getMaxAge());
        level.setBlock(pos, matureState, Block.UPDATE_ALL_IMMEDIATE);

        for (int i = 0; i < MULTIPLIER; i++) {
            List<ItemStack> drops = Block.getDrops(matureState, serverLevel, pos, null, context.getPlayer(), ItemStack.EMPTY);
            for (ItemStack drop : drops) {
                Block.popResource(serverLevel, pos, drop.copy());
            }
        }

        level.setBlock(pos, cropBlock.getStateForAge(0), Block.UPDATE_ALL_IMMEDIATE);
        level.levelEvent(1505, pos, 0);
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
