package com.github.hitman20081.dagmod.dragon_realm.boss;

import com.github.hitman20081.dagmod.dragon_realm.portal.DragonRealmTeleporter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

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
    // Every 5 minutes, then every minute for last 5, then 30s, then 10s countdown
    private static final long[] ANNOUNCEMENT_INTERVALS = {
        30000,  // 25 minutes
        24000,  // 20 minutes
        18000,  // 15 minutes
        12000,  // 10 minutes
        6000,   // 5 minutes
        4800,   // 4 minutes
        3600,   // 3 minutes
        2400,   // 2 minutes
        1200,   // 1 minute
        600,    // 30 seconds
        200,    // 10 seconds
        180,    // 9 seconds
        160,    // 8 seconds
        140,    // 7 seconds
        120,    // 6 seconds
        100,    // 5 seconds
        80,     // 4 seconds
        60,     // 3 seconds
        40,     // 2 seconds
        20      // 1 second
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
    public void startTimer(ServerLevel world) {
        this.deathTime = world.getGameTime();
        this.respawnDelay = DEFAULT_RESPAWN_DELAY;
        this.isActive = true;
        this.lastAnnouncementIndex = -1;

        announceTimerStart(world);
    }

    /**
     * Start timer with custom delay
     */
    public void startTimer(ServerLevel world, long customDelay) {
        this.deathTime = world.getGameTime();
        this.respawnDelay = Math.max(MIN_RESPAWN_DELAY, Math.min(MAX_RESPAWN_DELAY, customDelay));
        this.isActive = true;
        this.lastAnnouncementIndex = -1;

        announceTimerStart(world);
    }

    /**
     * Tick the timer - call this every server tick
     * Returns true if boss should respawn
     */
    public boolean tick(ServerLevel world) {
        if (!isActive) {
            return false;
        }

        long currentTime = world.getGameTime();
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
    private void checkAnnouncements(ServerLevel world, long remainingTime) {
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
    private void announceTimerStart(ServerLevel world) {
        long minutes = respawnDelay / 1200; // Convert ticks to minutes

        Component message = Component.literal("The Dragon Guardian will respawn in ")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(formatTime(respawnDelay))
                        .withStyle(ChatFormatting.GOLD))
                .append(Component.literal("...")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));

        broadcastToDragonRealm(world, message);
    }

    /**
     * Announce time remaining
     */
    private void announceTimeRemaining(ServerLevel world, long ticksRemaining) {
        long totalSeconds = ticksRemaining / 20;

        if (totalSeconds <= 10) {
            // Final countdown: bold red numbers
            Component message = Component.literal("Dragon Guardian respawning in ")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                    .append(Component.literal(totalSeconds + "...")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            broadcastToDragonRealm(world, message);
        } else {
            Component message = Component.literal("Dragon Guardian respawning in ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(formatTime(ticksRemaining))
                            .withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("!")
                            .withStyle(ChatFormatting.YELLOW));
            broadcastToDragonRealm(world, message);
        }
    }

    /**
     * Announce boss respawn
     */
    private void announceRespawn(ServerLevel world) {
        Component message = Component.literal("☆ The Dragon Guardian has respawned! ☆")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);

        broadcastToDragonRealm(world, message);

        // Also notify Overworld
        ServerLevel overworld = world.getServer().getLevel(DragonRealmTeleporter.OVERWORLD);
        if (overworld != null) {
            Component overworldMsg = Component.literal("The Dragon Guardian has respawned in the Dragon Realm!")
                    .withStyle(ChatFormatting.LIGHT_PURPLE);
            for (ServerPlayer player : overworld.players()) {
                player.sendSystemMessage(overworldMsg);
            }
        }
    }

    /**
     * Broadcast message to all players in Dragon Realm
     */
    private void broadcastToDragonRealm(ServerLevel world, Component message) {
        // Only broadcast if we're in the Dragon Realm
        if (world.dimension() != DragonRealmTeleporter.DRAGON_REALM) {
            // Get Dragon Realm world
            ServerLevel dragonRealm = world.getServer().getLevel(DragonRealmTeleporter.DRAGON_REALM);
            if (dragonRealm != null) {
                world = dragonRealm;
            }
        }

        for (ServerPlayer player : world.players()) {
            player.sendSystemMessage(message);
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
    public long getTimeRemaining(ServerLevel world) {
        if (!isActive) {
            return 0;
        }

        long currentTime = world.getGameTime();
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
    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong("DeathTime", deathTime);
        nbt.putLong("RespawnDelay", respawnDelay);
        nbt.putBoolean("IsActive", isActive);
        nbt.putInt("LastAnnouncementIndex", lastAnnouncementIndex);
        return nbt;
    }

    /**
     * Load timer state from NBT
     */
    public void readNbt(CompoundTag nbt) {
        this.deathTime = nbt.getLong("DeathTime").orElse(0L);
        this.respawnDelay = nbt.getLong("RespawnDelay").orElse(DEFAULT_RESPAWN_DELAY);
        this.isActive = nbt.getBoolean("IsActive").orElse(false);
        this.lastAnnouncementIndex = nbt.getInt("LastAnnouncementIndex").orElse(-1);
    }
}
