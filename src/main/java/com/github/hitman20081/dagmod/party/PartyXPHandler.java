package com.github.hitman20081.dagmod.party;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

/**
 * Handles XP sharing and bonuses for parties
 */
public class PartyXPHandler {

    /**
     * Award XP to a player and share with nearby party members
     *
     * @param player The player earning XP
     * @param baseXp The base XP amount before bonuses
     * @param source Description of XP source (for messages)
     */
    public static void awardPartyXP(ServerPlayer player, int baseXp, String source) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isXpShare()) {
            // No party or XP sharing disabled - award normal XP
            ProgressionManager.addXP(player, baseXp);
            return;
        }

        ServerLevel world = (ServerLevel) player.level();

        // Calculate XP with party bonus
        double partyBonus = party.getXpBonus();
        int bonusXp = (int) (baseXp * partyBonus);
        int totalXp = baseXp + bonusXp;

        // Award XP to the player who earned it
        ProgressionManager.addXP(player, totalXp);

        if (bonusXp > 0) {
            player.sendOverlayMessage(
                    Component.literal("+" + totalXp + " XP ")
                            .withStyle(ChatFormatting.GOLD)
                            .append(Component.literal("(Party Bonus: +" + bonusXp + ")")
                                    .withStyle(ChatFormatting.AQUA))
            );
        }

        // Share XP with nearby party members
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(player, world);

        if (!nearbyMembers.isEmpty()) {
            // Shared XP is 50% of the base XP (before party bonus)
            int sharedXp = baseXp / 2;

            for (ServerPlayer member : nearbyMembers) {
                // Each nearby member gets shared XP + their own party bonus
                int memberBonusXp = (int) (sharedXp * partyBonus);
                int memberTotalXp = sharedXp + memberBonusXp;

                ProgressionManager.addXP(member, memberTotalXp);

                member.sendOverlayMessage(
                        Component.literal("+" + memberTotalXp + " Shared XP ")
                                .withStyle(ChatFormatting.AQUA)
                                .append(Component.literal("from " + player.getName().getString())
                                        .withStyle(ChatFormatting.GRAY))
                );
            }
        }
    }

    /**
     * Get the XP multiplier for a player (includes party bonus)
     *
     * @param player The player
     * @return XP multiplier (1.0 = no bonus, 1.2 = +20% bonus)
     */
    public static double getXPMultiplier(ServerPlayer player) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null) {
            return 1.0;
        }

        return 1.0 + party.getXpBonus();
    }

    /**
     * Check if a player should receive shared XP from another player
     *
     * @param receiver The player who might receive XP
     * @param source The player who earned the XP
     * @return true if they're in the same party and close enough
     */
    public static boolean shouldShareXP(ServerPlayer receiver, ServerPlayer source) {
        PartyData party = PartyManager.getInstance().getParty(source);

        if (party == null || !party.isXpShare()) {
            return false;
        }

        if (!party.isMember(receiver.getUUID())) {
            return false;
        }

        if (receiver.level() != source.level()) {
            return false;
        }

        double distance = receiver.blockPosition().distSqr(source.blockPosition());
        return distance <= PartyData.XP_SHARE_RADIUS * PartyData.XP_SHARE_RADIUS;
    }
}