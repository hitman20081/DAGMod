package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.DragonEggBlock;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntities {

    public static final EntityType<SimpleNPC> SIMPLE_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "simple_npc"),
            EntityType.Builder.of(SimpleNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "simple_npc")))
    );

    public static final EntityType<InnkeeperGarrickNPC> INNKEEPER_GARRICK = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "innkeeper_garrick"),
            EntityType.Builder.of(InnkeeperGarrickNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "innkeeper_garrick")))
    );

    public static final EntityType<MysteryMerchantNPC> MYSTERY_MERCHANT_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "mystery_merchant_npc"),
            EntityType.Builder.of(MysteryMerchantNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "mystery_merchant_npc")))
    );

    public static final EntityType<MinerNPC> MINER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "miner_npc"),
            EntityType.Builder.of(MinerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "miner_npc")))
    );

    public static final EntityType<LumberjackNPC> LUMBERJACK_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "lumberjack_npc"),
            EntityType.Builder.of(LumberjackNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "lumberjack_npc")))
    );

    public static final EntityType<EnchantsmithNPC> ENCHANTSMITH_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "enchantsmith_npc"),
            EntityType.Builder.of(EnchantsmithNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "enchantsmith_npc")))
    );

    public static final EntityType<LuxuryMerchantNPC> LUXURY_MERCHANT_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "luxury_merchant_npc"),
            EntityType.Builder.of(LuxuryMerchantNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "luxury_merchant_npc")))
    );

    public static final EntityType<VillageMerchantNPC> VILLAGE_MERCHANT_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "village_merchant_npc"),
            EntityType.Builder.of(VillageMerchantNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "village_merchant_npc")))
    );

    public static final EntityType<HunterNPC> HUNTER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "hunter_npc"),
            EntityType.Builder.of(HunterNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "hunter_npc")))
    );

    public static final EntityType<VoodooIllusionerNPC> VOODOO_ILLUSIONER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "voodoo_illusioner_npc"),
            EntityType.Builder.of(VoodooIllusionerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "voodoo_illusioner_npc")))
    );

    public static final EntityType<ArmorerNPC> ARMORER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "armorer_npc"),
            EntityType.Builder.of(ArmorerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "armorer_npc")))
    );

    public static final EntityType<CuteVillagerNPC> CUTE_VILLAGER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "cute_villager_npc"),
            EntityType.Builder.of(CuteVillagerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "cute_villager_npc")))
    );

    public static final EntityType<BakerNPC> BAKER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "baker_npc"),
            EntityType.Builder.of(BakerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "baker_npc")))
    );

    public static final EntityType<JewelerNPC> JEWELER_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "jeweler_npc"),
            EntityType.Builder.of(JewelerNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "jeweler_npc")))
    );

    public static final EntityType<AlchemistNPC> ALCHEMIST_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "alchemist_npc"),
            EntityType.Builder.of(AlchemistNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "alchemist_npc")))
    );

    public static final EntityType<BlacksmithNPC> BLACKSMITH_NPC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "blacksmith_npc"),
            EntityType.Builder.of(BlacksmithNPC::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "blacksmith_npc")))
    );

    // Dragon Entities
    public static final EntityType<DragonGuardianEntity> DRAGON_GUARDIAN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dragon_guardian"),
            EntityType.Builder.of(DragonGuardianEntity::new, MobCategory.MONSTER)
                    .sized(3.0f, 1.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dragon_guardian")))
    );

    public static final EntityType<WildDragonEntity> WILD_DRAGON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "wild_dragon"),
            EntityType.Builder.of(WildDragonEntity::new, MobCategory.MONSTER)
                    .sized(2.5f, 1.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "wild_dragon")))
    );

    public static final EntityType<RedDragonEntity> RED_DRAGON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "red_dragon"),
            EntityType.Builder.of(RedDragonEntity::new, MobCategory.MONSTER)
                    .sized(2.5f, 1.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "red_dragon")))
    );

    // Dragon Egg Block Entity
    public static final BlockEntityType<DragonEggBlockEntity> DRAGON_EGG_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dragon_egg_entity"),
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
        FabricDefaultAttributeRegistry.register(BAKER_NPC, BakerNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ALCHEMIST_NPC, AlchemistNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(JEWELER_NPC, JewelerNPC.createMobAttributes());
        FabricDefaultAttributeRegistry.register(BLACKSMITH_NPC, BlacksmithNPC.createMobAttributes());

        // Register dragon entity attributes
        FabricDefaultAttributeRegistry.register(DRAGON_GUARDIAN, DragonGuardianEntity.createDragonGuardianAttributes());
        FabricDefaultAttributeRegistry.register(WILD_DRAGON, WildDragonEntity.createWildDragonAttributes());
        FabricDefaultAttributeRegistry.register(RED_DRAGON, WildDragonEntity.createWildDragonAttributes());

        // Block natural spawning of Dragon Guardian - it should only spawn via DragonGuardianSpawner
        SpawnPlacements.register(
                DRAGON_GUARDIAN,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, world, spawnReason, pos, random) -> false
        );

        // Block natural spawning of Red Dragon - it only spawns via quest acceptance
        SpawnPlacements.register(
                RED_DRAGON,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, world, spawnReason, pos, random) -> false
        );

        DagMod.LOGGER.info("Registering entities for " + DagMod.MOD_ID);
    }
}