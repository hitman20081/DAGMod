package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.bone_realm.portal.AncientBoneBlock;
import com.github.hitman20081.dagmod.bone_realm.portal.BoneRealmPortalBlock;
import com.github.hitman20081.dagmod.bone_realm.portal.NecroticKeyItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
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
 * Central registry for all Bone Realm content
 * Keeps everything organized in one place
 */
public class BoneRealmRegistry {

    // ============ BLOCKS ============

    public static Block ANCIENT_BONE_BLOCK;
    public static Block BONE_REALM_PORTAL;

    // ============ ITEMS ============

    public static Item NECROTIC_KEY;
    public static Item BONE_REALM_CHEST_KEY;  // NEW: Key for special Bone Realm chests
    public static Item SKELETON_KING_KEY;     // NEW: Drops from Skeleton King boss

    // ============ REGISTRATION METHODS ============

    /**
     * Register all Bone Realm content
     * Call this from your main mod initializer
     */
    public static void register() {
        // Register blocks - must use registryKey() for 1.21.10+
        ANCIENT_BONE_BLOCK = registerBlock(
                "ancient_bone_block",
                new AncientBoneBlock(BlockBehaviour.Properties.of()
                        .strength(50.0f, 1200.0f)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.BONE_BLOCK)
                        .lightLevel(state -> 5)
                ),
                true
        );

        BONE_REALM_PORTAL = registerBlock(
                "bone_realm_portal",
                new BoneRealmPortalBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_BLACK)
                        .noCollision()
                        .strength(-1.0f)
                        .sound(SoundType.GLASS)
                        .lightLevel(state -> 11)
                        .noLootTable()
                ),
                false // Don't create BlockItem for portal
        );

        // Register items
        NECROTIC_KEY = registerItem(
                "necrotic_key",
                new NecroticKeyItem(new Item.Properties()
                        
                        .stacksTo(1)
                        .rarity(Rarity.EPIC)
                )
        );

        // NEW: Bone Realm Chest Key - for locked chests in the dimension
        BONE_REALM_CHEST_KEY = registerItem(
                "bone_realm_chest_key",
                new Item(new Item.Properties()
                        
                        .stacksTo(1)
                        .rarity(Rarity.RARE)
                )
        );

        // NEW: Skeleton King Key - drops from boss, opens special chest
        SKELETON_KING_KEY = registerItem(
                "skeleton_king_key",
                new Item(new Item.Properties()
                        
                        .stacksTo(1)
                        .rarity(Rarity.EPIC)
                )
        );

        // Add items to creative tabs
        addToCreativeTabs();

    }

    /**
     * Register a block with optional BlockItem
     */
    private static Block registerBlock(String id, Block block, boolean createItem) {
        Identifier identifier = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, id);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, identifier);

        // Register block
        Block registered = Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        // Register BlockItem if requested
        if (createItem) {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
            Registry.register(BuiltInRegistries.ITEM, itemKey,
                    new BlockItem(registered, new Item.Properties().setId(itemKey)));
        }

        return registered;
    }

    /**
     * Register an item
     */
    private static Item registerItem(String id, Item item) {
        Identifier identifier = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, id);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    /**
     * Add items to creative tabs
     */
    private static void addToCreativeTabs() {
        // Add Ancient Bone Block to Building Blocks tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            content.accept(ANCIENT_BONE_BLOCK);
        });

        // Add keys to Tools tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(NECROTIC_KEY);
            content.accept(BONE_REALM_CHEST_KEY);
            content.accept(SKELETON_KING_KEY);
        });
    }
}