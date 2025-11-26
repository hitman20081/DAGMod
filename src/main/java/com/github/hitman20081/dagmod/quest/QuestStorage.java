package com.github.hitman20081.dagmod.quest;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Handles persistent storage of quest data to NBT files
 *
 * Storage location: world/data/dagmod/quests/{uuid}.dat
 */
public class QuestStorage {

    private static final String DATA_ROOT = "data/dagmod";
    private static final String QUESTS_FOLDER = "quests";

    // NBT keys
    private static final String ACTIVE_QUESTS_KEY = "activeQuests";
    private static final String COMPLETED_QUESTS_KEY = "completedQuests";
    private static final String QUEST_COMPLETION_TIMES_KEY = "questCompletionTimes";
    private static final String TOTAL_QUESTS_COMPLETED_KEY = "totalQuestsCompleted";
    private static final String QUEST_BOOK_TIER_KEY = "questBookTier";

    // Individual quest progress keys
    private static final String QUEST_ID_KEY = "questId";
    private static final String QUEST_OBJECTIVES_KEY = "objectives";
    private static final String OBJECTIVE_CURRENT_KEY = "current";
    private static final String OBJECTIVE_REQUIRED_KEY = "required";

    /**
     * Get the quests data directory
     */
    private static File getQuestsDirectory(MinecraftServer server) {
        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File dagmodRoot = new File(worldDir, DATA_ROOT);
        File questsDir = new File(dagmodRoot, QUESTS_FOLDER);

        if (!questsDir.exists()) {
            questsDir.mkdirs();
        }

        return questsDir;
    }

    /**
     * Get the quest data file for a specific player
     */
    private static File getQuestDataFile(MinecraftServer server, UUID playerId) {
        File questsDir = getQuestsDirectory(server);
        return new File(questsDir, playerId.toString() + ".dat");
    }

