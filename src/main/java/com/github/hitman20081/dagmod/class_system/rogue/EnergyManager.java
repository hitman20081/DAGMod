package com.github.hitman20081.dagmod.class_system.rogue;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages energy for Rogue players
 * Stores energy per-player in memory (like the mana system)
 */
public class EnergyManager {
    private static final Map<UUID, EnergyData> playerEnergyMap = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> regenTickCounter = new ConcurrentHashMap<>();
    private static final int TICKS_PER_ENERGY = 4; // 5 energy/sec = 1 energy per 4 ticks

    /**
     * Initialize energy management system
     */
    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickEnergyRegen(player);
            }
        });
    }

    /**
     * Get or create energy data for a player
     */
    private static EnergyData getEnergyData(UUID playerUuid) {
        return playerEnergyMap.computeIfAbsent(playerUuid, k -> new EnergyData());
    }

    /**
     * Handle energy regeneration for a player
     */
    private static void tickEnergyRegen(ServerPlayer player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        // Only regenerate for Rogue players (case-insensitive)
        if (!playerClass.equalsIgnoreCase("rogue")) {
            return;
        }

        UUID uuid = player.getUUID();
        EnergyData energyData = getEnergyData(uuid);

        // Check if already at max energy
        if (energyData.getCurrentEnergy() >= energyData.getMaxEnergy()) {
            regenTickCounter.remove(uuid);
            return;
        }

        // Calculate energy regen with armor bonus
        // Base: 1 energy per 4 ticks = 5 energy/second
        float armorBonus = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                .getEnergyRegenBonus(player);
        int ticksNeeded = (int)(TICKS_PER_ENERGY / (1.0f + armorBonus));

        // Ensure minimum of 1 tick (prevent division by zero or negative)
        ticksNeeded = Math.max(1, ticksNeeded);

        // Increment tick counter
        int ticks = regenTickCounter.getOrDefault(uuid, 0) + 1;

        if (ticks >= ticksNeeded) {
            // Regenerate 1 energy
            energyData.addEnergy(1);

            // Sync to client
            EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy());

            ticks = 0;
        }

        regenTickCounter.put(uuid, ticks);
    }

    /**
     * Get a player's current energy
     */
    public static int getEnergy(ServerPlayer player) {
        return getEnergyData(player.getUUID()).getCurrentEnergy();
    }

    /**
     * Get max energy
     */
    public static int getMaxEnergy() {
        return 100;
    }

    /**
     * Set a player's energy
     */
    public static void setEnergy(ServerPlayer player, int energy) {
        EnergyData energyData = getEnergyData(player.getUUID());
        energyData.setEnergy(energy);
        EnergyNetworking.syncEnergyToClient(player, energy);
    }

    /**
     * Add energy to a player
     */
    public static void addEnergy(ServerPlayer player, int amount) {
        EnergyData energyData = getEnergyData(player.getUUID());
        energyData.addEnergy(amount);
        EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy());
    }

    /**
     * Try to consume energy from a player
     * Returns true if successful, false if insufficient energy
     */
    public static boolean consumeEnergy(ServerPlayer player, int amount) {
        EnergyData energyData = getEnergyData(player.getUUID());
        boolean success = energyData.useEnergy(amount);

        if (success) {
            EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy());
        }

        return success;
    }

    /**
     * Check if player has enough energy
     */
    public static boolean hasEnergy(ServerPlayer player, int amount) {
        return getEnergyData(player.getUUID()).hasEnergy(amount);
    }

    /**
     * Initialize energy for a new Rogue player
     */
    public static void initializePlayerEnergy(ServerPlayer player) {
        setEnergy(player, getMaxEnergy());
    }

    /**
     * Clear energy data for a player (on disconnect)
     */
    public static void clearPlayerEnergy(UUID playerUuid) {
        playerEnergyMap.remove(playerUuid);
        regenTickCounter.remove(playerUuid);
    }

    /**
     * Alias for clearPlayerEnergy - consistent naming with other managers
     */
    public static void clearPlayerData(UUID playerUuid) {
        clearPlayerEnergy(playerUuid);
    }
}