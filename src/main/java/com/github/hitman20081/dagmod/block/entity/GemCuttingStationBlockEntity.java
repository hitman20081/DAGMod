package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.screen.GemCuttingStationScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GemCuttingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    public static final int WATER_SLOT = 0;     // Water bottle input
    public static final int INPUT_SLOT = 1;      // Raw gem input
    public static final int TOOL_SLOT = 2;       // Gem cutter tool
    public static final int OUTPUT_SLOT = 3;     // Finished gem output

    protected final PropertyDelegate propertyDelegate;
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
        this.propertyDelegate = new PropertyDelegate() {
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
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void markDirty() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        super.markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.dagmod.gem_cutting_station");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GemCuttingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.hasRecipe()) {
                this.increaseCraftProgress();
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                    // Play crafting complete sound
                    world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, GemCuttingStationBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        ItemStack inputStack = this.getStack(INPUT_SLOT);
        Item outputItem = GEM_RECIPES.get(inputStack.getItem());

        if (outputItem != null) {
            // Remove 1 raw gem
            this.removeStack(INPUT_SLOT, 1);

            // Remove 1 water bottle, leave empty bottle
            ItemStack waterStack = this.getStack(WATER_SLOT);
            if (waterStack.getCount() > 1) {
                waterStack.decrement(1);
            } else {
                this.setStack(WATER_SLOT, new ItemStack(Items.GLASS_BOTTLE));
            }

            // Damage the gem cutter tool
            ItemStack toolStack = this.getStack(TOOL_SLOT);
            if (toolStack.isDamageable()) {
                int currentDamage = toolStack.getDamage();
                int maxDamage = toolStack.getMaxDamage();
                if (currentDamage + 1 >= maxDamage) {
                    // Tool breaks
                    this.setStack(TOOL_SLOT, ItemStack.EMPTY);
                } else {
                    toolStack.setDamage(currentDamage + 1);
                }
            }

            // Add output
            ItemStack outputStack = this.getStack(OUTPUT_SLOT);
            if (outputStack.isEmpty()) {
                this.setStack(OUTPUT_SLOT, new ItemStack(outputItem, 1));
            } else {
                outputStack.increment(1);
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
        ItemStack waterStack = this.getStack(WATER_SLOT);
        ItemStack inputStack = this.getStack(INPUT_SLOT);
        ItemStack toolStack = this.getStack(TOOL_SLOT);

        // Check for water bottle (potion with water)
        boolean hasWater = false;
        if (waterStack.getItem() == Items.POTION) {
            PotionContentsComponent contents = waterStack.get(DataComponentTypes.POTION_CONTENTS);
            if (contents != null && contents.matches(Potions.WATER)) {
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
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        ItemStack outputStack = this.getStack(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return true;
        }
        return outputStack.getCount() + result.getCount() <= outputStack.getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}
