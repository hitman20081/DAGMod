package com.github.hitman20081.dagmod.client;

import com.github.hitman20081.dagmod.gui.QuestBookScreen;
import com.github.hitman20081.dagmod.networking.QuestRequestPacket;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class QuestBookClientHandler {

    public static void openQuestBook(QuestData.QuestBookTier tier) {
        // Request fresh quest data from server before opening the screen
        ClientPlayNetworking.send(new QuestRequestPacket());
        MinecraftClient.getInstance().setScreen(new QuestBookScreen(tier));
    }
}
