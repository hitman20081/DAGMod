package com.github.hitman20081.dagmod.class_system.rogue;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EnergyManager {
    private static final Map<UUID, EnergyData> playerEnergyMap   = new ConcurrentHashMap<>();
    private static final Map<UUID, Float>      regenAccumulator  = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickEnergyRegen(player);
            }
        });
    }

    private static EnergyData getEnergyData(UUID playerUuid) {
        return playerEnergyMap.computeIfAbsent(playerUuid, k -> new EnergyData());
    }

    private static void tickEnergyRegen(ServerPlayer player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!playerClass.equalsIgnoreCase("rogue")) return;

        UUID uuid = player.getUUID();
        EnergyData energyData = getEnergyData(uuid);

        if (energyData.getCurrentEnergy() >= energyData.getMaxEnergy()) {
            regenAccumulator.remove(uuid);
            return;
        }

        int level = 1;
        var progData = ProgressionManager.getPlayerData(player);
        if (progData != null) level = progData.getCurrentLevel();

        float armorBonus  = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                .getEnergyRegenBonus(player);
        float perTick = calculateRegenRate(level) / 20.0f * (1.0f + armorBonus);

        float acc = regenAccumulator.getOrDefault(uuid, 0.0f) + perTick;
        if (acc >= 1.0f) {
            int toAdd = (int) acc;
            acc -= toAdd;
            energyData.addEnergy(toAdd);
            EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy(), energyData.getMaxEnergy());
        }
        regenAccumulator.put(uuid, acc);
    }

    /** Energy per second: 5 at level 1, 15 at level 200. */
    public static float calculateRegenRate(int level) {
        return 5.0f + level * 0.05f;
    }

    /** Max energy: 100 at level 1, 498 at level 200. */
    public static int calculateMaxEnergy(int level) {
        return EnergyData.BASE_MAX_ENERGY + (level - 1) * 2;
    }

    public static void updateMaxEnergyForLevel(ServerPlayer player, int level) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (!playerClass.equalsIgnoreCase("rogue")) return;

        EnergyData data = getEnergyData(player.getUUID());
        data.setMaxEnergy(calculateMaxEnergy(level));
        EnergyNetworking.syncEnergyToClient(player, data.getCurrentEnergy(), data.getMaxEnergy());
    }

    public static int getEnergy(ServerPlayer player) {
        return getEnergyData(player.getUUID()).getCurrentEnergy();
    }

    public static int getMaxEnergy(ServerPlayer player) {
        return getEnergyData(player.getUUID()).getMaxEnergy();
    }

    public static void setEnergy(ServerPlayer player, int energy) {
        EnergyData energyData = getEnergyData(player.getUUID());
        energyData.setEnergy(energy);
        EnergyNetworking.syncEnergyToClient(player, energy, energyData.getMaxEnergy());
    }

    public static void addEnergy(ServerPlayer player, int amount) {
        EnergyData energyData = getEnergyData(player.getUUID());
        energyData.addEnergy(amount);
        EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy(), energyData.getMaxEnergy());
    }

    public static boolean consumeEnergy(ServerPlayer player, int amount) {
        EnergyData energyData = getEnergyData(player.getUUID());
        boolean success = energyData.useEnergy(amount);
        if (success) {
            EnergyNetworking.syncEnergyToClient(player, energyData.getCurrentEnergy(), energyData.getMaxEnergy());
        }
        return success;
    }

    public static boolean hasEnergy(ServerPlayer player, int amount) {
        return getEnergyData(player.getUUID()).hasEnergy(amount);
    }

    public static void initializePlayerEnergy(ServerPlayer player) {
        EnergyData data = getEnergyData(player.getUUID());
        EnergyNetworking.syncEnergyToClient(player, data.getCurrentEnergy(), data.getMaxEnergy());
    }

    public static void clearPlayerEnergy(UUID playerUuid) {
        playerEnergyMap.remove(playerUuid);
        regenAccumulator.remove(playerUuid);
    }

    public static void clearPlayerData(UUID playerUuid) {
        clearPlayerEnergy(playerUuid);
    }
}
