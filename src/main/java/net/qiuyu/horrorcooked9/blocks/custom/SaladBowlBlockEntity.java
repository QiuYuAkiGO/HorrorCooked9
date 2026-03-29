package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.qiuyu.horrorcooked9.gameplay.salad.SaladBowlRecipe;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaladBowlBlockEntity extends BlockEntity {
    private final List<ItemStack> addedIngredients = new ArrayList<>();
    @Nullable
    private ResourceLocation currentRecipeId;
    private boolean completed;
    private ItemStack resultStack = ItemStack.EMPTY;
    private int remainingServings;
    private int initialServings;

    public SaladBowlBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SALAD_BOWL_BE.get(), pPos, pBlockState);
    }

    public List<ItemStack> getAddedIngredients() {
        return Collections.unmodifiableList(addedIngredients);
    }

    public void addIngredient(ItemStack stack) {
        ItemStack toStore = stack.copy();
        toStore.setCount(1);
        addedIngredients.add(toStore);
        markAndSync();
    }

    public void setCurrentRecipeId(@Nullable ResourceLocation recipeId) {
        this.currentRecipeId = recipeId;
        markAndSync();
    }

    public int getStepIndex() {
        return addedIngredients.size();
    }

    public boolean isCompleted() {
        return completed;
    }

    @Nullable
    public ResourceLocation getCurrentRecipeId() {
        return currentRecipeId;
    }

    public ItemStack getResultStack() {
        return resultStack.copy();
    }

    public int getRemainingServings() {
        return remainingServings;
    }

    public int getInitialServings() {
        return initialServings;
    }

    public void completeWith(SaladBowlRecipe recipe) {
        this.completed = true;
        this.currentRecipeId = recipe.getId();
        this.resultStack = recipe.getResultStack();
        this.remainingServings = recipe.getServings();
        this.initialServings = recipe.getServings();
        markAndSync();
    }

    public void consumeOneServingOrReset() {
        if (remainingServings > 0) {
            remainingServings--;
        }
        if (remainingServings <= 0) {
            resetAll();
            return;
        }
        markAndSync();
    }

    public void addBonusServings(int extra) {
        if (!completed || extra <= 0) {
            return;
        }
        remainingServings += extra;
        if (remainingServings > initialServings) {
            initialServings = remainingServings;
        }
        markAndSync();
    }

    public void resetAll() {
        addedIngredients.clear();
        currentRecipeId = null;
        completed = false;
        resultStack = ItemStack.EMPTY;
        remainingServings = 0;
        initialServings = 0;
        markAndSync();
    }

    public List<ItemStack> dumpIngredientsAndReset() {
        List<ItemStack> dropped = new ArrayList<>(addedIngredients.size());
        for (ItemStack ingredient : addedIngredients) {
            dropped.add(ingredient.copy());
        }
        resetAll();
        return dropped;
    }

    private void markAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        ListTag listTag = new ListTag();
        for (ItemStack ingredient : addedIngredients) {
            listTag.add(ingredient.save(new CompoundTag()));
        }
        pTag.put("AddedIngredients", listTag);

        if (currentRecipeId != null) {
            pTag.putString("CurrentRecipeId", currentRecipeId.toString());
        }
        pTag.putBoolean("Completed", completed);
        pTag.putInt("RemainingServings", remainingServings);
        pTag.putInt("InitialServings", initialServings);
        if (!resultStack.isEmpty()) {
            pTag.put("ResultStack", resultStack.save(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        addedIngredients.clear();

        ListTag listTag = pTag.getList("AddedIngredients", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            if (tag instanceof CompoundTag ingredientTag) {
                addedIngredients.add(ItemStack.of(ingredientTag));
            }
        }

        currentRecipeId = pTag.contains("CurrentRecipeId")
                ? ResourceLocation.parse(pTag.getString("CurrentRecipeId"))
                : null;
        completed = pTag.getBoolean("Completed");
        remainingServings = pTag.getInt("RemainingServings");
        initialServings = pTag.getInt("InitialServings");
        resultStack = pTag.contains("ResultStack")
                ? ItemStack.of(pTag.getCompound("ResultStack"))
                : ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
