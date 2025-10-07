package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.progression.LevelRequirements;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import com.github.hitman20081.dagmod.quest.QuestUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuestManager {
    // Singleton instance
    private static QuestManager instance;

    // All registered quests (from QuestRegistry)
    private final Map<String, Quest> allQuests = new HashMap<>();
    private final Map<String, QuestChain> questChains = new HashMap<>();

    // Per-player quest data
    private final Map<UUID, QuestData> playerQuestData = new ConcurrentHashMap<>();

    // Private constructor for singleton
    private QuestManager() {}

    // Get singleton instance
    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }

    // Register a quest (called from QuestRegistry)
    public void registerQuest(Quest quest) {
        allQuests.put(quest.getId(), quest);
    }

    // Get player's quest data (create if doesn't exist)
    public QuestData getPlayerData(PlayerEntity player) {
        return playerQuestData.computeIfAbsent(player.getUuid(), uuid -> new QuestData(uuid));
    }
    public void registerQuestChain(QuestChain chain) {
        questChains.put(chain.getChainId(), chain);
    }

    public List<QuestChain> getAvailableChains(PlayerEntity player) {
        QuestData playerData = getPlayerData(player);
        List<QuestChain> available = new ArrayList<>();

        for (QuestChain chain : questChains.values()) {
            // Check if player's tier allows this chain
            if (playerData.canAcceptQuestDifficulty(chain.getRequiredTier().getAllowedDifficulties().get(0))) {
                // Check if chain has available quests
                String nextQuest = chain.getNextAvailableQuest(playerData);
                if (nextQuest != null) {
                    available.add(chain);
                }
            }
        }

        return available;
    }

    public void checkChainCompletion(PlayerEntity player, String completedQuestId) {
        QuestData playerData = getPlayerData(player);

        for (QuestChain chain : questChains.values()) {
            if (chain.getQuestIds().contains(completedQuestId)) {
                if (chain.isChainCompleted(playerData)) {
                    // Chain completed! Give rewards and unlock tier
                    player.sendMessage(Text.literal("*** QUEST CHAIN COMPLETED: " + chain.getChainName() + " ***"), false);

                    // Give chain completion rewards
                    for (QuestReward reward : chain.getChainCompletionRewards()) {
                        reward.giveReward(player, player.getWorld());
                    }

                    // Auto-upgrade quest book tier if applicable
                    if (chain.getRewardTier() != null &&
                            playerData.getQuestBookTier().getTier() < chain.getRewardTier().getTier()) {
                        playerData.setQuestBookTier(chain.getRewardTier());
                        player.sendMessage(Text.literal("Quest Book upgraded to: " + chain.getRewardTier().getDisplayName()), false);

                        // Give the physical book item - ADD THIS LINE
                        com.github.hitman20081.dagmod.quest.QuestUtils.giveQuestBookForTier((ServerPlayerEntity) player, chain.getRewardTier());
                    }

                    // Start next chain automatically if it exists
                    startNextChainInSequence(player, chain);
                }
            }
        }
    }

    private void startNextChainInSequence(PlayerEntity player, QuestChain completedChain) {
        // Look for chains that require the tier we just unlocked
        QuestData playerData = getPlayerData(player);

        for (QuestChain chain : questChains.values()) {
            if (chain.getRequiredTier() == completedChain.getRewardTier()) {
                String firstQuest = chain.getNextAvailableQuest(playerData);
                if (firstQuest != null) {
                    player.sendMessage(Text.literal("New quest chain unlocked: " + chain.getChainName()), false);
                    player.sendMessage(Text.literal("Visit a Quest Block to begin!"), false);
                    break; // Only unlock one chain at a time
                }
            }
        }
    }

    public QuestChain getQuestChain(String chainId) {
        return questChains.get(chainId);
    }

    public Collection<QuestChain> getAllChains() {
        return questChains.values();
    }


    public boolean canAcceptQuest(PlayerEntity player, Quest quest) {
        if (!quest.hasPrerequisites()) {
            return true; // No prerequisites required
        }

        QuestData playerData = getPlayerData(player);
        List<String> completedQuestIds = playerData.getCompletedQuestIdsList();

        // Check if all prerequisite quests are completed
        for (String prereqId : quest.getPrerequisiteQuestIds()) {
            if (!completedQuestIds.contains(prereqId)) {
                return false;
            }
        }

        return true;
    }

    // ADD THIS METHOD:
    public boolean canStartQuest(PlayerEntity player, Quest quest) {
        QuestData playerData = getPlayerData(player);

        // Check if already completed
        if (playerData.isQuestCompleted(quest.getId())) {
            return false;
        }

        // Check if already active
        if (playerData.hasActiveQuest(quest.getId())) {
            return false;
        }

        // Check prerequisites
        if (!quest.canStart(playerData.getCompletedQuestIdsList())) {
            return false;
        }

        return true;
    }

    // Check if player can start a quest
    public boolean startQuest(PlayerEntity player, String questId) {
        Quest quest = allQuests.get(questId);
        if (quest == null) {
            player.sendMessage(Text.literal("Quest not found: " + questId), false);
            return false;
        }

        QuestData playerData = getPlayerData(player);

        // DEBUG: Add this
        player.sendMessage(Text.literal("DEBUG: Attempting to start quest: " + quest.getName()), false);

        // Check if player's quest book tier allows this quest difficulty
        if (!playerData.canAcceptQuestDifficulty(quest.getDifficulty())) {
            player.sendMessage(Text.literal("Your quest book tier doesn't allow " +
                    quest.getDifficulty().getDisplayName() + " quests!"), false);
            player.sendMessage(Text.literal("Upgrade your quest book to access this quest."), false);
            return false;
        }

        // DEBUG: Add this
        player.sendMessage(Text.literal("DEBUG: Tier check passed"), false);

        // ADD THIS: Check level requirement
        if (!LevelRequirements.meetsLevelRequirement((ServerPlayerEntity) player, quest)) {
            int requiredLevel = LevelRequirements.getRequiredLevelForQuest(quest);
            LevelRequirements.sendLevelRequirementMessage((ServerPlayerEntity) player, requiredLevel);
            return false;
        }

        // DEBUG: Add this
        player.sendMessage(Text.literal("DEBUG: Level check passed"), false);

        // Check active quest limit
        if (playerData.getActiveQuests().size() >= getMaxActiveQuests(playerData)) {
            player.sendMessage(Text.literal("You have too many active quests! Complete some first."), false);
            return false;
        }

        // DEBUG: Add this
        player.sendMessage(Text.literal("DEBUG: Active quest limit check passed"), false);

        // Create a copy of the quest for this player
        Quest playerQuest = copyQuest(quest);
        playerQuest.setStatus(Quest.QuestStatus.ACTIVE);

        // Add to player's active quests
        playerData.addActiveQuest(playerQuest);

        player.sendMessage(Text.literal("Quest started: " + quest.getName()), false);
        return true;
    }

    // Update quest progress (called periodically or on specific events)
    public void updateQuestProgress(PlayerEntity player) {
        QuestData playerData = getPlayerData(player);

        for (Quest quest : playerData.getActiveQuests()) {
            boolean progressMade = false;

            // Update each objective
            for (QuestObjective objective : quest.getObjectives()) {
                if (!objective.isCompleted()) {
                    boolean objProgress = objective.updateProgress(player);
                    if (objProgress) {
                        progressMade = true;
                    }
                }
            }

            // Check if quest is now complete
            if (quest.isCompleted() && quest.getStatus() == Quest.QuestStatus.ACTIVE) {
                quest.setStatus(Quest.QuestStatus.COMPLETED);
                player.sendMessage(Text.literal("Quest completed: " + quest.getName() + "! Return to turn it in."), false);
            }

            // Notify player of progress (optional, might be too spammy)
            // if (progressMade) {
            //     player.sendMessage(Text.literal("Quest progress updated: " + quest.getName()), true);
            // }
        }
    }

    // Turn in a completed quest
    public boolean turnInQuest(PlayerEntity player, String questId) {
        QuestData playerData = getPlayerData(player);
        Quest quest = playerData.getActiveQuest(questId);

        if (quest == null) {
            player.sendMessage(Text.literal("You don't have that quest active."), false);
            return false;
        }

        if (!quest.isCompleted()) {
            player.sendMessage(Text.literal("Quest objectives not completed yet!"), false);
            return false;
        }

        // Consume items for collect objectives
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof CollectObjective collectObj) {
                if (!collectObj.consumeItems(player)) {
                    player.sendMessage(Text.literal("Error: Could not consume required items!"), false);
                    return false;
                }
            }
        }

        // Give rewards
        boolean allRewardsGiven = true;
        for (QuestReward reward : quest.getRewards()) {
            if (!reward.giveReward(player, player.getWorld())) {
                allRewardsGiven = false;
            }
        }

        if (!allRewardsGiven) {
            player.sendMessage(Text.literal("Some rewards could not be given (inventory full?)"), false);
        }

        // Mark quest as completed
        quest.setStatus(Quest.QuestStatus.TURNED_IN);
        playerData.completeQuest(quest, (ServerPlayerEntity) player);
        checkChainCompletion(player, quest.getId());

        player.sendMessage(Text.literal("Quest turned in: " + quest.getName()), false);

        // Start next quest in chain if exists
        if (quest.getNextQuestId() != null) {
            startQuest(player, quest.getNextQuestId());
        }

        return true;
    }

    // Get max active quests based on player progress
    private int getMaxActiveQuests(QuestData playerData) {
        int completedQuests = playerData.getCompletedQuestIds().size();

        if (completedQuests >= 20) return 7; // Master tier
        if (completedQuests >= 10) return 5; // Expert tier
        if (completedQuests >= 5) return 3;  // Apprentice tier
        return 2; // Novice tier
    }

    // Copy a quest for a player (so each player has their own progress)
    public Quest copyQuest(Quest original) {
        Quest copy = new Quest(original.getId())
                .setName(original.getName())
                .setDescription(original.getDescription())
                .setDifficulty(original.getDifficulty())
                .setNextQuest(original.getNextQuestId());

        // Copy objectives (each player needs their own objective instances)
        for (QuestObjective objective : original.getObjectives()) {
            // Create new instances of objectives
            if (objective instanceof CollectObjective collectObj) {
                copy.addObjective(new CollectObjective(collectObj.getTargetItem(), collectObj.getRequiredAmount()));
            }
            if (objective instanceof KillObjective killObj) {
                copy.addObjective(new KillObjective(killObj.getTargetEntityType(), killObj.getRequiredKills()));
            }
            // Add other objective types here as we create them
        }

        // Copy rewards (rewards can be shared references)
        for (QuestReward reward : original.getRewards()) {
            copy.addReward(reward);
        }

        return copy;
    }
    public void updateKillProgress(ServerPlayerEntity player, EntityType<?> killedEntityType) {
        QuestData playerData = getPlayerData(player);

        for (Quest quest : playerData.getActiveQuests()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof KillObjective killObj) {
                    boolean progressMade = killObj.updateProgress(player, killedEntityType);
                    if (progressMade) {
                        player.sendMessage(Text.literal("Quest progress: " + killObj.getDisplayText().getString()), true);
                    }
                }
            }

            // Check if quest is now complete
            if (quest.isCompleted() && quest.getStatus() == Quest.QuestStatus.ACTIVE) {
                quest.setStatus(Quest.QuestStatus.COMPLETED);
                player.sendMessage(Text.literal("Quest completed: " + quest.getName() + "! Return to turn it in."), false);
            }
        }
    }

    // Get all available quests for a player
    public List<Quest> getAvailableQuests(PlayerEntity player) {
        QuestData playerData = getPlayerData(player);
        List<Quest> available = new ArrayList<>();

        // Get player's class and race
        String playerClass = com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock.getPlayerClass(player.getUuid());
        String playerRace = com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock.getPlayerRace(player.getUuid());

        for (Quest quest : allQuests.values()) {
            // Check class requirement
            if (quest.isClassRestricted() && !quest.getRequiredClass().equals(playerClass)) {
                continue; // Skip quests for other classes
            }

            // Check race requirement
            if (quest.isRaceRestricted() && !quest.getRequiredRace().equals(playerRace)) {
                continue; // Skip quests for other races
            }

            // ADD THIS: Check level requirement
            if (!LevelRequirements.meetsLevelRequirement((ServerPlayerEntity) player, quest)) {
                continue; // Skip quests player's level is too low for
            }

            // Check if player can start the quest AND if their quest book tier allows it
            if (canStartQuest(player, quest) && playerData.canAcceptQuestDifficulty(quest.getDifficulty())) {
                available.add(quest);
            }
        }

        return available;
    }

    // Get quest by ID
    public Quest getQuest(String questId) {
        return allQuests.get(questId);
    }

    // Get all registered quests
    public Collection<Quest> getAllQuests() {
        return allQuests.values();
    }
}