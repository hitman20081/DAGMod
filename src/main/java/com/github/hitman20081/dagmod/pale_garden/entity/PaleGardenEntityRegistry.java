package com.github.hitman20081.dagmod.pale_garden.entity;

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
 * Registry for Pale Garden entities
 */
public class PaleGardenEntityRegistry {

    // Base vanilla Spider hitbox is 1.4 wide x 0.9 tall — bake SpiderQueenEntity.SCALE into the
    // registered size directly, same approach BoneRealmEntityRegistry uses for the Skeleton bosses.
    public static final EntityType<SpiderQueenEntity> SPIDER_QUEEN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "spider_queen"),
            EntityType.Builder.of(SpiderQueenEntity::new, MobCategory.MONSTER)
                    .sized(1.4f * (float) SpiderQueenEntity.SCALE, 0.9f * (float) SpiderQueenEntity.SCALE)
                    .clientTrackingRange(12)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "spider_queen")))
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(
                SPIDER_QUEEN,
                SpiderQueenEntity.createSpiderQueenAttributes()
        );
    }
}
