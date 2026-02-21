package com.github.hitman20081.dagmod.class_system.mage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * TIME WARP - Mage Ability
 *
 * Cooldown: 45 seconds
 *
 * Effects:
 * - Slows all enemies in 10 block radius
 * - Slowness IV (80% slower) for 8 seconds
 * - Weakness II for 8 seconds
 * - Mining Fatigue III for 8 seconds
 * - Does not affect allies or yourself
 *
 * Visual: Cyan/light blue particles + eerie sound
 */
public class TimeWarpAbility {

    private static final double RADIUS = 10.0;
    private static final int DURATION_TICKS = 8 * 20; // 8 seconds

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        MageCooldownManager.startCooldown(player, MageAbility.TIME_WARP);

        // Find all entities in radius
        Box searchBox = Box.of(
                player.getEntityPos(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        int affectedCount = 0;

        for (LivingEntity entity : nearbyEntities) {
            // Check distance
            double distance = player.squaredDistanceTo(entity);
            if (distance <= RADIUS * RADIUS) {

                // Apply time slow effects
                entity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        DURATION_TICKS,
                        3, // Slowness IV (80% slower)
                        false,
                        true,
                        true
                ));

                entity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WEAKNESS,
                        DURATION_TICKS,
                        1, // Weakness II
                        false,
                        true,
                        true
                ));

                entity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.MINING_FATIGUE,
                        DURATION_TICKS,
                        2, // Mining Fatigue III
                        false,
                        true,
                        true
                ));

                // Particles around affected entity
                world.spawnParticles(
                        ParticleTypes.SOUL,
                        entity.getX(),
                        entity.getY() + entity.getHeight() / 2,
                        entity.getZ(),
                        30,
                        0.3, 0.5, 0.3,
                        0.02
                );

                affectedCount++;
            }
        }

        // VISUAL: Expanding time warp effect
        for (int i = 0; i < 60; i++) {
            double angle = i * Math.PI * 2 / 60;
            double x = player.getX() + Math.cos(angle) * RADIUS;
            double z = player.getZ() + Math.sin(angle) * RADIUS;
            double y = player.getY() + 1.0;

            world.spawnParticles(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Center portal effect
        world.spawnParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                100,
                RADIUS * 0.5, 1.0, RADIUS * 0.5,
                0.5
        );

        // SOUND: Time distortion
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_PORTAL_TRIGGER,
                SoundCategory.PLAYERS,
                1.0f,
                0.5f // Low pitch for eerie effect
        );

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_BEACON_DEACTIVATE,
                SoundCategory.PLAYERS,
                0.8f,
                0.7f
        );

        // FEEDBACK
        if (affectedCount > 0) {
            serverPlayer.sendMessage(
                    Text.literal("⏰ Time Warp! Slowed " + affectedCount + " enemies!")
                            .formatted(Formatting.AQUA, Formatting.BOLD),
                    true
            );
        } else {
            serverPlayer.sendMessage(
                    Text.literal("⏰ Time Warp! No enemies nearby.")
                            .formatted(Formatting.YELLOW),
                    true
            );
        }

        return true;
    }
}