package com.github.hitman20081.dagmod.quest.registry;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestChain;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import com.github.hitman20081.dagmod.quest.rewards.ItemReward;
import com.github.hitman20081.dagmod.quest.rewards.XpReward;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

public class QuestRegistry {

    public static void registerQuests() {
        QuestManager manager = QuestManager.getInstance();

        // Register individual quests first
        registerIndividualQuests(manager);

        // Then register quest chains
        registerQuestChains(manager);

        System.out.println("Registered " + manager.getAllQuests().size() + " quests and " +
                manager.getAllChains().size() + " quest chains for DAGmod");
    }

    private static void registerIndividualQuests(QuestManager manager) {
            // ========== CLASS-SPECIFIC QUESTS ==========

        // ========== DWARF RACE QUESTS ==========
        manager.registerQuest(createDwarfApprenticeQuest());
        manager.registerQuest(createDeepDelvingQuest());
        manager.registerQuest(createAncientAlloysQuest());
        manager.registerQuest(createMountainKingsTributeQuest());
        manager.registerQuest(createRubyInTheRoughQuest());
        manager.registerQuest(createNetherForgeQuest());
        manager.registerQuest(createMasterBlacksmithQuest());
        manager.registerQuest(createRuneOfPowerQuest());
        manager.registerQuest(createHeartOfMountainQuest());
        manager.registerQuest(createForgemasterLegacyQuest());

        // ========== ELF RACE QUESTS ==========
        manager.registerQuest(createSeedlingQuest());
        manager.registerQuest(createRootsRunDeepQuest());
        manager.registerQuest(createBowmastersTrialQuest());
        manager.registerQuest(createCorruptedGroveQuest());
        manager.registerQuest(createWhispersOfLeavesQuest());
        manager.registerQuest(createSilverBowQuest());
        manager.registerQuest(createWildernessWardenQuest());
        manager.registerQuest(createWorldTreeSaplingQuest());
        manager.registerQuest(createMoonlitRitualQuest());
        manager.registerQuest(createForestLordQuest());

        // ========== HUMAN RACE QUESTS ==========
        manager.registerQuest(createWandererQuest());
        manager.registerQuest(createTasteOfEverythingQuest());
        manager.registerQuest(createWorldTravelerQuest());
        manager.registerQuest(createMasterTraderQuest());
        manager.registerQuest(createRenaissanceScholarQuest());
        manager.registerQuest(createBridgeBuilderQuest());
        manager.registerQuest(createHerosJourneyQuest());
        manager.registerQuest(createDiplomaticMissionQuest());
        manager.registerQuest(createMasterOfAllTradesQuest());
        manager.registerQuest(createLegendQuest());

        // ========== ORC RACE QUESTS ==========
        manager.registerQuest(createGruntQuest());
        manager.registerQuest(createProveYourStrengthQuest());
        manager.registerQuest(createHuntTheMightyQuest());
        manager.registerQuest(createRaidLeaderQuest());
        manager.registerQuest(createBloodAndIronQuest());
        manager.registerQuest(createBattleScarsQuest());
        manager.registerQuest(createChampionOfTheClanQuest());
        manager.registerQuest(createConquerTheNetherQuest());
        manager.registerQuest(createChallengeTheWitherQuest());
        manager.registerQuest(createWarlordQuest());

                // ========== CLASS-SPECIFIC QUESTS ==========

        // Warrior Quests
        manager.registerQuest(createShieldBearerQuest());
        manager.registerQuest(createWarriorTrainingQuest());
        manager.registerQuest(createBattleMasterQuest());

        // Mage Quests
        manager.registerQuest(createArcanistApprenticeQuest());
        manager.registerQuest(createPotionMasterQuest());
        manager.registerQuest(createEnchantmentSageQuest());

        // Rogue Quests
        manager.registerQuest(createShadowStrikerQuest());
        manager.registerQuest(createTreasureHunterQuest());
        manager.registerQuest(createMasterAssassinQuest());

        // ========== CHAIN QUESTS ==========
        // Adventurer's Path Chain
        manager.registerQuest(createTheBeginningQuest());
        manager.registerQuest(createEquipYourselfQuest());
        manager.registerQuest(createReadyForAdventureQuest());

        // Village Development Chain
        manager.registerQuest(createVillageFounderQuest());
        manager.registerQuest(createVillageBuilderQuest());
        manager.registerQuest(createLivestockKeeperQuest());
        manager.registerQuest(createVillageDefenderQuest());
        manager.registerQuest(createVillageMasterQuest());

        // Master Craftsman Chain
        manager.registerQuest(createToolmakerQuest());
        manager.registerQuest(createIronMinerQuest());
        manager.registerQuest(createMasterCrafterQuest());

        // Combat Specialist Chain
        manager.registerQuest(createMonsterHunterQuest());
        manager.registerQuest(createCaveCleanerQuest());
        manager.registerQuest(createNightWatchQuest());
        manager.registerQuest(createBountyHunterQuest());

        // Additional quests
        manager.registerQuest(createDeepMinerQuest());
    }

