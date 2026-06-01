package com.github.hitman20081.dagmod.class_system.warrior.client;

import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;

public class ClientCooldownData {
    // Indexed by WarriorAbility.ordinal(); 0 = ready
    private static final int[] remainingTicks = new int[WarriorAbility.values().length];

    public static void update(int rage, int shieldBash, int warCry,
                              int battleShout, int whirlwind, int ironSkin) {
        remainingTicks[WarriorAbility.RAGE.ordinal()]         = rage;
        remainingTicks[WarriorAbility.SHIELD_BASH.ordinal()]  = shieldBash;
        remainingTicks[WarriorAbility.WAR_CRY.ordinal()]      = warCry;
        remainingTicks[WarriorAbility.BATTLE_SHOUT.ordinal()] = battleShout;
        remainingTicks[WarriorAbility.WHIRLWIND.ordinal()]    = whirlwind;
        remainingTicks[WarriorAbility.IRON_SKIN.ordinal()]    = ironSkin;
    }

    public static int getRemainingTicks(WarriorAbility ability) {
        return remainingTicks[ability.ordinal()];
    }

    public static int getRemainingSeconds(WarriorAbility ability) {
        return (int) Math.ceil(remainingTicks[ability.ordinal()] / 20.0);
    }

    public static boolean isReady(WarriorAbility ability) {
        return remainingTicks[ability.ordinal()] <= 0;
    }
}
