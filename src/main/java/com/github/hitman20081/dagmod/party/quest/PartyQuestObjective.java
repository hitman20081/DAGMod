package com.github.hitman20081.dagmod.party.quest;

/**
 * Represents a single objective within a party quest
 */
public class PartyQuestObjective {
    private final String id;
    private final PartyQuestObjectiveType type;
    private final String target; // Entity type, item type, etc.
    private final int requiredAmount;
    private final String description;

    public PartyQuestObjective(String id, PartyQuestObjectiveType type, String target, int requiredAmount, String description) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.requiredAmount = requiredAmount;
        this.description = description;
    }

    // Getters
    public String getId() {
        return id;
    }

    public PartyQuestObjectiveType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Objective{" + description + " (" + requiredAmount + ")}";
    }
}