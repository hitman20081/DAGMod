package com.github.hitman20081.dagmod.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;

import java.util.List;
import java.util.Optional;

/**
 * A structure placement type that extends random_spread to support multiple exclusion zones.
 * Vanilla only allows one exclusion_zone per structure set; this allows an exclusion_zones list.
 *
 * JSON usage:
 * {
 *   "type": "dagmod:multi_exclusion_random_spread",
 *   "salt": 12345,
 *   "spacing": 32,
 *   "separation": 12,
 *   "exclusion_zones": [
 *     { "other_set": "dagmod:hall_spawn",      "chunk_count": 25 },
 *     { "other_set": "dagmod:village_npc_set", "chunk_count": 10 }
 *   ]
 * }
 *
 * Mutual exclusion (A excludes B and B excludes A) is safe: when B is evaluated
 * as part of A's exclusion check, B skips its own exclusion zones to break the cycle.
 */
public class MultiExclusionRandomSpreadPlacement extends RandomSpreadStructurePlacement {

    /**
     * Tracks exclusion-check call depth per thread to prevent infinite mutual recursion.
     * When depth > 0, this instance is being evaluated as part of another set's exclusion
     * check, so we skip our own exclusion zones.
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
            StructurePlacement.ExclusionZone.CODEC.listOf()
                .optionalFieldOf("exclusion_zones", List.of())
                .forGetter(p -> p.exclusionZones)
        ).apply(instance, (salt, spacing, separation, spreadType, exclusionZones) ->
            new MultiExclusionRandomSpreadPlacement(salt, spacing, separation, spreadType, exclusionZones)
        )
    );

    private final List<StructurePlacement.ExclusionZone> exclusionZones;

    public MultiExclusionRandomSpreadPlacement(
            int salt,
            int spacing,
            int separation,
            RandomSpreadType spreadType,
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
        this.exclusionZones = exclusionZones;
    }

    /**
     * Checks all exclusion zones before delegating to the parent's grid calculation.
     * Returns false (don't generate) if any zone finds a conflicting structure nearby.
     *
     * Uses EXCLUSION_DEPTH to guard against infinite recursion when two sets mutually
     * exclude each other (A excludes B, B excludes A). When depth > 0 we are being
     * evaluated as part of another set's exclusion check, so we skip our own zones.
     */
    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
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
