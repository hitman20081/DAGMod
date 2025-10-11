package com.github.hitman20081.dagmod.progression;

import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player progression data on the server
 * Handles loading, saving, and accessing player data
 */
public class ProgressionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("DAGMod-Progression");

    // Store all loaded player data in memory
    private static final Map<UUID, PlayerProgressionData> playerDataMap = new HashMap<>();

    /**
     * Get or create progression data for a player
     * @param player The player
     * @return Their progression data
     */
    public static PlayerProgressionData getPlayerData(ServerPlayerEntity player) {
        return getPlayerData(player.getUuid());
    }

    /**
     * Get or create progression data by UUID
     * @param uuid Player UUID
     * @return Their progression data
     */
    public static PlayerProgressionData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerProgressionData::new);
    }

    /**
     * Check if player data is loaded
     * @param uuid Player UUID
     * @return true if data exists in memory
     */
    public static boolean hasPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    /**
     * Load player data from storage
     * Called when player joins server
     * @param player The player joining
     */
    public static void loadPlayerData(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        // Try to load from file
        PlayerProgressionData data = ProgressionStorage.loadPlayerData(uuid);

        if (data != null) {
            playerDataMap.put(uuid, data);
            LOGGER.info("Loaded progression data for player {}: {}",
                    player.getName().getString(), data.getDisplayString());
        } else {
            // Create new data if file doesn't exist
            data = new PlayerProgressionData(uuid);
            playerDataMap.put(uuid, data);
            LOGGER.info("Created new progression data for player {}",
                    player.getName().getString());
        }

        // Apply level-based stats
        StatScalingHandler.applyLevelStats(player, data.getCurrentLevel());

        // Sync to client
        ProgressionPackets.sendProgressionData(player, data);
    }

    /**
     * Save player data to storage
     * Called when player leaves or server stops
     * @param player The player leaving
     */
    public static void savePlayerData(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerProgressionData data = playerDataMap.get(uuid);

        if (data != null) {
            boolean success = ProgressionStorage.savePlayerData(data);
            if (success) {
                LOGGER.info("Saved progression data for player {}: {}",
                        player.getName().getString(), data.getDisplayString());
            } else {
                LOGGER.error("Failed to save progression data for player {}",
                        player.getName().getString());
            }
        }
    }

    /**
     * Unload player data from memory
     * Called after saving when player disconnects
     * @param uuid Player UUID
     */
    public static void unloadPlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    /**
     * Save all player data
     * Called on server shutdown
     */
    public static void saveAllPlayerData() {
        LOGGER.info("Saving all player progression data...");
        int saved = 0;
        int failed = 0;

        for (PlayerProgressionData data : playerDataMap.values()) {
            boolean success = ProgressionStorage.savePlayerData(data);
            if (success) {
                saved++;
            } else {
                failed++;
            }
        }

        LOGGER.info("Saved {} player(s) progression data ({} failed)", saved, failed);
    }

    /**
     * Add XP to a player and sync to client
     * @param player The player
     * @param amount XP amount to add
     * @return Number of levels gained
     */
    public static int addXP(ServerPlayerEntity player, int amount) {
        PlayerProgressionData data = getPlayerData(player);
        int levelsGained = data.addXP(amount);

        // Sync to client
        ProgressionPackets.sendProgressionData(player, data);

        // Handle level up effects
        if (levelsGained > 0) {
            handleLevelUp(player, data, levelsGained);
        }

        return levelsGained;
    }

    /**
     * Handle level up effects (sounds, particles, healing, etc.)
     * @param player The player who leveled up
     * @param data Their progression data
     * @param levelsGained Number of levels gained
     */
    private static void handleLevelUp(ServerPlayerEntity player,
                                      PlayerProgressionData data,
                                      int levelsGained) {
        int currentLevel = data.getCurrentLevel();

        // Send level up message with stat bonuses
        String statBonus = StatScalingHandler.getStatSummary(currentLevel);
        player.sendMessage(
                net.minecraft.text.Text.literal("§6§l⚡ LEVEL UP! §eYou are now level " + currentLevel +
                        (statBonus.isEmpty() ? "" : " §7(" + statBonus + ")")),
                false
        );

        // Play level up sound
        player.playSound(
                net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.0f
        );

        // Spawn particles
        player.getEntityWorld().sendEntityStatus(player, (byte) 32); // Hearts particle effect

        // Heal player on level up (reward feeling)
        player.setHealth(player.getMaxHealth());

        // Apply new stat bonuses
        StatScalingHandler.applyLevelStats(player, currentLevel);
    }

    /**
     * Set player level directly (admin command use)
     * @param player The player
     * @param level New level
     */
    public static void setLevel(ServerPlayerEntity player, int level) {
        PlayerProgressionData data = getPlayerData(player);
        data.setLevel(level);

        // Sync to client
        ProgressionPackets.sendProgressionData(player, data);
    }

    /**
     * Reset player progression (admin command use)
     * @param player The player
     */
    public static void resetProgression(ServerPlayerEntity player) {
        PlayerProgressionData data = getPlayerData(player);
        data.reset();

        // Sync to client
        ProgressionPackets.sendProgressionData(player, data);
    }

    /**
     * Get total number of loaded players
     * @return Count of players with loaded data
     */
    public static int getLoadedPlayerCount() {
        return playerDataMap.size();
    }
}