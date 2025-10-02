package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModNetworking {

    public static final Identifier QUEST_SYNC_ID = Identifier.of(DagMod.MOD_ID, "quest_sync");
    public static final Identifier QUEST_REQUEST_ID = Identifier.of(DagMod.MOD_ID, "quest_request");

    public static void registerC2SPackets() {
        // Client to Server packets
        PayloadTypeRegistry.playC2S().register(QuestRequestPacket.ID, QuestRequestPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(QuestRequestPacket.ID, (payload, context) -> {
            context.server().execute(() -> {
                QuestNetworkHandler.handleQuestRequest(context.player(), payload);
            });
        });
    }

    public static void registerS2CPackets() {
        // Server to Client packets
        PayloadTypeRegistry.playS2C().register(QuestSyncPacket.ID, QuestSyncPacket.CODEC);
    }

    public static void initialize() {
        registerC2SPackets();
        registerS2CPackets();
        DagMod.LOGGER.info("Networking packets registered for " + DagMod.MOD_ID);
    }
}