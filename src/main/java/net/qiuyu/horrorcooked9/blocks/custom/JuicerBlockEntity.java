package net.qiuyu.horrorcooked9.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.juicing.JuicerMenu;
import net.qiuyu.horrorcooked9.gameplay.juicing.JuicingConfig;
import net.qiuyu.horrorcooked9.register.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JuicerBlockEntity extends BlockEntity implements MenuProvider {
    private static final String INVENTORY_KEY = "Inventory";
    private static final String TANK_KEY = "Tank";
    private static final String PULP_SOURCE_KEY = "PulpSource";
    private static final String PULP_REMAINING_KEY = "PulpRemainingMb";
    private static final String PRESSING_PLAYER_KEY = "PressingPlayer";
    private static final String HOLD_PROGRESS_KEY = "ManualHoldProgress";
    private static final String INTERRUPTED_TICK_KEY = "InterruptedTick";
    private static final int SLOT_FRUIT_INPUT = 0;
    private static final int SLOT_CONTAINER_IN = 1;
    private static final int SLOT_CONTAINER_OUT = 2;
    private static final int SLOT_BYPRODUCT = 3;
    private static final int AUTO_INTERVAL = 10;

    private static Class<?> kineticBlockEntityClass;
    private static Method kineticGetSpeedMethod;
    private static boolean kineticReflectionReady;

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                markAndSync();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == SLOT_FRUIT_INPUT) {
                return resolveFruitProfile(stack).isPresent();
            }
            if (slot == SLOT_CONTAINER_IN) {
                return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET);
            }
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == SLOT_CONTAINER_OUT || slot == SLOT_BYPRODUCT) {
                return 64;
            }
            return super.getSlotLimit(slot);
        }
    };

    private final FluidTank fluidTank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getAmount() > 0;
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                markAndSync();
            }
        }
    };

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> fluidTank.getFluidAmount();
                case 1 -> pulpRemainingMb;
                case 2 -> manualHoldProgress;
                case 3 -> getCurrentManualTargetTicks();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // server authoritative
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);
    @Nullable
    private ResourceLocation pulpSourceFruitId;
    private int pulpRemainingMb;
    @Nullable
    private UUID pressingPlayer;
    private int manualHoldProgress;
    private int interruptedTicks;
    private int autoTick;

    public JuicerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.JUICER_BE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, JuicerBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }
        blockEntity.ensureTankCapacity();
        blockEntity.absorbFruitInput();
        blockEntity.processContainerInput();
        blockEntity.tickManualPress();
        blockEntity.tickAutoProcessing();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getTank() {
        return fluidTank;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public boolean beginManualPress(Player player) {
        if (level == null || level.isClientSide()) {
            return false;
        }
        if (!hasPulp()) {
            return false;
        }
        pressingPlayer = player.getUUID();
        interruptedTicks = 0;
        manualHoldProgress = 0;
        markAndSync();
        return true;
    }

    public void dropContents() {
        if (level == null || level.isClientSide()) {
            return;
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                drops.add(stack.copy());
            }
        }
        for (ItemStack stack : drops) {
            Block.popResource(level, worldPosition, stack);
        }
    }

    private void ensureTankCapacity() {
        JuicingConfig.Defaults defaults = getDefaults();
        if (fluidTank.getCapacity() != defaults.tankCapacityMb()) {
            fluidTank.setCapacity(defaults.tankCapacityMb());
            if (fluidTank.getFluidAmount() > fluidTank.getCapacity()) {
                fluidTank.drain(fluidTank.getFluidAmount() - fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    private void absorbFruitInput() {
        ItemStack fruitStack = inventory.getStackInSlot(SLOT_FRUIT_INPUT);
        if (fruitStack.isEmpty()) {
            return;
        }

        Optional<JuicingConfig.Entry> optional = resolveFruitProfile(fruitStack);
        if (optional.isEmpty()) {
            return;
        }
        JuicingConfig.Entry entry = optional.get();

        if (hasPulp() && !canAcceptFruit(entry.fruitId())) {
            return;
        }

        ItemStack extracted = inventory.extractItem(SLOT_FRUIT_INPUT, 1, false);
        if (extracted.isEmpty()) {
            return;
        }

        if (!hasPulp()) {
            pulpSourceFruitId = entry.fruitId();
        }
        pulpRemainingMb = Math.max(0, pulpRemainingMb + entry.pulpTotalMb());
        markAndSync();
    }

    private boolean canAcceptFruit(ResourceLocation fruitId) {
        if (pulpSourceFruitId == null) {
            return true;
        }
        JuicingConfig.Defaults defaults = getDefaults();
        if (pulpSourceFruitId.equals(fruitId)) {
            return true;
        }
        // Current implementation keeps a single fruit-source pulp queue.
        return defaults.allowMixing() && pulpRemainingMb <= 0;
    }

    private void processContainerInput() {
        if (level == null) {
            return;
        }
        ItemStack containerIn = inventory.getStackInSlot(SLOT_CONTAINER_IN);
        if (containerIn.isEmpty() || fluidTank.getFluid().isEmpty()) {
            return;
        }

        JuicingConfig.Defaults defaults = getDefaults();
        if (containerIn.is(Items.GLASS_BOTTLE)) {
            if (fluidTank.getFluidAmount() < defaults.bottleAmountMb()) {
                return;
            }

            Optional<ResourceLocation> bottleId = JuicingConfig.resolveBottleItem(getResourceManagerSafe(), fluidTank.getFluid().getFluid());
            if (bottleId.isEmpty()) {
                return;
            }
            Item bottleItem = ForgeRegistries.ITEMS.getValue(bottleId.get());
            if (bottleItem == null) {
                return;
            }

            ItemStack out = new ItemStack(bottleItem);
            ItemStack remainder = inventory.insertItem(SLOT_CONTAINER_OUT, out, true);
            if (!remainder.isEmpty()) {
                return;
            }

            inventory.extractItem(SLOT_CONTAINER_IN, 1, false);
            fluidTank.drain(defaults.bottleAmountMb(), IFluidHandler.FluidAction.EXECUTE);
            inventory.insertItem(SLOT_CONTAINER_OUT, out, false);
            markAndSync();
            return;
        }

        if (!containerIn.is(Items.BUCKET)) {
            return;
        }
        if (fluidTank.getFluidAmount() < 1000) {
            return;
        }

        ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluidTank.getFluid().getFluid());
        if (fluidId == null) {
            return;
        }
        ResourceLocation bucketId = ResourceLocation.parse(fluidId.getNamespace() + ":" + fluidId.getPath() + "_bucket");
        Item bucketItem = ForgeRegistries.ITEMS.getValue(bucketId);
        if (bucketItem == null) {
            return;
        }

        ItemStack out = new ItemStack(bucketItem);
        ItemStack remainder = inventory.insertItem(SLOT_CONTAINER_OUT, out, true);
        if (!remainder.isEmpty()) {
            return;
        }

        inventory.extractItem(SLOT_CONTAINER_IN, 1, false);
        fluidTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        inventory.insertItem(SLOT_CONTAINER_OUT, out, false);
        markAndSync();
    }

    private void tickManualPress() {
        if (level == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (pressingPlayer == null) {
            return;
        }
        JuicingConfig.Defaults defaults = getDefaults();
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(pressingPlayer);
        if (player == null || !isValidManualPress(player)) {
            if (defaults.manualInterruptedResetTicks() <= 0) {
                stopManualPress(true);
                return;
            }
            interruptedTicks++;
            if (interruptedTicks >= defaults.manualInterruptedResetTicks()) {
                stopManualPress(true);
            }
            return;
        }

        interruptedTicks = 0;
        if (!hasPulp()) {
            stopManualPress(true);
            return;
        }

        manualHoldProgress++;
        int targetTicks = getCurrentManualTargetTicks();
        if (manualHoldProgress < targetTicks) {
            markAndSync();
            return;
        }

        if (processOneStep(true)) {
            manualHoldProgress = 0;
            if (!hasPulp()) {
                stopManualPress(true);
            } else {
                markAndSync();
            }
            return;
        }

        manualHoldProgress = targetTicks;
        markAndSync();
    }

    private boolean isValidManualPress(ServerPlayer player) {
        if (!player.isShiftKeyDown()) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 25.0D;
    }

    private void stopManualPress(boolean resetProgress) {
        pressingPlayer = null;
        interruptedTicks = 0;
        if (resetProgress) {
            manualHoldProgress = 0;
        }
        markAndSync();
    }

    private void tickAutoProcessing() {
        if (level == null || !hasPulp()) {
            return;
        }
        autoTick++;
        if (autoTick < AUTO_INTERVAL) {
            return;
        }
        autoTick = 0;

        if (!hasCreatePower()) {
            return;
        }
        processOneStep(false);
    }

    private boolean hasCreatePower() {
        if (level == null || !ModList.get().isLoaded("create")) {
            return false;
        }

        int minSpeed = Math.max(1, getDefaults().createMinimumSpeed());
        for (Direction direction : Direction.values()) {
            BlockEntity adjacent = level.getBlockEntity(worldPosition.relative(direction));
            if (adjacent == null) {
                continue;
            }
            float speed = readCreateKineticSpeed(adjacent);
            if (Math.abs(speed) >= minSpeed) {
                return true;
            }

            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(adjacent.getBlockState().getBlock());
            if (blockId != null && "create".equals(blockId.getNamespace())) {
                return true;
            }
        }
        return false;
    }

    private float readCreateKineticSpeed(BlockEntity blockEntity) {
        if (!kineticReflectionReady) {
            try {
                kineticBlockEntityClass = Class.forName("com.simibubi.create.content.kinetics.base.KineticBlockEntity");
                kineticGetSpeedMethod = kineticBlockEntityClass.getMethod("getSpeed");
            } catch (Exception ignored) {
                kineticBlockEntityClass = null;
                kineticGetSpeedMethod = null;
            }
            kineticReflectionReady = true;
        }

        if (kineticBlockEntityClass == null || kineticGetSpeedMethod == null) {
            return 0.0F;
        }
        if (!kineticBlockEntityClass.isInstance(blockEntity)) {
            return 0.0F;
        }

        try {
            Object value = kineticGetSpeedMethod.invoke(blockEntity);
            if (value instanceof Number number) {
                return number.floatValue();
            }
        } catch (Exception ignored) {
            // Reflection failure should not break gameplay.
        }
        return 0.0F;
    }

    private boolean processOneStep(boolean manual) {
        if (!hasPulp() || level == null) {
            return false;
        }
        JuicingConfig.Entry entry = getCurrentEntry();
        if (entry == null) {
            return false;
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(entry.fluidId());
        if (fluid == null) {
            return false;
        }

        int stepMb = manual ? entry.manualFluidMb() : entry.autoStepMb();
        int toDrainFromPulp = Math.min(stepMb, pulpRemainingMb);
        if (toDrainFromPulp <= 0) {
            return false;
        }

        int filled = fluidTank.fill(new FluidStack(fluid, toDrainFromPulp), IFluidHandler.FluidAction.EXECUTE);
        if (filled <= 0) {
            return false;
        }

        pulpRemainingMb = Math.max(0, pulpRemainingMb - filled);
        RandomSource random = level.getRandom();
        List<ItemStack> drops = JuicingConfig.rollByproducts(entry, random);
        for (ItemStack drop : drops) {
            ItemStack remaining = inventory.insertItem(SLOT_BYPRODUCT, drop.copy(), false);
            if (!remaining.isEmpty()) {
                Block.popResource(level, worldPosition, remaining);
            }
        }

        if (pulpRemainingMb <= 0) {
            pulpRemainingMb = 0;
            pulpSourceFruitId = null;
            manualHoldProgress = 0;
        }
        markAndSync();
        return true;
    }

    private int getCurrentManualTargetTicks() {
        JuicingConfig.Entry entry = getCurrentEntry();
        return entry == null ? 1 : Math.max(1, entry.manualHoldTicks());
    }

    @Nullable
    private JuicingConfig.Entry getCurrentEntry() {
        if (pulpSourceFruitId == null) {
            return null;
        }
        return JuicingConfig.resolveByFruit(getResourceManagerSafe(), pulpSourceFruitId).orElse(null);
    }

    private boolean hasPulp() {
        return pulpSourceFruitId != null && pulpRemainingMb > 0;
    }

    private JuicingConfig.Defaults getDefaults() {
        return JuicingConfig.defaults(getResourceManagerSafe());
    }

    @Nullable
    private net.minecraft.server.packs.resources.ResourceManager getResourceManagerSafe() {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getServer().getResourceManager();
        }
        return null;
    }

    private Optional<JuicingConfig.Entry> resolveFruitProfile(ItemStack stack) {
        return JuicingConfig.resolveByFruit(getResourceManagerSafe(), stack);
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
        tag.put(INVENTORY_KEY, inventory.serializeNBT());
        tag.put(TANK_KEY, fluidTank.writeToNBT(new CompoundTag()));
        if (pulpSourceFruitId != null) {
            tag.putString(PULP_SOURCE_KEY, pulpSourceFruitId.toString());
        }
        tag.putInt(PULP_REMAINING_KEY, pulpRemainingMb);
        if (pressingPlayer != null) {
            tag.putUUID(PRESSING_PLAYER_KEY, pressingPlayer);
        }
        tag.putInt(HOLD_PROGRESS_KEY, manualHoldProgress);
        tag.putInt(INTERRUPTED_TICK_KEY, interruptedTicks);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound(INVENTORY_KEY));
        fluidTank.readFromNBT(tag.getCompound(TANK_KEY));
        if (tag.contains(PULP_SOURCE_KEY)) {
            pulpSourceFruitId = ResourceLocation.parse(tag.getString(PULP_SOURCE_KEY));
        } else {
            pulpSourceFruitId = null;
        }
        pulpRemainingMb = Math.max(0, tag.getInt(PULP_REMAINING_KEY));
        pressingPlayer = tag.hasUUID(PRESSING_PLAYER_KEY) ? tag.getUUID(PRESSING_PLAYER_KEY) : null;
        manualHoldProgress = Math.max(0, tag.getInt(HOLD_PROGRESS_KEY));
        interruptedTicks = Math.max(0, tag.getInt(INTERRUPTED_TICK_KEY));
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
        itemHandler.invalidate();
        fluidHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> inventory);
        fluidHandler = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block." + HorrorCooked9.MODID + ".juicer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new JuicerMenu(id, inventory, this);
    }
}
