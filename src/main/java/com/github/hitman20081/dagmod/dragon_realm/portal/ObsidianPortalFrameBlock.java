package com.github.hitman20081.dagmod.dragon_realm.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

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

    public ObsidianPortalFrameBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        // Portal particles (glowing purple swirl effect)
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

            world.addParticleClient(
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

            world.addParticleClient(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02
            );
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        // Play resonant sound when placed
        if (!world.isClient()) {
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                    SoundCategory.BLOCKS,
                    0.6f,
                    0.9f
            );
        }
    }
}
