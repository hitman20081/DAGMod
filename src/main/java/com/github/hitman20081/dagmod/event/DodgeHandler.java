package com.github.hitman20081.dagmod.event;

import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks active dodge effects (Phantom Dust = 50%, Perfect Dodge = 100%).
 */
public class DodgeHandler {

    private static final Map<UUID, Float> dodgeChance = new HashMap<>();
    private static final Map<UUID, Long> dodgeExpiry = new HashMap<>();

    /**
     * Activates a dodge effect for the given player.
     *
     * @param uuid          Player UUID
     * @param chance        Dodge chance (0.0 to 1.0)
     * @param worldTime     Current world tick
     * @param durationTicks Duration in ticks
     */
    public static void activate(UUID uuid, float chance, long worldTime, int durationTicks) {
        dodgeChance.put(uuid, chance);
        dodgeExpiry.put(uuid, worldTime + durationTicks);
    }

    /**
     * Rolls a dodge check for the given player.
     * Auto-removes expired entries.
     *
     * @return true if the attack should be dodged
     */
    public static boolean tryDodge(UUID uuid, long worldTime, Random random) {
        Long expiry = dodgeExpiry.get(uuid);
        if (expiry == null) return false;
        if (worldTime >= expiry) {
            dodgeChance.remove(uuid);
            dodgeExpiry.remove(uuid);
            return false;
        }
        Float chance = dodgeChance.get(uuid);
        if (chance == null) return false;
        return random.nextFloat() < chance;
    }

    public static boolean isActive(UUID uuid) {
        return dodgeExpiry.containsKey(uuid);
    }
}
