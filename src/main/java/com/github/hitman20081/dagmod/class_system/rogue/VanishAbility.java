package com.github.hitman20081.dagmod.class_system.rogue;

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
 * VANISH - Rogue Ability
 *
 * Cooldown: 40 seconds
 *
 * Effects:
 * - Invisibility for 8 seconds
 * - Speed II for 8 seconds
 * - Blinds nearby enemies for 4 seconds
 * - Perfect escape tool
 *
 * Visual: Gray smoke cloud
 */
public class VanishAbility {

    private static final double BLIND_RADIUS = 6.0;
    private static final int INVISIBILITY_DURATION = 8 * 20;
    private static final int BLIND_DURATION = 4 * 20;

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.VANISH);

        // Grant invisibility and speed
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                INVISIBILITY_DURATION,
                0,
                false,
                false,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                INVISIBILITY_DURATION,
                1,
                false,
                true,
                true
        ));

        // Find and blind nearby enemies
        Box searchBox = Box.of(
                player.getEntityPos(),
                BLIND_RADIUS * 2,
                BLIND_RADIUS * 2,
                BLIND_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        int blindedCount = 0;
        for (LivingEntity entity : nearbyEntities) {
            double distance = player.squaredDistanceTo(entity);
            if (distance <= BLIND_RADIUS * BLIND_RADIUS) {
                entity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS,
                        BLIND_DURATION,
                        0,
                        false,
                        true,
                        true
                ));

                entity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        BLIND_DURATION,
                        0,
                        false,
                        true,
                        true
                ));

                blindedCount++;
            }
        }

        // VISUAL: Smoke cloud
        for (int i = 0; i < 100; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 4;
            double offsetY = world.getRandom().nextDouble() * 3;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 4;

            world.spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    1,
                    0.1, 0.1, 0.1,
                    0.02
            );
        }

        for (int i = 0; i < 150; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 6;
            double offsetY = world.getRandom().nextDouble() * 4;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 6;

            world.spawnParticles(
                    ParticleTypes.SMOKE,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    1,
                    0.2, 0.2, 0.2,
                    0.05
            );
        }

        // SOUND
        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                SoundCategory.PLAYERS,
                1.5f,
                0.5f
        );

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_TNT_PRIMED,
                SoundCategory.PLAYERS,
                0.5f,
                2.0f
        );

        // FEEDBACK
        if (blindedCount > 0) {
            serverPlayer.sendMessage(
                    Text.literal("💨 Vanish! Blinded " + blindedCount + " enemies!")
                            .formatted(Formatting.GRAY, Formatting.BOLD),
                    true
            );
        } else {
            serverPlayer.sendMessage(
                    Text.literal("💨 Vanish! Disappeared into shadows!")
                            .formatted(Formatting.GRAY, Formatting.BOLD),
                    true
            );
        }

        return true;
    }
}