package com.github.hitman20081.dagmod.bone_realm.chest;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

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
                key -> new LockedBoneChestBlock(
                        BlockBehaviour.Properties.of()
                                .setId(key)
                                .strength(50.0f, 1200.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.BONE_BLOCK)
                                .lightLevel(state -> 10),
                        LockedBoneChestBlock.LockedChestType.SKELETON_KING
                ),
                true
        );

        // Register Bone Realm Locked Chest
        BONE_REALM_LOCKED_CHEST = registerChestBlock(
                "bone_realm_locked_chest",
                key -> new LockedBoneChestBlock(
                        BlockBehaviour.Properties.of()
                                .setId(key)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.WOOD)
                                .lightLevel(state -> 5),
                        LockedBoneChestBlock.LockedChestType.BONE_REALM
                ),
                true
        );

        // Register Block Entity Type
        LOCKED_BONE_CHEST_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "locked_bone_chest"),
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

    private static Block registerChestBlock(String id, Function<ResourceKey<Block>, Block> factory, boolean createItem) {
        Identifier identifier = Identifier.fromNamespaceAndPath(DagMod.MOD_ID, id);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, identifier);

        // Register block
        Block registered = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(blockKey));

        // Register BlockItem if requested
        if (createItem) {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
            Registry.register(BuiltInRegistries.ITEM, itemKey,
                    new BlockItem(registered, new Item.Properties().setId(itemKey)));
        }

        return registered;
    }

    private static void addToCreativeTabs() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.accept(SKELETON_KING_CHEST);
            content.accept(BONE_REALM_LOCKED_CHEST);
        });
    }
}