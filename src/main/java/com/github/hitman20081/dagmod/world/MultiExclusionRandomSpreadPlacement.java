package com.github.hitman20081.dagmod.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

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
                .forGetter(p -> p.getSalt()),
            Codec.intRange(0, 4096).fieldOf("spacing")
                .forGetter(p -> p.getSpacing()),
            Codec.intRange(0, 4096).fieldOf("separation")
                .forGetter(p -> p.getSeparation()),
            SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR)
                .forGetter(p -> p.getSpreadType()),
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
            SpreadType spreadType,
            List<StructurePlacement.ExclusionZone> exclusionZones) {
        super(
            BlockPos.ORIGIN,
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
    protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
        if (EXCLUSION_DEPTH.get() == 0) {
            EXCLUSION_DEPTH.set(1);
            try {
                for (StructurePlacement.ExclusionZone zone : exclusionZones) {
                    if (zone.shouldExclude(calculator, chunkX, chunkZ)) {
                        return false;
                    }
                }
            } finally {
                EXCLUSION_DEPTH.set(0);
            }
        }
        return super.isStartChunk(calculator, chunkX, chunkZ);
    }

    @Override
    public StructurePlacementType<?> getType() {
        return ModStructurePlacements.MULTI_EXCLUSION_RANDOM_SPREAD;
    }
}
