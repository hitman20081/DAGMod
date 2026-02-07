package com.github.hitman20081.dagmod.trade;

import net.minecraft.nbt.NbtCompound;

/**
 * Stores rotation state for a single merchant type.
 * Tracks the current rotation index for that merchant's premium trades.
 */
public class MerchantRotationState {
    private final MerchantType merchantType;
    private int currentRotationIndex;

    public MerchantRotationState(MerchantType merchantType) {
        this.merchantType = merchantType;
        this.currentRotationIndex = 0;
    }

    public MerchantRotationState(MerchantType merchantType, int rotationIndex) {
        this.merchantType = merchantType;
        this.currentRotationIndex = rotationIndex;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public int getCurrentRotationIndex() {
        return currentRotationIndex;
    }

    /**
     * Advance to the next rotation, wrapping around based on max rotations.
     *
     * @param maxRotations The total number of rotation sets for this merchant
     */
    public void advanceRotation(int maxRotations) {
        if (maxRotations > 0) {
            currentRotationIndex = (currentRotationIndex + 1) % maxRotations;
        }
    }

    /**
     * Serialize this state to NBT.
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("rotationIndex", currentRotationIndex);
        return nbt;
    }

    /**
     * Deserialize state from NBT.
     */
    public static MerchantRotationState fromNbt(MerchantType type, NbtCompound nbt) {
        int index = nbt.getInt("rotationIndex").orElse(0);
        return new MerchantRotationState(type, index);
    }
}