    private static void registerQuestChains(QuestManager manager) {
        // CHAIN 1: Adventurer's Path (Novice → Apprentice)
        QuestChain adventurerPath = new QuestChain(
                "adventurer_path",
                "The Adventurer's Path",
                "Your first steps into the world of adventure. Master the basics of survival.",
                QuestData.QuestBookTier.NOVICE,
                QuestData.QuestBookTier.APPRENTICE
        )
                .addQuest("the_beginning")
                .addQuest("equip_yourself")
                .addQuest("ready_for_adventure")
                .addChainReward(new ItemReward(Items.IRON_SWORD, 1))
                .addChainReward(new ItemReward(Items.EMERALD, 5))
                .addChainReward(XpReward.apprentice());

        // CHAIN 2: Village Development (Apprentice → Expert)
        QuestChain villageDevelopment = new QuestChain(
                "village_development",
                "Village Development",
                "Help establish and protect a thriving settlement.",
                QuestData.QuestBookTier.APPRENTICE,
                QuestData.QuestBookTier.EXPERT
        )
                .addQuest("village_founder")
                .addQuest("village_builder")
                .addQuest("livestock_keeper")
                .addQuest("village_defender")
                .addQuest("village_master")
                .addChainReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addChainReward(new ItemReward(Items.EMERALD, 10))
                .addChainReward(XpReward.expert());

        // CHAIN 3: Master Craftsman (Expert → Master)
        QuestChain masterCraftsman = new QuestChain(
                "master_craftsman",
                "Master Craftsman",
                "Achieve mastery over tools, weapons, and the art of creation.",
                QuestData.QuestBookTier.EXPERT,
                QuestData.QuestBookTier.MASTER
        )
                .addQuest("toolmaker")
                .addQuest("iron_miner")
                .addQuest("master_crafter")
                .addChainReward(new ItemReward(Items.NETHERITE_PICKAXE, 1))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addChainReward(XpReward.master());

        // CHAIN 4: Combat Specialist (Parallel progression)
        QuestChain combatSpecialist = new QuestChain(
                "combat_specialist",
                "Combat Specialist",
                "Prove your prowess against the dangers that threaten the realm.",
                QuestData.QuestBookTier.APPRENTICE,
                null // No tier reward, just completion rewards
        )
                .addQuest("monster_hunter")
                .addQuest("cave_cleaner")
                .addQuest("night_watch")
                .addQuest("bounty_hunter")
                .addChainReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addChainReward(new ItemReward(Items.GOLDEN_APPLE, 5))
                .addChainReward(XpReward.expert());

        // Register all chains
        manager.registerQuestChain(adventurerPath);
        manager.registerQuestChain(villageDevelopment);
        manager.registerQuestChain(masterCraftsman);
        manager.registerQuestChain(combatSpecialist);
        registerDwarfQuestChain(manager);
        registerElfQuestChain(manager);
        registerHumanQuestChain(manager);
        registerOrcQuestChain(manager);
    }

    // ========== DWARF RACE QUESTS - "The Forgemaster's Legacy" ==========
// Theme: Mining, crafting, underground exploration
// Unlocks at: Levels 1, 10, 20, 30, 40

    private static Quest createDwarfApprenticeQuest() {
        return new Quest("dwarf_apprentice")
                .setName("Apprentice of the Forge")
                .setDescription("Every dwarf must prove their worth at the forge. Gather materials for your first creation.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Dwarf") // Requires Dwarf race
                .addObjective(new CollectObjective(Items.IRON_INGOT, 16))
                .addObjective(new CollectObjective(Items.COAL, 32))
                .addObjective(new CollectObjective(Items.STONE, 64))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createDeepDelvingQuest() {
        return new Quest("deep_delving")
                .setName("Deep Delving")
                .setDescription("The deepest treasures lie far below. Mine at the lowest depths to find rare ores.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.DEEPSLATE, 32))
                .addObjective(new CollectObjective(Items.DEEPSLATE_IRON_ORE, 8))
                .addObjective(new CollectObjective(Items.DEEPSLATE_GOLD_ORE, 4))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new ItemReward(Items.TORCH, 64))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("dwarf_apprentice");
    }

    private static Quest createAncientAlloysQuest() {
        return new Quest("ancient_alloys")
                .setName("Ancient Alloys")
                .setDescription("Learn the secrets of dwarven metallurgy. Craft with the strongest materials.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.IRON_BLOCK, 4))
                .addObjective(new CollectObjective(Items.GOLD_BLOCK, 2))
                .addObjective(new CollectObjective(Items.DIAMOND, 8))
                .addReward(new ItemReward(Items.NETHERITE_SCRAP, 2))
                .addReward(new ItemReward(Items.ANCIENT_DEBRIS, 1))
                .addReward(new ItemReward(Items.DIAMOND, 5))
                .addReward(XpReward.apprentice())
                .addPrerequisite("deep_delving");
    }

    private static Quest createMountainKingsTributeQuest() {
        return new Quest("mountain_kings_tribute")
                .setName("Mountain King's Tribute")
                .setDescription("Gather a tribute worthy of the Mountain Kings of old.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.DIAMOND_ORE, 16))
                .addObjective(new CollectObjective(Items.EMERALD_ORE, 8))
                .addObjective(new CollectObjective(Items.GOLD_BLOCK, 4))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 2))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 1))
                .addReward(new ItemReward(Items.NETHERITE_INGOT, 1))
                .addReward(XpReward.expert())
                .addPrerequisite("ancient_alloys");
    }

    private static Quest createRubyInTheRoughQuest() {
        return new Quest("ruby_in_rough")
                .setName("Ruby in the Rough")
                .setDescription("Find the rarest gems hidden in the deepest caverns.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.DIAMOND, 32))
                .addObjective(new CollectObjective(Items.EMERALD, 16))
                .addObjective(new CollectObjective(Items.LAPIS_LAZULI, 64))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 3))
                .addReward(new ItemReward(Items.EMERALD_BLOCK, 1))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("mountain_kings_tribute");
    }

    private static Quest createNetherForgeQuest() {
        return new Quest("nether_forge")
                .setName("The Nether Forge")
                .setDescription("Brave the Nether to gather materials for the ultimate forge.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.ANCIENT_DEBRIS, 8))
                .addObjective(new CollectObjective(Items.NETHERITE_SCRAP, 16))
                .addObjective(new CollectObjective(Items.CRYING_OBSIDIAN, 8))
                .addReward(new ItemReward(Items.NETHERITE_INGOT, 4))
                .addReward(new ItemReward(Items.RESPAWN_ANCHOR, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("ruby_in_rough");
    }

    private static Quest createMasterBlacksmithQuest() {
        return new Quest("master_blacksmith")
                .setName("Master Blacksmith")
                .setDescription("Craft the finest armor and weapons known to dwarf-kind.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 8))
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 32))
                .addReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.NETHERITE_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addReward(XpReward.master())
                .addPrerequisite("nether_forge");
    }

    private static Quest createRuneOfPowerQuest() {
        return new Quest("rune_of_power")
                .setName("Rune of Power")
                .setDescription("Inscribe ancient dwarven runes into your masterwork equipment.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.ENCHANTED_BOOK, 5))
                .addObjective(new CollectObjective(Items.LAPIS_BLOCK, 8))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 32))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 3))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 20))
                .addReward(new ItemReward(Items.DIAMOND, 10))
                .addReward(XpReward.master())
                .addPrerequisite("master_blacksmith");
    }

    private static Quest createHeartOfMountainQuest() {
        return new Quest("heart_of_mountain")
                .setName("Heart of the Mountain")
                .setDescription("Descend to the world's core and retrieve the legendary Heart of the Mountain.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 8))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 2))
                .addObjective(new CollectObjective(Items.BEACON, 1))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.NETHERITE_BOOTS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(XpReward.master())
                .addPrerequisite("rune_of_power");
    }

    private static Quest createForgemasterLegacyQuest() {
        return new Quest("forgemaster_legacy")
                .setName("The Forgemaster's Legacy")
                .setDescription("Complete your journey and become a true Forgemaster, worthy of the ancient dwarven halls.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Dwarf")
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 16))
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 16))
                .addObjective(new CollectObjective(Items.EMERALD_BLOCK, 8))
                .addReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addReward(new ItemReward(Items.NETHERITE_LEGGINGS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 10))
                .addReward(new ItemReward(Items.NETHER_STAR, 1))
                .addReward(XpReward.master())
                .addPrerequisite("heart_of_mountain");
    }

    // Register the Dwarf chain
    private static void registerDwarfQuestChain(QuestManager manager) {
        QuestChain dwarfChain = new QuestChain(
                "forgemasters_legacy",
                "The Forgemaster's Legacy",
                "Follow the ancient path of dwarven smiths. Master mining, crafting, and the secrets of the deep earth.",
                QuestData.QuestBookTier.NOVICE,
                null // No tier reward, race-specific
        )
                .addQuest("dwarf_apprentice")
                .addQuest("deep_delving")
                .addQuest("ancient_alloys")
                .addQuest("mountain_kings_tribute")
                .addQuest("ruby_in_rough")
                .addQuest("nether_forge")
                .addQuest("master_blacksmith")
                .addQuest("rune_of_power")
                .addQuest("heart_of_mountain")
                .addQuest("forgemaster_legacy")
                .addChainReward(new ItemReward(Items.DIAMOND_BLOCK, 10))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addChainReward(new ItemReward(Items.NETHER_STAR, 2));

        manager.registerQuestChain(dwarfChain);
    }

    // ========== ELF RACE QUESTS - "Guardian of the Wilds" ==========
