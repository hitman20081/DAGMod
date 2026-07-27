package com.github.hitman20081.dagmod.party;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
    public static void notifyPartyQuestProgress(ServerPlayer player, String progressMessage) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerLevel world = (ServerLevel) player.level();
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party members
        Component message = Component.literal(player.getName().getString() + " ")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(progressMessage)
                        .withStyle(ChatFormatting.GRAY));

        for (ServerPlayer member : nearbyMembers) {
            member.sendOverlayMessage(message);
        }
    }

    /**
     * Share mob kill credit with party members
     * Call this from your mob kill handler
     *
     * Example usage in your death event:
     * PartyQuestHandler.shareMobKill(killer, mob, "killed a " + mobName);
     */
    public static void shareMobKill(ServerPlayer killer, LivingEntity mob, String mobName) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerLevel world = (ServerLevel) killer.level();
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(killer, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party
        notifyPartyQuestProgress(killer, "killed " + mobName);

        // Update quest progress for all nearby party members
        for (ServerPlayer member : nearbyMembers) {
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
    public static void shareItemCollection(ServerPlayer player, ItemStack item, String itemName) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerLevel world = (ServerLevel) player.level();
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Notify party
        notifyPartyQuestProgress(player, "collected " + itemName);

        // Update quest progress for all nearby party members
        for (ServerPlayer member : nearbyMembers) {
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
    public static void shareQuestCompletion(ServerPlayer player, String questName, int xpReward) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isQuestShare()) {
            return;
        }

        ServerLevel world = (ServerLevel) player.level();
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(player, world);

        if (nearbyMembers.isEmpty()) {
            return;
        }

        // Give bonus XP for party completion
        int partyBonusXp = (int) (xpReward * 0.1); // 10% bonus XP for party

        for (ServerPlayer member : nearbyMembers) {
            // Award bonus XP
            com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, partyBonusXp);

            member.sendSystemMessage(
                    Component.literal("+" + partyBonusXp + " Bonus XP for party quest completion!")
                            .withStyle(ChatFormatting.GOLD));
        }

        // Notify party
        party.sendPartyMessage(world,
                Component.literal(player.getName().getString() + " completed: " + questName + "!")
                        .withStyle(ChatFormatting.GREEN)
        );
    }

    /**
     * Toggle quest sharing for a party
     */
    public static void toggleQuestSharing(ServerPlayer leader) {
        PartyData party = PartyManager.getInstance().getParty(leader);

        if (party == null) {
            leader.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return;
        }

        if (!party.isLeader(leader.getUUID())) {
            leader.sendSystemMessage(Component.literal("Only the party leader can toggle quest sharing!").withStyle(ChatFormatting.RED));
            return;
        }

        party.setQuestShare(!party.isQuestShare());

        ServerLevel world = (ServerLevel) leader.level();
        String status = party.isQuestShare() ? "enabled" : "disabled";
        ChatFormatting color = party.isQuestShare() ? ChatFormatting.GREEN : ChatFormatting.RED;

        party.sendPartyMessage(world,
                Component.literal("Quest sharing " + status + "!")
                        .withStyle(color)
        );
    }
}