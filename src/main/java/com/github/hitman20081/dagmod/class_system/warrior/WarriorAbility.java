package com.github.hitman20081.dagmod.class_system.warrior;

/**
 * Enum representing all Warrior abilities
 */
public enum WarriorAbility {
    RAGE("Rage", CooldownManager.RAGE_COOLDOWN, 0xCC0000),
    SHIELD_BASH("Shield Bash", CooldownManager.SHIELD_BASH_COOLDOWN, 0x4A90E2),
    WAR_CRY("War Cry", CooldownManager.WAR_CRY_COOLDOWN, 0xFFD700);

    private final String displayName;
    private final int cooldownTicks;
    private final int color; // RGB color for HUD display

    WarriorAbility(String displayName, int cooldownTicks, int color) {
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