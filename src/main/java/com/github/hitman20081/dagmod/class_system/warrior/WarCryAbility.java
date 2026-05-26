package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
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
    public static boolean activate(Player player) {
        if (!(player.level() instanceof ServerLevel serverWorld)) {
            return false;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        Vec3 playerPos = player.position();

        // Find all entities in radius
        AABB searchBox = new AABB(playerPos, playerPos).inflate(RADIUS);
        List<Entity> nearbyEntities = serverWorld.getEntities(player, searchBox);

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

            double distance = entity.position().distanceTo(playerPos);
            if (distance > RADIUS) {
                continue;
            }

            if (entity instanceof Player) {
                // Buff allied players
                applyAllyBuffs(livingEntity);
                alliesBuffed++;
            } else if (entity instanceof Monster) {
                // Debuff hostile mobs
                applyEnemyDebuffs(livingEntity);
                enemiesDebuffed++;
            }
        }

        // Visual and audio effects
        spawnWarCryParticles(serverWorld, playerPos);

        serverWorld.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENDER_DRAGON_GROWL, // Powerful war cry sound
                SoundSource.PLAYERS,
                2.0F,
                1.2F
        );

        // Feedback messages
        serverPlayer.sendOverlayMessage(
                Component.literal("🎺 War Cry rallied " + alliesBuffed + " allies and terrified " + enemiesDebuffed + " enemies!")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Notify nearby allied players
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ServerPlayer ally && entity != player) {
                ally.sendOverlayMessage(
                        Component.literal("⚔ " + player.getName().getString() + "'s War Cry empowers you!")
                                .withStyle(ChatFormatting.GOLD));
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
        MobEffectInstance strength = new MobEffectInstance(
                MobEffects.STRENGTH,
                BUFF_DURATION_TICKS,
                0, // Strength I
                false,
                true);
        entity.addEffect(strength);

        // Resistance I for 15 seconds
        MobEffectInstance resistance = new MobEffectInstance(
                MobEffects.RESISTANCE,
                BUFF_DURATION_TICKS,
                0, // Resistance I
                false,
                true,
                true
        );
        entity.addEffect(resistance);

        // Speed I for 15 seconds
        MobEffectInstance speed = new MobEffectInstance(
                MobEffects.SPEED,
                BUFF_DURATION_TICKS,
                0, // Speed I
                false,
                true,
                true
        );
        entity.addEffect(speed);
    }

    /**
     * Apply debuff effects to enemies (Fear effect)
     */
    private static void applyEnemyDebuffs(LivingEntity entity) {
        // Slowness I for 8 seconds
        MobEffectInstance slowness = new MobEffectInstance(
                MobEffects.SLOWNESS,
                DEBUFF_DURATION_TICKS,
                0, // Slowness I
                false,
                true,
                true
        );
        entity.addEffect(slowness);

        // Weakness I for 8 seconds
        MobEffectInstance weakness = new MobEffectInstance(
                MobEffects.WEAKNESS,
                DEBUFF_DURATION_TICKS,
                0, // Weakness I
                false,
                true,
                true
        );
        entity.addEffect(weakness);
    }

    /**
     * Spawn particles for War Cry effect
     */
    private static void spawnWarCryParticles(ServerLevel world, Vec3 center) {
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
                world.sendParticles(
                        ParticleTypes.END_ROD,
                        x, y, z,
                        1,
                        0, 0.1, 0,
                        0.02
                );

                // Note particles for musical effect
                if (i % 3 == 0) {
                    world.sendParticles(
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
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = world.getRandom().nextDouble() * 3.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2.0;

            world.sendParticles(
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
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.0;
            double offsetY = world.getRandom().nextDouble() * 2.5;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.0;

            world.sendParticles(
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