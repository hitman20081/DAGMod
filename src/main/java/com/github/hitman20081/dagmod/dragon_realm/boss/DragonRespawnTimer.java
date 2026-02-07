package com.github.hitman20081.dagmod.dragon_realm.boss;

import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Manages Dragon Guardian boss respawn timing
 *
 * Features:
 * - Configurable respawn delay (default 30 minutes)
 * - Persistent NBT storage in world data
 * - Global announcements at intervals
 * - Automatic boss respawning after timer expires
 */
public class DragonRespawnTimer {

    // Respawn delay configuration (in ticks)
    public static final long DEFAULT_RESPAWN_DELAY = 36000; // 30 minutes (36000 ticks)
    public static final long MIN_RESPAWN_DELAY = 12000;     // 10 minutes minimum
    public static final long MAX_RESPAWN_DELAY = 1728000;   // 1 day maximum

    // Announcement intervals (in ticks before respawn)
    private static final long[] ANNOUNCEMENT_INTERVALS = {
        24000,  // 20 minutes
        12000,  // 10 minutes
        6000,   // 5 minutes
        1200,   // 1 minute
        600,    // 30 seconds
        200     // 10 seconds
    };

    // Timer state
    private long deathTime;           // Game time when boss died
    private long respawnDelay;        // How long until respawn
    private boolean isActive;         // Is timer currently running
    private int lastAnnouncementIndex; // Last announcement made

    public DragonRespawnTimer() {
        this.deathTime = 0;
        this.respawnDelay = DEFAULT_RESPAWN_DELAY;
        this.isActive = false;
        this.lastAnnouncementIndex = -1;
    }

    /**
     * Start respawn timer after boss death
     */
    public void startTimer(ServerWorld world) {
        this.deathTime = world.getTime();
        this.respawnDelay = DEFAULT_RESPAWN_DELAY;
        this.isActive = true;
        this.lastAnnouncementIndex = -1;

        announceTimerStart(world);
    }

    /**
     * Start timer with custom delay
     */
    public void startTimer(ServerWorld world, long customDelay) {
        this.deathTime = world.getTime();
        this.respawnDelay = Math.max(MIN_RESPAWN_DELAY, Math.min(MAX_RESPAWN_DELAY, customDelay));
        this.isActive = true;
        this.lastAnnouncementIndex = -1;

        announceTimerStart(world);
    }

    /**
     * Tick the timer - call this every server tick
     * Returns true if boss should respawn
     */
    public boolean tick(ServerWorld world) {
        if (!isActive) {
            return false;
        }

        long currentTime = world.getTime();
        long elapsedTime = currentTime - deathTime;
        long remainingTime = respawnDelay - elapsedTime;

        // Check if it's time to respawn
        if (remainingTime <= 0) {
            isActive = false;
            announceRespawn(world);
            return true;
        }

        // Check for announcements
        checkAnnouncements(world, remainingTime);

        return false;
    }

    /**
     * Check if we should make an announcement
     */
    private void checkAnnouncements(ServerWorld world, long remainingTime) {
        for (int i = 0; i < ANNOUNCEMENT_INTERVALS.length; i++) {
            // Skip if we've already announced this interval
            if (i <= lastAnnouncementIndex) {
                continue;
            }

            // Check if we've crossed this announcement threshold
            if (remainingTime <= ANNOUNCEMENT_INTERVALS[i]) {
                lastAnnouncementIndex = i;
                announceTimeRemaining(world, ANNOUNCEMENT_INTERVALS[i]);
                break;
            }
        }
    }

    /**
     * Announce timer start
     */
    private void announceTimerStart(ServerWorld world) {
        long minutes = respawnDelay / 1200; // Convert ticks to minutes

        Text message = Text.literal("The Dragon Guardian will respawn in ")
                .formatted(Formatting.LIGHT_PURPLE)
                .append(Text.literal(formatTime(respawnDelay))
                        .formatted(Formatting.GOLD))
                .append(Text.literal("...")
                        .formatted(Formatting.LIGHT_PURPLE));

        broadcastToDragonRealm(world, message);
    }

    /**
     * Announce time remaining
     */
    private void announceTimeRemaining(ServerWorld world, long ticksRemaining) {
        Text message = Text.literal("Dragon Guardian respawning in ")
                .formatted(Formatting.YELLOW)
                .append(Text.literal(formatTime(ticksRemaining))
                        .formatted(Formatting.GOLD))
                .append(Text.literal("!")
                        .formatted(Formatting.YELLOW));

        broadcastToDragonRealm(world, message);
    }

    /**
     * Announce boss respawn
     */
    private void announceRespawn(ServerWorld world) {
        Text message = Text.literal("☆ The Dragon Guardian has respawned! ☆")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD);

        broadcastToDragonRealm(world, message);

        // Also notify Overworld
        ServerWorld overworld = world.getServer().getWorld(DragonRealmTeleporter.OVERWORLD);
        if (overworld != null) {
            Text overworldMsg = Text.literal("The Dragon Guardian has respawned in the Dragon Realm!")
                    .formatted(Formatting.LIGHT_PURPLE);
            for (ServerPlayerEntity player : overworld.getPlayers()) {
                player.sendMessage(overworldMsg, false);
            }
        }
    }

    /**
     * Broadcast message to all players in Dragon Realm
     */
    private void broadcastToDragonRealm(ServerWorld world, Text message) {
        // Only broadcast if we're in the Dragon Realm
        if (world.getRegistryKey() != DragonRealmTeleporter.DRAGON_REALM) {
            // Get Dragon Realm world
            ServerWorld dragonRealm = world.getServer().getWorld(DragonRealmTeleporter.DRAGON_REALM);
            if (dragonRealm != null) {
                world = dragonRealm;
            }
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        }
    }

    /**
     * Format ticks into human-readable time
     */
    private String formatTime(long ticks) {
        long totalSeconds = ticks / 20;

        if (totalSeconds >= 60) {
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            if (seconds > 0) {
                return minutes + "m " + seconds + "s";
            } else {
                return minutes + " minute" + (minutes != 1 ? "s" : "");
            }
        } else {
            return totalSeconds + " second" + (totalSeconds != 1 ? "s" : "");
        }
    }

    /**
     * Check if timer is currently active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Get time remaining in ticks
     */
    public long getTimeRemaining(ServerWorld world) {
        if (!isActive) {
            return 0;
        }

        long currentTime = world.getTime();
        long elapsedTime = currentTime - deathTime;
        return Math.max(0, respawnDelay - elapsedTime);
    }

    /**
     * Cancel the timer
     */
    public void cancel() {
        this.isActive = false;
    }

    /**
     * Save timer state to NBT
     */
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("DeathTime", deathTime);
        nbt.putLong("RespawnDelay", respawnDelay);
        nbt.putBoolean("IsActive", isActive);
        nbt.putInt("LastAnnouncementIndex", lastAnnouncementIndex);
        return nbt;
    }

    /**
     * Load timer state from NBT
     */
    public void readNbt(NbtCompound nbt) {
        this.deathTime = nbt.getLong("DeathTime").orElse(0L);
        this.respawnDelay = nbt.getLong("RespawnDelay").orElse(DEFAULT_RESPAWN_DELAY);
        this.isActive = nbt.getBoolean("IsActive").orElse(false);
        this.lastAnnouncementIndex = nbt.getInt("LastAnnouncementIndex").orElse(-1);
    }
}
