package com.github.hitman20081.dagmod.bone_realm.portal;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
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

/**
 * Necrotic Key - Activates Bone Realm portals
 * Right-click on Ancient Bone Block frame to activate
 * Single use item
 */
public class NecroticKeyItem extends Item {

    public NecroticKeyItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // Check if clicked on Ancient Bone Block
        if (!(world.getBlockState(pos).getBlock() instanceof AncientBoneBlock)) {
            if (player != null && !world.isClientSide()) {
                player.sendOverlayMessage(
                        Component.literal("The Necrotic Key must be used on an Ancient Bone Block frame!")
                                .withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        // Server-side only for actual portal creation
        if (!world.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) world;

            // Detect portal frame
            BonePortalFrameDetector detector = new BonePortalFrameDetector(world, pos);

            if (!detector.isValidFrame()) {
                if (player != null) {
                    player.sendOverlayMessage(
                            Component.literal("Invalid portal frame! Must be 5 blocks wide × 6 blocks tall.")
                                    .withStyle(ChatFormatting.RED));
                    player.sendOverlayMessage(
                            Component.literal("Frame must be made entirely of Ancient Bone Blocks.")
                                    .withStyle(ChatFormatting.GRAY));
                }
                return InteractionResult.FAIL;
            }

            // Valid frame found! Activate portal
            activatePortal(serverWorld, detector, player);

            // Consume the key (single use)
            if (player != null && !player.isCreative()) {
                context.getItemInHand().shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        // Client-side: just show we're attempting to use it
        return InteractionResult.SUCCESS;
    }

    /**
     * Activates the portal by filling interior with portal blocks
     */
    private void activatePortal(ServerLevel world, BonePortalFrameDetector detector, Player player) {
        List<BlockPos> interiorPositions = detector.getInteriorPositions();
        Direction.Axis axis = detector.getAxis();

        if (interiorPositions.isEmpty()) {
            return;
        }

        // Check if portal block is registered
        if (BoneRealmRegistry.BONE_REALM_PORTAL == null) {
            if (player != null) {
                player.sendSystemMessage(Component.literal("ERROR: Portal block is not registered!").withStyle(ChatFormatting.RED));
            }
            System.err.println("BoneRealmRegistry.BONE_REALM_PORTAL is NULL!");
            return;
        }

        // Fill interior with portal blocks
        // Portal axis is perpendicular to frame orientation
        Direction.Axis portalAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (BlockPos portalPos : interiorPositions) {
            world.setBlock(
                    portalPos,
                    BoneRealmRegistry.BONE_REALM_PORTAL.defaultBlockState()
                            .setValue(BoneRealmPortalBlock.AXIS, portalAxis),
                    3 // Notify neighbors and clients
            );
        }

        // Visual and audio effects
        BlockPos centerPos = detector.getBottomLeft().relative(
                axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 2
        ).above(3);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 3;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * 4;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 3;

            world.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    centerPos.getX() + offsetX,
                    centerPos.getY() + offsetY,
                    centerPos.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.05
            );

            world.sendParticles(
                    ParticleTypes.SOUL,
                    centerPos.getX() + offsetX,
                    centerPos.getY() + offsetY,
                    centerPos.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.02
            );
        }

        // Portal activation sounds
        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.PORTAL_TRIGGER,
                SoundSource.BLOCKS,
                1.0f,
                0.8f
        );

        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                0.8f,
                1.2f
        );

        // Success message
        if (player != null) {
            player.sendSystemMessage(
                    Component.literal("✦ The Bone Realm portal has been opened! ✦")
                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
            player.sendSystemMessage(
                    Component.literal("Step through to enter the realm of the dead...")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
        // Make the key sparkle
        return true;
    }
}