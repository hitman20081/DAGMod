package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Shield Bash ability - Dash forward and knock back enemies
 * Requires shield in hand
 * Dash distance: 3-5 blocks
 * Damage: 4 (2 hearts)
 * Knockback: 5 blocks
 * Stun: Slowness II for 2 seconds
 */
public class ShieldBashAbility {
    private static final double DASH_DISTANCE = 4.0;
    private static final int DASH_DURATION_TICKS = 10; // Half a second
    private static final float DAMAGE = 4.0F;
    private static final double KNOCKBACK_STRENGTH = 2.5;
    private static final int STUN_DURATION_TICKS = 2 * 20; // 2 seconds

    /**
     * Check if player is holding a shield
     */
    public static boolean isHoldingShield(Player player) {
        return player.getMainHandItem().getItem() instanceof ShieldItem ||
                player.getOffhandItem().getItem() instanceof ShieldItem;
    }

    /**
     * Activate Shield Bash ability
     */
    public static boolean activate(Player player) {
        if (!(player.level() instanceof ServerLevel serverWorld)) {
            return false;
        }

        if (!isHoldingShield(player)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(
                        Component.literal("⛨ Shield Bash requires a shield!")
                                .withStyle(ChatFormatting.RED));
            }
            return false;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // Calculate dash direction
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 dashVec = lookVec.scale(DASH_DISTANCE);

        // Store initial position for collision detection
        Vec3 startPos = ((ServerPlayer) player).position();
        Vec3 endPos = startPos.add(dashVec);

        // Apply dash velocity
        player.setDeltaMovement(dashVec.x, 0.2, dashVec.z); // Slight upward for clearance

        // Make player briefly invulnerable during dash
        player.setInvulnerable(true);

        // Schedule removal of invulnerability
        serverWorld.getServer().execute(() -> {
            serverWorld.getServer().execute(() -> {
                if (player.isAlive()) {
                    player.setInvulnerable(false);
                }
            });
        });

        // Find and hit entities in path
        List<Entity> entities = findEntitiesInDashPath(serverWorld, player, startPos, endPos);
        int hitCount = 0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                // Deal damage
                livingEntity.hurt(serverWorld.damageSources().playerAttack(player), DAMAGE);

                // Apply knockback
                Vec3 knockbackVec = lookVec.scale(KNOCKBACK_STRENGTH);
                livingEntity.setDeltaMovement(
                        livingEntity.getDeltaMovement().add(knockbackVec.x, 0.5, knockbackVec.z)
                );

                // Apply stun (slowness)
                MobEffectInstance stun = new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        STUN_DURATION_TICKS,
                        1, // Slowness II
                        false,
                        true,
                        true
                );
                livingEntity.addEffect(stun);

                hitCount++;

                // Impact particles at entity location
                spawnImpactParticles(serverWorld, livingEntity.position());
            }
        }

        // Visual and audio effects
        spawnDashParticles(serverWorld, player, startPos, endPos);

        serverWorld.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK.value(),
                SoundSource.PLAYERS,
                1.5F,
                0.8F
        );

        // Feedback message
        if (hitCount > 0) {
            serverPlayer.sendOverlayMessage(
                    Component.literal("⛨ Shield Bash hit " + hitCount + " enemies!")
                            .withStyle(ChatFormatting.AQUA));
        }

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.SHIELD_BASH);

        return true;
    }

    /**
     * Find entities in the dash path
     */
    private static List<Entity> findEntitiesInDashPath(ServerLevel world, Player player, Vec3 start, Vec3 end) {
        // Create bounding box along the dash path
        AABB boundingBox = new AABB(start, end).inflate(1.5, 1.0, 1.5);

        return world.getEntities(player, boundingBox, entity ->
                entity instanceof LivingEntity
        );
    }

    /**
     * Spawn particles along the dash path
     */
    private static void spawnDashParticles(ServerLevel world, Player player, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize();

        for (double d = 0; d < DASH_DISTANCE; d += 0.5) {
            Vec3 pos = start.add(direction.scale(d));

            // Cloud particles for dash trail
            world.sendParticles(
                    ParticleTypes.CLOUD,
                    pos.x, pos.y + 0.5, pos.z,
                    2,
                    0.2, 0.2, 0.2,
                    0.02
            );

            // Sweep attack particles
            world.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    pos.x, pos.y + 1.0, pos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }
    }

    /**
     * Spawn impact particles when hitting an entity
     */
    private static void spawnImpactParticles(ServerLevel world, Vec3 pos) {
        // Explosion particles
        world.sendParticles(
                ParticleTypes.EXPLOSION,
                pos.x, pos.y + 1.0, pos.z,
                1,
                0.0, 0.0, 0.0,
                0.0
        );

        // Crit particles
        for (int i = 0; i < 10; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 0.5;
            double offsetY = world.getRandom().nextDouble() * 1.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 0.5;

            world.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1,
                    0.0, 0.1, 0.0,
                    0.1
            );
        }
    }
}