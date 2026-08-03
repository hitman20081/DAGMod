package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
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

import java.util.UUID;

/**
 * ARCANE BARRIER - Mage Ability
 *
 * Cooldown: 60 seconds
 *
 * Effects:
 * - Creates magical barrier around caster
 * - +10 absorption hearts (20 HP shield), doubled to 40 HP when Overcharged
 * - Resistance II (40% damage reduction) for 10 seconds
 * - Fire Resistance for 10 seconds
 *
 * Visual: Magenta/purple barrier particles
 */
public class ArcaneBarrierAbility {

    private static final int DURATION_TICKS = 10 * 20; // 10 seconds
    private static final float ABSORPTION_AMOUNT = 20.0f; // 10 hearts

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
        float absorptionAmount = ABSORPTION_AMOUNT * powerMultiplier;

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.ARCANE_BARRIER);
        }

        player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionAmount);

        player.addEffect(new MobEffectInstance(
                MobEffects.RESISTANCE,
                DURATION_TICKS,
                1, // Resistance II
                false,
                true,
                true
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.INSTANT_HEALTH,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        // Outer sphere
        for (int i = 0; i < 50; i++) {
            double theta = Math.random() * Math.PI * 2;
            double phi = Math.random() * Math.PI;
            double radius = 2.0;

            double x = player.getX() + radius * Math.sin(phi) * Math.cos(theta);
            double y = player.getY() + 1.0 + radius * Math.cos(phi);
            double z = player.getZ() + radius * Math.sin(phi) * Math.sin(theta);

            world.sendParticles(
                    ParticleTypes.WITCH,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0
            );
        }

        world.sendParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                80,
                1.0, 1.0, 1.0,
                0.1
        );

        world.sendParticles(
                ParticleTypes.END_ROD,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30,
                0.5, 0.5, 0.5,
                0.15
        );

        for (int t = 0; t < DURATION_TICKS; t += 10) {
            world.getServer().execute(() -> {
                if (player.isAlive()) {
                    for (int i = 0; i < 3; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double radius = 1.5;
                        double x = player.getX() + Math.cos(angle) * radius;
                        double z = player.getZ() + Math.sin(angle) * radius;
                        double y = player.getY() + Math.random() * 2;

                        world.sendParticles(
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

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                1.0f,
                1.5f
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                1.0f,
                0.8f
        );

        int heartsGranted = Math.round(absorptionAmount / 2);
        player.sendOverlayMessage(
                Component.literal("🛡 Arcane Barrier activated!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        player.sendSystemMessage(
                Component.literal("Protected for 10 seconds with " + heartsGranted + " absorption hearts!")
                        .withStyle(ChatFormatting.DARK_PURPLE));

        return true;
    }
}
