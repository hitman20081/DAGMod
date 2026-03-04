package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
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
import java.util.UUID;

/**
 * ARCANE MISSILES - Mage Ability
 *
 * Cooldown: 20 seconds
 *
 * Effects:
 * - Fires 5 homing arcane missiles (10 when Overcharged)
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
        UUID uuid = serverPlayer.getUuid();

        boolean hasEcho = SpellModifierHandler.consumeSpellEcho(uuid);
        float power = SpellModifierHandler.consumeOvercharge(uuid);

        boolean result = activateInternal(serverPlayer, world, true, power);
        if (result && hasEcho) {
            world.getServer().execute(() -> activateInternal(serverPlayer, world, false, power));
        }
        return result;
    }

    private static boolean activateInternal(ServerPlayerEntity player, ServerWorld world,
                                            boolean applyModifiers, float powerMultiplier) {
        int missileCount = Math.round(MISSILE_COUNT * powerMultiplier);

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.ARCANE_MISSILES);
        }

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
        }

        for (int i = 0; i < missileCount; i++) {
            final int missileIndex = i;

            world.getServer().execute(() -> {
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

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                SoundCategory.PLAYERS,
                1.0f,
                1.5f
        );

        player.sendMessage(
                Text.literal("✦ Arcane Missiles launched! ✦")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                true
        );

        return true;
    }

    private static void fireMissile(ServerWorld world, PlayerEntity player, LivingEntity target, int index) {
        ShulkerBulletEntity missile = new ShulkerBulletEntity(world, player, target, null);

        double angle = (index - 2) * 0.3;
        double offsetX = Math.sin(angle) * 0.5;
        double offsetZ = Math.cos(angle) * 0.5;

        missile.setPosition(
                player.getX() + offsetX,
                player.getEyeY() - 0.1,
                player.getZ() + offsetZ
        );

        world.spawnEntity(missile);

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
