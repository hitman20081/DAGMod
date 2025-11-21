package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {

    public static Block register(String name, Block block) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, name));
        Block registeredBlock = Registry.register(Registries.BLOCK, blockKey, block);

        // Also register as an item so it can be placed
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, name));
        Registry.register(Registries.ITEM, itemKey, new BlockItem(registeredBlock, new Item.Settings().registryKey(itemKey)));

        return registeredBlock;
    }

    public static final Block QUEST_BLOCK = register("quest_block",
            new QuestBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "quest_block")))
                    .strength(2.0f)));

    public static final Block JOB_BOARD_BLOCK = register("job_board",
            new JobBoardBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "job_board")))
                    .strength(2.0f)
                    .sounds(BlockSoundGroup.WOOD)));

    // NEW: Class Selection Altar
    public static final Block CLASS_SELECTION_ALTAR = register("class_selection_altar",
            new ClassSelectionAltarBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "class_selection_altar")))
                    .strength(3.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()
                    .luminance(state -> 5))); // Slight glow

    public static final Block RACE_SELECTION_ALTAR = register("race_selection_altar",
            new RaceSelectionAltarBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "race_selection_altar")))
                    .strength(3.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()
                    .luminance(state -> 10))); // Glows more than class altar

    public static final Block SHIP_HELM = register("ship_helm",
            new ShipHelmBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "ship_helm")))
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.WOOD)
                    .requiresTool()));



    public static void registerModBlocks() {
        DagMod.LOGGER.info("Registering Mod Blocks for " + DagMod.MOD_ID);
    }

    public static void initialize() {
        // Add to creative inventory
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(QUEST_BLOCK));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(JOB_BOARD_BLOCK));

        // NEW: Add class altar to functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(CLASS_SELECTION_ALTAR));

        // NEW: Add race altar to functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(RACE_SELECTION_ALTAR));

        // NEW: Add ship helm to functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(SHIP_HELM));

        DagMod.LOGGER.info("Registering blocks for " + DagMod.MOD_ID);
    }


}