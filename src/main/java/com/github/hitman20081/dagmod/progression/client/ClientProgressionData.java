package com.github.hitman20081.dagmod.progression.client;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.github.hitman20081.dagmod.progression.ProgressionPackets;

/**
 * Client-side progression data storage and packet handling
 * Stores the local player's progression for HUD display
 */
public class ClientProgressionData {

    // Store local player's progression data
    private static PlayerProgressionData localPlayerData = null;

    /**
     * Get the local player's progression data
     * @return Current progression data, or null if not loaded
     */
    public static PlayerProgressionData getLocalPlayerData() {
        return localPlayerData;
    }

    /**
     * Set the local player's progression data
     * @param data New progression data
     */
    public static void setLocalPlayerData(PlayerProgressionData data) {
        localPlayerData = data;
    }

    /**
     * Clear local data (on disconnect)
     */
    public static void clearLocalData() {
        localPlayerData = null;
    }

    /**
     * Register client-side packet handlers
     * Call this during client mod initialization
     */
    public static void registerClientPackets() {
        // Don't register payload type here - it's already registered server-side
        // The PayloadTypeRegistry.playS2C() is shared between client and server

        // Handle progression sync from server
        ClientPlayNetworking.registerGlobalReceiver(
                ProgressionPackets.ProgressionSyncPayload.ID,
                (payload, context) -> {
                    // Must process on client thread
                    context.client().execute(() -> {
                        PlayerProgressionData data = PlayerProgressionData.fromNbt(payload.data());
                        setLocalPlayerData(data);
                    });
                }
        );
    }
}