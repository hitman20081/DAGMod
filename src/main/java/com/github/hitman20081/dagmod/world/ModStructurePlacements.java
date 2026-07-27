package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ModStructurePlacements {

    public static final StructurePlacementType<MultiExclusionRandomSpreadPlacement> MULTI_EXCLUSION_RANDOM_SPREAD =
            () -> MultiExclusionRandomSpreadPlacement.CODEC;

    public static void register() {
        Registry.register(
                BuiltInRegistries.STRUCTURE_PLACEMENT,
                Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "multi_exclusion_random_spread"),
                MULTI_EXCLUSION_RANDOM_SPREAD
        );

        NotInStructurePlacementModifier.TYPE = Registry.register(
                BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "not_in_structure"),
                () -> NotInStructurePlacementModifier.CODEC
        );

        DagMod.LOGGER.info("DAGMod custom structure placements registered!");
    }
}
