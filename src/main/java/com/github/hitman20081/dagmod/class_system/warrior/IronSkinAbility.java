package com.github.hitman20081.dagmod.class_system.warrior;

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

/**
 * IRON SKIN - Warrior Ability
 *
 * Cooldown: 120 seconds (2 minutes)
 * Duration: 15 seconds
 *
 * Effects:
 * - +8 absorption hearts (16 HP shield)
 * - Resistance II (60% damage reduction)
 * - Slowness I (can't run away while tanking!)
 * - Fire Resistance
 *
 * Visual: Gray/Iron particles + anvil sound
 *
 * This is a "last stand" defensive ability for tough situations
 */
public class IronSkinAbility {

    private static final int DURATION_TICKS = 15 * 20; // 15 seconds
    private static final float ABSORPTION_AMOUNT = 16.0f; // 8 hearts

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.IRON_SKIN);

        // ABSORPTION HEARTS (shield)
        player.setAbsorptionAmount(player.getAbsorptionAmount() + ABSORPTION_AMOUNT);

        // RESISTANCE II (60% damage reduction)
        player.addEffect(new MobEffectInstance(
                MobEffects.RESISTANCE,
                DURATION_TICKS,
                1, // Resistance II
                false,
                true,
                true
        ));

        // FIRE RESISTANCE (survive lava/fire)
        player.addEffect(new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        // SLOWNESS I (trade-off for defense)
        player.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS,
                DURATION_TICKS,
                0, // Slowness I
                false,
                true,
                true
        ));

        // VISUAL: Iron/Gray particles forming armor
        world.sendParticles(
                ParticleTypes.ITEM_SNOWBALL, // White/gray particles
                player.getX(), player.getY() + 1.0, player.getZ(),
                50,
                0.5, 1.0, 0.5,
                0.1
        );

        world.sendParticles(
                ParticleTypes.SMOKE,
                player.getX(), player.getY() + 1.0, player.getZ(),
                30,
                0.5, 1.0, 0.5,
                0.05
        );

        // Iron particles rising up
        for (int i = 0; i < 10; i++) {
            world.sendParticles(
                    ParticleTypes.CLOUD,
                    player.getX() + (Math.random() - 0.5),
                    player.getY() + (Math.random() * 2),
                    player.getZ() + (Math.random() - 0.5),
                    1,
                    0, 0, 0,
                    0
            );
        }

        // SOUND: Heavy metal impact
        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ANVIL_LAND,
                SoundSource.PLAYERS,
                0.7f,
                1.0f
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_IRON.value(),
                SoundSource.PLAYERS,
                1.0f,
                0.8f
        );

        // FEEDBACK
        serverPlayer.sendOverlayMessage(
                Component.literal("🛡 Iron Skin activated! Tank mode engaged!")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));

        serverPlayer.sendSystemMessage(
                Component.literal("60% damage reduction for 15 seconds")
                        .withStyle(ChatFormatting.DARK_GRAY));

        return true;
    }
}