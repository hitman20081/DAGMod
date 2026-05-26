package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.recipe.GemPolishingRecipe;
import com.github.hitman20081.dagmod.screen.GemPolishingStationScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GemPolishingStationBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos>, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public GemPolishingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_POLISHING_STATION, pos, state);
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemPolishingStationBlockEntity.this.progress;
                    case 1 -> GemPolishingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GemPolishingStationBlockEntity.this.progress = value;
                    case 1 -> GemPolishingStationBlockEntity.this.maxProgress = value;
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
        return Component.translatable("block.dagmod.gem_polishing_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GemPolishingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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

    public static void tick(Level world, BlockPos pos, BlockState state, GemPolishingStationBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        Optional<RecipeHolder<GemPolishingRecipe>> recipe = getCurrentRecipe();

        this.removeItem(INPUT_SLOT, 1);

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
        Optional<RecipeHolder<GemPolishingRecipe>> recipe = getCurrentRecipe();

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(recipe.get().value().result())
                && canInsertItemIntoOutputSlot(recipe.get().value().result().getItem());
    }

    private Optional<RecipeHolder<GemPolishingRecipe>> getCurrentRecipe() {
        if (this.getLevel() == null || !(this.getLevel() instanceof ServerLevel)) {
            return Optional.empty();
        }

        CraftingInput input = CraftingInput.of(1, 1, java.util.List.of(this.getItem(INPUT_SLOT)));
        return ((ServerLevel) this.getLevel()).getServer().getRecipeManager()
                .getRecipeFor(com.github.hitman20081.dagmod.recipe.ModRecipes.GEM_POLISHING_TYPE, input, this.getLevel());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getItem(OUTPUT_SLOT).getItem() == item || this.getItem(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        ItemStack outputStack = this.getItem(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return true; // Empty slot can always receive items
        }
        return outputStack.getCount() + result.getCount() <= outputStack.getItem().getDefaultMaxStackSize();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getItem(OUTPUT_SLOT).isEmpty() || this.getItem(OUTPUT_SLOT).getCount() < this.getItem(OUTPUT_SLOT).getItem().getDefaultMaxStackSize();
    }
}
