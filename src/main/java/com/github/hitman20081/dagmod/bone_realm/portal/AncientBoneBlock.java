package com.github.hitman20081.dagmod.bone_realm.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Ancient Bone Block - Frame block for Bone Realm portals
 * Crafted from Bone Blocks + Echo Shard
 * Has soul particle effects when placed
 */
public class AncientBoneBlock extends Block {

    public AncientBoneBlock(Settings settings) {
        super(settings);
        // Don't duplicate settings here - they're set in BoneRealmRegistry
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Soul particle effects (aesthetic)
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;

            world.addParticleClient(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        // Play eerie sound when placed
        if (!world.isClient()) {
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 0.5f, 0.8f);
        }
    }
}