package com.github.hitman20081.dagmod.quest;

import java.util.ArrayList;
import java.util.List;

public class QuestChain {
    private final String chainId;
    private final String chainName;
    private final String description;
    private final List<String> questIds;
    private final QuestData.QuestBookTier requiredTier;
    private final QuestData.QuestBookTier rewardTier; // Tier unlocked upon completion
    private final List<QuestReward> chainCompletionRewards;

    public QuestChain(String chainId, String chainName, String description,
                      QuestData.QuestBookTier requiredTier, QuestData.QuestBookTier rewardTier) {
        this.chainId = chainId;
        this.chainName = chainName;
        this.description = description;
        this.questIds = new ArrayList<>();
        this.requiredTier = requiredTier;
        this.rewardTier = rewardTier;
        this.chainCompletionRewards = new ArrayList<>();
    }

    public QuestChain addQuest(String questId) {
        questIds.add(questId);
        return this;
    }

    public QuestChain addChainReward(QuestReward reward) {
        chainCompletionRewards.add(reward);
        return this;
    }

    // Check if player has completed entire chain
    public boolean isChainCompleted(QuestData playerData) {
        for (String questId : questIds) {
            if (!playerData.isQuestCompleted(questId)) {
                return false;
            }
        }
        return true;
    }

    // Get next quest in chain that player can start
    public String getNextAvailableQuest(QuestData playerData) {
        for (String questId : questIds) {
            if (!playerData.isQuestCompleted(questId) && !playerData.hasActiveQuest(questId)) {
                return questId;
            }
        }
        return null; // Chain completed or all quests active
    }

    // Get progress through chain (0.0 to 1.0)
    public float getChainProgress(QuestData playerData) {
        if (questIds.isEmpty()) return 1.0f;

        int completed = 0;
        for (String questId : questIds) {
            if (playerData.isQuestCompleted(questId)) {
                completed++;
            }
        }
        return (float) completed / questIds.size();
    }

    // Getters
    public String getChainId() { return chainId; }
    public String getChainName() { return chainName; }
    public String getDescription() { return description; }
    public List<String> getQuestIds() { return new ArrayList<>(questIds); }
    public QuestData.QuestBookTier getRequiredTier() { return requiredTier; }
    public QuestData.QuestBookTier getRewardTier() { return rewardTier; }
    public List<QuestReward> getChainCompletionRewards() { return new ArrayList<>(chainCompletionRewards); }
    public int getChainLength() { return questIds.size(); }
}