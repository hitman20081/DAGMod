package com.github.hitman20081.dagmod.bone_realm.chest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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