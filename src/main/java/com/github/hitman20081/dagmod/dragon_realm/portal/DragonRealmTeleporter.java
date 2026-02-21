package com.github.hitman20081.dagmod.dragon_realm.portal;

import com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry;
import com.github.hitman20081.dagmod.dragon_realm.boss.DragonGuardianSpawner;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

/**
 * Handles teleportation to/from Dragon Realm
 * Creates return portals automatically with Crying Obsidian Portal Frame blocks
 * Maintains portal linking (same portal = same destination)
 */
public class DragonRealmTeleporter {

    // Dimension keys
    public static final RegistryKey<World> DRAGON_REALM = RegistryKey.of(
            RegistryKeys.WORLD,
            Identifier.of("dagmod", "dragon_realm")
    );

    public static final RegistryKey<World> OVERWORLD = World.OVERWORLD;

    /**
     * Teleports entity between Overworld and Dragon Realm
     */
    public static void teleport(Entity entity, ServerWorld destinationWorld) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        ServerWorld sourceWorld = (ServerWorld) entity.getEntityWorld();
        BlockPos sourcePos = entity.getBlockPos();

        // Special handling for returning to Overworld - teleport to spawn/bed
        if (sourceWorld.getRegistryKey() == DRAGON_REALM && destinationWorld.getRegistryKey() == OVERWORLD) {
            teleportToSpawn(player, destinationWorld);
            return;
        }

        // Get the axis of the source portal
        Direction.Axis sourceAxis = Direction.Axis.X; // Default to X
        if (sourceWorld.getBlockState(sourcePos).getBlock() instanceof DragonRealmPortalBlock) {
            sourceAxis = sourceWorld.getBlockState(sourcePos).get(DragonRealmPortalBlock.AXIS);
        }

        // Calculate destination position
        BlockPos destPos = getDestinationPos(sourcePos, sourceWorld, destinationWorld);

        // Find or create portal at destination
        BlockPos portalPos = findOrCreatePortal(destinationWorld, destPos, sourceAxis);

        // Create teleport target
        Vec3d destVec = new Vec3d(
                portalPos.getX() + 0.5,
                portalPos.getY(),
                portalPos.getZ() + 0.5
        );

        TeleportTarget target = new TeleportTarget(
                destinationWorld,
                destVec,
                entity.getVelocity(),
                entity.getYaw(),
                entity.getPitch(),
                TeleportTarget.NO_OP
        );

        // Perform teleport
        player.teleportTo(target);

