package com.github.hitman20081.dagmod.data;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/**
 * UPDATED VERSION - Consolidates all DAGMod player data into a single directory structure
 *
 * New structure:
 * world/
 *   data/
 *     dagmod/
 *       players/         <-- All player-specific data
 *         {uuid}.dat
 *       world/           <-- World-specific data (Hall location, etc.)
 *         hall_location.dat
 *       progression/     <-- This folder will be used by ProgressionStorage
 *         {uuid}.dat
 */
public class PlayerDataManager {

    private static final String RACE_KEY = "dagmod_race";
    private static final String CLASS_KEY = "dagmod_class";
    private static final String MET_GARRICK_KEY = "dagmod_met_garrick";

    // Tutorial task tracking keys
    private static final String TASK1_COMPLETE_KEY = "dagmod_task1_complete";
    private static final String TASK2_COMPLETE_KEY = "dagmod_task2_complete";
    private static final String TASK3_COMPLETE_KEY = "dagmod_task3_complete";
    private static final String TASK2_MOB_KILLS_KEY = "dagmod_task2_mob_kills";

    // Updated path constants
    private static final String DATA_ROOT = "data/dagmod";      // Base directory
    private static final String PLAYERS_FOLDER = "players";     // Player data subfolder
    private static final String WORLD_FOLDER = "world";         // World data subfolder

    /**
     * Get the root DAGMod data directory
     */
    private static File getDataRoot(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File dagmodRoot = new File(worldDir, DATA_ROOT);

        if (!dagmodRoot.exists()) {
            dagmodRoot.mkdirs();
        }

        return dagmodRoot;
    }

    /**
     * Get the players data directory
     */
    private static File getPlayersDirectory(MinecraftServer server) {
        File dagmodRoot = getDataRoot(server);
        File playersDir = new File(dagmodRoot, PLAYERS_FOLDER);

        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }

