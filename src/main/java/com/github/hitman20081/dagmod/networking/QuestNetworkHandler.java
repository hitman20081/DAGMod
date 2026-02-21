package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestNetworkHandler {

    public static void handleQuestRequest(ServerPlayerEntity player, QuestRequestPacket packet) {
        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        // Build active quest info list
        List<QuestSyncPacket.QuestInfo> activeQuestInfos = new ArrayList<>();

        Collection<Quest> activeQuests = playerData.getActiveQuests();
        if (activeQuests != null) {
            for (Quest quest : activeQuests) {
                List<String> objectiveDescs = new ArrayList<>();
                int completedObjectives = 0;

                List<QuestObjective> objectives = quest.getObjectives();
                if (objectives != null) {
                    for (QuestObjective obj : objectives) {
                        objectiveDescs.add(obj.getDisplayText().getString());
                        if (obj.isCompleted()) {
                            completedObjectives++;
                        }
                    }
                }

                activeQuestInfos.add(new QuestSyncPacket.QuestInfo(
                        quest.getId(),
                        quest.getName(),
                        quest.getDescription(),
                        quest.getDifficulty(),
                        completedObjectives,
                        objectives != null ? objectives.size() : 0,
                        quest.isCompleted(),
                        objectiveDescs
                ));
            }
        }

        // Build available quest info list
        List<QuestSyncPacket.QuestInfo> availableQuestInfos = new ArrayList<>();
        List<Quest> availableQuests = manager.getAvailableQuests(player);

        if (availableQuests != null) {
            for (Quest quest : availableQuests) {
                List<String> objectiveDescs = new ArrayList<>();

                List<QuestObjective> objectives = quest.getObjectives();
                if (objectives != null) {
                    for (QuestObjective obj : objectives) {
                        objectiveDescs.add(obj.getDisplayText().getString());
                    }
                }

                availableQuestInfos.add(new QuestSyncPacket.QuestInfo(
                        quest.getId(),
                        quest.getName(),
                        quest.getDescription(),
                        quest.getDifficulty(),
                        0,
                        objectives != null ? objectives.size() : 0,
                        false,
                        objectiveDescs
                ));
            }
        }

        // Send sync packet with both active and available quests
        QuestSyncPacket syncPacket = new QuestSyncPacket(
                playerData.getQuestBookTier(),
                playerData.getActiveQuestCount(),
                playerData.getMaxActiveQuests(),
                playerData.getTotalQuestsCompleted(),
                activeQuestInfos,
                availableQuestInfos  // NEW: Include available quests
        );

        ServerPlayNetworking.send(player, syncPacket);
    }

    public static void sendQuestUpdate(ServerPlayerEntity player) {
        handleQuestRequest(player, new QuestRequestPacket());
    }
}