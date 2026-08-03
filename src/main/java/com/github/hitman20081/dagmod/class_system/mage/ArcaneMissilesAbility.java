package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.event.SpellModifierHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
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

    public static boolean activate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel world = serverPlayer.level();
        UUID uuid = serverPlayer.getUUID();

        boolean hasEcho = SpellModifierHandler.consumeSpellEcho(uuid);
        float basePower = SpellModifierHandler.consumeOvercharge(uuid);
        float power = MageEnchantmentBonus.applyAmplification(serverPlayer, basePower);

        boolean result = activateInternal(serverPlayer, world, true, power);
        if (result && hasEcho) {
            world.getServer().execute(() -> activateInternal(serverPlayer, world, false, power));
        }
        return result;
    }

    private static boolean activateInternal(ServerPlayer player, ServerLevel world,
                                            boolean applyModifiers, float powerMultiplier) {
        int missileCount = Math.round(MISSILE_COUNT * powerMultiplier);

        if (applyModifiers) {
            MageCooldownManager.startCooldown(player, MageAbility.ARCANE_MISSILES);
        }

        AABB searchBox = AABB.ofSize(
                player.position(),
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2,
                SEARCH_RADIUS * 2
        );

        List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        if (nearbyEntities.isEmpty()) {
            player.sendOverlayMessage(
                    Component.literal("No enemies in range!")
                            .withStyle(ChatFormatting.YELLOW));
        }

        for (int i = 0; i < missileCount; i++) {
            final int missileIndex = i;

            world.getServer().execute(() -> {
                List<LivingEntity> currentTargets = world.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
                );

                LivingEntity target = currentTargets.isEmpty() ? null :
                        currentTargets.get(world.getRandom().nextInt(currentTargets.size()));

                fireMissile(world, player, target, missileIndex);
            });
        }

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                1.0f,
                1.5f
        );

        player.sendOverlayMessage(
                Component.literal("✦ Arcane Missiles launched! ✦")
                        .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        return true;
    }

    private static void fireMissile(ServerLevel world, Player player, LivingEntity target, int index) {
        ShulkerBullet missile = new ShulkerBullet(world, player, target, null);

        double angle = (index - 2) * 0.3;
        double offsetX = Math.sin(angle) * 0.5;
        double offsetZ = Math.cos(angle) * 0.5;

        missile.setPos(
                player.getX() + offsetX,
                player.getEyeY() - 0.1,
                player.getZ() + offsetZ
        );

        world.addFreshEntity(missile);

        world.sendParticles(
                ParticleTypes.ENCHANT,
                missile.getX(),
                missile.getY(),
                missile.getZ(),
                10,
                0.2, 0.2, 0.2,
                0.1
        );

        world.sendParticles(
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
                missile.blockPosition(),
                SoundEvents.SHULKER_SHOOT,
                SoundSource.PLAYERS,
                0.5f,
                1.5f
        );
    }
}
