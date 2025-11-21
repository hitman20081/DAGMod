package com.github.hitman20081.dagmod.party;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    public PartyData createParty(ServerPlayerEntity leader) {
        UUID playerId = leader.getUuid();

        // Check if player is already in a party
        if (playerToParty.containsKey(playerId)) {
            leader.sendMessage(Text.literal("You are already in a party!").formatted(Formatting.RED), false);
            return null;
        }

        PartyData party = new PartyData(playerId);
        parties.put(party.getPartyId(), party);
        playerToParty.put(playerId, party.getPartyId());

        leader.sendMessage(Text.literal("Party created! You are the party leader.").formatted(Formatting.GREEN), false);

        return party;
    }

    // Party invitation
    public boolean invitePlayer(ServerPlayerEntity inviter, ServerPlayerEntity invitee) {
        UUID inviterId = inviter.getUuid();
        UUID inviteeId = invitee.getUuid();

        // Check if inviter is in a party
        UUID partyId = playerToParty.get(inviterId);
        if (partyId == null) {
            inviter.sendMessage(Text.literal("You are not in a party! Use /party create first.").formatted(Formatting.RED), false);
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        // Check if inviter is the leader
        if (!party.isLeader(inviterId)) {
            inviter.sendMessage(Text.literal("Only the party leader can invite players!").formatted(Formatting.RED), false);
            return false;
        }

        // Check if party is full
        if (party.isFull()) {
            inviter.sendMessage(Text.literal("Your party is full! (Max 5 players)").formatted(Formatting.RED), false);
            return false;
        }

        // Check if invitee is already in a party
        if (playerToParty.containsKey(inviteeId)) {
            inviter.sendMessage(Text.literal(invitee.getName().getString() + " is already in a party!").formatted(Formatting.RED), false);
            return false;
        }

        // Add to pending invites
        pendingInvites.computeIfAbsent(inviteeId, k -> new HashSet<>()).add(partyId);

        // Send messages
        inviter.sendMessage(Text.literal("Party invitation sent to " + invitee.getName().getString()).formatted(Formatting.GREEN), false);

        Text inviteMessage = Text.literal(inviter.getName().getString() + " has invited you to their party! ")
                .formatted(Formatting.AQUA)
                .append(Text.literal("[ACCEPT]")
                        .formatted(Formatting.GREEN, Formatting.UNDERLINE)
                        .append(Text.literal(" Use /party accept")));

        invitee.sendMessage(inviteMessage, false);

        return true;
    }

    // Accept party invitation
    public boolean acceptInvite(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();

        // Check if player has pending invites
        Set<UUID> invites = pendingInvites.get(playerId);
        if (invites == null || invites.isEmpty()) {
            player.sendMessage(Text.literal("You have no pending party invitations!").formatted(Formatting.RED), false);
            return false;
        }

        // Get the most recent invite
        UUID partyId = invites.iterator().next();
        PartyData party = parties.get(partyId);

        if (party == null) {
            player.sendMessage(Text.literal("That party no longer exists!").formatted(Formatting.RED), false);
            pendingInvites.remove(playerId);
            return false;
        }

        // Check if party is full
        if (party.isFull()) {
            player.sendMessage(Text.literal("That party is now full!").formatted(Formatting.RED), false);
            pendingInvites.remove(playerId);
            return false;
        }

        // Add player to party
        party.addMember(playerId);
        playerToParty.put(playerId, partyId);
        pendingInvites.remove(playerId);

        // Notify everyone
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        party.sendPartyMessage(world, Text.literal(player.getName().getString() + " has joined the party!").formatted(Formatting.GREEN));

        return true;
    }

    // Leave party
    public boolean leaveParty(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            player.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();

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

                    party.sendPartyMessage(world, Text.literal(player.getName().getString() + " has left the party!").formatted(Formatting.YELLOW));

                    ServerPlayerEntity newLeaderPlayer = world.getServer().getPlayerManager().getPlayer(newLeader);
                    if (newLeaderPlayer != null) {
                        party.sendPartyMessage(world, Text.literal(newLeaderPlayer.getName().getString() + " is now the party leader!").formatted(Formatting.GOLD));
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

            player.sendMessage(Text.literal("You have left the party.").formatted(Formatting.YELLOW), false);
            party.sendPartyMessage(world, Text.literal(player.getName().getString() + " has left the party!").formatted(Formatting.YELLOW));
        }

        return true;
    }

    // Kick player from party
    public boolean kickPlayer(ServerPlayerEntity leader, ServerPlayerEntity target) {
        UUID leaderId = leader.getUuid();
        UUID targetId = target.getUuid();
        UUID partyId = playerToParty.get(leaderId);

        if (partyId == null) {
            leader.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (!party.isLeader(leaderId)) {
            leader.sendMessage(Text.literal("Only the party leader can kick players!").formatted(Formatting.RED), false);
            return false;
        }

        if (!party.isMember(targetId)) {
            leader.sendMessage(Text.literal(target.getName().getString() + " is not in your party!").formatted(Formatting.RED), false);
            return false;
        }

        if (leaderId.equals(targetId)) {
            leader.sendMessage(Text.literal("You cannot kick yourself! Use /party disband or /party leave.").formatted(Formatting.RED), false);
            return false;
        }

        // Remove player
        party.removeMember(targetId);
        playerToParty.remove(targetId);

        ServerWorld world = (ServerWorld) leader.getEntityWorld();
        target.sendMessage(Text.literal("You have been kicked from the party!").formatted(Formatting.RED), false);
        party.sendPartyMessage(world, Text.literal(target.getName().getString() + " was kicked from the party!").formatted(Formatting.RED));

        return true;
    }

    // Disband party
    public boolean disbandParty(ServerPlayerEntity leader) {
        UUID leaderId = leader.getUuid();
        UUID partyId = playerToParty.get(leaderId);

        if (partyId == null) {
            leader.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        if (!party.isLeader(leaderId)) {
            leader.sendMessage(Text.literal("Only the party leader can disband the party!").formatted(Formatting.RED), false);
            return false;
        }

        ServerWorld world = (ServerWorld) leader.getEntityWorld();
        disbandParty(party, world);

        return true;
    }

    private void disbandParty(PartyData party, ServerWorld world) {
        party.sendPartyMessage(world, Text.literal("The party has been disbanded!").formatted(Formatting.RED));

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

    public PartyData getParty(ServerPlayerEntity player) {
        return getParty(player.getUuid());
    }

    // Check if player is in a party
    public boolean isInParty(UUID playerId) {
        return playerToParty.containsKey(playerId);
    }

    public boolean isInParty(ServerPlayerEntity player) {
        return isInParty(player.getUuid());
    }

    // Party chat
    public void sendPartyChat(ServerPlayerEntity sender, String message) {
        UUID playerId = sender.getUuid();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            sender.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return;
        }

        PartyData party = parties.get(partyId);
        if (party != null) {
            ServerWorld world = (ServerWorld) sender.getEntityWorld();
            party.sendPartyChat(world, sender, message);
        }
    }

    // List party members
    public void listPartyMembers(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        UUID partyId = playerToParty.get(playerId);

        if (partyId == null) {
            player.sendMessage(Text.literal("You are not in a party!").formatted(Formatting.RED), false);
            return;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return;
        }

        player.sendMessage(Text.literal("=== Party Members (" + party.getSize() + "/5) ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("XP Bonus: +" + party.getXpBonusPercentage() + "%").formatted(Formatting.AQUA), false);

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        for (UUID memberId : party.getMembers()) {
            ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
            if (member != null) {
                String prefix = party.isLeader(memberId) ? "★ " : "  ";
                String status = "Online";
                Formatting color = Formatting.GREEN;

                player.sendMessage(
                        Text.literal(prefix + member.getName().getString() + " - " + status)
                                .formatted(color),
                        false
                );
            }
        }
    }

    // Handle player disconnect
    public void handlePlayerDisconnect(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();

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

        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // Check if all members are offline
        boolean anyOnline = false;
        for (UUID memberId : party.getMembers()) {
            if (!memberId.equals(playerId)) {
                ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
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