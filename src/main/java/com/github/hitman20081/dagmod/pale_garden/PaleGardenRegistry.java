package com.github.hitman20081.dagmod.pale_garden;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenPortalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class PaleGardenRegistry {

    public static Block PALE_GARDEN_PORTAL;

    public static void register() {
        Identifier portalId = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "pale_garden_portal");
        ResourceKey<Block> portalKey = ResourceKey.create(Registries.BLOCK, portalId);

        PALE_GARDEN_PORTAL = Registry.register(BuiltInRegistries.BLOCK, portalKey,
                new PaleGardenPortalBlock(BlockBehaviour.Properties.of()
                        .setId(portalKey)
                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                        .noCollision()
                        .strength(-1.0f)
                        .sound(SoundType.GLASS)
                        .lightLevel(state -> 11)
                        .noLootTable()
                )
        );
    }
}
