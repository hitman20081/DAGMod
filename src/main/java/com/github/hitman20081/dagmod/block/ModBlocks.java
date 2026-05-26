package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.util.valueproviders.UniformInt;

public class ModBlocks {

    public static Block register(String name, Block block) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name));
        Block registeredBlock = Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        // Also register as an item so it can be placed
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name));
        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(registeredBlock, new Item.Properties()));

        return registeredBlock;
    }

    public static final Block QUEST_BLOCK = register("quest_block",
            new QuestBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)));

    public static final Block JOB_BOARD_BLOCK = register("job_board",
            new JobBoardBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));

    // NEW: Class Selection Altar
    public static final Block CLASS_SELECTION_ALTAR = register("class_selection_altar",
            new ClassSelectionAltarBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 5))); // Slight glow

    public static final Block RACE_SELECTION_ALTAR = register("race_selection_altar",
            new RaceSelectionAltarBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 10))); // Glows more than class altar

    public static final Block SHIP_HELM = register("ship_helm",
            new ShipHelmBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .sound(SoundType.WOOD)
                    .requiresCorrectToolForDrops()));

    // Dragon Egg Block
    public static final Block DRAGON_EGG_BLOCK = register("dragon_egg",
            new DragonEggBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)
                    .noOcclusion()));

    // ===== GEM ORE BLOCKS =====

    // Standard Ores
    public static final Block MYTHRIL_ORE = register("mythril_ore",
            new Block(BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block CITRINE_ORE = register("citrine_ore",
            new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block RUBY_ORE = register("ruby_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block SAPPHIRE_ORE = register("sapphire_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block TANZANITE_ORE = register("tanzanite_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block ZIRCON_ORE = register("zircon_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    // Deepslate Ore Variants
    public static final Block DEEPSLATE_CITRINE_ORE = register("deepslate_citrine_ore",
            new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final Block DEEPSLATE_RUBY_ORE = register("deepslate_ruby_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final Block DEEPSLATE_SAPPHIRE_ORE = register("deepslate_sapphire_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final Block DEEPSLATE_TANZANITE_ORE = register("deepslate_tanzanite_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final Block DEEPSLATE_ZIRCON_ORE = register("deepslate_zircon_ore",
            new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final Block PINK_GARNET_DEEPSLATE_ORE = register("pink_garnet_deepslate_ore",
            new DropExperienceBlock(UniformInt.of(5, 9), BlockBehaviour.Properties.of()
                    .strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    // ===== GEM STORAGE BLOCKS =====

    public static final Block MYTHRIL_BLOCK = register("mythril_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block CITRINE_BLOCK = register("citrine_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block RUBY_BLOCK = register("ruby_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block SAPPHIRE_BLOCK = register("sapphire_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block TANZANITE_BLOCK = register("tanzanite_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block ZIRCON_BLOCK = register("zircon_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final Block PINK_GARNET_BLOCK = register("pink_garnet_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    // ===== RAW GEM BLOCKS =====

    public static final Block RAW_MYTHRIL_BLOCK = register("raw_mythril_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block RAW_CITRINE_BLOCK = register("raw_citrine_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block RAW_RUBY_BLOCK = register("raw_ruby_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block RAW_SAPPHIRE_BLOCK = register("raw_sapphire_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Block RAW_PINK_GARNET_BLOCK = register("raw_pink_garnet_block",
            new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    // ===== GEM CRAFTING STATIONS =====

    public static final Block GEM_CUTTING_STATION = register("gem_cutting_station",
            new GemCuttingStationBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .sound(SoundType.WOOD)));

    public static final Block GEM_POLISHING_STATION = register("gem_polishing_station",
            new GemPolishingStationBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)));

    public static final Block GEM_INFUSING_STATION = register("gem_infusing_station",
            new GemInfusingStationBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)));

    // ===== LOCKED CHESTS =====

    // Hall of Champions Respawn Block
    public static final Block HALL_RESPAWN_BLOCK = register("hall_respawn_block",
            new HallRespawnBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 12)));

    public static final Block IRON_CHEST = register("iron_chest",
            new IronChestBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 1200.0f)  // High blast resistance like obsidian
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static void registerModBlocks() {
        DagMod.LOGGER.info("Registering Mod Blocks for " + DagMod.MOD_ID);
    }

    public static void initialize() {
        // Add to creative inventory
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(QUEST_BLOCK));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(JOB_BOARD_BLOCK));

        // NEW: Add class altar to functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(CLASS_SELECTION_ALTAR));

        // NEW: Add race altar to functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(RACE_SELECTION_ALTAR));

        // NEW: Add ship helm to functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(SHIP_HELM));

        // Dragon Egg Block to functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(DRAGON_EGG_BLOCK));

        // Gem Ores - Add to Natural Blocks group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.accept(MYTHRIL_ORE);
                    itemGroup.accept(CITRINE_ORE);
                    itemGroup.accept(RUBY_ORE);
                    itemGroup.accept(SAPPHIRE_ORE);
                    itemGroup.accept(TANZANITE_ORE);
                    itemGroup.accept(ZIRCON_ORE);
                    itemGroup.accept(DEEPSLATE_CITRINE_ORE);
                    itemGroup.accept(DEEPSLATE_RUBY_ORE);
                    itemGroup.accept(DEEPSLATE_SAPPHIRE_ORE);
                    itemGroup.accept(DEEPSLATE_TANZANITE_ORE);
                    itemGroup.accept(DEEPSLATE_ZIRCON_ORE);
                    itemGroup.accept(PINK_GARNET_DEEPSLATE_ORE);
                });

        // Gem Storage Blocks - Add to Building Blocks group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.accept(MYTHRIL_BLOCK);
                    itemGroup.accept(CITRINE_BLOCK);
                    itemGroup.accept(RUBY_BLOCK);
                    itemGroup.accept(SAPPHIRE_BLOCK);
                    itemGroup.accept(TANZANITE_BLOCK);
                    itemGroup.accept(ZIRCON_BLOCK);
                    itemGroup.accept(PINK_GARNET_BLOCK);
                    itemGroup.accept(RAW_MYTHRIL_BLOCK);
                    itemGroup.accept(RAW_CITRINE_BLOCK);
                    itemGroup.accept(RAW_RUBY_BLOCK);
                    itemGroup.accept(RAW_SAPPHIRE_BLOCK);
                    itemGroup.accept(RAW_PINK_GARNET_BLOCK);
                });

        // Gem Crafting Stations - Add to Functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.accept(GEM_CUTTING_STATION);
                    itemGroup.accept(GEM_POLISHING_STATION);
                    itemGroup.accept(GEM_INFUSING_STATION);
                });

        // Hall Respawn Block - Add to Functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> itemGroup.accept(HALL_RESPAWN_BLOCK));

        // Locked Chests - Add to Functional group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.accept(IRON_CHEST);
                });

        // Boss Spawn Triggers - Add to Operator Utilities tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.OP_BLOCKS)
                .register((itemGroup) -> {
                    itemGroup.accept(BOSS_SPAWN_TRIGGER);
                    itemGroup.accept(SKELETON_KING_SPAWN_TRIGGER);
                });

        DagMod.LOGGER.info("Registering blocks for " + DagMod.MOD_ID);
    }

    // Boss spawn trigger — indestructible, no collision, hidden in boss room NBTs
    public static final Block BOSS_SPAWN_TRIGGER = register("boss_spawn_trigger",
            new BossSpawnTriggerBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0f, 3600000.0f)
                    .noCollision()
                    .noOcclusion()));

    // Skeleton King spawn trigger — indestructible, no collision, hidden in boss room NBTs
    public static final Block SKELETON_KING_SPAWN_TRIGGER = register("skeleton_king_spawn_trigger",
            new BossSpawnTriggerBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0f, 3600000.0f)
                    .noCollision()
                    .noOcclusion()));
}