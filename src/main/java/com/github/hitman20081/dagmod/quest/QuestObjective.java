package com.github.hitman20081.dagmod.quest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public abstract class QuestObjective {
    protected String description;
    protected int currentProgress;
    protected int requiredProgress;
    protected boolean completed;

    public QuestObjective(String description, int requiredProgress) {
        this.description = description;
        this.requiredProgress = requiredProgress;
        this.currentProgress = 0;
        this.completed = false;
    }

    // Abstract method - each objective type implements its own update logic
    public abstract boolean updateProgress(PlayerEntity player, Object... params);

    // Abstract method - each objective type defines what triggers it
    public abstract ObjectiveType getType();

    // Check if objective is completed
    public boolean isCompleted() {
        return completed || currentProgress >= requiredProgress;
    }

    // Add progress to the objective
    protected void addProgress(int amount) {
        currentProgress = Math.min(currentProgress + amount, requiredProgress);
        completed = currentProgress >= requiredProgress;
    }

    // Set progress directly
    public void setProgress(int progress) {
        currentProgress = progress; // Don't cap progress - let subclasses handle it
        completed = currentProgress >= requiredProgress;
    }

    // Get progress as text (e.g., "5/10")
    public Text getProgressText() {
        return Text.literal(currentProgress + "/" + requiredProgress);
    }

    // Get full description with progress
    public Text getDisplayText() {
        if (completed) {
            return Text.literal("✓ " + description + " (Complete)");
        } else {
            return Text.literal(description + " (" + currentProgress + "/" + requiredProgress + ")");
        }
    }

    // Reset objective progress
    public void reset() {
        currentProgress = 0;
        completed = false;
    }

    // Getters
    public String getDescription() { return description; }
    public int getCurrentProgress() { return currentProgress; }
    public int getRequiredProgress() { return requiredProgress; }

    // Objective types enum - helps with identifying what kind of objective this is
    public enum ObjectiveType {
        COLLECT("Collect"),
        KILL("Kill"),
        CRAFT("Craft"),
        DELIVERY("Delivery"),
        EXPLORE("Explore"),
        INTERACT("Interact");

        private final String displayName;

        ObjectiveType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
}