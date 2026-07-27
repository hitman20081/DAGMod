package com.github.hitman20081.dagmod.class_system.mage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns for Mage abilities
 * Similar to WarriorCooldownManager but for Mage abilities
 */
public class MageCooldownManager {
    // Cooldown durations in ticks (20 ticks = 1 second)
    public static final int ARCANE_MISSILES_COOLDOWN = 20 * 20;  // 20 seconds
    public static final int TIME_WARP_COOLDOWN = 45 * 20;         // 45 seconds
    public static final int MANA_BURST_COOLDOWN = 30 * 20;        // 30 seconds
    public static final int ARCANE_BARRIER_COOLDOWN = 60 * 20;    // 60 seconds

    // Storage: UUID -> (AbilityType -> cooldown end time)
    private static final Map<UUID, Map<MageAbility, Long>> cooldowns = new HashMap<>();

    /**
     * Start a cooldown for a specific ability
     */
    public static void startCooldown(Player player, MageAbility ability) {
        UUID uuid = player.getUUID();
        long endTime = player.level().getGameTime() + ability.getCooldownTicks();

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(ability, endTime);

        // Send feedback to player
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(Component.literal("✦ " + ability.getDisplayName() + " activated!")
                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        }
    }

    /**
     * Check if an ability is on cooldown
     */
    public static boolean isOnCooldown(Player player, MageAbility ability) {
        UUID uuid = player.getUUID();
        Map<MageAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return false;
        }

        long currentTime = player.level().getGameTime();
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
    public static int getRemainingCooldown(Player player, MageAbility ability) {
        UUID uuid = player.getUUID();
        Map<MageAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return 0;
        }

        long currentTime = player.level().getGameTime();
        long endTime = playerCooldowns.get(ability);

        return Math.max(0, (int)(endTime - currentTime));
    }

    /**
     * Get remaining cooldown in seconds (for display)
     */
    public static int getRemainingSeconds(Player player, MageAbility ability) {
        return (int) Math.ceil(getRemainingCooldown(player, ability) / 20.0);
    }

    /**
     * Send cooldown message to player
     */
    public static void sendCooldownMessage(Player player, MageAbility ability) {
        if (player instanceof ServerPlayer serverPlayer) {
            int seconds = getRemainingSeconds(player, ability);
            serverPlayer.sendOverlayMessage(
                    Component.literal("⏰ " + ability.getDisplayName() + " on cooldown: " + seconds + "s")
                            .withStyle(ChatFormatting.RED)
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
    public static void clearCooldown(Player player, MageAbility ability) {
        UUID uuid = player.getUUID();
        Map<MageAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            playerCooldowns.remove(ability);
        }
    }

    /**
     * Get all active cooldowns for a player (for HUD display)
     */
    public static Map<MageAbility, Integer> getActiveCooldowns(Player player) {
        UUID uuid = player.getUUID();
        Map<MageAbility, Integer> active = new HashMap<>();
        Map<MageAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            long currentTime = player.level().getGameTime();

            for (Map.Entry<MageAbility, Long> entry : playerCooldowns.entrySet()) {
                int remaining = (int)(entry.getValue() - currentTime);
                if (remaining > 0) {
                    active.put(entry.getKey(), remaining);
                }
            }
        }

        return active;
    }
}