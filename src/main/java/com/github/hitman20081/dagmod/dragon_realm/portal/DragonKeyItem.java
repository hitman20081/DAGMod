package com.github.hitman20081.dagmod.dragon_realm.portal;

import com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry;
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
 * Dragon Key - Activates Dragon Realm portals
 *
 * Usage:
 * - Right-click on Obsidian Portal Frame to activate
 * - Single use item (consumed on activation)
 * - Must be used on valid 3x3 frame
 */
public class DragonKeyItem extends Item {

    public DragonKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        // Check if clicked on Obsidian Portal Frame
        if (!(world.getBlockState(pos).getBlock() instanceof ObsidianPortalFrameBlock)) {
            if (player != null && !world.isClient()) {
                player.sendMessage(
                        Text.literal("The Dragon Key must be used on an Obsidian Portal Frame!")
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
            DragonPortalFrameDetector detector = new DragonPortalFrameDetector(world, pos);

            if (!detector.isValidFrame()) {
                if (player != null) {
                    player.sendMessage(
                            Text.literal("Invalid portal frame! Must be a 7x7 frame of Obsidian Portal Frame blocks with a 5x5 opening.")
                                    .formatted(Formatting.RED),
                            true
                    );
                    player.sendMessage(
                            Text.literal("Frame must be made entirely of Obsidian Portal Frame blocks.")
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
    private void activatePortal(ServerWorld world, DragonPortalFrameDetector detector, PlayerEntity player) {
        List<BlockPos> interiorPositions = detector.getInteriorPositions();
        Direction.Axis axis = detector.getAxis();

        if (interiorPositions.isEmpty()) {
            return;
        }

        // Check if portal block is registered
        if (DragonRealmRegistry.DRAGON_REALM_PORTAL == null) {
            if (player != null) {
                player.sendMessage(Text.literal("ERROR: Portal block is not registered!").formatted(Formatting.RED), false);
            }
            System.err.println("DragonRealmRegistry.DRAGON_REALM_PORTAL is NULL!");
            return;
        }

        // Fill interior with portal blocks
        // Portal axis is perpendicular to frame orientation
        Direction.Axis portalAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (BlockPos portalPos : interiorPositions) {
            world.setBlockState(
                    portalPos,
                    DragonRealmRegistry.DRAGON_REALM_PORTAL.getDefaultState()
                            .with(DragonRealmPortalBlock.AXIS, portalAxis),
                    3 // Notify neighbors and clients
            );
        }

        // Visual and audio effects
        BlockPos centerPos = detector.getBottomLeft().offset(
                axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 1
        ).up(1);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2;

            world.spawnParticles(
                    ParticleTypes.ENCHANT,
                    centerPos.getX() + offsetX,
                    centerPos.getY() + offsetY,
                    centerPos.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.05
            );

            world.spawnParticles(
                    ParticleTypes.END_ROD,
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
                SoundEvents.BLOCK_END_PORTAL_SPAWN,
                SoundCategory.BLOCKS,
                0.8f,
                1.2f
        );

        // Success message
        if (player != null) {
            player.sendMessage(
                    Text.literal("✦ The Dragon Realm portal has been opened! ✦")
                            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                    false
            );
            player.sendMessage(
                    Text.literal("Step through to face the Dragon Guardian...")
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
