package com.github.hitman20081.dagmod.quest.daily;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.quest.registry.DailyQuestRegistry;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages the server-wide daily quest rotation.
 * All players see the same pool of daily quests each day.
 * Resets at UTC midnight (epoch day boundary).
 */
public class DailyQuestManager {

    private static DailyQuestManager instance;

    private List<String> todayQuestIds = new ArrayList<>();
    private long lastResetEpochDay = -1;

    // 5 quests offered per day; players may complete up to 3
    static final int DAILY_POOL_SIZE = 5;
    public static final int DAILY_COMPLETION_LIMIT = 3;

    private static final String DATA_FILE = "data/dagmod/daily_quests.dat";

    private DailyQuestManager() {}

    public static DailyQuestManager getInstance() {
        if (instance == null) instance = new DailyQuestManager();
        return instance;
    }

    /**
     * Call on server start (via load) and lazily from JobBoardBlock on each interaction.
     * Rolls a new pool if UTC day has changed since last reset.
     */
    public void checkAndReset(MinecraftServer server) {
        long today = epochDay();
        if (today != lastResetEpochDay) {
            rollNewQuests();
            lastResetEpochDay = today;
            save(server);
            DagMod.LOGGER.info("[DailyQuests] Rolled new pool for day " + today + ": " + todayQuestIds);
        }
    }

    private void rollNewQuests() {
        List<String> pool = new ArrayList<>(DailyQuestRegistry.ALL_IDS);
        Collections.shuffle(pool, new Random());
        todayQuestIds = new ArrayList<>(pool.subList(0, Math.min(DAILY_POOL_SIZE, pool.size())));
    }

    public List<String> getTodayQuestIds() {
        return Collections.unmodifiableList(todayQuestIds);
    }

    /** Seconds remaining until the next UTC midnight reset. */
    public long secondsUntilReset() {
        long nextMidnightMs = (epochDay() + 1) * 86400000L;
        return (nextMidnightMs - System.currentTimeMillis()) / 1000L;
    }

    static long epochDay() {
        return System.currentTimeMillis() / 86400000L;
    }

    // ========== Persistence ==========

    public void load(MinecraftServer server) {
        File file = getDataFile(server);
        if (!file.exists()) {
            rollNewQuests();
            lastResetEpochDay = epochDay();
            save(server);
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            CompoundTag nbt = NbtIo.readCompressed(fis, NbtAccounter.unlimitedHeap());
            lastResetEpochDay = nbt.getLong("lastResetDay").orElse(-1L);
            todayQuestIds = new ArrayList<>();
            nbt.getList("questIds").ifPresent(list -> {
                for (int i = 0; i < list.size(); i++) {
                    list.getCompound(i).ifPresent(c -> c.getString("id").ifPresent(todayQuestIds::add));
                }
            });
            // Reroll if the saved day is stale
            if (epochDay() != lastResetEpochDay) {
                rollNewQuests();
                lastResetEpochDay = epochDay();
                save(server);
            }
            DagMod.LOGGER.info("[DailyQuests] Loaded rotation for day " + lastResetEpochDay + ": " + todayQuestIds);
        } catch (IOException e) {
            DagMod.LOGGER.error("[DailyQuests] Failed to load rotation, rolling fresh", e);
            rollNewQuests();
            lastResetEpochDay = epochDay();
        }
    }

    public void save(MinecraftServer server) {
        try {
            File file = getDataFile(server);
            file.getParentFile().mkdirs();
            CompoundTag nbt = new CompoundTag();
            nbt.putLong("lastResetDay", lastResetEpochDay);
            ListTag list = new ListTag();
            for (String id : todayQuestIds) {
                CompoundTag entry = new CompoundTag();
                entry.putString("id", id);
                list.add(entry);
            }
            nbt.put("questIds", list);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                NbtIo.writeCompressed(nbt, fos);
            }
        } catch (IOException e) {
            DagMod.LOGGER.error("[DailyQuests] Failed to save rotation", e);
        }
    }

    private File getDataFile(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(DATA_FILE).toFile();
    }
}
