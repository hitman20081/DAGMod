package com.github.hitman20081.dagmod.bone_realm.chest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

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
        this.markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putBoolean("Unlocked", this.unlocked);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.unlocked = view.getBoolean("Unlocked", false);
        // Pre-placed structure chests are saved without a LootTable tag.
        // Auto-assign it here so they populate correctly on first open.
        // Only applies to unopened chests — once unlocked, vanilla clears the loot
        // table and stores items in inventory slots instead.
        if (chestType == LockedBoneChestBlock.LockedChestType.SKELETON_KING && !this.unlocked) {
            this.setLootTable(
                net.minecraft.registry.RegistryKey.of(
                    net.minecraft.registry.RegistryKeys.LOOT_TABLE,
                    net.minecraft.util.Identifier.of("dagmod", "chests/skeleton_king_chest")
                ),
                this.pos.asLong()
            );
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (!unlocked) {
            return false;
        }
        return super.canPlayerUse(player);
    }

    public LockedBoneChestBlock.LockedChestType getChestType() {
        return chestType;
    }
}