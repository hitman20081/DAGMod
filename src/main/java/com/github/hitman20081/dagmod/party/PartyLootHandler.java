package com.github.hitman20081.dagmod.party;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Handles loot distribution for parties when killing bosses
 * Simplified version that duplicates dropped items for party members
 */
public class PartyLootHandler {

    /**
     * Check if an entity is considered a "boss" for party loot purposes
     */
    public static boolean isBossEntity(LivingEntity entity) {
        // Check for vanilla bosses
        if (entity instanceof EnderDragon || entity instanceof WitherBoss) {
            return true;
        }

        // Check for custom DAGMod bosses by class name
        String className = entity.getClass().getSimpleName();
        return className.contains("SkeletonKing") ||
                className.contains("SkeletonLord") ||
                className.contains("Boss");
    }

    /**
     * Distribute boss loot to all nearby party members
     * Call this AFTER the boss has dropped its loot naturally
     *
     * @param bossPos The position where the boss died
     * @param killer The player who got the killing blow
     * @param world The server world
     */
    public static void distributeBossLoot(ServerLevel world, ServerPlayer killer, double x, double y, double z) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null) {
            // No party - normal loot drop
            return;
        }

        // Get all nearby party members (within 50 blocks)
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(killer, world);

        if (nearbyMembers.isEmpty()) {
            // Only the killer is nearby - normal loot
            return;
        }

        // Find all dropped items near the boss death location (within 10 blocks)
        AABB searchBox = new AABB(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10);
        List<ItemEntity> droppedItems = world.getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                itemEntity -> !itemEntity.isRemoved()
        );

        if (droppedItems.isEmpty()) {
            return;
        }

        // Give copies of the loot to each party member
        int totalItemsGiven = 0;
        for (ServerPlayer member : nearbyMembers) {
            if (member.equals(killer)) continue; // Killer already got normal drops

            for (ItemEntity itemEntity : droppedItems) {
                ItemStack originalStack = itemEntity.getItem();
                ItemStack copyStack = originalStack.copy();

                // Try to add to inventory
                if (!member.getInventory().add(copyStack)) {
                    // Inventory full - drop at member's feet
                    ItemEntity droppedCopy = new ItemEntity(
                            world,
                            member.getX(),
                            member.getY(),
                            member.getZ(),
                            copyStack
                    );
                    world.addFreshEntity(droppedCopy);
                }

                totalItemsGiven++;
            }

            // Notify member
            member.sendSystemMessage(
                    Component.literal("Received " + droppedItems.size() + " items from party boss loot!")
                            .withStyle(ChatFormatting.GREEN));
        }

        // Notify party
        if (totalItemsGiven > 0) {
            party.sendPartyMessage(world,
                    Component.literal("Boss loot distributed to " + (nearbyMembers.size() + 1) + " party members!")
                            .withStyle(ChatFormatting.GOLD)
            );
        }
    }

    /**
     * Award party-wide XP bonus for boss kills
     *
     * @param boss The boss that was killed
     * @param killer The player who killed the boss
     * @param baseXp Base XP reward for the boss
     */
    public static void awardBossKillXP(LivingEntity boss, ServerPlayer killer, int baseXp) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null) {
            // No party - use normal XP handler
            PartyXPHandler.awardPartyXP(killer, baseXp, "Boss Kill");
            return;
        }

        ServerLevel world = (ServerLevel) killer.level();
        List<ServerPlayer> nearbyMembers = party.getNearbyMembers(killer, world);

        // Boss kills give bonus XP to everyone
        int bonusMultiplier = 2; // Bosses give 2x normal party XP
        int partyBonus = (int) (baseXp * party.getXpBonus() * bonusMultiplier);
        int totalXp = baseXp + partyBonus;

        // Award XP to killer
        com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(killer, totalXp);
        killer.sendOverlayMessage(
                Component.literal("+" + totalXp + " XP ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("(Boss Kill + Party Bonus!)")
                                .withStyle(ChatFormatting.AQUA)));

        // Award shared XP to nearby party members
        if (!nearbyMembers.isEmpty()) {
            int sharedXp = (int) (totalXp * 0.75); // 75% of total XP for party members

            for (ServerPlayer member : nearbyMembers) {
                if (member.equals(killer)) continue; // Skip killer (already got XP)

                com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, sharedXp);
                member.sendOverlayMessage(
                        Component.literal("+" + sharedXp + " Shared Boss XP!")
                                .withStyle(ChatFormatting.GOLD));
            }
        }

        // Send party notification
        String bossName = boss.getName().getString();
        party.sendPartyMessage(world,
                Component.literal(killer.getName().getString() + " defeated " + bossName + "!")
                        .withStyle(ChatFormatting.GOLD)
        );
    }

    /**
     * Calculate bonus loot chance for party size
     * Larger parties have slightly better loot chances
     */
    public static float getPartyLootBonus(PartyData party) {
        if (party == null) return 1.0f;

        int size = party.getSize();
        switch (size) {
            case 2: return 1.05f; // +5% better loot
            case 3: return 1.10f; // +10%
            case 4: return 1.15f; // +15%
            case 5: return 1.20f; // +20%
            default: return 1.0f;
        }
    }
}