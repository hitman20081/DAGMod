package com.github.hitman20081.dagmod.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * A structure placement type that extends random_spread with:
 * - Multiple exclusion zones (vanilla only allows one)
 * - Optional avoid_water flag: rejects candidates where the 3×3-chunk area
 *   around the start chunk has surface water (WORLD_SURFACE_WG > OCEAN_FLOOR_WG)
 *
 * JSON usage:
 * {
 *   "type": "dagmod:multi_exclusion_random_spread",
 *   "salt": 12345,
 *   "spacing": 32,
 *   "separation": 12,
 *   "avoid_water": true,
 *   "exclusion_zones": [
 *     { "other_set": "dagmod:hall_spawn",      "chunk_count": 25 },
 *     { "other_set": "dagmod:village_npc_set", "chunk_count": 10 }
 *   ]
 * }
 */
public class MultiExclusionRandomSpreadPlacement extends RandomSpreadStructurePlacement {

    /**
     * Tracks exclusion-check call depth per thread to prevent infinite mutual recursion.
     * When depth > 0, skip our own exclusion zones (we are being evaluated as part of
     * another set's exclusion check, not as the primary candidate).
     */
    private static final ThreadLocal<Integer> EXCLUSION_DEPTH = ThreadLocal.withInitial(() -> 0);

    public static final MapCodec<MultiExclusionRandomSpreadPlacement> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.INT.fieldOf("salt")
                .forGetter(p -> p.salt()),
            Codec.intRange(0, 4096).fieldOf("spacing")
                .forGetter(p -> p.spacing()),
            Codec.intRange(0, 4096).fieldOf("separation")
                .forGetter(p -> p.separation()),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR)
                .forGetter(p -> p.spreadType()),
            Codec.BOOL.optionalFieldOf("avoid_water", false)
                .forGetter(p -> p.avoidWater),
            StructurePlacement.ExclusionZone.CODEC.listOf()
                .optionalFieldOf("exclusion_zones", List.of())
                .forGetter(p -> p.exclusionZones)
        ).apply(instance, (salt, spacing, separation, spreadType, avoidWater, exclusionZones) ->
            new MultiExclusionRandomSpreadPlacement(salt, spacing, separation, spreadType, avoidWater, exclusionZones)
        )
    );

    private final boolean avoidWater;
    private final List<StructurePlacement.ExclusionZone> exclusionZones;

    public MultiExclusionRandomSpreadPlacement(
            int salt,
            int spacing,
            int separation,
            RandomSpreadType spreadType,
            boolean avoidWater,
            List<StructurePlacement.ExclusionZone> exclusionZones) {
        super(
            BlockPos.ZERO,
            StructurePlacement.FrequencyReductionMethod.DEFAULT,
            1.0f,
            salt,
            Optional.empty(),
            spacing,
            separation,
            spreadType
        );
        this.avoidWater = avoidWater;
        this.exclusionZones = exclusionZones;
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
        // Water check runs unconditionally — if this candidate would be underwater,
        // other structures also shouldn't exclude around it (it won't exist).
        if (avoidWater && hasSurfaceWater(calculator, chunkX, chunkZ)) {
            return false;
        }

        if (EXCLUSION_DEPTH.get() == 0) {
            EXCLUSION_DEPTH.set(1);
            try {
                for (StructurePlacement.ExclusionZone zone : exclusionZones) {
                    if (isExcluded(zone, calculator, chunkX, chunkZ)) {
                        return false;
                    }
                }
            } finally {
                EXCLUSION_DEPTH.set(0);
            }
        }
        return super.isPlacementChunk(calculator, chunkX, chunkZ);
    }

    @Override
    public StructurePlacementType<?> type() {
        return ModStructurePlacements.MULTI_EXCLUSION_RANDOM_SPREAD;
    }

    /**
     * Checks the candidate chunk and its 8 neighbours for surface water.
     * If any of the 3×3 chunks has WORLD_SURFACE_WG above OCEAN_FLOOR_WG, water is present.
     * Uses reflection to extract the generator/heightAccessor/randomState from the calculator
     * without depending on specific field names (works across MC version remappings).
     */
    private static boolean hasSurfaceWater(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
        try {
            ChunkGenerator generator = null;
            LevelHeightAccessor heightAccessor = null;
            RandomState randomState = null;

            for (Field f : ChunkGeneratorStructureState.class.getDeclaredFields()) {
                f.setAccessible(true);
                Object val = f.get(calculator);
                if (val instanceof ChunkGenerator cg && generator == null) generator = cg;
                else if (val instanceof LevelHeightAccessor lha && heightAccessor == null) heightAccessor = lha;
                else if (val instanceof RandomState rs && randomState == null) randomState = rs;
            }

            if (generator == null || heightAccessor == null || randomState == null) return false;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int blockX = (chunkX + dr) * 16 + 8;
                    int blockZ = (chunkZ + dc) * 16 + 8;
                    int surfaceY = generator.getBaseHeight(blockX, blockZ,
                            Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
                    int floorY = generator.getBaseHeight(blockX, blockZ,
                            Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
                    if (surfaceY > floorY) return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false; // fail open — allow placement if introspection fails
        }
    }

    /** Calls ExclusionZone.isPlacementForbidden via reflection (renamed/private in MC 26.x). */
    private static boolean isExcluded(StructurePlacement.ExclusionZone zone,
            ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
        try {
            var m = StructurePlacement.ExclusionZone.class.getDeclaredMethod(
                    "isPlacementForbidden", ChunkGeneratorStructureState.class, int.class, int.class);
            m.setAccessible(true);
            return (boolean) m.invoke(zone, calculator, chunkX, chunkZ);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
