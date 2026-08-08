package com.github.hitman20081.dagmod.pale_garden;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenTeleporter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Flattens solid ground in a bounded radius around the Pale Garden Castle (wherever it actually
 * generated — the castle is now a true singleton via concentric_rings, see
 * worldgen/structure_set/castle_pale_garden.json) so it never sits over a void or cave gap, and
 * never gets buried under naturally-generated terrain around it.
 *
 * Runs at most once per chunk, ever: gated on the {@code generated} flag from
 * {@link ServerChunkEvents#CHUNK_LOAD}, which vanilla only sets true the first time a chunk is
 * generated (never on a reload from disk) — this guarantees a chunk a player has since built on
 * is never touched again, with no extra persisted bookkeeping needed.
 *
 * IMPORTANT: the actual block edits (flattenChunk) are never run directly from the CHUNK_LOAD
 * callback. That callback can fire while the main thread is already nested inside another
 * blocking world.getChunk() call (e.g. PaleGardenTeleporter force-generating chunks) — calling
 * level.setBlock() in that state re-enters the chunk executor's task-draining loop and can
 * deadlock the server. onChunkLoad only enqueues the chunk position; a normal end-of-tick
 * handler drains the queue outside of any such nested call stack, where setBlock is safe.
 *
 * What actually protects the castle's own placed blocks is its real generated bounding box
 * (resolved lazily via StructureManager, see getCastleBoundingBox), not a guessed radius — a
 * fixed-radius circle either cuts into the castle (too small) or leaves it buried under whatever
 * terrain naturally generated around it (too large a "hands off" zone with no clearing).
 */
public class PaleGardenTerrainHandler {

    private static final Identifier CASTLE_STRUCTURE_ID = Identifier.fromNamespaceAndPath("dagmod", "castle_pale_garden");
    private static final int LOCATE_SEARCH_RADIUS_CHUNKS = 32; // castle placement uses distance:8 rings — generous margin
    private static final int BOUNDS_GENERATE_RADIUS_CHUNKS = 9; // covers max_distance_from_center: 128

    // Smaller than the original 400: chunk count (and therefore force-generation + flatten cost)
    // scales with radius^2, so this alone is a large chunk of the "took forever to load" fix, on
    // top of it just being a smaller, tighter flat zone as requested.
    private static final int FLATTEN_RADIUS = 180;
    private static final int FEATHER_WIDTH = 30;      // outer blend band, blocks

    // Matches the castle's start_height.absolute (worldgen/structure/castle_pale_garden.json).
    // NOT the dimension's sea_level (0) -- this noise config reuses vanilla overworld's
    // continents/erosion/ridges functions unscaled, so natural terrain here follows normal
    // overworld height distribution (commonly ~60-100, mountains well beyond) regardless of
    // sea_level. Y=100 sits inside that normal range, keeping the feather band's blend to
    // naturally-generated height (see computeTargetY) far shorter than a Y=0 target would.
    private static final int CASTLE_BASE_Y = 100;

    // Deliberately one below CASTLE_BASE_Y, not the same height: the castle's own NBT content is
    // wherever its pieces actually placed something (whatever that turns out to be, including
    // still-unfinished pieces the author is filling in directly), and trying to make our own
    // flattened ground land on EXACTLY that same Y kept producing seams anywhere the piece content
    // didn't perfectly cover its own footprint. Landing our ground one block lower instead gives
    // the castle a natural raised-foundation lip and means our flattening never has to guess where
    // the castle's own floor actually is -- it just always stays clear below it.
    private static final int GROUND_SURFACE_Y = CASTLE_BASE_Y - 1;
    private static final int CLEAR_CEILING = 60;     // blocks of air cleared above the target surface

    private static int castleX;
    private static int castleZ;
    private static boolean castleLocated = false;

    private static BoundingBox castleBoundingBox = null;
    private static List<BoundingBox> castlePieceBoxes = null;
    private static boolean castleBoundsResolved = false; // true once resolution has *succeeded*; retries otherwise

    // Chunks queued by onChunkLoad, drained (setBlock actually happens) from a normal tick —
    // see the class-level note on why this can't happen directly in the CHUNK_LOAD callback.
    private static final Deque<QueuedChunk> pendingChunks = new ArrayDeque<>();

    private record QueuedChunk(ServerLevel level, ChunkPos pos) {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(PaleGardenTerrainHandler::onServerStarted);
        ServerChunkEvents.CHUNK_LOAD.register(PaleGardenTerrainHandler::onChunkLoad);
        ServerTickEvents.END_SERVER_TICK.register(PaleGardenTerrainHandler::onServerTick);
    }

    private static void onServerStarted(MinecraftServer server) {
        ServerLevel paleGarden = server.getLevel(PaleGardenTeleporter.PALE_GARDEN);
        if (paleGarden == null) return;

        BlockPos castlePos = locateCastle(paleGarden);
        if (castlePos == null) {
            DagMod.LOGGER.warn("[DAGMod] Could not locate Pale Garden Castle — flat-zone terrain shaping disabled this session");
            return;
        }

        castleX = castlePos.getX();
        castleZ = castlePos.getZ();
        castleLocated = true;
        DagMod.LOGGER.info("[DAGMod] Pale Garden Castle located at (" + castleX + ", " + castleZ
                + ") — flattening terrain within " + FLATTEN_RADIUS + " blocks");
    }

    private static BlockPos locateCastle(ServerLevel world) {
        try {
            var registry = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            var entry = registry.get(CASTLE_STRUCTURE_ID).orElse(null);
            if (entry == null) return null;

            ChunkGenerator generator = world.getChunkSource().getGenerator();
            var result = generator.findNearestMapStructure(
                    world, HolderSet.direct(entry), BlockPos.ZERO, LOCATE_SEARCH_RADIUS_CHUNKS, false);
            return result != null ? result.getFirst() : null;
        } catch (Exception e) {
            DagMod.LOGGER.error("[DAGMod] Failed to locate Pale Garden Castle", e);
            return null;
        }
    }

    /**
     * Resolves (and caches) the castle's real combined piece bounding box. Lazy, not done at
     * server start, because the castle's own chunks won't have generated yet at that point and
     * the lookup would just fail. Force-generates a modest area around the castle first so the
     * lookup has something to find.
     */
    private static BoundingBox getCastleBoundingBox(ServerLevel level) {
        if (castleBoundsResolved) return castleBoundingBox;

        try {
            var registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            var entry = registry.get(CASTLE_STRUCTURE_ID).orElse(null);
            if (entry == null) {
                castleBoundsResolved = true; // nothing we can do, stop retrying
                return null;
            }
            Structure castleStructure = entry.value();

            BlockPos center = new BlockPos(castleX, CASTLE_BASE_Y, castleZ);
            int cx = center.getX() >> 4;
            int cz = center.getZ() >> 4;
            for (int dx = -BOUNDS_GENERATE_RADIUS_CHUNKS; dx <= BOUNDS_GENERATE_RADIUS_CHUNKS; dx++) {
                for (int dz = -BOUNDS_GENERATE_RADIUS_CHUNKS; dz <= BOUNDS_GENERATE_RADIUS_CHUNKS; dz++) {
                    level.getChunk(cx + dx, cz + dz);
                }
            }

            StructureStart start = level.structureManager().getStructureAt(center, castleStructure);
            if (start != null && start.isValid()) {
                castleBoundingBox = start.getBoundingBox();
                castlePieceBoxes = start.getPieces().stream().map(StructurePiece::getBoundingBox).toList();
                castleBoundsResolved = true;
                DagMod.LOGGER.info("[DAGMod] Pale Garden Castle bounding box resolved: " + castleBoundingBox
                        + " (" + castlePieceBoxes.size() + " pieces)");
            }
            // else: leave unresolved, try again next call (e.g. chunk genuinely wasn't ready yet)
        } catch (Exception e) {
            DagMod.LOGGER.error("[DAGMod] Failed to resolve Pale Garden Castle bounding box", e);
        }

        return castleBoundingBox;
    }

    /**
     * The castle is a sprawling, irregularly-shaped multi-piece jigsaw structure (courtyards,
     * gaps between wings) -- its combined bounding box (getCastleBoundingBox) is just the outer
     * rectangle and includes plenty of area no piece actually occupies. Treating that whole
     * rectangle as "protected, leave terrain alone" left those gap columns as raw unflattened
     * natural terrain, which -- being close to but not exactly GROUND_SURFACE_Y -- showed up as a
     * ~1 block seam between the castle and the surrounding flattened disc. Checking against each
     * piece's own real box (not the merged rectangle) lets those gap columns get flattened
     * normally, same as everywhere else outside the castle.
     */
    private static List<BoundingBox> getCastlePieceBoxes(ServerLevel level) {
        getCastleBoundingBox(level); // ensures castlePieceBoxes is resolved alongside it
        return castlePieceBoxes;
    }

    /** True if there's any non-air block between fromY (exclusive) and toY (inclusive) at this column. One-time cost per column, bounded by the castle's own resolved height. */
    private static boolean hasContentAbove(ServerLevel level, BlockPos.MutableBlockPos mutable, int x, int z, int fromY, int toY) {
        for (int y = fromY + 1; y <= toY; y++) {
            mutable.set(x, y, z);
            if (!level.getBlockState(mutable).isAir()) return true;
        }
        return false;
    }

    private static boolean insidePieceFootprint(List<BoundingBox> pieces, int worldX, int worldZ) {
        if (pieces == null) return false;
        for (BoundingBox box : pieces) {
            if (worldX >= box.minX() && worldX <= box.maxX() && worldZ >= box.minZ() && worldZ <= box.maxZ()) {
                return true;
            }
        }
        return false;
    }

    private static void onChunkLoad(ServerLevel level, LevelChunk chunk, boolean generated) {
        if (!generated) return; // only ever act the first time a chunk is created
        if (!castleLocated) return;
        if (level.dimension() != PaleGardenTeleporter.PALE_GARDEN) return;

        ChunkPos pos = chunk.getPos();
        double chunkDist = Math.sqrt(distSq(pos.getMiddleBlockX(), pos.getMiddleBlockZ(), castleX, castleZ));
        if (chunkDist - 16 > FLATTEN_RADIUS) return;

        // Queue only -- see class-level note. Do NOT call flattenChunk/setBlock here.
        pendingChunks.addLast(new QueuedChunk(level, pos));
    }

    private static void onServerTick(MinecraftServer server) {
        // Drain the whole queue every tick. By this point we're at the top of a normal server
        // tick, not nested inside any chunk-generation call, so setBlock is safe here.
        QueuedChunk next;
        while ((next = pendingChunks.pollFirst()) != null) {
            flattenChunk(next.level(), next.pos());
        }
    }

    private static void flattenChunk(ServerLevel level, ChunkPos pos) {
        BoundingBox castleBounds = getCastleBoundingBox(level); // null is fine -- just means "protect nothing extra"
        List<BoundingBox> pieceBoxes = getCastlePieceBoxes(level);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int worldX = pos.getMinBlockX() + lx;
                int worldZ = pos.getMinBlockZ() + lz;

                double dist = Math.sqrt(distSq(worldX, worldZ, castleX, castleZ));
                if (dist > FLATTEN_RADIUS) continue;

                // Piece-bbox membership narrows down WHERE real castle content could plausibly
                // be (see insidePieceFootprint), but the castle is still being built out and
                // large parts of a piece's own footprint can have nothing placed at all. Treating
                // "inside a piece's box" alone as "protected, don't touch" left those unbuilt
                // columns as a bare, uncapped gap -- fillVoidBelow's own dirt fill with nothing
                // above it, instead of looking like normal ground. Scanning for any real non-air
                // content above GROUND_SURFACE_Y (not just checking one exact Y) is robust to the
                // castle's own floor/walls not lining up on a single height, and correctly turns
                // "nothing built here yet" into ordinary flattened ground instead of a pit.
                boolean insidePieceBox = insidePieceFootprint(pieceBoxes, worldX, worldZ);
                boolean hasCastleContent = insidePieceBox && castleBounds != null
                        && hasContentAbove(level, mutable, worldX, worldZ, GROUND_SURFACE_Y, castleBounds.maxY());

                // Always safe to do, everywhere: patch any void directly beneath GROUND_SURFACE_Y.
                // Never touches GROUND_SURFACE_Y or above, so it can never disturb the castle's
                // own placed content (which starts a further block up, at CASTLE_BASE_Y) even when
                // run underneath it.
                fillVoidBelow(level, mutable, worldX, worldZ);

                if (!hasCastleContent) {
                    // No real castle content found here (outside every piece, a gap between
                    // pieces, or a piece column nothing's been built on yet): clear/flatten
                    // everything above too, so this player-visible ground never buries the castle
                    // under natural terrain, and unbuilt/gap columns read as ordinary flattened
                    // ground instead of a bare pit.
                    int targetY = computeTargetY(level, worldX, worldZ, dist);
                    clearAndFlattenAbove(level, mutable, worldX, worldZ, targetY);
                } else {
                    // Real castle content found in this column: never touch anything at or below
                    // the structure's own resolved roof height (that's the castle's own placed
                    // content), but DO clear whatever naturally-generated terrain grew above it —
                    // terrain_adaptation is "none" on this structure, so nothing removes a hill
                    // that happened to generate over the castle on its own. Using the merged
                    // bounding box's maxY (tallest piece, e.g. a tower) keeps this safe across
                    // every piece: it can never cut into anything the structure itself placed.
                    clearAboveCastleRoof(level, mutable, worldX, worldZ, castleBounds.maxY());
                }
            }
        }
    }

    /** Fills air gaps from bedrock up to (not including) GROUND_SURFACE_Y with solid ground. Safe everywhere, including under the castle. */
    private static void fillVoidBelow(ServerLevel level, BlockPos.MutableBlockPos mutable, int x, int z) {
        int minY = level.getMinY();

        mutable.set(x, minY, z);
        if (level.getBlockState(mutable).isAir()) {
            level.setBlock(mutable, Blocks.BEDROCK.defaultBlockState(), 2);
        }

        for (int y = minY + 1; y < GROUND_SURFACE_Y - 3; y++) {
            mutable.set(x, y, z);
            if (level.getBlockState(mutable).isAir()) {
                level.setBlock(mutable, Blocks.STONE.defaultBlockState(), 2);
            }
        }
        for (int y = Math.max(minY + 1, GROUND_SURFACE_Y - 3); y < GROUND_SURFACE_Y; y++) {
            mutable.set(x, y, z);
            if (level.getBlockState(mutable).isAir()) {
                level.setBlock(mutable, Blocks.DIRT.defaultBlockState(), 2);
            }
        }
    }

    /** Flat at GROUND_SURFACE_Y near the castle, blending toward the naturally-generated height across the outer feather band. */
    private static int computeTargetY(ServerLevel level, int worldX, int worldZ, double distFromCastle) {
        double featherStart = FLATTEN_RADIUS - FEATHER_WIDTH;
        if (distFromCastle <= featherStart) return GROUND_SURFACE_Y;

        int naturalHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
        double t = Math.min(1.0, Math.max(0.0, (distFromCastle - featherStart) / FEATHER_WIDTH));
        return (int) Math.round(GROUND_SURFACE_Y + t * (naturalHeight - GROUND_SURFACE_Y));
    }

    /** Overwrites the surface layer at targetY and clears everything above it up to CLEAR_CEILING. Only ever called outside the castle's own bounding box. */
    private static void clearAndFlattenAbove(ServerLevel level, BlockPos.MutableBlockPos mutable, int x, int z, int targetY) {
        for (int y = Math.max(level.getMinY() + 1, targetY - 3); y < targetY; y++) {
            mutable.set(x, y, z);
            level.setBlock(mutable, Blocks.DIRT.defaultBlockState(), 2);
        }

        mutable.set(x, targetY, z);
        level.setBlock(mutable, Blocks.PALE_MOSS_BLOCK.defaultBlockState(), 2);

        for (int y = targetY + 1; y <= targetY + CLEAR_CEILING; y++) {
            mutable.set(x, y, z);
            level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
        }
    }

    /** Clears any natural terrain sitting above the castle's own tallest resolved roof point. Never touches roofY or below. */
    private static void clearAboveCastleRoof(ServerLevel level, BlockPos.MutableBlockPos mutable, int x, int z, int roofY) {
        for (int y = roofY + 1; y <= roofY + CLEAR_CEILING; y++) {
            mutable.set(x, y, z);
            if (!level.getBlockState(mutable).isAir()) {
                level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static double distSq(int x1, int z1, int x2, int z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return dx * dx + dz * dz;
    }
}
