package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.IronChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.BlockPos;

/**
 * Iron Chest Screen Handler - 54 slots (6 rows of 9)
 * Similar to a double chest layout
 */
public class IronChestScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    public final IronChestBlockEntity blockEntity;

    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    public static final int SIZE = ROWS * COLUMNS; // 54 slots

    // Client constructor
    public IronChestScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(pos));
    }

    // Server constructor
    public IronChestScreenHandler(int syncId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.IRON_CHEST_SCREEN_HANDLER, syncId);
        this.inventory = ((Container) blockEntity);
        this.blockEntity = ((IronChestBlockEntity) blockEntity);

        checkContainerSize(inventory, SIZE);

        // Add chest inventory slots (6 rows of 9)
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                this.addSlot(new Slot(inventory, col + row * COLUMNS, 8 + col * 18, 18 + row * 18));
            }
        }

        // Add player inventory (below chest inventory)
        int playerInvY = 18 + ROWS * 18 + 14; // 14 pixel gap
        addPlayerInventory(playerInventory, playerInvY);
        addPlayerHotbar(playerInventory, playerInvY + 58);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot < SIZE) {
                // Moving from chest to player inventory
                if (!this.moveItemStackTo(originalStack, SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to chest
                if (!this.moveItemStackTo(originalStack, 0, SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    private void addPlayerInventory(Inventory playerInventory, int startY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, startY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory, int startY) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, startY));
        }
    }
}
