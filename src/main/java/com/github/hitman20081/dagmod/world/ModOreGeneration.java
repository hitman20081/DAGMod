package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModOreGeneration {

    private static final ResourceKey<PlacedFeature> ORE_CITRINE = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_citrine"));
    private static final ResourceKey<PlacedFeature> ORE_RUBY = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_ruby"));
    private static final ResourceKey<PlacedFeature> ORE_SAPPHIRE = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_sapphire"));
    private static final ResourceKey<PlacedFeature> ORE_TANZANITE = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_tanzanite"));
    private static final ResourceKey<PlacedFeature> ORE_ZIRCON = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_zircon"));
    private static final ResourceKey<PlacedFeature> ORE_PINK_GARNET = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "ore_pink_garnet"));

    // Exclude deep_dark to prevent sculk block entity conflicts during worldgen
    private static final java.util.function.Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext>
            OVERWORLD_NO_DEEP_DARK = BiomeSelectors.foundInOverworld()
                    .and(ctx -> !ctx.getBiomeKey().equals(Biomes.DEEP_DARK));

    public static void register() {
        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_CITRINE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_RUBY);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_SAPPHIRE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_TANZANITE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_ZIRCON);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ORE_PINK_GARNET);

        DagMod.LOGGER.info("DAGMod ore generation registered!");
    }
}
