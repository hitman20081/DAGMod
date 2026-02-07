package com.github.hitman20081.dagmod.trade;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles persistent storage of rotating trade state to NBT files.
 *
 * Storage location: world/data/dagmod/trades/rotating_trades.dat
 */
public class RotatingTradeStorage {

    private static final String DATA_ROOT = "data/dagmod";
    private static final String TRADES_FOLDER = "trades";
    private static final String TRADES_FILE = "rotating_trades.dat";

    // NBT keys
    private static final String VERSION_KEY = "version";
    private static final String LAST_ROTATION_KEY = "lastGlobalRotation";
    private static final String MERCHANTS_KEY = "merchants";
    private static final int CURRENT_VERSION = 1;

    /**
     * Get the trades data directory.
     */
    private static File getTradesDirectory(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File dagmodRoot = new File(worldDir, DATA_ROOT);
        File tradesDir = new File(dagmodRoot, TRADES_FOLDER);

        if (!tradesDir.exists()) {
            tradesDir.mkdirs();
        }

        return tradesDir;
    }

    /**
     * Get the trades data file.
     */
    private static File getTradesDataFile(MinecraftServer server) {
        return new File(getTradesDirectory(server), TRADES_FILE);
    }

    /**
     * Save rotation state to file.
     *
     * @param server The Minecraft server
     * @param lastRotationTime The timestamp of the last global rotation
     * @param merchantStates Map of merchant states
     */
    public static void saveRotationState(MinecraftServer server, long lastRotationTime,
                                         Map<MerchantType, MerchantRotationState> merchantStates) {
        try {
            File dataFile = getTradesDataFile(server);
            NbtCompound nbt = new NbtCompound();

            // Version for future migrations
            nbt.putInt(VERSION_KEY, CURRENT_VERSION);

            // Last global rotation timestamp
            nbt.putLong(LAST_ROTATION_KEY, lastRotationTime);

            // Merchant rotation states
            NbtCompound merchantsNbt = new NbtCompound();
            for (Map.Entry<MerchantType, MerchantRotationState> entry : merchantStates.entrySet()) {
                merchantsNbt.put(entry.getKey().getId(), entry.getValue().toNbt());
            }
            nbt.put(MERCHANTS_KEY, merchantsNbt);

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            DagMod.LOGGER.info("Saved rotating trade state");

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save rotating trade state", e);
        }
    }

    /**
     * Load rotation state from file.
     *
     * @param server The Minecraft server
     * @return RotationData containing timestamp and merchant states, or null if no data exists
     */
    public static RotationData loadRotationState(MinecraftServer server) {
        try {
            File dataFile = getTradesDataFile(server);

            if (!dataFile.exists()) {
                DagMod.LOGGER.info("No rotating trade data found (new world)");
                return null;
            }

            NbtCompound nbt;
            try (FileInputStream fis = new FileInputStream(dataFile)) {
                nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            }

            // Read last rotation time
            long lastRotationTime = nbt.getLong(LAST_ROTATION_KEY).orElse(System.currentTimeMillis());

            // Read merchant states
            Map<MerchantType, MerchantRotationState> merchantStates = new HashMap<>();

            if (nbt.contains(MERCHANTS_KEY)) {
                NbtCompound merchantsNbt = nbt.getCompound(MERCHANTS_KEY).orElse(new NbtCompound());
                for (MerchantType type : MerchantType.values()) {
                    if (merchantsNbt.contains(type.getId())) {
                        NbtCompound stateNbt = merchantsNbt.getCompound(type.getId()).orElse(new NbtCompound());
                        merchantStates.put(type, MerchantRotationState.fromNbt(type, stateNbt));
                    }
                }
            }

            DagMod.LOGGER.info("Loaded rotating trade state");
            return new RotationData(lastRotationTime, merchantStates);

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load rotating trade state", e);
            return null;
        }
    }

    /**
     * Container for loaded rotation data.
     */
    public static class RotationData {
        private final long lastRotationTime;
        private final Map<MerchantType, MerchantRotationState> merchantStates;

        public RotationData(long lastRotationTime, Map<MerchantType, MerchantRotationState> merchantStates) {
            this.lastRotationTime = lastRotationTime;
            this.merchantStates = merchantStates;
        }

        public long getLastRotationTime() {
            return lastRotationTime;
        }

        public Map<MerchantType, MerchantRotationState> getMerchantStates() {
            return merchantStates;
        }
    }
}
