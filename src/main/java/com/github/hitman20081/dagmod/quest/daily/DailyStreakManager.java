package com.github.hitman20081.dagmod.quest.daily;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player daily quest streaks and per-day completion counts.
 * Storage: world/data/dagmod/daily_streaks/{uuid}.dat
 *
 * Streak rules:
 *   - Completing at least one daily per UTC day extends the streak
 *   - Missing a day resets to 1 on the next completion
 *
 * XP multipliers (applied at turn-in):
 *   streak >= 14 days → ×1.5
 *   streak >= 7 days  → ×1.25
 *   streak >= 3 days  → ×1.1
 *   otherwise         → ×1.0
 *
 * Level multipliers (applied at turn-in):
 *   level > 150 → ×2.0
 *   level > 100 → ×1.6
 *   level > 50  → ×1.3
 *   otherwise   → ×1.0
 */
public class DailyStreakManager {

    private static final ConcurrentHashMap<UUID, PlayerDailyData> playerData = new ConcurrentHashMap<>();
    private static final String DATA_DIR = "data/dagmod/daily_streaks";

    private static class PlayerDailyData {
        final UUID playerId;
        long lastCompletionDay = -1;
        int streakCount = 0;
        final Set<String> completedTodayIds = new HashSet<>();

        PlayerDailyData(UUID id) { this.playerId = id; }
    }

    // ========== Lifecycle ==========

    public static void load(MinecraftServer server, UUID playerId) {
        File file = getFile(server, playerId);
        if (!file.exists()) {
            playerData.put(playerId, new PlayerDailyData(playerId));
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            CompoundTag nbt = NbtIo.readCompressed(fis, NbtAccounter.unlimitedHeap());
            PlayerDailyData data = new PlayerDailyData(playerId);
            data.lastCompletionDay = nbt.getLong("lastDay").orElse(-1L);
            data.streakCount = nbt.getInt("streak").orElse(0);
            // Only restore today's completions if the saved day is still today
            if (data.lastCompletionDay == epochDay()) {
                nbt.getList("completedToday").ifPresent(list -> {
                    for (int i = 0; i < list.size(); i++) {
                        list.getCompound(i).ifPresent(c -> c.getString("id").ifPresent(data.completedTodayIds::add));
                    }
                });
            }
            playerData.put(playerId, data);
        } catch (IOException e) {
            DagMod.LOGGER.error("[DailyStreak] Failed to load data for " + playerId, e);
            playerData.put(playerId, new PlayerDailyData(playerId));
        }
    }

    public static void unload(UUID playerId) {
        playerData.remove(playerId);
    }

    // ========== Queries ==========

    /** True if the player has already completed this specific daily today. */
    public static boolean hasCompletedToday(UUID playerId, String questId) {
        PlayerDailyData data = playerData.get(playerId);
        if (data == null || data.lastCompletionDay != epochDay()) return false;
        return data.completedTodayIds.contains(questId);
    }

    /** How many different daily quests the player has completed today. */
    public static int completedTodayCount(UUID playerId) {
        PlayerDailyData data = playerData.get(playerId);
        if (data == null || data.lastCompletionDay != epochDay()) return 0;
        return data.completedTodayIds.size();
    }

    /** Current streak length in days. */
    public static int getStreak(UUID playerId) {
        PlayerDailyData data = playerData.get(playerId);
        return data != null ? data.streakCount : 0;
    }

    // ========== Mutation ==========

    /** Call when a player successfully turns in a daily quest. Updates streak and persists. */
    public static void markCompleted(MinecraftServer server, UUID playerId, String questId) {
        PlayerDailyData data = playerData.computeIfAbsent(playerId, PlayerDailyData::new);
        long today = epochDay();

        // Clear stale today set if it's a new day
        if (data.lastCompletionDay < today) {
            data.completedTodayIds.clear();
        }

        // Update streak counter
        if (data.lastCompletionDay == today - 1) {
            // Consecutive day — extend streak
            data.streakCount++;
        } else if (data.lastCompletionDay < today - 1 || data.streakCount == 0) {
            // Missed at least one day or first time — reset to 1
            data.streakCount = 1;
        }
        // If lastCompletionDay == today: same day, multiple dailies — streak unchanged

        data.completedTodayIds.add(questId);
        data.lastCompletionDay = today;
        save(server, data);
    }

    // ========== Multipliers ==========

    /** XP multiplier based on consecutive-day streak. */
    public static float getStreakMultiplier(UUID playerId) {
        int streak = getStreak(playerId);
        if (streak >= 14) return 1.5f;
        if (streak >= 7)  return 1.25f;
        if (streak >= 3)  return 1.1f;
        return 1.0f;
    }

    /** XP multiplier based on player level (rewards higher-level players more). */
    public static float getLevelMultiplier(int level) {
        if (level > 150) return 2.0f;
        if (level > 100) return 1.6f;
        if (level > 50)  return 1.3f;
        return 1.0f;
    }

    /** Combined XP bonus multiplier (streak × level). */
    public static float getTotalMultiplier(UUID playerId, int level) {
        return getStreakMultiplier(playerId) * getLevelMultiplier(level);
    }

    // ========== Persistence ==========

    private static void save(MinecraftServer server, PlayerDailyData data) {
        try {
            File file = getFile(server, data.playerId);
            file.getParentFile().mkdirs();
            CompoundTag nbt = new CompoundTag();
            nbt.putLong("lastDay", data.lastCompletionDay);
            nbt.putInt("streak", data.streakCount);
            ListTag list = new ListTag();
            for (String id : data.completedTodayIds) {
                CompoundTag entry = new CompoundTag();
                entry.putString("id", id);
                list.add(entry);
            }
            nbt.put("completedToday", list);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                NbtIo.writeCompressed(nbt, fos);
            }
        } catch (IOException e) {
            DagMod.LOGGER.error("[DailyStreak] Failed to save data", e);
        }
    }

    private static File getFile(MinecraftServer server, UUID playerId) {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve(DATA_DIR)
                .resolve(playerId.toString() + ".dat")
                .toFile();
    }

    private static long epochDay() {
        return DailyQuestManager.epochDay();
    }
}
