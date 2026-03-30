package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.qiuyu.horrorcooked9.register.ModFluids;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleFilterBlockEntity extends BlockEntity {
    private static final String FUEL_KEY = "FuelInventory";
    private static final String TICK_KEY = "ProcessTick";
    private static final int PROCESS_INTERVAL_TICKS = 20;
    private static final int MB_PER_OPERATION = 250;

    private final ItemStackHandler fuelInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markAndSync();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.CHARCOAL);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    private LazyOptional<IItemHandler> fuelHandler = LazyOptional.of(() -> fuelInventory);
    private int processTick;

    public SimpleFilterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SIMPLE_FILTER_BE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SimpleFilterBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }
        blockEntity.processTick++;
        if (blockEntity.processTick < PROCESS_INTERVAL_TICKS) {
            return;
        }
        blockEntity.processTick = 0;
        blockEntity.tryFilterOnce();
    }

    public boolean insertOneCharcoal() {
        ItemStack remain = fuelInventory.insertItem(0, new ItemStack(Items.CHARCOAL), false);
        return remain.isEmpty();
    }

    public void dropContents() {
        if (level == null || level.isClientSide()) {
            return;
        }
        ItemStack fuel = fuelInventory.getStackInSlot(0);
        if (!fuel.isEmpty()) {
            net.minecraft.world.level.block.Block.popResource(level, worldPosition, fuel.copy());
            fuelInventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    private void tryFilterOnce() {
        if (level == null || !hasInputWater() || !hasFuel()) {
            return;
        }

        boolean success = tryFillContainerBelow() || tryPlaceFilteredWaterBelow();
        if (!success) {
            return;
        }

        consumeOneFuel();
        markAndSync();
    }

    private boolean hasInputWater() {
        return level != null && level.getFluidState(worldPosition.above()).getType() == Fluids.WATER;
    }

    private boolean hasFuel() {
        return !fuelInventory.getStackInSlot(0).isEmpty();
    }

    private void consumeOneFuel() {
        ItemStack fuel = fuelInventory.getStackInSlot(0);
        if (!fuel.isEmpty()) {
            fuel.shrink(1);
            fuelInventory.setStackInSlot(0, fuel);
        }
    }

    private boolean tryFillContainerBelow() {
        if (level == null) {
            return false;
        }
        BlockPos belowPos = worldPosition.below();
        BlockEntity below = level.getBlockEntity(belowPos);
        if (below == null) {
            return false;
        }
        LazyOptional<IFluidHandler> fluidHandler = below.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP);
        if (!fluidHandler.isPresent()) {
            return false;
        }
        IFluidHandler handler = fluidHandler.orElseThrow(IllegalStateException::new);
        int filled = handler.fill(new FluidStack(ModFluids.FILTERED_WATER.get(), MB_PER_OPERATION), IFluidHandler.FluidAction.EXECUTE);
        return filled > 0;
    }

    private boolean tryPlaceFilteredWaterBelow() {
        if (level == null) {
            return false;
        }
        BlockPos belowPos = worldPosition.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.canBeReplaced() || !level.getFluidState(belowPos).isEmpty()) {
            return false;
        }
        level.setBlock(belowPos, ModBlocks.FILTERED_WATER_BLOCK.get().defaultBlockState(), 3);
        return true;
    }

    private void markAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(FUEL_KEY, fuelInventory.serializeNBT());
        tag.putInt(TICK_KEY, processTick);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        fuelInventory.deserializeNBT(tag.getCompound(FUEL_KEY));
        processTick = tag.getInt(TICK_KEY);
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

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fuelHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        fuelHandler = LazyOptional.of(() -> fuelInventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side != Direction.DOWN) {
            return fuelHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
