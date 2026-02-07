package com.github.hitman20081.dagmod.party;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Handles quest progress sharing for parties
 *
 * NOTE: This is a simplified placeholder. To fully integrate with your quest system:
 * 1. Hook into your QuestManager's progress update methods
 * 2. Call the notification methods when party members should be updated
 * 3. Customize based on your QuestData structure
 */
public class PartyQuestHandler {

    /**
     * Notify party members when the player makes quest progress
     * Call this from your quest progress update code
     *
     * @param player The player who made progress
     * @param progressMessage The message to send to party
     */
    public static void notifyPartyQuestProgress(ServerPlayerEntity player, String progressMessage) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party members
        Text message = Text.literal(player.getName().getString() + " ")
                .formatted(Formatting.AQUA)
                .append(Text.literal(progressMessage)
                        .formatted(Formatting.GRAY));

        for (ServerPlayerEntity member : nearbyMembers) {
            member.sendMessage(message, true);
        }
    }

    /**
     * Share mob kill credit with party members
     * Call this from your mob kill handler
     *
     * Example usage in your death event:
     * PartyQuestHandler.shareMobKill(killer, mob, "killed a " + mobName);
     */
    public static void shareMobKill(ServerPlayerEntity killer, LivingEntity mob, String mobName) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerWorld world = (ServerWorld) killer.getEntityWorld();
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(killer, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party
        notifyPartyQuestProgress(killer, "killed " + mobName);

        // Update quest progress for all nearby party members
        for (ServerPlayerEntity member : nearbyMembers) {
            com.github.hitman20081.dagmod.quest.QuestManager.getInstance().updateQuestProgress(member);
        }
    }

    /**
     * Share item collection with party members
     * Call this when a player picks up a quest item
     *
     * Example usage:
     * PartyQuestHandler.shareItemCollection(player, itemStack, "found " + itemName);
     */
    public static void shareItemCollection(ServerPlayerEntity player, ItemStack item, String itemName) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party
        notifyPartyQuestProgress(player, "collected " + itemName);

        // Update quest progress for all nearby party members
        for (ServerPlayerEntity member : nearbyMembers) {
            com.github.hitman20081.dagmod.quest.QuestManager.getInstance().updateQuestProgress(member);
        }
    }

    /**
     * Award bonus XP to party members when a quest is completed
     * Call this from your quest completion code
     *
     * Example usage in QuestManager.completeQuest():
     * PartyQuestHandler.shareQuestCompletion(player, questName, xpReward);
     */
    public static void shareQuestCompletion(ServerPlayerEntity player, String questName, int xpReward) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Give bonus XP for party completion
        int partyBonusXp = (int) (xpReward * 0.1); // 10% bonus XP for party

        for (ServerPlayerEntity member : nearbyMembers) {
            // Award bonus XP
            com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, partyBonusXp);

            member.sendMessage(
                    Text.literal("+" + partyBonusXp + " Bonus XP for party quest completion!")
                            .formatted(Formatting.GOLD),
                    false
            );
        }

        // Notify party
        party.sendPartyMessage(world,
                Text.literal(player.getName().getString() + " completed: " + questName + "!")
                        .formatted(Formatting.GREEN)
        );
    }

    /**
     * Toggle quest sharing for a party
     */
    public static void toggleQuestSharing(ServerPlayerEntity leader) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return;
        }

        if (!party.isLeader(leader.getUuid())) {
            leader.sendMessage(Text.literal("Only the party leader can toggle quest sharing!").formatted(Formatting.RED), false);
            return;
        }

        party.setQuestShare(!party.isQuestShare());

        ServerWorld world = (ServerWorld) leader.getEntityWorld();
        String status = party.isQuestShare() ? "enabled" : "disabled";
        Formatting color = party.isQuestShare() ? Formatting.GREEN : Formatting.RED;

        party.sendPartyMessage(world,
                Text.literal("Quest sharing " + status + "!")
                        .formatted(color)
        );
    }
}