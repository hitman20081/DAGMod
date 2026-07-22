package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public boolean giveReward(Player player, Level world) {
        if (!canGiveReward(player)) {
            return false;
        }

        if (isLevels) {
            // Add experience levels
            player.giveExperienceLevels(xpAmount);
        } else {
            // Add experience points
            player.giveExperiencePoints(xpAmount);
        }

        // Send success message
        player.sendOverlayMessage(createSuccessMessage());
        return true;
    }

    @Override
    public boolean canGiveReward(Player player) {
        // XP can always be given (no inventory limitations)
        return true;
    }

    // Getters
    public int getXpAmount() { return xpAmount; }
    public boolean isLevels() { return isLevels; }

    // Static helper methods for easy creation
    public static XpReward points(int points) {
        return new XpReward(points);
    }

    public static XpReward levels(int levels) {
        return new XpReward(levels);
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

    /** Give XP scaled by a multiplier (used for daily quest streak/level bonuses). */
    public boolean giveScaledReward(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level world, float multiplier) {
        if (isLevels) {
            player.giveExperienceLevels(Math.max(1, Math.round(xpAmount * multiplier)));
        } else {
            player.giveExperiencePoints(Math.max(1, Math.round(xpAmount * multiplier)));
        }
        int scaled = Math.max(1, Math.round(xpAmount * multiplier));
        player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("Gained " + scaled + " experience points!"));
        return true;
    }

    // Override success message for XP-specific feedback
    @Override
    protected net.minecraft.network.chat.Component createSuccessMessage() {
        if (isLevels) {
            return net.minecraft.network.chat.Component.literal("Gained " + xpAmount + " experience " + (xpAmount == 1 ? "level!" : "levels!"));
        } else {
            return net.minecraft.network.chat.Component.literal("Gained " + xpAmount + " experience points!");
        }
    }
}