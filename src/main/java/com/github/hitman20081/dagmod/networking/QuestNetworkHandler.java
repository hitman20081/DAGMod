package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class QuestNetworkHandler {

    public static void handleQuestRequest(ServerPlayerEntity player, QuestRequestPacket packet) {
        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        // Build quest info list
        List<QuestSyncPacket.QuestInfo> questInfos = new ArrayList<>();

        for (Quest quest : playerData.getActiveQuests()) {
            List<String> objectiveDescs = new ArrayList<>();
            int completedObjectives = 0;

            for (QuestObjective obj : quest.getObjectives()) {
                objectiveDescs.add(obj.getDisplayText().getString());
                if (obj.isCompleted()) {
                    completedObjectives++;
                }
            }

            questInfos.add(new QuestSyncPacket.QuestInfo(
                    quest.getId(),
                    quest.getName(),
                    quest.getDescription(),
                    quest.getDifficulty(),
                    completedObjectives,
                    quest.getObjectives().size(),
                    quest.isCompleted(),
                    objectiveDescs
            ));
        }

        // Send sync packet
        QuestSyncPacket syncPacket = new QuestSyncPacket(
                playerData.getQuestBookTier(),
                playerData.getActiveQuestCount(),
                playerData.getMaxActiveQuests(),
                playerData.getTotalQuestsCompleted(),
                questInfos
        );

        ServerPlayNetworking.send(player, syncPacket);
    }

    public static void sendQuestUpdate(ServerPlayerEntity player) {
        handleQuestRequest(player, new QuestRequestPacket());
    }
}