    /**
     * Save player's quest data to file
     */
    public static void saveQuestData(ServerPlayerEntity player, QuestData questData) {
        try {
            File dataFile = getQuestDataFile(player.getEntityWorld().getServer(), player.getUuid());
            NbtCompound nbt = new NbtCompound();

            // Save quest book tier
            nbt.putString(QUEST_BOOK_TIER_KEY, questData.getQuestBookTier().name());

            // Save total quests completed
            nbt.putInt(TOTAL_QUESTS_COMPLETED_KEY, questData.getTotalQuestsCompleted());

            // Save completed quest IDs
            NbtList completedList = new NbtList();
            for (String questId : questData.getCompletedQuestIds()) {
                NbtCompound questNbt = new NbtCompound();
                questNbt.putString("id", questId);
                completedList.add(questNbt);
            }
            nbt.put(COMPLETED_QUESTS_KEY, completedList);

            // Save quest completion times
            NbtCompound timesNbt = new NbtCompound();
            for (Map.Entry<String, Long> entry : questData.getQuestCompletionTimes().entrySet()) {
                timesNbt.putLong(entry.getKey(), entry.getValue());
            }
            nbt.put(QUEST_COMPLETION_TIMES_KEY, timesNbt);

            // Save active quests with their progress
            NbtList activeList = new NbtList();
            for (Quest quest : questData.getActiveQuests()) {
                NbtCompound questNbt = new NbtCompound();
                questNbt.putString(QUEST_ID_KEY, quest.getId());

                // Save objective progress
                NbtList objectivesList = new NbtList();
                for (int i = 0; i < quest.getObjectives().size(); i++) {
                    QuestObjective objective = quest.getObjectives().get(i);
                    NbtCompound objectiveNbt = new NbtCompound();
                    objectiveNbt.putInt(OBJECTIVE_CURRENT_KEY, objective.getCurrentProgress());
                    objectiveNbt.putInt(OBJECTIVE_REQUIRED_KEY, objective.getRequiredProgress());
                    objectivesList.add(objectiveNbt);
                }
                questNbt.put(QUEST_OBJECTIVES_KEY, objectivesList);

                activeList.add(questNbt);
            }
            nbt.put(ACTIVE_QUESTS_KEY, activeList);

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }

            DagMod.LOGGER.info("Saved quest data for player: " + player.getName().getString());

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to save quest data for " + player.getName().getString(), e);
        }
    }

    /**
     * Load player's quest data from file
     * Returns null if no save data exists (new player)
     */
    public static QuestData loadQuestData(ServerPlayerEntity player) {
        try {
            File dataFile = getQuestDataFile(player.getEntityWorld().getServer(), player.getUuid());

            if (!dataFile.exists()) {
                DagMod.LOGGER.info("No quest data found for player: " + player.getName().getString() + " (new player)");
                return null; // No data to load for new players
            }

            NbtCompound nbt;
            try (FileInputStream fis = new FileInputStream(dataFile)) {
                nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            }

            // Create new QuestData
            QuestData questData = new QuestData(player.getUuid());
            QuestManager manager = QuestManager.getInstance();

            // Load quest book tier
            if (nbt.contains(QUEST_BOOK_TIER_KEY)) {
                String tierName = nbt.getString(QUEST_BOOK_TIER_KEY).orElse("NOVICE");
                try {
                    QuestData.QuestBookTier tier = QuestData.QuestBookTier.valueOf(tierName);
                    questData.setQuestBookTier(tier);
                } catch (IllegalArgumentException e) {
                    DagMod.LOGGER.warn("Invalid quest book tier: " + tierName + ", defaulting to NOVICE");
                }
            }

            // Load total quests completed
            if (nbt.contains(TOTAL_QUESTS_COMPLETED_KEY)) {
                questData.setTotalQuestsCompleted(nbt.getInt(TOTAL_QUESTS_COMPLETED_KEY).orElse(0));
            }

            // Load completed quest IDs
            if (nbt.contains(COMPLETED_QUESTS_KEY)) {
                NbtList completedList = nbt.getList(COMPLETED_QUESTS_KEY).orElse(new NbtList());
                for (int i = 0; i < completedList.size(); i++) {
                    NbtCompound questNbt = completedList.getCompound(i).orElse(new NbtCompound());
                    String questId = questNbt.getString("id").orElse("");
                    if (!questId.isEmpty()) {
                        questData.markQuestCompleted(questId);
                    }
                }
            }

            // Load quest completion times
            if (nbt.contains(QUEST_COMPLETION_TIMES_KEY)) {
                NbtCompound timesNbt = nbt.getCompound(QUEST_COMPLETION_TIMES_KEY).orElse(new NbtCompound());
                for (String key : timesNbt.getKeys()) {
                    long time = timesNbt.getLong(key).orElse(0L);
                    questData.setQuestCompletionTime(key, time);
                }
            }

            // Load active quests
            if (nbt.contains(ACTIVE_QUESTS_KEY)) {
                NbtList activeList = nbt.getList(ACTIVE_QUESTS_KEY).orElse(new NbtList());
                for (int i = 0; i < activeList.size(); i++) {
                    NbtCompound questNbt = activeList.getCompound(i).orElse(new NbtCompound());
                    String questId = questNbt.getString(QUEST_ID_KEY).orElse("");

                    // Get the quest from registry
                    Quest quest = manager.getQuest(questId);
                    if (quest == null) {
                        DagMod.LOGGER.warn("Quest not found in registry: " + questId + " (skipping)");
                        continue;
                    }

                    // Create a copy of the quest for this player
                    Quest questCopy = quest.copy();

                    // Restore objective progress
                    if (questNbt.contains(QUEST_OBJECTIVES_KEY)) {
                        NbtList objectivesList = questNbt.getList(QUEST_OBJECTIVES_KEY).orElse(new NbtList());
                        for (int j = 0; j < objectivesList.size() && j < questCopy.getObjectives().size(); j++) {
                            NbtCompound objectiveNbt = objectivesList.getCompound(j).orElse(new NbtCompound());
                            int current = objectiveNbt.getInt(OBJECTIVE_CURRENT_KEY).orElse(0);

                            QuestObjective objective = questCopy.getObjectives().get(j);
                            objective.setProgress(current);
                        }
                    }

                    // Add to player's active quests
                    questData.addActiveQuest(questCopy);
                }
            }

            DagMod.LOGGER.info("Loaded quest data for player: " + player.getName().getString() +
                    " (Active: " + questData.getActiveQuestCount() +
                    ", Completed: " + questData.getCompletedQuestIds().size() +
                    ", Tier: " + questData.getQuestBookTier().name() + ")");

            return questData;

        } catch (IOException e) {
            DagMod.LOGGER.error("Failed to load quest data for " + player.getName().getString(), e);
            return null;
        }
    }

    /**
     * Check if player has existing quest data
     */
    public static boolean hasQuestData(ServerPlayerEntity player) {
        File dataFile = getQuestDataFile(player.getEntityWorld().getServer(), player.getUuid());
        return dataFile.exists();
    }

    /**
     * Delete quest data file (used for reset commands)
     */
    public static boolean deleteQuestData(ServerPlayerEntity player) {
        File dataFile = getQuestDataFile(player.getEntityWorld().getServer(), player.getUuid());
        if (dataFile.exists()) {
            return dataFile.delete();
        }
        return false;
    }
}
