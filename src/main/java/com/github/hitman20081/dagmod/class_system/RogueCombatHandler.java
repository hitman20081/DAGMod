package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbilityManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

public class RogueCombatHandler {

    private static final float BACKSTAB_MULTIPLIER = 1.5f;

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide() || !(entity instanceof LivingEntity target)) {
                return InteractionResult.PASS;
            }

            if (!ClassSelectionAltarBlock.getPlayerClass(player.getUUID()).equals("rogue")) {
                return InteractionResult.PASS;
            }

            handleRogueAttack((ServerPlayer) player, target);

            return InteractionResult.PASS;
        });
    }

    private static void handleRogueAttack(ServerPlayer player, LivingEntity target) {
        boolean isBackstab = isBackstabAngle(player, target);
        boolean hasPoisonDagger = RogueAbilityManager.hasPoisonDaggerActive(player.getUUID());

        if (isBackstab) {
            applyBackstabVisuals(player, target);
        }

        if (hasPoisonDagger) {
            applyPoisonDaggerEffects(player, target);
            RogueAbilityManager.consumePoisonDagger(player.getUUID());
        }

        if (player.hasEffect(MobEffects.INVISIBILITY)) {
            player.removeEffect(MobEffects.INVISIBILITY);
            player.sendOverlayMessage(Component.literal("Stealth broken!")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static boolean isBackstabAngle(Player player, LivingEntity target) {
        Vec3 playerPos = player.position();
        Vec3 targetPos = target.position();
        Vec3 targetLook = target.getViewVector(1.0f);
        Vec3 toPlayer = playerPos.subtract(targetPos).normalize();
        double dotProduct = targetLook.dot(toPlayer);
        return dotProduct > 0.5;
    }

    private static void applyBackstabVisuals(ServerPlayer player, LivingEntity target) {
        ServerLevel world = (ServerLevel) player.level();
        Vec3 targetPos = target.position();

        for (int i = 0; i < 15; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * target.getBbWidth();
            double offsetY = world.getRandom().nextDouble() * target.getBbHeight();
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * target.getBbWidth();

            world.sendParticles(ParticleTypes.CRIT,
                    targetPos.x + offsetX,
                    targetPos.y + offsetY,
                    targetPos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.1);
        }

        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 0.8f);

        player.sendOverlayMessage(Component.literal("BACKSTAB!")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
    }

    private static void applyPoisonDaggerEffects(ServerPlayer player, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 2));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
        target.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));

        ServerLevel world = (ServerLevel) player.level();
        Vec3 targetPos = target.position();

        for (int i = 0; i < 30; i++) {
            double offsetX = (world.getRandom().nextDouble() - 0.5) * target.getBbWidth() * 2;
            double offsetY = world.getRandom().nextDouble() * target.getBbHeight();
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * target.getBbWidth() * 2;

            // Changed to CAMPFIRE_COSY_SMOKE to avoid particle type error
            world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    targetPos.x + offsetX,
                    targetPos.y + offsetY,
                    targetPos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.05);
        }

        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.0f, 0.6f);

        player.sendOverlayMessage(Component.literal("Poison applied!")
                .withStyle(ChatFormatting.DARK_GREEN));
    }

    // Method called from mixin for damage modification
    public static float handleRogueDamage(ServerPlayer attacker, LivingEntity target, float amount) {
        // Base backstab check
        boolean isBackstab = isBackstabAngle(attacker, target);

        if (!isBackstab) {
            return amount; // No backstab, no damage modification
        }

        // Start with base backstab multiplier
        float backstabMultiplier = BACKSTAB_MULTIPLIER; // 1.5x

        // Add Orc Rogue synergy bonus (from race system)
        String race = com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock.getPlayerRace(attacker.getUUID());
        if ("Orc".equals(race)) {
            backstabMultiplier += 0.20f; // Orc Rogue gets +20% backstab (total 1.7x)
        }

        // Add weapon synergy backstab bonus (Shadow Blade + Shadow Armor)
        float weaponSynergyBonus = com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus
                .getWeaponSynergyBackstabBonus(attacker);
        backstabMultiplier += weaponSynergyBonus;

        // Apply the total backstab multiplier
        return amount * backstabMultiplier;
    }

    // Method called from mixin for fall damage reduction
    public static float modifyFallDamage(ServerPlayer player, float amount) {
        return amount * 0.5f; // 50% reduction
    }
}