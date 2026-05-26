package com.github.hitman20081.dagmod.client;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;

/**
 * Tracks the player's held-item light properties and position for dynamic lighting.
 *
 * Each light source has two independent properties:
 *   level  — peak block-light value at the player's position (0–15)
 *   radius — how many blocks the light reaches (Chebyshev distance)
 *
 * Brightness at a given block = max(0, level - level * dist / radius)
 * This scales the falloff to always reach exactly zero at the radius boundary
 * regardless of the item's peak level.
 *
 * No Minecraft client-only imports so this class loads safely on a dedicated
 * server (where it is never written to and always returns 0).
 *
 * Written by: DagModClient (client tick)
 * Read by:    DynamicLightMixin (BlockAndLightGetter.getLightLevel injection)
 */
public final class DynamicLightManager {

    // Player state — updated every client tick
    private static BlockPos playerPos      = BlockPos.ZERO;
    private static int      heldLightLevel = 0;
    private static int      heldRadius     = 0;

    // Rebuild tracking — used to schedule chunk re-renders
    private static BlockPos lastRebuildPos    = BlockPos.ZERO;
    private static int      lastRebuildRadius = 0;

    private DynamicLightManager() {}

    // -------------------------------------------------------------------------
    // State updates — called from DagModClient (client-only context)
    // -------------------------------------------------------------------------

    public static void updatePlayerLight(BlockPos pos, int lightLevel, int radius) {
        playerPos      = pos;
        heldLightLevel = lightLevel;
        heldRadius     = radius;
    }

    public static void clear() {
        heldLightLevel = 0;
        heldRadius     = 0;
    }

    // -------------------------------------------------------------------------
    // Rebuild tracking
    // -------------------------------------------------------------------------

    /** True when chunk rebuilds should be scheduled this tick. */
    public static boolean needsChunkRebuild(BlockPos currentPos, int currentRadius) {
        if (currentRadius != lastRebuildRadius) return true;
        if (currentRadius == 0) return false;
        return !currentPos.equals(lastRebuildPos);
    }

    public static BlockPos getLastRebuildPos()    { return lastRebuildPos; }
    public static int      getLastRebuildRadius() { return lastRebuildRadius; }

    public static void setLastRebuildState(BlockPos pos, int radius) {
        lastRebuildPos    = pos;
        lastRebuildRadius = radius;
    }

    // -------------------------------------------------------------------------
    // Light boost query — called from DynamicLightMixin, hot path
    // -------------------------------------------------------------------------

    /**
     * Returns the synthetic block-light contribution (0–15) at the given position.
     * Uses Chebyshev distance, scaled so brightness falls to 0 exactly at the radius.
     */
    public static int getLightBoost(BlockPos pos) {
        if (heldLightLevel == 0 || heldRadius == 0) return 0;
        int dx   = Math.abs(pos.getX() - playerPos.getX());
        int dy   = Math.abs(pos.getY() - playerPos.getY());
        int dz   = Math.abs(pos.getZ() - playerPos.getZ());
        int dist = Math.max(dx, Math.max(dy, dz));
        if (dist >= heldRadius) return 0;
        return Math.max(0, heldLightLevel - heldLightLevel * dist / heldRadius);
    }

    // -------------------------------------------------------------------------
    // Item tables — level (peak brightness) and radius (reach in blocks)
    //
    // Increase radius to cast light further; increase level to make nearby
    // blocks brighter. They are independent.
    // -------------------------------------------------------------------------

    public static int getLightLevelForItem(Item item) {
        if (item == Items.TORCH)            return 14;
        if (item == Items.SOUL_TORCH)       return 10;
        if (item == Items.LANTERN)          return 15;
        if (item == Items.SOUL_LANTERN)     return 10;
        if (item == Items.GLOWSTONE)        return 15;
        if (item == Items.SEA_LANTERN)      return 15;
        if (item == Items.SHROOMLIGHT)      return 15;
        if (item == Items.JACK_O_LANTERN)   return 15;
        if (item == Items.CAMPFIRE)         return 15;
        if (item == Items.SOUL_CAMPFIRE)    return 10;
        if (item == Items.LAVA_BUCKET)      return 15;
        if (item == Items.BLAZE_ROD)        return 10;
        if (item == Items.FIRE_CHARGE)      return 13;
        if (item == Items.END_ROD)          return 14;
        if (item == Items.GLOW_BERRIES)     return  8;
        if (item == Items.GLOWSTONE_DUST)   return  6;
        if (item == Items.REDSTONE_TORCH)   return  7;
        if (item == Items.NETHER_STAR)      return 15;
        if (item == Items.MAGMA_BLOCK)      return  3;
        return 0;
    }

    public static int getRadiusForItem(Item item) {
        if (item == Items.TORCH)            return 14;  // standard torch, wide reach
        if (item == Items.SOUL_TORCH)       return 10;  // narrower, eerie light
        if (item == Items.LANTERN)          return 15;  // enclosed flame, full range
        if (item == Items.SOUL_LANTERN)     return 10;
        if (item == Items.GLOWSTONE)        return 15;  // block-in-hand, full range
        if (item == Items.SEA_LANTERN)      return 15;
        if (item == Items.SHROOMLIGHT)      return 15;
        if (item == Items.JACK_O_LANTERN)   return 15;
        if (item == Items.CAMPFIRE)         return 15;  // roaring fire
        if (item == Items.SOUL_CAMPFIRE)    return 10;
        if (item == Items.LAVA_BUCKET)      return 12;  // intense but contained
        if (item == Items.BLAZE_ROD)        return  8;  // small smouldering rod
        if (item == Items.FIRE_CHARGE)      return 10;  // compact fireball
        if (item == Items.END_ROD)          return 12;  // focused beam
        if (item == Items.GLOW_BERRIES)     return  5;  // handful of small fruit
        if (item == Items.GLOWSTONE_DUST)   return  4;  // loose powder
        if (item == Items.REDSTONE_TORCH)   return  5;  // dim signal torch
        if (item == Items.NETHER_STAR)      return 15;  // shining artefact
        if (item == Items.MAGMA_BLOCK)      return  3;  // barely warm
        return 0;
    }
}
