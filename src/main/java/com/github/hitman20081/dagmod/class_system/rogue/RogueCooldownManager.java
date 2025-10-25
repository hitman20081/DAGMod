package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns for cooldown-based Rogue abilities
 * Separate from RogueCooldownData (which is for energy abilities)
 */
public class RogueCooldownManager {
    // Cooldown durations in ticks (20 ticks = 1 second)
    public static final int BLINK_STRIKE_COOLDOWN = 25 * 20;   // 25 seconds
    public static final int VANISH_COOLDOWN = 40 * 20;          // 40 seconds
    public static final int POISON_STRIKE_COOLDOWN = 20 * 20;   // 20 seconds
    public static final int ASSASSINATE_COOLDOWN = 15 * 20;     // 15 seconds

    // Storage: UUID -> (AbilityType -> cooldown end time)
    private static final Map<UUID, Map<RogueAbility, Long>> cooldowns = new HashMap<>();

    /**
     * Start a cooldown for a specific ability
     */
    public static void startCooldown(PlayerEntity player, RogueAbility ability) {
        UUID uuid = player.getUuid();
        long endTime = player.getEntityWorld().getTime() + ability.getCooldownTicks();

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(ability, endTime);

        // Send feedback to player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(
                    Text.literal("⚔ " + ability.getDisplayName() + " activated!")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD),
                    true
            );
        }
    }

    /**
     * Check if an ability is on cooldown
     */
    public static boolean isOnCooldown(PlayerEntity player, RogueAbility ability) {
        UUID uuid = player.getUuid();
        Map<RogueAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return false;
        }

        long currentTime = player.getEntityWorld().getTime();
        long endTime = playerCooldowns.get(ability);

        if (currentTime >= endTime) {
            playerCooldowns.remove(ability);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in ticks
     */
    public static int getRemainingCooldown(PlayerEntity player, RogueAbility ability) {
        UUID uuid = player.getUuid();
        Map<RogueAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || !playerCooldowns.containsKey(ability)) {
            return 0;
        }

        long currentTime = player.getEntityWorld().getTime();
        long endTime = playerCooldowns.get(ability);

        return Math.max(0, (int)(endTime - currentTime));
    }

    /**
     * Get remaining cooldown in seconds (for display)
     */
    public static int getRemainingSeconds(PlayerEntity player, RogueAbility ability) {
        return (int) Math.ceil(getRemainingCooldown(player, ability) / 20.0);
    }

    /**
     * Send cooldown message to player
     */
    public static void sendCooldownMessage(PlayerEntity player, RogueAbility ability) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            int seconds = getRemainingSeconds(player, ability);
            serverPlayer.sendMessage(
                    Text.literal("⏰ " + ability.getDisplayName() + " on cooldown: " + seconds + "s")
                            .formatted(Formatting.RED),
                    true
            );
        }
    }

    /**
     * Clear all cooldowns for a player
     */
    public static void clearPlayerCooldowns(UUID playerUuid) {
        cooldowns.remove(playerUuid);
    }

    /**
     * Clear a specific cooldown
     */
    public static void clearCooldown(PlayerEntity player, RogueAbility ability) {
        UUID uuid = player.getUuid();
        Map<RogueAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            playerCooldowns.remove(ability);
        }
    }

    /**
     * Get all active cooldowns for a player
     */
    public static Map<RogueAbility, Integer> getActiveCooldowns(PlayerEntity player) {
        UUID uuid = player.getUuid();
        Map<RogueAbility, Integer> active = new HashMap<>();
        Map<RogueAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            long currentTime = player.getEntityWorld().getTime();

            for (Map.Entry<RogueAbility, Long> entry : playerCooldowns.entrySet()) {
                int remaining = (int)(entry.getValue() - currentTime);
                if (remaining > 0) {
                    active.put(entry.getKey(), remaining);
                }
            }
        }

        return active;
    }
}