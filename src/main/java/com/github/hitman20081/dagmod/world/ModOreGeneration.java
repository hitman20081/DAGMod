package com.github.hitman20081.dagmod.world;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModOreGeneration {

    private static final RegistryKey<PlacedFeature> ORE_CITRINE = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_citrine"));
    private static final RegistryKey<PlacedFeature> ORE_RUBY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_ruby"));
    private static final RegistryKey<PlacedFeature> ORE_SAPPHIRE = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_sapphire"));
    private static final RegistryKey<PlacedFeature> ORE_TANZANITE = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_tanzanite"));
    private static final RegistryKey<PlacedFeature> ORE_ZIRCON = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_zircon"));
    private static final RegistryKey<PlacedFeature> ORE_PINK_GARNET = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Identifier.of(DagMod.MOD_ID, "ore_pink_garnet"));

    // Exclude deep_dark to prevent sculk block entity conflicts during worldgen
    private static final java.util.function.Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext>
            OVERWORLD_NO_DEEP_DARK = BiomeSelectors.foundInOverworld()
                    .and(ctx -> !ctx.getBiomeKey().equals(BiomeKeys.DEEP_DARK));

    public static void register() {
        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_CITRINE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_RUBY);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_SAPPHIRE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_TANZANITE);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_ZIRCON);

        BiomeModifications.addFeature(
                OVERWORLD_NO_DEEP_DARK,
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_PINK_GARNET);

        DagMod.LOGGER.info("DAGMod ore generation registered!");
    }
}
