package com.github.hitman20081.dagmod.progression;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Handles saving and loading player progression data to/from NBT files
 * Files stored in: world/data/dagmod/progression/<uuid>.dat
 */
public class ProgressionStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger("DAGMod-Storage");
    private static MinecraftServer server;

    /**
     * Initialize storage with server instance
     * Call this during server start
     * @param minecraftServer The server instance
     */
    public static void initialize(MinecraftServer minecraftServer) {
        server = minecraftServer;
        ensureDirectoryExists();
    }

    /**
     * Get the progression data directory
     * @return File pointing to progression data folder
     */
    private static File getProgressionDirectory() {
        if (server == null) {
            throw new IllegalStateException("ProgressionStorage not initialized! Call initialize() first.");
        }

        // Get world save path
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File dagmodDir = new File(worldDir, "data/dagmod");
        File progressionDir = new File(dagmodDir, "progression");

        return progressionDir;
    }

    /**
     * Ensure the progression directory exists
     */
    private static void ensureDirectoryExists() {
        File dir = getProgressionDirectory();
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                LOGGER.info("Created progression data directory: {}", dir.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create progression data directory: {}", dir.getAbsolutePath());
            }
        }
    }

    /**
     * Get the file for a specific player's data
     * @param uuid Player UUID
     * @return File for that player's progression data
     */
    private static File getPlayerFile(UUID uuid) {
        File dir = getProgressionDirectory();
        return new File(dir, uuid.toString() + ".dat");
    }

    /**
     * Save player progression data to file
     * @param data The progression data to save
     * @return true if save was successful
     */
    public static boolean savePlayerData(PlayerProgressionData data) {
        try {
            File file = getPlayerFile(data.getPlayerUUID());
            NbtCompound nbt = data.toNbt();

            // Write to temporary file first (safer)
            File tempFile = new File(file.getAbsolutePath() + ".tmp");
            NbtIo.writeCompressed(nbt, tempFile.toPath());

            // Delete old file if exists
            if (file.exists()) {
                file.delete();
            }

            // Rename temp file to actual file
            boolean renamed = tempFile.renameTo(file);

            if (!renamed) {
                LOGGER.error("Failed to rename temp file to {}", file.getName());
                return false;
            }

            return true;

        } catch (IOException e) {
            LOGGER.error("Failed to save progression data for {}", data.getPlayerUUID(), e);
            return false;
        }
    }

    /**
     * Load player progression data from file
     * @param uuid Player UUID
     * @return Loaded data, or null if file doesn't exist
     */
    public static PlayerProgressionData loadPlayerData(UUID uuid) {
        try {
            File file = getPlayerFile(uuid);

            if (!file.exists()) {
                // No saved data for this player
                return null;
            }

            NbtCompound nbt = NbtIo.readCompressed(file.toPath(), net.minecraft.nbt.NbtSizeTracker.ofUnlimitedBytes());
            PlayerProgressionData data = PlayerProgressionData.fromNbt(nbt);

            LOGGER.debug("Loaded progression data from {}", file.getName());
            return data;

        } catch (IOException e) {
            LOGGER.error("Failed to load progression data for {}", uuid, e);
            return null;
        } catch (Exception e) {
            LOGGER.error("Corrupted progression data for {}, creating new data", uuid, e);
            return null;
        }
    }

    /**
     * Check if saved data exists for a player
     * @param uuid Player UUID
     * @return true if save file exists
     */
    public static boolean hasPlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);
        return file.exists();
    }

    /**
     * Delete player progression data (admin use)
     * @param uuid Player UUID
     * @return true if deletion was successful
     */
    public static boolean deletePlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                LOGGER.info("Deleted progression data for {}", uuid);
            }
            return deleted;
        }
        return false;
    }

    /**
     * Get the number of saved player data files
     * @return Count of .dat files in progression directory
     */
    public static int getSavedPlayerCount() {
        File dir = getProgressionDirectory();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
        return files != null ? files.length : 0;
    }
}