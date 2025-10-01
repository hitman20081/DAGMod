package com.github.hitman20081.dagmod.quest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public abstract class QuestReward {
    protected String description;
    protected RewardType type;

    public QuestReward(RewardType type, String description) {
        this.type = type;
        this.description = description;
    }

    // Abstract method - each reward type implements how it's given to the player
    public abstract boolean giveReward(PlayerEntity player, World world);

    // Abstract method - check if the reward can be given (inventory space, etc.)
    public abstract boolean canGiveReward(PlayerEntity player);

    // Get display text for the reward (shown in quest UI)
    public Text getDisplayText() {
        return Text.literal("• " + description);
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
    protected Text createSuccessMessage() {
        return Text.literal("Received: " + description);
    }

    protected Text createFailureMessage() {
        return Text.literal("Could not receive: " + description + " (inventory full?)");
    }
}