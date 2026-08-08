package com.github.hitman20081.dagmod.pale_garden.portal;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.pale_garden.PaleGardenRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The only sanctioned Pale Garden portal is the one built into the Pale Garden castle
 * structure — there is no way to build a player-made frame (Pale Heartstone is
 * operator-only and has no crafting recipe). The castle is a true singleton
 * (structure_set/castle_pale_garden.json uses concentric_rings, count: 1), placed close to
 * Pale Garden's own (0, 0) — NOT the player's Overworld position, which is an unrelated
 * coordinate space. Overworld -> Pale Garden entry always locates the castle by searching
 * from Pale Garden's origin via the vanilla structure-search API. Pale Garden -> Overworld
 * never searches for or builds a receiving portal; it just places the player safely back
 * where they entered from, since no Overworld-side portal structure can exist to receive them.
 */
public class PaleGardenTeleporter {

    public static final ResourceKey<Level> PALE_GARDEN = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath("dagmod", "pale_garden")
    );

    public static final ResourceKey<Level> OVERWORLD = Level.OVERWORLD;

    private static final Identifier CASTLE_STRUCTURE_ID = Identifier.fromNamespaceAndPath("dagmod", "castle_pale_garden");
    private static final int CASTLE_LOCATE_RADIUS_CHUNKS = 32; // castle is a concentric_rings singleton near origin (see PaleGardenTerrainHandler)
    private static final int CASTLE_BASE_Y = 100; // matches start_height.absolute in worldgen/structure/castle_pale_garden.json

    // Progressive radii, smallest first. Measured cost in practice is much cheaper than
    // originally feared (~65ms/chunk, not the ~750ms/chunk worst case assumed earlier) — 81
    // chunks (32+64 block tiers) took ~5.3s total. 128 is the structure's own documented
    // max_distance_from_center (castle_pale_garden.json), so it's a true upper bound: if the
    // frame isn't found within it, it genuinely doesn't exist within this structure instance,
    // not just "radius too small." getChunk() on an already-generated chunk is a cheap no-op,
    // so escalating steps only ever pay for the new outer ring, not the whole area again.
    private static final int[] FRAME_SEARCH_RADII_BLOCKS = {32, 64, 128};
    private static final int FRAME_SEARCH_Y_RANGE = 32;

    /** Once found, reused directly (with a cheap validity check) instead of re-scanning. */
    private static BlockPos cachedPortalPos = null;

    public static void teleport(Entity entity, ServerLevel destinationWorld) {
        if (!(entity instanceof ServerPlayer player)) return;

        ServerLevel sourceWorld = (ServerLevel) entity.level();
        BlockPos sourcePos = entity.blockPosition();

        if (sourceWorld.dimension() == PALE_GARDEN && destinationWorld.dimension() == OVERWORLD) {
            returnToOverworld(player, destinationWorld);
            return;
        }

        if (sourceWorld.dimension() == OVERWORLD && destinationWorld.dimension() == PALE_GARDEN) {
            // Remember where this player entered from, so the return trip knows where to
            // place them back — there's no physical portal to link back to on this side.
            PlayerDataManager.setPaleGardenEntryPos(player, sourcePos);

            // Search from Pale Garden's own origin, not the player's Overworld position —
            // the two dimensions' coordinates have no relationship to each other, so reusing
            // sourcePos here would search in the wrong place for any player not near (0,0).
            BlockPos portalPos = findCastlePortal(destinationWorld, BlockPos.ZERO);
            if (portalPos == null) {
                player.sendSystemMessage(Component.literal(
                                "The portal fizzles... no path to the Pale Garden could be found nearby.")
                        .withStyle(ChatFormatting.GRAY));
                return;
            }

            doTeleport(player, destinationWorld, portalPos);
            player.sendSystemMessage(Component.literal("§d§lYou have entered the Pale Garden..."));
            player.sendSystemMessage(Component.literal("§7An eerie stillness settles around you"));
        }
    }

    private static void doTeleport(ServerPlayer player, ServerLevel destinationWorld, BlockPos pos) {
        Vec3 destVec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        TeleportTransition target = new TeleportTransition(
                destinationWorld, destVec,
                player.getDeltaMovement(),
                player.getYRot(), player.getXRot(),
                TeleportTransition.DO_NOTHING
        );
        player.teleport(target);
    }

    private static void returnToOverworld(ServerPlayer player, ServerLevel overworld) {
        BlockPos entryPos = PlayerDataManager.getPaleGardenEntryPos(player.getUUID());
        if (entryPos == null) {
            // No recorded entry point (e.g. brought here by a command) — best effort.
            entryPos = player.blockPosition();
        }

        BlockPos safePos = findGroundLevel(overworld, entryPos);
        doTeleport(player, overworld, safePos);
        player.sendSystemMessage(Component.literal("§aYou have returned to the Overworld"));
    }

    /** Locates the real castle (wherever it generated) and returns its portal, activating the frame if needed. */
    private static BlockPos findCastlePortal(ServerLevel world, BlockPos searchOrigin) {
        if (cachedPortalPos != null
                && world.getBlockState(cachedPortalPos).getBlock() instanceof PaleGardenPortalBlock) {
            return cachedPortalPos;
        }

        BlockPos castlePos = locateNearestCastle(world, searchOrigin);
        if (castlePos == null) return null;

        // Search around the structure's known start height (CASTLE_BASE_Y, per
        // worldgen/structure/castle_pale_garden.json), not whatever Y the structure-locate call
        // happened to report.
        BlockPos searchCenter = new BlockPos(castlePos.getX(), CASTLE_BASE_Y, castlePos.getZ());

        for (int radius : FRAME_SEARCH_RADII_BLOCKS) {
            // Force-generate exactly the area this step's block scan covers, so the scan never
            // runs past pre-generated ground into implicit chunk generation. getChunk() on an
            // already-generated chunk (from a smaller prior step) is a cheap no-op, so this only
            // ever pays for the new outer ring.
            int radiusChunks = (radius >> 4) + 1;
            int cx = searchCenter.getX() >> 4;
            int cz = searchCenter.getZ() >> 4;
            for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
                for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                    world.getChunk(cx + dx, cz + dz);
                }
            }

            BlockPos existing = findNearbyPortal(world, searchCenter, radius);
            if (existing != null) {
                cachedPortalPos = existing;
                return existing;
            }

            BlockPos activated = findAndActivateFrame(world, searchCenter, radius);
            if (activated != null) {
                cachedPortalPos = activated;
                return activated;
            }
        }
        return null;
    }

    /** Uses the vanilla structure-search API (same one /locate and the Hall Locator use) instead of a blind block scan. */
    private static BlockPos locateNearestCastle(ServerLevel world, BlockPos searchOrigin) {
        try {
            var registry = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            var entry = registry.get(CASTLE_STRUCTURE_ID).orElse(null);
            if (entry == null) return null;

            var structures = HolderSet.direct(entry);
            ChunkGenerator generator = world.getChunkSource().getGenerator();
            var result = generator.findNearestMapStructure(world, structures, searchOrigin, CASTLE_LOCATE_RADIUS_CHUNKS, false);
            return result != null ? result.getFirst() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static BlockPos findAndActivateFrame(ServerLevel world, BlockPos center, int radius) {
        int yMin = center.getY() - FRAME_SEARCH_Y_RANGE;
        int yMax = center.getY() + FRAME_SEARCH_Y_RANGE;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    BlockPos checkPos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                    if (!(world.getBlockState(checkPos).getBlock() instanceof PaleHeartstoneBlock)) continue;

                    PaleGardenPortalFrameDetector detector = new PaleGardenPortalFrameDetector(world, checkPos);
                    if (!detector.isValidFrame()) continue;

                    // Activate the frame
                    Direction.Axis frameAxis = detector.getAxis();
                    Direction.Axis portalAxis = (frameAxis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
                    List<BlockPos> interior = detector.getInteriorPositions();
                    for (BlockPos portalPos : interior) {
                        world.setBlock(portalPos,
                                PaleGardenRegistry.PALE_GARDEN_PORTAL.defaultBlockState()
                                        .setValue(PaleGardenPortalBlock.AXIS, portalAxis), 3);
                    }

                    // Return center of activated portal
                    Direction rightDir = frameAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
                    return detector.getBottomLeft().relative(rightDir, 3).above(3);
                }
            }
        }
        return null;
    }

    private static BlockPos findNearbyPortal(ServerLevel world, BlockPos center, int radius) {
        int yMin = center.getY() - FRAME_SEARCH_Y_RANGE;
        int yMax = center.getY() + FRAME_SEARCH_Y_RANGE;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    BlockPos checkPos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                    if (world.getBlockState(checkPos).getBlock() instanceof PaleGardenPortalBlock) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
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

        // No solid ground found at all (e.g. void) — place a small safety platform.
        world.setBlock(startPos.below(), Blocks.STONE.defaultBlockState(), 3);
        return startPos;
    }
}
