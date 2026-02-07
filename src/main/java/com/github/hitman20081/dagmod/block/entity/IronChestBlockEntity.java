package com.github.hitman20081.dagmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * Iron Chest Block Entity - 27 slot storage with chest rendering
 * Extends ChestBlockEntity for proper chest animation and rendering
 */
public class IronChestBlockEntity extends ChestBlockEntity {

    public IronChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRON_CHEST, pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.dagmod.iron_chest");
    }
}
