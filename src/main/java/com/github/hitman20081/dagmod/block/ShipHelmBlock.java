package com.github.hitman20081.dagmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.github.hitman20081.dagmod.networking.ShipTravelMenuPacket;

public class ShipHelmBlock extends Block {

    public ShipHelmBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
                                 Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            // Open the ship travel GUI for the player
            ShipTravelMenuPacket.openTravelMenu(serverPlayer);
        }

        return InteractionResult.SUCCESS;
    }
}