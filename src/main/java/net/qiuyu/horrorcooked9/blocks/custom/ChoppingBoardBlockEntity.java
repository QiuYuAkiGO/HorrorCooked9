package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChoppingBoardBlockEntity extends BlockEntity {

    private ItemStack placedItem = ItemStack.EMPTY;

    public ChoppingBoardBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHOPPING_BOARD_BE.get(), pPos, pBlockState);
    }

    public ItemStack getPlacedItem() {
        return placedItem;
    }

    public void setPlacedItem(ItemStack stack) {
        this.placedItem = stack.copy();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStack removePlacedItem() {
        ItemStack removed = placedItem;
        placedItem = ItemStack.EMPTY;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return removed;
    }

    public boolean hasPlacedItem() {
        return !placedItem.isEmpty();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!placedItem.isEmpty()) {
            pTag.put("PlacedItem", placedItem.save(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("PlacedItem")) {
            placedItem = ItemStack.of(pTag.getCompound("PlacedItem"));
        } else {
            placedItem = ItemStack.EMPTY;
        }
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
        } else {
            this.placedItem = ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
