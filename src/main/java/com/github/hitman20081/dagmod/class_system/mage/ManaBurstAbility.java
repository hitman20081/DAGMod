package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
 * MANA BURST - Mage Ability
 *
 * Cooldown: 30 seconds
 *
 * Effects:
 * - Release explosive burst of mana in 7 block radius
 * - Deals 10 damage (5 hearts) to all enemies (20 when Overcharged)
 * - Knocks back enemies significantly
 * - Pure magic damage (ignores armor)
 *
 * Visual: Blue explosion + enchant particles
 */
public class ManaBurstAbility {

    private static final double RADIUS = 7.0;
    private static final float DAMAGE = 10.0f; // 5 hearts
    private static final double KNOCKBACK_STRENGTH = 1.5;

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
        float damage = DAMAGE * powerMultiplier;

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.MANA_BURST);
        }

        Box searchBox = Box.of(
                player.getEntityPos(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive()
        );

        int hitCount = 0;

        for (LivingEntity entity : nearbyEntities) {
            double distance = player.squaredDistanceTo(entity);
            if (distance <= RADIUS * RADIUS) {

                entity.damage(world, world.getDamageSources().magic(), damage);

                double dx = entity.getX() - player.getX();
                double dy = entity.getY() - player.getY();
                double dz = entity.getZ() - player.getZ();
                double distance3d = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (distance3d > 0) {
                    dx /= distance3d;
                    dy /= distance3d;
                    dz /= distance3d;

                    entity.setVelocity(
                            dx * KNOCKBACK_STRENGTH,
                            0.5,
                            dz * KNOCKBACK_STRENGTH
                    );
                }

                world.spawnParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        entity.getX(),
                        entity.getY() + entity.getHeight() / 2,
                        entity.getZ(),
                        20,
                        0.3, 0.3, 0.3,
                        0.1
                );

                hitCount++;
            }
        }

        // Shockwave rings
        for (int ring = 0; ring < 3; ring++) {
            double ringRadius = RADIUS * (ring + 1) / 3.0;
            for (int i = 0; i < 40; i++) {
                double angle = i * Math.PI * 2 / 40;
                double x = player.getX() + Math.cos(angle) * ringRadius;
                double z = player.getZ() + Math.sin(angle) * ringRadius;
                double y = player.getY() + 0.5;

                world.spawnParticles(
                        ParticleTypes.WITCH,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        world.spawnParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                1,
                0, 0, 0,
                0
        );

        world.spawnParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                100,
                RADIUS * 0.5, RADIUS * 0.5, RADIUS * 0.5,
                0.2
        );

        world.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                50,
                RADIUS * 0.5, RADIUS * 0.5, RADIUS * 0.5,
                0.3
        );

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                1.0f,
                1.5f
        );

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK,
                SoundCategory.PLAYERS,
                1.0f,
                0.8f
        );

        if (hitCount > 0) {
            player.sendMessage(
                    Text.literal("💥 Mana Burst! Hit " + hitCount + " enemies!")
                            .formatted(Formatting.BLUE, Formatting.BOLD),
                    true
            );
        } else {
            player.sendMessage(
                    Text.literal("💥 Mana Burst! No enemies nearby.")
                            .formatted(Formatting.YELLOW),
                    true
            );
        }

        return true;
    }
}
