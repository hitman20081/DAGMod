package com.github.hitman20081.dagmod.pale_garden.portal;

import com.github.hitman20081.dagmod.pale_garden.PaleGardenRegistry;
import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.List;

public class PaleGardenKeyItem extends Item {

    public PaleGardenKeyItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!(world.getBlockState(pos).getBlock() instanceof PaleHeartstoneBlock)) {
            if (player != null && !world.isClientSide()) {
                player.sendOverlayMessage(
                        Component.literal("The Pale Garden Key must be used on a Pale Heartstone frame!")
                                .withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        if (!world.isClientSide()) {
            // Level gate: Pale Garden requires level 35 -- Spider Queen's stats (275 HP/10 armor)
            // land between Bone Realm's Lord and King, so it's gated as roughly Bone-Realm-tier
            // content, above Bone Realm's own gate (25) but below Dragon Realm's (50)
            if (player instanceof ServerPlayer sp && !sp.isCreative()) {
                PlayerProgressionData data = ProgressionManager.getPlayerData(sp);
                if (data == null || data.getCurrentLevel() < 35) {
                    int current = data != null ? data.getCurrentLevel() : 1;
                    player.sendSystemMessage(Component.literal(
                        "You are not strong enough to open the Pale Garden. You must reach level 35 first. (Current: " + current + ")")
                        .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }
            }

            PaleGardenPortalFrameDetector detector = new PaleGardenPortalFrameDetector(world, pos);

            if (!detector.isValidFrame()) {
                if (player != null) {
                    player.sendOverlayMessage(
                            Component.literal("Invalid portal frame! Must be a 7×7 frame of Pale Heartstone blocks with a 5×5 opening.")
                                    .withStyle(ChatFormatting.RED));
                }
                return InteractionResult.FAIL;
            }

            activatePortal((ServerLevel) world, detector, player);

            if (player != null && !player.isCreative()) {
                context.getItemInHand().shrink(1);
            }

            return InteractionResult.SUCCESS;
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
                frameAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 3
        ).above(3);

        for (int i = 0; i < 50; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 4;
            double oy = (world.getRandom().nextDouble() - 0.5) * 4;
            double oz = (world.getRandom().nextDouble() - 0.5) * 4;
            world.sendParticles(ParticleTypes.END_ROD,
                    center.getX() + ox, center.getY() + oy, center.getZ() + oz,
                    1, 0.05, 0.05, 0.05, 0.05);
            world.sendParticles(ParticleTypes.PORTAL,
                    center.getX() + ox, center.getY() + oy, center.getZ() + oz,
                    1, 0.1, 0.1, 0.1, 0.05);
        }

        world.playSound(null, center.getX(), center.getY(), center.getZ(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 0.8f);
        world.playSound(null, center.getX(), center.getY(), center.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.8f, 1.2f);

        if (player != null) {
            player.sendSystemMessage(Component.literal("✦ The Pale Garden portal awakens ✦")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("Step through to enter the pale beyond...")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
        return true;
    }
}
