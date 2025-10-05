package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.progression.XPEventHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class QuestData {
    private final UUID playerId;
    private final Map<String, Quest> activeQuests;
    private final Set<String> completedQuestIds;
    private final Map<String, Long> questCompletionTimes; // For tracking when quests were completed
    private int totalQuestsCompleted;
    private QuestBookTier questBookTier;

    // Quest book tiers that determine available quests and slots
    public enum QuestBookTier {
        NOVICE(1, 2, "Novice Quest Book",
                List.of(Quest.QuestDifficulty.NOVICE)),
        APPRENTICE(2, 3, "Apprentice Quest Book",
                List.of(Quest.QuestDifficulty.NOVICE, Quest.QuestDifficulty.APPRENTICE)),
        EXPERT(3, 5, "Expert Quest Book",
                List.of(Quest.QuestDifficulty.NOVICE, Quest.QuestDifficulty.APPRENTICE, Quest.QuestDifficulty.EXPERT)),
        MASTER(4, 7, "Master Quest Tome",
                List.of(Quest.QuestDifficulty.NOVICE, Quest.QuestDifficulty.APPRENTICE, Quest.QuestDifficulty.EXPERT, Quest.QuestDifficulty.MASTER));

        private final int tier;
        private final int maxActiveQuests;
        private final String displayName;
        private final List<Quest.QuestDifficulty> allowedDifficulties;

        QuestBookTier(int tier, int maxActiveQuests, String displayName, List<Quest.QuestDifficulty> allowedDifficulties) {
            this.tier = tier;
            this.maxActiveQuests = maxActiveQuests;
            this.displayName = displayName;
            this.allowedDifficulties = allowedDifficulties;
        }

        public int getTier() { return tier; }
        public int getMaxActiveQuests() { return maxActiveQuests; }
        public String getDisplayName() { return displayName; }
        public List<Quest.QuestDifficulty> getAllowedDifficulties() { return allowedDifficulties; }

        public boolean canAcceptDifficulty(Quest.QuestDifficulty difficulty) {
            return allowedDifficulties.contains(difficulty);
        }
    }

    public QuestData(UUID playerId) {
        this.playerId = playerId;
        this.activeQuests = new HashMap<>();
        this.completedQuestIds = new HashSet<>();
        this.questCompletionTimes = new HashMap<>();
        this.totalQuestsCompleted = 0;
        this.questBookTier = QuestBookTier.NOVICE; // Start with novice book
    }

    // Active quest management
    public void addActiveQuest(Quest quest) {
        activeQuests.put(quest.getId(), quest);
    }

    public void removeActiveQuest(String questId) {
        activeQuests.remove(questId);
    }

    public Quest getActiveQuest(String questId) {
        return activeQuests.get(questId);
    }

    public boolean hasActiveQuest(String questId) {
        return activeQuests.containsKey(questId);
    }

    public Collection<Quest> getActiveQuests() {
        return activeQuests.values();
    }

    public List<Quest> getActiveQuestsList() {
        return new ArrayList<>(activeQuests.values());
    }

    public int getActiveQuestCount() {
        return activeQuests.size();
    }

    // Completed quest management
    public void completeQuest(Quest quest, ServerPlayerEntity player) {
        // Remove from active quests
        removeActiveQuest(quest.getId());

        // Add to completed
        completedQuestIds.add(quest.getId());
        questCompletionTimes.put(quest.getId(), System.currentTimeMillis());
        totalQuestsCompleted++;

        // Check for quest book upgrade
        checkQuestBookUpgrade();

        // ADD THESE LINES:
        // Award XP based on quest difficulty
        XPEventHandler.onQuestCompleted(player, quest.getDifficulty().name());
    }

    public boolean isQuestCompleted(String questId) {
        return completedQuestIds.contains(questId);
    }

    public Set<String> getCompletedQuestIds() {
        return new HashSet<>(completedQuestIds);
    }

    public List<String> getCompletedQuestIdsList() {
        return new ArrayList<>(completedQuestIds);
    }

    public int getTotalQuestsCompleted() {
        return totalQuestsCompleted;
    }

    // Quest book tier management
    public QuestBookTier getQuestBookTier() {
        return questBookTier;
    }

    public void setQuestBookTier(QuestBookTier tier) {
        this.questBookTier = tier;
    }

    public boolean canAcceptQuestDifficulty(Quest.QuestDifficulty difficulty) {
        return questBookTier.canAcceptDifficulty(difficulty);
    }

    public int getMaxActiveQuests() {
        return questBookTier.getMaxActiveQuests();
    }

    public boolean canAcceptMoreQuests() {
        return activeQuests.size() < getMaxActiveQuests();
    }

    // Check if player can upgrade their quest book
    public boolean canUpgradeQuestBook() {
        return getNextQuestBookTier() != null && meetsUpgradeRequirements(getNextQuestBookTier());
    }

    public QuestBookTier getNextQuestBookTier() {
        switch (questBookTier) {
            case NOVICE -> { return QuestBookTier.APPRENTICE; }
            case APPRENTICE -> { return QuestBookTier.EXPERT; }
            case EXPERT -> { return QuestBookTier.MASTER; }
            case MASTER -> { return null; } // Max tier
        }
        return null;
    }

    private boolean meetsUpgradeRequirements(QuestBookTier targetTier) {
        return switch (targetTier) {
            case APPRENTICE -> totalQuestsCompleted >= 5;
            case EXPERT -> totalQuestsCompleted >= 15 && hasCompletedQuestsOfDifficulty(Quest.QuestDifficulty.APPRENTICE, 3);
            case MASTER -> totalQuestsCompleted >= 30 && hasCompletedQuestsOfDifficulty(Quest.QuestDifficulty.EXPERT, 2);
            default -> false;
        };
    }

    // Check if player has completed enough quests of a specific difficulty
    private boolean hasCompletedQuestsOfDifficulty(Quest.QuestDifficulty difficulty, int required) {
        // This would need quest difficulty tracking - for now, assume requirements are met
        // In a full implementation, you'd track quest difficulties when completing them
        return totalQuestsCompleted >= required * 2; // Rough estimate
    }

    // Automatic quest book upgrade check
    private void checkQuestBookUpgrade() {
        QuestBookTier nextTier = getNextQuestBookTier();
        if (nextTier != null && meetsUpgradeRequirements(nextTier)) {
            // Don't auto-upgrade - let player choose when to upgrade
            // This prevents losing access to lower-tier quests if they want them
        }
    }

    // Get quest statistics
    public Map<String, Object> getQuestStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCompleted", totalQuestsCompleted);
        stats.put("currentActive", activeQuests.size());
        stats.put("maxActive", getMaxActiveQuests());
        stats.put("questBookTier", questBookTier.getDisplayName());
        stats.put("canUpgrade", canUpgradeQuestBook());
        return stats;
    }

    // Player ID getter
    public UUID getPlayerId() {
        return playerId;
    }

    // Get quest completion time
    public Long getQuestCompletionTime(String questId) {
        return questCompletionTimes.get(questId);
    }

    // Clear all quest data (for testing/reset purposes)
    public void clearAllData() {
        activeQuests.clear();
        completedQuestIds.clear();
        questCompletionTimes.clear();
        totalQuestsCompleted = 0;
        questBookTier = QuestBookTier.NOVICE;
    }
    // Persistence methods
}