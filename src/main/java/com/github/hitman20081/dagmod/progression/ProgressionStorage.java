package com.github.hitman20081.dagmod.progression;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * UPDATED VERSION - Handles saving and loading player progression data to/from NBT files
 * Now uses consolidated path structure:
 *
 * world/
 *   data/
 *     dagmod/
 *       progression/     <-- Progression data here
 *         {uuid}.dat
 *       players/         <-- Player race/class data (managed by PlayerDataManager)
 *       world/           <-- World data (managed by PlayerDataManager)
 */
public class ProgressionStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger("DAGMod-ProgressionStorage");
    private static MinecraftServer server;

    // Updated path constants to match PlayerDataManager
    private static final String DATA_ROOT = "data/dagmod";
    private static final String PROGRESSION_FOLDER = "progression";

    /**
     * Initialize storage with server instance
     * Call this during server start
     * @param minecraftServer The server instance
     */
    public static void initialize(MinecraftServer minecraftServer) {
        server = minecraftServer;
        ensureDirectoryExists();
        LOGGER.info("Progression storage initialized with consolidated path structure");
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
        File dagmodDir = new File(worldDir, DATA_ROOT);
        File progressionDir = new File(dagmodDir, PROGRESSION_FOLDER);

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

            try (FileOutputStream fos = new FileOutputStream(file)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            LOGGER.debug("Saved progression data for player {}: Level {} | XP {}/{}",
                    data.getPlayerUUID(),
                    data.getCurrentLevel(),
                    data.getCurrentXP(),
                    data.getXPRequiredForNextLevel());
            return true;

        } catch (IOException e) {
            LOGGER.error("Failed to save progression data for player {}", data.getPlayerUUID(), e);
            return false;
        }
    }

    /**
     * Load player progression data from file
     * @param uuid Player UUID
     * @return Loaded data, or new data if file doesn't exist
     */
    public static PlayerProgressionData loadPlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);

        if (!file.exists()) {
            LOGGER.debug("No progression data found for player {}, creating new data", uuid);
            return new PlayerProgressionData(uuid);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            NbtCompound nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            PlayerProgressionData data = PlayerProgressionData.fromNbt(nbt);

            LOGGER.debug("Loaded progression data for player {}: Level {} | XP {}/{}",
                    uuid,
                    data.getCurrentLevel(),
                    data.getCurrentXP(),
                    data.getXPRequiredForNextLevel());
            return data;

        } catch (IOException e) {
            LOGGER.error("Failed to load progression data for player {}, creating new data", uuid, e);
            return new PlayerProgressionData(uuid);
        }
    }

    /**
     * Check if player has existing progression data
     * @param uuid Player UUID
     * @return true if data file exists
     */
    public static boolean hasPlayerData(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    /**
     * Delete player progression data
     * @param uuid Player UUID
     * @return true if deletion was successful
     */
    public static boolean deletePlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                LOGGER.info("Deleted progression data for player {}", uuid);
            }
            return deleted;
        }
        return false;
    }
}