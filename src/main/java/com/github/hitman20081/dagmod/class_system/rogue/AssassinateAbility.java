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

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.ASSASSINATE);

        // Find target in melee range
        LivingEntity target = findMeleeTarget(player);

        if (target == null) {
            player.sendMessage(
                    Text.literal("No target in melee range!")
                            .formatted(Formatting.YELLOW),
                    true
            );
            return true;
        }

        // Check if attacking from behind
        boolean isBackstab = isAttackingFromBehind(player, target);

        float damage = isBackstab ? BACKSTAB_DAMAGE : FRONTSTAB_DAMAGE;

        // Deal damage
        target.damage(world, world.getDamageSources().playerAttack(serverPlayer), damage);

        // VISUAL
        if (isBackstab) {
            for (int i = 0; i < 50; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
                double offsetY = world.getRandom().nextDouble() * 2.0;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

                world.spawnParticles(
                        ParticleTypes.DAMAGE_INDICATOR,
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ,
                        1,
                        0, 0, 0,
                        0
                );
            }

            world.spawnParticles(
                    ParticleTypes.CRIMSON_SPORE,
                    target.getX(),
                    target.getY() + target.getHeight() / 2,
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

                world.spawnParticles(
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

        world.spawnParticles(
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
                    SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                    SoundCategory.PLAYERS,
                    1.5f,
                    0.8f
            );

            world.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                    SoundCategory.PLAYERS,
                    1.0f,
                    0.5f
            );
        } else {
            world.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );
        }

        // FEEDBACK
        if (isBackstab) {
            serverPlayer.sendMessage(
                    Text.literal("🗡 ASSASSINATE! Critical strike on " + target.getName().getString() + "!")
                            .formatted(Formatting.RED, Formatting.BOLD),
                    true
            );
        } else {
            serverPlayer.sendMessage(
                    Text.literal("⚔ Strike! Hit " + target.getName().getString() + " from the front.")
                            .formatted(Formatting.DARK_RED),
                    true
            );
        }

        return true;
    }

    private static LivingEntity findMeleeTarget(PlayerEntity player) {
        Box searchBox = Box.of(
                player.getEyePos(),
                MELEE_RANGE * 2,
                MELEE_RANGE * 2,
                MELEE_RANGE * 2
        );

        List<LivingEntity> entities = player.getEntityWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        LivingEntity closest = null;
        double closestDistance = MELEE_RANGE;
        Vec3d lookVec = player.getRotationVec(1.0f);

        for (LivingEntity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                Vec3d toEntity = entity.getEntityPos().subtract(player.getEntityPos()).normalize();
                double dot = lookVec.dotProduct(toEntity);
                if (dot > 0.7) {
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }

    private static boolean isAttackingFromBehind(PlayerEntity player, LivingEntity target) {
        Vec3d targetLookVec = target.getRotationVec(1.0f);
        Vec3d toPlayer = player.getEntityPos().subtract(target.getEntityPos()).normalize();
        double dot = targetLookVec.dotProduct(toPlayer);
        return dot > 0;
    }
}