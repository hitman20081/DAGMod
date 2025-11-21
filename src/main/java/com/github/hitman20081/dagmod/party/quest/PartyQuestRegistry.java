package com.github.hitman20081.dagmod.party.quest;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

/**
 * Registry of all available party quests
 */
public class PartyQuestRegistry {
    private static final Map<String, PartyQuestTemplate> QUESTS = new HashMap<>();

    /**
     * Register all party quests
     */
    public static void registerQuests() {
        // Clear existing
        QUESTS.clear();

        // Register quests
        registerBoneDungeonQuests();
        registerCombatQuests();
        registerGatheringQuests();
        registerBossQuests();
        registerChallengeQuests();
    }

    /**
     * Bone Dungeon themed quests
     */
    private static void registerBoneDungeonQuests() {
        // Dungeon Clear - Easy
        register(new PartyQuestTemplate.Builder("dungeon_clear_easy", "Bone Dungeon: Easy Clear")
                .description("Clear the Bone Dungeon together!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.EASY)
                .objective(new PartyQuestObjective(
                        "kill_skeletons",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "minecraft:skeleton",
                        20,
                        "Kill 20 Skeletons"
                ))
                .xpReward(1000)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND), 3))
                .reward(new PartyQuestReward(new ItemStack(Items.GOLDEN_APPLE), 1))
                .build()
        );

        // Dungeon Clear - Normal
        register(new PartyQuestTemplate.Builder("dungeon_clear_normal", "Bone Dungeon: Full Clear")
                .description("Defeat all enemies in the Bone Dungeon!")
                .minPartySize(3)
                .difficulty(PartyQuestDifficulty.NORMAL)
                .objective(new PartyQuestObjective(
                        "kill_skeletons",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "minecraft:skeleton",
                        30,
                        "Kill 30 Skeletons"
                ))
                .objective(new PartyQuestObjective(
                        "kill_skeleton_king",
                        PartyQuestObjectiveType.KILL_BOSS,
                        "dagmod:skeleton_king",
                        1,
                        "Defeat the Skeleton King"
                ))
                .xpReward(2500)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND), 5))
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_SCRAP), 1))
                .build()
        );
    }

    /**
     * Combat quests
     */
    private static void registerCombatQuests() {
        // Mob Slayer - Easy
        register(new PartyQuestTemplate.Builder("mob_slayer_easy", "Mob Slayer")
                .description("Work together to defeat enemies!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.EASY)
                .objective(new PartyQuestObjective(
                        "kill_zombies",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "minecraft:zombie",
                        30,
                        "Kill 30 Zombies"
                ))
                .xpReward(800)
                .reward(new PartyQuestReward(new ItemStack(Items.IRON_INGOT), 10))
                .build()
        );

        // Undead Hunter - Normal
        register(new PartyQuestTemplate.Builder("undead_hunter", "Undead Hunter")
                .description("Hunt down the undead menace!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.NORMAL)
                .objective(new PartyQuestObjective(
                        "kill_zombies",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "minecraft:zombie",
                        25,
                        "Kill 25 Zombies"
                ))
                .objective(new PartyQuestObjective(
                        "kill_skeletons",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "minecraft:skeleton",
                        25,
                        "Kill 25 Skeletons"
                ))
                .xpReward(1500)
                .reward(new PartyQuestReward(new ItemStack(Items.GOLD_INGOT), 15))
                .reward(new PartyQuestReward(new ItemStack(Items.ENCHANTED_BOOK), 1))
                .build()
        );

        // Monster Extermination - Hard
        register(new PartyQuestTemplate.Builder("monster_extermination", "Monster Extermination")
                .description("Eliminate all hostile creatures!")
                .minPartySize(3)
                .difficulty(PartyQuestDifficulty.HARD)
                .objective(new PartyQuestObjective(
                        "kill_any",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "hostile",
                        100,
                        "Kill 100 Hostile Mobs"
                ))
                .timeLimit(15) // 15 minutes
                .xpReward(3000)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND), 8))
                .reward(new PartyQuestReward(new ItemStack(Items.GOLDEN_APPLE), 3))
                .build()
        );
    }

    /**
     * Gathering quests
     */
    private static void registerGatheringQuests() {
        // Mining Expedition
        register(new PartyQuestTemplate.Builder("mining_expedition", "Mining Expedition")
                .description("Mine valuable ores together!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.EASY)
                .objective(new PartyQuestObjective(
                        "mine_iron",
                        PartyQuestObjectiveType.MINE_BLOCK,
                        "minecraft:iron_ore",
                        32,
                        "Mine 32 Iron Ore"
                ))
                .objective(new PartyQuestObjective(
                        "mine_gold",
                        PartyQuestObjectiveType.MINE_BLOCK,
                        "minecraft:gold_ore",
                        16,
                        "Mine 16 Gold Ore"
                ))
                .xpReward(1200)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND_PICKAXE), 1))
                .build()
        );

        // Resource Gathering
        register(new PartyQuestTemplate.Builder("resource_gathering", "Resource Gathering")
                .description("Collect valuable resources!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.NORMAL)
                .objective(new PartyQuestObjective(
                        "collect_diamonds",
                        PartyQuestObjectiveType.COLLECT_ITEM,
                        "minecraft:diamond",
                        10,
                        "Collect 10 Diamonds"
                ))
                .objective(new PartyQuestObjective(
                        "collect_emeralds",
                        PartyQuestObjectiveType.COLLECT_ITEM,
                        "minecraft:emerald",
                        5,
                        "Collect 5 Emeralds"
                ))
                .xpReward(2000)
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_INGOT), 1))
                .build()
        );
    }

    /**
     * Boss quests
     */
    private static void registerBossQuests() {
        // Skeleton King Challenge
        register(new PartyQuestTemplate.Builder("skeleton_king_challenge", "Face the Skeleton King")
                .description("Defeat the powerful Skeleton King!")
                .minPartySize(3)
                .maxPartySize(5)
                .difficulty(PartyQuestDifficulty.HARD)
                .objective(new PartyQuestObjective(
                        "defeat_king",
                        PartyQuestObjectiveType.KILL_BOSS,
                        "dagmod:skeleton_king",
                        1,
                        "Defeat the Skeleton King"
                ))
                .xpReward(5000)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND), 10))
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_INGOT), 2))
                .reward(new PartyQuestReward(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 2))
                .build()
        );

        // Wither Challenge
        register(new PartyQuestTemplate.Builder("wither_challenge", "Wither Slayer")
                .description("Take down the fearsome Wither!")
                .minPartySize(4)
                .difficulty(PartyQuestDifficulty.EXPERT)
                .objective(new PartyQuestObjective(
                        "defeat_wither",
                        PartyQuestObjectiveType.KILL_BOSS,
                        "minecraft:wither",
                        1,
                        "Defeat the Wither"
                ))
                .xpReward(10000)
                .reward(new PartyQuestReward(new ItemStack(Items.NETHER_STAR), 1))
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_INGOT), 5))
                .build()
        );

        // Dragon Slayer
        register(new PartyQuestTemplate.Builder("dragon_slayer", "Dragon Slayer")
                .description("Defeat the Ender Dragon together!")
                .minPartySize(3)
                .difficulty(PartyQuestDifficulty.LEGENDARY)
                .objective(new PartyQuestObjective(
                        "defeat_dragon",
                        PartyQuestObjectiveType.KILL_BOSS,
                        "minecraft:ender_dragon",
                        1,
                        "Defeat the Ender Dragon"
                ))
                .xpReward(15000)
                .reward(new PartyQuestReward(new ItemStack(Items.ELYTRA), 1))
                .reward(new PartyQuestReward(new ItemStack(Items.DRAGON_HEAD), 1))
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_INGOT), 10))
                .build()
        );
    }

    /**
     * Challenge/Timed quests
     */
    private static void registerChallengeQuests() {
        // Speed Run
        register(new PartyQuestTemplate.Builder("speed_run", "Speed Run Challenge")
                .description("Defeat enemies quickly!")
                .minPartySize(2)
                .difficulty(PartyQuestDifficulty.HARD)
                .objective(new PartyQuestObjective(
                        "kill_fast",
                        PartyQuestObjectiveType.KILL_ENTITY,
                        "hostile",
                        50,
                        "Kill 50 Mobs"
                ))
                .timeLimit(10) // 10 minutes
                .xpReward(4000)
                .reward(new PartyQuestReward(new ItemStack(Items.DIAMOND), 8))
                .reward(new PartyQuestReward(new ItemStack(Items.GOLDEN_APPLE), 5))
                .build()
        );

        // Survival Challenge
        register(new PartyQuestTemplate.Builder("survival_challenge", "Survival Challenge")
                .description("Survive waves of enemies!")
                .minPartySize(3)
                .difficulty(PartyQuestDifficulty.EXPERT)
                .objective(new PartyQuestObjective(
                        "survive_waves",
                        PartyQuestObjectiveType.SURVIVE_WAVES,
                        "waves",
                        10,
                        "Survive 10 Waves"
                ))
                .timeLimit(20)
                .xpReward(8000)
                .reward(new PartyQuestReward(new ItemStack(Items.NETHERITE_INGOT), 3))
                .reward(new PartyQuestReward(new ItemStack(Items.TOTEM_OF_UNDYING), 1))
                .build()
        );
    }

    /**
     * Register a quest template
     */
    public static void register(PartyQuestTemplate template) {
        QUESTS.put(template.getId(), template);
    }

    /**
     * Get a quest template by ID
     */
    public static PartyQuestTemplate getQuest(String id) {
        return QUESTS.get(id);
    }

    /**
     * Get all registered quests
     */
    public static Collection<PartyQuestTemplate> getAllQuests() {
        return QUESTS.values();
    }

    /**
     * Get quests by difficulty
     */
    public static List<PartyQuestTemplate> getQuestsByDifficulty(PartyQuestDifficulty difficulty) {
        List<PartyQuestTemplate> result = new ArrayList<>();
        for (PartyQuestTemplate template : QUESTS.values()) {
            if (template.getDifficulty() == difficulty) {
                result.add(template);
            }
        }
        return result;
    }
}