package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.server.level.ServerPlayer;

public class HumanBonusHandler {

    /**
     * Humans get 25% bonus experience from all sources
     */
    public static int modifyExperienceGain(ServerPlayer player, int originalXP) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if ("Human".equals(playerRace)) {
            return (int)(originalXP * 1.25f); // +25% XP
        }

        return originalXP;
    }
}