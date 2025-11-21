package com.github.hitman20081.dagmod.party.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    public Text getProgressText() {
        Text result = Text.literal("").formatted(Formatting.GRAY);

        for (PartyQuestObjective objective : template.getObjectives()) {
            int current = objectiveProgress.getOrDefault(objective.getId(), 0);
            int required = objective.getRequiredAmount();
            boolean complete = current >= required;

            Text line = Text.literal(complete ? "✓ " : "○ ")
                    .formatted(complete ? Formatting.GREEN : Formatting.GRAY)
                    .append(Text.literal(objective.getDescription() + " ")
                            .formatted(Formatting.WHITE))
                    .append(Text.literal("(" + Math.min(current, required) + "/" + required + ")")
                            .formatted(complete ? Formatting.GREEN : Formatting.YELLOW));

            result = result.copy().append("\n").append(line);
        }

        return result;
    }

    // NBT Serialization
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putString("QuestId", questId);
        nbt.putString("PartyId", partyId.toString());
        nbt.putString("TemplateId", template.getId());
        nbt.putLong("StartTime", startTime);
        nbt.putBoolean("Completed", completed);
        nbt.putBoolean("Failed", failed);

        // Save objective progress
        NbtList progressList = new NbtList();
        for (Map.Entry<String, Integer> entry : objectiveProgress.entrySet()) {
            NbtCompound progressNbt = new NbtCompound();
            progressNbt.putString("ObjectiveId", entry.getKey());
            progressNbt.putInt("Progress", entry.getValue());
            progressList.add(progressNbt);
        }
        nbt.put("ObjectiveProgress", progressList);

        return nbt;
    }

    // NBT Deserialization
    public static PartyQuestData fromNbt(NbtCompound nbt, PartyQuestTemplate template) {
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
        NbtList progressList = nbt.getList("ObjectiveProgress").orElse(new NbtList());
        for (int i = 0; i < progressList.size(); i++) {
            NbtCompound progressNbt = progressList.getCompound(i).orElse(null);
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