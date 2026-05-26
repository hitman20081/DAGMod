package com.github.hitman20081.dagmod.party;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
    public List<ServerPlayer> getNearbyMembers(ServerPlayer player, ServerLevel world) {
        List<ServerPlayer> nearbyMembers = new ArrayList<>();

        for (UUID memberId : members) {
            if (memberId.equals(player.getUUID())) {
                continue; // Skip self
            }

            ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && member.level() == player.level()) {
                double distance = player.blockPosition().distSqr(member.blockPosition());
                if (distance <= XP_SHARE_RADIUS * XP_SHARE_RADIUS) {
                    nearbyMembers.add(member);
                }
            }
        }

        return nearbyMembers;
    }

    // Get all online members
    public List<ServerPlayer> getOnlineMembers(ServerLevel world) {
        List<ServerPlayer> onlineMembers = new ArrayList<>();

        for (UUID memberId : members) {
            ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                onlineMembers.add(member);
            }
        }

        return onlineMembers;
    }

    // Send message to all party members
    public void sendPartyMessage(ServerLevel world, Component message) {
        Component formattedMessage = Component.literal("[PARTY] ").withStyle(ChatFormatting.AQUA)
                .append(message);

        for (UUID memberId : members) {
            ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                member.sendSystemMessage(formattedMessage);
            }
        }
    }

    // Send message to all party members (with sender name)
    public void sendPartyChat(ServerLevel world, ServerPlayer sender, String message) {
        Component formattedMessage = Component.literal("[PARTY] ")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(sender.getName().getString() + ": ")
                        .withStyle(ChatFormatting.WHITE))
                .append(Component.literal(message)
                        .withStyle(ChatFormatting.GRAY));

        for (UUID memberId : members) {
            ServerPlayer member = world.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                member.sendSystemMessage(formattedMessage);
            }
        }
    }

    // NBT Serialization
    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("PartyId", partyId.toString());
        nbt.putString("LeaderId", leaderId.toString());
        nbt.putLong("CreatedTime", createdTime);
        nbt.putBoolean("XpShare", xpShare);
        nbt.putBoolean("QuestShare", questShare);

        ListTag membersList = new ListTag();
        for (UUID memberId : members) {
            CompoundTag memberNbt = new CompoundTag();
            memberNbt.putString("MemberId", memberId.toString());
            membersList.add(memberNbt);
        }
        nbt.put("Members", membersList);

        return nbt;
    }

    // NBT Deserialization
    public static PartyData fromNbt(CompoundTag nbt) {
        UUID partyId = UUID.fromString(nbt.getString("PartyId").orElse(""));
        UUID leaderId = UUID.fromString(nbt.getString("LeaderId").orElse(""));
        long createdTime = nbt.getLong("CreatedTime").orElse(Long.valueOf(System.currentTimeMillis()));
        boolean xpShare = nbt.getBoolean("XpShare").orElse(Boolean.valueOf(true));
        boolean questShare = nbt.getBoolean("QuestShare").orElse(Boolean.valueOf(true));

        List<UUID> members = new ArrayList<>();
        ListTag membersList = nbt.getList("Members").orElse(new ListTag());
        for (int i = 0; i < membersList.size(); i++) {
            CompoundTag memberNbt = membersList.getCompound(i).orElse(null);
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