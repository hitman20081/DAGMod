package com.github.hitman20081.dagmod.quest.registry;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import com.github.hitman20081.dagmod.quest.objectives.TagCollectObjective;
import com.github.hitman20081.dagmod.quest.rewards.CoinReward;
import com.github.hitman20081.dagmod.quest.rewards.ItemReward;
import com.github.hitman20081.dagmod.quest.rewards.XpReward;
import com.github.hitman20081.dagmod.economy.CoinTier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.List;

public class DailyQuestRegistry {

    // Full pool of quest IDs available for daily rotation.
    // DailyQuestManager picks DAILY_POOL_SIZE of these each day.
    public static final List<String> ALL_IDS = List.of(
            "daily_chop_wood", "daily_mine_stone", "daily_mine_iron",
            "daily_hunt_zombies", "daily_catch_fish", "daily_gather_leather",
            "daily_gather_wool", "daily_collect_wheat", "daily_hunt_skeletons",
            "daily_hunt_spiders", "daily_mine_coal", "daily_mine_gold",
            "daily_hunt_creepers", "daily_gather_sand", "daily_hunt_blazes",
            "daily_mine_diamonds", "daily_collect_obsidian", "daily_hunt_endermen",
            "daily_gather_eggs", "daily_collect_clay"
    );

    public static void registerDailyQuests(QuestManager manager) {

        // ========== NOVICE DAILIES ==========

        manager.registerQuest(new Quest("daily_chop_wood")
                .setName("Daily: Chop Wood")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily timber contract. Any wood type accepted.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new TagCollectObjective(ItemTags.LOGS, 24, "any logs"))
                .addReward(new CoinReward(CoinTier.COPPER, 35))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_mine_stone")
                .setName("Daily: Mine Stone")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The builders are running low. Bring cobblestone.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COBBLESTONE, 64))
                .addReward(new CoinReward(CoinTier.COPPER, 25))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_catch_fish")
                .setName("Daily: Catch Fish")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Fresh fish for tonight's dinner at the inn.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COD, 8))
                .addReward(new CoinReward(CoinTier.COPPER, 35))
                .addReward(new ItemReward(Items.BREAD, 4))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_gather_wool")
                .setName("Daily: Gather Wool")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The weaver's daily order. Any colour.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new TagCollectObjective(ItemTags.WOOL, 12, "any wool"))
                .addReward(new CoinReward(CoinTier.COPPER, 35))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_collect_wheat")
                .setName("Daily: Harvest Wheat")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Bread won't bake itself. Bring in the daily harvest.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.WHEAT, 24))
                .addReward(new CoinReward(CoinTier.COPPER, 25))
                .addReward(new ItemReward(Items.BREAD, 8))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_gather_sand")
                .setName("Daily: Gather Sand")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The glassmaker needs sand every morning.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.SAND, 32))
                .addReward(new CoinReward(CoinTier.COPPER, 25))
                .addReward(new ItemReward(Items.GLASS, 8))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_gather_eggs")
                .setName("Daily: Collect Eggs")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The baker needs fresh eggs for today's cakes.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.EGG, 16))
                .addReward(new CoinReward(CoinTier.COPPER, 25))
                .addReward(new ItemReward(Items.CAKE, 1))
                .addReward(XpReward.novice()));

        manager.registerQuest(new Quest("daily_collect_clay")
                .setName("Daily: Collect Clay")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The potter needs clay from the riverbed.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.CLAY_BALL, 24))
                .addReward(new CoinReward(CoinTier.COPPER, 25))
                .addReward(new ItemReward(Items.FLOWER_POT, 4))
                .addReward(XpReward.novice()));

        // ========== APPRENTICE DAILIES ==========

        manager.registerQuest(new Quest("daily_hunt_zombies")
                .setName("Daily: Hunt Zombies")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily bounty: clear undead threats from the roads.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.zombies(8))
                .addReward(new CoinReward(CoinTier.COPPER, 60))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_hunt_skeletons")
                .setName("Daily: Skeleton Patrol")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily patrol contract: eliminate skeleton archers.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.skeletons(10))
                .addReward(new CoinReward(CoinTier.COPPER, 60))
                .addReward(new ItemReward(Items.ARROW, 12))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_hunt_spiders")
                .setName("Daily: Spider Cull")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily extermination contract: spiders near the storehouse.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.spiders(8))
                .addReward(new CoinReward(CoinTier.COPPER, 50))
                .addReward(new ItemReward(Items.STRING, 8))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_mine_iron")
                .setName("Daily: Iron Run")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily supply order for the smithy. Raw iron needed.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.RAW_IRON, 12))
                .addReward(new CoinReward(CoinTier.COPPER, 60))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_mine_coal")
                .setName("Daily: Coal Supply")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Furnaces run dry fast. Bring the daily coal supply.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.COAL, 24))
                .addReward(new CoinReward(CoinTier.COPPER, 50))
                .addReward(new ItemReward(Items.TORCH, 12))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_gather_leather")
                .setName("Daily: Leather Run")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily order for the leatherworker.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.LEATHER, 12))
                .addReward(new CoinReward(CoinTier.COPPER, 50))
                .addReward(new ItemReward(Items.COOKED_BEEF, 4))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_hunt_creepers")
                .setName("Daily: Creeper Patrol")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Creepers are destroying the farmland again.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(KillObjective.fromIdentifier("minecraft:creeper", 6))
                .addReward(new CoinReward(CoinTier.COPPER, 60))
                .addReward(new ItemReward(Items.GUNPOWDER, 4))
                .addReward(XpReward.apprentice()));

        manager.registerQuest(new Quest("daily_mine_gold")
                .setName("Daily: Gold Rush")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("The jeweler's standing daily order for raw gold.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.RAW_GOLD, 6))
                .addReward(new CoinReward(CoinTier.COPPER, 70))
                .addReward(XpReward.apprentice()));

        // ========== EXPERT DAILIES ==========

        manager.registerQuest(new Quest("daily_hunt_blazes")
                .setName("Daily: Blaze Culling")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Daily Nether contract. The alchemist needs blaze rods.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(KillObjective.fromIdentifier("minecraft:blaze", 4))
                .addReward(new CoinReward(CoinTier.SILVER, 1))
                .addReward(new ItemReward(Items.BLAZE_ROD, 2))
                .addReward(XpReward.expert()));

        manager.registerQuest(new Quest("daily_mine_diamonds")
                .setName("Daily: Diamond Haul")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Expert mining contract. Diamonds from the deep.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.DIAMOND, 2))
                .addReward(new CoinReward(CoinTier.SILVER, 1))
                .addReward(new CoinReward(CoinTier.COPPER, 20))
                .addReward(XpReward.expert()));

        manager.registerQuest(new Quest("daily_collect_obsidian")
                .setName("Daily: Obsidian Run")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Expert daily: obsidian for the vault expansion.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.OBSIDIAN, 12))
                .addReward(new CoinReward(CoinTier.SILVER, 1))
                .addReward(XpReward.expert()));

        manager.registerQuest(new Quest("daily_hunt_endermen")
                .setName("Daily: Enderman Cull")
                .setCategory(Quest.QuestCategory.DAILY)
                .setDescription("Expert bounty: endermen are stealing from the treasury.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(KillObjective.fromIdentifier("minecraft:enderman", 4))
                .addReward(new CoinReward(CoinTier.SILVER, 1))
                .addReward(new ItemReward(Items.ENDER_PEARL, 2))
                .addReward(XpReward.expert()));
    }
}
