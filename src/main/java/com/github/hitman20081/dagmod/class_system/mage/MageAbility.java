package com.github.hitman20081.dagmod.class_system.mage;

/**
 * Enum representing all Mage permanent abilities
 * These are different from spell scrolls - they use cooldowns instead of consumables
 */
public enum MageAbility {
    // Existing wands/scrolls are separate - these are NEW permanent abilities
    ARCANE_MISSILES("Arcane Missiles", 20 * 20, 0x9933FF),     // 20 seconds, Purple
    TIME_WARP("Time Warp", 45 * 20, 0x00FFFF),                 // 45 seconds, Cyan
    MANA_BURST("Mana Burst", 30 * 20, 0x0099FF),               // 30 seconds, Blue
    ARCANE_BARRIER("Arcane Barrier", 60 * 20, 0xFF00FF);       // 60 seconds, Magenta

    private final String displayName;
    private final int cooldownTicks;
    private final int color; // RGB color for HUD display

    MageAbility(String displayName, int cooldownTicks, int color) {
        this.displayName = displayName;
        this.cooldownTicks = cooldownTicks;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public int getColor() {
        return color;
    }

    public int getCooldownSeconds() {
        return cooldownTicks / 20;
    }
}