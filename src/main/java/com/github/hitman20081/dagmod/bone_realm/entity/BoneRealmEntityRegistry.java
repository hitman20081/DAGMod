package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Registry for Bone Realm entities
 */
public class BoneRealmEntityRegistry {

    // Entity Types
    public static final EntityType<SkeletonKingEntity> SKELETON_KING = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "skeleton_king"),
            EntityType.Builder.create(SkeletonKingEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f * 2.0f) // Width, Height (scaled by 2.0)
                    .maxTrackingRange(10)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(DagMod.MOD_ID, "skeleton_king")))
    );

    public static final EntityType<SkeletonLordEntity> SKELETON_LORD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "skeleton_lord"),
            EntityType.Builder.create(SkeletonLordEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f * 1.5f) // Width, Height (scaled by 1.5)
                    .maxTrackingRange(10)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(DagMod.MOD_ID, "skeleton_lord")))
    );

    public static final EntityType<SkeletonSummonerEntity> SKELETON_SUMMONER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "skeleton_summoner"),
            EntityType.Builder.create(SkeletonSummonerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.99f)
                    .maxTrackingRange(8)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(DagMod.MOD_ID, "skeleton_summoner")))
    );

    public static final EntityType<BonelingEntity> BONELING = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DagMod.MOD_ID, "boneling"),
            EntityType.Builder.create(BonelingEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f * 0.8f) // Width, Height (scaled by 0.8)
                    .maxTrackingRange(8)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(DagMod.MOD_ID, "boneling")))
    );

    public static void register() {
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(
                SKELETON_KING,
                SkeletonKingEntity.createSkeletonKingAttributes()
        );

        FabricDefaultAttributeRegistry.register(
                SKELETON_LORD,
                SkeletonLordEntity.createSkeletonLordAttributes()
        );

        FabricDefaultAttributeRegistry.register(
                SKELETON_SUMMONER,
                SkeletonSummonerEntity.createSkeletonSummonerAttributes());

        FabricDefaultAttributeRegistry.register(
                BONELING,
                BonelingEntity.createBonelingAttributes()
        );

    }
}