package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.GemInfusingStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.BlockPos;

public class GemInfusingStationScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    public final GemInfusingStationBlockEntity blockEntity;

    // Client constructor
    public GemInfusingStationScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    // Server constructor
    public GemInfusingStationScreenHandler(int syncId, Inventory playerInventory, BlockEntity blockEntity, ContainerData propertyDelegate) {
        super(ModScreenHandlers.GEM_INFUSING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = ((Container) blockEntity);
        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((GemInfusingStationBlockEntity) blockEntity);

        checkContainerSize(inventory, 3);

        // Catalyst/fuel slot (left side)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.CATALYST_SLOT, 12, 15));

        // Input slot (right top)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.INPUT_SLOT, 86, 15));

        // Output slot (right bottom)
        this.addSlot(new Slot(inventory, GemInfusingStationBlockEntity.OUTPUT_SLOT, 86, 60) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output slot can't be inserted into
            }
        });

        // Add player inventory
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addDataSlots(propertyDelegate);
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
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot < this.inventory.getContainerSize()) {
                // Moving from block inventory to player inventory
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                // Moving from player inventory to block inventory
                return ItemStack.EMPTY;
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

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
