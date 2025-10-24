package com.github.hitman20081.dagmod.bone_realm;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.bone_realm.portal.AncientBoneBlock;
import com.github.hitman20081.dagmod.bone_realm.portal.BoneRealmPortalBlock;
import com.github.hitman20081.dagmod.bone_realm.portal.NecroticKeyItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
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
                new AncientBoneBlock(AbstractBlock.Settings.create()
                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "ancient_bone_block")))
                        .strength(50.0f, 1200.0f)
                        .requiresTool()
                        .sounds(BlockSoundGroup.BONE)
                        .luminance(state -> 5)
                ),
                true
        );

        BONE_REALM_PORTAL = registerBlock(
                "bone_realm_portal",
                new BoneRealmPortalBlock(AbstractBlock.Settings.create()
                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "bone_realm_portal")))
                        .mapColor(MapColor.BLACK)
                        .noCollision()
                        .strength(-1.0f)
                        .sounds(BlockSoundGroup.GLASS)
                        .luminance(state -> 11)
                        .dropsNothing()
                ),
                false // Don't create BlockItem for portal
        );

        // Register items
        NECROTIC_KEY = registerItem(
                "necrotic_key",
                new NecroticKeyItem(new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, "necrotic_key")))
                        .maxCount(1)
                        .rarity(Rarity.EPIC)
                )
        );

        // NEW: Bone Realm Chest Key - for locked chests in the dimension
        BONE_REALM_CHEST_KEY = registerItem(
                "bone_realm_chest_key",
                new Item(new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, "bone_realm_chest_key")))
                        .maxCount(1)
                        .rarity(Rarity.RARE)
                )
        );

        // NEW: Skeleton King Key - drops from boss, opens special chest
        SKELETON_KING_KEY = registerItem(
                "skeleton_king_key",
                new Item(new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, "skeleton_king_key")))
                        .maxCount(1)
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
        Identifier identifier = Identifier.of(DagMod.MOD_ID, id);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        // Register block
        Block registered = Registry.register(Registries.BLOCK, blockKey, block);

        // Register BlockItem if requested
        if (createItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, identifier);
            Registry.register(Registries.ITEM, itemKey,
                    new BlockItem(registered, new Item.Settings().registryKey(itemKey)));
        }

        return registered;
    }

    /**
     * Register an item
     */
    private static Item registerItem(String id, Item item) {
        Identifier identifier = Identifier.of(DagMod.MOD_ID, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, identifier);
        return Registry.register(Registries.ITEM, itemKey, item);
    }

    /**
     * Add items to creative tabs
     */
    private static void addToCreativeTabs() {
        // Add Ancient Bone Block to Building Blocks tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.add(ANCIENT_BONE_BLOCK);
        });

        // Add keys to Tools tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(NECROTIC_KEY);
            content.add(BONE_REALM_CHEST_KEY);
            content.add(SKELETON_KING_KEY);
        });
    }
}