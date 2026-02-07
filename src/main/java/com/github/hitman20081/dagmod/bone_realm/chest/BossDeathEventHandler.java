package com.github.hitman20081.dagmod.bone_realm.chest;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Handles boss death events to spawn treasure chests
 */
public class BossDeathEventHandler {

    /**
     * Register the boss death listener
     * Call this in your main mod initialization
     */
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            onEntityDeath(entity, damageSource);
        });
    }

    /**
     * Called when any entity dies
     */
    private static void onEntityDeath(LivingEntity entity, DamageSource damageSource) {
        // Only process on server side
        if (entity.getEntityWorld().isClient()) {
            return;
        }

        // Check if this is a boss
        if (!BossChestSpawner.isBoss(entity)) {
            return;
        }

        // Get the killer if it was a player
        PlayerEntity killer = null;
        if (damageSource.getAttacker() instanceof PlayerEntity player) {
            killer = player;
        }

        // Spawn the chest
        BossChestSpawner.onBossDeath(entity, killer, entity.getEntityWorld());
    }
}