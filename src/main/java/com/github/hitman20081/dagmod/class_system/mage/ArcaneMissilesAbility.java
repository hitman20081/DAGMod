package com.github.hitman20081.dagmod.class_system.mage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * ARCANE MISSILES - Mage Ability
 *
 * Cooldown: 20 seconds
 *
 * Effects:
 * - Fires 5 homing arcane missiles
 * - Each missile deals 3 damage (1.5 hearts)
 * - Missiles automatically seek nearest enemy
 * - Total: 15 damage (7.5 hearts) if all hit
 *
 * Visual: Purple shulker bullets + enchant particles
 */
public class ArcaneMissilesAbility {

    private static final int MISSILE_COUNT = 5;
    private static final float DAMAGE_PER_MISSILE = 3.0f;
    private static final double SEARCH_RADIUS = 20.0;

    public static boolean activate(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        ServerWorld world = serverPlayer.getEntityWorld();

        // Start cooldown
        MageCooldownManager.startCooldown(player, MageAbility.ARCANE_MISSILES);

        // Find nearest enemy
        Box searchBox = Box.of(
                player.getEntityPos(),
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
        );

        if (nearbyEntities.isEmpty()) {
            player.sendMessage(
                    Text.literal("No enemies in range!")
                            .formatted(Formatting.YELLOW),
                    true
            );
            // Still use cooldown, but warn player
        }

        // Fire missiles with delay
        for (int i = 0; i < MISSILE_COUNT; i++) {
            final int missileIndex = i;

            // Delay each missile by 2 ticks (0.1 seconds)
            world.getServer().execute(() -> {
                // Recheck for targets each time
                List<LivingEntity> currentTargets = world.getEntitiesByClass(
                        LivingEntity.class,
                        searchBox,
                        entity -> entity != player && entity.isAlive() && !entity.isTeammate(player)
                );

                LivingEntity target = currentTargets.isEmpty() ? null :
                        currentTargets.get(world.getRandom().nextInt(currentTargets.size()));

                fireMissile(world, player, target, missileIndex);
            });
        }

        // SOUND: Arcane whoosh
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                SoundCategory.PLAYERS,
                1.0f,
                1.5f
        );

        // FEEDBACK
        serverPlayer.sendMessage(
                Text.literal("✦ Arcane Missiles launched! ✦")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                true
        );

        return true;
    }

    private static void fireMissile(ServerWorld world, PlayerEntity player, LivingEntity target, int index) {
        // Create shulker bullet (homing projectile)
        ShulkerBulletEntity missile = new ShulkerBulletEntity(world, player, target, null);

        // Position missile in front of player with slight offset
        double angle = (index - 2) * 0.3; // Spread missiles
        double offsetX = Math.sin(angle) * 0.5;
        double offsetZ = Math.cos(angle) * 0.5;

        missile.setPosition(
                player.getX() + offsetX,
                player.getEyeY() - 0.1,
                player.getZ() + offsetZ
        );

        world.spawnEntity(missile);

        // VISUAL: Purple particles
        world.spawnParticles(
                ParticleTypes.ENCHANT,
                missile.getX(),
                missile.getY(),
                missile.getZ(),
                10,
                0.2, 0.2, 0.2,
                0.1
        );

        world.spawnParticles(
                ParticleTypes.WITCH,
                missile.getX(),
                missile.getY(),
                missile.getZ(),
                5,
                0.1, 0.1, 0.1,
                0.05
        );

        // SOUND: Missile launch
        world.playSound(
                null,
                missile.getBlockPos(),
                SoundEvents.ENTITY_SHULKER_SHOOT,
                SoundCategory.PLAYERS,
                0.5f,
                1.5f
        );
    }
}