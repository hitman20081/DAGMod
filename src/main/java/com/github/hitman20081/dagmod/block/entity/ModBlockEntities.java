package com.github.hitman20081.dagmod.block.entity;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<GemPolishingStationBlockEntity> GEM_POLISHING_STATION =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DagMod.MOD_ID, "gem_polishing_station"),
                    FabricBlockEntityTypeBuilder.create(GemPolishingStationBlockEntity::new, ModBlocks.GEM_POLISHING_STATION).build()
            );

    public static final BlockEntityType<GemInfusingStationBlockEntity> GEM_INFUSING_STATION =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DagMod.MOD_ID, "gem_infusing_station"),
                    FabricBlockEntityTypeBuilder.create(GemInfusingStationBlockEntity::new, ModBlocks.GEM_INFUSING_STATION).build()
            );

    public static final BlockEntityType<GemCuttingStationBlockEntity> GEM_CUTTING_STATION =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DagMod.MOD_ID, "gem_cutting_station"),
                    FabricBlockEntityTypeBuilder.create(GemCuttingStationBlockEntity::new, ModBlocks.GEM_CUTTING_STATION).build()
            );

    public static final BlockEntityType<IronChestBlockEntity> IRON_CHEST =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DagMod.MOD_ID, "iron_chest"),
                    FabricBlockEntityTypeBuilder.create(IronChestBlockEntity::new, ModBlocks.IRON_CHEST).build()
            );

    public static void registerBlockEntities() {
        DagMod.LOGGER.info("Registering Block Entities for " + DagMod.MOD_ID);
    }
}