// Theme: Nature, archery, forest protection, harmony with the land
// Unlocks at: Levels 1, 10, 20, 30, 40

    private static Quest createSeedlingQuest() {
        return new Quest("seedling")
                .setName("The Seedling")
                .setDescription("Every great forest begins with a single seed. Plant the foundations of a thriving woodland.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Elf") // Requires Elf race
                .addObjective(new CollectObjective(Items.OAK_SAPLING, 32))
                .addObjective(new CollectObjective(Items.BIRCH_SAPLING, 16))
                .addObjective(new CollectObjective(Items.SPRUCE_SAPLING, 16))
                .addReward(new ItemReward(Items.BONE_MEAL, 32))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createRootsRunDeepQuest() {
        return new Quest("roots_run_deep")
                .setName("Roots Run Deep")
                .setDescription("The ancient trees remember. Gather wood from the eldest groves.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.OAK_LOG, 64))
                .addObjective(new CollectObjective(Items.DARK_OAK_LOG, 32))
                .addObjective(new CollectObjective(Items.JUNGLE_LOG, 32))
                .addReward(new ItemReward(Items.DIAMOND_AXE, 1))
                .addReward(new ItemReward(Items.APPLE, 16))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 3))
                .addReward(XpReward.apprentice())
                .addPrerequisite("seedling");
    }

    private static Quest createBowmastersTrialQuest() {
        return new Quest("bowmasters_trial")
                .setName("Bowmaster's Trial")
                .setDescription("Master the sacred art of the bow. Hunt with precision and honor.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Elf")
                .addObjective(new KillObjective(EntityType.SKELETON, 20))
                .addObjective(new CollectObjective(Items.BONE, 32))
                .addObjective(new CollectObjective(Items.ARROW, 64))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.ARROW, 128))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 1))
                .addReward(XpReward.apprentice())
                .addPrerequisite("roots_run_deep");
    }

    private static Quest createCorruptedGroveQuest() {
        return new Quest("corrupted_grove")
                .setName("The Corrupted Grove")
                .setDescription("Darkness has taken root in the sacred groves. Cleanse the corruption.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Elf")
                .addObjective(KillObjective.zombies(15))
                .addObjective(new KillObjective(EntityType.SPIDER, 12))
                .addObjective(new KillObjective(EntityType.CREEPER, 8))
                .addReward(new ItemReward(Items.DIAMOND, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 1))
                .addReward(XpReward.expert())
                .addPrerequisite("bowmasters_trial");
    }

    private static Quest createWhispersOfLeavesQuest() {
        return new Quest("whispers_of_leaves")
                .setName("Whispers of the Leaves")
                .setDescription("The forest speaks to those who listen. Gather its gifts with reverence.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.OAK_LEAVES, 128))
                .addObjective(new CollectObjective(Items.AZALEA, 16))
                .addObjective(new CollectObjective(Items.GLOW_BERRIES, 32))
                .addReward(new ItemReward(Items.MOSS_BLOCK, 64))
                .addReward(new ItemReward(Items.FLOWERING_AZALEA, 8))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("corrupted_grove");
    }

    private static Quest createSilverBowQuest() {
        return new Quest("silver_bow")
                .setName("The Silver Bow")
                .setDescription("Craft a bow worthy of the ancient elven rangers. Imbue it with moonlight.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.STRING, 32))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 8))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.SPECTRAL_ARROW, 64))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("whispers_of_leaves");
    }

    private static Quest createWildernessWardenQuest() {
        return new Quest("wilderness_warden")
                .setName("Wilderness Warden")
                .setDescription("Protect the sacred places. Drive back those who would defile nature.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Elf")
                .addObjective(new KillObjective(EntityType.PILLAGER, 10))
                .addObjective(new KillObjective(EntityType.VINDICATOR, 5))
                .addObjective(new KillObjective(EntityType.RAVAGER, 3))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 2))
                .addReward(new ItemReward(Items.EMERALD, 32))
                .addReward(XpReward.master())
                .addPrerequisite("silver_bow");
    }

    private static Quest createWorldTreeSaplingQuest() {
        return new Quest("world_tree_sapling")
                .setName("The World Tree's Sapling")
                .setDescription("Seek the legendary World Tree's offspring, hidden in the deepest forests.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.DARK_OAK_SAPLING, 64))
                .addObjective(new CollectObjective(Items.BONE_MEAL, 128))
                .addObjective(new CollectObjective(Items.MOSS_BLOCK, 64))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 4))
                .addReward(new ItemReward(Items.EMERALD_BLOCK, 2))
                .addReward(XpReward.master())
                .addPrerequisite("wilderness_warden");
    }

    private static Quest createMoonlitRitualQuest() {
        return new Quest("moonlit_ritual")
                .setName("The Moonlit Ritual")
                .setDescription("Under the full moon, perform the ancient ritual of renewal.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.GLOWSTONE_DUST, 64))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 16))
                .addObjective(new CollectObjective(Items.GOLDEN_APPLE, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 32))
                .addReward(new ItemReward(Items.BEACON, 1))
                .addReward(XpReward.master())
                .addPrerequisite("world_tree_sapling");
    }

    private static Quest createForestLordQuest() {
        return new Quest("forest_lord")
                .setName("Forest Lord")
                .setDescription("Achieve mastery over nature itself. Become one with the eternal forest.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Elf")
                .addObjective(new CollectObjective(Items.EMERALD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 16))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 8))
                .addReward(new ItemReward(Items.ELYTRA, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addReward(new ItemReward(Items.NETHER_STAR, 1))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 3))
                .addReward(XpReward.master())
                .addPrerequisite("moonlit_ritual");
    }

    // Register the Elf chain
    private static void registerElfQuestChain(QuestManager manager) {
        QuestChain elfChain = new QuestChain(
                "guardian_of_wilds",
                "Guardian of the Wilds",
                "Walk the path of the ancient elven rangers. Protect the forests, master the bow, and commune with nature.",
                QuestData.QuestBookTier.NOVICE,
                null // No tier reward, race-specific
        )
                .addQuest("seedling")
                .addQuest("roots_run_deep")
                .addQuest("bowmasters_trial")
                .addQuest("corrupted_grove")
                .addQuest("whispers_of_leaves")
                .addQuest("silver_bow")
                .addQuest("wilderness_warden")
                .addQuest("world_tree_sapling")
                .addQuest("moonlit_ritual")
                .addQuest("forest_lord")
                .addChainReward(new ItemReward(Items.EMERALD_BLOCK, 8))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addChainReward(new ItemReward(Items.NETHER_STAR, 2));

        manager.registerQuestChain(elfChain);
    }

    // ========== HUMAN RACE QUESTS - "Jack of All Trades" ==========
