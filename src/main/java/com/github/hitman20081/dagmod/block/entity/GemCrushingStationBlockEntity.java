package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.recipe.GemCrushingRecipe;
import com.github.hitman20081.dagmod.recipe.ModRecipes;
import com.github.hitman20081.dagmod.screen.GemCrushingStationScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GemCrushingStationBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos>, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public static final int HAMMER_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public GemCrushingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_CRUSHING_STATION, pos, state);
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemCrushingStationBlockEntity.this.progress;
                    case 1 -> GemCrushingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GemCrushingStationBlockEntity.this.progress = value;
                    case 1 -> GemCrushingStationBlockEntity.this.maxProgress = value;
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
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        ContainerHelper.loadAllItems(tag, inventory);
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
        return Component.translatable("block.dagmod.gem_crushing_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GemCrushingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            BlockEntity.setChanged(world, pos, state);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, GemCrushingStationBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        Optional<RecipeHolder<GemCrushingRecipe>> recipe = getCurrentRecipe();

        this.removeItem(INPUT_SLOT, 1);

        // Damage the crushing hammer
        ItemStack hammerStack = this.getItem(HAMMER_SLOT);
        if (hammerStack.isDamageableItem()) {
            int newDamage = hammerStack.getDamageValue() + 1;
            if (newDamage >= hammerStack.getMaxDamage()) {
                this.setItem(HAMMER_SLOT, ItemStack.EMPTY);
            } else {
                hammerStack.setDamageValue(newDamage);
            }
        }

        this.setItem(OUTPUT_SLOT, new ItemStack(recipe.get().value().result().getItem(),
                getItem(OUTPUT_SLOT).getCount() + recipe.get().value().result().getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        // Crushing Hammer must be present (it is not consumed)
        ItemStack hammerStack = this.getItem(HAMMER_SLOT);
        if (hammerStack.isEmpty() || hammerStack.getItem() != ModItems.CRUSHING_HAMMER) {
            return false;
        }

        Optional<RecipeHolder<GemCrushingRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent()
                && canInsertAmountIntoOutputSlot(recipe.get().value().result())
                && canInsertItemIntoOutputSlot(recipe.get().value().result().getItem());
    }

    private Optional<RecipeHolder<GemCrushingRecipe>> getCurrentRecipe() {
        if (this.getLevel() == null || !(this.getLevel() instanceof ServerLevel)) {
            return Optional.empty();
        }

        CraftingInput input = CraftingInput.of(1, 1, java.util.List.of(this.getItem(INPUT_SLOT)));
        return ((ServerLevel) this.getLevel()).getServer().getRecipeManager()
                .getRecipeFor(ModRecipes.GEM_CRUSHING_TYPE, input, this.getLevel());
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
