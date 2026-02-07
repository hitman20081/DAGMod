package com.github.hitman20081.dagmod.bone_realm.portal;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

/**
 * Necrotic Key - Activates Bone Realm portals
 * Right-click on Ancient Bone Block frame to activate
 * Single use item
 */
public class NecroticKeyItem extends Item {

    public NecroticKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        // Check if clicked on Ancient Bone Block
        if (!(world.getBlockState(pos).getBlock() instanceof AncientBoneBlock)) {
            if (player != null && !world.isClient()) {
                player.sendMessage(
                        Text.literal("The Necrotic Key must be used on an Ancient Bone Block frame!")
                                .formatted(Formatting.RED),
                        true
                );
            }
            return ActionResult.FAIL;
        }

        // Server-side only for actual portal creation
        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Detect portal frame
            BonePortalFrameDetector detector = new BonePortalFrameDetector(world, pos);

            if (!detector.isValidFrame()) {
                if (player != null) {
                    player.sendMessage(
                            Text.literal("Invalid portal frame! Must be 5 blocks wide × 6 blocks tall.")
                                    .formatted(Formatting.RED),
                            true
                    );
                    player.sendMessage(
                            Text.literal("Frame must be made entirely of Ancient Bone Blocks.")
                                    .formatted(Formatting.GRAY),
                            true
                    );
                }
                return ActionResult.FAIL;
            }

            // Valid frame found! Activate portal
            activatePortal(serverWorld, detector, player);

            // Consume the key (single use)
            if (player != null && !player.isCreative()) {
                context.getStack().decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        // Client-side: just show we're attempting to use it
        return ActionResult.SUCCESS;
    }

    /**
     * Activates the portal by filling interior with portal blocks
     */
    private void activatePortal(ServerWorld world, BonePortalFrameDetector detector, PlayerEntity player) {
        List<BlockPos> interiorPositions = detector.getInteriorPositions();
        Direction.Axis axis = detector.getAxis();

        if (interiorPositions.isEmpty()) {
            return;
        }

        // Check if portal block is registered
        if (BoneRealmRegistry.BONE_REALM_PORTAL == null) {
            if (player != null) {
                player.sendMessage(Text.literal("ERROR: Portal block is not registered!").formatted(Formatting.RED), false);
            }
            System.err.println("BoneRealmRegistry.BONE_REALM_PORTAL is NULL!");
            return;
        }

        // Fill interior with portal blocks
        // Portal axis is perpendicular to frame orientation
        Direction.Axis portalAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (BlockPos portalPos : interiorPositions) {
            world.setBlockState(
                    portalPos,
                    BoneRealmRegistry.BONE_REALM_PORTAL.getDefaultState()
                            .with(BoneRealmPortalBlock.AXIS, portalAxis),
                    3 // Notify neighbors and clients
            );
        }

        // Visual and audio effects
        BlockPos centerPos = detector.getBottomLeft().offset(
                axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 2
        ).up(3);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3;
            double offsetY = (world.random.nextDouble() - 0.5) * 4;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3;

            world.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    centerPos.getX() + offsetX,
                    centerPos.getY() + offsetY,
                    centerPos.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.05
            );

            world.spawnParticles(
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
                SoundEvents.BLOCK_PORTAL_TRIGGER,
                SoundCategory.BLOCKS,
                1.0f,
                0.8f
        );

        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                SoundCategory.BLOCKS,
                0.8f,
                1.2f
        );

        // Success message
        if (player != null) {
            player.sendMessage(
                    Text.literal("✦ The Bone Realm portal has been opened! ✦")
                            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                    false
            );
            player.sendMessage(
                    Text.literal("Step through to enter the realm of the dead...")
                            .formatted(Formatting.GRAY, Formatting.ITALIC),
                    false
            );
        }
    }

    @Override
    public boolean hasGlint(net.minecraft.item.ItemStack stack) {
        // Make the key sparkle
        return true;
    }
}