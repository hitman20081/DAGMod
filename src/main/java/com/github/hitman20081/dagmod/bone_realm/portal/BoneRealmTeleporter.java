package com.github.hitman20081.dagmod.bone_realm.portal;

import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
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
 * Handles teleportation to/from Bone Realm
 * Creates return portals automatically with Ancient Bone Blocks
 * Maintains portal linking (same portal = same destination)
 */
public class BoneRealmTeleporter {

    // Dimension keys
    public static final RegistryKey<World> BONE_REALM = RegistryKey.of(
            RegistryKeys.WORLD,
            Identifier.of("dag011", "bone_realm")
    );

    public static final RegistryKey<World> OVERWORLD = World.OVERWORLD;

    /**
     * Teleports entity between Overworld and Bone Realm
     */
    public static void teleport(Entity entity, ServerWorld destinationWorld) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        ServerWorld sourceWorld = (ServerWorld) entity.getEntityWorld();
        BlockPos sourcePos = entity.getBlockPos();

        // Calculate destination position
        BlockPos destPos = getDestinationPos(sourcePos, sourceWorld, destinationWorld);

        // Find or create portal at destination
        BlockPos portalPos = findOrCreatePortal(destinationWorld, destPos);

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
        if (destinationWorld.getRegistryKey() == BONE_REALM) {
            player.sendMessage(Text.literal("§5§lYou have entered the Bone Realm..."), false);
            player.sendMessage(Text.literal("§7The air is thick with death"), false);
        } else {
            player.sendMessage(Text.literal("§aYou have returned to the Overworld"), false);
        }
    }

    /**
     * Calculate destination position based on source world
     */
    private static BlockPos getDestinationPos(BlockPos sourcePos, ServerWorld sourceWorld, ServerWorld destWorld) {
        // If going from Overworld to Bone Realm, use 1:1 coordinates
        // This maintains portal linking
        if (sourceWorld.getRegistryKey() == OVERWORLD && destWorld.getRegistryKey() == BONE_REALM) {
            return sourcePos;
        }

        // If returning from Bone Realm to Overworld, use same coordinates
        if (sourceWorld.getRegistryKey() == BONE_REALM && destWorld.getRegistryKey() == OVERWORLD) {
            return sourcePos;
        }

        return sourcePos;
    }

    /**
     * Find existing portal near position, or create new one
     */
    private static BlockPos findOrCreatePortal(ServerWorld world, BlockPos targetPos) {
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
    private static BlockPos findNearbyPortal(ServerWorld world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -20; y <= 20; y++) {
                    BlockPos checkPos = center.add(x, y, z);

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
    private static BlockPos createPortal(ServerWorld world, BlockPos pos) {
        // Find safe ground level
        BlockPos groundPos = findGroundLevel(world, pos);

        // Build portal frame (5 wide x 6 tall) with Ancient Bone Blocks
        // Frame extends EAST-WEST (along X axis)
        BlockPos bottomLeft = groundPos;

        // Bottom edge (X axis - East to West)
        for (int i = 0; i < 5; i++) {
            world.setBlockState(bottomLeft.east(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.getDefaultState());
        }

        // Top edge (X axis - East to West)
        for (int i = 0; i < 5; i++) {
            world.setBlockState(bottomLeft.east(i).up(5), BoneRealmRegistry.ANCIENT_BONE_BLOCK.getDefaultState());
        }

        // Left edge (vertical)
        for (int i = 1; i < 5; i++) {
            world.setBlockState(bottomLeft.up(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.getDefaultState());
        }

        // Right edge (vertical)
        for (int i = 1; i < 5; i++) {
            world.setBlockState(bottomLeft.east(4).up(i), BoneRealmRegistry.ANCIENT_BONE_BLOCK.getDefaultState());
        }

        // Clear interior space
        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 5; y++) {
                world.setBlockState(bottomLeft.east(x).up(y), Blocks.AIR.getDefaultState());
            }
        }

        // Fill interior with portal blocks
        // Frame extends along X (East-West), so portal should face perpendicular
        // If portal appears rotated, swap the axis
        Direction.Axis portalAxis = Direction.Axis.X; // Try X axis instead of Z

        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 5; y++) {
                BlockPos portalBlockPos = bottomLeft.east(x).up(y);
                world.setBlockState(
                        portalBlockPos,
                        BoneRealmRegistry.BONE_REALM_PORTAL.getDefaultState()
                                .with(BoneRealmPortalBlock.AXIS, portalAxis),
                        3
                );
            }
        }

        // Visual effects when portal is created
        BlockPos centerPos = bottomLeft.east(2).up(3);

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
                0.8f
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
        world.setBlockState(startPos.down(), BoneRealmRegistry.ANCIENT_BONE_BLOCK.getDefaultState());
        return startPos;
    }
}