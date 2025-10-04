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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
                String race = String.valueOf(nbt.getString(RACE_KEY));
                if (!race.equals("none") && !race.isEmpty()) {
                    RaceSelectionAltarBlock.setPlayerRace(player.getUuid(), race);
                    RaceAbilityManager.applyRaceAbilities(player);
                }
            }

            if (nbt.contains(CLASS_KEY)) {
                String playerClass = String.valueOf(nbt.getString(CLASS_KEY));
                if (!playerClass.equals("none") && !playerClass.isEmpty()) {
                    ClassSelectionAltarBlock.setPlayerClass(player.getUuid(), playerClass);
                    ClassAbilityManager.applyClassAbilities(player);
                }
            }

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load player data for " + player.getName().getString(), e);
        }
    }

    public static boolean hasPlayerData(ServerPlayerEntity player) {
        File dataFile = getPlayerDataFile(player.getServer(), player.getUuid());
        return dataFile.exists();
    }
}