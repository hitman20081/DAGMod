package com.github.hitman20081.dagmod.party;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores data for a party (temporary group of players)
 */
public class PartyData {
    private final UUID partyId;
    private UUID leaderId;
    private final List<UUID> members;
    private final long createdTime;
    private boolean xpShare;
    private boolean questShare;

    public static final int MAX_PARTY_SIZE = 5;
    public static final double XP_SHARE_RADIUS = 50.0;

    // XP bonuses based on party size
    private static final double[] XP_BONUSES = {
            0.0,   // 1 player (no bonus)
            0.05,  // 2 players (+5%)
            0.10,  // 3 players (+10%)
            0.15,  // 4 players (+15%)
            0.20   // 5 players (+20%)
    };

    public PartyData(UUID leaderId) {
        this.partyId = UUID.randomUUID();
        this.leaderId = leaderId;
        this.members = new ArrayList<>();
        this.members.add(leaderId);
        this.createdTime = System.currentTimeMillis();
        this.xpShare = true;
        this.questShare = true;
    }

    // Constructor for loading from NBT
    private PartyData(UUID partyId, UUID leaderId, List<UUID> members, long createdTime, boolean xpShare, boolean questShare) {
        this.partyId = partyId;
        this.leaderId = leaderId;
        this.members = members;
        this.createdTime = createdTime;
        this.xpShare = xpShare;
        this.questShare = questShare;
    }

    // Getters
    public UUID getPartyId() {
        return partyId;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    public int getSize() {
        return members.size();
    }

    public boolean isXpShare() {
        return xpShare;
    }

    public boolean isQuestShare() {
        return questShare;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    // Setters
    public void setLeader(UUID newLeaderId) {
        if (members.contains(newLeaderId)) {
            this.leaderId = newLeaderId;
        }
    }

    public void setXpShare(boolean xpShare) {
        this.xpShare = xpShare;
    }

    public void setQuestShare(boolean questShare) {
        this.questShare = questShare;
    }

    // Member management
    public boolean addMember(UUID playerId) {
        if (members.size() >= MAX_PARTY_SIZE) {
            return false;
        }
        if (members.contains(playerId)) {
            return false;
        }
        return members.add(playerId);
    }

    public boolean removeMember(UUID playerId) {
        return members.remove(playerId);
    }

    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    public boolean isLeader(UUID playerId) {
        return leaderId.equals(playerId);
    }

    public boolean isFull() {
        return members.size() >= MAX_PARTY_SIZE;
    }

    // XP bonus calculation
    public double getXpBonus() {
        int size = members.size();
        if (size <= 0 || size > XP_BONUSES.length) {
            return 0.0;
        }
        return XP_BONUSES[size - 1];
    }

    public int getXpBonusPercentage() {
        return (int) (getXpBonus() * 100);
    }

    // Get nearby party members (within XP share radius)
    public List<ServerPlayerEntity> getNearbyMembers(ServerPlayerEntity player, ServerWorld world) {
        List<ServerPlayerEntity> nearbyMembers = new ArrayList<>();

        for (UUID memberId : members) {
            if (memberId.equals(player.getUuid())) {
                continue; // Skip self
            }

            ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
            if (member != null && member.getEntityWorld() == player.getEntityWorld()) {
                double distance = player.getBlockPos().getSquaredDistance(member.getBlockPos());
                if (distance <= XP_SHARE_RADIUS * XP_SHARE_RADIUS) {
                    nearbyMembers.add(member);
                }
            }
        }

        return nearbyMembers;
    }

    // Get all online members
    public List<ServerPlayerEntity> getOnlineMembers(ServerWorld world) {
        List<ServerPlayerEntity> onlineMembers = new ArrayList<>();

        for (UUID memberId : members) {
            ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
            if (member != null) {
                onlineMembers.add(member);
            }
        }

        return onlineMembers;
    }

    // Send message to all party members
    public void sendPartyMessage(ServerWorld world, Text message) {
        Text formattedMessage = Text.literal("[PARTY] ").formatted(Formatting.AQUA)
                .append(message);

        for (UUID memberId : members) {
            ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
            if (member != null) {
                member.sendMessage(formattedMessage, false);
            }
        }
    }

    // Send message to all party members (with sender name)
    public void sendPartyChat(ServerWorld world, ServerPlayerEntity sender, String message) {
        Text formattedMessage = Text.literal("[PARTY] ")
                .formatted(Formatting.AQUA)
                .append(Text.literal(sender.getName().getString() + ": ")
                        .formatted(Formatting.WHITE))
                .append(Text.literal(message)
                        .formatted(Formatting.GRAY));

        for (UUID memberId : members) {
            ServerPlayerEntity member = world.getServer().getPlayerManager().getPlayer(memberId);
            if (member != null) {
                member.sendMessage(formattedMessage, false);
            }
        }
    }

    // NBT Serialization
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putString("PartyId", partyId.toString());
        nbt.putString("LeaderId", leaderId.toString());
        nbt.putLong("CreatedTime", createdTime);
        nbt.putBoolean("XpShare", xpShare);
        nbt.putBoolean("QuestShare", questShare);

        NbtList membersList = new NbtList();
        for (UUID memberId : members) {
            NbtCompound memberNbt = new NbtCompound();
            memberNbt.putString("MemberId", memberId.toString());
            membersList.add(memberNbt);
        }
        nbt.put("Members", membersList);

        return nbt;
    }

    // NBT Deserialization
    public static PartyData fromNbt(NbtCompound nbt) {
        UUID partyId = UUID.fromString(nbt.getString("PartyId").orElse(""));
        UUID leaderId = UUID.fromString(nbt.getString("LeaderId").orElse(""));
        long createdTime = nbt.getLong("CreatedTime").orElse(Long.valueOf(System.currentTimeMillis()));
        boolean xpShare = nbt.getBoolean("XpShare").orElse(Boolean.valueOf(true));
        boolean questShare = nbt.getBoolean("QuestShare").orElse(Boolean.valueOf(true));

        List<UUID> members = new ArrayList<>();
        NbtList membersList = nbt.getList("Members").orElse(new NbtList());
        for (int i = 0; i < membersList.size(); i++) {
            NbtCompound memberNbt = membersList.getCompound(i).orElse(null);
            if (memberNbt != null) {
                String memberIdStr = memberNbt.getString("MemberId").orElse("");
                if (!memberIdStr.isEmpty()) {
                    members.add(UUID.fromString(memberIdStr));
                }
            }
        }

        return new PartyData(partyId, leaderId, members, createdTime, xpShare, questShare);
    }

    @Override
    public String toString() {
        return "Party{" +
                "id=" + partyId +
                ", leader=" + leaderId +
                ", size=" + members.size() +
                ", xpBonus=" + getXpBonusPercentage() + "%" +
                '}';
    }
}