        return playersDir;
    }

    /**
     * Get the world data directory (for hall location, etc.)
     */
    private static File getWorldDirectory(MinecraftServer server) {
        File dagmodRoot = getDataRoot(server);
        File worldDir = new File(dagmodRoot, WORLD_FOLDER);

        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }

        return worldDir;
    }

    /**
     * Get the data file for a specific player
     */
    private static File getPlayerDataFile(MinecraftServer server, UUID playerId) {
        File playersDir = getPlayersDirectory(server);
        return new File(playersDir, playerId.toString() + ".dat");
    }

    /**
     * Save player's race and class to file with backup system
     *
     * Backup flow: .tmp → .dat → .bak
     * 1. Write to temporary file (.tmp)
     * 2. If successful, backup old file to .bak (if exists)
     * 3. Replace .dat with .tmp
     * This prevents data loss on write failure
     */
    public static void savePlayerData(ServerPlayerEntity player) {
        File dataFile = null;
        File tempFile = null;
        File backupFile = null;

        try {
            dataFile = getPlayerDataFile(player.getEntityWorld().getServer(), player.getUuid());
            tempFile = new File(dataFile.getPath() + ".tmp");
            backupFile = new File(dataFile.getPath() + ".bak");

            // Prepare NBT data
            NbtCompound nbt = new NbtCompound();

            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

            nbt.putString(RACE_KEY, race);
            nbt.putString(CLASS_KEY, playerClass);

            // Save NPC interaction tracking
            nbt.putBoolean(MET_GARRICK_KEY, hasMetGarrick(player.getUuid()));

            // Save tutorial task tracking
            nbt.putBoolean(TASK1_COMPLETE_KEY, isTask1Complete(player.getUuid()));
            nbt.putBoolean(TASK2_COMPLETE_KEY, isTask2Complete(player.getUuid()));
            nbt.putBoolean(TASK3_COMPLETE_KEY, isTask3Complete(player.getUuid()));
            nbt.putInt(TASK2_MOB_KILLS_KEY, getTask2MobKills(player.getUuid()));

            // Save mana data for Mages
            if ("Mage".equals(playerClass)) {
                com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                        com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                NbtCompound manaNbt = new NbtCompound();
                manaData.writeToNbt(manaNbt);
                nbt.put("manaData", manaNbt);
            }

            // Step 1: Write to temporary file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            // Step 2: Backup old file if it exists
            if (dataFile.exists()) {
                Files.copy(dataFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Step 3: Replace main file with temp file
            Files.move(tempFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save player data for " + player.getName().getString(), e);

            // Cleanup temp file if it exists
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    DagMod.LOGGER.warn("Failed to delete temporary file: " + tempFile.getPath());
                }
            }
        }
    }

    /**
     * Load player's race and class from file with backup recovery
     */
    public static void loadPlayerData(ServerPlayerEntity player) {
        try {
            File dataFile = getPlayerDataFile(player.getEntityWorld().getServer(), player.getUuid());
            File backupFile = new File(dataFile.getPath() + ".bak");

            if (!dataFile.exists()) {
                // No data file exists (new world or new player)
                // Clear static HashMaps to ensure fresh start
                RaceSelectionAltarBlock.resetPlayerRace(player.getUuid());
                ClassSelectionAltarBlock.resetPlayerClass(player.getUuid());

                // Clear progression data (level/XP)
                com.github.hitman20081.dagmod.progression.ProgressionManager.clearPlayerData(player.getUuid());

                // Clear tutorial task tracking
                playersWhoMetGarrick.remove(player.getUuid());
                task1CompleteSet.remove(player.getUuid());
                task2CompleteSet.remove(player.getUuid());
                task3CompleteSet.remove(player.getUuid());
                task2MobKills.remove(player.getUuid());

                return; // No data to load for new players
            }

            NbtCompound nbt = null;

            // Try to load from main file
            try (FileInputStream fis = new FileInputStream(dataFile)) {
                nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            } catch (IOException mainFileError) {
                DagMod.LOGGER.error("Failed to load player data from main file for " + player.getName().getString(), mainFileError);

                // Try to load from backup file
                if (backupFile.exists()) {
                    DagMod.LOGGER.warn("Attempting to recover from backup file...");
                    try (FileInputStream fis = new FileInputStream(backupFile)) {
                        nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
                        DagMod.LOGGER.info("Successfully recovered player data from backup for " + player.getName().getString());

                        // Restore backup as main file
                        Files.copy(backupFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException backupFileError) {
                        DagMod.LOGGER.error("Failed to load from backup file as well for " + player.getName().getString(), backupFileError);
                        throw backupFileError; // Rethrow to trigger outer catch
                    }
                } else {
                    DagMod.LOGGER.error("No backup file available for recovery");
                    throw mainFileError; // Rethrow to trigger outer catch
                }
            }

            // If we still don't have data, bail out
            if (nbt == null) {
                return;
            }

            if (nbt.contains(RACE_KEY)) {
                Optional<String> raceOpt = nbt.getString(RACE_KEY);
                if (raceOpt.isPresent()) {
                    String race = raceOpt.get();
                    if (!race.equals("none") && !race.isEmpty()) {
                        RaceSelectionAltarBlock.setPlayerRace(player.getUuid(), race);
                        RaceAbilityManager.applyRaceAbilities(player);
                    }
                }
            }

            if (nbt.contains(CLASS_KEY)) {
                Optional<String> classOpt = nbt.getString(CLASS_KEY);
                if (classOpt.isPresent()) {
                    String playerClass = classOpt.get();
                    if (!playerClass.equals("none") && !playerClass.isEmpty()) {
                        ClassSelectionAltarBlock.setPlayerClass(player.getUuid(), playerClass);
                        ClassAbilityManager.applyClassAbilities(player);

                        // Load mana data for Mages
                        if ("Mage".equals(playerClass) && nbt.contains("manaData")) {
                            NbtCompound manaNbt = nbt;
                            com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                                    com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                            manaData.readFromNbt(manaNbt);
                        }
                    }
                }
            }

            // Load NPC interaction tracking
            if (nbt.contains(MET_GARRICK_KEY)) {
                Optional<Boolean> metGarrickOpt = nbt.getBoolean(MET_GARRICK_KEY);
                if (metGarrickOpt.isPresent() && metGarrickOpt.get()) {
                    markMetGarrick(player.getUuid());
                }
            }

            // Load tutorial task tracking
            if (nbt.contains(TASK1_COMPLETE_KEY)) {
                Optional<Boolean> task1Opt = nbt.getBoolean(TASK1_COMPLETE_KEY);
                if (task1Opt.isPresent() && task1Opt.get()) {
                    markTask1Complete(player.getUuid());
                }
            }
            if (nbt.contains(TASK2_COMPLETE_KEY)) {
                Optional<Boolean> task2Opt = nbt.getBoolean(TASK2_COMPLETE_KEY);
                if (task2Opt.isPresent() && task2Opt.get()) {
                    markTask2Complete(player.getUuid());
                }
            }
            if (nbt.contains(TASK3_COMPLETE_KEY)) {
                Optional<Boolean> task3Opt = nbt.getBoolean(TASK3_COMPLETE_KEY);
                if (task3Opt.isPresent() && task3Opt.get()) {
                    markTask3Complete(player.getUuid());
                }
            }
            if (nbt.contains(TASK2_MOB_KILLS_KEY)) {
                Optional<Integer> mobKillsOpt = nbt.getInt(TASK2_MOB_KILLS_KEY);
                if (mobKillsOpt.isPresent()) {
                    setTask2MobKills(player.getUuid(), mobKillsOpt.get());
                }
            }

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load player data for " + player.getName().getString(), e);
        }
    }

    private static final String HALL_LOCATION_FILE = "hall_location.dat";

    /**
     * Save the Hall of Champions location for this world
     */
    public static void saveHallLocation(MinecraftServer server, BlockPos pos) {
        try {
            File worldDir = getWorldDirectory(server);
            File hallFile = new File(worldDir, HALL_LOCATION_FILE);

            NbtCompound nbt = new NbtCompound();
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());

            try (FileOutputStream fos = new FileOutputStream(hallFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            DagMod.LOGGER.info("Saved Hall of Champions location: " + pos);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save Hall location", e);
        }
    }

    /**
     * Load the Hall of Champions location for this world
     */
    public static BlockPos loadHallLocation(MinecraftServer server) {
        try {
            File worldDir = getWorldDirectory(server);
            File hallFile = new File(worldDir, HALL_LOCATION_FILE);

            if (!hallFile.exists()) {
                return null;
            }

            NbtCompound nbt;
            try (FileInputStream fis = new FileInputStream(hallFile)) {
                nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            }

            // Handle Optional<Integer> return type
            if (!nbt.contains("x") || !nbt.contains("y") || !nbt.contains("z")) {
                return null;
            }

            Optional<Integer> xOpt = nbt.getInt("x");
            Optional<Integer> yOpt = nbt.getInt("y");
            Optional<Integer> zOpt = nbt.getInt("z");

            if (xOpt.isEmpty() || yOpt.isEmpty() || zOpt.isEmpty()) {
                return null;
            }

            int x = xOpt.get();
            int y = yOpt.get();
            int z = zOpt.get();

            return new BlockPos(x, y, z);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load Hall location", e);
            return null;
        }
    }

    /**
     * Check if player has existing data
     */
    public static boolean hasPlayerData(ServerPlayerEntity player) {
        File dataFile = getPlayerDataFile(player.getEntityWorld().getServer(), player.getUuid());
        return dataFile.exists();
    }

    // ========== NPC INTERACTION TRACKING ==========

    /**
     * In-memory cache of players who have met Garrick (session only, persisted via save/load)
     */
    private static final java.util.Set<UUID> playersWhoMetGarrick = new java.util.HashSet<>();

    /**
     * Check if a player has met Innkeeper Garrick
     */
    public static boolean hasMetGarrick(UUID playerId) {
        return playersWhoMetGarrick.contains(playerId);
    }

    /**
     * Mark that a player has met Innkeeper Garrick
     */
    public static void markMetGarrick(UUID playerId) {
        playersWhoMetGarrick.add(playerId);
    }

    /**
     * Check if a player has met Innkeeper Garrick (ServerPlayerEntity version)
     */
    public static boolean hasMetGarrick(ServerPlayerEntity player) {
        return hasMetGarrick(player.getUuid());
    }

    /**
     * Mark that a player has met Innkeeper Garrick (ServerPlayerEntity version)
     */
    public static void markMetGarrick(ServerPlayerEntity player) {
        markMetGarrick(player.getUuid());
        // Auto-save immediately
        savePlayerData(player);
    }

    // ========== TUTORIAL TASK TRACKING ==========

    /**
     * In-memory caches for tutorial task completion
     */
    private static final java.util.Set<UUID> task1CompleteSet = new java.util.HashSet<>();
    private static final java.util.Set<UUID> task2CompleteSet = new java.util.HashSet<>();
    private static final java.util.Set<UUID> task3CompleteSet = new java.util.HashSet<>();
    private static final java.util.Map<UUID, Integer> task2MobKills = new java.util.HashMap<>();

    // Task 1: Gather 10 Oak Logs
    public static boolean isTask1Complete(UUID playerId) {
        return task1CompleteSet.contains(playerId);
    }

    public static void markTask1Complete(UUID playerId) {
        task1CompleteSet.add(playerId);
    }

    public static void markTask1Complete(ServerPlayerEntity player) {
        markTask1Complete(player.getUuid());
        savePlayerData(player);
    }

    // Task 2: Kill 5 hostile mobs
    public static boolean isTask2Complete(UUID playerId) {
        return task2CompleteSet.contains(playerId);
    }

    public static void markTask2Complete(UUID playerId) {
        task2CompleteSet.add(playerId);
    }

    public static void markTask2Complete(ServerPlayerEntity player) {
        markTask2Complete(player.getUuid());
        savePlayerData(player);
    }

    public static int getTask2MobKills(UUID playerId) {
        return task2MobKills.getOrDefault(playerId, 0);
    }

    public static void setTask2MobKills(UUID playerId, int kills) {
        task2MobKills.put(playerId, kills);
    }

    public static void incrementTask2MobKills(ServerPlayerEntity player) {
        int currentKills = getTask2MobKills(player.getUuid());
        setTask2MobKills(player.getUuid(), currentKills + 1);

        // Save the kill count (Garrick will mark task complete when giving the note)
        savePlayerData(player);
    }

    // Task 3: Bring 1 Iron Ingot
    public static boolean isTask3Complete(UUID playerId) {
        return task3CompleteSet.contains(playerId);
    }

    public static void markTask3Complete(UUID playerId) {
        task3CompleteSet.add(playerId);
    }

    public static void markTask3Complete(ServerPlayerEntity player) {
        markTask3Complete(player.getUuid());
        savePlayerData(player);
    }

    /**
     * Check if player has completed all 3 tutorial tasks
     */
    public static boolean hasCompletedAllTasks(UUID playerId) {
        return isTask1Complete(playerId) && isTask2Complete(playerId) && isTask3Complete(playerId);
    }

    public static boolean hasCompletedAllTasks(ServerPlayerEntity player) {
        return hasCompletedAllTasks(player.getUuid());
    }
}