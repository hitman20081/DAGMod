package com.github.hitman20081.dagmod.class_system.rogue;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

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

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        RogueCooldownManager.startCooldown(player, RogueAbility.POISON_STRIKE);

        // Find target (raycast + nearby entities)
        LivingEntity target = findTarget(world, player);

        if (target == null) {
            player.sendMessage(
                    Text.literal("No target in range!")
                            .formatted(Formatting.YELLOW),
                    true
            );
            return true; // Still use cooldown
        }

        // Apply poison effects
        target.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON,
                POISON_DURATION,
                3, // Poison IV (very deadly)
                false,
                true,
                true
        ));

        target.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WEAKNESS,
                POISON_DURATION,
                1, // Weakness II
                false,
                true,
                true
        ));

        target.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                POISON_DURATION,
                0, // Slowness I
                false,
                true,
                true
        ));

        // Deal initial damage
        target.damage(world, world.getDamageSources().playerAttack(serverPlayer), 4.0f);

        // VISUAL: Poison particles on target
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.0;
            double offsetY = world.getRandom().nextDouble() * 2.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.0;

            world.spawnParticles(
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
        world.spawnParticles(
                ParticleTypes.SNEEZE,
                target.getX(),
                target.getY() + 1.0,
                target.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.1
        );

        // Weapon particles (from player to target)
        Vec3d start = player.getEyePos();
        Vec3d end = target.getEntityPos().add(0, target.getHeight() / 2, 0);
        Vec3d direction = end.subtract(start).normalize();

        for (int i = 0; i < 10; i++) {
            Vec3d particlePos = start.add(direction.multiply(i * 0.3));
            world.spawnParticles(
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
                    world.spawnParticles(
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
                SoundEvents.ITEM_BOTTLE_EMPTY,
                SoundCategory.PLAYERS,
                1.0f,
                0.7f
        );

        world.playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.ENTITY_SPIDER_HURT,
                SoundCategory.PLAYERS,
                0.8f,
                1.5f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("☠ Poison Strike! " + target.getName().getString() + " is poisoned!")
                        .formatted(Formatting.GREEN, Formatting.BOLD),
                true
        );

        return true;
    }

    private static LivingEntity findTarget(ServerWorld world, PlayerEntity player) {
        // Raycast for entity
        Vec3d start = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d end = start.add(lookVec.multiply(MELEE_RANGE));

        HitResult hitResult = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        // Check for entities along raycast
        Box searchBox = new Box(start, end).expand(1.0);
        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        // Find closest entity in crosshair
        LivingEntity closest = null;
        double closestDistance = MELEE_RANGE;

        for (LivingEntity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                // Check if entity is in line of sight
                Vec3d toEntity = entity.getEntityPos().subtract(start).normalize();
                double dot = lookVec.dotProduct(toEntity);
                if (dot > 0.9) { // Must be looking at target
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }
}