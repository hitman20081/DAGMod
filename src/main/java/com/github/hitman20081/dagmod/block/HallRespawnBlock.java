package com.github.hitman20081.dagmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import java.util.concurrent.ThreadLocalRandom;

public class HallRespawnBlock extends Block {

    private static final int MIN_OFFSET = 5;
    private static final int MAX_OFFSET = 10;

    public HallRespawnBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
                                 Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        // Pick a random safe position 5-10 blocks from the respawn block
        BlockPos spawnPos = findSafeRandomPos(world, pos);

        serverPlayer.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(world.dimension(), spawnPos, serverPlayer.getYRot(), 0f), true), true);

        // Feedback
        serverPlayer.sendSystemMessage(
                Component.literal("Respawn point set at the Hall of Champions!")
                        .withStyle(ChatFormatting.GOLD));

        // Effects
        ServerLevel serverWorld = (ServerLevel) world;
        serverWorld.playSound(null, pos, SoundEvents.PLAYER_LEVELUP,
                SoundSource.BLOCKS, 1.0f, 1.0f);
        serverWorld.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.1);

        return InteractionResult.SUCCESS;
    }

    /**
     * Finds a random safe position (2 blocks of air) within 5-10 blocks of the given position.
     * Tries up to 20 random positions, falls back to directly above the block if none found.
     */
    private BlockPos findSafeRandomPos(Level world, BlockPos center) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < 20; attempt++) {
            int dx = rand.nextInt(MIN_OFFSET, MAX_OFFSET + 1) * (rand.nextBoolean() ? 1 : -1);
            int dz = rand.nextInt(MIN_OFFSET, MAX_OFFSET + 1) * (rand.nextBoolean() ? 1 : -1);
            BlockPos candidate = center.offset(dx, 0, dz);

            // Scan vertically near the block's Y level to find ground with 2 air blocks above
            for (int dy = -3; dy <= 3; dy++) {
                BlockPos ground = candidate.offset(0, dy, 0);
                BlockPos feet = ground.above();
                BlockPos head = ground.above(2);

                if (!world.getBlockState(ground).isAir()
                        && world.getBlockState(feet).isAir()
                        && world.getBlockState(head).isAir()) {
                    return feet; // safe standing position
                }
            }
        }

        // Fallback: directly above the respawn block
        return center.above();
    }
}
