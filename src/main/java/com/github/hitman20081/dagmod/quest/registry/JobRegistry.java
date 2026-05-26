package com.github.hitman20081.dagmod.quest.registry;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import com.github.hitman20081.dagmod.quest.objectives.MultiItemCollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.TagCollectObjective;
import com.github.hitman20081.dagmod.quest.rewards.EnchantedBookReward;
import com.github.hitman20081.dagmod.quest.rewards.ItemReward;
import com.github.hitman20081.dagmod.quest.rewards.XpReward;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;

public class JobRegistry {

    public static void registerJobs(QuestManager manager) {
        // ========== NOVICE JOBS ==========
        manager.registerQuest(createGatherCobblestoneJob());
        manager.registerQuest(createHuntZombiesJob());
        manager.registerQuest(createCollectWheatJob());
        manager.registerQuest(createChopLogsJob());
        manager.registerQuest(createCatchFishJob());
        manager.registerQuest(createHuntSpidersJob());
        manager.registerQuest(createGatherWoolJob());
        manager.registerQuest(createCollectSandJob());
        manager.registerQuest(createSkeletonPatrolJob());

        // ========== APPRENTICE JOBS ==========
        manager.registerQuest(createMineIronJob());
        manager.registerQuest(createMineCoalJob());
        manager.registerQuest(createHuntCreepersJob());
        manager.registerQuest(createCollectLeatherJob());
        manager.registerQuest(createMineGoldJob());
        manager.registerQuest(createGatherPumpkinsJob());

        // ========== EXPERT JOBS ==========
        manager.registerQuest(createDeepMinerJob());
        manager.registerQuest(createHuntEndermanJob());
        manager.registerQuest(createMineDiamondsJob());
        manager.registerQuest(createHuntBlazesJob());
        manager.registerQuest(createCollectObsidianJob());
    }

    // ========== NOVICE JOBS ==========

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

