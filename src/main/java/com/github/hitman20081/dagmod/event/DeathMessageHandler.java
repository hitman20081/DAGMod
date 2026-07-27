package com.github.hitman20081.dagmod.event;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class DeathMessageHandler {

    public static void sendDeathMessage(ServerPlayer player) {
        String race = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        // Only send if player has race/class selected
        if (!race.equals("none") || !playerClass.equals("none")) {
            StringBuilder message = new StringBuilder();

            if (!race.equals("none")) {
                message.append(race);
            }

            if (!playerClass.equals("none")) {
                if (message.length() > 0) {
                    message.append(" ");
                }
                message.append(playerClass);
            }

            player.level().getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal(player.getName().getString() + " (" + message + ") has died")
                            .withStyle(ChatFormatting.RED),
                    false
            );
        }
    }
}