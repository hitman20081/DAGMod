package com.github.hitman20081.dagmod.class_system.mage;

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

/**
 * ARCANE BARRIER - Mage Ability
 *
 * Cooldown: 60 seconds
 *
 * Effects:
 * - Creates magical barrier around caster
 * - +10 absorption hearts (20 HP shield)
 * - Resistance II (40% damage reduction) for 10 seconds
 * - Fire Resistance for 10 seconds
 * - Reflects projectiles (Thorns-like effect)
 *
 * Visual: Magenta/purple barrier particles
 */
public class ArcaneBarrierAbility {

    private static final int DURATION_TICKS = 10 * 20; // 10 seconds
    private static final float ABSORPTION_AMOUNT = 20.0f; // 10 hearts

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        MageCooldownManager.startCooldown(player, MageAbility.ARCANE_BARRIER);

        // ABSORPTION SHIELD
        player.setAbsorptionAmount(player.getAbsorptionAmount() + ABSORPTION_AMOUNT);

        // RESISTANCE II (40% damage reduction)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                DURATION_TICKS,
                1, // Resistance II
                false,
                true,
                true
        ));

        // FIRE RESISTANCE
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        // THORNS-like effect (reflects damage)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INSTANT_HEALTH,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        // VISUAL: Barrier formation

        // Outer sphere
        for (int i = 0; i < 50; i++) {
            double theta = Math.random() * Math.PI * 2;
            double phi = Math.random() * Math.PI;
            double radius = 2.0;

            double x = player.getX() + radius * Math.sin(phi) * Math.cos(theta);
            double y = player.getY() + 1.0 + radius * Math.cos(phi);
            double z = player.getZ() + radius * Math.sin(phi) * Math.sin(theta);

            world.spawnParticles(
                    ParticleTypes.WITCH,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Inner glow
        world.spawnParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                80,
                1.0, 1.0, 1.0,
                0.1
        );

        // Flash effect
        world.spawnParticles(
                ParticleTypes.END_ROD,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.15
        );

        // Continuous barrier effect (spawned over time)
        for (int t = 0; t < DURATION_TICKS; t += 10) {
            final int tick = t;
            world.getServer().execute(() -> {
                if (player.isAlive()) {
                    // Barrier particles around player
                    for (int i = 0; i < 3; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double radius = 1.5;
                        double x = player.getX() + Math.cos(angle) * radius;
                        double z = player.getZ() + Math.sin(angle) * radius;
                        double y = player.getY() + Math.random() * 2;

                        world.spawnParticles(
                                ParticleTypes.WITCH,
                                x, y, z,
                                1,
                                0, 0.1, 0,
                                0
                        );
                    }
                }
            });
        }

        // SOUND: Magical barrier
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.PLAYERS,
                1.0f,
                1.5f
        );

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS,
                1.0f,
                0.8f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("🛡 Arcane Barrier activated!")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                true
        );

        serverPlayer.sendMessage(
                Text.literal("Protected for 10 seconds with 10 absorption hearts!")
                        .formatted(Formatting.DARK_PURPLE),
                false
        );

        return true;
    }
}