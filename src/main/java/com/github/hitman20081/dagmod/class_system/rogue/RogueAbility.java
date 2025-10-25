package com.github.hitman20081.dagmod.class_system.rogue;

/**
 * Enum representing cooldown-based Rogue abilities
 * Separate from the energy-based abilities in RogueAbilityManager
 *
 * These are permanent ability items with cooldowns (no energy cost)
 */
public enum RogueAbility {
    BLINK_STRIKE("Blink Strike", 25 * 20, 0x8B008B),      // 25 seconds, Dark purple
    VANISH("Vanish", 40 * 20, 0x696969),                   // 40 seconds, Gray
    POISON_STRIKE("Poison Strike", 20 * 20, 0x00FF00),     // 20 seconds, Green
    ASSASSINATE("Assassinate", 15 * 20, 0xFF0000);         // 15 seconds, Red

    private final String displayName;
    private final int cooldownTicks;
    private final int color;

    RogueAbility(String displayName, int cooldownTicks, int color) {
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