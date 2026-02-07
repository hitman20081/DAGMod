package com.github.hitman20081.dagmod.party;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

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
        if (entity instanceof EnderDragonEntity || entity instanceof WitherEntity) {
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
    public static void distributeBossLoot(ServerWorld world, ServerPlayerEntity killer, double x, double y, double z) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null) {
            // No party - normal loot drop
            return;
        }

        // Get all nearby party members (within 50 blocks)
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(killer, world);

        if (nearbyMembers.isEmpty()) {
            // Only the killer is nearby - normal loot
            return;
        }

        // Find all dropped items near the boss death location (within 10 blocks)
        Box searchBox = new Box(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10);
        List<ItemEntity> droppedItems = world.getEntitiesByClass(
                ItemEntity.class,
                searchBox,
                itemEntity -> !itemEntity.isRemoved()
        );

        if (droppedItems.isEmpty()) {
            return;
        }

        // Give copies of the loot to each party member
        int totalItemsGiven = 0;
        for (ServerPlayerEntity member : nearbyMembers) {
            if (member.equals(killer)) continue; // Killer already got normal drops

            for (ItemEntity itemEntity : droppedItems) {
                ItemStack originalStack = itemEntity.getStack();
                ItemStack copyStack = originalStack.copy();

                // Try to add to inventory
                if (!member.getInventory().insertStack(copyStack)) {
                    // Inventory full - drop at member's feet
                    ItemEntity droppedCopy = new ItemEntity(
                            world,
                            member.getX(),
                            member.getY(),
                            member.getZ(),
                            copyStack
                    );
                    world.spawnEntity(droppedCopy);
                }

                totalItemsGiven++;
            }

            // Notify member
            member.sendMessage(
                    Text.literal("Received " + droppedItems.size() + " items from party boss loot!")
                            .formatted(Formatting.GREEN),
                    false
            );
        }

        // Notify party
        if (totalItemsGiven > 0) {
            party.sendPartyMessage(world,
                    Text.literal("Boss loot distributed to " + (nearbyMembers.size() + 1) + " party members!")
                            .formatted(Formatting.GOLD)
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
    public static void awardBossKillXP(LivingEntity boss, ServerPlayerEntity killer, int baseXp) {
        PartyData party = PartyManager.getInstance().getParty(killer);

        if (party == null) {
            // No party - use normal XP handler
            PartyXPHandler.awardPartyXP(killer, baseXp, "Boss Kill");
            return;
        }

        ServerWorld world = (ServerWorld) killer.getEntityWorld();
        List<ServerPlayerEntity> nearbyMembers = party.getNearbyMembers(killer, world);

        // Boss kills give bonus XP to everyone
        int bonusMultiplier = 2; // Bosses give 2x normal party XP
        int partyBonus = (int) (baseXp * party.getXpBonus() * bonusMultiplier);
        int totalXp = baseXp + partyBonus;

        // Award XP to killer
        com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(killer, totalXp);
        killer.sendMessage(
                Text.literal("+" + totalXp + " XP ")
                        .formatted(Formatting.GOLD)
                        .append(Text.literal("(Boss Kill + Party Bonus!)")
                                .formatted(Formatting.AQUA)),
                true
        );

        // Award shared XP to nearby party members
        if (!nearbyMembers.isEmpty()) {
            int sharedXp = (int) (totalXp * 0.75); // 75% of total XP for party members

            for (ServerPlayerEntity member : nearbyMembers) {
                if (member.equals(killer)) continue; // Skip killer (already got XP)

                com.github.hitman20081.dagmod.progression.ProgressionManager.addXP(member, sharedXp);
                member.sendMessage(
                        Text.literal("+" + sharedXp + " Shared Boss XP!")
                                .formatted(Formatting.GOLD),
                        true
                );
            }
        }

        // Send party notification
        String bossName = boss.getName().getString();
        party.sendPartyMessage(world,
                Text.literal(killer.getName().getString() + " defeated " + bossName + "!")
                        .formatted(Formatting.GOLD)
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