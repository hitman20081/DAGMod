package com.github.hitman20081.dagmod.party.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Template/definition for a party quest
 * Contains all the quest information and requirements
 */
public class PartyQuestTemplate {
    private final String id;
    private final String name;
    private final String description;
    private final int minPartySize;
    private final int maxPartySize;
    private final List<PartyQuestObjective> objectives;
    private final int xpReward;
    private final List<PartyQuestReward> rewards;
    private final long timeLimit; // In milliseconds, 0 = no limit
    private final PartyQuestDifficulty difficulty;

    private PartyQuestTemplate(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.minPartySize = builder.minPartySize;
        this.maxPartySize = builder.maxPartySize;
        this.objectives = builder.objectives;
        this.xpReward = builder.xpReward;
        this.rewards = builder.rewards;
        this.timeLimit = builder.timeLimit;
        this.difficulty = builder.difficulty;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinPartySize() {
        return minPartySize;
    }

    public int getMaxPartySize() {
        return maxPartySize;
    }

    public List<PartyQuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public PartyQuestObjective getObjective(String objectiveId) {
        return objectives.stream()
                .filter(obj -> obj.getId().equals(objectiveId))
                .findFirst()
                .orElse(null);
    }

    public int getXpReward() {
        return xpReward;
    }

    public List<PartyQuestReward> getRewards() {
        return new ArrayList<>(rewards);
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public PartyQuestDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }

    public int getTimeLimitMinutes() {
        return (int) (timeLimit / 60000);
    }

    // Builder pattern for creating quest templates
    public static class Builder {
        private String id;
        private String name;
        private String description;
        private int minPartySize = 2;
        private int maxPartySize = 5;
        private List<PartyQuestObjective> objectives = new ArrayList<>();
        private int xpReward = 0;
        private List<PartyQuestReward> rewards = new ArrayList<>();
        private long timeLimit = 0;
        private PartyQuestDifficulty difficulty = PartyQuestDifficulty.NORMAL;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder minPartySize(int size) {
            this.minPartySize = size;
            return this;
        }

        public Builder maxPartySize(int size) {
            this.maxPartySize = size;
            return this;
        }

        public Builder objective(PartyQuestObjective objective) {
            this.objectives.add(objective);
            return this;
        }

        public Builder xpReward(int xp) {
            this.xpReward = xp;
            return this;
        }

        public Builder reward(PartyQuestReward reward) {
            this.rewards.add(reward);
            return this;
        }

        public Builder timeLimit(int minutes) {
            this.timeLimit = minutes * 60000L; // Convert to milliseconds
            return this;
        }

        public Builder difficulty(PartyQuestDifficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public PartyQuestTemplate build() {
            if (id == null || name == null) {
                throw new IllegalStateException("Quest must have id and name");
            }
            if (objectives.isEmpty()) {
                throw new IllegalStateException("Quest must have at least one objective");
            }
            return new PartyQuestTemplate(this);
        }
    }

    @Override
    public String toString() {
        return "PartyQuestTemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", difficulty=" + difficulty +
                ", objectives=" + objectives.size() +
                '}';
    }
}