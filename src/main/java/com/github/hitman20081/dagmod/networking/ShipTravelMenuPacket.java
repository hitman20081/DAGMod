package com.github.hitman20081.dagmod.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import com.github.hitman20081.dagmod.DagMod;

public class ShipTravelMenuPacket {

    public static void openTravelMenu(ServerPlayerEntity player) {
        // This will be sent to client to open GUI
        // For now, send a message with available destinations
        var destinations = com.github.hitman20081.dagmod.travel.ShipTravelManager
                .getAccessibleDestinations(player);

        player.sendMessage(
                net.minecraft.text.Text.literal("═══════════════════════════════")
                        .formatted(net.minecraft.util.Formatting.GOLD),
                false
        );
        player.sendMessage(
                net.minecraft.text.Text.literal("⛵ Ship Travel Menu")
                        .formatted(net.minecraft.util.Formatting.YELLOW)
                        .formatted(net.minecraft.util.Formatting.BOLD),
                false
        );
        player.sendMessage(
                net.minecraft.text.Text.literal("Available Destinations:")
                        .formatted(net.minecraft.util.Formatting.GRAY),
                false
        );

        int index = 1;
        for (var entry : destinations.entrySet()) {
            var dest = entry.getValue();
            player.sendMessage(
                    net.minecraft.text.Text.literal(index + ". " + dest.name + " - " + dest.description)
                            .formatted(net.minecraft.util.Formatting.AQUA),
                    false
            );
            index++;
        }

        player.sendMessage(
                net.minecraft.text.Text.literal("Use /travel <destination> to sail there!")
                        .formatted(net.minecraft.util.Formatting.GRAY)
                        .formatted(net.minecraft.util.Formatting.ITALIC),
                false
        );
        player.sendMessage(
                net.minecraft.text.Text.literal("═══════════════════════════════")
                        .formatted(net.minecraft.util.Formatting.GOLD),
                false
        );
    }
}