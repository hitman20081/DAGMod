package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.block.entity.GemCrushingStationBlockEntity;
import com.github.hitman20081.dagmod.item.ModItems;
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

public class GemCrushingStationScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    public final GemCrushingStationBlockEntity blockEntity;

    // Client constructor
    public GemCrushingStationScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    // Server constructor
    public GemCrushingStationScreenHandler(int syncId, Inventory playerInventory, BlockEntity blockEntity, ContainerData propertyDelegate) {
        super(ModScreenHandlers.GEM_CRUSHING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = ((Container) blockEntity);
        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((GemCrushingStationBlockEntity) blockEntity);

        checkContainerSize(inventory, 3);

        // Crushing Hammer slot (left side) — only accepts the Crushing Hammer
        this.addSlot(new Slot(inventory, GemCrushingStationBlockEntity.HAMMER_SLOT, 12, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == ModItems.CRUSHING_HAMMER;
            }
        });

        // Input slot (right top)
        this.addSlot(new Slot(inventory, GemCrushingStationBlockEntity.INPUT_SLOT, 86, 15));

        // Output slot (right bottom)
        this.addSlot(new Slot(inventory, GemCrushingStationBlockEntity.OUTPUT_SLOT, 86, 60) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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
        int progressArrowSize = 26;

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
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
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
