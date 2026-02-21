package com.github.hitman20081.dagmod.party;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles party-related events
 */
public class PartyEvents {

    /**
     * Register all party event handlers
     */
    public static void register() {
        // Handle player disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            PartyManager.getInstance().handlePlayerDisconnect(player);
        });
    }
}