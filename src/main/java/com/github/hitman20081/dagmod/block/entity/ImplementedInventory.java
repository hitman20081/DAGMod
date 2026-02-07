package com.github.hitman20081.dagmod.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@code SidedInventory} implementation with only default methods + an item list getter.
 */
@FunctionalInterface
public interface ImplementedInventory extends SidedInventory {
    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    DefaultedList<ItemStack> getItems();

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        stack.capCount(getMaxCount(stack));
        markDirty();
    }

    @Override
    default void clear() {
        getItems().clear();
    }

    @Override
    default void markDirty() {
        // Override if needed
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        int[] result = new int[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
