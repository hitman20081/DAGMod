package com.github.hitman20081.dagmod.class_system.warrior;

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

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.IRON_SKIN);

        // ABSORPTION HEARTS (shield)
        player.setAbsorptionAmount(player.getAbsorptionAmount() + ABSORPTION_AMOUNT);

        // RESISTANCE II (60% damage reduction)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                DURATION_TICKS,
                1, // Resistance II
                false,
                true,
                true
        ));

        // FIRE RESISTANCE (survive lava/fire)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        // SLOWNESS I (trade-off for defense)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                DURATION_TICKS,
                0, // Slowness I
                false,
                true,
                true
        ));

        // VISUAL: Iron/Gray particles forming armor
        world.spawnParticles(
                ParticleTypes.ITEM_SNOWBALL, // White/gray particles
                player.getX(), player.getY() + 1.0, player.getZ(),
                50,
                0.5, 1.0, 0.5,
                0.1
        );

        world.spawnParticles(
                ParticleTypes.SMOKE,
                player.getX(), player.getY() + 1.0, player.getZ(),
                30,
                0.5, 1.0, 0.5,
                0.05
        );

        // Iron particles rising up
        for (int i = 0; i < 10; i++) {
            world.spawnParticles(
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
                player.getBlockPos(),
                SoundEvents.BLOCK_ANVIL_LAND,
                SoundCategory.PLAYERS,
                0.7f,
                1.0f
        );

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_ARMOR_EQUIP_IRON.value(),
                SoundCategory.PLAYERS,
                1.0f,
                0.8f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("🛡 Iron Skin activated! Tank mode engaged!")
                        .formatted(Formatting.GRAY, Formatting.BOLD),
                true
        );

        serverPlayer.sendMessage(
                Text.literal("60% damage reduction for 15 seconds")
                        .formatted(Formatting.DARK_GRAY),
                false
        );

        return true;
    }
}