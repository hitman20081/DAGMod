package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class ModStructurePlacements {

    public static final StructurePlacementType<MultiExclusionRandomSpreadPlacement> MULTI_EXCLUSION_RANDOM_SPREAD =
            () -> MultiExclusionRandomSpreadPlacement.CODEC;

    public static void register() {
        Registry.register(
                Registries.STRUCTURE_PLACEMENT,
                Identifier.of(DagMod.MOD_ID, "multi_exclusion_random_spread"),
                MULTI_EXCLUSION_RANDOM_SPREAD
        );

        NotInStructurePlacementModifier.TYPE = Registry.register(
                Registries.PLACEMENT_MODIFIER_TYPE,
                Identifier.of(DagMod.MOD_ID, "not_in_structure"),
                () -> NotInStructurePlacementModifier.CODEC
        );

        DagMod.LOGGER.info("DAGMod custom structure placements registered!");
    }
}
