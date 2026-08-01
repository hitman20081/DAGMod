package com.github.hitman20081.dagmod.pale_garden.portal;

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.pale_garden.PaleGardenRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
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

public class PaleGardenTeleporter {

    public static final ResourceKey<Level> PALE_GARDEN = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("dagmod", "pale_garden")
    );

    public static final ResourceKey<Level> OVERWORLD = Level.OVERWORLD;

    public static void teleport(Entity entity, ServerLevel destinationWorld) {
        if (!(entity instanceof ServerPlayer player)) return;

        BlockPos sourcePos = entity.blockPosition();
        BlockPos destPos = getDestinationPos(sourcePos, (ServerLevel) entity.level(), destinationWorld);
        BlockPos portalPos = findOrCreatePortal(destinationWorld, destPos);

        Vec3 destVec = new Vec3(portalPos.getX() + 0.5, portalPos.getY(), portalPos.getZ() + 0.5);

        TeleportTransition target = new TeleportTransition(
                destinationWorld, destVec,
                entity.getDeltaMovement(),
                entity.getYRot(), entity.getXRot(),
                TeleportTransition.DO_NOTHING
        );

        player.teleport(target);

        if (destinationWorld.dimension() == PALE_GARDEN) {
            player.sendSystemMessage(Component.literal("§d§lYou have entered the Pale Garden..."));
            player.sendSystemMessage(Component.literal("§7An eerie stillness settles around you"));
        } else {
            player.sendSystemMessage(Component.literal("§aYou have returned to the Overworld"));
        }
    }

    private static BlockPos getDestinationPos(BlockPos sourcePos, ServerLevel sourceWorld, ServerLevel destWorld) {
        if (sourceWorld.dimension() == OVERWORLD && destWorld.dimension() == PALE_GARDEN) {
            return new BlockPos(sourcePos.getX(), 70, sourcePos.getZ());
        }
        return sourcePos;
    }

    private static BlockPos findOrCreatePortal(ServerLevel world, BlockPos targetPos) {
        BlockPos existing = findNearbyPortal(world, targetPos, 128);
        if (existing != null) return existing;
        return createPortal(world, targetPos);
    }

    private static BlockPos findNearbyPortal(ServerLevel world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -256; y <= 256; y++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    if (world.getBlockState(checkPos).getBlock() instanceof PaleGardenPortalBlock) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos createPortal(ServerLevel world, BlockPos pos) {
        BlockPos groundPos = findGroundLevel(world, pos);

        // Build 5×5 Pale Heartstone frame
        for (int i = 0; i < 5; i++) {
            world.setBlock(groundPos.east(i), ModBlocks.PALE_HEARTSTONE.defaultBlockState(), 3);
            world.setBlock(groundPos.east(i).above(4), ModBlocks.PALE_HEARTSTONE.defaultBlockState(), 3);
        }
        for (int i = 1; i < 4; i++) {
            world.setBlock(groundPos.above(i), ModBlocks.PALE_HEARTSTONE.defaultBlockState(), 3);
            world.setBlock(groundPos.east(4).above(i), ModBlocks.PALE_HEARTSTONE.defaultBlockState(), 3);
        }

        // Clear interior
        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 4; y++) {
                world.setBlock(groundPos.east(x).above(y), Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // Fill interior with portal blocks
        for (int x = 1; x < 4; x++) {
            for (int y = 1; y < 4; y++) {
                world.setBlock(groundPos.east(x).above(y),
                        PaleGardenRegistry.PALE_GARDEN_PORTAL.defaultBlockState()
                                .setValue(PaleGardenPortalBlock.AXIS, Direction.Axis.X), 3);
            }
        }

        BlockPos centerPos = groundPos.east(2).above(2);

        for (int i = 0; i < 30; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 3;
            double oy = (world.getRandom().nextDouble() - 0.5) * 3;
            double oz = (world.getRandom().nextDouble() - 0.5) * 3;
            world.sendParticles(ParticleTypes.END_ROD,
                    centerPos.getX() + ox, centerPos.getY() + oy, centerPos.getZ() + oz,
                    1, 0.05, 0.05, 0.05, 0.05);
        }

        world.playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 0.8f);

        return centerPos;
    }

    private static BlockPos findGroundLevel(ServerLevel world, BlockPos startPos) {
        BlockPos.MutableBlockPos mutable = startPos.mutable();

        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.DOWN);
            if (world.getBlockState(mutable).isSolid()) {
                return mutable.above().immutable();
            }
        }

        mutable.set(startPos);
        for (int i = 0; i < 128; i++) {
            mutable.move(Direction.UP);
            if (world.getBlockState(mutable).isSolid()) {
                return mutable.above().immutable();
            }
        }

        world.setBlock(startPos.below(), ModBlocks.PALE_HEARTSTONE.defaultBlockState(), 3);
        return startPos;
    }
}
