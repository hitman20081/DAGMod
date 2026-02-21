package com.github.hitman20081.dagmod.enchantment;

import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * Storage for Soul Bound items across player death/respawn.
 * Used by SoulBoundMixin (saves on death) and DagMod AFTER_RESPAWN (restores on respawn).
 * Stores slot indices so items can be restored to their original positions.
 */
public class SoulBoundStorage {

    private static final Map<UUID, List<ItemStack>> soulBoundItems = new HashMap<>();
    private static final Map<UUID, List<Integer>> soulBoundSlots = new HashMap<>();

    public static void store(UUID playerId, List<ItemStack> items, List<Integer> slots) {
        soulBoundItems.put(playerId, items);
        soulBoundSlots.put(playerId, slots);
    }

    /**
     * @deprecated Use {@link #store(UUID, List, List)} instead to preserve slot indices.
     */
    @Deprecated
    public static void store(UUID playerId, List<ItemStack> items) {
        soulBoundItems.put(playerId, items);
        soulBoundSlots.remove(playerId);
    }

    public static List<ItemStack> retrieve(UUID playerId) {
        return soulBoundItems.remove(playerId);
    }

    public static List<Integer> retrieveSlots(UUID playerId) {
        return soulBoundSlots.remove(playerId);
    }
}
