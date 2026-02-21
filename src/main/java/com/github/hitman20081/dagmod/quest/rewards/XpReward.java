package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class XpReward extends QuestReward {
    private final int xpAmount;
    private final boolean isLevels; // true for levels, false for points

    // Constructor for XP points
    public XpReward(int xpAmount) {
        super(RewardType.EXPERIENCE, createPointsDescription(xpAmount));
        this.xpAmount = xpAmount;
        this.isLevels = false;
    }

    // Constructor for XP levels or points
    public XpReward(int amount, boolean isLevels) {
        super(RewardType.EXPERIENCE, isLevels ? createLevelsDescription(amount) : createPointsDescription(amount));
        this.xpAmount = amount;
        this.isLevels = isLevels;
    }

    // Create description for XP points
    private static String createPointsDescription(int points) {
        return points + " XP";
    }

    // Create description for XP levels
    private static String createLevelsDescription(int levels) {
        return levels + " XP " + (levels == 1 ? "Level" : "Levels");
    }

    @Override
    public boolean giveReward(PlayerEntity player, World world) {
        if (!canGiveReward(player)) {
            return false;
        }

        if (isLevels) {
            // Add experience levels
            player.addExperienceLevels(xpAmount);
        } else {
            // Add experience points
            player.addExperience(xpAmount);
        }

        // Send success message
        player.sendMessage(createSuccessMessage(), false);
        return true;
    }

    @Override
    public boolean canGiveReward(PlayerEntity player) {
        // XP can always be given (no inventory limitations)
        return true;
    }

    // Getters
    public int getXpAmount() { return xpAmount; }
    public boolean isLevels() { return isLevels; }

    // Static helper methods for easy creation
    public static XpReward points(int points) {
        return new XpReward(points, false);
    }

    public static XpReward levels(int levels) {
        return new XpReward(levels, true);
    }

    // Preset XP rewards for different quest difficulties
    public static XpReward novice() {
        return new XpReward(50, false); // 50 XP points
    }

    public static XpReward apprentice() {
        return new XpReward(150, false); // 150 XP points
    }

    public static XpReward expert() {
        return new XpReward(300, false); // 300 XP points
    }

    public static XpReward master() {
        return new XpReward(1, true); // 1 full level
    }

    // Override success message for XP-specific feedback
    @Override
    protected net.minecraft.text.Text createSuccessMessage() {
        if (isLevels) {
            return net.minecraft.text.Text.literal("Gained " + xpAmount + " experience " + (xpAmount == 1 ? "level!" : "levels!"));
        } else {
            return net.minecraft.text.Text.literal("Gained " + xpAmount + " experience points!");
        }
    }
}