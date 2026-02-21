package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.nbt.NbtCompound;

/**
 * Simple energy data storage for Rogues
 * Mirrors the ManaData pattern
 */
public class EnergyData {
    private static final int MAX_ENERGY = 100;
    private int currentEnergy;

    public EnergyData() {
        this.currentEnergy = MAX_ENERGY;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
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
        currentEnergy = Math.min(currentEnergy + amount, MAX_ENERGY);
    }

    public void setEnergy(int amount) {
        currentEnergy = Math.max(0, Math.min(amount, MAX_ENERGY));
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("energy", currentEnergy);
    }

    public void readFromNbt(NbtCompound nbt) {
        currentEnergy = nbt.getInt("energy").orElse(MAX_ENERGY);
    }
}