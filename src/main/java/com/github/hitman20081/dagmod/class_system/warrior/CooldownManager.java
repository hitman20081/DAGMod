package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns for Warrior abilities
 * Tracks per-player cooldowns for each ability type
 */
public class CooldownManager {
    // Cooldown durations in ticks (20 ticks = 1 second)
    public static final int RAGE_COOLDOWN = 60 * 20; // 60 seconds
    public static final int SHIELD_BASH_COOLDOWN = 15 * 20; // 15 seconds
    public static final int WAR_CRY_COOLDOWN = 90 * 20; // 90 seconds

    // Storage: UUID -> (AbilityType -> cooldown end time)
    private static final Map<UUID, Map<WarriorAbility, Long>> cooldowns = new HashMap<>();

    /**
     * Start a cooldown for a specific ability
     */
    public static void startCooldown(PlayerEntity player, WarriorAbility ability) {
        UUID uuid = player.getUuid();
        long endTime = player.getWorld().getTime() + ability.getCooldownTicks();

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(ability, endTime);

        // Send feedback to player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(
                    Text.literal("⚔ " + ability.getDisplayName() + " activated!")
                            .formatted(Formatting.GOLD, Formatting.BOLD),
                    true // Action bar
            );
        }
    }

    /**
     * Check if an ability is on cooldown
     */
    public static boolean isOnCooldown(PlayerEntity player, WarriorAbility ability) {
        UUID uuid = player.getUuid();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return false;
        }

        long currentTime = player.getWorld().getTime();
        long endTime = playerCooldowns.get(ability);

        if (currentTime >= endTime) {
            // Cooldown expired, remove it
            playerCooldowns.remove(ability);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in ticks
     */
    public static int getRemainingCooldown(PlayerEntity player, WarriorAbility ability) {
        UUID uuid = player.getUuid();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return 0;
        }

        long currentTime = player.getWorld().getTime();
        long endTime = playerCooldowns.get(ability);

        return Math.max(0, (int)(endTime - currentTime));
    }

    /**
     * Get remaining cooldown in seconds (for display)
     */
    public static int getRemainingSeconds(PlayerEntity player, WarriorAbility ability) {
        return (int) Math.ceil(getRemainingCooldown(player, ability) / 20.0);
    }

    /**
     * Send cooldown message to player
     */
    public static void sendCooldownMessage(PlayerEntity player, WarriorAbility ability) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            int seconds = getRemainingSeconds(player, ability);
            serverPlayer.sendMessage(
                    Text.literal("⏰ " + ability.getDisplayName() + " on cooldown: " + seconds + "s")
                            .formatted(Formatting.RED),
                    true // Action bar
            );
        }
    }

    /**
     * Clear all cooldowns for a player (e.g., on logout)
     */
    public static void clearPlayerCooldowns(UUID playerUuid) {
        cooldowns.remove(playerUuid);
    }

    /**
     * Clear a specific cooldown (for admin commands or special events)
     */
    public static void clearCooldown(PlayerEntity player, WarriorAbility ability) {
        UUID uuid = player.getUuid();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            playerCooldowns.remove(ability);
        }
    }

    /**
     * Get all active cooldowns for a player (for HUD display)
     */
    public static Map<WarriorAbility, Integer> getActiveCooldowns(PlayerEntity player) {
        UUID uuid = player.getUuid();
        Map<WarriorAbility, Integer> active = new HashMap<>();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            long currentTime = player.getWorld().getTime();

            for (Map.Entry<WarriorAbility, Long> entry : playerCooldowns.entrySet()) {
                int remaining = (int)(entry.getValue() - currentTime);
                if (remaining > 0) {
                    active.put(entry.getKey(), remaining);
                }
            }
        }

        return active;
    }
}