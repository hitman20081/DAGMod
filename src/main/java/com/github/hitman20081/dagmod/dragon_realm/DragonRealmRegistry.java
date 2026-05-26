package com.github.hitman20081.dagmod.dragon_realm;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.dragon_realm.portal.DragonKeyItem;
import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmPortalBlock;
import com.github.hitman20081.dagmod.dragon_realm.portal.ObsidianPortalFrameBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

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
                    BlockBehaviour.Properties.of()
                            .strength(50.0f, 1200.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 8)
                            .sound(SoundType.STONE)
            )
    );

    /**
     * Dragon Realm Portal - The actual portal block
     * Auto-generated when frame is activated
     */
    public static final Block DRAGON_REALM_PORTAL = registerBlock(
            "dragon_realm_portal",
            new DragonRealmPortalBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0f)
                            .noCollision()
                            .lightLevel(state -> 15)
                            .noLootTable()
            )
    );

    // ===== ITEMS =====

    /**
     * Dragon Key - Activates Dragon Realm portals
     * Single-use item, consumed on portal activation
     */
    public static final Item DRAGON_KEY = registerItem(
            "dragon_key",
            new DragonKeyItem(new Item.Properties()
                    
                    .stacksTo(1)
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
                BuiltInRegistries.BLOCK,
                Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name),
                block
        );

        // Register the block's item form
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name));
        Registry.register(
                BuiltInRegistries.ITEM,
                itemKey,
                new net.minecraft.world.item.BlockItem(registeredBlock, new Item.Properties().setId(itemKey))
        );

        return registeredBlock;
    }

    /**
     * Register an item
     */
    private static Item registerItem(String name, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name),
                item
        );
    }

    /**
     * Add Dragon Realm items to creative inventory tabs
     */
    private static void addToCreativeTabs() {
        // Add to Building Blocks tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(OBSIDIAN_PORTAL_FRAME.asItem());
        });

        // Add to Functional Blocks tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(DRAGON_KEY);
        });
    }
}
