package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbilityManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class RogueCombatHandler {

    private static final float BACKSTAB_MULTIPLIER = 1.5f;

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient || !(entity instanceof LivingEntity target)) {
                return ActionResult.PASS;
            }

            // FIX: use getUuid()
            if (!ClassSelectionAltarBlock.getPlayerClass(player.getUuid()).equals("rogue")) {
                return ActionResult.PASS;
            }

            handleRogueAttack((ServerPlayerEntity) player, target);

            return ActionResult.PASS;
        });
    }

    private static void handleRogueAttack(ServerPlayerEntity player, LivingEntity target) {
        boolean isBackstab = isBackstabAngle(player, target);
        boolean hasPoisonDagger = RogueAbilityManager.hasPoisonDaggerActive(player.getUuid());

        if (isBackstab) {
            applyBackstabVisuals(player, target);
        }

        if (hasPoisonDagger) {
            applyPoisonDaggerEffects(player, target);
            RogueAbilityManager.consumePoisonDagger(player.getUuid());
        }

        if (player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            player.removeStatusEffect(StatusEffects.INVISIBILITY);
            player.sendMessage(Text.literal("Stealth broken!")
                    .formatted(Formatting.GRAY), true);
        }
    }

    private static boolean isBackstabAngle(PlayerEntity player, LivingEntity target) {
        Vec3d playerPos = player.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d targetLook = target.getRotationVec(1.0f);
        Vec3d toPlayer = playerPos.subtract(targetPos).normalize();
        double dotProduct = targetLook.dotProduct(toPlayer);
        return dotProduct > 0.5;
    }

    private static void applyBackstabVisuals(ServerPlayerEntity player, LivingEntity target) {
        ServerWorld world = (ServerWorld) player.getWorld();
        Vec3d targetPos = target.getPos();

        for (int i = 0; i < 15; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * target.getWidth();
            double offsetY = world.random.nextDouble() * target.getHeight();
            double offsetZ = (world.random.nextDouble() - 0.5) * target.getWidth();

            world.spawnParticles(ParticleTypes.CRIT,
                    targetPos.x + offsetX,
                    targetPos.y + offsetY,
                    targetPos.z + offsetZ,
                    1, 0, 0, 0, 0.1);
        }

        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 0.8f);

        player.sendMessage(Text.literal("BACKSTAB!")
                .formatted(Formatting.RED, Formatting.BOLD), true);
    }

    private static void applyPoisonDaggerEffects(ServerPlayerEntity player, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 1));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));

        ServerWorld world = (ServerWorld) player.getWorld();
        Vec3d targetPos = target.getPos();

        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * target.getWidth() * 2;
            double offsetY = world.random.nextDouble() * target.getHeight();
            double offsetZ = (world.random.nextDouble() - 0.5) * target.getWidth() * 2;

            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                    targetPos.x + offsetX,
                    targetPos.y + offsetY,
                    targetPos.z + offsetZ,
                    1, 0, 0, 0, 0.05);
        }

        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1.0f, 0.6f);

        player.sendMessage(Text.literal("Poison applied!")
                .formatted(Formatting.DARK_GREEN), true);
    }

    // Method called from mixin for damage modification
    // Method called from mixin for damage modification
    public static float handleRogueDamage(ServerPlayerEntity attacker, LivingEntity target, float amount) {
        // Base backstab check
        boolean isBackstab = isBackstabAngle(attacker, target);

        if (!isBackstab) {
            return amount; // No backstab, no damage modification
        }

        // Start with base backstab multiplier
        float backstabMultiplier = BACKSTAB_MULTIPLIER; // 1.5x

        // Add Orc Rogue synergy bonus (from race system)
        String race = com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock.getPlayerRace(attacker.getUuid());
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
    public static float modifyFallDamage(ServerPlayerEntity player, float amount) {
        return amount * 0.5f; // 50% reduction
    }
}