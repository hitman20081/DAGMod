package com.github.hitman20081.dagmod.progression;

import net.minecraft.nbt.NbtCompound;
import java.util.UUID;

/**
 * Stores and manages player progression data (XP, Level, Stats)
 *
 * XP Curve Design:
 * - Level 1→2: 100 XP
 * - Each level requires ~15% more XP than previous
 * - Level 49→50: ~48,000 XP
 * - Total XP to reach level 50: ~500,000 XP
 * - Designed for ~20 hours of gameplay
 */
public class PlayerProgressionData {

    // Constants
    public static final int MAX_LEVEL = 50;
    public static final int MIN_LEVEL = 1;
    private static final int BASE_XP_REQUIREMENT = 100;
    private static final double XP_MULTIPLIER = 1.15;

    // Player identifier
    private final UUID playerUUID;

    // Progression data
    private int currentXP;
    private int currentLevel;
    private long totalXPEarned; // Track lifetime XP for statistics

    /**
     * Create new progression data for a player
     * @param playerUUID The player's unique identifier
     */
    public PlayerProgressionData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.currentXP = 0;
        this.currentLevel = 1;
        this.totalXPEarned = 0;
    }

    /**
     * Add XP to the player and handle level-ups
     * @param amount Amount of XP to add (must be positive)
     * @return Number of levels gained (0 if no level up)
     */
    public int addXP(int amount) {
        if (amount <= 0) {
            return 0;
        }

        // Don't gain XP at max level
        if (currentLevel >= MAX_LEVEL) {
            return 0;
        }

        currentXP += amount;
        totalXPEarned += amount;

        int levelsGained = 0;

        // Check for level ups (can level up multiple times)
        while (canLevelUp() && currentLevel < MAX_LEVEL) {
            levelUp();
            levelsGained++;
        }

        return levelsGained;
    }

    /**
     * Remove XP from the player (for death penalties, etc.)
     * @param amount Amount of XP to remove
     */
    public void removeXP(int amount) {
        currentXP = Math.max(0, currentXP - amount);
    }

    /**
     * Check if player has enough XP to level up
     * @return true if player can level up
     */
    public boolean canLevelUp() {
        if (currentLevel >= MAX_LEVEL) {
            return false;
        }
        return currentXP >= getXPRequiredForNextLevel();
    }

    /**
     * Level up the player (private - called by addXP)
     */
    private void levelUp() {
        int xpRequired = getXPRequiredForNextLevel();
        currentXP -= xpRequired;
        currentLevel++;
    }

    /**
     * Calculate XP required to reach the next level
     * Uses exponential curve: baseXP * (multiplier ^ (level - 1))
     * @return XP needed for next level
     */
    public int getXPRequiredForNextLevel() {
        if (currentLevel >= MAX_LEVEL) {
            return 0;
        }
        return calculateXPForLevel(currentLevel + 1);
    }

    /**
     * Calculate total XP required to reach a specific level from level 1
     * @param level Target level
     * @return Total XP needed to reach that level
     */
    public static int calculateXPForLevel(int level) {
        if (level <= 1) {
            return 0;
        }

        // Formula: BASE_XP * (MULTIPLIER ^ (level - 1))
        return (int) (BASE_XP_REQUIREMENT * Math.pow(XP_MULTIPLIER, level - 2));
    }

    /**
     * Calculate total XP needed to go from level 1 to target level
     * @param targetLevel The level to calculate for
     * @return Total cumulative XP
     */
    public static int calculateTotalXPForLevel(int targetLevel) {
        int totalXP = 0;
        for (int i = 2; i <= targetLevel; i++) {
            totalXP += calculateXPForLevel(i);
        }
        return totalXP;
    }

    /**
     * Get progress to next level as a percentage
     * @return Progress from 0.0 to 1.0 (0% to 100%)
     */
    public double getProgressToNextLevel() {
        if (currentLevel >= MAX_LEVEL) {
            return 1.0;
        }

        int xpRequired = getXPRequiredForNextLevel();
        if (xpRequired == 0) {
            return 1.0;
        }

        return Math.min(1.0, (double) currentXP / xpRequired);
    }

    /**
     * Get progress to next level as a percentage (0-100)
     * @return Progress from 0 to 100
     */
    public int getProgressPercentage() {
        return (int) (getProgressToNextLevel() * 100);
    }

    /**
     * Set player level directly (admin/debug use)
     * @param level New level (clamped to valid range)
     */
    public void setLevel(int level) {
        this.currentLevel = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
        this.currentXP = 0; // Reset XP when setting level
    }

    /**
     * Set current XP directly (admin/debug use)
     * @param xp New XP amount
     */
    public void setXP(int xp) {
        this.currentXP = Math.max(0, xp);
    }

    /**
     * Reset player progression to level 1
     */
    public void reset() {
        this.currentLevel = 1;
        this.currentXP = 0;
        // Note: totalXPEarned is not reset (lifetime statistic)
    }

    // ========== GETTERS ==========

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getCurrentXP() {
        return currentXP;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public long getTotalXPEarned() {
        return totalXPEarned;
    }

    public boolean isMaxLevel() {
        return currentLevel >= MAX_LEVEL;
    }

    // ========== NBT SERIALIZATION ==========

    /**
     * Save progression data to NBT
     * @return NBT compound with all progression data
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("CurrentXP", currentXP);
        nbt.putInt("CurrentLevel", currentLevel);
        nbt.putLong("TotalXPEarned", totalXPEarned);
        nbt.putString("PlayerUUID", playerUUID.toString());
        return nbt;
    }

    /**
     * Load progression data from NBT
     * @param nbt NBT compound containing progression data
     * @return New PlayerProgressionData instance
     */
    public static PlayerProgressionData fromNbt(NbtCompound nbt) {
        UUID uuid = UUID.fromString(nbt.getString("PlayerUUID").orElse(""));
        PlayerProgressionData data = new PlayerProgressionData(uuid);

        data.currentXP = nbt.getInt("CurrentXP").orElse(0);
        data.currentLevel = nbt.getInt("CurrentLevel").orElse(1);
        data.totalXPEarned = nbt.getLong("TotalXPEarned").orElse(0L);

        // Validate data
        data.currentLevel = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, data.currentLevel));
        data.currentXP = Math.max(0, data.currentXP);

        return data;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get formatted string for display
     * Example: "Level 15 | 1,234/5,000 XP (24%)"
     */
    public String getDisplayString() {
        if (isMaxLevel()) {
            return String.format("Level %d (MAX)", currentLevel);
        }

        return String.format("Level %d | %,d/%,d XP (%d%%)",
                currentLevel,
                currentXP,
                getXPRequiredForNextLevel(),
                getProgressPercentage()
        );
    }

    /**
     * Get short display string for action bar
     * Example: "Lv15 [====    ] 24%"
     */
    public String getShortDisplayString() {
        if (isMaxLevel()) {
            return String.format("Lv%d [MAX]", currentLevel);
        }

        int bars = (int) (getProgressToNextLevel() * 10);
        StringBuilder barString = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            barString.append(i < bars ? "=" : " ");
        }
        barString.append("]");

        return String.format("Lv%d %s %d%%", currentLevel, barString, getProgressPercentage());
    }

    @Override
    public String toString() {
        return String.format("PlayerProgressionData{uuid=%s, level=%d, xp=%d, totalXP=%d}",
                playerUUID, currentLevel, currentXP, totalXPEarned);
    }
}