// Theme: Versatility, exploration, adaptability, mastering diverse skills
// Unlocks at: Levels 1, 10, 20, 30, 40

    private static Quest createWandererQuest() {
        return new Quest("wanderer")
                .setName("The Wanderer")
                .setDescription("Humans thrive through adaptability. Begin your journey by mastering the basics of survival.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Human") // Requires Human race
                .addObjective(new CollectObjective(Items.BREAD, 16))
                .addObjective(new CollectObjective(Items.COBBLESTONE, 32))
                .addObjective(new CollectObjective(Items.OAK_LOG, 16))
                .addReward(new ItemReward(Items.STONE_SWORD, 1))
                .addReward(new ItemReward(Items.STONE_PICKAXE, 1))
                .addReward(new ItemReward(Items.COOKED_BEEF, 8))
                .addReward(XpReward.novice());
    }

    private static Quest createTasteOfEverythingQuest() {
        return new Quest("taste_of_everything")
                .setName("A Taste of Everything")
                .setDescription("True versatility comes from experiencing all aspects of life. Craft items from diverse categories.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.IRON_SWORD, 1))
                .addObjective(new CollectObjective(Items.IRON_PICKAXE, 1))
                .addObjective(new CollectObjective(Items.BREAD, 8))
                .addObjective(new CollectObjective(Items.LEATHER_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.DIAMOND, 4))
                .addReward(new ItemReward(Items.GOLD_INGOT, 8))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("wanderer");
    }

    private static Quest createWorldTravelerQuest() {
        return new Quest("world_traveler")
                .setName("World Traveler")
                .setDescription("Explore the diverse biomes of the world. Adaptability requires understanding all environments.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.SAND, 32))
                .addObjective(new CollectObjective(Items.SNOWBALL, 16))
                .addObjective(new CollectObjective(Items.JUNGLE_LOG, 16))
                .addObjective(new CollectObjective(Items.RED_SAND, 16))
                .addReward(new ItemReward(Items.COMPASS, 1))
                .addReward(new ItemReward(Items.MAP, 3))
                .addReward(new ItemReward(Items.ENDER_PEARL, 4))
                .addReward(XpReward.apprentice())
                .addPrerequisite("taste_of_everything");
    }

    private static Quest createMasterTraderQuest() {
        return new Quest("master_trader")
                .setName("Master Trader")
                .setDescription("Humans excel at diplomacy and trade. Build relationships with the world's merchants.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.EMERALD, 32))
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 16))
                .addObjective(new CollectObjective(Items.DIAMOND, 8))
                .addReward(new ItemReward(Items.EMERALD_BLOCK, 2))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 2))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 4))
                .addReward(XpReward.expert())
                .addPrerequisite("world_traveler");
    }

    private static Quest createRenaissanceScholarQuest() {
        return new Quest("renaissance_scholar")
                .setName("Renaissance Scholar")
                .setDescription("Knowledge is power. Master the arts of enchanting, brewing, and crafting.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.ENCHANTING_TABLE, 1))
                .addObjective(new CollectObjective(Items.BREWING_STAND, 1))
                .addObjective(new CollectObjective(Items.ANVIL, 1))
                .addObjective(new CollectObjective(Items.BOOKSHELF, 16))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 16))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 3))
                .addReward(new ItemReward(Items.DIAMOND, 8))
                .addReward(XpReward.expert())
                .addPrerequisite("master_trader");
    }

    private static Quest createBridgeBuilderQuest() {
        return new Quest("bridge_builder")
                .setName("The Bridge Builder")
                .setDescription("Humans unite disparate peoples. Gather resources that span all realms.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.NETHERRACK, 64))
                .addObjective(new CollectObjective(Items.END_STONE, 32))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 16))
                .addReward(new ItemReward(Items.ENDER_PEARL, 16))
                .addReward(new ItemReward(Items.BLAZE_ROD, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("renaissance_scholar");
    }

    private static Quest createHerosJourneyQuest() {
        return new Quest("heros_journey")
                .setName("Hero's Journey")
                .setDescription("Prove yourself against the greatest challenges. Complete tasks from all walks of life.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 4))
                .addObjective(new KillObjective(EntityType.WITHER_SKELETON, 5))
                .addObjective(new CollectObjective(Items.NETHER_STAR, 1))
                .addReward(new ItemReward(Items.NETHERITE_INGOT, 4))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(new ItemReward(Items.BEACON, 1))
                .addReward(XpReward.master())
                .addPrerequisite("bridge_builder");
    }

    private static Quest createDiplomaticMissionQuest() {
        return new Quest("diplomatic_mission")
                .setName("Diplomatic Mission")
                .setDescription("Forge alliances with all peoples. Gather tributes from every corner of the realm.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.EMERALD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.GOLD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.IRON_BLOCK, 16))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 4))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 2))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 4))
                .addReward(XpReward.master())
                .addPrerequisite("heros_journey");
    }

    private static Quest createMasterOfAllTradesQuest() {
        return new Quest("master_of_all_trades")
                .setName("Master of All Trades")
                .setDescription("Achieve true mastery. Demonstrate expertise in combat, crafting, and exploration.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Human")
                .addObjective(new KillObjective(EntityType.ENDER_DRAGON, 1))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 2))
                .addObjective(new CollectObjective(Items.ELYTRA, 1))
                .addReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addReward(new ItemReward(Items.NETHERITE_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(XpReward.master())
                .addPrerequisite("diplomatic_mission");
    }

    private static Quest createLegendQuest() {
        return new Quest("legend")
                .setName("Legend")
                .setDescription("Transcend mortality. Become a legend whose name echoes through the ages.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Human")
                .addObjective(new CollectObjective(Items.NETHER_STAR, 2))
                .addObjective(new CollectObjective(Items.DRAGON_EGG, 1))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 8))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.NETHERITE_LEGGINGS, 1))
                .addReward(new ItemReward(Items.NETHERITE_BOOTS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addReward(new ItemReward(Items.NETHER_STAR, 2))
                .addReward(XpReward.master())
                .addPrerequisite("master_of_all_trades");
    }

    // Register the Human chain
    private static void registerHumanQuestChain(QuestManager manager) {
        QuestChain humanChain = new QuestChain(
                "jack_of_all_trades",
                "Jack of All Trades",
                "The human path is one of infinite possibility. Master all skills, explore all lands, and become legendary.",
                QuestData.QuestBookTier.NOVICE,
                null // No tier reward, race-specific
        )
                .addQuest("wanderer")
                .addQuest("taste_of_everything")
                .addQuest("world_traveler")
                .addQuest("master_trader")
                .addQuest("renaissance_scholar")
                .addQuest("bridge_builder")
                .addQuest("heros_journey")
                .addQuest("diplomatic_mission")
                .addQuest("master_of_all_trades")
                .addQuest("legend")
                .addChainReward(new ItemReward(Items.DIAMOND_BLOCK, 16))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addChainReward(new ItemReward(Items.NETHER_STAR, 3));

        manager.registerQuestChain(humanChain);
    }

    // ========== ORC RACE QUESTS - "Path of the Warlord" ==========
// Theme: Combat, hunting, strength, conquest, honor in battle
// Unlocks at: Levels 1, 10, 20, 30, 40

    private static Quest createGruntQuest() {
        return new Quest("grunt")
                .setName("The Grunt")
                .setDescription("Every warrior begins as a grunt. Prove your strength in combat and bring meat for the clan.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Orc") // Requires Orc race
                .addObjective(KillObjective.zombies(10))
                .addObjective(new CollectObjective(Items.COOKED_BEEF, 8))
                .addObjective(new CollectObjective(Items.COOKED_PORKCHOP, 8))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.COOKED_BEEF, 16))
                .addReward(new ItemReward(Items.LEATHER_CHESTPLATE, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createProveYourStrengthQuest() {
        return new Quest("prove_your_strength")
                .setName("Prove Your Strength")
                .setDescription("The weak fall, the strong survive. Deal devastating blows to your enemies.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Orc")
                .addObjective(KillObjective.zombies(15))
                .addObjective(KillObjective.skeletons(10))
                .addObjective(new KillObjective(EntityType.CREEPER, 5))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(new ItemReward(Items.IRON_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 3))
                .addReward(XpReward.apprentice())
                .addPrerequisite("grunt");
    }

    private static Quest createHuntTheMightyQuest() {
        return new Quest("hunt_the_mighty")
                .setName("Hunt the Mighty")
                .setDescription("Only the greatest hunters can claim the mightiest trophies. Bring down powerful beasts.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Orc")
                .addObjective(new KillObjective(EntityType.IRON_GOLEM, 2))
                .addObjective(new KillObjective(EntityType.RAVAGER, 1))
                .addObjective(new CollectObjective(Items.BEEF, 16))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 1))
                .addReward(new ItemReward(Items.EMERALD, 8))
                .addReward(XpReward.apprentice())
                .addPrerequisite("prove_your_strength");
    }

    private static Quest createRaidLeaderQuest() {
        return new Quest("raid_leader")
                .setName("Raid Leader")
                .setDescription("Lead your warriors to victory. Complete dangerous raids and emerge triumphant.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Orc")
                .addObjective(new KillObjective(EntityType.PILLAGER, 12))
                .addObjective(new KillObjective(EntityType.VINDICATOR, 8))
                .addObjective(new KillObjective(EntityType.EVOKER, 3))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 1))
                .addReward(new ItemReward(Items.EMERALD, 16))
                .addReward(XpReward.expert())
                .addPrerequisite("hunt_the_mighty");
    }

    private static Quest createBloodAndIronQuest() {
        return new Quest("blood_and_iron")
                .setName("Blood and Iron")
                .setDescription("Forge weapons in the fires of battle. Gather the materials of war.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Orc")
                .addObjective(new CollectObjective(Items.IRON_INGOT, 64))
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 32))
                .addReward(new ItemReward(Items.DIAMOND_AXE, 1))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("raid_leader");
    }

    private static Quest createBattleScarsQuest() {
        return new Quest("battle_scars")
                .setName("Battle Scars")
                .setDescription("Every scar tells a story of survival. Face the deadliest foes and live to tell the tale.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Orc")
                .addObjective(new KillObjective(EntityType.BLAZE, 10))
                .addObjective(new KillObjective(EntityType.WITHER_SKELETON, 8))
                .addObjective(new KillObjective(EntityType.GHAST, 5))
                .addReward(new ItemReward(Items.NETHERITE_SCRAP, 4))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("blood_and_iron");
    }

    private static Quest createChampionOfTheClanQuest() {
        return new Quest("champion_of_clan")
                .setName("Champion of the Clan")
                .setDescription("Rise above your peers. Become the champion your clan needs.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Orc")
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 8))
                .addObjective(new KillObjective(EntityType.ENDERMAN, 10))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 16))
                .addReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(XpReward.master())
                .addPrerequisite("battle_scars");
    }

    private static Quest createConquerTheNetherQuest() {
        return new Quest("conquer_the_nether")
                .setName("Conquer the Nether")
                .setDescription("The Nether belongs to the strong. Claim its treasures through might and fury.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Orc")
                .addObjective(new CollectObjective(Items.ANCIENT_DEBRIS, 16))
                .addObjective(new KillObjective(EntityType.PIGLIN_BRUTE, 10))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 2))
                .addReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.NETHERITE_LEGGINGS, 1))
                .addReward(new ItemReward(Items.BEACON, 1))
                .addReward(XpReward.master())
                .addPrerequisite("champion_of_clan");
    }

    private static Quest createChallengeTheWitherQuest() {
        return new Quest("challenge_the_wither")
                .setName("Challenge the Wither")
                .setDescription("Only the mightiest warriors dare challenge the Wither. Prove you are worthy.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Orc")
                .addObjective(new KillObjective(EntityType.WITHER, 1))
                .addObjective(new CollectObjective(Items.NETHER_STAR, 1))
                .addReward(new ItemReward(Items.NETHERITE_BOOTS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 2))
                .addReward(XpReward.master())
                .addPrerequisite("conquer_the_nether");
    }

    private static Quest createWarlordQuest() {
        return new Quest("warlord")
                .setName("Warlord")
                .setDescription("You have conquered all challenges. Claim your place as Warlord, the ultimate warrior.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredClass("Orc")
                .addObjective(new KillObjective(EntityType.ENDER_DRAGON, 1))
                .addObjective(new CollectObjective(Items.NETHER_STAR, 2))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 4))
                .addReward(new ItemReward(Items.NETHERITE_AXE, 1))
                .addReward(new ItemReward(Items.NETHERITE_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addReward(new ItemReward(Items.NETHER_STAR, 2))
                .addReward(new ItemReward(Items.DRAGON_EGG, 1))
                .addReward(XpReward.master())
                .addPrerequisite("challenge_the_wither");
    }

    // Register the Orc chain
    private static void registerOrcQuestChain(QuestManager manager) {
        QuestChain orcChain = new QuestChain(
                "path_of_warlord",
                "Path of the Warlord",
                "The way of the warrior is written in blood and steel. Prove your strength, conquer your foes, and become Warlord.",
                QuestData.QuestBookTier.NOVICE,
                null // No tier reward, race-specific
        )
                .addQuest("grunt")
                .addQuest("prove_your_strength")
                .addQuest("hunt_the_mighty")
                .addQuest("raid_leader")
                .addQuest("blood_and_iron")
                .addQuest("battle_scars")
                .addQuest("champion_of_clan")
                .addQuest("conquer_the_nether")
                .addQuest("challenge_the_wither")
                .addQuest("warlord")
                .addChainReward(new ItemReward(Items.NETHERITE_BLOCK, 4))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addChainReward(new ItemReward(Items.NETHER_STAR, 3));

        manager.registerQuestChain(orcChain);
    }

    // ========== WARRIOR CLASS QUESTS ==========

    private static Quest createShieldBearerQuest() {
        return new Quest("shield_bearer")
                .setName("The Shield Bearer")
                .setDescription("A warrior needs protection. Gather materials for defensive equipment.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Warrior")
                .addObjective(new CollectObjective(Items.IRON_INGOT, 24))
                .addObjective(new CollectObjective(Items.LEATHER, 8))
                .addReward(new ItemReward(Items.SHIELD, 1))
                .addReward(new ItemReward(Items.IRON_HELMET, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createWarriorTrainingQuest() {
        return new Quest("warrior_training")
                .setName("Warrior's Training")
                .setDescription("Prove your combat prowess by slaying enemies in honorable battle.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Warrior")
                .addObjective(KillObjective.zombies(15))
                .addObjective(KillObjective.skeletons(10))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.IRON_CHESTPLATE, 1))
                .addReward(XpReward.apprentice());
    }

    private static Quest createBattleMasterQuest() {
        return new Quest("battle_master")
                .setName("Battle Master")
                .setDescription("Face the most dangerous foes and emerge victorious.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Warrior")
                .addObjective(new KillObjective(EntityType.CREEPER, 5))
                .addObjective(new KillObjective(EntityType.SPIDER, 8))
                .addObjective(KillObjective.zombies(12))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 3))
                .addReward(XpReward.expert());
    }

    // ========== MAGE CLASS QUESTS ==========

    private static Quest createArcanistApprenticeQuest() {
        return new Quest("arcanist_apprentice")
                .setName("Arcanist's Apprentice")
                .setDescription("Gather magical reagents to begin your study of the arcane arts.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.LAPIS_LAZULI, 32))
                .addObjective(new CollectObjective(Items.BOOK, 5))
                .addObjective(new CollectObjective(Items.GLOWSTONE_DUST, 16))
                .addReward(new ItemReward(Items.ENCHANTING_TABLE, 1))
                .addReward(new ItemReward(Items.BOOKSHELF, 3))
                .addReward(XpReward.novice());
    }

    private static Quest createPotionMasterQuest() {
        return new Quest("potion_master")
                .setName("Potion Master")
                .setDescription("Master the art of potion brewing by gathering rare ingredients.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.NETHER_WART, 16))
                .addObjective(new CollectObjective(Items.BLAZE_POWDER, 8))
                .addObjective(new CollectObjective(Items.SPIDER_EYE, 4))
                .addReward(new ItemReward(Items.BREWING_STAND, 1))
                .addReward(new ItemReward(Items.POTION, 3))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 5))
                .addReward(XpReward.apprentice());
    }

    private static Quest createEnchantmentSageQuest() {
        return new Quest("enchantment_sage")
                .setName("Enchantment Sage")
                .setDescription("Collect powerful enchantments to unlock the secrets of magic.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.DIAMOND, 8))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 16))
                .addObjective(new CollectObjective(Items.LAPIS_LAZULI, 64))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 3))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 10))
                .addReward(XpReward.expert());
    }

    // ========== ROGUE CLASS QUESTS ==========

    private static Quest createShadowStrikerQuest() {
        return new Quest("shadow_striker")
                .setName("Shadow Striker")
                .setDescription("Use your agility to hunt down swift and dangerous prey.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(EntityType.SPIDER, 10))
                .addObjective(new KillObjective(EntityType.CAVE_SPIDER, 5))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.ARROW, 64))
                .addReward(new ItemReward(Items.LEATHER_BOOTS, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createTreasureHunterQuest() {
        return new Quest("treasure_hunter")
                .setName("Treasure Hunter")
                .setDescription("Seek out valuable treasures hidden in dangerous places.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredClass("Rogue")
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 16))
                .addObjective(new CollectObjective(Items.EMERALD, 5))
                .addObjective(new CollectObjective(Items.DIAMOND, 3))
                .addReward(new ItemReward(Items.ENDER_PEARL, 8))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.apprentice());
    }

    private static Quest createMasterAssassinQuest() {
        return new Quest("master_assassin")
                .setName("Master Assassin")
                .setDescription("Execute precision strikes against formidable enemies.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredClass("Rogue")
                .addObjective(KillObjective.skeletons(15))
                .addObjective(new KillObjective(EntityType.ENDERMAN, 3))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 8))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 2))
                .addReward(XpReward.expert());
    }

    // ========== ADVENTURER'S PATH CHAIN QUESTS ==========

    private static Quest createTheBeginningQuest() {
        return new Quest("the_beginning")
                .setName("The Beginning")
                .setDescription("Every adventurer needs basic tools. Start your journey here.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.OAK_LOG, 5))
                .addReward(new ItemReward(Items.WOODEN_AXE, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createEquipYourselfQuest() {
        return new Quest("equip_yourself")
                .setName("Equip Yourself")
                .setDescription("Now that you have an axe, gather materials for better tools.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.OAK_LOG, 10))
                .addObjective(new CollectObjective(Items.COBBLESTONE, 16))
                .addReward(new ItemReward(Items.STONE_SWORD, 1))
                .addReward(new ItemReward(Items.STONE_PICKAXE, 1))
                .addReward(XpReward.novice())
                .addPrerequisite("the_beginning");
    }

    private static Quest createReadyForAdventureQuest() {
        return new Quest("ready_for_adventure")
                .setName("Ready for Adventure")
                .setDescription("Stock up on supplies. Your real adventure begins now!")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.BREAD, 8))
                .addObjective(new CollectObjective(Items.COOKED_BEEF, 4))
                .addReward(new ItemReward(Items.LEATHER_BOOTS, 1))
                .addReward(new ItemReward(Items.LEATHER_HELMET, 1))
                .addReward(XpReward.novice())
                .addPrerequisite("equip_yourself");
    }

    // ========== VILLAGE DEVELOPMENT CHAIN QUESTS ==========

    private static Quest createVillageFounderQuest() {
        return new Quest("village_founder")
                .setName("Village Founder")
                .setDescription("Start building the foundation of a great village.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 128))
                .addObjective(new CollectObjective(Items.OAK_PLANKS, 64))
                .addReward(new ItemReward(Items.EMERALD, 5))
                .addReward(XpReward.apprentice());
    }

    private static Quest createVillageBuilderQuest() {
        return new Quest("village_builder")
                .setName("Village Builder")
                .setDescription("The village is expanding! Help construct new buildings.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 64))
                .addObjective(new CollectObjective(Items.OAK_PLANKS, 32))
                .addObjective(new CollectObjective(Items.GLASS, 16))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(XpReward.apprentice())
                .addPrerequisite("village_founder");
    }

    private static Quest createLivestockKeeperQuest() {
        return new Quest("livestock_keeper")
                .setName("Livestock Keeper")
                .setDescription("Establish proper livestock management for the village.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.BEEF, 8))
                .addObjective(new CollectObjective(Items.PORKCHOP, 8))
                .addObjective(new CollectObjective(Items.LEATHER, 12))
                .addObjective(new CollectObjective(Items.MILK_BUCKET, 4))
                .addReward(new ItemReward(Items.GOLDEN_CARROT, 8))
                .addReward(new ItemReward(Items.LEAD, 4))
                .addReward(new ItemReward(Items.NAME_TAG, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("village_builder");
    }

    private static Quest createVillageDefenderQuest() {
        return new Quest("village_defender")
                .setName("Village Defender")
                .setDescription("Protect the growing village from threats.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.zombies(10))
                .addObjective(KillObjective.skeletons(8))
                .addReward(new ItemReward(Items.IRON_CHESTPLATE, 1))
                .addReward(XpReward.apprentice())
                .addPrerequisite("livestock_keeper");
    }

    private static Quest createVillageMasterQuest() {
        return new Quest("village_master")
                .setName("Village Master")
                .setDescription("Complete the transformation into a thriving settlement.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.EMERALD, 15))
                .addObjective(new CollectObjective(Items.BREAD, 32))
                .addReward(new ItemReward(Items.DIAMOND, 3))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.expert())
                .addPrerequisite("village_defender");
    }

    // ========== MASTER CRAFTSMAN CHAIN QUESTS ==========

    private static Quest createToolmakerQuest() {
        return new Quest("toolmaker")
                .setName("The Toolmaker")
                .setDescription("The village blacksmith needs help creating quality tools.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.IRON_INGOT, 16))
                .addObjective(new CollectObjective(Items.STICK, 24))
                .addObjective(new CollectObjective(Items.COAL, 20))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(Items.BUCKET, 1))
                .addReward(new ItemReward(Items.DIAMOND, 2))
                .addReward(XpReward.apprentice());
    }

    private static Quest createIronMinerQuest() {
        return new Quest("iron_miner")
                .setName("Iron Miner")
                .setDescription("The village needs iron for advanced tools and armor.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.RAW_IRON, 24))
                .addObjective(new CollectObjective(Items.COAL, 16))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(Items.IRON_INGOT, 8))
                .addReward(XpReward.expert())
                .addPrerequisite("toolmaker");
    }

    private static Quest createMasterCrafterQuest() {
        return new Quest("master_crafter")
                .setName("Master Crafter")
                .setDescription("Prove your mastery by gathering the rarest materials.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .addObjective(new CollectObjective(Items.DIAMOND, 5))
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 10))
                .addObjective(new CollectObjective(Items.EMERALD, 3))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.master())
                .addPrerequisite("iron_miner");
    }

    // ========== COMBAT SPECIALIST CHAIN QUESTS ==========

    private static Quest createMonsterHunterQuest() {
        return new Quest("monster_hunter")
                .setName("Monster Hunter")
                .setDescription("The village is under threat! Clear out dangerous creatures.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.zombies(3))
                .addObjective(KillObjective.skeletons(2))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.ARROW, 32))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(XpReward.apprentice());
    }

    private static Quest createCaveCleanerQuest() {
        return new Quest("cave_cleaner")
                .setName("Cave Cleaner")
                .setDescription("The mines are infested with dangerous creatures. Make them safe.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.spiders(4))
                .addObjective(new CollectObjective(Items.SPIDER_EYE, 2))
                .addReward(new ItemReward(Items.TORCH, 16))
                .addReward(new ItemReward(Items.IRON_HELMET, 1))
                .addReward(XpReward.apprentice())
                .addPrerequisite("monster_hunter");
    }

    private static Quest createNightWatchQuest() {
        return new Quest("night_watch")
                .setName("Night Watch")
                .setDescription("Protect the village through the dangerous night hours.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(KillObjective.zombies(8))
                .addObjective(KillObjective.skeletons(6))
                .addObjective(new KillObjective(EntityType.CREEPER, 3))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 3))
                .addReward(XpReward.expert())
                .addPrerequisite("cave_cleaner");
    }

    private static Quest createBountyHunterQuest() {
        return new Quest("bounty_hunter")
                .setName("Bounty Hunter")
                .setDescription("Eliminate threats and collect proof of your victories.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(KillObjective.zombies(5))
                .addObjective(new CollectObjective(Items.ROTTEN_FLESH, 10))
                .addObjective(new CollectObjective(Items.BONE, 8))
                .addReward(new ItemReward(Items.DIAMOND, 3))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("night_watch");
    }

    // ========== ADDITIONAL QUESTS ==========

    private static Quest createDeepMinerQuest() {
        return new Quest("deep_miner")
                .setName("Deep Miner")
                .setDescription("Venture into the depths to gather precious resources.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.IRON_ORE, 20))
                .addObjective(new CollectObjective(Items.GOLD_ORE, 8))
                .addObjective(new CollectObjective(Items.DIAMOND_ORE, 3))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_BOOK, 1))
                .addReward(XpReward.expert());
    }
}