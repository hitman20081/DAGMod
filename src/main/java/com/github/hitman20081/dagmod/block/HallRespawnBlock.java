package com.github.hitman20081.dagmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

import java.util.concurrent.ThreadLocalRandom;

public class HallRespawnBlock extends Block {

    private static final int MIN_OFFSET = 5;
    private static final int MAX_OFFSET = 10;

    public HallRespawnBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }

        // Pick a random safe position 5-10 blocks from the respawn block
        BlockPos spawnPos = findSafeRandomPos(world, pos);

        WorldProperties.SpawnPoint spawnData = WorldProperties.SpawnPoint.create(
                world.getRegistryKey(), spawnPos, serverPlayer.getYaw(), 0f
        );
        ServerPlayerEntity.Respawn respawn = new ServerPlayerEntity.Respawn(spawnData, true);
        serverPlayer.setSpawnPoint(respawn, true);

        // Feedback
        serverPlayer.sendMessage(
                Text.literal("Respawn point set at the Hall of Champions!")
                        .formatted(Formatting.GOLD),
                false
        );

        // Effects
        ServerWorld serverWorld = (ServerWorld) world;
        serverWorld.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
        serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.1);

        return ActionResult.SUCCESS;
    }

    /**
     * Finds a random safe position (2 blocks of air) within 5-10 blocks of the given position.
     * Tries up to 20 random positions, falls back to directly above the block if none found.
     */
    private BlockPos findSafeRandomPos(World world, BlockPos center) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < 20; attempt++) {
            int dx = rand.nextInt(MIN_OFFSET, MAX_OFFSET + 1) * (rand.nextBoolean() ? 1 : -1);
            int dz = rand.nextInt(MIN_OFFSET, MAX_OFFSET + 1) * (rand.nextBoolean() ? 1 : -1);
            BlockPos candidate = center.add(dx, 0, dz);

            // Scan vertically near the block's Y level to find ground with 2 air blocks above
            for (int dy = -3; dy <= 3; dy++) {
                BlockPos ground = candidate.add(0, dy, 0);
                BlockPos feet = ground.up();
                BlockPos head = ground.up(2);

                if (!world.getBlockState(ground).isAir()
                        && world.getBlockState(feet).isAir()
                        && world.getBlockState(head).isAir()) {
                    return feet; // safe standing position
                }
            }
        }

        // Fallback: directly above the respawn block
        return center.up();
    }
}
