package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.recipe.GemPolishingRecipe;
import com.github.hitman20081.dagmod.screen.GemPolishingStationScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.Map;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.CraftingInput;
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

public class GemPolishingStationBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos>, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public static final int CATALYST_SLOT = 0;  // Diamond powder
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    // Charges granted per diamond powder, keyed by input gem tier
    private static final Map<Item, Integer> TIER_CHARGES = Map.ofEntries(
        Map.entry(ModItems.GEM_CUT_CITRINE,         4),
        Map.entry(ModItems.GEM_CUT_RUBY,            4),
        Map.entry(ModItems.GEM_CUT_SAPPHIRE,        4),
        Map.entry(ModItems.GEM_CUT_TANZANITE,       4),
        Map.entry(ModItems.GEM_CUT_TOPAZ,           4),
        Map.entry(ModItems.GEM_CUT_ZIRCON,          4),
        Map.entry(ModItems.GEM_CUT_PINK_GARNET,     4),
        Map.entry(ModItems.GEM_POLISHED_CITRINE,    2),
        Map.entry(ModItems.GEM_POLISHED_RUBY,       2),
        Map.entry(ModItems.GEM_POLISHED_SAPPHIRE,   2),
        Map.entry(ModItems.GEM_POLISHED_TANZANITE,  2),
        Map.entry(ModItems.GEM_POLISHED_TOPAZ,      2),
        Map.entry(ModItems.GEM_POLISHED_ZIRCON,     2),
        Map.entry(ModItems.GEM_POLISHED_PINK_GARNET, 2),
        Map.entry(ModItems.GEM_FLAWLESS_CITRINE,    1),
        Map.entry(ModItems.GEM_FLAWLESS_RUBY,       1),
        Map.entry(ModItems.GEM_FLAWLESS_SAPPHIRE,   1),
        Map.entry(ModItems.GEM_FLAWLESS_TANZANITE,  1),
        Map.entry(ModItems.GEM_FLAWLESS_TOPAZ,      1),
        Map.entry(ModItems.GEM_FLAWLESS_ZIRCON,     1),
        Map.entry(ModItems.GEM_FLAWLESS_PINK_GARNET, 1)
    );

    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    private int powderCharges = 0;

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
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("powderCharges", powderCharges);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        powderCharges = tag.getIntOr("powderCharges", 0);
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

        // Load charges from catalyst slot when depleted — amount depends on input gem tier
        if (powderCharges <= 0) {
            ItemStack catalystStack = this.getItem(CATALYST_SLOT);
            powderCharges = TIER_CHARGES.getOrDefault(this.getItem(INPUT_SLOT).getItem(), 1);
            if (catalystStack.getCount() > 1) {
                catalystStack.shrink(1);
            } else {
                this.setItem(CATALYST_SLOT, ItemStack.EMPTY);
            }
        }
        powderCharges--;

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
        boolean hasCatalyst = powderCharges > 0 || !this.getItem(CATALYST_SLOT).isEmpty()
                && this.getItem(CATALYST_SLOT).is(ModItems.DIAMOND_POWDER);

        Optional<RecipeHolder<GemPolishingRecipe>> recipe = getCurrentRecipe();
        return hasCatalyst && recipe.isPresent()
                && canInsertAmountIntoOutputSlot(recipe.get().value().result())
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
            return true;
        }
        return outputStack.getCount() + result.getCount() <= outputStack.getItem().getDefaultMaxStackSize();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getItem(OUTPUT_SLOT).isEmpty() || this.getItem(OUTPUT_SLOT).getCount() < this.getItem(OUTPUT_SLOT).getItem().getDefaultMaxStackSize();
    }
}
