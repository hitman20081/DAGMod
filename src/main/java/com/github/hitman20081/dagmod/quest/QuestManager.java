package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.progression.LevelRequirements;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.quest.daily.DailyQuestManager;
import com.github.hitman20081.dagmod.quest.daily.DailyStreakManager;
import com.github.hitman20081.dagmod.quest.objectives.CollectObjective;
import com.github.hitman20081.dagmod.quest.objectives.KillObjective;
import com.github.hitman20081.dagmod.quest.objectives.MultiItemCollectObjective;
import com.github.hitman20081.dagmod.quest.QuestUtils;
import com.github.hitman20081.dagmod.quest.objectives.TagCollectObjective;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

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
    public QuestData getPlayerData(Player player) {
        return playerQuestData.computeIfAbsent(player.getUUID(), uuid -> new QuestData(uuid));
    }
    public void registerQuestChain(QuestChain chain) {
        questChains.put(chain.getChainId(), chain);
    }

    public List<QuestChain> getAvailableChains(Player player) {
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

    public void checkChainCompletion(Player player, String completedQuestId) {
        QuestData playerData = getPlayerData(player);

        for (QuestChain chain : questChains.values()) {
            if (chain.getQuestIds().contains(completedQuestId)) {
                if (chain.isChainCompleted(playerData)) {
                    // Chain completed! Give rewards and unlock tier
                    player.sendOverlayMessage(Component.literal("*** QUEST CHAIN COMPLETED: " + chain.getChainName() + " ***"));

                    // Give chain completion rewards
                    for (QuestReward reward : chain.getChainCompletionRewards()) {
                        reward.giveReward(player, player.level());
                    }

                    // Auto-upgrade quest book tier if applicable
                    if (chain.getRewardTier() != null &&
                            playerData.getQuestBookTier().getTier() < chain.getRewardTier().getTier()) {
                        QuestData.QuestBookTier oldTier = playerData.getQuestBookTier();
                        playerData.setQuestBookTier(chain.getRewardTier());
                        player.sendSystemMessage(Component.literal("Quest Book upgraded to: " + chain.getRewardTier().getDisplayName()));

                        // Swap the physical book: remove old, give new
                        com.github.hitman20081.dagmod.quest.QuestUtils.swapQuestBook((ServerPlayer) player, oldTier, chain.getRewardTier());
                    }

                    // Start next chain automatically if it exists
                    startNextChainInSequence(player, chain);
                }
            }
        }
    }

    private void startNextChainInSequence(Player player, QuestChain completedChain) {
        // Look for chains that require the tier we just unlocked
        QuestData playerData = getPlayerData(player);

        for (QuestChain chain : questChains.values()) {
            if (chain.getRequiredTier() == completedChain.getRewardTier()) {
                String firstQuest = chain.getNextAvailableQuest(playerData);
                if (firstQuest != null) {
                    player.sendSystemMessage(Component.literal("New quest chain unlocked: " + chain.getChainName()));
                    player.sendSystemMessage(Component.literal("Visit a Quest Block to begin!"));
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


    public boolean canAcceptQuest(Player player, Quest quest) {
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
    public boolean canStartQuest(Player player, Quest quest) {
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
    public boolean startQuest(Player player, String questId) {
        return startQuest(player, questId, false);
    }

    public boolean startClassQuest(Player player, String questId) {
        return startQuest(player, questId, true);
    }

    /**
     * Starts a daily quest. Bypasses quest book tier check.
     * Enforces the per-day completion limit (3 dailies/day) and checks today's pool.
     */
    public boolean startDailyQuest(Player player, String questId) {
        UUID uid = player.getUUID();

        // Must be in today's pool
        if (!DailyQuestManager.getInstance().getTodayQuestIds().contains(questId)) {
            player.sendSystemMessage(Component.literal("That quest isn't available today.").withStyle(net.minecraft.ChatFormatting.RED));
            return false;
        }

        // Can't accept one already completed today
        if (DailyStreakManager.hasCompletedToday(uid, questId)) {
            player.sendSystemMessage(Component.literal("You've already completed that daily today!").withStyle(net.minecraft.ChatFormatting.YELLOW));
            return false;
        }

        // Enforce 3-daily-per-day limit (completed + currently active dailies)
        int completedToday = DailyStreakManager.completedTodayCount(uid);
        long activeDailies = getPlayerData(player).getActiveQuests().stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.DAILY).count();
        if (completedToday + activeDailies >= DailyQuestManager.DAILY_COMPLETION_LIMIT) {
            player.sendSystemMessage(Component.literal("Daily quest limit reached (3/day). Come back tomorrow!").withStyle(net.minecraft.ChatFormatting.YELLOW));
            return false;
        }

        return startQuest(player, questId, true); // bypass tier check
    }

    /**
     * Turns in a completed daily quest. Does NOT add to completedQuestIds — daily quests are
     * repeatable. Instead, marks the completion in DailyStreakManager and applies bonus multipliers.
     */
    public boolean turnInDailyQuest(Player player, String questId) {
        QuestData playerData = getPlayerData(player);
        Quest quest = playerData.getActiveQuest(questId);

        if (quest == null) {
            player.sendOverlayMessage(Component.literal("You don't have that quest active."));
            return false;
        }

        // Refresh and validate objectives
        for (QuestObjective objective : quest.getObjectives()) {
            objective.updateProgress(player);
        }
        if (!quest.isCompleted()) {
            player.sendSystemMessage(Component.literal("Quest objectives not completed yet!"));
            return false;
        }

        // Consume collect items
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof CollectObjective co) {
                if (!co.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items!").withStyle(net.minecraft.ChatFormatting.RED));
                    return false;
                }
            } else if (objective instanceof com.github.hitman20081.dagmod.quest.objectives.MultiItemCollectObjective mi) {
                if (!mi.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items!").withStyle(net.minecraft.ChatFormatting.RED));
                    return false;
                }
            } else if (objective instanceof com.github.hitman20081.dagmod.quest.objectives.TagCollectObjective tc) {
                if (!tc.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items!").withStyle(net.minecraft.ChatFormatting.RED));
                    return false;
                }
            }
        }

        // Calculate bonus XP multiplier from streak and level
        UUID uid = player.getUUID();
        int playerLevel = 1;
        var progData = ProgressionManager.getPlayerData((ServerPlayer) player);
        if (progData != null) playerLevel = progData.getCurrentLevel();
        float multiplier = DailyStreakManager.getTotalMultiplier(uid, playerLevel);

        // Give rewards (with XP multiplier applied)
        for (QuestReward reward : quest.getRewards()) {
            if (reward instanceof com.github.hitman20081.dagmod.quest.rewards.XpReward xpReward && multiplier != 1.0f) {
                xpReward.giveScaledReward(player, player.level(), multiplier);
            } else {
                reward.giveReward(player, player.level());
            }
        }

        // Mark as completed in DailyStreakManager (NOT in permanentcompletedQuestIds)
        net.minecraft.server.MinecraftServer server = player.level().getServer();
        if (server != null) {
            DailyStreakManager.markCompleted(server, uid, questId);
        }

        // Remove from active quests (but not added to completedQuestIds — it's repeatable)
        playerData.removeActiveQuest(questId);

        // Persist
        if (player instanceof ServerPlayer sp) {
            savePlayerQuestData(sp);
        }

        // Streak feedback
        int streak = DailyStreakManager.getStreak(uid);
        int completedToday = DailyStreakManager.completedTodayCount(uid);
        player.sendSystemMessage(Component.literal("✓ Daily quest complete: " + quest.getName()).withStyle(net.minecraft.ChatFormatting.GREEN));
        if (multiplier > 1.0f) {
            player.sendSystemMessage(Component.literal(String.format("  Bonus XP ×%.2f (streak %dd, level %d)", multiplier, streak, playerLevel)).withStyle(net.minecraft.ChatFormatting.AQUA));
        }
        if (streak >= 3) {
            player.sendSystemMessage(Component.literal("  🔥 " + streak + "-day streak!").withStyle(net.minecraft.ChatFormatting.GOLD));
        }
        int remaining = DailyQuestManager.DAILY_COMPLETION_LIMIT - completedToday;
        if (remaining > 0) {
            player.sendSystemMessage(Component.literal("  " + remaining + " daily quest(s) remaining today.").withStyle(net.minecraft.ChatFormatting.GRAY));
        } else {
            player.sendSystemMessage(Component.literal("  All daily quests complete for today! Come back tomorrow.").withStyle(net.minecraft.ChatFormatting.YELLOW));
        }

        return true;
    }

    private boolean startQuest(Player player, String questId, boolean bypassTierCheck) {
        Quest quest = allQuests.get(questId);
        if (quest == null) {
            player.sendSystemMessage(Component.literal("Quest not found: " + questId));
            return false;
        }

        QuestData playerData = getPlayerData(player);

        // Class quests bypass the quest book tier check — they run on their own track
        if (!bypassTierCheck && !playerData.canAcceptQuestDifficulty(quest.getDifficulty())) {
            player.sendSystemMessage(Component.literal("Your quest book tier doesn't allow " +
                    quest.getDifficulty().getDisplayName() + " quests!"));
            player.sendSystemMessage(Component.literal("Upgrade your quest book to access this quest."));
            return false;
        }

        // Check level requirement
        if (!LevelRequirements.meetsLevelRequirement((ServerPlayer) player, quest)) {
            int requiredLevel = LevelRequirements.getRequiredLevelForQuest(quest);
            LevelRequirements.sendLevelRequirementMessage((ServerPlayer) player, requiredLevel);
            return false;
        }

        // Check active quest limit (uses quest book tier)
        if (playerData.getActiveQuests().size() >= playerData.getMaxActiveQuests()) {
            player.sendSystemMessage(Component.literal("You have too many active quests! Complete some first."));
            return false;
        }

        // Create a copy of the quest for this player
        Quest playerQuest = copyQuest(quest);
        playerQuest.setStatus(Quest.QuestStatus.ACTIVE);

        // Add to player's active quests
        playerData.addActiveQuest(playerQuest);

        // Save quest data immediately after accepting quest
        if (player instanceof ServerPlayer serverPlayer) {
            savePlayerQuestData(serverPlayer);
        }

        player.sendSystemMessage(Component.literal("Quest started: " + quest.getName()));
        return true;
    }

    // Update quest progress (called periodically or on specific events)
    public void updateQuestProgress(Player player) {
        QuestData playerData = getPlayerData(player);

        for (Quest quest : playerData.getActiveQuests()) {
            // Null safety check for quest objectives
            if (quest == null || quest.getObjectives() == null) {
                continue; // Skip malformed quest data
            }

            boolean progressMade = false;

            // Update each objective (always update collect objectives to track current inventory)
            for (QuestObjective objective : quest.getObjectives()) {
                // Always update collect objectives (they need to track current inventory state)
                boolean isCollectObjective = (objective instanceof CollectObjective) ||
                                            (objective instanceof MultiItemCollectObjective) ||
                                            (objective instanceof TagCollectObjective);

                if (!objective.isCompleted() || isCollectObjective) {
                    boolean objProgress = objective.updateProgress(player);
                    if (objProgress) {
                        progressMade = true;
                    }
                }
            }

            // Check if quest is now complete
            if (quest.isCompleted() && quest.getStatus() == Quest.QuestStatus.ACTIVE) {
                quest.setStatus(Quest.QuestStatus.COMPLETED);
                player.sendSystemMessage(Component.literal("Quest completed: " + quest.getName() + "! Return to turn it in."));
            }

            // Check if a COMPLETED quest is no longer complete (e.g., dropped collect items)
            if (!quest.isCompleted() && quest.getStatus() == Quest.QuestStatus.COMPLETED) {
                quest.setStatus(Quest.QuestStatus.ACTIVE);
                player.sendSystemMessage(Component.literal("Quest no longer complete: " + quest.getName()).withStyle(net.minecraft.ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("(Collect quests require items at turn-in)").withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            // Notify player of progress (optional, might be too spammy)
            // if (progressMade) {
            //     player.sendSystemMessage(Component.literal("Quest progress updated: " + quest.getName()));
            // }
        }
    }

    // Turn in a completed quest
    public boolean turnInQuest(Player player, String questId) {
        QuestData playerData = getPlayerData(player);
        Quest quest = playerData.getActiveQuest(questId);

        if (quest == null) {
            player.sendOverlayMessage(Component.literal("You don't have that quest active."));
            return false;
        }

        // Null safety check for quest objectives
        if (quest.getObjectives() == null) {
            player.sendSystemMessage(Component.literal("Quest data error - invalid objectives.").withStyle(net.minecraft.ChatFormatting.RED));
            com.github.hitman20081.dagmod.DagMod.LOGGER.error("Null objectives for quest: " + questId);
            return false;
        }

        // Refresh progress immediately before validation
        for (QuestObjective objective : quest.getObjectives()) {
            objective.updateProgress(player);
        }

        if (!quest.isCompleted()) {
            player.sendSystemMessage(Component.literal("Quest objectives not completed yet!"));
            return false;
        }

        // Consume items for collect objectives
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof CollectObjective collectObj) {
                if (!collectObj.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items in your inventory!").withStyle(net.minecraft.ChatFormatting.RED));
                    player.sendSystemMessage(Component.literal("Quest requires items to be present at turn-in.").withStyle(net.minecraft.ChatFormatting.GRAY));
                    return false;
                }
                player.sendSystemMessage(Component.literal("✓ Consumed quest items").withStyle(net.minecraft.ChatFormatting.GRAY));
            } else if (objective instanceof MultiItemCollectObjective multiCollectObj) {
                if (!multiCollectObj.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items in your inventory!").withStyle(net.minecraft.ChatFormatting.RED));
                    player.sendSystemMessage(Component.literal("Quest requires items to be present at turn-in.").withStyle(net.minecraft.ChatFormatting.GRAY));
                    return false;
                }
                player.sendSystemMessage(Component.literal("✓ Consumed quest items").withStyle(net.minecraft.ChatFormatting.GRAY));
            } else if (objective instanceof TagCollectObjective tagCollectObj) {
                if (!tagCollectObj.consumeItems(player)) {
                    player.sendSystemMessage(Component.literal("✗ You don't have the required items in your inventory!").withStyle(net.minecraft.ChatFormatting.RED));
                    player.sendSystemMessage(Component.literal("Quest requires: " + tagCollectObj.getDescription()).withStyle(net.minecraft.ChatFormatting.GRAY));
                    return false;
                }
                player.sendSystemMessage(Component.literal("✓ Consumed " + tagCollectObj.getRequiredAmount() + " " + tagCollectObj.getDisplayName()).withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        }

        // Give rewards
        boolean allRewardsGiven = true;
        for (QuestReward reward : quest.getRewards()) {
            if (!reward.giveReward(player, player.level())) {
                allRewardsGiven = false;
            }
        }

        if (!allRewardsGiven) {
            player.sendSystemMessage(Component.literal("Some rewards could not be given (inventory full?)"));
        }

        // Mark quest as completed
        quest.setStatus(Quest.QuestStatus.TURNED_IN);
        playerData.completeQuest(quest, (ServerPlayer) player);
        checkChainCompletion(player, quest.getId());

        // Save quest data immediately after completion
        if (player instanceof ServerPlayer serverPlayer) {
            savePlayerQuestData(serverPlayer);
        }

        player.sendSystemMessage(Component.literal("Quest turned in: " + quest.getName()));

        // Start next quest in chain if exists
        if (quest.getNextQuestId() != null) {
            startQuest(player, quest.getNextQuestId());
        }

        return true;
    }

    /**
     * Abandon an active quest (removes it without completing)
     */
    public boolean abandonQuest(Player player, String questId) {
        QuestData playerData = getPlayerData(player);
        Quest quest = playerData.getActiveQuest(questId);

        if (quest == null) {
            player.sendSystemMessage(Component.literal("You don't have that quest active.").withStyle(net.minecraft.ChatFormatting.RED));
            return false;
        }

        playerData.removeActiveQuest(questId);

        if (player instanceof ServerPlayer serverPlayer) {
            savePlayerQuestData(serverPlayer);
        }

        player.sendSystemMessage(Component.literal("Quest abandoned: " + quest.getName()).withStyle(net.minecraft.ChatFormatting.YELLOW));
        return true;
    }

    // Copy a quest for a player (so each player has their own progress)
    public Quest copyQuest(Quest original) {
        // Use the Quest.copy() method which handles all objective types
        return original.copy();
    }
    public void updateKillProgress(ServerPlayer player, EntityType<?> killedEntityType) {
        QuestData playerData = getPlayerData(player);

        for (Quest quest : playerData.getActiveQuests()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof KillObjective killObj) {
                    boolean progressMade = killObj.updateProgress(player, killedEntityType);
                    if (progressMade) {
                        player.sendSystemMessage(Component.literal("Quest progress: " + killObj.getDisplayText().getString()));
                    }
                }
            }

            // Check if quest is now complete
            if (quest.isCompleted() && quest.getStatus() == Quest.QuestStatus.ACTIVE) {
                quest.setStatus(Quest.QuestStatus.COMPLETED);
                player.sendSystemMessage(Component.literal("Quest completed: " + quest.getName() + "! Return to turn it in."));
            }
        }
    }

    // Get all available quests for a player
    public List<Quest> getAvailableQuests(Player player) {
        QuestData playerData = getPlayerData(player);
        List<Quest> available = new ArrayList<>();

        // Get player's class and race
        String playerClass = com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        String playerRace = com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        for (Quest quest : allQuests.values()) {
            // NPC-exclusive quests are given directly by NPCs, never shown in quest blocks
            if (quest.getCategory() == Quest.QuestCategory.NPC) {
                continue;
            }

            // Check class requirement
            if (quest.isClassRestricted() && !quest.getRequiredClass().equals(playerClass)) {
                continue; // Skip quests for other classes
            }

            // Check race requirement
            if (quest.isRaceRestricted() && !quest.getRequiredRace().equals(playerRace)) {
                continue; // Skip quests for other races
            }

            boolean isDaily = quest.getCategory() == Quest.QuestCategory.DAILY;

            // Daily quests bypass level/tier gating — startDailyQuest() already does the
            // same, so today's rotation should always be fully visible and completable.
            if (!isDaily) {
                // Check level requirement
                if (!LevelRequirements.meetsLevelRequirement((ServerPlayer) player, quest)) {
                    continue; // Skip quests player's level is too low for
                }

                // Check if their quest book tier allows this difficulty
                if (!playerData.canAcceptQuestDifficulty(quest.getDifficulty())) {
                    continue;
                }
            }

            if (canStartQuest(player, quest)) {
                available.add(quest);
            }
        }

        // ✨ SORT QUESTS BY PRIORITY ✨
        // This ensures tutorial quests appear first, followed by difficulty and name
        available.sort((q1, q2) -> {
            // PRIORITY 1: Tutorial quest (Garrick's Welcome) always appears first
            boolean q1IsTutorial = q1.getId().equals("garricks_special_brew");
            boolean q2IsTutorial = q2.getId().equals("garricks_special_brew");

            if (q1IsTutorial && !q2IsTutorial) return -1;  // q1 comes first
            if (!q1IsTutorial && q2IsTutorial) return 1;   // q2 comes first

            // PRIORITY 2: Sort by difficulty (NOVICE → APPRENTICE → EXPERT → MASTER)
            int difficultyCompare = q1.getDifficulty().compareTo(q2.getDifficulty());
            if (difficultyCompare != 0) {
                return difficultyCompare;
            }

            // PRIORITY 3: Sort alphabetically by quest name
            return q1.getName().compareTo(q2.getName());
        });

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

    // ========== PERSISTENCE METHODS ==========

    /**
     * Save player's quest data to disk
     */
    public void savePlayerQuestData(ServerPlayer player) {
        QuestData questData = getPlayerData(player);
        QuestStorage.saveQuestData(player, questData);
    }

    /**
     * Load player's quest data from disk
     * Called when player joins the server
     */
    public void loadPlayerQuestData(ServerPlayer player) {
        QuestData loadedData = QuestStorage.loadQuestData(player);

        if (loadedData != null) {
            // Replace the in-memory data with loaded data
            playerQuestData.put(player.getUUID(), loadedData);
        }
        // If null, player is new - keep the default QuestData created by getPlayerData()
    }

    /**
     * Clear player data from memory (called on disconnect for cleanup)
     */
    public void clearPlayerData(UUID playerId) {
        playerQuestData.remove(playerId);
    }

    /**
     * Clear all player data (called on server shutdown)
     */
    public void clearAllData() {
        playerQuestData.clear();
    }
}