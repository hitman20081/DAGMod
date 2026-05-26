package com.github.hitman20081.dagmod.bone_realm.chest;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

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
        if (entity.level().isClientSide()) {
            return;
        }

        // Check if this is a boss
        if (!BossChestSpawner.isBoss(entity)) {
            return;
        }

        // Get the killer if it was a player
        Player killer = null;
        if (damageSource.getEntity() instanceof Player player) {
            killer = player;
        }

        // Spawn the chest
        BossChestSpawner.onBossDeath(entity, killer, entity.level());
    }
}