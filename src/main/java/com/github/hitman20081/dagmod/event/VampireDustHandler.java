package com.github.hitman20081.dagmod.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks active Vampire Dust effect: 10% lifesteal for 20 seconds.
 * Expiry is stored as a world-tick timestamp.
 */
public class VampireDustHandler {

    // UUID -> world tick at which the effect expires
    private static final Map<UUID, Long> activeVampirePlayers = new HashMap<>();

    public static void activate(UUID uuid, long worldTime) {
        activeVampirePlayers.put(uuid, worldTime + 400L); // 400 ticks = 20 seconds
    }

    /**
     * Returns true if the player's Vampire Dust is still active.
     * Auto-removes expired entries.
     */
    public static boolean isActive(UUID uuid, long worldTime) {
        Long expiry = activeVampirePlayers.get(uuid);
        if (expiry == null) return false;
        if (worldTime >= expiry) {
            activeVampirePlayers.remove(uuid);
            return false;
        }
        return true;
    }
}
