package com.github.hitman20081.dagmod.race_system;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

public class PlayerTickHandler {

    private static int tickCounter = 0;
    private static final int SYNERGY_CHECK_INTERVAL = 20; // Check every second (20 ticks)

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Check synergies every second
            if (tickCounter >= SYNERGY_CHECK_INTERVAL) {
                tickCounter = 0;

                // Apply synergy bonuses to all online players
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    RaceClassSynergyManager.applySynergyBonuses(player);
                }
            }
        });
    }
}