package com.github.hitman20081.dagmod.trade;

/**
 * Configuration settings for the rotating trade system.
 * Values can be adjusted by server admins.
 */
public class TradeConfig {
    // Default rotation interval: 72 hours
    private static int rotationIntervalHours = 72;

    // Minimum rotation interval: 12 hours
    public static final int MIN_INTERVAL_HOURS = 12;

    // Maximum rotation interval: 7 days (168 hours)
    public static final int MAX_INTERVAL_HOURS = 168;

    /**
     * Get the rotation interval in hours.
     */
    public static int getRotationIntervalHours() {
        return rotationIntervalHours;
    }

    /**
     * Set the rotation interval in hours.
     * Value will be clamped to valid range.
     *
     * @param hours The interval in hours
     */
    public static void setRotationIntervalHours(int hours) {
        rotationIntervalHours = Math.max(MIN_INTERVAL_HOURS, Math.min(MAX_INTERVAL_HOURS, hours));
    }

    /**
     * Get the rotation interval in milliseconds.
     */
    public static long getRotationIntervalMillis() {
        return (long) rotationIntervalHours * 60L * 60L * 1000L;
    }

    /**
     * Get the rotation check interval in ticks.
     * Checks every 1 minute (1200 ticks).
     */
    public static int getCheckIntervalTicks() {
        return 1200; // 1 minute = 20 ticks/sec * 60 sec
    }
}
