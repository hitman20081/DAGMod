package com.github.hitman20081.dagmod.party;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all active parties on the server
 */
public class PartyManager {
    private static final PartyManager INSTANCE = new PartyManager();

    // Map of party ID to party data
    private final Map<UUID, PartyData> parties = new ConcurrentHashMap<>();

    // Map of player ID to party ID for quick lookup
    private final Map<UUID, UUID> playerToParty = new ConcurrentHashMap<>();

    // Map of player ID to pending party invites
    private final Map<UUID, Set<UUID>> pendingInvites = new ConcurrentHashMap<>();

    private PartyManager() {}

    public static PartyManager getInstance() {
        return INSTANCE;
    }

    // Party creation
    public PartyData createParty(ServerPlayer leader) {
        UUID playerId = leader.getUUID();

        // Check if player is already in a party
        if (playerToParty.containsKey(playerId)) {
            leader.sendSystemMessage(Component.literal("You are already in a party!").withStyle(ChatFormatting.RED));
            return null;
        }

        PartyData party = new PartyData(playerId);
        parties.put(party.getPartyId(), party);
        playerToParty.put(playerId, party.getPartyId());

        leader.sendSystemMessage(Component.literal("Party created! You are the party leader.").withStyle(ChatFormatting.GREEN));

        return party;
    }

    // Party invitation
    public boolean invitePlayer(ServerPlayer inviter, ServerPlayer invitee) {
        UUID inviterId = inviter.getUUID();
        UUID inviteeId = invitee.getUUID();

        // Check if inviter is in a party
        UUID partyId = playerToParty.get(inviterId);
        if (partyId == null) {
            inviter.sendSystemMessage(Component.literal("You are not in a party! Use /party create first.").withStyle(ChatFormatting.RED));
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        // Check if inviter is the leader
        if (!party.isLeader(inviterId)) {
            inviter.sendSystemMessage(Component.literal("Only the party leader can invite players!").withStyle(ChatFormatting.RED));
            return false;
        }

        // Check if party is full
        if (party.isFull()) {
            inviter.sendSystemMessage(Component.literal("Your party is full! (Max 5 players)").withStyle(ChatFormatting.RED));
            return false;
        }

        // Check if invitee is already in a party
        if (playerToParty.containsKey(inviteeId)) {
            inviter.sendSystemMessage(Component.literal(invitee.getName().getString() + " is already in a party!").withStyle(ChatFormatting.RED));
            return false;
        }

        // Add to pending invites
        pendingInvites.computeIfAbsent(inviteeId, k -> new HashSet<>()).add(partyId);

        // Send messages
        inviter.sendSystemMessage(Component.literal("Party invitation sent to " + invitee.getName().getString()).withStyle(ChatFormatting.GREEN));

        Component inviteMessage = Component.literal(inviter.getName().getString() + " has invited you to their party! ")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal("[ACCEPT]")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE)
                        .append(Component.literal(" Use /party accept")));

        invitee.sendSystemMessage(inviteMessage);

