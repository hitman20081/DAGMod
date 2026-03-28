package com.github.hitman20081.dagmod.quest.registry;

import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestChain;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.MultiItemCollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.TagCollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import net.minecraft.registry.tag.ItemTags;
import com.github.hitman20081.dagmod.quest.rewards.EnchantedBookReward;
import com.github.hitman20081.dagmod.quest.rewards.ItemReward;
import com.github.hitman20081.dagmod.quest.rewards.UnlockReward;
import com.github.hitman20081.dagmod.quest.rewards.XpReward;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class QuestRegistry {

    public static void registerQuests() {
        QuestManager manager = QuestManager.getInstance();

        // Register individual quests first
        registerIndividualQuests(manager);

        // Then register quest chains
        registerQuestChains(manager);
    }

    private static void registerIndividualQuests(QuestManager manager) {
        // ========== RACE-SPECIFIC QUESTS ==========

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

        // ========== DUNGEON DISCOVERY QUESTS ==========
        manager.registerQuest(createRumoursOfTheBoneKingQuest());
        manager.registerQuest(createRedDragonFuryQuest());

        // Class quest registration is handled inside registerQuestChains()

        // ========== CHAIN QUESTS ==========
        // Adventurer's Path Chain
        // REMOVED: createTheBeginningQuest() - buggy log collection
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

        // Reset System Quests
        manager.registerQuest(createIdentityCrisisQuest());
        manager.registerQuest(createPathOfDestinyQuest());
        manager.registerQuest(createRebirthRitualQuest());

        // NPC Quests
        manager.registerQuest(createGarricksSpecialBrewQuest());

        // ========== JOB BOARD QUESTS ==========
        manager.registerQuest(createGatherCobblestoneJob());
        manager.registerQuest(createHuntZombiesJob());
        manager.registerQuest(createCollectWheatJob());
        manager.registerQuest(createMineIronJob());
    }

    private static void registerQuestChains(QuestManager manager) {
        // CHAIN 1: Adventurer's Path (Novice → Apprentice)
        QuestChain adventurerPath = new QuestChain(
                "adventurer_path",
                "The Adventurer's Path",
                "Your first steps into the world of adventure. Start at the inn!",
                QuestData.QuestBookTier.NOVICE,
                QuestData.QuestBookTier.APPRENTICE
        )
                .addQuest("garricks_special_brew")  // FIRST - Tutorial quest at the inn
                .addQuest("equip_yourself")         // SECOND - Gather materials
                .addQuest("ready_for_adventure")    // THIRD - Advanced preparation
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
        registerWarriorQuestChain(manager);
        registerMageQuestChain(manager);
        registerRogueQuestChain(manager);
    }

    // ========== DWARF RACE QUESTS - "The Forgemaster's Legacy" ==========

    private static Quest createDwarfApprenticeQuest() {
        return new Quest("dwarf_apprentice")
                .setName("Apprentice of the Forge")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Every dwarf must prove their worth at the forge. Gather materials for your first creation.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredRace("Dwarf")
                .addObjective(new CollectObjective(Items.IRON_INGOT, 16))
                .addObjective(new CollectObjective(Items.COAL, 32))
                .addObjective(new CollectObjective(Items.STONE, 64))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(new ItemReward(ModItems.BEEF_STEW, 3))
                .addReward(new ItemReward(ModItems.HONEY_BREAD, 3))
                .addReward(XpReward.novice());
    }

    private static Quest createDeepDelvingQuest() {
        return new Quest("deep_delving")
                .setName("Deep Delving")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("The deepest treasures lie far below. Mine at the lowest depths to find rare ores.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Dwarf")
                .addObjective(new CollectObjective(Items.DEEPSLATE, 32))
                .addObjective(new CollectObjective(Items.DEEPSLATE_IRON_ORE, 8))
                .addObjective(new CollectObjective(Items.DEEPSLATE_GOLD_ORE, 4))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new ItemReward(Items.DIAMOND, 4))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("dwarf_apprentice");
    }

    private static Quest createAncientAlloysQuest() {
        return new Quest("ancient_alloys")
                .setName("Ancient Alloys")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Learn the secrets of dwarven metallurgy. Craft with the strongest materials.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Dwarf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Gather a tribute worthy of the Mountain Kings of old.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Dwarf")
                .addObjective(new MultiItemCollectObjective("Diamond Ore", 16, Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE))
                .addObjective(new MultiItemCollectObjective("Emerald Ore", 8, Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE))
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Find the rarest gems hidden in the deepest caverns.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Dwarf")
                .addObjective(new CollectObjective(Items.DIAMOND, 32))
                .addObjective(new CollectObjective(Items.EMERALD, 16))
                .addObjective(new CollectObjective(Items.LAPIS_LAZULI, 64))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 3))
                .addReward(new ItemReward(Items.EMERALD_BLOCK, 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("efficiency"), 4))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("fortune"), 2))
                .addReward(XpReward.expert())
                .addPrerequisite("mountain_kings_tribute");
    }

    private static Quest createNetherForgeQuest() {
        return new Quest("nether_forge")
                .setName("The Nether Forge")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Brave the Nether to gather materials for the ultimate forge.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Dwarf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Craft the finest armor and weapons known to dwarf-kind.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Dwarf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Inscribe ancient dwarven runes into your masterwork equipment.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Dwarf")
                .addObjective(new CollectObjective(Items.LAPIS_BLOCK, 8))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 32))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("unbreaking"), 3))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("mending"), 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("silk_touch"), 1))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 20))
                .addReward(new ItemReward(Items.DIAMOND, 10))
                .addReward(XpReward.master())
                .addPrerequisite("master_blacksmith");
    }

    private static Quest createHeartOfMountainQuest() {
        return new Quest("heart_of_mountain")
                .setName("Heart of the Mountain")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Descend to the world's core and retrieve the legendary Heart of the Mountain.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Dwarf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Complete your journey and become a true Forgemaster, worthy of the ancient dwarven halls.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Dwarf")
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

    private static void registerDwarfQuestChain(QuestManager manager) {
        QuestChain dwarfChain = new QuestChain(
                "forgemasters_legacy",
                "The Forgemaster's Legacy",
                "Follow the ancient path of dwarven smiths. Master mining, crafting, and the secrets of the deep earth.",
                QuestData.QuestBookTier.NOVICE,
                null
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

    private static Quest createSeedlingQuest() {
        return new Quest("seedling")
                .setName("The Seedling")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Every great forest begins with a single seed. Plant the foundations of a thriving woodland.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredRace("Elf")
                .addObjective(new CollectObjective(Items.OAK_SAPLING, 32))
                .addObjective(new CollectObjective(Items.BIRCH_SAPLING, 16))
                .addObjective(new CollectObjective(Items.SPRUCE_SAPLING, 16))
                .addReward(new ItemReward(Items.BONE_MEAL, 32))
                .addReward(new ItemReward(ModItems.ELVEN_BREAD, 4))
                .addReward(new ItemReward(ModItems.GLOWBERRY_JAM, 3))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createRootsRunDeepQuest() {
        return new Quest("roots_run_deep")
                .setName("Roots Run Deep")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("The ancient trees remember. Gather wood from the eldest groves.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Master the sacred art of the bow. Hunt with precision and honor.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Elf")
                .addObjective(new KillObjective(EntityType.SKELETON, 20))
                .addObjective(new CollectObjective(Items.BONE, 32))
                .addObjective(new CollectObjective(Items.FLINT, 48))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.ARROW, 128))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("power"), 3))
                .addReward(XpReward.apprentice())
                .addPrerequisite("roots_run_deep");
    }

    private static Quest createCorruptedGroveQuest() {
        return new Quest("corrupted_grove")
                .setName("The Corrupted Grove")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Darkness has taken root in the sacred groves. Cleanse the corruption.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("The forest speaks to those who listen. Gather its gifts with reverence.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Craft a bow worthy of the ancient elven rangers. Imbue it with moonlight.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Elf")
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.STRING, 32))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 8))
                .addReward(new ItemReward(Items.BOW, 1))
                .addReward(new ItemReward(Items.SPECTRAL_ARROW, 64))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("infinity"), 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("flame"), 1))
                .addReward(XpReward.expert())
                .addPrerequisite("whispers_of_leaves");
    }

    private static Quest createWildernessWardenQuest() {
        return new Quest("wilderness_warden")
                .setName("Wilderness Warden")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Protect the sacred places. Drive back those who would defile nature.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Seek the legendary World Tree's offspring, hidden in the deepest forests.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Under the full moon, perform the ancient ritual of renewal.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Elf")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Achieve mastery over nature itself. Become one with the eternal forest.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Elf")
                .addObjective(new CollectObjective(Items.EMERALD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 16))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addReward(new ItemReward(Items.NETHER_STAR, 2))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 3))
                .addReward(XpReward.master())
                .addPrerequisite("moonlit_ritual");
    }

    private static void registerElfQuestChain(QuestManager manager) {
        QuestChain elfChain = new QuestChain(
                "guardian_of_wilds",
                "Guardian of the Wilds",
                "Walk the path of the ancient elven rangers. Protect the forests, master the bow, and commune with nature.",
                QuestData.QuestBookTier.NOVICE,
                null
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

    private static Quest createWandererQuest() {
        return new Quest("wanderer")
                .setName("The Wanderer")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Humans thrive through adaptability. Begin your journey by mastering the basics of survival.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredRace("Human")
                .addObjective(new CollectObjective(Items.BREAD, 16))
                .addObjective(new CollectObjective(Items.COBBLESTONE, 32))
                .addObjective(new CollectObjective(Items.OAK_LOG, 16))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(ModItems.MYSTIC_STEW, 3))
                .addReward(new ItemReward(ModItems.GOLDEN_APPLE_STRUDEL, 2))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createTasteOfEverythingQuest() {
        return new Quest("taste_of_everything")
                .setName("A Taste of Everything")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("True versatility comes from experiencing all aspects of life. Craft items from diverse categories.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Human")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Explore the diverse biomes of the world. Adaptability requires understanding all environments.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Human")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Humans excel at diplomacy and trade. Build relationships with the world's merchants.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Human")
                .addObjective(new CollectObjective(Items.EMERALD, 32))
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 16))
                .addObjective(new CollectObjective(Items.DIAMOND, 8))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("mending"), 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("looting"), 3))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 4))
                .addReward(XpReward.expert())
                .addPrerequisite("world_traveler");
    }

    private static Quest createRenaissanceScholarQuest() {
        return new Quest("renaissance_scholar")
                .setName("Renaissance Scholar")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Knowledge is power. Master the arts of enchanting, brewing, and crafting.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Human")
                .addObjective(new CollectObjective(Items.ENCHANTING_TABLE, 1))
                .addObjective(new CollectObjective(Items.BREWING_STAND, 1))
                .addObjective(new CollectObjective(Items.ANVIL, 1))
                .addObjective(new CollectObjective(Items.BOOKSHELF, 16))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 16))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("mending"), 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("looting"), 3))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("unbreaking"), 3))
                .addReward(new ItemReward(Items.DIAMOND, 8))
                .addReward(XpReward.expert())
                .addPrerequisite("master_trader");
    }

    private static Quest createBridgeBuilderQuest() {
        return new Quest("bridge_builder")
                .setName("The Bridge Builder")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Humans unite disparate peoples. Gather resources that span all realms.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Human")
                .addObjective(new CollectObjective(Items.NETHERRACK, 64))
                .addObjective(new CollectObjective(Items.ECHO_SHARD, 4))
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Prove yourself against the greatest challenges. Complete tasks from all walks of life.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Human")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Forge alliances with all peoples. Gather tributes from every corner of the realm.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Human")
                .addObjective(new CollectObjective(Items.EMERALD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.GOLD_BLOCK, 8))
                .addObjective(new CollectObjective(Items.IRON_BLOCK, 16))
                .addReward(new ItemReward(Items.DIAMOND_BLOCK, 4))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 2))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("protection"), 4))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("thorns"), 3))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("feather_falling"), 4))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("sharpness"), 5))
                .addReward(XpReward.master())
                .addPrerequisite("heros_journey");
    }

    private static Quest createMasterOfAllTradesQuest() {
        return new Quest("master_of_all_trades")
                .setName("Master of All Trades")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Achieve true mastery. Demonstrate expertise in combat, crafting, and exploration.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Human")
                .addObjective(new KillObjective(ModEntities.WILD_DRAGON, 1))
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Transcend mortality. Become a legend whose name echoes through the ages.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Human")
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

    private static void registerHumanQuestChain(QuestManager manager) {
        QuestChain humanChain = new QuestChain(
                "jack_of_all_trades",
                "Jack of All Trades",
                "The human path is one of infinite possibility. Master all skills, explore all lands, and become legendary.",
                QuestData.QuestBookTier.NOVICE,
                null
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

    private static Quest createGruntQuest() {
        return new Quest("grunt")
                .setName("The Grunt")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Every warrior begins as a grunt. Prove your strength in combat and bring meat for the clan.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setRequiredRace("Orc")
                .addObjective(KillObjective.zombies(10))
                .addObjective(new CollectObjective(Items.COOKED_BEEF, 8))
                .addObjective(new CollectObjective(Items.COOKED_PORKCHOP, 8))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(new ItemReward(ModItems.SAVORY_BEEF_ROAST, 4))
                .addReward(new ItemReward(ModItems.MOLTEN_CHILI, 3))
                .addReward(new ItemReward(Items.CHAINMAIL_CHESTPLATE, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createProveYourStrengthQuest() {
        return new Quest("prove_your_strength")
                .setName("Prove Your Strength")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("The weak fall, the strong survive. Deal devastating blows to your enemies.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Only the greatest hunters can claim the mightiest trophies. Bring down powerful beasts.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setRequiredRace("Orc")
                .addObjective(new KillObjective(EntityType.IRON_GOLEM, 2))
                .addObjective(new KillObjective(EntityType.RAVAGER, 1))
                .addObjective(new CollectObjective(Items.BEEF, 16))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("sharpness"), 3))
                .addReward(new ItemReward(Items.EMERALD, 8))
                .addReward(XpReward.apprentice())
                .addPrerequisite("prove_your_strength");
    }

    private static Quest createRaidLeaderQuest() {
        return new Quest("raid_leader")
                .setName("Raid Leader")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Lead your warriors to victory. Complete dangerous raids and emerge triumphant.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Forge weapons in the fires of battle. Gather the materials of war.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Orc")
                .addObjective(new CollectObjective(Items.IRON_INGOT, 64))
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.OBSIDIAN, 32))
                .addReward(new ItemReward(Items.DIAMOND_AXE, 1))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("smite"), 4))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("fire_aspect"), 2))
                .addReward(XpReward.expert())
                .addPrerequisite("raid_leader");
    }

    private static Quest createBattleScarsQuest() {
        return new Quest("battle_scars")
                .setName("Battle Scars")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Every scar tells a story of survival. Face the deadliest foes and live to tell the tale.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Rise above your peers. Become the champion your clan needs.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("The Nether belongs to the strong. Claim its treasures through might and fury.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Only the mightiest warriors dare challenge the Wither. Prove you are worthy.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Orc")
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("You have conquered all challenges. Claim your place as Warlord, the ultimate warrior.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setRequiredRace("Orc")
                .addObjective(new KillObjective(BoneRealmEntityRegistry.SKELETON_LORD, 1))
                .addObjective(new CollectObjective(Items.NETHER_STAR, 2))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 4))
                .addReward(new ItemReward(Items.NETHERITE_AXE, 1))
                .addReward(new ItemReward(Items.NETHERITE_PICKAXE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 16))
                .addReward(new ItemReward(Items.NETHER_STAR, 2))
                .addReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addReward(XpReward.master())
                .addPrerequisite("challenge_the_wither");
    }

    private static void registerOrcQuestChain(QuestManager manager) {
        QuestChain orcChain = new QuestChain(
                "path_of_warlord",
                "Path of the Warlord",
                "The way of the warrior is written in blood and steel. Prove your strength, conquer your foes, and become Warlord.",
                QuestData.QuestBookTier.NOVICE,
                null
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

    private static Quest createTrialOfFuryQuest() {
        return new Quest("trial_of_fury")
                .setName("Trial of Fury")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Awaken the berserker within. Cut down your enemies and claim the Rage Totem — your first taste of true warrior power.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setMinLevel(10)
                .setRequiredClass("Warrior")
                .addObjective(KillObjective.zombies(15))
                .addObjective(KillObjective.skeletons(10))
                .addObjective(new CollectObjective(Items.IRON_INGOT, 16))
                .addReward(new ItemReward(ModItems.RAGE_TOTEM, 1))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createBattleHardenedQuest() {
        return new Quest("battle_hardened")
                .setName("Battle Hardened")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("A warrior who cannot rally is a warrior who dies alone. Prove yourself in harder combat and earn the Battle Standard.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setMinLevel(25)
                .setRequiredClass("Warrior")
                .addObjective(new KillObjective(EntityType.PILLAGER, 12))
                .addObjective(new KillObjective(EntityType.VINDICATOR, 6))
                .addObjective(new CollectObjective(Items.GOLD_INGOT, 16))
                .addObjective(new CollectObjective(Items.IRON_BLOCK, 4))
                .addReward(new ItemReward(ModItems.BATTLE_STANDARD, 1))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("trial_of_fury");
    }

    private static Quest createWhirlwindMasteryQuest() {
        return new Quest("whirlwind_mastery")
                .setName("Whirlwind Mastery")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Single targets are for lesser fighters. Tear through entire hordes and claim the Whirlwind Axe.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(50)
                .setRequiredClass("Warrior")
                .addObjective(new KillObjective(EntityType.BLAZE, 10))
                .addObjective(new KillObjective(EntityType.WITHER_SKELETON, 8))
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.NETHERITE_SCRAP, 4))
                .addReward(new ItemReward(ModItems.WHIRLWIND_AXE, 1))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.DIAMOND_LEGGINGS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addReward(XpReward.expert())
                .addPrerequisite("battle_hardened");
    }

    private static Quest createIronSkinTrialQuest() {
        return new Quest("iron_skin_trial")
                .setName("Iron Skin Trial")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Survive the unsurvivable. Endure the harshest battles and forge the Iron Talisman from the wreckage.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(75)
                .setRequiredClass("Warrior")
                .addObjective(new KillObjective(EntityType.PIGLIN_BRUTE, 8))
                .addObjective(new CollectObjective(Items.ANCIENT_DEBRIS, 8))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 4))
                .addReward(new ItemReward(ModItems.IRON_TALISMAN, 1))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.NETHERITE_BOOTS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("whirlwind_mastery");
    }

    private static Quest createWarCryQuest() {
        return new Quest("war_cry")
                .setName("War Cry")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("A true berserker does not fight alone — they inspire fear in enemies and courage in allies. Slay the mightiest foes and claim the War Horn.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setMinLevel(100)
                .setRequiredClass("Warrior")
                .addObjective(new KillObjective(ModEntities.WILD_DRAGON, 1))
                .addObjective(new KillObjective(BoneRealmEntityRegistry.SKELETON_LORD, 1))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 4))
                .addReward(new ItemReward(ModItems.WAR_HORN, 1))
                .addReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.NETHERITE_LEGGINGS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(XpReward.master())
                .addPrerequisite("iron_skin_trial");
    }

    private static void registerWarriorQuestChain(QuestManager manager) {
        manager.registerQuest(createTrialOfFuryQuest());
        manager.registerQuest(createBattleHardenedQuest());
        manager.registerQuest(createWhirlwindMasteryQuest());
        manager.registerQuest(createIronSkinTrialQuest());
        manager.registerQuest(createWarCryQuest());

        QuestChain warriorChain = new QuestChain(
                "path_of_the_berserker",
                "Path of the Berserker",
                "Six abilities. Five trials. One berserker. Master rage, endurance, and destruction to become an unstoppable force on the battlefield.",
                QuestData.QuestBookTier.NOVICE,
                null
        )
                .addQuest("trial_of_fury")
                .addQuest("battle_hardened")
                .addQuest("whirlwind_mastery")
                .addQuest("iron_skin_trial")
                .addQuest("war_cry")
                .addChainReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addChainReward(new ItemReward(Items.NETHERITE_AXE, 1))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 10));

        manager.registerQuestChain(warriorChain);
    }

    // ========== MAGE CLASS QUESTS ==========

    private static Quest createArcaneMissilesQuest() {
        return new Quest("arcane_missiles_unlock")
                .setName("First Spark")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Every archmage begins with a single spark. Gather the components to channel your first arcane ability.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setMinLevel(10)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.PAPER, 16))
                .addObjective(new CollectObjective(Items.GLOWSTONE_DUST, 16))
                .addObjective(new CollectObjective(Items.REDSTONE, 32))
                .addReward(new ItemReward(ModItems.ARCANE_ORB, 1))
                .addReward(new ItemReward(ModItems.HEAL_SCROLL, 4))
                .addReward(new ItemReward(ModItems.ABSORPTION_SCROLL, 3))
                .addReward(new ItemReward(ModItems.MANA_SHIELD_SCROLL, 3))
                .addReward(XpReward.novice());
    }

    private static Quest createTemporalMasteryQuest() {
        return new Quest("temporal_mastery")
                .setName("Temporal Mastery")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Time bends to the will of a true mage. Gather components from the most volatile corners of the world to forge a Temporal Crystal.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setMinLevel(25)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.BLAZE_POWDER, 16))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 8))
                .addObjective(new CollectObjective(Items.GHAST_TEAR, 4))
                .addReward(new ItemReward(ModItems.TEMPORAL_CRYSTAL, 1))
                .addReward(new ItemReward(ModItems.FIREBALL_SCROLL, 4))
                .addReward(new ItemReward(ModItems.TELEPORT_SCROLL, 3))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 16))
                .addReward(XpReward.apprentice())
                .addPrerequisite("arcane_missiles_unlock");
    }

    private static Quest createManaBurstQuest() {
        return new Quest("mana_burst_unlock")
                .setName("Surge of Power")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("The arcane arts demand sacrifice. Channel rare materials into a Mana Catalyst capable of leveling everything around you.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(50)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.AMETHYST_SHARD, 32))
                .addObjective(new CollectObjective(Items.ENDER_EYE, 8))
                .addObjective(new CollectObjective(Items.LAPIS_BLOCK, 8))
                .addObjective(new CollectObjective(Items.ECHO_SHARD, 4))
                .addReward(new ItemReward(ModItems.MANA_CATALYST, 1))
                .addReward(new ItemReward(ModItems.LIGHTNING_SCROLL, 4))
                .addReward(new ItemReward(ModItems.FROST_NOVA_SCROLL, 4))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addReward(XpReward.expert())
                .addPrerequisite("temporal_mastery");
    }

    private static Quest createArcaneBarrierQuest() {
        return new Quest("arcane_barrier_unlock")
                .setName("Archmage's Aegis")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("You have conquered offense and control. Now forge the ultimate defense — an Arcane Barrier that reflects the world's attacks back upon itself.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(75)
                .setRequiredClass("Mage")
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 4))
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 4))
                .addObjective(new CollectObjective(Items.EXPERIENCE_BOTTLE, 64))
                .addReward(new ItemReward(ModItems.BARRIER_CHARM, 1))
                .addReward(new ItemReward(ModItems.MANA_SHIELD_SCROLL, 5))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("mana_burst_unlock");
    }

    private static Quest createArchmageTrial() {
        return new Quest("archmage_trial")
                .setName("The Archmage's Trial")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Theory without application is nothing. Channel every ability you have mastered and prove your power against the mightiest of foes. This is what separates an archmage from a scholar.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setMinLevel(100)
                .setRequiredClass("Mage")
                .addObjective(new KillObjective(BoneRealmEntityRegistry.SKELETON_LORD, 1))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 4))
                .addObjective(new CollectObjective(Items.DIAMOND_BLOCK, 2))
                .addReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 32))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(XpReward.master())
                .addPrerequisite("arcane_barrier_unlock");
    }

    private static void registerMageQuestChain(QuestManager manager) {
        manager.registerQuest(createArcaneMissilesQuest());
        manager.registerQuest(createTemporalMasteryQuest());
        manager.registerQuest(createManaBurstQuest());
        manager.registerQuest(createArcaneBarrierQuest());
        manager.registerQuest(createArchmageTrial());

        QuestChain mageChain = new QuestChain(
                "path_of_the_archmage",
                "Path of the Archmage",
                "Four abilities. Five trials. Master missiles, time, destruction, and defense — then prove it all in combat to become a true Archmage.",
                QuestData.QuestBookTier.NOVICE,
                null
        )
                .addQuest("arcane_missiles_unlock")
                .addQuest("temporal_mastery")
                .addQuest("mana_burst_unlock")
                .addQuest("arcane_barrier_unlock")
                .addQuest("archmage_trial")
                .addChainReward(new EnchantedBookReward(Identifier.ofVanilla("mending"), 1))
                .addChainReward(new EnchantedBookReward(Identifier.ofVanilla("unbreaking"), 3))
                .addChainReward(new EnchantedBookReward(Identifier.ofVanilla("power"), 5))
                .addChainReward(new EnchantedBookReward(Identifier.ofVanilla("looting"), 3))
                .addChainReward(new EnchantedBookReward(Identifier.ofVanilla("silk_touch"), 1))
                .addChainReward(new ItemReward(Items.EXPERIENCE_BOTTLE, 64))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 10));

        manager.registerQuestChain(mageChain);
    }

    // ========== ROGUE CLASS QUESTS ==========

    private static Quest createShadowsCallingQuest() {
        return new Quest("shadows_calling")
                .setName("Shadow's Calling")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("The shadows have chosen you. Hunt creatures of darkness and claim your Rogue Ability Tome — the gateway to the energy system and three deadly stealth abilities.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .setMinLevel(10)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(EntityType.SPIDER, 15))
                .addObjective(new KillObjective(EntityType.CAVE_SPIDER, 8))
                .addObjective(new CollectObjective(Items.GUNPOWDER, 8))
                .addReward(new ItemReward(ModItems.ROGUE_ABILITY_TOME, 1))
                .addReward(new ItemReward(Items.DIAMOND_SWORD, 1))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createBlinkStrikeQuest() {
        return new Quest("blink_strike_unlock")
                .setName("Step Between Shadows")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("A rogue who cannot close distance is already dead. Hunt creatures of the night and forge the Void Blade.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .setMinLevel(25)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(EntityType.PHANTOM, 6))
                .addObjective(new CollectObjective(Items.PHANTOM_MEMBRANE, 4))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 8))
                .addReward(new ItemReward(ModItems.VOID_BLADE, 1))
                .addReward(new ItemReward(Items.DIAMOND_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.apprentice())
                .addPrerequisite("shadows_calling");
    }

    private static Quest createPoisonStrikeQuest() {
        return new Quest("poison_strike_unlock")
                .setName("Toxin and Shadow")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("Every assassin needs poison. Gather rare toxins from the deepest places and brew the Poison Vial.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(50)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(EntityType.ENDERMAN, 8))
                .addObjective(new CollectObjective(Items.ECHO_SHARD, 4))
                .addObjective(new CollectObjective(Items.SPIDER_EYE, 16))
                .addObjective(new CollectObjective(Items.FERMENTED_SPIDER_EYE, 8))
                .addReward(new ItemReward(ModItems.POISON_VIAL, 1))
                .addReward(new ItemReward(Items.DIAMOND_CHESTPLATE, 1))
                .addReward(new ItemReward(Items.DIAMOND_LEGGINGS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addReward(XpReward.expert())
                .addPrerequisite("blink_strike_unlock");
    }

    private static Quest createAssassinateQuest() {
        return new Quest("assassinate_unlock")
                .setName("The Perfect Kill")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("One strike from behind. One kill. Gather the components to craft the Assassin's Mark and perfect the art of the kill.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .setMinLevel(75)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(EntityType.PILLAGER, 15))
                .addObjective(new KillObjective(EntityType.EVOKER, 3))
                .addObjective(new CollectObjective(Items.DIAMOND, 16))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 2))
                .addReward(new ItemReward(ModItems.ASSASSINS_MARK, 1))
                .addReward(new ItemReward(Items.NETHERITE_HELMET, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addReward(XpReward.expert())
                .addPrerequisite("poison_strike_unlock");
    }

    private static Quest createVanishQuest() {
        return new Quest("vanish_unlock")
                .setName("Into the Dark")
                .setCategory(Quest.QuestCategory.CLASS)
                .setDescription("The greatest rogues are never seen. Prove your mastery by eliminating the most dangerous targets and claim the Vanish Cloak — your ultimate escape.")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                .setMinLevel(100)
                .setRequiredClass("Rogue")
                .addObjective(new KillObjective(ModEntities.WILD_DRAGON, 1))
                .addObjective(new CollectObjective(Items.NETHERITE_BLOCK, 2))
                .addObjective(new CollectObjective(Items.ENCHANTED_GOLDEN_APPLE, 4))
                .addReward(new ItemReward(ModItems.VANISH_CLOAK, 1))
                .addReward(new ItemReward(Items.NETHERITE_LEGGINGS, 1))
                .addReward(new ItemReward(Items.NETHERITE_BOOTS, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 8))
                .addReward(XpReward.master())
                .addPrerequisite("assassinate_unlock");
    }

    private static void registerRogueQuestChain(QuestManager manager) {
        manager.registerQuest(createShadowsCallingQuest());
        manager.registerQuest(createBlinkStrikeQuest());
        manager.registerQuest(createPoisonStrikeQuest());
        manager.registerQuest(createAssassinateQuest());
        manager.registerQuest(createVanishQuest());

        QuestChain rogueChain = new QuestChain(
                "path_of_shadows",
                "Path of Shadows",
                "Five abilities. Five trials. Master stealth, poison, teleportation, assassination, and vanishing to become the ultimate shadow.",
                QuestData.QuestBookTier.NOVICE,
                null
        )
                .addQuest("shadows_calling")
                .addQuest("blink_strike_unlock")
                .addQuest("poison_strike_unlock")
                .addQuest("assassinate_unlock")
                .addQuest("vanish_unlock")
                .addChainReward(new ItemReward(Items.NETHERITE_SWORD, 1))
                .addChainReward(new ItemReward(Items.NETHERITE_CHESTPLATE, 1))
                .addChainReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 10));

        manager.registerQuestChain(rogueChain);
    }

    // ========== INNKEEPER GARRICK QUEST ==========

    /**
     * Garrick's Special Brew - Early game fetch quest from Innkeeper Garrick
     * Rewards: Emeralds and custom food item
     * A simple gathering quest to help the barkeep create his special brew.
     * Uses common Overworld materials for early game accessibility.
     */
    private static Quest createGarricksSpecialBrewQuest() {
        return new Quest("garricks_special_brew")
                .setName("Garrick's Welcome")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Innkeeper Garrick welcomes all newcomers with a simple task. " +
                        "A perfect introduction to the quest system!")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.WHEAT, 3))
                .addObjective(new CollectObjective(Items.APPLE, 1))
                .addReward(new ItemReward(ModItems.HONEY_BREAD, 3))
                .addReward(new ItemReward(ModItems.CANDIED_APPLE, 2))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(XpReward.novice());
    }

    // ========== ADVENTURER'S PATH CHAIN QUESTS ==========

    private static Quest createTheBeginningQuest() {
        return new Quest("the_beginning")
                .setName("The Beginning")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Gather oak wood for basic tools.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.OAK_LOG, 5))
                .addReward(new ItemReward(Items.WOODEN_AXE, 1))
                .addReward(new ItemReward(Items.STICK, 4))
                .addReward(XpReward.novice());
    }

    private static Quest createEquipYourselfQuest() {
        return new Quest("equip_yourself")
                .setName("Equip Yourself")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Gather materials for better tools and weapons.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 20))
                .addObjective(new CollectObjective(Items.IRON_INGOT, 3))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.IRON_PICKAXE, 1))
                .addReward(new ItemReward(Items.IRON_AXE, 1))
                .addReward(XpReward.novice())
                .addPrerequisite("garricks_special_brew");
    }

    private static Quest createReadyForAdventureQuest() {
        return new Quest("ready_for_adventure")
                .setName("Ready for Adventure")
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Stock up on supplies. Your real adventure begins now!")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.BREAD, 8))
                .addObjective(new CollectObjective(Items.COOKED_BEEF, 4))
                .addReward(new ItemReward(Items.IRON_BOOTS, 1))
                .addReward(new ItemReward(Items.IRON_HELMET, 1))
                .addReward(new ItemReward(ModItems.BEEF_STEW, 4))
                .addReward(new ItemReward(Items.GOLDEN_APPLE, 2))
                .addReward(XpReward.novice())
                .addPrerequisite("equip_yourself");
    }

    // ========== VILLAGE DEVELOPMENT CHAIN QUESTS ==========

    private static Quest createVillageFounderQuest() {
        return new Quest("village_founder")
                .setName("Village Founder")
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Complete the transformation into a thriving settlement. Completing this quest unlocks the Expert Quest Book!")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
                .setDescription("Prove your mastery by gathering the rarest materials.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.MAIN)
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
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Venture into the depths to gather precious resources.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new MultiItemCollectObjective("Iron Ore", 20, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE))
                .addObjective(new MultiItemCollectObjective("Gold Ore", 8, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE))
                .addObjective(new MultiItemCollectObjective("Diamond Ore", 3, Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new EnchantedBookReward(Identifier.ofVanilla("fortune"), 3))
                .addReward(XpReward.expert());
    }

    /**
     * IDENTITY CRISIS - Level 25+ Expert Quest
     * Rewards: Potion of Racial Rebirth (free race reset)
     * <p>
     * A quest about questioning your heritage and seeking a new path.
     * Requires collecting materials from all 4 racial homelands.
     * Note: Level requirement is handled by LevelRequirements.meetsLevelRequirement()
     */
    private static Quest createIdentityCrisisQuest() {
        return new Quest("identity_crisis")
                .setName("Identity Crisis")
                .setCategory(Quest.QuestCategory.SIDE)
                .setDescription("You've begun to question your heritage. Perhaps there's another path for you? " +
                        "Collect materials from all racial homelands to undergo the Ritual of Rebirth.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                // Dwarf homeland material
                .addObjective(new CollectObjective(Items.DEEPSLATE, 32))
                .addObjective(new CollectObjective(Items.IRON_INGOT, 16))
                // Elf homeland material
                .addObjective(new CollectObjective(Items.OAK_LOG, 32))
                .addObjective(new CollectObjective(Items.SWEET_BERRIES, 16))
                // Human homeland material
                .addObjective(new CollectObjective(Items.WHEAT, 32))
                .addObjective(new CollectObjective(Items.BREAD, 16))
                // Orc homeland material
                .addObjective(new CollectObjective(Items.PORKCHOP, 16))
                .addObjective(new CollectObjective(Items.BONE, 32))
                // Ritual materials
                .addObjective(new CollectObjective(Items.AMETHYST_SHARD, 8))
                .addObjective(new CollectObjective(Items.ECHO_SHARD, 2))
                // Rewards
                .addReward(new ItemReward(ModItems.POTION_OF_RACIAL_REBIRTH, 1))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 1))
                .addReward(XpReward.expert());
    }

    /**
     * PATH OF DESTINY - Level 30+ Expert Quest
     * Rewards: Potion of Class Rebirth (free class reset)
     * <p>
     * A quest about mastering your class and considering a new calling.
     * Requires demonstrating mastery across multiple class abilities.
     * Note: Level requirement is handled by LevelRequirements.meetsLevelRequirement()
     */
    private static Quest createPathOfDestinyQuest() {
        return new Quest("path_of_destiny")
                .setName("Path of Destiny")
                .setCategory(Quest.QuestCategory.SIDE)
                .setDescription("You've mastered your current class, but feel the call of a different destiny. " +
                        "Prove your versatility to earn the right to choose a new path.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                // Combat mastery
                .addObjective(KillObjective.zombies(25))
                .addObjective(KillObjective.skeletons(20))
                .addObjective(new KillObjective(EntityType.ENDERMAN, 5))
                // Resource gathering (versatility)
                .addObjective(new CollectObjective(Items.DIAMOND, 8))
                .addObjective(new CollectObjective(Items.EMERALD, 4))
                .addObjective(new CollectObjective(Items.NETHER_STAR, 1))
                // Magic materials
                .addObjective(new CollectObjective(Items.BLAZE_ROD, 8))
                .addObjective(new CollectObjective(Items.ENDER_PEARL, 12))
                // Ritual components
                .addObjective(new CollectObjective(Items.EXPERIENCE_BOTTLE, 10))
                // Rewards
                .addReward(new ItemReward(ModItems.POTION_OF_CLASS_REBIRTH, 1))
                .addReward(new ItemReward(Items.TOTEM_OF_UNDYING, 1))
                .addReward(XpReward.expert());
    }

    /**
     * REBIRTH RITUAL - Level 40+ Master Quest
     * Rewards: Potion of Total Rebirth (free race + class reset)
     * <p>
     * The ultimate quest - a complete character reset.
     * Requires completion of both Identity Crisis and Path of Destiny.
     * Note: Level requirement is handled by LevelRequirements.meetsLevelRequirement()
     */
    private static Quest createRebirthRitualQuest() {
        return new Quest("rebirth_ritual")
                .setName("The Rebirth Ritual")
                .setCategory(Quest.QuestCategory.SIDE)
                .setDescription("You seek to completely remake yourself - race, class, everything. " +
                        "This ancient ritual requires the ultimate sacrifice and the rarest of materials. " +
                        "Are you certain this is your path?")
                .setDifficulty(Quest.QuestDifficulty.MASTER)
                // Must complete both previous reset quests first
                .addPrerequisite("identity_crisis")
                .addPrerequisite("path_of_destiny")
                // DAGMod boss challenges
                .addObjective(new KillObjective(ModEntities.WILD_DRAGON, 1))
                .addObjective(new KillObjective(BoneRealmEntityRegistry.SKELETON_LORD, 1))
                // Rare ritual components
                .addObjective(new CollectObjective(Items.ECHO_SHARD, 8))
                .addObjective(new CollectObjective(Items.NETHERITE_INGOT, 4))
                .addObjective(new CollectObjective(Items.TOTEM_OF_UNDYING, 2))
                // Magical essences
                .addObjective(new CollectObjective(Items.ENCHANTED_GOLDEN_APPLE, 5))
                .addObjective(new CollectObjective(Items.EXPERIENCE_BOTTLE, 64))
                // Rewards
                .addReward(new ItemReward(ModItems.POTION_OF_TOTAL_REBIRTH, 1))
                .addReward(new ItemReward(Items.NETHERITE_INGOT, 8))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 3))
                .addReward(XpReward.master());
    }

    // ========== JOB BOARD QUESTS ==========

    private static Quest createGatherCobblestoneJob() {
        return new Quest("gather_cobblestone")
                .setName("Gather Cobblestone")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The town needs building materials. Gather cobblestone.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 64))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(XpReward.novice());
    }

    private static Quest createHuntZombiesJob() {
        return new Quest("hunt_zombies")
                .setName("Hunt Zombies")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Clear out the undead threats near town.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(KillObjective.zombies(10))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(XpReward.novice());
    }

    private static Quest createCollectWheatJob() {
        return new Quest("collect_wheat")
                .setName("Collect Wheat")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The baker needs wheat for bread.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.WHEAT, 32))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(new ItemReward(Items.BREAD, 8))
                .addReward(XpReward.novice());
    }

    private static Quest createRumoursOfTheBoneKingQuest() {
        return new Quest("rumours_of_the_bone_king")
                .setName("Rumours of the Bone King")
                .setCategory(Quest.QuestCategory.SIDE)
                .setDescription("Travellers speak of ancient bone-filled ruins buried deep in the earth. " +
                    "The skeletons that roam the surface are but echoes of something far worse below. " +
                    "Slay enough of them to earn a chart that marks the entrance to the nearest dungeon.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(KillObjective.skeletons(20))
                .addObjective(new CollectObjective(Items.BONE, 10))
                .addReward(new ItemReward(ModItems.BONE_DUNGEON_LOCATOR, 1))
                .addReward(new ItemReward(Items.IRON_SWORD, 1))
                .addReward(new ItemReward(Items.TORCH, 32))
                .addReward(XpReward.novice());
    }

    private static Quest createRedDragonFuryQuest() {
        return new Quest("red_dragon_fury")
                .setName("The Red Dragon's Fury")
                .setCategory(Quest.QuestCategory.SIDE)
                .setDescription("A village on the outskirts has been terrorised by a Red Dragon — buildings burned, " +
                    "livestock taken, and survivors scattered. The beast must be slain before it strikes again. " +
                    "Hunt it down, claim its heart and scales, then forge the Dragon Key to unlock the portal " +
                    "at the Hall of Champions and enter the Dragon Realm.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new KillObjective(ModEntities.RED_DRAGON, 1))
                .addObjective(new CollectObjective(ModItems.DRAGON_HEART, 1))
                .addObjective(new CollectObjective(ModItems.DRAGON_SCALE, 3))
                .addObjective(new CollectObjective(ModItems.DRAGON_BONE, 2))
                .addReward(new ItemReward(DragonRealmRegistry.DRAGON_KEY, 1))
                .addReward(new UnlockReward(Identifier.of("dagmod", "dragon_key"), "Dragon Key"))
                .addReward(new ItemReward(Items.ENCHANTED_GOLDEN_APPLE, 2))
                .addReward(XpReward.expert());
    }

    private static Quest createMineIronJob() {
        return new Quest("mine_iron")
                .setName("Mine Raw Iron")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The blacksmith needs raw iron.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.RAW_IRON, 16))
                .addReward(new ItemReward(Items.EMERALD, 4))
                .addReward(XpReward.apprentice());
    }



}