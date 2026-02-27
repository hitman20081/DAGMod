package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public class ModStructurePlacements {

    public static final StructurePlacementType<MultiExclusionRandomSpreadPlacement> MULTI_EXCLUSION_RANDOM_SPREAD =
            () -> MultiExclusionRandomSpreadPlacement.CODEC;

    public static void register() {
        Registry.register(
                Registries.STRUCTURE_PLACEMENT,
                Identifier.of(DagMod.MOD_ID, "multi_exclusion_random_spread"),
                MULTI_EXCLUSION_RANDOM_SPREAD
        );
        DagMod.LOGGER.info("DAGMod custom structure placements registered!");
    }
}