    private static Quest createChopLogsJob() {
        return new Quest("chop_logs")
                .setName("Chop Wood")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The carpenter needs timber. Any wood type will do.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new TagCollectObjective(ItemTags.LOGS, 32, "any logs"))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(new ItemReward(Items.STICK, 8))
                .addReward(XpReward.novice());
    }

    private static Quest createCatchFishJob() {
        return new Quest("catch_fish")
                .setName("Catch Fish")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The cook needs fresh fish for tonight's feast.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.COD, 10))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(new ItemReward(Items.BREAD, 4))
                .addReward(XpReward.novice());
    }

    private static Quest createHuntSpidersJob() {
        return new Quest("hunt_spiders")
                .setName("Hunt Spiders")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Spiders have been raiding the storehouse. Clear them out.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(KillObjective.spiders(10))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(new ItemReward(Items.STRING, 8))
                .addReward(XpReward.novice());
    }

    private static Quest createGatherWoolJob() {
        return new Quest("gather_wool")
                .setName("Gather Wool")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The weaver needs wool. Any colour is welcome.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new TagCollectObjective(ItemTags.WOOL, 16, "any wool"))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(new ItemReward(Items.BREAD, 4))
                .addReward(XpReward.novice());
    }

    private static Quest createCollectSandJob() {
        return new Quest("collect_sand")
                .setName("Collect Sand")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The glassmaker needs sand from the riverbank or desert.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(new CollectObjective(Items.SAND, 32))
                .addReward(new ItemReward(Items.EMERALD, 2))
                .addReward(new ItemReward(Items.GLASS, 8))
                .addReward(XpReward.novice());
    }

    private static Quest createSkeletonPatrolJob() {
        return new Quest("skeleton_patrol")
                .setName("Skeleton Patrol")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Skeletons have been spotted near the road at night. Drive them back.")
                .setDifficulty(Quest.QuestDifficulty.NOVICE)
                .addObjective(KillObjective.skeletons(15))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(new ItemReward(Items.ARROW, 16))
                .addReward(XpReward.novice());
    }

    // ========== APPRENTICE JOBS ==========

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

    private static Quest createMineCoalJob() {
        return new Quest("mine_coal")
                .setName("Mine Coal")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The torchmaker is running low on coal. Mine a good supply.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.COAL, 32))
                .addReward(new ItemReward(Items.EMERALD, 4))
                .addReward(new ItemReward(Items.TORCH, 16))
                .addReward(XpReward.apprentice());
    }

    private static Quest createHuntCreepersJob() {
        return new Quest("hunt_creepers")
                .setName("Hunt Creepers")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Creepers have been destroying the farmland. Eliminate them.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new KillObjective(EntityType.CREEPER, 8))
                .addReward(new ItemReward(Items.EMERALD, 5))
                .addReward(new ItemReward(Items.GUNPOWDER, 4))
                .addReward(XpReward.apprentice());
    }

    private static Quest createCollectLeatherJob() {
        return new Quest("collect_leather")
                .setName("Collect Leather")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The leatherworker needs hides from cattle and horses.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.LEATHER, 16))
                .addReward(new ItemReward(Items.EMERALD, 4))
                .addReward(new ItemReward(Items.COOKED_BEEF, 4))
                .addReward(XpReward.apprentice());
    }

    private static Quest createMineGoldJob() {
        return new Quest("mine_gold")
                .setName("Mine Raw Gold")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The jeweler is paying well for raw gold ore.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.RAW_GOLD, 8))
                .addReward(new ItemReward(Items.EMERALD, 5))
                .addReward(XpReward.apprentice());
    }

    private static Quest createGatherPumpkinsJob() {
        return new Quest("gather_pumpkins")
                .setName("Gather Pumpkins")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The festival is coming. The baker needs pumpkins for pies.")
                .setDifficulty(Quest.QuestDifficulty.APPRENTICE)
                .addObjective(new CollectObjective(Items.PUMPKIN, 16))
                .addReward(new ItemReward(Items.EMERALD, 3))
                .addReward(new ItemReward(Items.PUMPKIN_PIE, 8))
                .addReward(XpReward.apprentice());
    }

    // ========== EXPERT JOBS ==========

    private static Quest createDeepMinerJob() {
        return new Quest("deep_miner")
                .setName("Deep Miner")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Venture into the depths to gather precious resources.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new MultiItemCollectObjective("Iron Ore", 20, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE))
                .addObjective(new MultiItemCollectObjective("Gold Ore", 8, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE))
                .addObjective(new MultiItemCollectObjective("Diamond Ore", 3, Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(new EnchantedBookReward(Identifier.withDefaultNamespace("fortune"), 3))
                .addReward(XpReward.expert());
    }

    private static Quest createHuntEndermanJob() {
        return new Quest("hunt_endermen")
                .setName("Hunt Endermen")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("Endermen have been stealing blocks from the keep. Drive them off.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new KillObjective(EntityType.ENDERMAN, 5))
                .addReward(new ItemReward(Items.EMERALD, 8))
                .addReward(new ItemReward(Items.ENDER_PEARL, 2))
                .addReward(XpReward.expert());
    }

    private static Quest createMineDiamondsJob() {
        return new Quest("mine_diamonds")
                .setName("Mine Diamonds")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("A noble is commissioning a gem-encrusted blade. Diamonds needed urgently.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.DIAMOND, 4))
                .addReward(new ItemReward(Items.EMERALD, 10))
                .addReward(XpReward.expert());
    }

    private static Quest createHuntBlazesJob() {
        return new Quest("hunt_blazes")
                .setName("Hunt Blazes")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The alchemist needs blaze rods for brewing. Brave the Nether.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new KillObjective(EntityType.BLAZE, 5))
                .addReward(new ItemReward(Items.EMERALD, 8))
                .addReward(new ItemReward(Items.BLAZE_ROD, 2))
                .addReward(XpReward.expert());
    }

    private static Quest createCollectObsidianJob() {
        return new Quest("collect_obsidian")
                .setName("Collect Obsidian")
                .setCategory(Quest.QuestCategory.JOB)
                .setDescription("The architect is building a vault and needs obsidian blocks.")
                .setDifficulty(Quest.QuestDifficulty.EXPERT)
                .addObjective(new CollectObjective(Items.OBSIDIAN, 16))
                .addReward(new ItemReward(Items.EMERALD, 8))
                .addReward(new ItemReward(Items.DIAMOND_PICKAXE, 1))
                .addReward(XpReward.expert());
    }
}
