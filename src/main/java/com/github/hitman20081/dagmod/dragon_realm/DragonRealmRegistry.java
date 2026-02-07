package com.github.hitman20081.dagmod.dragon_realm;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.dragon_realm.portal.DragonKeyItem;
import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmPortalBlock;
import com.github.hitman20081.dagmod.dragon_realm.portal.ObsidianPortalFrameBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

/**
 * Central registry for all Dragon Realm dimension content
 * Handles blocks, items, and creative tab integration
 */
public class DragonRealmRegistry {

    // ===== BLOCKS =====

    /**
     * Obsidian Portal Frame - Used to build Dragon Realm portal frames
     * 3x3 frame structure, activated with Dragon Key
     */
    public static final Block OBSIDIAN_PORTAL_FRAME = registerBlock(
            "obsidian_portal_frame",
            new ObsidianPortalFrameBlock(
                    AbstractBlock.Settings.create()
                            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "obsidian_portal_frame")))
                            .strength(50.0f, 1200.0f)
                            .requiresTool()
                            .luminance(state -> 8)
                            .sounds(BlockSoundGroup.STONE)
            )
    );

    /**
     * Dragon Realm Portal - The actual portal block
     * Auto-generated when frame is activated
     */
    public static final Block DRAGON_REALM_PORTAL = registerBlock(
            "dragon_realm_portal",
            new DragonRealmPortalBlock(
                    AbstractBlock.Settings.create()
                            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "dragon_realm_portal")))
                            .strength(-1.0f)
                            .noCollision()
                            .luminance(state -> 15)
                            .dropsNothing()
            )
    );

    // ===== ITEMS =====

    /**
     * Dragon Key - Activates Dragon Realm portals
     * Single-use item, consumed on portal activation
     */
    public static final Item DRAGON_KEY = registerItem(
            "dragon_key",
            new DragonKeyItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, "dragon_key")))
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
            )
    );

    /**
     * Main registration method - call from DagMod.onInitialize()
     */
    public static void register() {
        DagMod.LOGGER.info("Registering Dragon Realm dimension content...");

        // Blocks and items are registered via static initialization above

        // Add to creative tabs
        addToCreativeTabs();

        DagMod.LOGGER.info("Dragon Realm registration complete!");
    }

    /**
     * Register a block and its corresponding item
     */
    private static Block registerBlock(String name, Block block) {
        // Register the block itself
        Block registeredBlock = Registry.register(
                Registries.BLOCK,
                Identifier.of(DagMod.MOD_ID, name),
                block
        );

        // Register the block's item form
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, name));
        Registry.register(
                Registries.ITEM,
                itemKey,
                new net.minecraft.item.BlockItem(registeredBlock, new Item.Settings().registryKey(itemKey))
        );

        return registeredBlock;
    }

    /**
     * Register an item
     */
    private static Item registerItem(String name, Item item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(DagMod.MOD_ID, name),
                item
        );
    }

    /**
     * Add Dragon Realm items to creative inventory tabs
     */
    private static void addToCreativeTabs() {
        // Add to Building Blocks tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(OBSIDIAN_PORTAL_FRAME);
        });

        // Add to Functional Blocks tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(DRAGON_KEY);
        });
    }
}
