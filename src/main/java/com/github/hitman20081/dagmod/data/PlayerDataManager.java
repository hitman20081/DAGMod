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
import java.util.Optional;
import java.util.UUID;

public class PlayerDataManager {

    private static final String RACE_KEY = "dagmod_race";
    private static final String CLASS_KEY = "dagmod_class";
    private static final String DATA_FOLDER = "dagmod";

    /**
     * Get the data file for a specific player
     */
    private static File getPlayerDataFile(MinecraftServer server, UUID playerId) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File modDataDir = new File(worldDir, DATA_FOLDER);

        if (!modDataDir.exists()) {
            modDataDir.mkdirs();
        }

        return new File(modDataDir, playerId.toString() + ".dat");
    }

    /**
     * Save player's race and class to file
     */
    public static void savePlayerData(ServerPlayerEntity player) {
        try {
            File dataFile = getPlayerDataFile(player.getServer(), player.getUuid());
            NbtCompound nbt = new NbtCompound();

            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

            nbt.putString(RACE_KEY, race);
            nbt.putString(CLASS_KEY, playerClass);

            // Save mana data for Mages - ADD THIS
            if ("Mage".equals(playerClass)) {
                com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                        com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                NbtCompound manaNbt = new NbtCompound();
                manaData.writeToNbt(manaNbt);
                nbt.put("manaData", manaNbt);
            }

            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save player data for " + player.getName().getString(), e);
        }
    }

    /**
     * Load player's race and class from file
     */
    public static void loadPlayerData(ServerPlayerEntity player) {
        try {
            File dataFile = getPlayerDataFile(player.getServer(), player.getUuid());

            if (!dataFile.exists()) {
                return; // No data to load for new players
            }

            NbtCompound nbt;
            try (FileInputStream fis = new FileInputStream(dataFile)) {
                nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
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

                        // Load mana data for Mages - ADD THIS
                        if ("Mage".equals(playerClass) && nbt.contains("manaData")) {
                            NbtCompound manaNbt = nbt;
                            com.github.hitman20081.dagmod.class_system.mana.ManaData manaData =
                                    com.github.hitman20081.dagmod.class_system.mana.ManaManager.getManaData(player);
                            manaData.readFromNbt(manaNbt);
                        }
                    }
                }
            }

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load player data for " + player.getName().getString(), e);
        }
    }

    private static final String HALL_LOCATION_KEY = "hall_of_champions_location";

    /**
     * Save the Hall of Champions location for this world
     */
    public static void saveHallLocation(MinecraftServer server, BlockPos pos) {
        try {
            File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
            File modDataDir = new File(worldDir, DATA_FOLDER);

            if (!modDataDir.exists()) {
                modDataDir.mkdirs();
            }

            File hallFile = new File(modDataDir, "hall_location.dat");
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
            File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
            File modDataDir = new File(worldDir, DATA_FOLDER);
            File hallFile = new File(modDataDir, "hall_location.dat");

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

            // Declare variables OUTSIDE the if block
            int x = xOpt.get();
            int y = yOpt.get();
            int z = zOpt.get();

            return new BlockPos(x, y, z);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load Hall location", e);
            return null;
        }
    }

    public static boolean hasPlayerData(ServerPlayerEntity player) {
        File dataFile = getPlayerDataFile(player.getServer(), player.getUuid());
        return dataFile.exists();
    }
}