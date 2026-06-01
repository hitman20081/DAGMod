package com.github.hitman20081.dagmod.class_system.rogue.client;

public class ClientEnergyData {
    private static int currentEnergy = 0;
    private static int maxEnergy = 100;

    public static void setEnergy(int current, int max) {
        currentEnergy = current;
        maxEnergy = max;
    }

    public static int getCurrentEnergy() {
        return currentEnergy;
    }

    public static int getMaxEnergy() {
        return maxEnergy;
    }

    public static float getEnergyPercentage() {
        if (maxEnergy == 0) return 0;
        return (float) currentEnergy / maxEnergy;
    }
}
