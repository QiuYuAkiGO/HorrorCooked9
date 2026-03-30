package net.qiuyu.horrorcooked9.gameplay.juicing;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import net.qiuyu.horrorcooked9.blocks.custom.JuicerBlockEntity;
import net.qiuyu.horrorcooked9.register.ModMenus;
import org.jetbrains.annotations.NotNull;

public class JuicerMenu extends AbstractContainerMenu {
    private final JuicerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public JuicerMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, getJuicerEntity(inventory, buffer));
    }

    public JuicerMenu(int id, Inventory inventory, JuicerBlockEntity blockEntity) {
        super(ModMenus.JUICER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.data = blockEntity.getContainerData();

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 26, 17));
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 1, 26, 53));
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 2, 134, 53));
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 3, 134, 17));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    private static JuicerBlockEntity getJuicerEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (entity instanceof JuicerBlockEntity juicer) {
            return juicer;
        }
        throw new IllegalStateException("Expected juicer block entity at menu position");
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();
        int machineSlots = 4;
        if (index < machineSlots) {
            if (!moveItemStackTo(source, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(source, 0, machineSlots, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, source);
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, blockEntity.getBlockState().getBlock());
    }

    public int getFluidAmount() {
        return data.get(0);
    }

    public int getTankCapacity() {
        return blockEntity.getTank().getCapacity();
    }

    public int getPulpRemainingMb() {
        return data.get(1);
    }

    public int getManualProgressTicks() {
        return data.get(2);
    }

    public int getManualTargetTicks() {
        return Math.max(1, data.get(3));
    }

    public JuicerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public Level getLevel() {
        return level;
    }
}
