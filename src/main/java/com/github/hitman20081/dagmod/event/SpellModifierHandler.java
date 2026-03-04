package com.github.hitman20081.dagmod.event;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks Mage spell modifiers:
 * - Spell Echo: next spell casts twice (no extra cooldown on echo cast)
 * - Overcharge Dust: next spell has 2× power
 */
public class SpellModifierHandler {

    private static final Set<UUID> spellEchoPlayers = new HashSet<>();
    private static final Set<UUID> overchargePlayers = new HashSet<>();

    // --- Spell Echo ---

    public static void activateSpellEcho(UUID uuid) {
        spellEchoPlayers.add(uuid);
    }

    /**
     * Consumes the Spell Echo modifier.
     *
     * @return true if echo was active (and is now consumed)
     */
    public static boolean consumeSpellEcho(UUID uuid) {
        return spellEchoPlayers.remove(uuid);
    }

    public static boolean hasSpellEcho(UUID uuid) {
        return spellEchoPlayers.contains(uuid);
    }

    // --- Overcharge Dust ---

    public static void activateOvercharge(UUID uuid) {
        overchargePlayers.add(uuid);
    }

    /**
     * Consumes the Overcharge modifier.
     *
     * @return 2.0f if overcharge was active (consumed), 1.0f otherwise
     */
    public static float consumeOvercharge(UUID uuid) {
        if (overchargePlayers.remove(uuid)) {
            return 2.0f;
        }
        return 1.0f;
    }

    public static boolean hasOvercharge(UUID uuid) {
        return overchargePlayers.contains(uuid);
    }
}
