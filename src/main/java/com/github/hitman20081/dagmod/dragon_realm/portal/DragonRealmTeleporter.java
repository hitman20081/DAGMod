package com.github.hitman20081.dagmod.dragon_realm.portal;

import com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry;
import com.github.hitman20081.dagmod.dragon_realm.boss.DragonGuardianSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

/**
 * Handles teleportation to/from Dragon Realm
 * Creates return portals automatically with Crying Obsidian Portal Frame blocks
 * Maintains portal linking (same portal = same destination)
 */
public class DragonRealmTeleporter {

    // Dimension keys
    public static final ResourceKey<Level> DRAGON_REALM = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("dagmod", "dragon_realm")
    );

    public static final ResourceKey<Level> OVERWORLD = Level.OVERWORLD;

    /**
     * Teleports entity between Overworld and Dragon Realm
     */
    public static void teleport(Entity entity, ServerLevel destinationWorld) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel sourceWorld = (ServerLevel) entity.level();
        BlockPos sourcePos = entity.blockPosition();

        // Special handling for returning to Overworld - teleport to spawn/bed
        if (sourceWorld.dimension() == DRAGON_REALM && destinationWorld.dimension() == OVERWORLD) {
            teleportToSpawn(player, destinationWorld);
            return;
        }

        // Get the axis of the source portal
        Direction.Axis sourceAxis = Direction.Axis.X; // Default to X
        if (sourceWorld.getBlockState(sourcePos).getBlock() instanceof DragonRealmPortalBlock) {
            sourceAxis = sourceWorld.getBlockState(sourcePos).getValue(DragonRealmPortalBlock.AXIS);
        }

        // Calculate destination position
        BlockPos destPos = getDestinationPos(sourcePos, sourceWorld, destinationWorld);

        // Find or create portal at destination
        BlockPos portalPos = findOrCreatePortal(destinationWorld, destPos, sourceAxis);

        // Create teleport target
        Vec3 destVec = new Vec3(
                portalPos.getX() + 0.5,
                portalPos.getY(),
                portalPos.getZ() + 0.5
        );

        TeleportTransition target = new TeleportTransition(
                destinationWorld,
                destVec,
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                TeleportTransition.DO_NOTHING
        );

        // Perform teleport
        player.teleport(target);

        // Welcome message
        if (destinationWorld.dimension() == DRAGON_REALM) {
            player.sendSystemMessage(Component.literal("§d§lYou have entered the Dragon Realm..."));
            player.sendSystemMessage(Component.literal("§7The Dragon Guardian awaits"));

            // Try to spawn boss if not already present
            DragonGuardianSpawner.trySpawnBoss(destinationWorld, player);
        } else {
            player.sendSystemMessage(Component.literal("§aYou have returned to the Overworld"));
        }
    }

    /**
     * Teleport player to their respawn point or world spawn when returning from Dragon Realm.
     */
    private static void teleportToSpawn(ServerPlayer player, ServerLevel overworld) {
        // Prefer player's bed/respawn anchor in the overworld; fall back to world spawn
        ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
        BlockPos spawnPos;
        if (respawnConfig != null && respawnConfig.respawnData().dimension() == overworld.dimension()) {
            spawnPos = respawnConfig.respawnData().pos();
        } else {
            spawnPos = overworld.getRespawnData().pos();
        }

        // IMPORTANT: Find SAFE spawn location (not inside blocks)
        BlockPos safePos = findSafeSpawnLocationAggressive(overworld, spawnPos);

        // Create teleport target
        Vec3 destVec = new Vec3(
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5
        );

        TeleportTransition target = new TeleportTransition(
                overworld,
                destVec,
                Vec3.ZERO, // Clear velocity
                0.0F,
                0.0F, // Look horizontally
                TeleportTransition.DO_NOTHING
        );

        // Perform teleport
        player.teleport(target);

        // Spawn effects at destination
        spawnReturnEffects(overworld, safePos);

        // Welcome back message
        player.sendSystemMessage(Component.literal("§aReturned to the Overworld"));
    }

    /**
     * Aggressively find safe spawn location - searches wider area
     */
    private static BlockPos findSafeSpawnLocationAggressive(ServerLevel world, BlockPos pos) {
        // First try the exact position
        if (isSafeSpawnLocation(world, pos)) {
            return pos;
        }

        // Search in a wider vertical range
        for (int y = -10; y <= 10; y++) {
            BlockPos checkPos = pos.offset(0, y, 0);
            if (isSafeSpawnLocation(world, checkPos)) {
                return checkPos;
            }
        }

        // Search in horizontal spiral pattern
        for (int radius = 1; radius <= 5; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -5; y <= 5; y++) {
                        BlockPos checkPos = pos.offset(x, y, z);
                        if (isSafeSpawnLocation(world, checkPos)) {
                            return checkPos;
                        }
                    }
                }
            }
        }

        // Last resort: find ground level and spawn above it
        return findGroundLevel(world, pos).above(1);
    }

    /**
     * Find safe spawn location (not inside blocks)
     */
    private static BlockPos findSafeSpawnLocation(ServerLevel world, BlockPos pos) {
        // Check if current position is safe
        if (isSafeSpawnLocation(world, pos)) {
            return pos;
        }

        // Search nearby for safe spot
        for (int y = -2; y <= 2; y++) {
            BlockPos checkPos = pos.offset(0, y, 0);
            if (isSafeSpawnLocation(world, checkPos)) {
                return checkPos;
            }
        }

        // Fallback: find ground level
        return findGroundLevel(world, pos);
    }

    /**
     * Check if position is safe for spawning (not suffocating)
     */
    private static boolean isSafeSpawnLocation(ServerLevel world, BlockPos pos) {
        // Check feet and head level are not solid
        return !world.getBlockState(pos).isSolid() &&
               !world.getBlockState(pos.above()).isSolid() &&
               world.getBlockState(pos.below()).isSolid(); // Has solid ground
    }

    /**
     * Spawn particle effects when returning from Dragon Realm
     */
    private static void spawnReturnEffects(ServerLevel world, BlockPos pos) {
        // Moderate particle effect (not too flashy)
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = world.getRandom().nextDouble() * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2;

            world.sendParticles(
                    ParticleTypes.PORTAL,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.05
            );
        }

        // Return sound
        world.playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );
    }

    /**
     * Calculate destination position based on source world
     */
    private static BlockPos getDestinationPos(BlockPos sourcePos, ServerLevel sourceWorld, ServerLevel destWorld) {
        // If going to Dragon Realm, always spawn at boss arena
        // This ensures players can find the Dragon Guardian at (0, 64, 0)
        if (destWorld.dimension() == DRAGON_REALM) {
            return new BlockPos(0, 80, 0);  // Spawn above boss arena
        }

        // If returning from Dragon Realm to Overworld, use source coordinates
        // Portal linking will find the nearest portal within 128 blocks (the entry portal)
        if (sourceWorld.dimension() == DRAGON_REALM && destWorld.dimension() == OVERWORLD) {
            return sourcePos;
        }

        return sourcePos;
    }

    /**
     * Find existing portal near position, or create new one
     */
    private static BlockPos findOrCreatePortal(ServerLevel world, BlockPos targetPos, Direction.Axis axis) {
        // Search for existing portal within 128 blocks
        BlockPos existingPortal = findNearbyPortal(world, targetPos, 128);
        if (existingPortal != null) {
            return existingPortal;
        }

        // No portal found, create new one
        return createPortal(world, targetPos, axis);
    }

    /**
     * Search for existing portal near position
     */
    private static BlockPos findNearbyPortal(ServerLevel world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -20; y <= 20; y++) {
                    BlockPos checkPos = center.offset(x, y, z);

                    // Check if this is a portal block
                    if (world.getBlockState(checkPos).getBlock() instanceof DragonRealmPortalBlock) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create new portal at position with Crying Obsidian Portal Frame blocks
     * Portal is 3x3 (smaller than Bone Realm's 5x6)
     */
    private static BlockPos createPortal(ServerLevel world, BlockPos pos, Direction.Axis axis) {
        // Find safe ground level
        BlockPos groundPos = findGroundLevel(world, pos);
        BlockPos bottomLeftFrameCorner = groundPos;

        int frameWidth = 7;
        int frameHeight = 7;

        // Determine the direction to build the frame based on the portal's block axis
        Direction frameDirection = (axis == Direction.Axis.X) ? Direction.EAST : Direction.SOUTH;

        // Build portal frame
        for (int i = 0; i < frameWidth; i++) {
            // Bottom edge
            world.setBlock(bottomLeftFrameCorner.relative(frameDirection, i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.defaultBlockState(), 3);
            // Top edge
            world.setBlock(bottomLeftFrameCorner.relative(frameDirection, i).above(frameHeight - 1), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.defaultBlockState(), 3);
        }

        for (int i = 1; i < frameHeight - 1; i++) {
            // Left edge
            world.setBlock(bottomLeftFrameCorner.above(i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.defaultBlockState(), 3);
            // Right edge
            world.setBlock(bottomLeftFrameCorner.relative(frameDirection, frameWidth - 1).above(i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.defaultBlockState(), 3);
        }

        // Fill interior with portal blocks
        BlockPos innerBottomLeft = bottomLeftFrameCorner.relative(frameDirection, 1).above(1);

        for (int w = 0; w < frameWidth - 2; w++) { // 5 blocks wide
            for (int h = 0; h < frameHeight - 2; h++) { // 5 blocks tall
                BlockPos currentInnerPos = innerBottomLeft.relative(frameDirection, w).above(h);
                world.setBlock(
                        currentInnerPos,
                        DragonRealmRegistry.DRAGON_REALM_PORTAL.defaultBlockState()
                                .setValue(DragonRealmPortalBlock.AXIS, axis),
                        3
                );
            }
        }

        // Visual effects when portal is created
        BlockPos centerPos = innerBottomLeft.relative(frameDirection, 2).above(2);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 4;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * 4;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 4;

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

        // Portal creation sound
        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.PORTAL_TRIGGER,
                SoundSource.BLOCKS,
                1.0f,
                1.2f
        );

        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.BLOCKS,
                0.8f,
                1.0f
        );

        return centerPos;
    }

    /**
     * Find safe ground level to build portal
     */
    private static BlockPos findGroundLevel(ServerLevel world, BlockPos startPos) {
        BlockPos.MutableBlockPos mutable = startPos.mutable();

        // Search down for solid ground
        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.DOWN);

            if (world.getBlockState(mutable).isSolid()) {
                // Found solid ground, go up 1 block
                return mutable.above().immutable();
            }
        }

        // Search up if we didn't find ground below
        mutable.set(startPos);
        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.UP);

            if (world.getBlockState(mutable).isSolid()) {
                // Found solid ground, go up 1 block
                return mutable.above().immutable();
            }
        }

        // Fallback: use original position and create a platform
        world.setBlock(startPos.below(), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.defaultBlockState(), 3);
        return startPos;
    }
}
