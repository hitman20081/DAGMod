package com.github.hitman20081.dagmod.trade;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.server.MinecraftServer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central manager for the rotating trade system.
 * Tracks rotation state for all merchant types and handles rotation timing.
 *
 * Follows the singleton pattern like QuestManager and ProgressionManager.
 */
public class RotatingTradeManager {
    // Singleton instance
    private static RotatingTradeManager instance;

    // Server reference for saving
    private MinecraftServer server;

    // Last time a global rotation occurred (real-world milliseconds)
    private long lastRotationTime;

    // Rotation state for each merchant type
    private final Map<MerchantType, MerchantRotationState> merchantStates;

    // Tick counter for periodic rotation checks
    private int tickCounter = 0;

    // Whether the manager has been initialized
    private boolean initialized = false;

    private RotatingTradeManager() {
        this.merchantStates = new EnumMap<>(MerchantType.class);
        this.lastRotationTime = System.currentTimeMillis();

        // Initialize default states for all merchant types
        for (MerchantType type : MerchantType.values()) {
            merchantStates.put(type, new MerchantRotationState(type));
        }
    }

    /**
     * Get the singleton instance.
     */
    public static RotatingTradeManager getInstance() {
        if (instance == null) {
            instance = new RotatingTradeManager();
        }
        return instance;
    }

    /**
     * Initialize the manager with a server reference and load persisted state.
     * Called on SERVER_STARTING event.
     *
     * @param server The Minecraft server
     */
    public void initialize(MinecraftServer server) {
        this.server = server;

        // Load persisted state
        RotatingTradeStorage.RotationData data = RotatingTradeStorage.loadRotationState(server);
        if (data != null) {
            this.lastRotationTime = data.getLastRotationTime();

            // Merge loaded states with defaults
            for (Map.Entry<MerchantType, MerchantRotationState> entry : data.getMerchantStates().entrySet()) {
                merchantStates.put(entry.getKey(), entry.getValue());
            }
        }

        // Check if we need to catch up on rotations (server was offline)
        checkAndApplyRotations();

        this.initialized = true;
        DagMod.LOGGER.info("RotatingTradeManager initialized");
    }

    /**
     * Called on each server tick to check for rotation updates.
     * Only checks every minute to reduce overhead.
     */
    public void tick() {
        if (!initialized || server == null) {
            return;
        }

        tickCounter++;
        if (tickCounter >= TradeConfig.getCheckIntervalTicks()) {
            tickCounter = 0;
            checkAndApplyRotations();
        }
    }

    /**
     * Check if enough time has passed for a rotation and apply if needed.
     */
    private void checkAndApplyRotations() {
        long currentTime = System.currentTimeMillis();
        long rotationInterval = TradeConfig.getRotationIntervalMillis();
        long timeSinceLastRotation = currentTime - lastRotationTime;

        // Calculate how many rotations should have occurred
        int rotationsToApply = (int) (timeSinceLastRotation / rotationInterval);

        if (rotationsToApply > 0) {
            // Apply rotations for each merchant
            for (MerchantType type : MerchantType.values()) {
                int maxRotations = RotatingTradeRegistry.getRotationCount(type);
                if (maxRotations > 0) {
                    MerchantRotationState state = merchantStates.get(type);
                    for (int i = 0; i < rotationsToApply; i++) {
                        state.advanceRotation(maxRotations);
                    }
                }
            }

            // Update the last rotation time
            lastRotationTime = lastRotationTime + (rotationsToApply * rotationInterval);

            DagMod.LOGGER.info("Applied " + rotationsToApply + " trade rotation(s)");

            // Save state after rotation
            saveState();
        }
    }

    /**
     * Get the current rotation index for a merchant type.
     *
     * @param type The merchant type
     * @return The current rotation index
     */
    public int getRotationIndex(MerchantType type) {
        MerchantRotationState state = merchantStates.get(type);
        return state != null ? state.getCurrentRotationIndex() : 0;
    }

    /**
     * Get time until next rotation in milliseconds.
     */
    public long getTimeUntilNextRotation() {
        long rotationInterval = TradeConfig.getRotationIntervalMillis();
        long timeSinceLastRotation = System.currentTimeMillis() - lastRotationTime;
        return Math.max(0, rotationInterval - timeSinceLastRotation);
    }

    /**
     * Get time until next rotation as a formatted string.
     */
    public String getTimeUntilNextRotationFormatted() {
        long millis = getTimeUntilNextRotation();
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%dh %dm", hours, minutes);
    }

    /**
     * Force an immediate rotation of all merchant trades.
     */
    public void forceRotation() {
        for (MerchantType type : MerchantType.values()) {
            int maxRotations = RotatingTradeRegistry.getRotationCount(type);
            if (maxRotations > 0) {
                MerchantRotationState state = merchantStates.get(type);
                state.advanceRotation(maxRotations);
            }
        }
        lastRotationTime = System.currentTimeMillis();
        saveState();
        DagMod.LOGGER.info("Forced trade rotation for all merchants");
    }

    /**
     * Get merchant states map (for debug display).
     */
    public Map<MerchantType, MerchantRotationState> getMerchantStates() {
        return merchantStates;
    }

    /**
     * Save the current state to disk.
     * Called on SERVER_STOPPING and after rotations.
     */
    public void saveState() {
        if (server != null) {
            RotatingTradeStorage.saveRotationState(server, lastRotationTime, merchantStates);
        }
    }

    /**
     * Shutdown the manager. Called on SERVER_STOPPING.
     */
    public void shutdown() {
        saveState();
        this.server = null;
        this.initialized = false;
        DagMod.LOGGER.info("RotatingTradeManager shutdown");
    }

    /**
     * Check if the manager is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}
