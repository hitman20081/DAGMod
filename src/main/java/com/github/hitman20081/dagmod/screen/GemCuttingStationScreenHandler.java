package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.GemCuttingStationBlockEntity;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.BlockPos;

public class GemCuttingStationScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    public final GemCuttingStationBlockEntity blockEntity;

    // Client constructor
    public GemCuttingStationScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    // Server constructor
    public GemCuttingStationScreenHandler(int syncId, Inventory playerInventory, BlockEntity blockEntity, ContainerData propertyDelegate) {
        super(ModScreenHandlers.GEM_CUTTING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = ((Container) blockEntity);
        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((GemCuttingStationBlockEntity) blockEntity);

        checkContainerSize(inventory, 4);

        // Water bottle slot (left side)
        this.addSlot(new Slot(inventory, GemCuttingStationBlockEntity.WATER_SLOT, 34, 40) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.POTION; // Only potions (water bottles)
            }
        });

        // Raw gem input slot (top middle)
        this.addSlot(new Slot(inventory, GemCuttingStationBlockEntity.INPUT_SLOT, 57, 18));

        // Gem cutter tool slot (top right)
        this.addSlot(new Slot(inventory, GemCuttingStationBlockEntity.TOOL_SLOT, 103, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == ModItems.GEM_CUTTER_TOOL;
            }
        });

        // Output slot (bottom center) - can't insert items
        this.addSlot(new Slot(inventory, GemCuttingStationBlockEntity.OUTPUT_SLOT, 80, 60) {
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
            } else {
                // Moving from player inventory to block inventory
                // Try to insert into appropriate slot based on item type
                if (originalStack.getItem() == Items.POTION) {
                    // Water bottles go to water slot
                    if (!this.moveItemStackTo(originalStack, GemCuttingStationBlockEntity.WATER_SLOT, GemCuttingStationBlockEntity.WATER_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.getItem() == ModItems.GEM_CUTTER_TOOL) {
                    // Gem cutter tool goes to tool slot
                    if (!this.moveItemStackTo(originalStack, GemCuttingStationBlockEntity.TOOL_SLOT, GemCuttingStationBlockEntity.TOOL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // Everything else tries to go into input slot
                    if (!this.moveItemStackTo(originalStack, GemCuttingStationBlockEntity.INPUT_SLOT, GemCuttingStationBlockEntity.INPUT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
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
