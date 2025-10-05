package com.github.hitman20081.dagmod.progression;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Registers all progression system event handlers
 * Call register() during mod initialization
 */
public class ProgressionEvents {

    /**
     * Register all progression events
     */
    public static void register() {
        // Server starting - initialize storage
        ServerLifecycleEvents.SERVER_STARTING.register(ProgressionEvents::onServerStarting);

        // Server stopping - save all data
        ServerLifecycleEvents.SERVER_STOPPING.register(ProgressionEvents::onServerStopping);

        // Player join - load their data
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ProgressionManager.loadPlayerData(handler.player);
        });

        // Player disconnect - save and unload data
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ProgressionManager.savePlayerData(handler.player);
            ProgressionManager.unloadPlayerData(handler.player.getUuid());
        });
    }

    /**
     * Called when server starts
     */
    private static void onServerStarting(MinecraftServer server) {
        // Initialize storage with server instance
        ProgressionStorage.initialize(server);
    }

    /**
     * Called when server stops
     */
    private static void onServerStopping(MinecraftServer server) {
        // Save all loaded player data
        ProgressionManager.saveAllPlayerData();
    }
}