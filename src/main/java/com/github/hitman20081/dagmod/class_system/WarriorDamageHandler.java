package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;

public class WarriorDamageHandler {

    /**
     * Apply Warrior's damage reduction (15% less physical damage taken)
     */
    public static float modifyDamageTaken(ServerPlayer player, DamageSource source, float amount) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

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
        String damageType = source.type().msgId();

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