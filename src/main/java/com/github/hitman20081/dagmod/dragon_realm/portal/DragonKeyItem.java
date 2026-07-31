package com.github.hitman20081.dagmod.dragon_realm.portal;

import com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
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

/**
 * Dragon Key - Activates Dragon Realm portals
 *
 * Usage:
 * - Right-click on Obsidian Portal Frame to activate
 * - Single use item (consumed on activation)
 * - Must be used on valid 3x3 frame
 */
public class DragonKeyItem extends Item {

    public DragonKeyItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // Check if clicked on Obsidian Portal Frame
        if (!(world.getBlockState(pos).getBlock() instanceof ObsidianPortalFrameBlock)) {
            if (player != null && !world.isClientSide()) {
                player.sendOverlayMessage(
                        Component.literal("The Dragon Key must be used on an Obsidian Portal Frame!")
                                .withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        // Server-side only for actual portal creation
        if (!world.isClientSide()) {
            // Level gate: Dragon Realm requires level 50
            if (player instanceof ServerPlayer sp && !sp.isCreative()) {
                PlayerProgressionData data = ProgressionManager.getPlayerData(sp);
                if (data == null || data.getCurrentLevel() < 50) {
                    int current = data != null ? data.getCurrentLevel() : 1;
                    player.sendSystemMessage(Component.literal(
                        "You are not strong enough to open the Dragon Realm. You must reach level 50 first. (Current: " + current + ")")
                        .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }
            }

            ServerLevel serverWorld = (ServerLevel) world;

            // Detect portal frame
            DragonPortalFrameDetector detector = new DragonPortalFrameDetector(world, pos);

            if (!detector.isValidFrame()) {
                if (player != null) {
                    player.sendOverlayMessage(
                            Component.literal("Invalid portal frame! Must be a 7x7 frame of Obsidian Portal Frame blocks with a 5x5 opening.")
                                    .withStyle(ChatFormatting.RED));
                    player.sendOverlayMessage(
                            Component.literal("Frame must be made entirely of Obsidian Portal Frame blocks.")
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
    private void activatePortal(ServerLevel world, DragonPortalFrameDetector detector, Player player) {
        List<BlockPos> interiorPositions = detector.getInteriorPositions();
        Direction.Axis axis = detector.getAxis();

        if (interiorPositions.isEmpty()) {
            return;
        }

        // Check if portal block is registered
        if (DragonRealmRegistry.DRAGON_REALM_PORTAL == null) {
            if (player != null) {
                player.sendSystemMessage(Component.literal("ERROR: Portal block is not registered!").withStyle(ChatFormatting.RED));
            }
            System.err.println("DragonRealmRegistry.DRAGON_REALM_PORTAL is NULL!");
            return;
        }

        // Fill interior with portal blocks
        // Portal axis is perpendicular to frame orientation
        Direction.Axis portalAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (BlockPos portalPos : interiorPositions) {
            world.setBlock(
                    portalPos,
                    DragonRealmRegistry.DRAGON_REALM_PORTAL.defaultBlockState()
                            .setValue(DragonRealmPortalBlock.AXIS, portalAxis),
                    3 // Notify neighbors and clients
            );
        }

        // Visual and audio effects
        BlockPos centerPos = detector.getBottomLeft().relative(
                axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST, 1
        ).above(1);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2;

            world.sendParticles(
                    ParticleTypes.ENCHANT,
                    centerPos.getX() + offsetX,
                    centerPos.getY() + offsetY,
                    centerPos.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.05
            );

            world.sendParticles(
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
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.BLOCKS,
                0.8f,
                1.2f
        );

        // Success message
        if (player != null) {
            player.sendSystemMessage(
                    Component.literal("✦ The Dragon Realm portal has been opened! ✦")
                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
            player.sendSystemMessage(
                    Component.literal("Step through to face the Dragon Guardian...")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
        // Make the key sparkle
        return true;
    }
}
