package com.github.hitman20081.dagmod.party.quest;

import com.github.hitman20081.dagmod.party.PartyData;
import com.github.hitman20081.dagmod.party.PartyManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active party quests
 */
public class PartyQuestManager {
    private static final PartyQuestManager INSTANCE = new PartyQuestManager();

    // Map of party ID to active quest
    private final Map<UUID, PartyQuestData> activeQuests = new ConcurrentHashMap<>();

    private PartyQuestManager() {}

    public static PartyQuestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Start a party quest
     */
    public boolean startQuest(ServerPlayerEntity leader, String questId) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendMessage(Text.literal("You must be in a party to start a party quest!").formatted(Formatting.RED), false);
            return false;
        }

        if (!party.isLeader(leader.getUuid())) {
            leader.sendMessage(Text.literal("Only the party leader can start party quests!").formatted(Formatting.RED), false);
            return false;
        }

        // Check if party already has an active quest
        if (activeQuests.containsKey(party.getPartyId())) {
            leader.sendMessage(Text.literal("Your party already has an active quest!").formatted(Formatting.RED), false);
            return false;
        }

        // Get quest template
        PartyQuestTemplate template = PartyQuestRegistry.getQuest(questId);
        if (template == null) {
            leader.sendMessage(Text.literal("Unknown quest: " + questId).formatted(Formatting.RED), false);
            return false;
        }

        // Check party size requirements
        int partySize = party.getSize();
        if (partySize < template.getMinPartySize()) {
            leader.sendMessage(
                    Text.literal("This quest requires at least " + template.getMinPartySize() + " players!")
                            .formatted(Formatting.RED),
                    false
            );
            return false;
        }

        if (partySize > template.getMaxPartySize()) {
            leader.sendMessage(
                    Text.literal("This quest allows at most " + template.getMaxPartySize() + " players!")
                            .formatted(Formatting.RED),
                    false
            );
            return false;
        }

        // Create and start quest
        PartyQuestData questData = new PartyQuestData(questId, party.getPartyId(), template);
        activeQuests.put(party.getPartyId(), questData);

        // Notify party
        ServerWorld world = (ServerWorld) leader.getEntityWorld();
        party.sendPartyMessage(world,
                Text.literal("═══ Party Quest Started ═══").formatted(Formatting.GOLD)
        );
        party.sendPartyMessage(world,
                Text.literal(template.getName()).formatted(Formatting.YELLOW)
        );
        party.sendPartyMessage(world,
                Text.literal(template.getDescription()).formatted(Formatting.GRAY)
        );
        party.sendPartyMessage(world,
                Text.literal("Difficulty: " + template.getDifficulty().getDisplayName())
                        .formatted(Formatting.AQUA)
        );

        if (template.hasTimeLimit()) {
            party.sendPartyMessage(world,
                    Text.literal("Time Limit: " + template.getTimeLimitMinutes() + " minutes")
                            .formatted(Formatting.RED)
            );
        }

        party.sendPartyMessage(world, questData.getProgressText());

        return true;
    }

    /**
     * Get active quest for a party
     */
    public PartyQuestData getActiveQuest(UUID partyId) {
        return activeQuests.get(partyId);
    }

    public PartyQuestData getActiveQuest(ServerPlayerEntity player) {
        PartyData party = PartyManager.getInstance().getParty(player);
        if (party == null) return null;
        return getActiveQuest(party.getPartyId());
    }

    /**
     * Update quest objective progress
     */
    public void updateObjective(UUID partyId, String objectiveId, int amount) {
        PartyQuestData quest = activeQuests.get(partyId);
        if (quest == null || quest.isCompleted() || quest.isFailed()) {
            return;
        }

        quest.incrementObjective(objectiveId, amount);

        // Check if quest is complete
        if (quest.isCompleted()) {
            completeQuest(partyId);
        }
    }

    /**
     * Complete a party quest
     */
    private void completeQuest(UUID partyId) {
        PartyQuestData quest = activeQuests.get(partyId);
        if (quest == null) return;

        PartyData party = PartyManager.getInstance().getAllParties().stream()
                .filter(p -> p.getPartyId().equals(partyId))
                .findFirst()
                .orElse(null);

        if (party == null) {
            activeQuests.remove(partyId);
            return;
        }

        // Get all online party members
        List<ServerPlayerEntity> members = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            // You'll need to get player from server
            // This is simplified - you'll need actual player lookup
        }

        if (!members.isEmpty()) {
            ServerWorld world = (ServerWorld) members.get(0).getEntityWorld();

            // Notify completion
            party.sendPartyMessage(world,
                    Text.literal("═══ Quest Complete! ═══").formatted(Formatting.GREEN, Formatting.BOLD)
            );
            party.sendPartyMessage(world,
                    Text.literal(quest.getTemplate().getName() + " completed!")
                            .formatted(Formatting.YELLOW)
            );

            // Award rewards to all members
            distributeRewards(quest, members, world);
        }

        // Remove quest
        activeQuests.remove(partyId);
    }

    /**
     * Distribute rewards to party members
     */
    private void distributeRewards(PartyQuestData quest, List<ServerPlayerEntity> members, ServerWorld world) {
        PartyQuestTemplate template = quest.getTemplate();
        int baseXp = template.getXpReward();
        double multiplier = template.getDifficulty().getRewardMultiplier();
        int finalXp = (int) (baseXp * multiplier);

        for (ServerPlayerEntity member : members) {
            // Award XP
            com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, finalXp);
            member.sendMessage(
                    Text.literal("+" + finalXp + " XP").formatted(Formatting.GOLD),
                    false
            );

            // Award items
            for (PartyQuestReward reward : template.getRewards()) {
                if (!member.getInventory().insertStack(reward.createRewardStack())) {
                    // Drop if inventory full
                    member.dropStack(world, reward.createRewardStack());
                }

                member.sendMessage(
                        Text.literal("Received: " + reward.toString()).formatted(Formatting.GREEN),
                        false
                );
            }
        }
    }

    /**
     * Abandon/cancel a party quest
     */
    public boolean abandonQuest(ServerPlayerEntity leader) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return false;
        }

        if (!party.isLeader(leader.getUuid())) {
            leader.sendMessage(Text.literal("Only the party leader can abandon party quests!").formatted(Formatting.RED), false);
            return false;
        }

        PartyQuestData quest = activeQuests.remove(party.getPartyId());
        if (quest == null) {
            leader.sendMessage(Text.literal("Your party has no active quest!").formatted(Formatting.RED), false);
            return false;
        }

        ServerWorld world = (ServerWorld) leader.getEntityWorld();
        party.sendPartyMessage(world,
                Text.literal("Party quest abandoned.").formatted(Formatting.RED)
        );

        return true;
    }

    /**
     * Show quest progress
     */
    public void showProgress(ServerPlayerEntity player) {
        PartyQuestData quest = getActiveQuest(player);

        if (quest == null) {
            player.sendMessage(Text.literal("Your party has no active quest!").formatted(Formatting.RED), false);
            return;
        }

        player.sendMessage(
                Text.literal("═══ " + quest.getTemplate().getName() + " ═══").formatted(Formatting.GOLD),
                false
        );
        player.sendMessage(
                Text.literal("Progress: " + quest.getProgressPercentageInt() + "%").formatted(Formatting.YELLOW),
                false
        );
        player.sendMessage(quest.getProgressText(), false);

        if (quest.getTemplate().hasTimeLimit()) {
            long remaining = quest.getRemainingTime() / 1000;
            player.sendMessage(
                    Text.literal("Time Remaining: " + remaining + " seconds").formatted(Formatting.AQUA),
                    false
            );
        }
    }

    /**
     * Tick - check for timeouts
     */
    public void tick() {
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, PartyQuestData> entry : activeQuests.entrySet()) {
            PartyQuestData quest = entry.getValue();

            if (quest.isTimedOut() && !quest.isCompleted()) {
                quest.setFailed(true);
                toRemove.add(entry.getKey());

                // Notify party of failure
                // (You'll need to get party and notify members)
            }
        }

        for (UUID partyId : toRemove) {
            activeQuests.remove(partyId);
        }
    }

    /**
     * List available quests
     */
    public void listQuests(ServerPlayerEntity player) {
        player.sendMessage(
                Text.literal("═══ Available Party Quests ═══").formatted(Formatting.GOLD),
                false
        );

        for (PartyQuestTemplate template : PartyQuestRegistry.getAllQuests()) {
            player.sendMessage(
                    Text.literal("• ").formatted(Formatting.GRAY)
                            .append(Text.literal(template.getId()).formatted(Formatting.YELLOW))
                            .append(Text.literal(" - " + template.getName()).formatted(Formatting.WHITE))
                            .append(Text.literal(" [" + template.getDifficulty().getDisplayName() + "]")
                                    .formatted(Formatting.AQUA)),
                    false
            );
        }
    }
}