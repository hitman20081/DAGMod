package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WarriorDamageHandler {

    /**
     * Apply Warrior's damage reduction (15% less physical damage taken)
     */
    public static float modifyDamageTaken(ServerPlayerEntity player, DamageSource source, float amount) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Warrior".equals(playerClass)) {
            return amount;
        }

        // Warriors take 15% less damage from physical sources
        if (isPhysicalDamage(source)) {
            return amount * 0.85f; // Reduce by 15%
        }

        return amount;
    }

    private static boolean isPhysicalDamage(DamageSource source) {
        // Physical damage types that Warriors resist
        String damageType = source.getName();

        return damageType.contains("mob") ||
                damageType.contains("player") ||
                damageType.contains("arrow") ||
                damageType.contains("explosion") ||
                damageType.equals("cactus") ||
                damageType.equals("fall") ||
                damageType.equals("flyIntoWall") ||
                damageType.equals("cramming");
    }
}