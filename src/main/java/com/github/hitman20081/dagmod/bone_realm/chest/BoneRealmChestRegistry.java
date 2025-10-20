package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Registry for Locked Bone Chests
 */
public class BoneRealmChestRegistry {

    // Block Entity Type
    public static BlockEntityType<LockedBoneChestBlockEntity> LOCKED_BONE_CHEST_ENTITY;

    // Chest Blocks
    public static Block SKELETON_KING_CHEST;
    public static Block BONE_REALM_LOCKED_CHEST;

    public static void register() {
        // Register Skeleton King Chest
        SKELETON_KING_CHEST = registerChestBlock(
                "skeleton_king_chest",
                new LockedBoneChestBlock(
                        AbstractBlock.Settings.create()
                                .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                        Identifier.of(DagMod.MOD_ID, "skeleton_king_chest")))
                                .strength(50.0f, 1200.0f)
                                .requiresTool()
                                .sounds(BlockSoundGroup.BONE)
                                .luminance(state -> 10),
                        LockedBoneChestBlock.LockedChestType.SKELETON_KING
                ),
                true
        );

        // Register Bone Realm Locked Chest
        BONE_REALM_LOCKED_CHEST = registerChestBlock(
                "bone_realm_locked_chest",
                new LockedBoneChestBlock(
                        AbstractBlock.Settings.create()
                                .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                        Identifier.of(DagMod.MOD_ID, "bone_realm_locked_chest")))
                                .strength(5.0f, 6.0f)
                                .requiresTool()
                                .sounds(BlockSoundGroup.WOOD)
                                .luminance(state -> 5),
                        LockedBoneChestBlock.LockedChestType.BONE_REALM
                ),
                true
        );

        // Register Block Entity Type
        LOCKED_BONE_CHEST_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(DagMod.MOD_ID, "locked_bone_chest"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> {
                            Block block = state.getBlock();
                            if (block instanceof LockedBoneChestBlock chestBlock) {
                                return new LockedBoneChestBlockEntity(pos, state, chestBlock.getChestType());
                            }
                            return new LockedBoneChestBlockEntity(pos, state,
                                    LockedBoneChestBlock.LockedChestType.BONE_REALM);
                        },
                        SKELETON_KING_CHEST,
                        BONE_REALM_LOCKED_CHEST
                ).build()
        );

        // Add to creative tabs
        addToCreativeTabs();
    }

    private static Block registerChestBlock(String id, Block block, boolean createItem) {
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

    private static void addToCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(SKELETON_KING_CHEST);
            content.add(BONE_REALM_LOCKED_CHEST);
        });
    }
}