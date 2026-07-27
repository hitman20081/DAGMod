package com.github.hitman20081.dagmod.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

/**
 * Iron Chest Block Entity - 27 slot storage with chest rendering
 * Extends ChestBlockEntity for proper chest animation and rendering
 */
public class IronChestBlockEntity extends ChestBlockEntity {

    public IronChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRON_CHEST, pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.dagmod.iron_chest");
    }
}
