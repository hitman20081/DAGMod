package com.github.hitman20081.dagmod.class_system.warrior;

import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cooldowns for Warrior abilities
 * Tracks per-player cooldowns for each ability type
 */
public class CooldownManager {
    // Cooldown durations in ticks (20 ticks = 1 second)
    public static final int RAGE_COOLDOWN = 60 * 20; // 60 seconds
    public static final int SHIELD_BASH_COOLDOWN = 20 * 20; // 20 seconds
    public static final int WAR_CRY_COOLDOWN = 90 * 20; // 90 seconds
    public static final int BATTLE_SHOUT_COOLDOWN = 45 * 20;  // 45 seconds
    public static final int WHIRLWIND_COOLDOWN = 30 * 20;     // 30 seconds
    public static final int IRON_SKIN_COOLDOWN = 120 * 20;    // 120 seconds (2 minutes)

    // Storage: UUID -> (AbilityType -> cooldown end time)
    private static final Map<UUID, Map<WarriorAbility, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * Start a cooldown for a specific ability
     */
    /** Cooldown reduction: 0% at level 1, 40% at level 200. */
    public static int getEffectiveCooldownTicks(int baseTicks, int level) {
        float multiplier = Math.max(0.6f, 1.0f - level * 0.002f);
        return Math.round(baseTicks * multiplier);
    }

    public static void startCooldown(Player player, WarriorAbility ability) {
        UUID uuid = player.getUUID();

        int level = 1;
        if (player instanceof ServerPlayer sp) {
            var progData = ProgressionManager.getPlayerData(sp);
            if (progData != null) level = progData.getCurrentLevel();
        }

        int effectiveTicks = getEffectiveCooldownTicks(ability.getCooldownTicks(), level);
        long endTime = player.level().getGameTime() + effectiveTicks;

        cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(ability, endTime);

        // Send feedback to player
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(
                    Component.literal("⚔ " + ability.getDisplayName() + " activated!")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            );
        }
    }

    /**
     * Check if an ability is on cooldown
     */
    public static boolean isOnCooldown(Player player, WarriorAbility ability) {
        UUID uuid = player.getUUID();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

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
    public static int getRemainingCooldown(Player player, WarriorAbility ability) {
        UUID uuid = player.getUUID();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

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
    public static int getRemainingSeconds(Player player, WarriorAbility ability) {
        return (int) Math.ceil(getRemainingCooldown(player, ability) / 20.0);
    }

    /**
     * Send cooldown message to player
     */
    public static void sendCooldownMessage(Player player, WarriorAbility ability) {
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
    public static void clearCooldown(Player player, WarriorAbility ability) {
        UUID uuid = player.getUUID();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            playerCooldowns.remove(ability);
        }
    }

    /**
     * Get all active cooldowns for a player (for HUD display)
     */
    public static Map<WarriorAbility, Integer> getActiveCooldowns(Player player) {
        UUID uuid = player.getUUID();
        Map<WarriorAbility, Integer> active = new HashMap<>();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            long currentTime = player.level().getGameTime();

            for (Map.Entry<WarriorAbility, Long> entry : playerCooldowns.entrySet()) {
                int remaining = (int)(entry.getValue() - currentTime);
                if (remaining > 0) {
                    active.put(entry.getKey(), remaining);
                }
            }
        }

        return active;
    }
    /**
     * Reduces all active cooldowns for a player by the specified amount
     * Used by Cooldown Elixir consumable item
     * @param player The player whose cooldowns to reduce
     * @param ticks Amount of ticks to reduce (20 ticks = 1 second)
     */
    public static void reduceAllCooldowns(ServerPlayer player, int ticks) {
        UUID uuid = player.getUUID();
        Map<WarriorAbility, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null || playerCooldowns.isEmpty()) {
            player.sendSystemMessage(
                    Component.literal("No active cooldowns to reduce!")
                            .withStyle(ChatFormatting.YELLOW));
            return;
        }

        long currentTime = player.level().getGameTime();
        int reducedCount = 0;

        // Reduce each cooldown
        for (Map.Entry<WarriorAbility, Long> entry : playerCooldowns.entrySet()) {
            long cooldownEndTime = entry.getValue();

            // Only reduce if cooldown is still active
            if (cooldownEndTime > currentTime) {
                long newEndTime = cooldownEndTime - ticks;
                // Make sure we don't go below current time (instant ready)
                newEndTime = Math.max(currentTime, newEndTime);
                entry.setValue(newEndTime);
                reducedCount++;
            }
        }

        if (reducedCount > 0) {
            player.sendOverlayMessage(
                    Component.literal("⏰ Reduced " + reducedCount + " cooldown(s) by " + (ticks / 20) + " seconds!")
                            .withStyle(ChatFormatting.GOLD));
        }
    }
}