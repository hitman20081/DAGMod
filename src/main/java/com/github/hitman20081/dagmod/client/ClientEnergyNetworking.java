package com.github.hitman20081.dagmod.client;

import com.github.hitman20081.dagmod.class_system.rogue.EnergyNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-side networking handler for energy synchronization
 */
public class ClientEnergyNetworking {

    /**
     * Register client-side packet handlers
     */
    public static void register() {
        // Register energy sync packet handler
        ClientPlayNetworking.registerGlobalReceiver(
                EnergyNetworking.EnergySyncPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        // Update client-side energy display
                        EnergyHudRenderer.setClientEnergy(payload.energy());
                    });
                }
        );
    }
}