package com.github.hitman20081.dagmod.class_system.warrior;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.util.Identifier;

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

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.BATTLE_SHOUT);

        // INSTANT HEAL
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float newHealth = Math.min(currentHealth + HEAL_AMOUNT, maxHealth);
        player.setHealth(newHealth);

        // REMOVE NEGATIVE EFFECTS
        player.removeStatusEffect(StatusEffects.POISON);
        player.removeStatusEffect(StatusEffects.WEAKNESS);
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        player.removeStatusEffect(StatusEffects.WITHER);

        // BUFF: +20% Attack Damage
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                DURATION_TICKS,
                0, // Strength I = +3 damage (roughly +20%)
                false,
                true
        ));

        // BUFF: +10% Movement Speed
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                DURATION_TICKS,
                0, // Speed I = +20% movement
                false,
                true
        ));

        // VISUAL: Red + Orange particles in circle
        world.spawnParticles(
                ParticleTypes.FLAME,
                player.getX(), player.getY() + 1.0, player.getZ(),
                30, // count
                0.5, 0.5, 0.5, // spread
                0.05 // speed
        );

        world.spawnParticles(
                ParticleTypes.ANGRY_VILLAGER,
                player.getX(), player.getY() + 1.5, player.getZ(),
                10, // count
                0.3, 0.3, 0.3, // spread
                0.0 // speed
        );

        // SOUND: Roar
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_RAVAGER_ROAR,
                SoundCategory.PLAYERS,
                1.0f,
                1.2f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("💪 Battle Shout! Healed and empowered!")
                        .formatted(Formatting.RED, Formatting.BOLD),
                true
        );

        return true;
    }
}