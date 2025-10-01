package com.github.hitman20081.dagmod.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quest {
    private String id;
    private String name;
    private String description;
    private QuestDifficulty difficulty;
    private List<QuestObjective> objectives;
    private List<QuestReward> rewards;
    private List<String> prerequisites; // IDs of quests that must be completed first
    private String nextQuestId; // For quest chains
    private QuestStatus status;

    // NEW FIELDS FOR REPEATABLE/SEASONAL QUESTS:
    private boolean repeatable = false;
    private long cooldownTime = 0; // In milliseconds
    private String seasonRequirement = null; // "spring", "summer", etc.

    // Quest difficulty levels
    public enum QuestDifficulty {
        NOVICE("Novice", 0x00FF00),    // Green
        APPRENTICE("Apprentice", 0x0066FF), // Blue
        EXPERT("Expert", 0x9900FF),    // Purple
        MASTER("Master", 0xFFD700);    // Gold

        private final String displayName;
        private final int color;

        QuestDifficulty(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public int getColor() { return color; }
    }

    // Quest completion status
    public enum QuestStatus {
        NOT_STARTED,
        ACTIVE,
        COMPLETED,
        FAILED,
        TURNED_IN
    }

    // Constructor
    public Quest(String id) {
        this.id = id;
        this.objectives = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
        this.status = QuestStatus.NOT_STARTED;
    }

    // Builder pattern methods for easy quest creation
    public Quest setName(String name) {
        this.name = name;
        return this;
    }

    public Quest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Quest setDifficulty(QuestDifficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public Quest addObjective(QuestObjective objective) {
        this.objectives.add(objective);
        return this;
    }

    public Quest addReward(QuestReward reward) {
        this.rewards.add(reward);
        return this;
    }

    public Quest addPrerequisite(String questId) {
        this.prerequisites.add(questId);
        return this;
    }

    public Quest setNextQuest(String nextQuestId) {
        this.nextQuestId = nextQuestId;
        return this;
    }

    // NEW METHODS FOR REPEATABLE/SEASONAL QUESTS:
    public Quest setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public Quest setCooldown(long cooldownMinutes) {
        this.cooldownTime = cooldownMinutes * 60 * 1000; // Convert to milliseconds
        return this;
    }

    public Quest setSeasonRequirement(String season) {
        this.seasonRequirement = season;
        return this;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public QuestDifficulty getDifficulty() { return difficulty; }
    public List<QuestObjective> getObjectives() { return objectives; }
    public List<QuestReward> getRewards() { return rewards; }
    public List<String> getPrerequisites() { return prerequisites; }
    public String getNextQuestId() { return nextQuestId; }
    public QuestStatus getStatus() { return status; }

    // NEW GETTERS:
    public boolean isRepeatable() { return repeatable; }
    public long getCooldownTime() { return cooldownTime; }
    public String getSeasonRequirement() { return seasonRequirement; }

    // Status management
    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    // Check if all objectives are completed
    public boolean isCompleted() {
        return objectives.stream().allMatch(QuestObjective::isCompleted);
    }

    // Check if quest can be started (prerequisites met)
    public boolean canStart(List<String> completedQuestIds) {
        return prerequisites.stream().allMatch(completedQuestIds::contains);
    }

    // SIMPLIFIED PREREQUISITE METHODS:
    public List<String> getPrerequisiteQuestIds() {
        return new ArrayList<>(prerequisites);
    }

    public boolean hasPrerequisites() {
        return !prerequisites.isEmpty();
    }
}