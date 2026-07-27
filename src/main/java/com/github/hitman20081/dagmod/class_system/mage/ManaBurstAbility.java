package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.AABB;

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

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();
        UUID uuid = serverPlayer.getUUID();

        boolean hasEcho = SpellModifierHandler.consumeSpellEcho(uuid);
        float power = SpellModifierHandler.consumeOvercharge(uuid);

        boolean result = activateInternal(serverPlayer, world, true, power);
        if (result && hasEcho) {
            world.getServer().execute(() -> activateInternal(serverPlayer, world, false, power));
        }
        return result;
    }

    private static boolean activateInternal(ServerPlayer player, ServerLevel world,
                                            boolean applyModifiers, float powerMultiplier) {
        float damage = DAMAGE * powerMultiplier;

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.MANA_BURST);
        }

        AABB searchBox = AABB.ofSize(
                player.position(),
                RADIUS * 2,
                RADIUS * 2,
                RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive()
        );

        int hitCount = 0;

        for (LivingEntity entity : nearbyEntities) {
            double distance = player.distanceToSqr(entity);
            if (distance <= RADIUS * RADIUS) {

                entity.hurt(world.damageSources().magic(), damage);

                double dx = entity.getX() - player.getX();
                double dy = entity.getY() - player.getY();
                double dz = entity.getZ() - player.getZ();
                double distance3d = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (distance3d > 0) {
                    dx /= distance3d;
                    dy /= distance3d;
                    dz /= distance3d;

                    entity.setDeltaMovement(
                            dx * KNOCKBACK_STRENGTH,
                            0.5,
                            dz * KNOCKBACK_STRENGTH
                    );
                }

                world.sendParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() / 2,
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

                world.sendParticles(
                        ParticleTypes.WITCH,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        world.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                1,
                0, 0, 0,
                0
        );

        world.sendParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                100,
                RADIUS * 0.5, RADIUS * 0.5, RADIUS * 0.5,
                0.2
        );

        world.sendParticles(
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
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                1.0f,
                1.5f
        );

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.EVOKER_PREPARE_ATTACK,
                SoundSource.PLAYERS,
                1.0f,
                0.8f
        );

        if (hitCount > 0) {
            player.sendOverlayMessage(
                    Component.literal("💥 Mana Burst! Hit " + hitCount + " enemies!")
                            .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
        } else {
            player.sendOverlayMessage(
                    Component.literal("💥 Mana Burst! No enemies nearby.")
                            .withStyle(ChatFormatting.YELLOW));
        }

        return true;
    }
}
