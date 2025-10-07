package com.github.hitman20081.dagmod.class_system.mana.client;

public class ClientManaData {
    private static float currentMana = 0;
    private static int maxMana = 100;

    public static void setMana(float current, int max) {
        currentMana = current;
        maxMana = max;
    }

    public static float getCurrentMana() {
        return currentMana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public static float getManaPercentage() {
        if (maxMana == 0) return 0;
        return currentMana / maxMana;
    }
}