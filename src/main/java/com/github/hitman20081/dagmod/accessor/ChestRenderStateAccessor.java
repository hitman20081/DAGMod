package com.github.hitman20081.dagmod.accessor;

import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlock;

public interface ChestRenderStateAccessor {
    void dagmod$setCustomChestType(LockedBoneChestBlock.LockedChestType type);
    LockedBoneChestBlock.LockedChestType dagmod$getCustomChestType();
}