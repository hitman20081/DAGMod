package com.github.hitman20081.dagmod.party.quest;

import com.github.hitman20081.dagmod.party.PartyData;
import com.github.hitman20081.dagmod.party.PartyManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
    public boolean startQuest(ServerPlayer leader, String questId) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendSystemMessage(Component.literal("You must be in a party to start a party quest!").withStyle(ChatFormatting.RED));
            return false;
        }

        if (!party.isLeader(leader.getUUID())) {
            leader.sendSystemMessage(Component.literal("Only the party leader can start party quests!").withStyle(ChatFormatting.RED));
            return false;
        }

        // Check if party already has an active quest
        if (activeQuests.containsKey(party.getPartyId())) {
            leader.sendSystemMessage(Component.literal("Your party already has an active quest!").withStyle(ChatFormatting.RED));
            return false;
        }

        // Get quest template
        PartyQuestTemplate template = PartyQuestRegistry.getQuest(questId);
        if (template == null) {
            leader.sendSystemMessage(Component.literal("Unknown quest: " + questId).withStyle(ChatFormatting.RED));
            return false;
        }

        // Check party size requirements
        int partySize = party.getSize();
        if (partySize < template.getMinPartySize()) {
            leader.sendSystemMessage(
                    Component.literal("This quest requires at least " + template.getMinPartySize() + " players!")
                            .withStyle(ChatFormatting.RED));
            return false;
        }

        if (partySize > template.getMaxPartySize()) {
            leader.sendSystemMessage(
                    Component.literal("This quest allows at most " + template.getMaxPartySize() + " players!")
                            .withStyle(ChatFormatting.RED));
            return false;
        }

        // Create and start quest
        PartyQuestData questData = new PartyQuestData(questId, party.getPartyId(), template);
        activeQuests.put(party.getPartyId(), questData);

        // Notify party
        ServerLevel world = (ServerLevel) leader.level();
        party.sendPartyMessage(world,
                Component.literal("═══ Party Quest Started ═══").withStyle(ChatFormatting.GOLD)
        );
        party.sendPartyMessage(world,
                Component.literal(template.getName()).withStyle(ChatFormatting.YELLOW)
        );
        party.sendPartyMessage(world,
                Component.literal(template.getDescription()).withStyle(ChatFormatting.GRAY)
        );
        party.sendPartyMessage(world,
                Component.literal("Difficulty: " + template.getDifficulty().getDisplayName())
                        .withStyle(ChatFormatting.AQUA)
        );

        if (template.hasTimeLimit()) {
            party.sendPartyMessage(world,
                    Component.literal("Time Limit: " + template.getTimeLimitMinutes() + " minutes")
                            .withStyle(ChatFormatting.RED)
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

    public PartyQuestData getActiveQuest(ServerPlayer player) {
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
        List<ServerPlayer> members = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            // You'll need to get player from server
            // This is simplified - you'll need actual player lookup
        }

        if (!members.isEmpty()) {
            ServerLevel world = (ServerLevel) members.get(0).level();

            // Notify completion
            party.sendPartyMessage(world,
                    Component.literal("═══ Quest Complete! ═══").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
            );
            party.sendPartyMessage(world,
                    Component.literal(quest.getTemplate().getName() + " completed!")
                            .withStyle(ChatFormatting.YELLOW)
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
    private void distributeRewards(PartyQuestData quest, List<ServerPlayer> members, ServerLevel world) {
        PartyQuestTemplate template = quest.getTemplate();
        int baseXp = template.getXpReward();
        double multiplier = template.getDifficulty().getRewardMultiplier();
        int finalXp = (int) (baseXp * multiplier);

        for (ServerPlayer member : members) {
            // Award XP
            com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, finalXp);
            member.sendSystemMessage(
                    Component.literal("+" + finalXp + " XP").withStyle(ChatFormatting.GOLD));

            // Award items
            for (PartyQuestReward reward : template.getRewards()) {
                if (!member.getInventory().add(reward.createRewardStack())) {
                    // Drop if inventory full
                    member.drop(reward.createRewardStack(), false);
                }

                member.sendSystemMessage(
                        Component.literal("Received: " + reward.toString()).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    /**
     * Abandon/cancel a party quest
     */
    public boolean abandonQuest(ServerPlayer leader) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return false;
        }

        if (!party.isLeader(leader.getUUID())) {
            leader.sendSystemMessage(Component.literal("Only the party leader can abandon party quests!").withStyle(ChatFormatting.RED));
            return false;
        }

        PartyQuestData quest = activeQuests.remove(party.getPartyId());
        if (quest == null) {
            leader.sendSystemMessage(Component.literal("Your party has no active quest!").withStyle(ChatFormatting.RED));
            return false;
        }

        ServerLevel world = (ServerLevel) leader.level();
        party.sendPartyMessage(world,
                Component.literal("Party quest abandoned.").withStyle(ChatFormatting.RED)
        );

        return true;
    }

    /**
     * Show quest progress
     */
    public void showProgress(ServerPlayer player) {
        PartyQuestData quest = getActiveQuest(player);

        if (quest == null) {
            player.sendSystemMessage(Component.literal("Your party has no active quest!").withStyle(ChatFormatting.RED));
            return;
        }

        player.sendSystemMessage(
                Component.literal("═══ " + quest.getTemplate().getName() + " ═══").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(
                Component.literal("Progress: " + quest.getProgressPercentageInt() + "%").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(quest.getProgressText());

        if (quest.getTemplate().hasTimeLimit()) {
            long remaining = quest.getRemainingTime() / 1000;
            player.sendSystemMessage(
                    Component.literal("Time Remaining: " + remaining + " seconds").withStyle(ChatFormatting.AQUA));
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
    public void listQuests(ServerPlayer player) {
        player.sendSystemMessage(
                Component.literal("═══ Available Party Quests ═══").withStyle(ChatFormatting.GOLD));

        for (PartyQuestTemplate template : PartyQuestRegistry.getAllQuests()) {
            player.sendSystemMessage(
                    Component.literal("• ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(template.getId()).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" - " + template.getName()).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(" [" + template.getDifficulty().getDisplayName() + "]")
                                    .withStyle(ChatFormatting.AQUA)));
        }
    }
}