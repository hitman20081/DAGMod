package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.networking.QuestSyncPacket;

import java.util.ArrayList;
import java.util.List;

public class ClientQuestData {
    private static ClientQuestData instance;

    private QuestData.QuestBookTier tier = QuestData.QuestBookTier.NOVICE;
    private int activeQuestCount = 0;
    private int maxActiveQuests = 2;
    private int totalCompleted = 0;
    private List<QuestSyncPacket.QuestInfo> activeQuests = new ArrayList<>();
    private List<QuestSyncPacket.QuestInfo> availableQuests = new ArrayList<>();

    public static ClientQuestData getInstance() {
        if (instance == null) {
            instance = new ClientQuestData();
        }
        return instance;
    }

    public void updateFromPacket(QuestSyncPacket packet) {
        this.tier = packet.tier();
        this.activeQuestCount = packet.activeQuestCount();
        this.maxActiveQuests = packet.maxActiveQuests();
        this.totalCompleted = packet.totalCompleted();
        this.activeQuests = new ArrayList<>(packet.activeQuests());
        this.availableQuests = new ArrayList<>(packet.availableQuests());
    }

    public QuestData.QuestBookTier getTier() { return tier; }
    public int getActiveQuestCount() { return activeQuestCount; }
    public int getMaxActiveQuests() { return maxActiveQuests; }
    public int getTotalCompleted() { return totalCompleted; }
    public List<QuestSyncPacket.QuestInfo> getActiveQuests() { return activeQuests; }
    public List<QuestSyncPacket.QuestInfo> getAvailableQuests() { return availableQuests; }
}