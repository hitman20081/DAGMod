package com.github.hitman20081.dagmod.quest;

import java.util.List;

/**
 * Canonical per-class quest chain IDs (5 per class, level-gated 10/25/50/75/100). Single source
 * of truth for "has this player finished their class's chain" -- shared by ClassTrainerNPC (its
 * own progress display) and any quest gating that depends on class-chain completion, so the two
 * can't drift out of sync with each other.
 */
public class ClassQuestChains {

    public static final List<String> WARRIOR = List.of(
            "trial_of_fury", "battle_hardened", "whirlwind_mastery", "iron_skin_trial", "war_cry"
    );
    public static final List<String> MAGE = List.of(
            "arcane_missiles_unlock", "temporal_mastery", "mana_burst_unlock", "arcane_barrier_unlock", "archmage_trial"
    );
    public static final List<String> ROGUE = List.of(
            "shadows_calling", "blink_strike_unlock", "poison_strike_unlock", "assassinate_unlock", "vanish_unlock"
    );

    public static List<String> forClass(String playerClass) {
        return switch (playerClass) {
            case "Warrior" -> WARRIOR;
            case "Mage" -> MAGE;
            case "Rogue" -> ROGUE;
            default -> List.of();
        };
    }
}
