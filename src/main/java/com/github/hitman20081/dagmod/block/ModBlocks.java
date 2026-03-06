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
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.intprovider.UniformIntProvider;

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

    // Dragon Egg Block
    public static final Block DRAGON_EGG_BLOCK = register("dragon_egg",
            new DragonEggBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "dragon_egg")))
                    .strength(3.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 7)
                    .nonOpaque()));

    // ===== GEM ORE BLOCKS =====

    // Standard Ores
    public static final Block MYTHRIL_ORE = register("mythril_ore",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "mythril_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block CITRINE_ORE = register("citrine_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "citrine_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RUBY_ORE = register("ruby_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "ruby_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block SAPPHIRE_ORE = register("sapphire_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "sapphire_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block TANZANITE_ORE = register("tanzanite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "tanzanite_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block ZIRCON_ORE = register("zircon_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "zircon_ore")))
                    .strength(3.0f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    // Deepslate Ore Variants
    public static final Block DEEPSLATE_CITRINE_ORE = register("deepslate_citrine_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "deepslate_citrine_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block DEEPSLATE_RUBY_ORE = register("deepslate_ruby_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "deepslate_ruby_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block DEEPSLATE_SAPPHIRE_ORE = register("deepslate_sapphire_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "deepslate_sapphire_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block DEEPSLATE_TANZANITE_ORE = register("deepslate_tanzanite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "deepslate_tanzanite_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block DEEPSLATE_ZIRCON_ORE = register("deepslate_zircon_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "deepslate_zircon_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block PINK_GARNET_DEEPSLATE_ORE = register("pink_garnet_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(5, 9), AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "pink_garnet_deepslate_ore")))
                    .strength(4.5f, 3.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)));

    // ===== GEM STORAGE BLOCKS =====

    public static final Block MYTHRIL_BLOCK = register("mythril_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "mythril_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block CITRINE_BLOCK = register("citrine_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "citrine_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block RUBY_BLOCK = register("ruby_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "ruby_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block SAPPHIRE_BLOCK = register("sapphire_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "sapphire_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block TANZANITE_BLOCK = register("tanzanite_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "tanzanite_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block ZIRCON_BLOCK = register("zircon_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "zircon_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block PINK_GARNET_BLOCK = register("pink_garnet_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "pink_garnet_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    // ===== RAW GEM BLOCKS =====

    public static final Block RAW_MYTHRIL_BLOCK = register("raw_mythril_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "raw_mythril_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RAW_CITRINE_BLOCK = register("raw_citrine_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "raw_citrine_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RAW_RUBY_BLOCK = register("raw_ruby_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "raw_ruby_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RAW_SAPPHIRE_BLOCK = register("raw_sapphire_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "raw_sapphire_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block RAW_PINK_GARNET_BLOCK = register("raw_pink_garnet_block",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "raw_pink_garnet_block")))
                    .strength(5.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    // ===== GEM CRAFTING STATIONS =====

    public static final Block GEM_CUTTING_STATION = register("gem_cutting_station",
            new GemCuttingStationBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "gem_cutting_station")))
                    .strength(2.5f)
                    .requiresTool()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.WOOD)));

    public static final Block GEM_POLISHING_STATION = register("gem_polishing_station",
            new GemPolishingStationBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "gem_polishing_station")))
                    .strength(2.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.WOOD)));

    public static final Block GEM_INFUSING_STATION = register("gem_infusing_station",
            new GemInfusingStationBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "gem_infusing_station")))
                    .strength(2.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.WOOD)));

    // ===== LOCKED CHESTS =====

    // Hall of Champions Respawn Block
    public static final Block HALL_RESPAWN_BLOCK = register("hall_respawn_block",
            new HallRespawnBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "hall_respawn_block")))
                    .strength(50.0f, 1200.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()
                    .luminance(state -> 12)));

    public static final Block IRON_CHEST = register("iron_chest",
            new IronChestBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "iron_chest")))
                    .strength(5.0f, 1200.0f)  // High blast resistance like obsidian
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

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

        // Dragon Egg Block to functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(DRAGON_EGG_BLOCK));

        // Gem Ores - Add to Natural Blocks group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register((itemGroup) -> {
                    itemGroup.add(MYTHRIL_ORE);
                    itemGroup.add(CITRINE_ORE);
                    itemGroup.add(RUBY_ORE);
                    itemGroup.add(SAPPHIRE_ORE);
                    itemGroup.add(TANZANITE_ORE);
                    itemGroup.add(ZIRCON_ORE);
                    itemGroup.add(DEEPSLATE_CITRINE_ORE);
                    itemGroup.add(DEEPSLATE_RUBY_ORE);
                    itemGroup.add(DEEPSLATE_SAPPHIRE_ORE);
                    itemGroup.add(DEEPSLATE_TANZANITE_ORE);
                    itemGroup.add(DEEPSLATE_ZIRCON_ORE);
                    itemGroup.add(PINK_GARNET_DEEPSLATE_ORE);
                });

        // Gem Storage Blocks - Add to Building Blocks group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.add(MYTHRIL_BLOCK);
                    itemGroup.add(CITRINE_BLOCK);
                    itemGroup.add(RUBY_BLOCK);
                    itemGroup.add(SAPPHIRE_BLOCK);
                    itemGroup.add(TANZANITE_BLOCK);
                    itemGroup.add(ZIRCON_BLOCK);
                    itemGroup.add(PINK_GARNET_BLOCK);
                    itemGroup.add(RAW_MYTHRIL_BLOCK);
                    itemGroup.add(RAW_CITRINE_BLOCK);
                    itemGroup.add(RAW_RUBY_BLOCK);
                    itemGroup.add(RAW_SAPPHIRE_BLOCK);
                    itemGroup.add(RAW_PINK_GARNET_BLOCK);
                });

        // Gem Crafting Stations - Add to Functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> {
                    itemGroup.add(GEM_CUTTING_STATION);
                    itemGroup.add(GEM_POLISHING_STATION);
                    itemGroup.add(GEM_INFUSING_STATION);
                });

        // Hall Respawn Block - Add to Functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> itemGroup.add(HALL_RESPAWN_BLOCK));

        // Locked Chests - Add to Functional group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> {
                    itemGroup.add(IRON_CHEST);
                });

        // Boss Spawn Trigger - Add to Operator Utilities tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR)
                .register((itemGroup) -> itemGroup.add(BOSS_SPAWN_TRIGGER));

        DagMod.LOGGER.info("Registering blocks for " + DagMod.MOD_ID);
    }

    // Boss spawn trigger — indestructible, no collision, hidden in boss room NBTs
    public static final Block BOSS_SPAWN_TRIGGER = register("boss_spawn_trigger",
            new BossSpawnTriggerBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "boss_spawn_trigger")))
                    .strength(-1.0f, 3600000.0f)
                    .noCollision()
                    .nonOpaque()));
}