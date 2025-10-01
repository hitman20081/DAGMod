package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SimpleNPC> SIMPLE_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "simple_npc"),
            EntityType.Builder.create(SimpleNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "simple_npc")))
    );

    public static void initialize() {
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(SIMPLE_NPC, SimpleNPC.createMobAttributes());

        DagMod.LOGGER.info("Registering entities for " + DagMod.MOD_ID);
    }
}