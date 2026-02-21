package com.github.hitman20081.dagmod.progression;

import com.github.hitman20081.dagmod.quest.Quest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Manages level requirements for quests and content
 */
public class LevelRequirements {

    // Level requirements for race quest tiers
    public static final int RACE_QUEST_TIER_1 = 1;   // Available immediately
    public static final int RACE_QUEST_TIER_2 = 10;  // First major milestone
    public static final int RACE_QUEST_TIER_3 = 20;  // Mid-game
    public static final int RACE_QUEST_TIER_4 = 30;  // Late-game
    public static final int RACE_QUEST_TIER_5 = 40;  // End-game

    // Quest difficulty level requirements
    public static final int APPRENTICE_QUESTS = 5;
    public static final int EXPERT_QUESTS = 15;
    public static final int MASTER_QUESTS = 25;

    /**
     * Check if player meets level requirement for a quest
     * @param player The player
     * @param quest The quest to check
     * @return true if player's level is high enough
     */
    public static boolean meetsLevelRequirement(ServerPlayerEntity player, Quest quest) {
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);
        if (data == null) {
            return false; // Data not loaded yet, deny access
        }
        int playerLevel = data.getCurrentLevel();
        int requiredLevel = getRequiredLevelForQuest(quest);

        return playerLevel >= requiredLevel;
    }

    /**
     * Get the required level for a specific quest
     * @param quest The quest
     * @return Minimum level required
     */
    public static int getRequiredLevelForQuest(Quest quest) {
        // Check quest difficulty first
        switch (quest.getDifficulty()) {
            case NOVICE -> {
                return 1; // No level requirement
            }
            case APPRENTICE -> {
                return APPRENTICE_QUESTS;
            }
            case EXPERT -> {
                return EXPERT_QUESTS;
            }
            case MASTER -> {
                return MASTER_QUESTS;
            }
        }

        return 1; // Default: no requirement
    }

    /**
     * Get required level for a race quest tier
     * Use this when creating race-specific quests
     * @param tier Quest tier (1-5)
     * @return Required level
     */
    public static int getRequiredLevelForRaceQuestTier(int tier) {
        return switch (tier) {
            case 1 -> RACE_QUEST_TIER_1;
            case 2 -> RACE_QUEST_TIER_2;
            case 3 -> RACE_QUEST_TIER_3;
            case 4 -> RACE_QUEST_TIER_4;
            case 5 -> RACE_QUEST_TIER_5;
            default -> 1;
        };
    }

    /**
     * Send level requirement message to player
     * @param player The player
     * @param requiredLevel Level needed
     */
    public static void sendLevelRequirementMessage(ServerPlayerEntity player, int requiredLevel) {
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);
        if (data == null) {
            return; // Data not loaded yet, skip message
        }
        int currentLevel = data.getCurrentLevel();
        int levelsNeeded = requiredLevel - currentLevel;

        player.sendMessage(
                Text.literal("§c⚠ Level " + requiredLevel + " required! " +
                        "§7(You need " + levelsNeeded + " more level" +
                        (levelsNeeded > 1 ? "s" : "") + ")"),
                false
        );
    }

    /**
     * Check if player can access content at a specific level
     * @param player The player
     * @param requiredLevel Minimum level needed
     * @return true if player's level is high enough
     */
    public static boolean canAccessContent(ServerPlayerEntity player, int requiredLevel) {
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);
        if (data == null) {
            return false; // Data not loaded yet, deny access
        }
        return data.getCurrentLevel() >= requiredLevel;
    }

    /**
     * Get a formatted display string for level requirements
     * @param requiredLevel The level requirement
     * @return Formatted string like "[Lv.10+]"
     */
    public static String getRequirementDisplay(int requiredLevel) {
        if (requiredLevel <= 1) {
            return "";
        }
        return "§7[§eLv." + requiredLevel + "+§7] ";
    }
}