        return true;
    }

    // Accept party invitation
    public boolean acceptInvite(ServerPlayer player) {
        UUID playerId = player.getUUID();

        // Check if player has pending invites
        Set<UUID> invites = pendingInvites.get(playerId);
        if (invites == null || invites.isEmpty()) {
            player.sendSystemMessage(Component.literal("You have no pending party invitations!").withStyle(ChatFormatting.RED));
            return false;
        }

        // Get the most recent invite
        UUID partyId = invites.iterator().next();
        PartyData party = parties.get(partyId);

        if (party == null) {
            player.sendSystemMessage(Component.literal("That party no longer exists!").withStyle(ChatFormatting.RED));
            pendingInvites.remove(playerId);
            return false;
        }

        // Check if party is full
        if (party.isFull()) {
            player.sendSystemMessage(Component.literal("That party is now full!").withStyle(ChatFormatting.RED));
            pendingInvites.remove(playerId);
            return false;
        }

        // Add player to party
        party.addMember(playerId);
        playerToParty.put(playerId, partyId);
        pendingInvites.remove(playerId);

        // Notify everyone
        ServerLevel world = (ServerLevel) player.level();
        party.sendPartyMessage(world, Component.literal(player.getName().getString() + " has joined the party!").withStyle(ChatFormatting.GREEN));

        return true;
    }

    // Leave party
    public boolean leaveParty(ServerPlayer player) {
        UUID playerId = player.getUUID();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            player.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        ServerLevel world = (ServerLevel) player.level();

        // If player is the leader, transfer leadership or disband
        if (party.isLeader(playerId)) {
            List<UUID> members = party.getMembers();
            if (members.size() > 1) {
                // Transfer leadership to next member
                UUID newLeader = members.stream()
                        .filter(id -> !id.equals(playerId))
                        .findFirst()
                        .orElse(null);

                if (newLeader != null) {
                    party.setLeader(newLeader);
                    party.removeMember(playerId);
                    playerToParty.remove(playerId);

                    party.sendPartyMessage(world, Component.literal(player.getName().getString() + " has left the party!").withStyle(ChatFormatting.YELLOW));

                    ServerPlayer newLeaderPlayer = world.getServer().getPlayerList().getPlayer(newLeader);
                    if (newLeaderPlayer != null) {
                        party.sendPartyMessage(world, Component.literal(newLeaderPlayer.getName().getString() + " is now the party leader!").withStyle(ChatFormatting.GOLD));
                    }
                } else {
                    disbandParty(party, world);
                }
            } else {
                disbandParty(party, world);
            }
        } else {
            // Regular member leaving
            party.removeMember(playerId);
            playerToParty.remove(playerId);

            player.sendSystemMessage(Component.literal("You have left the party.").withStyle(ChatFormatting.YELLOW));
            party.sendPartyMessage(world, Component.literal(player.getName().getString() + " has left the party!").withStyle(ChatFormatting.YELLOW));
        }

        return true;
    }

    // Kick player from party
    public boolean kickPlayer(ServerPlayer leader, ServerPlayer target) {
        UUID leaderId = leader.getUUID();
        UUID targetId = target.getUUID();
        UUID partyId = playerToParty.get(leaderId);

        if (partyId == null) {
            leader.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (!party.isLeader(leaderId)) {
            leader.sendSystemMessage(Component.literal("Only the party leader can kick players!").withStyle(ChatFormatting.RED));
            return false;
        }

        if (!party.isMember(targetId)) {
            leader.sendSystemMessage(Component.literal(target.getName().getString() + " is not in your party!").withStyle(ChatFormatting.RED));
            return false;
        }

        if (leaderId.equals(targetId)) {
            leader.sendSystemMessage(Component.literal("You cannot kick yourself! Use /party disband or /party leave.").withStyle(ChatFormatting.RED));
            return false;
        }

        // Remove player
        party.removeMember(targetId);
        playerToParty.remove(targetId);

        ServerLevel world = (ServerLevel) leader.level();
        target.sendSystemMessage(Component.literal("You have been kicked from the party!").withStyle(ChatFormatting.RED));
        party.sendPartyMessage(world, Component.literal(target.getName().getString() + " was kicked from the party!").withStyle(ChatFormatting.RED));

        return true;
    }

    // Disband party
    public boolean disbandParty(ServerPlayer leader) {
        UUID leaderId = leader.getUUID();
        UUID partyId = playerToParty.get(leaderId);

        if (partyId == null) {
            leader.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (!party.isLeader(leaderId)) {
            leader.sendSystemMessage(Component.literal("Only the party leader can disband the party!").withStyle(ChatFormatting.RED));
            return false;
        }

        ServerLevel world = (ServerLevel) leader.level();
        disbandParty(party, world);

        return true;
    }

    private void disbandParty(PartyData party, ServerLevel world) {
        party.sendPartyMessage(world, Component.literal("The party has been disbanded!").withStyle(ChatFormatting.RED));

        // Remove all members from lookup
        for (UUID memberId : party.getMembers()) {
            playerToParty.remove(memberId);
        }

        // Remove party
        parties.remove(party.getPartyId());
    }

    // Get party for player
    public PartyData getParty(UUID playerId) {
        UUID partyId = playerToParty.get(playerId);
        return partyId != null ? parties.get(partyId) : null;
    }

    public PartyData getParty(ServerPlayer player) {
        return getParty(player.getUUID());
    }

    // Check if player is in a party
    public boolean isInParty(UUID playerId) {
        return playerToParty.containsKey(playerId);
    }

    public boolean isInParty(ServerPlayer player) {
        return isInParty(player.getUUID());
    }

    // Party chat
    public void sendPartyChat(ServerPlayer sender, String message) {
        UUID playerId = sender.getUUID();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            sender.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return;
        }

        PartyData party = parties.get(partyId);
        if (party != null) {
            ServerLevel world = (ServerLevel) sender.level();
            party.sendPartyChat(world, sender, message);
        }
    }

    // List party members
    public void listPartyMembers(ServerPlayer player) {
        UUID playerId = player.getUUID();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            player.sendSystemMessage(Component.literal("You are not in a party!").withStyle(ChatFormatting.RED));
            return;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return;
        }

        player.sendSystemMessage(Component.literal("=== Party Members (" + party.getSize() + "/5) ===").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("XP Bonus: +" + party.getXpBonusPercentage() + "%").withStyle(ChatFormatting.AQUA));

        ServerLevel world = (ServerLevel) player.level();
        for (UUID memberId : party.getMembers()) {
            ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                String prefix = party.isLeader(memberId) ? "★ " : "  ";
                String status = "Online";
                ChatFormatting color = ChatFormatting.GREEN;

                player.sendSystemMessage(
                        Component.literal(prefix + member.getName().getString() + " - " + status)
                                .withStyle(color));
            }
        }
    }

    // Handle player disconnect
    public void handlePlayerDisconnect(ServerPlayer player) {
        UUID playerId = player.getUUID();

        // Remove pending invites
        pendingInvites.remove(playerId);

        // Check if player is in a party
        UUID partyId = playerToParty.get(playerId);
        if (partyId == null) {
            return;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return;
        }

        ServerLevel world = (ServerLevel) player.level();

        // Check if all members are offline
        boolean anyOnline = false;
        for (UUID memberId : party.getMembers()) {
            if (!memberId.equals(playerId)) {
                ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
                if (member != null) {
                    anyOnline = true;
                    break;
                }
            }
        }

        if (!anyOnline) {
            // Disband party if no one else is online
            disbandParty(party, world);
        }
    }

    // Clean up empty or invalid parties
    public void cleanupParties() {
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, PartyData> entry : parties.entrySet()) {
            PartyData party = entry.getValue();
            if (party.getSize() == 0) {
                toRemove.add(entry.getKey());
            }
        }

        for (UUID partyId : toRemove) {
            parties.remove(partyId);
        }
    }

    // Get all parties (for admin/debug)
    public Collection<PartyData> getAllParties() {
        return new ArrayList<>(parties.values());
    }

    public int getPartyCount() {
        return parties.size();
    }
}