package com.github.hitman20081.dagmod.pale_garden;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenTeleporter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * The Spider Queen Lair's entrance staircase (worldgen/structure/spider_queen_chamber, built by
 * scratchpad/gen_spider_queen_lair.py) only bakes a short, fixed-length flight into its NBT --
 * local terrain height above the lair varies per world/seed (the same reason the castle needs the
 * flatten/feather system rather than a fixed Y), so a fixed-height guess baked into the structure
 * can easily fall short and dead-end underground. This handler finds the {@code
 * dagmod:lair_shaft_marker} block embedded in the staircase's topmost step at runtime (its real
 * world position, whatever rotation the jigsaw piece happened to get) and carves a ladder-shaft
 * upward from there, one Y layer at a time, until it breaks into genuinely open space -- daylight
 * or an existing natural cavern -- guaranteeing the entrance always reaches air, regardless of
 * local terrain.
 *
 * Runs once, ever, for this world (the lair is a concentric_rings singleton): locates the
 * structure at server start (cheap, no force-generation -- same pattern as
 * PaleGardenTerrainHandler's castle lookup), then resolves+scans+carves lazily from a normal
 * end-of-tick call. Everything here (chunk force-generation, setBlock) runs directly from
 * onServerTick, never from a chunk-load callback, so it can never re-enter a nested
 * chunk-generation call stack the way a CHUNK_LOAD-triggered edit could.
 */
public class LairShaftHandler {

    private static final Identifier LAIR_STRUCTURE_ID = Identifier.fromNamespaceAndPath("dagmod", "spider_queen_lair");
    private static final int LOCATE_SEARCH_RADIUS_CHUNKS = 150; // structure_set distance:96 chunks, spread:2 — generous margin
    private static final int BOUNDS_GENERATE_RADIUS_CHUNKS = 9; // comfortably covers the piece's ~110x50 footprint from its origin in any rotation

    private static final int LAIR_START_Y = -40; // matches start_height.absolute in worldgen/structure/spider_queen_lair.json
    private static final int MARKER_LOCAL_Y = 60; // matches STAIR_RUN in gen_spider_queen_lair.py (marker sits at the top step's tread)
    private static final int BAKED_HEADROOM = 3; // matches STAIR_HEADROOM in gen_spider_queen_lair.py -- the baked corridor already carved this much air above the marker's own tread

    private static final int SHAFT_HALF_WIDTH = 2; // 5-wide, matching the baked corridor's STAIR_INNER
    private static final int SHAFT_MAX_CLIMB = 260; // safety cap; world height (min -64, 384 tall) leaves ample margin from any plausible marker Y

    private static int lairX;
    private static int lairZ;
    private static boolean lairLocated = false;
    private static boolean shaftDone = false;
    private static int resolveAttempts = 0;
    private static final int MAX_RESOLVE_ATTEMPTS = 100; // ~5s of ticks; gives chunk force-gen plenty of room before giving up

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(LairShaftHandler::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(LairShaftHandler::onServerTick);
    }

    private static void onServerStarted(MinecraftServer server) {
        ServerLevel paleGarden = server.getLevel(PaleGardenTeleporter.PALE_GARDEN);
        if (paleGarden == null) return;

        BlockPos lairPos = locateLair(paleGarden);
        if (lairPos == null) {
            DagMod.LOGGER.warn("[DAGMod] Could not locate Spider Queen Lair — entrance shaft carving disabled this session");
            shaftDone = true;
            return;
        }

        lairX = lairPos.getX();
        lairZ = lairPos.getZ();
        lairLocated = true;
    }

    private static BlockPos locateLair(ServerLevel world) {
        try {
            var registry = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            var entry = registry.get(LAIR_STRUCTURE_ID).orElse(null);
            if (entry == null) return null;

            ChunkGenerator generator = world.getChunkSource().getGenerator();
            var result = generator.findNearestMapStructure(
                    world, HolderSet.direct(entry), BlockPos.ZERO, LOCATE_SEARCH_RADIUS_CHUNKS, false);
            return result != null ? result.getFirst() : null;
        } catch (Exception e) {
            DagMod.LOGGER.error("[DAGMod] Failed to locate Spider Queen Lair", e);
            return null;
        }
    }

    private static void onServerTick(MinecraftServer server) {
        if (!lairLocated || shaftDone) return;

        ServerLevel level = server.getLevel(PaleGardenTeleporter.PALE_GARDEN);
        if (level == null) return;

        resolveAttempts++;
        if (!tryCarveShaft(level)) {
            if (resolveAttempts >= MAX_RESOLVE_ATTEMPTS) {
                DagMod.LOGGER.error("[DAGMod] Gave up resolving Spider Queen Lair structure after "
                        + MAX_RESOLVE_ATTEMPTS + " attempts — entrance shaft not carved");
                shaftDone = true;
            }
            return;
        }

        shaftDone = true;
    }

    /** Returns true once the shaft has been fully carved (or definitively failed); false to retry next tick. */
    private static boolean tryCarveShaft(ServerLevel level) {
        try {
            var registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            var entry = registry.get(LAIR_STRUCTURE_ID).orElse(null);
            if (entry == null) return true; // nothing we can do, stop retrying
            Structure lairStructure = entry.value();

            BlockPos origin = new BlockPos(lairX, LAIR_START_Y, lairZ);
            int cx = origin.getX() >> 4;
            int cz = origin.getZ() >> 4;
            for (int dx = -BOUNDS_GENERATE_RADIUS_CHUNKS; dx <= BOUNDS_GENERATE_RADIUS_CHUNKS; dx++) {
                for (int dz = -BOUNDS_GENERATE_RADIUS_CHUNKS; dz <= BOUNDS_GENERATE_RADIUS_CHUNKS; dz++) {
                    level.getChunk(cx + dx, cz + dz);
                }
            }

            StructureStart start = level.structureManager().getStructureAt(origin, lairStructure);
            if (start == null || !start.isValid()) return false; // not ready yet, retry next tick

            BoundingBox bounds = start.getBoundingBox();
            int markerY = bounds.minY() + MARKER_LOCAL_Y; // Y is never affected by jigsaw rotation

            BlockPos marker = findMarker(level, bounds, markerY);
            if (marker == null) {
                DagMod.LOGGER.error("[DAGMod] Spider Queen Lair resolved but shaft marker not found at Y="
                        + markerY + " within " + bounds + " — entrance shaft not carved");
                return true; // structure is resolved; this isn't going to change on retry
            }

            // Extend the ladder down through the baked corridor's own headroom (already open air,
            // markerY+1..markerY+BAKED_HEADROOM) so it's continuous from the top step -- without
            // this, the ladder placed by carveShaftUpward would start several blocks above the
            // tread with nothing to climb to reach its first rung.
            BlockPos.MutableBlockPos ladderPos = new BlockPos.MutableBlockPos();
            for (int y = markerY + 1; y <= markerY + BAKED_HEADROOM; y++) {
                ladderPos.set(marker.getX() - SHAFT_HALF_WIDTH, y, marker.getZ());
                level.setBlock(ladderPos, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST), 2);
            }

            // Start carving above the corridor's OWN baked ceiling (markerY + BAKED_HEADROOM),
            // not directly above the marker's tread -- the baked corridor already carved
            // markerY+1..markerY+BAKED_HEADROOM to air, so starting there made
            // crossSectionAlreadyOpen see already-open air on its very first check and stop
            // immediately, carving nothing at all.
            int reachedY = carveShaftUpward(level, marker.getX(), markerY + BAKED_HEADROOM, marker.getZ());
            level.setBlock(marker, Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 2);

            DagMod.LOGGER.info("[DAGMod] Spider Queen Lair entrance shaft carved from Y=" + markerY
                    + " to Y=" + reachedY + " at (" + marker.getX() + ", " + marker.getZ() + ")");
            return true;
        } catch (Exception e) {
            DagMod.LOGGER.error("[DAGMod] Failed to carve Spider Queen Lair entrance shaft", e);
            return true; // don't retry forever on a genuine error
        }
    }

    private static BlockPos findMarker(ServerLevel level, BoundingBox bounds, int markerY) {
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                BlockPos pos = new BlockPos(x, markerY, z);
                if (level.getBlockState(pos).getBlock() == ModBlocks.LAIR_SHAFT_MARKER) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Climbs from (centerX, startY+1, centerZ) upward, carving a 5-wide open shaft with a ladder
     * on its west face, until an entire cross-section is already open (natural cavern or
     * daylight) before this method touches it -- that layer is left untouched, since it's already
     * the way out. Returns the highest Y actually reached.
     */
    private static int carveShaftUpward(ServerLevel level, int centerX, int startY, int centerZ) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int y = startY + 1;
        int top = startY;

        for (; y <= startY + SHAFT_MAX_CLIMB; y++) {
            if (crossSectionAlreadyOpen(level, mutable, centerX, y, centerZ)) {
                top = y - 1;
                break;
            }

            for (int dx = -SHAFT_HALF_WIDTH; dx <= SHAFT_HALF_WIDTH; dx++) {
                for (int dz = -SHAFT_HALF_WIDTH; dz <= SHAFT_HALF_WIDTH; dz++) {
                    mutable.set(centerX + dx, y, centerZ + dz);
                    if (dx == -SHAFT_HALF_WIDTH && dz == 0) {
                        level.setBlock(mutable, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST), 2);
                    } else {
                        level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
            top = y;
        }

        return top;
    }

    private static boolean crossSectionAlreadyOpen(ServerLevel level, BlockPos.MutableBlockPos mutable, int centerX, int y, int centerZ) {
        for (int dx = -SHAFT_HALF_WIDTH; dx <= SHAFT_HALF_WIDTH; dx++) {
            for (int dz = -SHAFT_HALF_WIDTH; dz <= SHAFT_HALF_WIDTH; dz++) {
                mutable.set(centerX + dx, y, centerZ + dz);
                if (!level.getBlockState(mutable).isAir()) return false;
            }
        }
        return true;
    }
}
