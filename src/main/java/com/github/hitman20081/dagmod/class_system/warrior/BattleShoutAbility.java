package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.resources.Identifier;

/**
 * BATTLE SHOUT - Warrior Ability
 *
 * Cooldown: 45 seconds
 * Duration: 12 seconds
 *
 * Effects:
 * - Heal 6 hearts (12 HP) immediately
 * - +20% attack damage for 12 seconds
 * - +10% movement speed for 12 seconds
 * - Removes negative effects (Poison, Weakness, Slowness)
 *
 * Visual: Red particles + roar sound
 */
public class BattleShoutAbility {

    private static final int DURATION_TICKS = 12 * 20; // 12 seconds
    private static final float HEAL_AMOUNT = 12.0f;    // 6 hearts

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.BATTLE_SHOUT);

        // INSTANT HEAL
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float newHealth = Math.min(currentHealth + HEAL_AMOUNT, maxHealth);
        player.setHealth(newHealth);

        // REMOVE NEGATIVE EFFECTS
        player.removeEffect(MobEffects.POISON);
        player.removeEffect(MobEffects.WEAKNESS);
        player.removeEffect(MobEffects.SLOWNESS);
        player.removeEffect(MobEffects.MINING_FATIGUE);
        player.removeEffect(MobEffects.WITHER);

        // BUFF: +20% Attack Damage
        player.addEffect(new MobEffectInstance(
                MobEffects.STRENGTH,
                DURATION_TICKS,
                0, // Strength I = +3 damage (roughly +20%)
                false,
                true
        ));

        // BUFF: +10% Movement Speed
        player.addEffect(new MobEffectInstance(
                MobEffects.SPEED,
                DURATION_TICKS,
                0, // Speed I = +20% movement
                false,
                true
        ));

        // VISUAL: Red + Orange particles in circle
        world.sendParticles(
                ParticleTypes.FLAME,
                player.getX(), player.getY() + 1.0, player.getZ(),
                30, // count
                0.5, 0.5, 0.5, // spread
                0.05 // speed
        );

        world.sendParticles(
                ParticleTypes.ANGRY_VILLAGER,
                player.getX(), player.getY() + 1.5, player.getZ(),
                10, // count
                0.3, 0.3, 0.3, // spread
                0.0 // speed
        );

        // SOUND: Roar
        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.RAVAGER_ROAR,
                SoundSource.PLAYERS,
                1.0f,
                1.2f
        );

        // FEEDBACK
        serverPlayer.sendOverlayMessage(
                Component.literal("💪 Battle Shout! Healed and empowered!")
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));

        return true;
    }
}