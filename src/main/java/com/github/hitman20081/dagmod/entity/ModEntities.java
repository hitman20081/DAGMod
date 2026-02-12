package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.DragonEggBlock;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {

    public static final EntityType<SimpleNPC> SIMPLE_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "simple_npc"),
            EntityType.Builder.create(SimpleNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "simple_npc")))
    );

    public static final EntityType<InnkeeperGarrickNPC> INNKEEPER_GARRICK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "innkeeper_garrick"),
            EntityType.Builder.create(InnkeeperGarrickNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "innkeeper_garrick")))
    );

    public static final EntityType<MysteryMerchantNPC> MYSTERY_MERCHANT_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "mystery_merchant_npc"),
            EntityType.Builder.create(MysteryMerchantNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "mystery_merchant_npc")))
    );

    public static final EntityType<MinerNPC> MINER_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "miner_npc"),
            EntityType.Builder.create(MinerNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "miner_npc")))
    );

    public static final EntityType<LumberjackNPC> LUMBERJACK_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "lumberjack_npc"),
            EntityType.Builder.create(LumberjackNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "lumberjack_npc")))
    );

    public static final EntityType<EnchantsmithNPC> ENCHANTSMITH_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "enchantsmith_npc"),
            EntityType.Builder.create(EnchantsmithNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "enchantsmith_npc")))
    );

    public static final EntityType<LuxuryMerchantNPC> LUXURY_MERCHANT_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "luxury_merchant_npc"),
            EntityType.Builder.create(LuxuryMerchantNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "luxury_merchant_npc")))
    );

    public static final EntityType<VillageMerchantNPC> VILLAGE_MERCHANT_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "village_merchant_npc"),
            EntityType.Builder.create(VillageMerchantNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "village_merchant_npc")))
    );

    public static final EntityType<HunterNPC> HUNTER_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "hunter_npc"),
            EntityType.Builder.create(HunterNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "hunter_npc")))
    );

    public static final EntityType<VoodooIllusionerNPC> VOODOO_ILLUSIONER_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "voodoo_illusioner_npc"),
            EntityType.Builder.create(VoodooIllusionerNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "voodoo_illusioner_npc")))
    );

    public static final EntityType<ArmorerNPC> ARMORER_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "armorer_npc"),
            EntityType.Builder.create(ArmorerNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "armorer_npc")))
    );

    public static final EntityType<CuteVillagerNPC> CUTE_VILLAGER_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "cute_villager_npc"),
            EntityType.Builder.create(CuteVillagerNPC::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.95f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "cute_villager_npc")))
    );

    // Dragon Entities
    public static final EntityType<DragonGuardianEntity> DRAGON_GUARDIAN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "dragon_guardian"),
            EntityType.Builder.create(DragonGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(3.0f, 1.5f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "dragon_guardian")))
    );

    public static final EntityType<WildDragonEntity> WILD_DRAGON = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "wild_dragon"),
            EntityType.Builder.create(WildDragonEntity::new, SpawnGroup.MONSTER)
                    .dimensions(2.5f, 1.5f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DagMod.MOD_ID, "wild_dragon")))
    );

    // Dragon Egg Block Entity
    public static final BlockEntityType<DragonEggBlockEntity> DRAGON_EGG_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "dragon_egg_entity"),
            FabricBlockEntityTypeBuilder.create(DragonEggBlockEntity::new, ModBlocks.DRAGON_EGG_BLOCK).build()
    );

    public static void initialize() {
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(SIMPLE_NPC, SimpleNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(INNKEEPER_GARRICK, InnkeeperGarrickNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(MYSTERY_MERCHANT_NPC, MysteryMerchantNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(MINER_NPC, MinerNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(LUMBERJACK_NPC, LumberjackNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ENCHANTSMITH_NPC, EnchantsmithNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(LUXURY_MERCHANT_NPC, LuxuryMerchantNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(VILLAGE_MERCHANT_NPC, VillageMerchantNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(HUNTER_NPC, HunterNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(VOODOO_ILLUSIONER_NPC, VoodooIllusionerNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ARMORER_NPC, ArmorerNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(CUTE_VILLAGER_NPC, CuteVillagerNPC.createMobAttributes());

        // Register dragon entity attributes
        FabricDefaultAttributeRegistry.register(DRAGON_GUARDIAN, DragonGuardianEntity.createDragonGuardianAttributes());
        FabricDefaultAttributeRegistry.register(WILD_DRAGON, WildDragonEntity.createWildDragonAttributes());

        // Block natural spawning of Dragon Guardian - it should only spawn via DragonGuardianSpawner
        SpawnRestriction.register(
                DRAGON_GUARDIAN,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                (type, world, spawnReason, pos, random) -> false
        );

        DagMod.LOGGER.info("Registering entities for " + DagMod.MOD_ID);
    }
}