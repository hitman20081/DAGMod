package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.nbt.CompoundTag;

/**
 * Simple energy data storage for Rogues
 * Mirrors the ManaData pattern
 */
public class EnergyData {
    static final int BASE_MAX_ENERGY = 100;
    private int currentEnergy;
    private int maxEnergy;

    public EnergyData() {
        this.maxEnergy = BASE_MAX_ENERGY;
        this.currentEnergy = BASE_MAX_ENERGY;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int newMax) {
        this.maxEnergy = Math.max(1, newMax);
        if (currentEnergy > maxEnergy) currentEnergy = maxEnergy;
    }

    public boolean hasEnergy(int amount) {
        return currentEnergy >= amount;
    }

    public boolean useEnergy(int amount) {
        if (hasEnergy(amount)) {
            currentEnergy -= amount;
            return true;
        }
        return false;
    }

    public void addEnergy(int amount) {
        currentEnergy = Math.min(currentEnergy + amount, maxEnergy);
    }

    public void setEnergy(int amount) {
        currentEnergy = Math.max(0, Math.min(amount, maxEnergy));
    }

    public void writeToNbt(CompoundTag nbt) {
        nbt.putInt("energy", currentEnergy);
    }

    public void readFromNbt(CompoundTag nbt) {
        currentEnergy = nbt.getInt("energy").orElse(BASE_MAX_ENERGY);
    }
}