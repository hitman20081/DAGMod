package com.github.hitman20081.dagmod.dragon_realm.boss;

import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Dragon Guardian respawn timers with persistent storage
 *
 * Features:
 * - Per-dimension timer tracking
 * - Persistent file-based NBT storage
 * - Automatic timer ticking
 * - Boss respawning when timer expires
 */
public class DragonRespawnTimerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("DAGMod-DragonRespawnTimer");
    private static MinecraftServer server;
    private static DragonRespawnTimerManager instance;

    // Storage paths
    private static final String DATA_ROOT = "data/dagmod";
    private static final String TIMER_FILE = "dragon_respawn_timers.dat";

    // Map of dimension key -> respawn timer
    private final Map<String, DragonRespawnTimer> timers = new HashMap<>();

    public DragonRespawnTimerManager() {
    }

    /**
     * Get or create instance for the server (cached singleton)
     */
    public static DragonRespawnTimerManager get(MinecraftServer minecraftServer) {
        if (instance == null || server != minecraftServer) {
            server = minecraftServer;
            instance = loadFromFile();
        }
        return instance;
    }

    /**
     * Start respawn timer for a dimension
     */
    public void startTimer(ServerLevel world) {
        String dimensionKey = world.dimension().identifier().toString();
        DragonRespawnTimer timer = timers.computeIfAbsent(dimensionKey, k -> new DragonRespawnTimer());
        timer.startTimer(world);
        saveToFile();
    }

    /**
     * Start respawn timer with custom delay
     */
    public void startTimer(ServerLevel world, long customDelay) {
        String dimensionKey = world.dimension().identifier().toString();
        DragonRespawnTimer timer = timers.computeIfAbsent(dimensionKey, k -> new DragonRespawnTimer());
        timer.startTimer(world, customDelay);
        saveToFile();
    }

    /**
     * Tick all timers - call this from server tick
     */
    public void tick(MinecraftServer minecraftServer) {
        for (Map.Entry<String, DragonRespawnTimer> entry : timers.entrySet()) {
            DragonRespawnTimer timer = entry.getValue();

            if (!timer.isActive()) {
                continue;
            }

            // Get the world for this timer
            ServerLevel world = getWorldByKey(minecraftServer, entry.getKey());
            if (world == null) {
                continue;
            }

            // Tick the timer
            if (timer.tick(world)) {
                // Timer expired - respawn boss
                respawnBoss(world);
                saveToFile();
            }
        }
    }

    /**
     * Respawn the boss in the dimension
     */
    private void respawnBoss(ServerLevel world) {
        // Check if boss already exists (shouldn't, but safety check)
        if (DragonGuardianSpawner.bossExists(world)) {
            return;
        }

        // Spawn the boss
        DragonGuardianSpawner.spawnBoss(world, null);
    }

    /**
     * Check if a dimension has an active timer
     */
    public boolean hasActiveTimer(ServerLevel world) {
        String dimensionKey = world.dimension().identifier().toString();
        DragonRespawnTimer timer = timers.get(dimensionKey);
        return timer != null && timer.isActive();
    }

    /**
     * Get time remaining for a dimension (in ticks)
     */
    public long getTimeRemaining(ServerLevel world) {
        String dimensionKey = world.dimension().identifier().toString();
        DragonRespawnTimer timer = timers.get(dimensionKey);
        if (timer == null || !timer.isActive()) {
            return 0;
        }
        return timer.getTimeRemaining(world);
    }

    /**
     * Cancel timer for a dimension
     */
    public void cancelTimer(ServerLevel world) {
        String dimensionKey = world.dimension().identifier().toString();
        DragonRespawnTimer timer = timers.get(dimensionKey);
        if (timer != null) {
            timer.cancel();
            saveToFile();
        }
    }

    /**
     * Get world by dimension key string
     */
    private ServerLevel getWorldByKey(MinecraftServer minecraftServer, String keyString) {
        for (ServerLevel world : minecraftServer.getAllLevels()) {
            if (world.dimension().identifier().toString().equals(keyString)) {
                return world;
            }
        }
        return null;
    }

    /**
     * Get the timer data file
     */
    private static File getTimerFile() {
        if (server == null) {
            throw new IllegalStateException("DragonRespawnTimerManager not initialized!");
        }

        File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();
        File dagmodDir = new File(worldDir, DATA_ROOT);

        if (!dagmodDir.exists()) {
            dagmodDir.mkdirs();
        }

        return new File(dagmodDir, TIMER_FILE);
    }

    /**
     * Save all timers to file
     */
    private void saveToFile() {
        try {
            File file = getTimerFile();
            CompoundTag nbt = new CompoundTag();

            CompoundTag timersNbt = new CompoundTag();
            for (Map.Entry<String, DragonRespawnTimer> entry : timers.entrySet()) {
                CompoundTag timerNbt = new CompoundTag();
                entry.getValue().writeNbt(timerNbt);
                timersNbt.put(entry.getKey(), timerNbt);
            }

            nbt.put("Timers", timersNbt);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            LOGGER.debug("Saved {} dragon respawn timers", timers.size());

        } catch (IOException e) {
            LOGGER.error("Failed to save dragon respawn timers", e);
        }
    }

    /**
     * Load all timers from file
     */
    private static DragonRespawnTimerManager loadFromFile() {
        DragonRespawnTimerManager manager = new DragonRespawnTimerManager();

        File file = getTimerFile();
        if (!file.exists()) {
            LOGGER.debug("No dragon respawn timer data found, creating new manager");
            return manager;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            CompoundTag nbt = NbtIo.readCompressed(fis, NbtAccounter.unlimitedHeap());

            if (nbt.contains("Timers")) {
                CompoundTag timersNbt = nbt.getCompound("Timers").orElse(new CompoundTag());

                for (String key : timersNbt.keySet()) {
                    CompoundTag timerNbt = timersNbt.getCompound(key).orElse(new CompoundTag());
                    DragonRespawnTimer timer = new DragonRespawnTimer();
                    timer.readNbt(timerNbt);
                    manager.timers.put(key, timer);
                }
            }

            LOGGER.debug("Loaded {} dragon respawn timers", manager.timers.size());
            return manager;

        } catch (IOException e) {
            LOGGER.error("Failed to load dragon respawn timers, creating new manager", e);
            return manager;
        }
    }
}
