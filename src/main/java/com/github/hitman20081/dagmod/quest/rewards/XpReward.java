package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Grants XP through ProgressionManager -- the mod's own 200-level system that drives HP/attack/
 * armor bonuses and dimension gates -- rather than vanilla enchanting XP. Quests only ever
 * complete server-side, so `player` is always a ServerPlayer in practice; the instanceof guard is
 * defensive rather than an expected fallback path.
 */
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

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ProgressionManager.addXP(serverPlayer, resolvePoints(serverPlayer));

        // Send success message
        player.sendOverlayMessage(createSuccessMessage());
        return true;
    }

    /**
     * Converts this reward into raw XP points for ProgressionManager.addXP(). Points-mode rewards
     * pass straight through; levels-mode rewards (Master tier) sum the actual XP cost of each of
     * the next N levels from the player's current position, so "1 level" stays proportional to
     * where they are on the curve instead of degrading into a flat point value the way the other
     * tiers do.
     */
    private int resolvePoints(ServerPlayer player) {
        return isLevels ? sumLevelPoints(player, xpAmount) : xpAmount;
    }

    /** Sums the XP cost of the next {@code levelCount} levels from the player's current position. */
    private static int sumLevelPoints(ServerPlayer player, int levelCount) {
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);
        int fromLevel = data != null ? data.getCurrentLevel() : 1;

        int totalPoints = 0;
        for (int i = 1; i <= levelCount; i++) {
            totalPoints += PlayerProgressionData.calculateXPForLevel(fromLevel + i);
        }
        return totalPoints;
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
    public boolean giveScaledReward(Player player, Level world, float multiplier) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        int scaled = Math.max(1, Math.round(xpAmount * multiplier));
        int points = isLevels ? sumLevelPoints(serverPlayer, scaled) : scaled;
        ProgressionManager.addXP(serverPlayer, points);

        player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("Gained " + scaled + (isLevels ? " experience levels!" : " experience points!")));
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