package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class RogueCombatHandler {

    private static final double BACKSTAB_ANGLE = 90.0; // Degrees - attack from behind

    /**
     * Handle Rogue's critical hit (25% chance) and backstab bonus
     * Returns the modified damage amount
     */
    public static float handleRogueDamage(ServerPlayerEntity attacker, LivingEntity target, float baseDamage) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(attacker.getUuid());

        if (!"Rogue".equals(playerClass)) {
            return baseDamage;
        }

        float finalDamage = baseDamage;
        boolean isCritical = false;
        boolean isBackstab = false;

        // Check for critical hit (25% chance)
        if (attacker.getRandom().nextFloat() < 0.25f) {
            finalDamage *= 2.0f; // Double damage
            isCritical = true;
        }

        // Check for backstab (attacking from behind)
        if (isAttackingFromBehind(attacker, target)) {
            finalDamage *= 1.5f; // +50% damage
            isBackstab = true;
        }

        // Check for Orc Rogue synergy - extra backstab damage
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(attacker.getUuid());
        if ("Orc".equals(playerRace) && isBackstab) {
            finalDamage *= 1.2f; // Additional +20% for Orc Rogues backstabbing
        }

        // Send feedback to player
        if (isCritical || isBackstab) {
            StringBuilder message = new StringBuilder("⚡ ");
            if (isCritical) message.append("CRITICAL HIT! ");
            if (isBackstab) message.append("BACKSTAB! ");
            message.append(String.format("(%.1f damage)", finalDamage));

            attacker.sendMessage(
                    Text.literal(message.toString()).formatted(Formatting.YELLOW),
                    true // Action bar
            );
        }

        return finalDamage;
    }

    /**
     * Check if attacker is attacking target from behind
     */
    private static boolean isAttackingFromBehind(ServerPlayerEntity attacker, LivingEntity target) {
        // Get direction target is facing
        Vec3d targetLook = target.getRotationVector();

        // Get direction from target to attacker
        Vec3d toAttacker = attacker.getPos().subtract(target.getPos()).normalize();

        // Calculate dot product (how aligned the vectors are)
        // If positive and high, attacker is in front
        // If negative, attacker is behind
        double dotProduct = targetLook.dotProduct(toAttacker);

        // Backstab if attacking from behind (dot product < -0.5 means roughly behind)
        return dotProduct < -0.5;
    }

    /**
     * Reduce fall damage for Rogues by 50%
     */
    public static float modifyFallDamage(ServerPlayerEntity player, float fallDamage) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Rogue".equals(playerClass)) {
            return fallDamage;
        }

        // Rogues take 50% less fall damage
        return fallDamage * 0.5f;
    }
}