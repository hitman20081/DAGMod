package com.github.hitman20081.dagmod.party.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.*;

/**
 * Data structure for a party quest instance
 */
public class PartyQuestData {
    private final String questId;
    private final UUID partyId;
    private final PartyQuestTemplate template;
    private final long startTime;
    private final Map<String, Integer> objectiveProgress;
    private boolean completed;
    private boolean failed;

    public PartyQuestData(String questId, UUID partyId, PartyQuestTemplate template) {
        this.questId = questId;
        this.partyId = partyId;
        this.template = template;
        this.startTime = System.currentTimeMillis();
        this.objectiveProgress = new HashMap<>();
        this.completed = false;
        this.failed = false;

        // Initialize objective progress
        for (PartyQuestObjective objective : template.getObjectives()) {
            objectiveProgress.put(objective.getId(), 0);
        }
    }

    // Getters
    public String getQuestId() {
        return questId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public PartyQuestTemplate getTemplate() {
        return template;
    }

    public long getStartTime() {
        return startTime;
    }

    public Map<String, Integer> getObjectiveProgress() {
        return new HashMap<>(objectiveProgress);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isFailed() {
        return failed;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public long getRemainingTime() {
        if (template.getTimeLimit() <= 0) {
            return -1; // No time limit
        }
        return template.getTimeLimit() - getElapsedTime();
    }

    public boolean isTimedOut() {
        if (template.getTimeLimit() <= 0) {
            return false; // No time limit
        }
        return getElapsedTime() >= template.getTimeLimit();
    }

    // Progress tracking
    public void incrementObjective(String objectiveId, int amount) {
        int current = objectiveProgress.getOrDefault(objectiveId, 0);
        objectiveProgress.put(objectiveId, current + amount);

        checkCompletion();
    }

    public int getObjectiveProgress(String objectiveId) {
        return objectiveProgress.getOrDefault(objectiveId, 0);
    }

    public boolean isObjectiveComplete(String objectiveId) {
        PartyQuestObjective objective = template.getObjective(objectiveId);
        if (objective == null) return false;

        int current = objectiveProgress.getOrDefault(objectiveId, 0);
        return current >= objective.getRequiredAmount();
    }

    public double getProgressPercentage() {
        int totalRequired = 0;
        int totalCurrent = 0;

        for (PartyQuestObjective objective : template.getObjectives()) {
            totalRequired += objective.getRequiredAmount();
            totalCurrent += Math.min(
                    objectiveProgress.getOrDefault(objective.getId(), 0),
                    objective.getRequiredAmount()
            );
        }

        if (totalRequired == 0) return 0;
        return (double) totalCurrent / totalRequired;
    }

    public int getProgressPercentageInt() {
        return (int) (getProgressPercentage() * 100);
    }

    // Check if all objectives are complete
    private void checkCompletion() {
        for (PartyQuestObjective objective : template.getObjectives()) {
            if (!isObjectiveComplete(objective.getId())) {
                return; // Not all objectives complete
            }
        }
        completed = true;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    // Get formatted progress text
    public Component getProgressText() {
        Component result = Component.literal("").withStyle(ChatFormatting.GRAY);

        for (PartyQuestObjective objective : template.getObjectives()) {
            int current = objectiveProgress.getOrDefault(objective.getId(), 0);
            int required = objective.getRequiredAmount();
            boolean complete = current >= required;

            Component line = Component.literal(complete ? "✓ " : "○ ")
                    .withStyle(complete ? ChatFormatting.GREEN : ChatFormatting.GRAY)
                    .append(Component.literal(objective.getDescription() + " ")
                            .withStyle(ChatFormatting.WHITE))
                    .append(Component.literal("(" + Math.min(current, required) + "/" + required + ")")
                            .withStyle(complete ? ChatFormatting.GREEN : ChatFormatting.YELLOW));

            result = result.copy().append("\n").append(line);
        }

        return result;
    }

    // NBT Serialization
    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("QuestId", questId);
        nbt.putString("PartyId", partyId.toString());
        nbt.putString("TemplateId", template.getId());
        nbt.putLong("StartTime", startTime);
        nbt.putBoolean("Completed", completed);
        nbt.putBoolean("Failed", failed);

        // Save objective progress
        ListTag progressList = new ListTag();
        for (Map.Entry<String, Integer> entry : objectiveProgress.entrySet()) {
            CompoundTag progressNbt = new CompoundTag();
            progressNbt.putString("ObjectiveId", entry.getKey());
            progressNbt.putInt("Progress", entry.getValue());
            progressList.add(progressNbt);
        }
        nbt.put("ObjectiveProgress", progressList);

        return nbt;
    }

    // NBT Deserialization
    public static PartyQuestData fromNbt(CompoundTag nbt, PartyQuestTemplate template) {
        String questId = nbt.getString("QuestId").orElse("");
        UUID partyId = UUID.fromString(nbt.getString("PartyId").orElse(UUID.randomUUID().toString()));

        PartyQuestData data = new PartyQuestData(questId, partyId, template);

        // Restore times and status
        if (nbt.getLong("StartTime").isPresent()) {
            // Can't set final field, but we can restore progress
        }
        data.completed = nbt.getBoolean("Completed").orElse(false);
        data.failed = nbt.getBoolean("Failed").orElse(false);

        // Restore objective progress
        ListTag progressList = nbt.getList("ObjectiveProgress").orElse(new ListTag());
        for (int i = 0; i < progressList.size(); i++) {
            CompoundTag progressNbt = progressList.getCompound(i).orElse(null);
            if (progressNbt != null) {
                String objectiveId = progressNbt.getString("ObjectiveId").orElse("");
                int progress = progressNbt.getInt("Progress").orElse(0);
                data.objectiveProgress.put(objectiveId, progress);
            }
        }

        return data;
    }

    @Override
    public String toString() {
        return "PartyQuest{" +
                "id=" + questId +
                ", template=" + template.getName() +
                ", progress=" + getProgressPercentageInt() + "%" +
                ", completed=" + completed +
                '}';
    }
}