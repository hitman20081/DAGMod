package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
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
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/**
 * TIME WARP - Mage Ability
 *
 * Cooldown: 45 seconds
 *
 * Effects:
 * - Slows all enemies in 10 block radius
 * - Slowness IV (80% slower) for 8 seconds (16s when Overcharged)
 * - Weakness II for the same duration
 * - Mining Fatigue III for the same duration
 * - Does not affect allies or yourself
 *
 * Visual: Cyan/light blue particles + eerie sound
 */
public class TimeWarpAbility {

    private static final double RADIUS = 10.0;
    private static final int DURATION_TICKS = 8 * 20; // 8 seconds

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();
        UUID uuid = serverPlayer.getUUID();

        boolean hasEcho = SpellModifierHandler.consumeSpellEcho(uuid);
        float basePower = SpellModifierHandler.consumeOvercharge(uuid);
        float power = MageEnchantmentBonus.applyAmplification(serverPlayer, basePower);

        boolean result = activateInternal(serverPlayer, world, true, power);
        if (result && hasEcho) {
            world.getServer().execute(() -> activateInternal(serverPlayer, world, false, power));
        }
        return result;
    }

    private static boolean activateInternal(ServerPlayer player, ServerLevel world,
                                            boolean applyModifiers, float powerMultiplier) {
        int durationTicks = Math.round(DURATION_TICKS * powerMultiplier);

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.TIME_WARP);
        }

        AABB searchBox = AABB.ofSize(
                player.position(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        int affectedCount = 0;

        for (LivingEntity entity : nearbyEntities) {
            double distance = player.distanceToSqr(entity);
            if (distance <= RADIUS * RADIUS) {

                entity.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS,
                        durationTicks,
                        3, // Slowness IV
                        false,
                        true,
                        true
                ));

                entity.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        durationTicks,
                        1, // Weakness II
                        false,
                        true,
                        true
                ));

                entity.addEffect(new MobEffectInstance(
                        MobEffects.MINING_FATIGUE,
                        durationTicks,
                        2, // Mining Fatigue III
                        false,
                        true,
                        true
                ));

                world.sendParticles(
                        ParticleTypes.SOUL,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() / 2,
                        entity.getZ(),
                        30,
                        0.3, 0.5, 0.3,
                        0.02
                );

                affectedCount++;
            }
        }

        for (int i = 0; i < 60; i++) {
            double angle = i * Math.PI * 2 / 60;
            double x = player.getX() + Math.cos(angle) * RADIUS;
            double z = player.getZ() + Math.sin(angle) * RADIUS;
            double y = player.getY() + 1.0;

            world.sendParticles(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        world.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                100,
                RADIUS * 0.5, 1.0, RADIUS * 0.5,
                0.5
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.PORTAL_TRIGGER,
                SoundSource.PLAYERS,
                1.0f,
                0.5f
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BEACON_DEACTIVATE,
                SoundSource.PLAYERS,
                0.8f,
                0.7f
        );

        if (affectedCount > 0) {
            player.sendOverlayMessage(
                    Component.literal("⏰ Time Warp! Slowed " + affectedCount + " enemies!")
                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        } else {
            player.sendOverlayMessage(
                    Component.literal("⏰ Time Warp! No enemies nearby.")
                            .withStyle(ChatFormatting.YELLOW));
        }

        return true;
    }
}
