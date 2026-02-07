package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.GemInfusingStationBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class GemInfusingStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final GemInfusingStationBlockEntity blockEntity;

    // Client constructor
    public GemInfusingStationScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getEntityWorld().getBlockEntity(pos), new ArrayPropertyDelegate(2));
    }

    // Server constructor
    public GemInfusingStationScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.GEM_INFUSING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);
        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((GemInfusingStationBlockEntity) blockEntity);

        checkSize(inventory, 3);

        // Catalyst/fuel slot (left side)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.CATALYST_SLOT, 12, 15));

        // Input slot (right top)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.INPUT_SLOT, 86, 15));

        // Output slot (right bottom)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.OUTPUT_SLOT, 86, 60) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output slot can't be inserted into
            }
        });

        // Add player inventory
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int progressArrowSize = 26; // Width in pixels of progress arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < this.inventory.size()) {
                // Moving from block inventory to player inventory
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                // Moving from player inventory to block inventory
                return ItemStack.EMPTY;
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

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
