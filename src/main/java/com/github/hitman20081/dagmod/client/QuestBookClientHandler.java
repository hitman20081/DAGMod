package com.github.hitman20081.dagmod.client;

import com.github.hitman20081.dagmod.gui.QuestBookScreen;
import com.github.hitman20081.dagmod.networking.QuestRequestPacket;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class QuestBookClientHandler {

    public static void openQuestBook(QuestData.QuestBookTier tier) {
        // Request quest data from server
        ClientPlayNetworking.send(new QuestRequestPacket());

        // Open the GUI
        MinecraftClient.getInstance().setScreen(new QuestBookScreen(tier));
    }
}