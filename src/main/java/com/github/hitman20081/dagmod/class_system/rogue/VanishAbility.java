package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.AABB;

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

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.VANISH);

        // Grant invisibility and speed
        player.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY,
                INVISIBILITY_DURATION,
                0,
                false,
                false,
                true
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.SPEED,
                INVISIBILITY_DURATION,
                1,
                false,
                true,
                true
        ));

        // Find and blind nearby enemies
        AABB searchBox = AABB.ofSize(
                player.position(),
                BLIND_RADIUS * 2,
                BLIND_RADIUS * 2,
                BLIND_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        int blindedCount = 0;
        for (LivingEntity entity : nearbyEntities) {
            double distance = player.distanceToSqr(entity);
            if (distance <= BLIND_RADIUS * BLIND_RADIUS) {
                entity.addEffect(new MobEffectInstance(
                        MobEffects.BLINDNESS,
                        BLIND_DURATION,
                        0,
                        false,
                        true,
                        true
                ));

                entity.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS,
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

            world.sendParticles(
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

            world.sendParticles(
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
                SoundEvents.GENERIC_EXTINGUISH_FIRE,
                SoundSource.PLAYERS,
                1.5f,
                0.5f
        );

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.TNT_PRIMED,
                SoundSource.PLAYERS,
                0.5f,
                2.0f
        );

        // FEEDBACK
        if (blindedCount > 0) {
            serverPlayer.sendOverlayMessage(
                    Component.literal("💨 Vanish! Blinded " + blindedCount + " enemies!")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        } else {
            serverPlayer.sendOverlayMessage(
                    Component.literal("💨 Vanish! Disappeared into shadows!")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        }

        return true;
    }
}