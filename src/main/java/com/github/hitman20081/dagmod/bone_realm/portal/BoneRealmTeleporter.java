package com.github.hitman20081.dagmod.bone_realm.portal;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
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

/**
 * Handles teleportation to/from Bone Realm
 * Creates return portals automatically with Ancient Bone Blocks
 * Maintains portal linking (same portal = same destination)
 */
public class BoneRealmTeleporter {

    // Dimension keys
    public static final ResourceKey<Level> BONE_REALM = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("dagmod", "bone_realm")
    );

    public static final ResourceKey<Level> OVERWORLD = Level.OVERWORLD;

    /**
     * Teleports entity between Overworld and Bone Realm
     */
    public static void teleport(Entity entity, ServerLevel destinationWorld) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel sourceWorld = (ServerLevel) entity.level();
        BlockPos sourcePos = entity.blockPosition();

        // Calculate destination position
        BlockPos destPos = getDestinationPos(sourcePos, sourceWorld, destinationWorld);

        // Find or create portal at destination
        BlockPos portalPos = findOrCreatePortal(destinationWorld, destPos);

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
        if (destinationWorld.dimension() == BONE_REALM) {
            player.sendSystemMessage(Component.literal("§5§lYou have entered the Bone Realm..."));
            player.sendSystemMessage(Component.literal("§7The air is thick with death"));
        } else {
            player.sendSystemMessage(Component.literal("§aYou have returned to the Overworld"));
        }
    }

    /**
     * Calculate destination position based on source world
     */
    private static BlockPos getDestinationPos(BlockPos sourcePos, ServerLevel sourceWorld, ServerLevel destWorld) {
        // If going from Overworld to Bone Realm, use same X/Z but fix Y to 64
        // so the portal generates near surface level rather than deep underground
        if (sourceWorld.dimension() == OVERWORLD && destWorld.dimension() == BONE_REALM) {
            return new BlockPos(sourcePos.getX(), 128, sourcePos.getZ());
        }

        // If returning from Bone Realm to Overworld, use same coordinates
        if (sourceWorld.dimension() == BONE_REALM && destWorld.dimension() == OVERWORLD) {
            return sourcePos;
        }

        return sourcePos;
    }

    /**
     * Find existing portal near position, or create new one
     */
    private static BlockPos findOrCreatePortal(ServerLevel world, BlockPos targetPos) {
        // Search for existing portal within 128 blocks
        BlockPos existingPortal = findNearbyPortal(world, targetPos, 128);
        if (existingPortal != null) {
            return existingPortal;
        }

        // No portal found, create new one
        return createPortal(world, targetPos);
    }

    /**
     * Search for existing portal near position
     */
    private static BlockPos findNearbyPortal(ServerLevel world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -256; y <= 256; y++) {
                    BlockPos checkPos = center.offset(x, y, z);

                    // Check if this is a portal block
                    if (world.getBlockState(checkPos).getBlock() instanceof BoneRealmPortalBlock) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create new portal at position with Ancient Bone Blocks
     */
    private static BlockPos createPortal(ServerLevel world, BlockPos pos) {
        // Find safe ground level
        BlockPos groundPos = findGroundLevel(world, pos);

        // Build portal frame (5 wide x 6 tall) with Ancient Bone Blocks
        // Frame extends EAST-WEST (along X axis)
        BlockPos bottomLeft = groundPos;

        // Bottom edge (X axis - East to West)
        for (int i = 0; i < 5; i++) {
            world.setBlock(bottomLeft.east(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.defaultBlockState(), 3);
        }

        // Top edge (X axis - East to West)
        for (int i = 0; i < 5; i++) {
            world.setBlock(bottomLeft.east(i).above(5), BoneRealmRegistry.ANCIENT_BONE_BLOCK.defaultBlockState(), 3);
        }

        // Left edge (vertical)
        for (int i = 1; i < 5; i++) {
            world.setBlock(bottomLeft.above(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.defaultBlockState(), 3);
        }

        // Right edge (vertical)
        for (int i = 1; i < 5; i++) {
            world.setBlock(bottomLeft.east(4).above(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.defaultBlockState(), 3);
        }

        // Clear interior space
        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 5; y++) {
                world.setBlock(bottomLeft.east(x).above(y), Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // Fill interior with portal blocks
        // Frame extends along X (East-West), so portal should face perpendicular
        // If portal appears rotated, swap the axis
        Direction.Axis portalAxis = Direction.Axis.X; // Try X axis instead of Z

        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 5; y++) {
                BlockPos portalBlockPos = bottomLeft.east(x).above(y);
                world.setBlock(
                        portalBlockPos,
                        BoneRealmRegistry.BONE_REALM_PORTAL.defaultBlockState()
                                .setValue(BoneRealmPortalBlock.AXIS, portalAxis),
                        3
                );
            }
        }

        // Visual effects when portal is created
        BlockPos centerPos = bottomLeft.east(2).above(3);

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
                0.8f
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
        world.setBlock(startPos.below(), BoneRealmRegistry.ANCIENT_BONE_BLOCK.defaultBlockState(), 3);
        return startPos;
    }
}