package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.race_system.DwarfMiningHandler;
import com.github.hitman20081.dagmod.race_system.ElfGatheringHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.block.Block.class)
public class RaceMiningMixin {

    @Inject(
            method = "playerWillDestroy",
            at = @At("HEAD")
    )
    private void onBlockBreak(Level world, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<BlockState> cir) {
        // Only run on server side
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ServerLevel serverWorld = (ServerLevel) world;

            // Handle Dwarf mining bonuses
            DwarfMiningHandler.handleDwarfMining(serverPlayer, state, pos, serverWorld);

            // Handle Elf gathering bonuses
            ElfGatheringHandler.handleElfGathering(serverPlayer, state, pos, serverWorld);
        }
    }
}