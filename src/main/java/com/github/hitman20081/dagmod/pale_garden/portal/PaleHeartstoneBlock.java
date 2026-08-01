package com.github.hitman20081.dagmod.pale_garden.portal;

import com.github.hitman20081.dagmod.pale_garden.PaleGardenRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class PaleHeartstoneBlock extends Block {

    public PaleHeartstoneBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.025, 0.0);
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        if (!world.isClientSide()) {
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.4f, 0.5f);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) {
            PaleGardenPortalFrameDetector detector = new PaleGardenPortalFrameDetector(world, pos);
            if (detector.isValidFrame()) {
                activatePortal((ServerLevel) world, detector, player);
            } else {
                player.sendOverlayMessage(Component.literal("The Pale Heartstone hums, but the frame is incomplete...")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void activatePortal(ServerLevel world, PaleGardenPortalFrameDetector detector, Player player) {
        List<BlockPos> interiorPositions = detector.getInteriorPositions();
        Direction.Axis frameAxis = detector.getAxis();
        Direction.Axis portalAxis = (frameAxis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (BlockPos portalPos : interiorPositions) {
            world.setBlock(portalPos,
                    PaleGardenRegistry.PALE_GARDEN_PORTAL.defaultBlockState()
                            .setValue(PaleGardenPortalBlock.AXIS, portalAxis), 3);
        }

        BlockPos center = detector.getBottomLeft().relative(
                frameAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 2
        ).above(2);

        for (int i = 0; i < 40; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 3;
            double oy = (world.getRandom().nextDouble() - 0.5) * 4;
            double oz = (world.getRandom().nextDouble() - 0.5) * 3;
            world.sendParticles(ParticleTypes.END_ROD,
                    center.getX() + ox, center.getY() + oy, center.getZ() + oz,
                    1, 0.05, 0.05, 0.05, 0.05);
        }

        world.playSound(null, center.getX(), center.getY(), center.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 0.9f);

        if (player != null) {
            player.sendSystemMessage(Component.literal("✦ The Pale Garden portal awakens ✦")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("Step through to enter the pale beyond...")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
