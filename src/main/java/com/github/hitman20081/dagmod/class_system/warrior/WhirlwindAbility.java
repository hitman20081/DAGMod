package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
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
 * WHIRLWIND - Warrior Ability
 *
 * Cooldown: 30 seconds
 *
 * Effects:
 * - Spin attack hitting all enemies in 5 block radius
 * - Deals 8 damage (4 hearts) to each enemy
 * - Knocks back enemies away from player
 * - Ignores armor (true damage)
 *
 * Visual: Sweep particles + swoosh sound
 */
public class WhirlwindAbility {

    private static final double RADIUS = 5.0;
    private static final float DAMAGE = 8.0f; // 4 hearts
    private static final double KNOCKBACK_STRENGTH = 0.8;

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.WHIRLWIND);

        // Find all entities in radius
        AABB searchBox = AABB.ofSize(
                player.position(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<Entity> nearbyEntities = world.getEntities(player, searchBox);
        int hitCount = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                // Check distance
                double distance = player.distanceToSqr(entity);
                if (distance <= RADIUS * RADIUS) {

                    // Deal damage (generic damage source ignores armor)
                    DamageSource damageSource = player.damageSources().playerAttack(serverPlayer);
                    livingEntity.hurt(damageSource, DAMAGE);

                    // Knockback
                    double dx = entity.getX() - player.getX();
                    double dz = entity.getZ() - player.getZ();
                    double distance2d = Math.sqrt(dx * dx + dz * dz);

                    if (distance2d > 0) {
                        dx /= distance2d;
                        dz /= distance2d;

                        entity.setDeltaMovement(
                                dx * KNOCKBACK_STRENGTH,
                                0.3, // Upward knockback
                                dz * KNOCKBACK_STRENGTH
                        );
                    }

                    hitCount++;
                }
            }
        }

        // VISUAL: Sweep attack particles in circle
        for (int i = 0; i < 36; i++) {
            double angle = i * 10 * Math.PI / 180.0;
            double x = player.getX() + Math.cos(angle) * RADIUS;
            double z = player.getZ() + Math.sin(angle) * RADIUS;
            double y = player.getY() + 0.5;

            world.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Additional cloud particles
        world.sendParticles(
                ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.5, player.getZ(),
                20,
                RADIUS * 0.5, 0.2, RADIUS * 0.5,
                0.05
        );

        // SOUND: Swoosh
        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                1.0f,
                0.8f
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.IRON_GOLEM_ATTACK,
                SoundSource.PLAYERS,
                0.5f,
                1.5f
        );

        // FEEDBACK
        if (hitCount > 0) {
            serverPlayer.sendOverlayMessage(
                    Component.literal("🌪 Whirlwind! Hit " + hitCount + " enemies!")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        } else {
            serverPlayer.sendOverlayMessage(
                    Component.literal("🌪 Whirlwind! No enemies nearby.")
                            .withStyle(ChatFormatting.YELLOW));
        }

        return true;
    }
}