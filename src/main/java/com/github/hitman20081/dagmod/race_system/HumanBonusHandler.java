package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

public class HumanBonusHandler {

    /**
     * Humans get 25% bonus experience from all sources, plus an additional 5% per level of
     * the Versatile enchantment (worn on a helmet), stacking additively on top of the base bonus.
     */
    public static int modifyExperienceGain(ServerPlayer player, int originalXP) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if ("Human".equals(playerRace)) {
            int versatileLevel = CustomEnchantmentEffects.getEnchantmentLevel(
                    player.getItemBySlot(EquipmentSlot.HEAD), player.level(), "human_versatile");
            float multiplier = 1.25f + 0.05f * versatileLevel;
            return (int) (originalXP * multiplier);
        }

        return originalXP;
    }
}