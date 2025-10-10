package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
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
 * War Cry ability - AOE team buff and enemy debuff
 * Radius: 15 blocks
 * Ally buffs: Strength I, Resistance I, Speed I for 15 seconds
 * Enemy debuffs: Slowness I, Weakness I for 8 seconds
 */
public class WarCryAbility {
    private static final double RADIUS = 15.0;
    private static final int BUFF_DURATION_TICKS = 15 * 20; // 15 seconds
    private static final int DEBUFF_DURATION_TICKS = 8 * 20; // 8 seconds

    /**
     * Activate War Cry ability
     */
    public static boolean activate(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        Vec3d playerPos = player.getPos();

        // Find all entities in radius
        Box searchBox = new Box(playerPos, playerPos).expand(RADIUS);
        List<Entity> nearbyEntities = serverWorld.getOtherEntities(player, searchBox);

        int alliesBuffed = 0;
        int enemiesDebuffed = 0;

        // Buff self first
        applyAllyBuffs(player);
        alliesBuffed++;

        // Process nearby entities
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            double distance = entity.getPos().distanceTo(playerPos);
            if (distance > RADIUS) {
                continue;
            }

            if (entity instanceof PlayerEntity) {
                // Buff allied players
                applyAllyBuffs(livingEntity);
                alliesBuffed++;
            } else if (entity instanceof HostileEntity) {
                // Debuff hostile mobs
                applyEnemyDebuffs(livingEntity);
                enemiesDebuffed++;
            }
        }

        // Visual and audio effects
        spawnWarCryParticles(serverWorld, playerPos);

        serverWorld.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_ENDER_DRAGON_GROWL, // Powerful war cry sound
                SoundCategory.PLAYERS,
                2.0F,
                1.2F
        );

        // Feedback messages
        serverPlayer.sendMessage(
                Text.literal("🎺 War Cry rallied " + alliesBuffed + " allies and terrified " + enemiesDebuffed + " enemies!")
                        .formatted(Formatting.GOLD, Formatting.BOLD),
                false
        );

        // Notify nearby allied players
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ServerPlayerEntity ally && entity != player) {
                ally.sendMessage(
                        Text.literal("⚔ " + player.getName().getString() + "'s War Cry empowers you!")
                                .formatted(Formatting.GOLD),
                        true
                );
            }
        }

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.WAR_CRY);

        return true;
    }

    /**
     * Apply buff effects to allies
     */
    private static void applyAllyBuffs(LivingEntity entity) {
        // Strength I for 15 seconds
        StatusEffectInstance strength = new StatusEffectInstance(
                StatusEffects.STRENGTH,
                BUFF_DURATION_TICKS,
                0, // Strength I
                false,
                true,
                true
        );
        entity.addStatusEffect(strength);

        // Resistance I for 15 seconds
        StatusEffectInstance resistance = new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                BUFF_DURATION_TICKS,
                0, // Resistance I
                false,
                true,
                true
        );
        entity.addStatusEffect(resistance);

        // Speed I for 15 seconds
        StatusEffectInstance speed = new StatusEffectInstance(
                StatusEffects.SPEED,
                BUFF_DURATION_TICKS,
                0, // Speed I
                false,
                true,
                true
        );
        entity.addStatusEffect(speed);
    }

    /**
     * Apply debuff effects to enemies (Fear effect)
     */
    private static void applyEnemyDebuffs(LivingEntity entity) {
        // Slowness I for 8 seconds
        StatusEffectInstance slowness = new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                DEBUFF_DURATION_TICKS,
                0, // Slowness I
                false,
                true,
                true
        );
        entity.addStatusEffect(slowness);

        // Weakness I for 8 seconds
        StatusEffectInstance weakness = new StatusEffectInstance(
                StatusEffects.WEAKNESS,
                DEBUFF_DURATION_TICKS,
                0, // Weakness I
                false,
                true,
                true
        );
        entity.addStatusEffect(weakness);
    }

    /**
     * Spawn particles for War Cry effect
     */
    private static void spawnWarCryParticles(ServerWorld world, Vec3d center) {
        // Golden wave emanating outward
        for (int ring = 0; ring < 5; ring++) {
            double radius = 3.0 + ring * 2.5;
            int particleCount = (int) (radius * 8);

            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI * i) / particleCount;
                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;
                double y = center.y + 0.5;

                // Gold sparkle particles
                world.spawnParticles(
                        ParticleTypes.END_ROD,
                        x, y, z,
                        1,
                        0, 0.1, 0,
                        0.02
                );

                // Note particles for musical effect
                if (i % 3 == 0) {
                    world.spawnParticles(
                            ParticleTypes.NOTE,
                            x, y + 1.0, z,
                            1,
                            0, 0, 0,
                            0.0
                    );
                }
            }
        }

        // Upward burst at center
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetY = world.random.nextDouble() * 3.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;

            world.spawnParticles(
                    ParticleTypes.FIREWORK,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    1,
                    0, 0.2, 0,
                    0.05
            );
        }

        // Totem-like effect particles
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.0;
            double offsetY = world.random.nextDouble() * 2.5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.0;

            world.spawnParticles(
                    ParticleTypes.ENCHANT,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    1,
                    0, 0.1, 0,
                    0.1
            );
        }
    }
}