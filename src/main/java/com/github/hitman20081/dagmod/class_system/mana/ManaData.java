package com.github.hitman20081.dagmod.class_system.mana;

import net.minecraft.nbt.NbtCompound;

public class ManaData {
    private static final int MAX_MANA = 100;
    private float currentMana;

    public ManaData() {
        this.currentMana = MAX_MANA;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return MAX_MANA;
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
        currentMana = Math.min(currentMana + amount, MAX_MANA);
    }

    public void setMana(float amount) {
        currentMana = Math.max(0, Math.min(amount, MAX_MANA));
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("mana", currentMana);
    }

    public void readFromNbt(NbtCompound nbt) {
        currentMana = nbt.getFloat("mana").orElse((float)MAX_MANA);
    }
}