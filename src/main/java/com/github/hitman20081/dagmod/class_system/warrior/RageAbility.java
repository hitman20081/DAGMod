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
 * Rage ability - Temporary combat enhancement
 * Duration: 10 seconds
 * Effects: +50% attack damage, +30% movement speed, +2 hearts absorption
 */
public class RageAbility {
    private static final int DURATION_TICKS = 10 * 20; // 10 seconds
    private static final int ABSORPTION_HEARTS = 2;

    // Attribute modifier IDs
    private static final Identifier RAGE_DAMAGE_ID = Identifier.fromNamespaceAndPath("dagmod", "rage_damage");
    private static final Identifier RAGE_SPEED_ID = Identifier.fromNamespaceAndPath("dagmod", "rage_speed");

    /**
     * Activate Rage ability
     */
    public static boolean activate(Player player) {
        if (!(player.level() instanceof ServerLevel serverWorld)) {
            return false;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // Apply absorption effect (2 hearts)
        MobEffectInstance absorption = new MobEffectInstance(
                MobEffects.ABSORPTION,
                DURATION_TICKS,
                ABSORPTION_HEARTS - 1, // Level 1 = 2 hearts
                false,
                true,
                true
        );
        serverPlayer.addEffect(absorption);

        // Apply speed effect (30% = Speed I with custom amplifier)
        MobEffectInstance speed = new MobEffectInstance(
                MobEffects.SPEED,
                DURATION_TICKS,
                1, // Speed II for approximately 30% boost
                false,
                true,
                true
        );
        serverPlayer.addEffect(speed);

        // Apply strength effect (50% damage = Strength II)
        MobEffectInstance strength = new MobEffectInstance(
                MobEffects.STRENGTH,
                DURATION_TICKS,
                1, // Strength II
                false,
                true,
                true
        );
        serverPlayer.addEffect(strength);

        // Visual effects
        spawnRageParticles(serverWorld, serverPlayer);

        // Sound effect
        serverWorld.playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.RAVAGER_ROAR,
                SoundSource.PLAYERS,
                1.0F,
                0.8F
        );

        // Screen title
        serverPlayer.sendSystemMessage(
                Component.literal("RAGE MODE ACTIVATED!")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.RAGE);

        return true;
    }

    /**
     * Spawn particle effects around the player
     */
    private static void spawnRageParticles(ServerLevel world, ServerPlayer player) {
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();

        // Spawn a burst of red particles
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 2.0;
            double offsetY = world.getRandom().nextDouble() * 2.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 2.0;

            world.sendParticles(
                    ParticleTypes.FLAME,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0.1, 0,
                    0.02
            );
        }

        // Spawn angry particles
        for (int i = 0; i < 15; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
            double offsetY = world.getRandom().nextDouble() * 2.0;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

            world.sendParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0, 0,
                    0.0
            );
        }
    }

    /**
     * Create continuous particle effects during rage (call this every tick)
     */
    public static void tickRageParticles(ServerLevel world, ServerPlayer player) {
        if (!player.hasEffect(MobEffects.STRENGTH)) {
            return; // Rage not active
        }

        double x = player.getX();
        double y = player.getY() + 0.5;
        double z = player.getZ();

        // Continuous flame aura
        if (world.getGameTime() % 2 == 0) { // Every 2 ticks
            double angle = world.getRandom().nextDouble() * Math.PI * 2;
            double radius = 0.8;

            world.sendParticles(
                    ParticleTypes.FLAME,
                    x + Math.cos(angle) * radius,
                    y,
                    z + Math.sin(angle) * radius,
                    1,
                    0, 0.1, 0,
                    0.01
            );
        }
    }
}