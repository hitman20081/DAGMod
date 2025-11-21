package com.github.hitman20081.dagmod.party;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    public static void awardPartyXP(ServerPlayerEntity player, int baseXp, String source) {
        PartyData party = PartyManager.getInstance().getParty(player);

        if (party == null || !party.isXpShare()) {
            // No party or XP sharing disabled - award normal XP
            ProgressionManager.addXP(player, baseXp);
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // Calculate XP with party bonus
        double partyBonus = party.getXpBonus();
        int bonusXp = (int) (baseXp * partyBonus);
        int totalXp = baseXp + bonusXp;

        // Award XP to the player who earned it
        ProgressionManager.addXP(player, totalXp);

        if (bonusXp > 0) {
            player.sendMessage(
                    Text.literal("+" + totalXp + " XP ")
                            .formatted(Formatting.GOLD)
                            .append(Text.literal("(Party Bonus: +" + bonusXp + ")")
                                    .formatted(Formatting.AQUA)),
                    true // Action bar
            );
        }

        // Share XP with nearby party members
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(player, world);

        if (!nearbyMembers.isEmpty()) {
            // Shared XP is 50% of the base XP (before party bonus)
            int sharedXp = baseXp / 2;

            for (ServerPlayerEntity member : nearbyMembers) {
                // Each nearby member gets shared XP + their own party bonus
                int memberBonusXp = (int) (sharedXp * partyBonus);
                int memberTotalXp = sharedXp + memberBonusXp;

                ProgressionManager.addXP(member, memberTotalXp);

                member.sendMessage(
                        Text.literal("+" + memberTotalXp + " Shared XP ")
                                .formatted(Formatting.AQUA)
                                .append(Text.literal("from " + player.getName().getString())
                                        .formatted(Formatting.GRAY)),
                        true // Action bar
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
    public static double getXPMultiplier(ServerPlayerEntity player) {
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
    public static boolean shouldShareXP(ServerPlayerEntity receiver, ServerPlayerEntity source) {
        PartyData party = PartyManager.getInstance().getParty(source);

        if (party == null || !party.isXpShare()) {
            return false;
        }

        if (!party.isMember(receiver.getUuid())) {
            return false;
        }

        if (receiver.getEntityWorld() != source.getEntityWorld()) {
            return false;
        }

        double distance = receiver.getBlockPos().getSquaredDistance(source.getBlockPos());
        return distance <= PartyData.XP_SHARE_RADIUS * PartyData.XP_SHARE_RADIUS;
    }
}