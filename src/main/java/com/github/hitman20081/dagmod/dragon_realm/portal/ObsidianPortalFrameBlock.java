package com.github.hitman20081.dagmod.dragon_realm.portal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Obsidian Portal Frame Block - Used to construct Dragon Realm portals
 *
 * Properties:
 * - Very strong (50.0f hardness, 1200.0f blast resistance)
 * - Glowing obsidian effect (luminance 8)
 * - Emits dragon breath particles
 * - Portal frame: 7x7 structure
 */
public class ObsidianPortalFrameBlock extends Block {

    public ObsidianPortalFrameBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        // Portal particles (glowing purple swirl effect)
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticle(
                    ParticleTypes.PORTAL,
                    x, y, z,
                    0.0,
                    0.05,
                    0.0
            );
        }

        // Rare end rod particles (glowing white sparks)
        if (random.nextInt(20) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

            world.addParticle(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02
            );
        }
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);

        // Play resonant sound when placed
        if (!world.isClientSide()) {
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.BLOCKS,
                    0.6f,
                    0.9f
            );
        }
    }
}
