package net.qiuyu.horrorcooked9.gameplay.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class MultiUseFoodData {
    public static final String TAG_TOTAL_USES = "horrorcooked9_food_uses";
    public static final String TAG_REMAINING_USES = "horrorcooked9_food_uses_remaining";

    private MultiUseFoodData() {
    }

    public static int sanitizeUses(int uses) {
        return Math.max(1, uses);
    }

    public static void initialize(ItemStack stack, int uses) {
        if (stack.isEmpty()) {
            return;
        }
        int validUses = sanitizeUses(uses);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_TOTAL_USES, validUses);
        tag.putInt(TAG_REMAINING_USES, validUses);
    }

    public static void ensureInitialized(ItemStack stack, int fallbackUses) {
        if (stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        int total = sanitizeUses(tag.contains(TAG_TOTAL_USES) ? tag.getInt(TAG_TOTAL_USES) : fallbackUses);
        int remaining = tag.contains(TAG_REMAINING_USES) ? tag.getInt(TAG_REMAINING_USES) : total;
        if (remaining < 0) {
            remaining = 0;
        }
        if (remaining > total) {
            remaining = total;
        }
        tag.putInt(TAG_TOTAL_USES, total);
        tag.putInt(TAG_REMAINING_USES, remaining);
    }

    public static int getTotalUses(ItemStack stack, int fallbackUses) {
        if (stack.isEmpty()) {
            return sanitizeUses(fallbackUses);
        }
        ensureInitialized(stack, fallbackUses);
        return sanitizeUses(stack.getOrCreateTag().getInt(TAG_TOTAL_USES));
    }

    public static int getRemainingUses(ItemStack stack, int fallbackUses) {
        if (stack.isEmpty()) {
            return 0;
        }
        ensureInitialized(stack, fallbackUses);
        return Math.max(0, stack.getOrCreateTag().getInt(TAG_REMAINING_USES));
    }

    public static int consumeOne(ItemStack stack, int fallbackUses) {
        if (stack.isEmpty()) {
            return 0;
        }
        ensureInitialized(stack, fallbackUses);
        CompoundTag tag = stack.getOrCreateTag();
        int remaining = Math.max(0, tag.getInt(TAG_REMAINING_USES) - 1);
        tag.putInt(TAG_REMAINING_USES, remaining);
        return remaining;
    }
}
