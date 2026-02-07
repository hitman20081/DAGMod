package com.github.hitman20081.dagmod.party.quest;

/**
 * Difficulty levels for party quests
 */
public enum PartyQuestDifficulty {
    EASY(1.0, "Easy"),
    NORMAL(1.5, "Normal"),
    HARD(2.0, "Hard"),
    EXPERT(2.5, "Expert"),
    LEGENDARY(3.0, "Legendary");

    private final double rewardMultiplier;
    private final String displayName;

    PartyQuestDifficulty(double rewardMultiplier, String displayName) {
        this.rewardMultiplier = rewardMultiplier;
        this.displayName = displayName;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }
}