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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

import java.util.List;

/**
 * POISON STRIKE - Rogue Ability
 *
 * Cooldown: 20 seconds
 *
 * Effects:
 * - Coat weapon with deadly poison
 * - Next attack applies Poison IV for 8 seconds
 * - Also applies Weakness and Slowness
 * - Total ~16 damage over time (8 hearts)
 * - Stacks with normal attack damage
 *
 * Visual: Green poison particles
 */
public class PoisonStrikeAbility {

    private static final int POISON_DURATION = 8 * 20; // 8 seconds
    private static final double MELEE_RANGE = 4.0;

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.POISON_STRIKE);

        // Find target (raycast + nearby entities)
        LivingEntity target = findTarget(world, player);

        if (target == null) {
            player.sendOverlayMessage(
                    Component.literal("No target in range!")
                            .withStyle(ChatFormatting.YELLOW));
            return true; // Still use cooldown
        }

        // Apply poison effects
        target.addEffect(new MobEffectInstance(
                MobEffects.POISON,
                POISON_DURATION,
                3, // Poison IV (very deadly)
                false,
                true,
                true
        ));

        target.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                POISON_DURATION,
                1, // Weakness II
                false,
                true,
                true
        ));

        target.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                POISON_DURATION,
                0, // Slowness I
                false,
                true,
                true
        ));

        // Deal initial damage
        target.hurt(world.damageSources().playerAttack(serverPlayer), 4.0f);

        // VISUAL: Poison particles on target
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.0;
            double offsetY = world.getRandom().nextDouble() * 2.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.0;

            world.sendParticles(
                    ParticleTypes.ITEM_SLIME,
                    target.getX() + offsetX,
                    target.getY() + offsetY,
                    target.getZ() + offsetZ,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Green poison cloud
        world.sendParticles(
                ParticleTypes.SNEEZE,
                target.getX(),
                target.getY() + 1.0,
                target.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        // Weapon particles (from player to target)
        Vec3 start = player.getEyePosition();
        Vec3 end = target.position().add(0, target.getBbHeight() / 2, 0);
        Vec3 direction = end.subtract(start).normalize();

        for (int i = 0; i < 10; i++) {
            Vec3 particlePos = start.add(direction.scale(i * 0.3));
            world.sendParticles(
                    ParticleTypes.SNEEZE,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Lingering poison effect
        for (int t = 0; t < POISON_DURATION; t += 10) {
            final int tick = t;
            world.getServer().execute(() -> {
                if (target.isAlive()) {
                    world.sendParticles(
                            ParticleTypes.ITEM_SLIME,
                            target.getX(),
                            target.getY() + 1.0,
                            target.getZ(),
                            3,
                            0.3, 0.5, 0.3,
                            0.05
                    );
                }
            });
        }

        // SOUND: Poison splash
        world.playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.BOTTLE_EMPTY,
                SoundSource.PLAYERS,
                1.0f,
                0.7f
        );

        world.playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.SPIDER_HURT,
                SoundSource.PLAYERS,
                0.8f,
                1.5f
        );

        // FEEDBACK
        serverPlayer.sendOverlayMessage(
                Component.literal("☠ Poison Strike! " + target.getName().getString() + " is poisoned!")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));

        return true;
    }

    private static LivingEntity findTarget(ServerLevel world, Player player) {
        // Raycast for entity
        Vec3 start = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 end = start.add(lookVec.scale(MELEE_RANGE));

        HitResult hitResult = world.clip(new ClipContext(
                start, end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        // Check for entities along raycast
        AABB searchBox = new AABB(start, end).inflate(1.0);
        List<LivingEntity> entities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        // Find closest entity in crosshair
        LivingEntity closest = null;
        double closestDistance = MELEE_RANGE;

        for (LivingEntity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                // Check if entity is in line of sight
                Vec3 toEntity = entity.position().subtract(start).normalize();
                double dot = lookVec.dot(toEntity);
                if (dot > 0.9) { // Must be looking at target
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }
}