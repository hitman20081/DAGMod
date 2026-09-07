package com.github.hitman20081.dagmod.quest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public abstract class QuestReward {
    protected String description;
    protected RewardType type;

    public QuestReward(RewardType type, String description) {
        this.type = type;
        this.description = description;
    }

    // Abstract method - each reward type implements how it's given to the player
    public abstract boolean giveReward(Player player, Level world);

    // Abstract method - check if the reward can be given (inventory space, etc.)
    public abstract boolean canGiveReward(Player player);

    // Get display text for the reward (shown in quest UI)
    public Component getDisplayText() {
        return Component.literal("• " + description);
    }

    // Get the reward type
    public RewardType getType() {
        return type;
    }

    // Get description
    public String getDescription() {
        return description;
    }

    // Reward types enum
    public enum RewardType {
        ITEM("Item"),
        CURRENCY("Currency"),
        EXPERIENCE("Experience"),
        UNLOCK("Unlock"),
        REPUTATION("Reputation"),
        TITLE("Title"),
        CHOICE("Choice"); // For multiple reward options

        private final String displayName;

        RewardType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    // Helper method to create success/failure messages
    protected Component createSuccessMessage() {
        return Component.literal("Received: " + description);
    }

    protected Component createFailureMessage() {
        return Component.literal("Could not receive: " + description + " (inventory full?)");
    }
}