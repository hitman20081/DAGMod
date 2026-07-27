package com.github.hitman20081.dagmod.class_system.mana;

import net.minecraft.nbt.CompoundTag;

public class ManaData {
    static final int BASE_MAX_MANA = 100;
    private float currentMana;
    private int maxMana;

    public ManaData() {
        this.maxMana = BASE_MAX_MANA;
        this.currentMana = BASE_MAX_MANA;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int newMax) {
        this.maxMana = Math.max(1, newMax);
        if (currentMana > maxMana) currentMana = maxMana;
    }

    public boolean hasMana(float amount) {
        return currentMana >= amount;
    }

    public boolean useMana(float amount) {
        if (hasMana(amount)) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public void addMana(float amount) {
        currentMana = Math.min(currentMana + amount, maxMana);
    }

    public void setMana(float amount) {
        currentMana = Math.max(0, Math.min(amount, maxMana));
    }

    public void writeToNbt(CompoundTag nbt) {
        nbt.putFloat("mana", currentMana);
    }

    public void readFromNbt(CompoundTag nbt) {
        currentMana = nbt.getFloat("mana").orElse((float)BASE_MAX_MANA);
    }
}