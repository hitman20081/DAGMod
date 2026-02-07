package com.github.hitman20081.dagmod.util;

import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlock;

public class ChestTextureHolder {
    private static final ThreadLocal<LockedBoneChestBlock.LockedChestType> customChestType =
            ThreadLocal.withInitial(() -> null);

    // For custom chests that aren't locked (like Iron Chest)
    private static final ThreadLocal<String> customTextureName =
            ThreadLocal.withInitial(() -> null);

    public static void setCustomChestType(LockedBoneChestBlock.LockedChestType type) {
        customChestType.set(type);
    }

    public static LockedBoneChestBlock.LockedChestType getCustomChestType() {
        return customChestType.get();
    }

    public static void setCustomTextureName(String textureName) {
        customTextureName.set(textureName);
    }

    public static String getCustomTextureName() {
        return customTextureName.get();
    }

    public static void clear() {
        customChestType.remove();
        customTextureName.remove();
    }
}