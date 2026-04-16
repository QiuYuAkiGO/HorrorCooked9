package net.qiuyu.horrorcooked9.network.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.qiuyu.horrorcooked9.blocks.custom.SaladBowlBlockEntity;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladRecipeMatcher;
import net.qiuyu.horrorcooked9.gameplay.stir.IStirrable;
import net.qiuyu.horrorcooked9.gameplay.stir.StirResult;
import net.qiuyu.horrorcooked9.gameplay.stir.StirToolBalanceConfig;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 客户端 -> 服务端：发送 Stir 小游戏的多轮判定结果。
 */
public class StirResultPacket {
    private static final int MAX_STIR_RESULT_COUNT = 10;
    private static final double MAX_INTERACTION_DISTANCE_SQR = 64.0D;

    private final BlockPos pos;
    private final List<Integer> resultOrdinals;

    public StirResultPacket(BlockPos pos, List<StirResult> results) {
        this.pos = pos;
        this.resultOrdinals = new ArrayList<>(results.size());
        for (StirResult result : results) {
            this.resultOrdinals.add(result.ordinal());
        }
    }

    public StirResultPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readVarInt();
        if (size < 0 || size > MAX_STIR_RESULT_COUNT) {
            throw new IllegalArgumentException("Invalid stir result count: " + size);
        }
        this.resultOrdinals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            resultOrdinals.add(buf.readVarInt());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(resultOrdinals.size());
        for (Integer ordinal : resultOrdinals) {
            buf.writeVarInt(ordinal);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }

            Level level = player.level();
            if (!isInteractionAllowed(player, level)) {
                return;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof SaladBowlBlockEntity bowlEntity) || bowlEntity.isCompleted()) {
                return;
            }

            List<SaladBowlRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.SALAD_BOWL_TYPE.get());
            List<ItemStack> sequence = bowlEntity.getAddedIngredients();
            SaladBowlRecipe recipe = SaladRecipeMatcher.resolveStirRecipe(bowlEntity.getCurrentRecipeId(), sequence, recipes);
            if (recipe == null) {
                return;
            }
            boolean exactNow = SaladRecipeMatcher.isExactMatch(sequence, recipe);
            if (!recipe.requiresStirNow(sequence.size(), bowlEntity.getCompletedStirPhases(), exactNow)
                    || !recipe.getMixingTool().test(player.getMainHandItem())) {
                return;
            }

            int requiredStirCount = StirToolBalanceConfig.resolveEffectiveStirCount(
                    level.getServer() != null ? level.getServer().getResourceManager() : null,
                    player.getMainHandItem(),
                    recipe.getStirCount()
            );
            if (resultOrdinals.size() != requiredStirCount) {
                return;
            }

            List<StirResult> roundResults = new ArrayList<>(resultOrdinals.size());
            for (Integer ordinal : resultOrdinals) {
                roundResults.add(StirResult.fromOrdinal(ordinal));
            }

            Item saladBowlItem = ModItems.SALAD_BOWL.get();
            if (!(saladBowlItem instanceof IStirrable stirrable)) {
                return;
            }
            stirrable.onStir(level, pos, player, bowlEntity, recipe, roundResults);
        });
        ctx.setPacketHandled(true);
    }

    private boolean isInteractionAllowed(ServerPlayer player, Level level) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)
                <= MAX_INTERACTION_DISTANCE_SQR;
    }
}
