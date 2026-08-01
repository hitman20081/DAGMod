package com.github.hitman20081.dagmod.pale_garden;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenKeyItem;
import com.github.hitman20081.dagmod.pale_garden.portal.PaleGardenPortalBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

public class PaleGardenRegistry {

    public static Block PALE_GARDEN_PORTAL;
    public static Item PALE_GARDEN_KEY;

    public static void register() {
        // Portal block (no BlockItem — not obtainable in survival)
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

        // Key item
        Identifier keyId = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "pale_garden_key");
        ResourceKey<Item> keyItemKey = ResourceKey.create(Registries.ITEM, keyId);
        PALE_GARDEN_KEY = Registry.register(BuiltInRegistries.ITEM, keyItemKey,
                new PaleGardenKeyItem(new Item.Properties()
                        .setId(keyItemKey)
                        .stacksTo(1)
                        .rarity(Rarity.EPIC)
                )
        );

        addToCreativeTabs();
    }

    private static void addToCreativeTabs() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content ->
                content.accept(PALE_GARDEN_KEY));
    }
}
