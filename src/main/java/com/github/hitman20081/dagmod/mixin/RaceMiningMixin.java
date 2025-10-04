package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.race_system.DwarfMiningHandler;
import com.github.hitman20081.dagmod.race_system.ElfGatheringHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.block.Block.class)
public class RaceMiningMixin {

    @Inject(
            method = "onBreak",
            at = @At("HEAD")
    )
    private void onBlockBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        // Only run on server side
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Handle Dwarf mining bonuses
            DwarfMiningHandler.handleDwarfMining(serverPlayer, state, pos, serverWorld);

            // Handle Elf gathering bonuses
            ElfGatheringHandler.handleElfGathering(serverPlayer, state, pos, serverWorld);
        }
    }
}