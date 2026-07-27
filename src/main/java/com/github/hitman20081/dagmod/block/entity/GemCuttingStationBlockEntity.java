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
import net.minecraft.world.ContainerHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    private int maxProgress = 40;
    private int waterCharges = 0;

    private static final int BOTTLE_CHARGES = 8;
    private static final int BUCKET_CHARGES = 32;

    // Mapping of raw gems to their cut versions
    private static final Map<Item, Item> GEM_RECIPES = new HashMap<>();

    static {
        // Register gem cutting recipes (raw -> gem_cut_*)
        GEM_RECIPES.put(ModItems.RAW_CITRINE,    ModItems.GEM_CUT_CITRINE);
        GEM_RECIPES.put(ModItems.RAW_RUBY,       ModItems.GEM_CUT_RUBY);
        GEM_RECIPES.put(ModItems.RAW_SAPPHIRE,   ModItems.GEM_CUT_SAPPHIRE);
        GEM_RECIPES.put(ModItems.RAW_PINK_GARNET, ModItems.GEM_CUT_PINK_GARNET);
        GEM_RECIPES.put(ModItems.RAW_TANZANITE,  ModItems.GEM_CUT_TANZANITE);
        GEM_RECIPES.put(ModItems.RAW_TOPAZ,      ModItems.GEM_CUT_TOPAZ);
        GEM_RECIPES.put(ModItems.RAW_ZIRCON,     ModItems.GEM_CUT_ZIRCON);
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
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("waterCharges", waterCharges);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        waterCharges = tag.getIntOr("waterCharges", 0);
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

        if (outputItem == null) return;

        // Load water charges from the slot item when depleted
        if (waterCharges <= 0) {
            ItemStack waterStack = this.getItem(WATER_SLOT);
            if (waterStack.getItem() == Items.WATER_BUCKET) {
                waterCharges = BUCKET_CHARGES;
                if (waterStack.getCount() > 1) {
                    waterStack.shrink(1);
                } else {
                    this.setItem(WATER_SLOT, new ItemStack(Items.BUCKET));
                }
            } else {
                // Water bottle
                waterCharges = BOTTLE_CHARGES;
                if (waterStack.getCount() > 1) {
                    waterStack.shrink(1);
                } else {
                    this.setItem(WATER_SLOT, new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }
        waterCharges--;

        // Remove 1 raw gem
        this.removeItem(INPUT_SLOT, 1);

        // Damage the gem cutter tool
        ItemStack toolStack = this.getItem(TOOL_SLOT);
        if (toolStack.isDamageableItem()) {
            int currentDamage = toolStack.getDamageValue();
            int maxDamage = toolStack.getMaxDamage();
            if (currentDamage + 1 >= maxDamage) {
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

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack inputStack = this.getItem(INPUT_SLOT);
        ItemStack toolStack = this.getItem(TOOL_SLOT);

        boolean hasWater = waterCharges > 0 || hasWaterInSlot();
        boolean hasValidInput = GEM_RECIPES.containsKey(inputStack.getItem());
        boolean hasTool = !toolStack.isEmpty() && toolStack.getItem() == ModItems.GEM_CUTTER_TOOL;

        if (!hasWater || !hasValidInput || !hasTool) {
            return false;
        }

        Item outputItem = GEM_RECIPES.get(inputStack.getItem());
        return canInsertAmountIntoOutputSlot(new ItemStack(outputItem, 1))
                && canInsertItemIntoOutputSlot(outputItem);
    }

    private boolean hasWaterInSlot() {
        ItemStack waterStack = this.getItem(WATER_SLOT);
        if (waterStack.getItem() == Items.WATER_BUCKET) {
            return true;
        }
        if (waterStack.getItem() == Items.POTION) {
            PotionContents contents = waterStack.get(DataComponents.POTION_CONTENTS);
            return contents != null && contents.is(Potions.WATER);
        }
        return false;
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
