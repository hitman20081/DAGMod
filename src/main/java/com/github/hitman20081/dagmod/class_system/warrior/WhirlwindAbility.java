package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.WHIRLWIND);

        // Find all entities in radius
        Box searchBox = Box.of(
                player.getEntityPos(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<Entity> nearbyEntities = world.getOtherEntities(player, searchBox);
        int hitCount = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                // Check distance
                double distance = player.squaredDistanceTo(entity);
                if (distance <= RADIUS * RADIUS) {

                    // Deal damage (generic damage source ignores armor)
                    DamageSource damageSource = player.getDamageSources().playerAttack(serverPlayer);
                    livingEntity.damage(world, damageSource, DAMAGE);

                    // Knockback
                    double dx = entity.getX() - player.getX();
                    double dz = entity.getZ() - player.getZ();
                    double distance2d = Math.sqrt(dx * dx + dz * dz);

                    if (distance2d > 0) {
                        dx /= distance2d;
                        dz /= distance2d;

                        entity.setVelocity(
                                dx * KNOCKBACK_STRENGTH,
                                0.3, // Upward knockback
                                dz * KNOCKBACK_STRENGTH
                        );
                        entity.velocityModified = true;
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

            world.spawnParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Additional cloud particles
        world.spawnParticles(
                ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.5, player.getZ(),
                20,
                RADIUS * 0.5, 0.2, RADIUS * 0.5,
                0.05
        );

        // SOUND: Swoosh
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                1.0f,
                0.8f
        );

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_IRON_GOLEM_ATTACK,
                SoundCategory.PLAYERS,
                0.5f,
                1.5f
        );

        // FEEDBACK
        if (hitCount > 0) {
            serverPlayer.sendMessage(
                    Text.literal("🌪 Whirlwind! Hit " + hitCount + " enemies!")
                            .formatted(Formatting.GOLD, Formatting.BOLD),
                    true
            );
        } else {
            serverPlayer.sendMessage(
                    Text.literal("🌪 Whirlwind! No enemies nearby.")
                            .formatted(Formatting.YELLOW),
                    true
            );
        }

        return true;
    }
}