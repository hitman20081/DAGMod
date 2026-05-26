package com.github.hitman20081.dagmod.bone_realm.portal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Ancient Bone Block - Frame block for Bone Realm portals
 * Crafted from Bone Blocks + Echo Shard
 * Has soul particle effects when placed
 */
public class AncientBoneBlock extends Block {

    public AncientBoneBlock(Properties settings) {
        super(settings);
        // Don't duplicate settings here - they're set in BoneRealmRegistry
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        // Soul particle effects (aesthetic)
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;

            world.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);

        // Play eerie sound when placed
        if (!world.isClientSide()) {
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, SoundSource.BLOCKS, 0.5f, 0.8f);
        }
    }
}