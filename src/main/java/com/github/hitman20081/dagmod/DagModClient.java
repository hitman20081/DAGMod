package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.mana.client.ClientManaData;
import com.github.hitman20081.dagmod.class_system.mana.client.ManaHudRenderer;
import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.progression.client.ClientProgressionData;
import com.github.hitman20081.dagmod.progression.client.ProgressionHUD;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DagModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("DAGMod client initializing...");

        // Register progression system FIRST (before any packets)
        ClientProgressionData.registerClientPackets();
        ProgressionHUD.register();
        System.out.println("Progression HUD registered!");

        // Register client-side quest packet receivers
        ClientPlayNetworking.registerGlobalReceiver(QuestSyncPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientQuestData.getInstance().updateFromPacket(payload);
            });
        });

        // Register mana system - ADD THIS
        ClientPlayNetworking.registerGlobalReceiver(
                ManaNetworking.ManaUpdatePayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        ClientManaData.setMana(payload.currentMana(), payload.maxMana());
                    });
                }
        );

        // Register Mana HUD renderer - ADD THIS
        HudRenderCallback.EVENT.register(new ManaHudRenderer());
        System.out.println("Mana system registered!");

        System.out.println("DAGMod client networking initialized!");
    }
}