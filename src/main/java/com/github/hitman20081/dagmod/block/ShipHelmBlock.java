package com.github.hitman20081.dagmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.github.hitman20081.dagmod.networking.ShipTravelMenuPacket;

public class ShipHelmBlock extends Block {

    public ShipHelmBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            // Open the ship travel GUI for the player
            ShipTravelMenuPacket.openTravelMenu(serverPlayer);
        }

        return ActionResult.SUCCESS;
    }
}