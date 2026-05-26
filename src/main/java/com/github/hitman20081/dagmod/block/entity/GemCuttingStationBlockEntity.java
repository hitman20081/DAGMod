package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.screen.GemCuttingStationScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GemCuttingStationBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos>, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    public static final int WATER_SLOT = 0;     // Water bottle input
    public static final int INPUT_SLOT = 1;      // Raw gem input
    public static final int TOOL_SLOT = 2;       // Gem cutter tool
    public static final int OUTPUT_SLOT = 3;     // Finished gem output

    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress = 40; // 2 seconds at 20 ticks/second

    // Mapping of raw gems to their cut versions
    private static final Map<Item, Item> GEM_RECIPES = new HashMap<>();

    static {
        // Register gem cutting recipes (raw -> cut)
        GEM_RECIPES.put(ModItems.RAW_CITRINE, ModItems.CITRINE);
        GEM_RECIPES.put(ModItems.RAW_RUBY, ModItems.RUBY);
        GEM_RECIPES.put(ModItems.RAW_SAPPHIRE, ModItems.SAPPHIRE);
        GEM_RECIPES.put(ModItems.RAW_PINK_GARNET, ModItems.PINK_GARNET);
        GEM_RECIPES.put(ModItems.RAW_TANZANITE, ModItems.TANZANITE);
        GEM_RECIPES.put(ModItems.RAW_TOPAZ, ModItems.TOPAZ);
        GEM_RECIPES.put(ModItems.RAW_ZIRCON, ModItems.ZIRCON);
    }

    public GemCuttingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_CUTTING_STATION, pos, state);
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemCuttingStationBlockEntity.this.progress;
                    case 1 -> GemCuttingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GemCuttingStationBlockEntity.this.progress = value;
                    case 1 -> GemCuttingStationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void setChanged() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        super.setChanged();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.worldPosition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.dagmod.gem_cutting_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GemCuttingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.hasRecipe()) {
                this.increaseCraftProgress();
                BlockEntity.setChanged(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                    // Play crafting complete sound
                    world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            BlockEntity.setChanged(world, pos, state);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, GemCuttingStationBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        ItemStack inputStack = this.getItem(INPUT_SLOT);
        Item outputItem = GEM_RECIPES.get(inputStack.getItem());

        if (outputItem != null) {
            // Remove 1 raw gem
            this.removeItem(INPUT_SLOT, 1);

            // Remove 1 water bottle, leave empty bottle
            ItemStack waterStack = this.getItem(WATER_SLOT);
            if (waterStack.getCount() > 1) {
                waterStack.shrink(1);
            } else {
                this.setItem(WATER_SLOT, new ItemStack(Items.GLASS_BOTTLE));
            }

            // Damage the gem cutter tool
            ItemStack toolStack = this.getItem(TOOL_SLOT);
            if (toolStack.isDamageableItem()) {
                int currentDamage = toolStack.getDamageValue();
                int maxDamage = toolStack.getMaxDamage();
                if (currentDamage + 1 >= maxDamage) {
                    // Tool breaks
                    this.setItem(TOOL_SLOT, ItemStack.EMPTY);
                } else {
                    toolStack.setDamageValue(currentDamage + 1);
                }
            }

            // Add output
            ItemStack outputStack = this.getItem(OUTPUT_SLOT);
            if (outputStack.isEmpty()) {
                this.setItem(OUTPUT_SLOT, new ItemStack(outputItem, 1));
            } else {
                outputStack.grow(1);
            }
        }
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack waterStack = this.getItem(WATER_SLOT);
        ItemStack inputStack = this.getItem(INPUT_SLOT);
        ItemStack toolStack = this.getItem(TOOL_SLOT);

        // Check for water bottle (potion with water)
        boolean hasWater = false;
        if (waterStack.getItem() == Items.POTION) {
            PotionContents contents = waterStack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER)) {
                hasWater = true;
            }
        }

        // Check for valid raw gem input
        boolean hasValidInput = GEM_RECIPES.containsKey(inputStack.getItem());

        // Check for gem cutter tool
        boolean hasTool = toolStack.getItem() == ModItems.GEM_CUTTER_TOOL && !toolStack.isEmpty();

        if (!hasWater || !hasValidInput || !hasTool) {
            return false;
        }

        // Check if output can receive the result
        Item outputItem = GEM_RECIPES.get(inputStack.getItem());
        return canInsertAmountIntoOutputSlot(new ItemStack(outputItem, 1))
                && canInsertItemIntoOutputSlot(outputItem);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getItem(OUTPUT_SLOT).getItem() == item || this.getItem(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        ItemStack outputStack = this.getItem(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return true;
        }
        return outputStack.getCount() + result.getCount() <= outputStack.getItem().getDefaultMaxStackSize();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getItem(OUTPUT_SLOT).isEmpty() || this.getItem(OUTPUT_SLOT).getCount() < this.getItem(OUTPUT_SLOT).getItem().getDefaultMaxStackSize();
    }
}
