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
        // ========== STANDALONE QUESTS ==========
        manager.registerQuest(createGatherWoodQuest());
        manager.registerQuest(createFarmersHelperQuest());
        manager.registerQuest(createMiningExpeditionQuest());
        manager.registerQuest(createBreadMakerQuest());

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
    }

    // ========== STANDALONE NOVICE QUESTS ==========

    private static Quest createGatherWoodQuest() {
        return new Quest("gather_wood")
                .setName("Wood Gatherer")
                .setDescription("The village needs wood for construction. Gather some oak logs.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.OAK_LOG, 10))
                .addReward(new ItemReward(Items.BREAD, 3))
                .addReward(XpReward.novice());
    }

    private static Quest createFarmersHelperQuest() {
        return new Quest("farmers_helper")
                .setName("Farmer's Helper")
                .setDescription("The local farmer needs wheat to make bread for the village.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.WHEAT, 15))
                .addReward(new ItemReward(Items.BREAD, 5))
                .addReward(new ItemReward(Items.WHEAT_SEEDS, 10))
                .addReward(XpReward.novice());
    }

    private static Quest createMiningExpeditionQuest() {
        return new Quest("mining_expedition")
                .setName("Mining Expedition")
                .setDescription("The village blacksmith needs cobblestone for repairs.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 32))
                .addReward(new ItemReward(Items.STONE_PICKAXE, 1))
                .addReward(XpReward.novice());
    }

    private static Quest createBreadMakerQuest() {
        return new Quest("bread_maker")
                .setName("The Bread Maker")
                .setDescription("The baker needs ingredients to feed the growing village population.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.WHEAT, 32))
                .addObjective(new CollectObjective(Items.EGG, 6))
                .addReward(new ItemReward(Items.BREAD, 12))
                .addReward(new ItemReward(Items.CAKE, 2))
                .addReward(XpReward.apprentice());
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