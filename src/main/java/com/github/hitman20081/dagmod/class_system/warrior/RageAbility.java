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
 * Rage ability - Temporary combat enhancement
 * Duration: 10 seconds
 * Effects: +50% attack damage, +30% movement speed, +2 hearts absorption
 */
public class RageAbility {
    private static final int DURATION_TICKS = 10 * 20; // 10 seconds
    private static final int ABSORPTION_HEARTS = 2;

    // Attribute modifier IDs
    private static final Identifier RAGE_DAMAGE_ID = Identifier.of("dagmod", "rage_damage");
    private static final Identifier RAGE_SPEED_ID = Identifier.of("dagmod", "rage_speed");

    /**
     * Activate Rage ability
     */
    public static boolean activate(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Apply absorption effect (2 hearts)
        StatusEffectInstance absorption = new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                DURATION_TICKS,
                ABSORPTION_HEARTS - 1, // Level 1 = 2 hearts
                false,
                true,
                true
        );
        serverPlayer.addStatusEffect(absorption);

        // Apply speed effect (30% = Speed I with custom amplifier)
        StatusEffectInstance speed = new StatusEffectInstance(
                StatusEffects.SPEED,
                DURATION_TICKS,
                1, // Speed II for approximately 30% boost
                false,
                true,
                true
        );
        serverPlayer.addStatusEffect(speed);

        // Apply strength effect (50% damage = Strength II)
        StatusEffectInstance strength = new StatusEffectInstance(
                StatusEffects.STRENGTH,
                DURATION_TICKS,
                1, // Strength II
                false,
                true,
                true
        );
        serverPlayer.addStatusEffect(strength);

        // Visual effects
        spawnRageParticles(serverWorld, serverPlayer);

        // Sound effect
        serverWorld.playSound(
                null,
                serverPlayer.getBlockPos(),
                SoundEvents.ENTITY_RAVAGER_ROAR,
                SoundCategory.PLAYERS,
                1.0F,
                0.8F
        );

        // Screen title
        serverPlayer.sendMessage(
                Text.literal("RAGE MODE ACTIVATED!")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        // Start cooldown
        CooldownManager.startCooldown(player, WarriorAbility.RAGE);

        return true;
    }

    /**
     * Spawn particle effects around the player
     */
    private static void spawnRageParticles(ServerWorld world, ServerPlayerEntity player) {
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();

        // Spawn a burst of red particles
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetY = world.random.nextDouble() * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;

            world.spawnParticles(
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
            double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
            double offsetY = world.random.nextDouble() * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;

            world.spawnParticles(
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
    public static void tickRageParticles(ServerWorld world, ServerPlayerEntity player) {
        if (!player.hasStatusEffect(StatusEffects.STRENGTH)) {
            return; // Rage not active
        }

        double x = player.getX();
        double y = player.getY() + 0.5;
        double z = player.getZ();

        // Continuous flame aura
        if (world.getTime() % 2 == 0) { // Every 2 ticks
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = 0.8;

            world.spawnParticles(
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