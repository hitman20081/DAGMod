package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.server.level.ServerPlayer;

public class MageEnchantmentHandler {

    /**
     * Reduce enchantment costs for Mages by 50%
     */
    public static int modifyEnchantmentCost(ServerPlayer player, int originalCost) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        if (!"Mage".equals(playerClass)) {
            return originalCost;
        }

        // Mages pay 50% less for enchantments (minimum 1 level)
        return Math.max(1, originalCost / 2);
    }

    /**
     * Check if enchantment effectiveness should be boosted
     */
    public static boolean shouldBoostEnchantment(ServerPlayer player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        return "Mage".equals(playerClass);
    }
}