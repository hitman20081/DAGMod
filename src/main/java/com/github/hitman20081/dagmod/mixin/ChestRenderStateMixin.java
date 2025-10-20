package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.accessor.ChestRenderStateAccessor;
import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlock;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChestBlockEntityRenderState.class)
public class ChestRenderStateMixin implements ChestRenderStateAccessor {

    @Unique
    private LockedBoneChestBlock.LockedChestType dagmod$customChestType;

    @Override
    public void dagmod$setCustomChestType(LockedBoneChestBlock.LockedChestType type) {
        this.dagmod$customChestType = type;
    }

    @Override
    public LockedBoneChestBlock.LockedChestType dagmod$getCustomChestType() {
        return this.dagmod$customChestType;
    }
}