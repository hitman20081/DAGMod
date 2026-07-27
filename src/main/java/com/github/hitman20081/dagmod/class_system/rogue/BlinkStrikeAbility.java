package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Find nearest enemy
        AABB searchBox = AABB.ofSize(
                player.position(),
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        if (nearbyEntities.isEmpty()) {
            player.sendOverlayMessage(
                    Component.literal("No enemies in range!")
                            .withStyle(ChatFormatting.RED));
            return false;
        }

        // Find closest enemy
        LivingEntity closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : nearbyEntities) {
            double distance = player.distanceToSqr(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = entity;
            }
        }

        if (closestEnemy == null) {
            return false;
        }

        // Calculate position behind target
        Vec3 targetPos = closestEnemy.position();
        Vec3 targetLookVec = closestEnemy.getViewVector(1.0f);

        Vec3 behindPos = targetPos.subtract(
                targetLookVec.x * TELEPORT_DISTANCE,
                0,
                targetLookVec.z * TELEPORT_DISTANCE
        );

        // Particles at old position
        world.sendParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        world.sendParticles(
                ParticleTypes.LARGE_SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                15,
                0.3, 0.5, 0.3,
                0.05
        );

        // TELEPORT
        serverPlayer.connection.teleport(behindPos.x, behindPos.y, behindPos.z, player.getYRot(), player.getXRot());

        // Face the target
        Vec3 lookDirection = targetPos.subtract(player.position()).normalize();
        float yaw = (float)(Math.atan2(lookDirection.z, lookDirection.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.asin(-lookDirection.y) * 180.0 / Math.PI);
        player.setYRot(yaw);
        player.setXRot(pitch);

        // Particles at new position
        world.sendParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        world.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                20,
                0.5, 1.0, 0.5,
                0.5
        );

        // Brief invisibility + speed
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.INVISIBILITY,
                2 * 20,
                0,
                false,
                false,
                true
        ));

        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SPEED,
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
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0f,
                1.2f
        );

        // FEEDBACK
        serverPlayer.sendOverlayMessage(
                Component.literal("🌑 Blink Strike! Behind " + closestEnemy.getName().getString() + "!")
                        .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));

        return true;
    }
}