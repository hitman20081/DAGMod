package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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
    public static boolean isHoldingShield(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof ShieldItem ||
                player.getOffHandStack().getItem() instanceof ShieldItem;
    }

    /**
     * Activate Shield Bash ability
     */
    public static boolean activate(PlayerEntity player) {
        if (!(player.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        if (!isHoldingShield(player)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(
                        Text.literal("⛨ Shield Bash requires a shield!")
                                .formatted(Formatting.RED),
                        true
                );
            }
            return false;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Calculate dash direction
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d dashVec = lookVec.multiply(DASH_DISTANCE);

        // Store initial position for collision detection
        Vec3d startPos = ((ServerPlayerEntity) player).getCommandSource().getPosition();
        Vec3d endPos = startPos.add(dashVec);

        // Apply dash velocity
        player.setVelocity(dashVec.x, 0.2, dashVec.z); // Slight upward for clearance
        player.velocityModified = true;

        // Make player briefly invulnerable during dash
        player.setInvulnerable(true);
        player.timeUntilRegen = 0; // Reset regen timer

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
                livingEntity.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), DAMAGE);

                // Apply knockback
                Vec3d knockbackVec = lookVec.multiply(KNOCKBACK_STRENGTH);
                livingEntity.setVelocity(
                        livingEntity.getVelocity().add(knockbackVec.x, 0.5, knockbackVec.z)
                );
                livingEntity.velocityModified = true;

                // Apply stun (slowness)
                StatusEffectInstance stun = new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        STUN_DURATION_TICKS,
                        1, // Slowness II
                        false,
                        true,
                        true
                );
                livingEntity.addStatusEffect(stun);

                hitCount++;

                // Impact particles at entity location
                spawnImpactParticles(serverWorld, livingEntity.getTrackedPosition().getPos());
            }
        }

        // Visual and audio effects
        spawnDashParticles(serverWorld, player, startPos, endPos);

        serverWorld.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_SHIELD_BLOCK.value(),
                SoundCategory.PLAYERS,
                1.5F,
                0.8F
        );

        // Feedback message
        if (hitCount > 0) {
            serverPlayer.sendMessage(
                    Text.literal("⛨ Shield Bash hit " + hitCount + " enemies!")
                            .formatted(Formatting.AQUA),
                    true
            );
        }

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.SHIELD_BASH);

        return true;
    }

    /**
     * Find entities in the dash path
     */
    private static List<Entity> findEntitiesInDashPath(ServerWorld world, PlayerEntity player, Vec3d start, Vec3d end) {
        // Create bounding box along the dash path
        Box boundingBox = new Box(start, end).expand(1.5, 1.0, 1.5);

        return world.getOtherEntities(player, boundingBox, entity ->
                entity instanceof LivingEntity
        );
    }

    /**
     * Spawn particles along the dash path
     */
    private static void spawnDashParticles(ServerWorld world, PlayerEntity player, Vec3d start, Vec3d end) {
        Vec3d direction = end.subtract(start).normalize();

        for (double d = 0; d < DASH_DISTANCE; d += 0.5) {
            Vec3d pos = start.add(direction.multiply(d));

            // Cloud particles for dash trail
            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    pos.x, pos.y + 0.5, pos.z,
                    2,
                    0.2, 0.2, 0.2,
                    0.02
            );

            // Sweep attack particles
            world.spawnParticles(
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
    private static void spawnImpactParticles(ServerWorld world, Vec3d pos) {
        // Explosion particles
        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                pos.x, pos.y + 1.0, pos.z,
                1,
                0.0, 0.0, 0.0,
                0.0
        );

        // Crit particles
        for (int i = 0; i < 10; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.5;
            double offsetY = world.random.nextDouble() * 1.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;

            world.spawnParticles(
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