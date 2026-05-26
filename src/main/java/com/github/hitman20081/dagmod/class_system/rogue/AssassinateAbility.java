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
 * ASSASSINATE - Rogue Ability
 *
 * Cooldown: 15 seconds
 *
 * Effects:
 * - Deals massive damage if attacking from behind (20 damage / 10 hearts)
 * - Reduced damage if not behind (10 damage / 5 hearts)
 * - Manual activation of backstab mechanic
 *
 * Visual: Red blood particles + critical hit animation
 */
public class AssassinateAbility {

    private static final double MELEE_RANGE = 4.0;
    private static final float BACKSTAB_DAMAGE = 20.0f;
    private static final float FRONTSTAB_DAMAGE = 10.0f;

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.ASSASSINATE);

        // Find target in melee range
        LivingEntity target = findMeleeTarget(player);

        if (target == null) {
            player.sendOverlayMessage(
                    Component.literal("No target in melee range!")
                            .withStyle(ChatFormatting.YELLOW));
            return true;
        }

        // Check if attacking from behind
        boolean isBackstab = isAttackingFromBehind(player, target);

        float damage = isBackstab ? BACKSTAB_DAMAGE : FRONTSTAB_DAMAGE;

        // Deal damage
        target.hurt(world.damageSources().playerAttack(serverPlayer), damage);

        // VISUAL
        if (isBackstab) {
            for (int i = 0; i < 50; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
                double offsetY = world.getRandom().nextDouble() * 2.0;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

                world.sendParticles(
                        ParticleTypes.DAMAGE_INDICATOR,
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ,
                        1,
                        0, 0, 0,
                        0
                );
            }

            world.sendParticles(
                    ParticleTypes.CRIMSON_SPORE,
                    target.getX(),
                    target.getY() + target.getBbHeight() / 2,
                    target.getZ(),
                    40,
                    0.5, 0.5, 0.5,
                    0.2
            );

        } else {
            for (int i = 0; i < 25; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.0;
                double offsetY = world.getRandom().nextDouble() * 1.5;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.0;

                world.sendParticles(
                        ParticleTypes.CRIT,
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        world.sendParticles(
                ParticleTypes.SWEEP_ATTACK,
                target.getX(),
                target.getY() + 1.0,
                target.getZ(),
                3,
                0.5, 0.5, 0.5,
                0
        );

        // SOUND
        if (isBackstab) {
            world.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS,
                    1.5f,
                    0.8f
            );

            world.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.PLAYER_ATTACK_STRONG,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.5f
            );
        } else {
            world.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
        }

        // FEEDBACK
        if (isBackstab) {
            serverPlayer.sendOverlayMessage(
                    Component.literal("🗡 ASSASSINATE! Critical strike on " + target.getName().getString() + "!")
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        } else {
            serverPlayer.sendOverlayMessage(
                    Component.literal("⚔ Strike! Hit " + target.getName().getString() + " from the front.")
                            .withStyle(ChatFormatting.DARK_RED));
        }

        return true;
    }

    private static LivingEntity findMeleeTarget(Player player) {
        AABB searchBox = AABB.ofSize(
                player.getEyePosition(),
                MELEE_RANGE * 2,
                MELEE_RANGE * 2,
                MELEE_RANGE * 2
        );

        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        LivingEntity closest = null;
        double closestDistance = MELEE_RANGE;
        Vec3 lookVec = player.getViewVector(1.0f);

        for (LivingEntity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                Vec3 toEntity = entity.position().subtract(player.position()).normalize();
                double dot = lookVec.dot(toEntity);
                if (dot > 0.7) {
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }

    private static boolean isAttackingFromBehind(Player player, LivingEntity target) {
        Vec3 targetLookVec = target.getViewVector(1.0f);
        Vec3 toPlayer = player.position().subtract(target.position()).normalize();
        double dot = targetLookVec.dot(toPlayer);
        return dot > 0;
    }
}