        // Welcome message
        if (destinationWorld.getRegistryKey() == DRAGON_REALM) {
            player.sendMessage(Text.literal("§d§lYou have entered the Dragon Realm..."), false);
            player.sendMessage(Text.literal("§7The Dragon Guardian awaits"), false);

            // Try to spawn boss if not already present
            DragonGuardianSpawner.trySpawnBoss(destinationWorld, player);
        } else {
            player.sendMessage(Text.literal("§aYou have returned to the Overworld"), false);
        }
    }

    /**
     * Teleport player to their spawn point (bed or world spawn)
     * Used when returning from Dragon Realm - no portal creation
     */
    private static void teleportToSpawn(ServerPlayerEntity player, ServerWorld overworld) {
        // Use world spawn as safe return point
        // TODO: In future, implement bed spawn detection using player NBT data
        BlockPos spawnPos = new BlockPos(0, 64, 0); // Default world spawn

        // IMPORTANT: Find SAFE spawn location (not inside blocks)
        BlockPos safePos = findSafeSpawnLocationAggressive(overworld, spawnPos);

        // Create teleport target
        Vec3d destVec = new Vec3d(
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5
        );

        TeleportTarget target = new TeleportTarget(
                overworld,
                destVec,
                Vec3d.ZERO, // Clear velocity
                0.0F,
                0.0F, // Look horizontally
                TeleportTarget.NO_OP
        );

        // Perform teleport
        player.teleportTo(target);

        // Spawn effects at destination
        spawnReturnEffects(overworld, safePos);

        // Welcome back message
        player.sendMessage(Text.literal("§aReturned to the Overworld"), false);
    }

    /**
     * Aggressively find safe spawn location - searches wider area
     */
    private static BlockPos findSafeSpawnLocationAggressive(ServerWorld world, BlockPos pos) {
        // First try the exact position
        if (isSafeSpawnLocation(world, pos)) {
            return pos;
        }

        // Search in a wider vertical range
        for (int y = -10; y <= 10; y++) {
            BlockPos checkPos = pos.add(0, y, 0);
            if (isSafeSpawnLocation(world, checkPos)) {
                return checkPos;
            }
        }

        // Search in horizontal spiral pattern
        for (int radius = 1; radius <= 5; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -5; y <= 5; y++) {
                        BlockPos checkPos = pos.add(x, y, z);
                        if (isSafeSpawnLocation(world, checkPos)) {
                            return checkPos;
                        }
                    }
                }
            }
        }

        // Last resort: find ground level and spawn above it
        return findGroundLevel(world, pos).up(1);
    }

    /**
     * Find safe spawn location (not inside blocks)
     */
    private static BlockPos findSafeSpawnLocation(ServerWorld world, BlockPos pos) {
        // Check if current position is safe
        if (isSafeSpawnLocation(world, pos)) {
            return pos;
        }

        // Search nearby for safe spot
        for (int y = -2; y <= 2; y++) {
            BlockPos checkPos = pos.add(0, y, 0);
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
    private static boolean isSafeSpawnLocation(ServerWorld world, BlockPos pos) {
        // Check feet and head level are not solid
        return !world.getBlockState(pos).isSolidBlock(world, pos) &&
               !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) &&
               world.getBlockState(pos.down()).isSolidBlock(world, pos.down()); // Has solid ground
    }

    /**
     * Spawn particle effects when returning from Dragon Realm
     */
    private static void spawnReturnEffects(ServerWorld world, BlockPos pos) {
        // Moderate particle effect (not too flashy)
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2;

            world.spawnParticles(
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
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );
    }

    /**
     * Calculate destination position based on source world
     */
    private static BlockPos getDestinationPos(BlockPos sourcePos, ServerWorld sourceWorld, ServerWorld destWorld) {
        // If going to Dragon Realm, always spawn at boss arena
        // This ensures players can find the Dragon Guardian at (0, 64, 0)
        if (destWorld.getRegistryKey() == DRAGON_REALM) {
            return new BlockPos(0, 80, 0);  // Spawn above boss arena
        }

        // If returning from Dragon Realm to Overworld, use source coordinates
        // Portal linking will find the nearest portal within 128 blocks (the entry portal)
        if (sourceWorld.getRegistryKey() == DRAGON_REALM && destWorld.getRegistryKey() == OVERWORLD) {
            return sourcePos;
        }

        return sourcePos;
    }

    /**
     * Find existing portal near position, or create new one
     */
    private static BlockPos findOrCreatePortal(ServerWorld world, BlockPos targetPos, Direction.Axis axis) {
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
    private static BlockPos findNearbyPortal(ServerWorld world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -20; y <= 20; y++) {
                    BlockPos checkPos = center.add(x, y, z);

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
    private static BlockPos createPortal(ServerWorld world, BlockPos pos, Direction.Axis axis) {
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
            world.setBlockState(bottomLeftFrameCorner.offset(frameDirection, i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.getDefaultState());
            // Top edge
            world.setBlockState(bottomLeftFrameCorner.offset(frameDirection, i).up(frameHeight - 1), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.getDefaultState());
        }

        for (int i = 1; i < frameHeight - 1; i++) {
            // Left edge
            world.setBlockState(bottomLeftFrameCorner.up(i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.getDefaultState());
            // Right edge
            world.setBlockState(bottomLeftFrameCorner.offset(frameDirection, frameWidth - 1).up(i), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.getDefaultState());
        }

        // Fill interior with portal blocks
        BlockPos innerBottomLeft = bottomLeftFrameCorner.offset(frameDirection, 1).up(1);

        for (int w = 0; w < frameWidth - 2; w++) { // 5 blocks wide
            for (int h = 0; h < frameHeight - 2; h++) { // 5 blocks tall
                BlockPos currentInnerPos = innerBottomLeft.offset(frameDirection, w).up(h);
                world.setBlockState(
                        currentInnerPos,
                        DragonRealmRegistry.DRAGON_REALM_PORTAL.getDefaultState()
                                .with(DragonRealmPortalBlock.AXIS, axis),
                        3
                );
            }
        }

        // Visual effects when portal is created
        BlockPos centerPos = innerBottomLeft.offset(frameDirection, 2).up(2);

        // Epic particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 4;
            double offsetY = (world.random.nextDouble() - 0.5) * 4;
            double offsetZ = (world.random.nextDouble() - 0.5) * 4;

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

        // Portal creation sound
        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.BLOCK_PORTAL_TRIGGER,
                SoundCategory.BLOCKS,
                1.0f,
                1.2f
        );

        world.playSound(
                null,
                centerPos.getX(),
                centerPos.getY(),
                centerPos.getZ(),
                SoundEvents.BLOCK_END_PORTAL_SPAWN,
                SoundCategory.BLOCKS,
                0.8f,
                1.0f
        );

        return centerPos;
    }

    /**
     * Find safe ground level to build portal
     */
    private static BlockPos findGroundLevel(ServerWorld world, BlockPos startPos) {
        BlockPos.Mutable mutable = startPos.mutableCopy();

        // Search down for solid ground
        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.DOWN);

            if (world.getBlockState(mutable).isSolidBlock(world, mutable)) {
                // Found solid ground, go up 1 block
                return mutable.up().toImmutable();
            }
        }

        // Search up if we didn't find ground below
        mutable.set(startPos);
        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.UP);

            if (world.getBlockState(mutable).isSolidBlock(world, mutable)) {
                // Found solid ground, go up 1 block
                return mutable.up().toImmutable();
            }
        }

        // Fallback: use original position and create a platform
        world.setBlockState(startPos.down(), DragonRealmRegistry.OBSIDIAN_PORTAL_FRAME.getDefaultState());
        return startPos;
    }
}
