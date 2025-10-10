package com.github.hitman20081.dagmod.class_system.rogue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple cooldown tracking for Rogue abilities
 * Separate from Warrior system to avoid enum conflicts
 */
public class RogueCooldownData {

    // Per-player cooldown storage: UUID -> (ability key -> end time)
    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    /**
     * Start a cooldown
     */
    public static void startCooldown(UUID playerUuid, String abilityKey, long worldTime, int durationTicks) {
        long endTime = worldTime + durationTicks;
        cooldowns.computeIfAbsent(playerUuid, k -> new HashMap<>()).put(abilityKey, endTime);
    }

    /**
     * Check if ability is on cooldown
     */
    public static boolean isOnCooldown(UUID playerUuid, String abilityKey, long currentWorldTime) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerUuid);
        if (playerCooldowns == null) {
            return false;
        }

        Long endTime = playerCooldowns.get(abilityKey);
        if (endTime == null) {
            return false;
        }

        if (currentWorldTime >= endTime) {
            // Cooldown expired
            playerCooldowns.remove(abilityKey);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown in ticks
     */
    public static long getRemainingCooldown(UUID playerUuid, String abilityKey, long currentWorldTime) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerUuid);
        if (playerCooldowns == null) {
            return 0;
        }

        Long endTime = playerCooldowns.get(abilityKey);
        if (endTime == null) {
            return 0;
        }

        long remaining = endTime - currentWorldTime;
        return Math.max(0, remaining);
    }

    /**
     * Clear all cooldowns for a player
     */
    public static void clearPlayerCooldowns(UUID playerUuid) {
        cooldowns.remove(playerUuid);
    }
}