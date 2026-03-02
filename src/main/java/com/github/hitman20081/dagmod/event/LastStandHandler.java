package com.github.hitman20081.dagmod.event;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks players with an active Last Stand Powder effect.
 * When a lethal hit is received, the effect is consumed and the player survives.
 */
public class LastStandHandler {

    private static final Set<UUID> activeLastStandPlayers = new HashSet<>();

    public static void activate(UUID uuid) {
        activeLastStandPlayers.add(uuid);
    }

    /**
     * Attempts to consume the Last Stand effect.
     *
     * @return true if the effect was active and has been consumed
     */
    public static boolean consume(UUID uuid) {
        return activeLastStandPlayers.remove(uuid);
    }

    public static boolean isActive(UUID uuid) {
        return activeLastStandPlayers.contains(uuid);
    }
}
