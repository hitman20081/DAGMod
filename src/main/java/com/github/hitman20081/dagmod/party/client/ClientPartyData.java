package com.github.hitman20081.dagmod.party.client;

import com.github.hitman20081.dagmod.party.PartyData;
import com.github.hitman20081.dagmod.party.client.PartyHUD.PartyMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Client-side storage for party data
 */
public class ClientPartyData {
    private static PartyData localPartyData = null;
    private static List<PartyMemberInfo> partyMembers = new ArrayList<>();

    /**
     * Get the local player's party data
     */
    public static PartyData getLocalPartyData() {
        return localPartyData;
    }

    /**
     * Set the local player's party data
     */
    public static void setLocalPartyData(PartyData partyData) {
        localPartyData = partyData;
    }

    /**
     * Clear the local party data
     */
    public static void clearLocalPartyData() {
        localPartyData = null;
        partyMembers.clear();
    }

    /**
     * Get the list of party members
     */
    public static List<PartyMemberInfo> getPartyMembers() {
        return new ArrayList<>(partyMembers);
    }

    /**
     * Update a party member's information
     */
    public static void updatePartyMember(UUID uuid, String name, float health, float maxHealth, boolean online) {
        // Remove old entry if exists
        partyMembers.removeIf(member -> member.uuid.equals(uuid));

        // Add new entry
        partyMembers.add(new PartyMemberInfo(uuid, name, health, maxHealth, online));
    }

    /**
     * Remove a party member
     */
    public static void removePartyMember(UUID uuid) {
        partyMembers.removeIf(member -> member.uuid.equals(uuid));
    }

    /**
     * Set all party members at once
     */
    public static void setPartyMembers(List<PartyMemberInfo> members) {
        partyMembers = new ArrayList<>(members);
    }

    /**
     * Clear all party members
     */
    public static void clearPartyMembers() {
        partyMembers.clear();
    }
}