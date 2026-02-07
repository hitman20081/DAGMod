package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
 * BLINK STRIKE - Rogue Ability
 *
 * Cooldown: 25 seconds
 *
 * Effects:
 * - Teleport directly behind nearest enemy within 15 blocks
 * - Brief invisibility (2 seconds)
 * - Speed boost after teleport (3 seconds)
 * - Perfect for engaging or escaping
 *
 * Visual: Dark purple/black smoke particles
 */
public class BlinkStrikeAbility {

    private static final double SEARCH_RADIUS = 15.0;
    private static final double TELEPORT_DISTANCE = 2.0;

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Find nearest enemy
        Box searchBox = Box.of(
                player.getEntityPos(),
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        if (nearbyEntities.isEmpty()) {
            player.sendMessage(
                    Text.literal("No enemies in range!")
                            .formatted(Formatting.RED),
                    true
            );
            return false;
        }

        // Find closest enemy
        LivingEntity closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : nearbyEntities) {
            double distance = player.squaredDistanceTo(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = entity;
            }
        }

        if (closestEnemy == null) {
            return false;
        }

        // Calculate position behind target
        Vec3d targetPos = closestEnemy.getEntityPos();
        Vec3d targetLookVec = closestEnemy.getRotationVec(1.0f);

        Vec3d behindPos = targetPos.subtract(
                targetLookVec.x * TELEPORT_DISTANCE,
                0,
                targetLookVec.z * TELEPORT_DISTANCE
        );

        // Particles at old position
        world.spawnParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        world.spawnParticles(
                ParticleTypes.LARGE_SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                15,
                0.3, 0.5, 0.3,
                0.05
        );

        // TELEPORT
        player.teleport(behindPos.x, behindPos.y, behindPos.z, true);

        // Face the target
        Vec3d lookDirection = targetPos.subtract(player.getEntityPos()).normalize();
        float yaw = (float)(Math.atan2(lookDirection.z, lookDirection.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.asin(-lookDirection.y) * 180.0 / Math.PI);
        player.setYaw(yaw);
        player.setPitch(pitch);

        // Particles at new position
        world.spawnParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        world.spawnParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                20,
                0.5, 1.0, 0.5,
                0.5
        );

        // Brief invisibility + speed
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.INVISIBILITY,
                2 * 20,
                0,
                false,
                false,
                true
        ));

        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SPEED,
                3 * 20,
                1,
                false,
                true,
                true
        ));

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.BLINK_STRIKE);

        // SOUND
        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1.0f,
                1.2f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("🌑 Blink Strike! Behind " + closestEnemy.getName().getString() + "!")
                        .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                true
        );

        return true;
    }
}