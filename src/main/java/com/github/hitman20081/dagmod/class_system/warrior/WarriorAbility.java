package com.github.hitman20081.dagmod.class_system.warrior;

/**
 * Enum representing all Warrior abilities
 */
public enum WarriorAbility {
    RAGE("Rage", CooldownManager.RAGE_COOLDOWN, 0xCC0000),
    SHIELD_BASH("Shield Bash", CooldownManager.SHIELD_BASH_COOLDOWN, 0x4A90E2),
    WAR_CRY("War Cry", CooldownManager.WAR_CRY_COOLDOWN, 0xFFD700),

    // NEW ABILITIES
    BATTLE_SHOUT("Battle Shout", CooldownManager.BATTLE_SHOUT_COOLDOWN, 0xFF4500),  // Red-Orange
    WHIRLWIND("Whirlwind", CooldownManager.WHIRLWIND_COOLDOWN, 0x87CEEB),          // Sky Blue
    IRON_SKIN("Iron Skin", CooldownManager.IRON_SKIN_COOLDOWN, 0x808080);          // Gray

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