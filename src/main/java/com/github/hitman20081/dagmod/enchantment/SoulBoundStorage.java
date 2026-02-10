package com.github.hitman20081.dagmod.enchantment;

import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * Storage for Soul Bound items across player death/respawn.
 * Used by SoulBoundMixin (saves on death) and DagMod AFTER_RESPAWN (restores on respawn).
 */
public class SoulBoundStorage {

    private static final Map<UUID, List<ItemStack>> soulBoundItems = new HashMap<>();

    public static void store(UUID playerId, List<ItemStack> items) {
        soulBoundItems.put(playerId, items);
    }

    public static List<ItemStack> retrieve(UUID playerId) {
        return soulBoundItems.remove(playerId);
    }
}
