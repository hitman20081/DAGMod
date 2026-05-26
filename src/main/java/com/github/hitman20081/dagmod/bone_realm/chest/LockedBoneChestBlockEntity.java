package com.github.hitman20081.dagmod.bone_realm.chest;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Block Entity for Locked Bone Chests
 * Simple chest that can be locked/unlocked
 */
public class LockedBoneChestBlockEntity extends ChestBlockEntity {

    private boolean unlocked = false;
    private final LockedBoneChestBlock.LockedChestType chestType;

    public LockedBoneChestBlockEntity(BlockPos pos, BlockState state, LockedBoneChestBlock.LockedChestType type) {
        super(BoneRealmChestRegistry.LOCKED_BONE_CHEST_ENTITY, pos, state);
        this.chestType = type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlock() {
        this.unlocked = true;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putBoolean("Unlocked", this.unlocked);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.unlocked = view.getBooleanOr("Unlocked", false);
        // Pre-placed structure chests are saved without a LootTable tag.
        // Auto-assign it here so they populate correctly on first open.
        // Only applies to unopened chests — once unlocked, vanilla clears the loot
        // table and stores items in inventory slots instead.
        if (chestType == LockedBoneChestBlock.LockedChestType.SKELETON_KING && !this.unlocked) {
            this.setLootTable(
                net.minecraft.resources.ResourceKey.create(
                    net.minecraft.core.registries.Registries.LOOT_TABLE,
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("dagmod", "chests/skeleton_king_chest")
                )
            );
            this.setLootTableSeed(this.worldPosition.asLong());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (!unlocked) {
            return false;
        }
        return super.stillValid(player);
    }

    public LockedBoneChestBlock.LockedChestType getChestType() {
        return chestType;
    }
}