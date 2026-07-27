package com.github.hitman20081.dagmod.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import com.github.hitman20081.dagmod.DagMod;

public class ShipTravelMenuPacket {

    public static void openTravelMenu(ServerPlayer player) {
        // This will be sent to client to open GUI
        // For now, send a message with available destinations
        var destinations = com.github.hitman20081.dagmod.travel.ShipTravelManager
                .getAccessibleDestinations(player);

        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("═══════════════════════════════")
                        .withStyle(net.minecraft.ChatFormatting.GOLD));
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("⛵ Ship Travel Menu")
                        .withStyle(net.minecraft.ChatFormatting.YELLOW)
                        .withStyle(net.minecraft.ChatFormatting.BOLD));
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("Available Destinations:")
                        .withStyle(net.minecraft.ChatFormatting.GRAY));

        int index = 1;
        for (var entry : destinations.entrySet()) {
            var dest = entry.getValue();
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(index + ". " + dest.name + " - " + dest.description)
                            .withStyle(net.minecraft.ChatFormatting.AQUA));
            index++;
        }

        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("Use /travel <destination> to sail there!")
                        .withStyle(net.minecraft.ChatFormatting.GRAY)
                        .withStyle(net.minecraft.ChatFormatting.ITALIC));
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("═══════════════════════════════")
                        .withStyle(net.minecraft.ChatFormatting.GOLD));
    }
}