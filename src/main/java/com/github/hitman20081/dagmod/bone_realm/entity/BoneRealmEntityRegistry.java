package com.github.hitman20081.dagmod.bone_realm.entity;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

/**
 * Registry for Bone Realm entities
 */
public class BoneRealmEntityRegistry {

    // Entity Types
    public static final EntityType<SkeletonKingEntity> SKELETON_KING = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_king"),
            EntityType.Builder.of(SkeletonKingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f * 2.0f) // Width, Height (scaled by 2.0)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_king")))
    );

    public static final EntityType<SkeletonLordEntity> SKELETON_LORD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_lord"),
            EntityType.Builder.of(SkeletonLordEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f * 1.5f) // Width, Height (scaled by 1.5)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_lord")))
    );

    public static final EntityType<SkeletonSummonerEntity> SKELETON_SUMMONER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_summoner"),
            EntityType.Builder.of(SkeletonSummonerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.99f)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "skeleton_summoner")))
    );

    public static final EntityType<BonelingEntity> BONELING = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "boneling"),
            EntityType.Builder.of(BonelingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f * 0.8f) // Width, Height (scaled by 0.8)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "boneling")))
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