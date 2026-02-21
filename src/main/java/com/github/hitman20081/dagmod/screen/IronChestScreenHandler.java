package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.IronChestBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * Iron Chest Screen Handler - 54 slots (6 rows of 9)
 * Similar to a double chest layout
 */
public class IronChestScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public final IronChestBlockEntity blockEntity;

    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    public static final int SIZE = ROWS * COLUMNS; // 54 slots

    // Client constructor
    public IronChestScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getEntityWorld().getBlockEntity(pos));
    }

    // Server constructor
    public IronChestScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.IRON_CHEST_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);
        this.blockEntity = ((IronChestBlockEntity) blockEntity);

        checkSize(inventory, SIZE);

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
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < SIZE) {
                // Moving from chest to player inventory
                if (!this.insertItem(originalStack, SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to chest
                if (!this.insertItem(originalStack, 0, SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory, int startY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, startY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory, int startY) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, startY));
        }
    }
}
