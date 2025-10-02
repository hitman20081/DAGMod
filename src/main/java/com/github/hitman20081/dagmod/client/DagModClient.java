package com.github.hitman20081.dagmod.client;

import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DagModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register client-side packet receivers
        ClientPlayNetworking.registerGlobalReceiver(QuestSyncPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientQuestData.getInstance().updateFromPacket(payload);
            });
        });
    }
}