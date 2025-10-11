package com.github.hitman20081.dagmod.event;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DeathMessageHandler {

    public static void sendDeathMessage(ServerPlayerEntity player) {
        String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

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

            player.getEntityWorld().getServer().getPlayerManager().broadcast(
                    Text.literal(player.getName().getString() + " (" + message + ") has died")
                            .formatted(Formatting.RED),
                    false
            );
        }
    }
}