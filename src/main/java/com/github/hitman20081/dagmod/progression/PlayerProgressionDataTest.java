package com.github.hitman20081.dagmod.progression;

import java.util.UUID;

/**
 * Test class to verify PlayerProgressionData behavior
 * Run this to validate the XP curve and leveling system
 */
public class PlayerProgressionDataTest {

    public static void main(String[] args) {

        testXPCurve();
        testLevelUps();
        testMaxLevel();
        testNBTSerialization();
        testDisplayStrings();


    }

    /**
     * Test 1: Verify XP curve progression
     */
    private static void testXPCurve() {

        int previousXP = 0;
        int totalXP = 0;

        for (int level = 1; level <= 50; level++) {
            int xpForLevel = PlayerProgressionData.calculateXPForLevel(level);
            totalXP += xpForLevel;

            double increasePercent = previousXP > 0
                    ? ((double)(xpForLevel - previousXP) / previousXP * 100)
                    : 0;

            // Print every 5 levels for readability
            if (level == 1 || level % 5 == 0 || level == 50) {
                System.out.printf("%5d | %,11d | %,8d | %+6.1f%%\n",
                        level, xpForLevel, totalXP, increasePercent);
            }

            previousXP = xpForLevel;
        }

        int totalToMax = PlayerProgressionData.calculateTotalXPForLevel(50);
        System.out.printf("\nTotal XP to reach level 50: %,d\n", totalToMax);
        System.out.printf("Estimated time at 400 XP/min: %.1f hours\n\n",
                totalToMax / 400.0 / 60.0);
    }

    /**
     * Test 2: Level up mechanics
     */
    private static void testLevelUps() {


        UUID testUUID = UUID.randomUUID();
        PlayerProgressionData data = new PlayerProgressionData(testUUID);

        // Test single level up
        int xpNeeded = data.getXPRequiredForNextLevel();
        int levelsGained = data.addXP(xpNeeded);

        System.out.printf("Added %d XP (exact), gained %d level(s)\n", xpNeeded, levelsGained);
        System.out.printf("Current level: %d, Current XP: %d\n",
                data.getCurrentLevel(), data.getCurrentXP());

        // Test multiple level ups at once
        data = new PlayerProgressionData(testUUID);
        int massiveXP = 50000; // Should level up multiple times
        levelsGained = data.addXP(massiveXP);

        System.out.printf("\nAdded %,d XP at once, gained %d level(s)\n",
                massiveXP, levelsGained);
        System.out.printf("Current level: %d, Current XP: %d\n",
                data.getCurrentLevel(), data.getCurrentXP());

        // Test partial progress
        data = new PlayerProgressionData(testUUID);
        data.addXP(50);
        System.out.printf("\nProgress with 50 XP at level 1: %d%%\n",
                data.getProgressPercentage());


    }

    /**
     * Test 3: Max level behavior
     */
    private static void testMaxLevel() {

        UUID testUUID = UUID.randomUUID();
        PlayerProgressionData data = new PlayerProgressionData(testUUID);

        // Jump to level 49
        data.setLevel(49);
        System.out.printf("Set to level 49, XP required for 50: %,d\n",
                data.getXPRequiredForNextLevel());

        // Level to 50
        data.addXP(data.getXPRequiredForNextLevel());
        System.out.printf("Leveled to 50, is max level: %b\n", data.isMaxLevel());

        // Try to add more XP
        int levelsGained = data.addXP(100000);
        System.out.printf("Added 100k XP at max level, levels gained: %d\n",
                levelsGained);
        System.out.printf("XP required for next level: %d\n",
                data.getXPRequiredForNextLevel());


    }

    /**
     * Test 4: NBT serialization
     */
    private static void testNBTSerialization() {


        UUID testUUID = UUID.randomUUID();
        PlayerProgressionData original = new PlayerProgressionData(testUUID);

        // Add some progression
        original.addXP(15000);

        System.out.printf("Original: Level %d, XP %d, Total XP %d\n",
                original.getCurrentLevel(),
                original.getCurrentXP(),
                original.getTotalXPEarned());

        // Note: This test requires Minecraft NBT classes to actually run
        // In a real test, you would do:
        // NbtCompound nbt = original.toNbt();
        // PlayerProgressionData loaded = PlayerProgressionData.fromNbt(nbt);

    }

    /**
     * Test 5: Display strings
     */
    private static void testDisplayStrings() {


        UUID testUUID = UUID.randomUUID();
        PlayerProgressionData data = new PlayerProgressionData(testUUID);

        // Test at various levels
        int[] testLevels = {1, 10, 25, 49, 50};

        for (int level : testLevels) {
            data.setLevel(level);
            data.setXP(level < 50 ? data.getXPRequiredForNextLevel() / 4 : 0);

            System.out.printf("Level %d:\n", level);
            System.out.printf("  Full: %s\n", data.getDisplayString());
            System.out.printf("  Short: %s\n\n", data.getShortDisplayString());
        }
    }

    /**
     * Bonus: Calculate XP distribution for 20-hour gameplay
     */
    public static void analyzeXPSources() {

        int totalXP = PlayerProgressionData.calculateTotalXPForLevel(50);
        int targetMinutes = 20 * 60; // 20 hours in minutes
        int xpPerMinute = totalXP / targetMinutes;

        System.out.printf("Total XP needed: %,d\n", totalXP);
        System.out.printf("Target time: %d hours (%d minutes)\n",
                targetMinutes / 60, targetMinutes);
        System.out.printf("Required XP/minute: %d\n\n", xpPerMinute);

        System.out.println("Suggested XP values:");
        System.out.println("- Mob kill (zombie): 15 XP (4 kills/min = 60 XP/min)");
        System.out.println("- Mining diamond ore: 50 XP");
        System.out.println("- Novice quest: 200 XP (5 min = 40 XP/min)");
        System.out.println("- Journeyman quest: 1000 XP (15 min = 67 XP/min)");
        System.out.println("- Boss kill (Wither): 2000 XP");
        System.out.println("\nMixed gameplay should average ~400-500 XP/minute");
    }
}