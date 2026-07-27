package com.github.hitman20081.dagmod.travel;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.quest.QuestManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShipTravelManager {

    // Define destinations with their requirements
    public static class Destination {
        public final String name;
        public final String dimensionId;
        public final double x, y, z;
        public final String requiredQuest; // null if no quest required
        public final String description;

        public Destination(String name, String dimensionId, double x, double y, double z,
                           String requiredQuest, String description) {
            this.name = name;
            this.dimensionId = dimensionId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.requiredQuest = requiredQuest;
            this.description = description;
        }
    }

    private static final Map<String, Destination> DESTINATIONS = new HashMap<>();

    static {
        // Register your destinations here
        DESTINATIONS.put("bleakwind", new Destination(
                "Bleakwind Isles",
                "dagmod:bleakwind",
                -426.5, 69, 161, // Spawn coordinates in Bleakwind
                "discover_bleakwind", // Quest ID required to unlock
                "A harsh, windswept archipelago shrouded in mystery..."
        ));

        DESTINATIONS.put("overworld", new Destination(
                "The Overworld",
                "minecraft:overworld",
                0, 64, 0,
                null, // Always available
                "Return to the familiar lands..."
        ));

        // Add more destinations as needed
    }

    /**
     * Check if player can access a destination
     */
    public static boolean canAccessDestination(ServerPlayer player, String destinationId) {
        Destination dest = DESTINATIONS.get(destinationId);
        if (dest == null) return false;

        // If no quest required, always accessible
        if (dest.requiredQuest == null) return true;

        // Check if player has completed the required quest
        // You'll need to implement this method in QuestManager or adjust to your existing method
        return checkQuestCompleted(player, dest.requiredQuest);
    }

    /**
     * Helper method to check quest completion - adjust based on your QuestManager API
     */
    private static boolean checkQuestCompleted(ServerPlayer player, String questId) {
        // Replace this with your actual quest checking logic
        // Example possibilities:
        // return QuestManager.getInstance().isQuestCompleted(player, questId);
        // or check player data directly
        return true; // Temporary - always allow for testing
    }

    /**
     * Get all destinations accessible to this player
     */
    public static Map<String, Destination> getAccessibleDestinations(ServerPlayer player) {
        Map<String, Destination> accessible = new HashMap<>();

        for (Map.Entry<String, Destination> entry : DESTINATIONS.entrySet()) {
            if (canAccessDestination(player, entry.getKey())) {
                accessible.put(entry.getKey(), entry.getValue());
            }
        }

        return accessible;
    }

    /**
     * Teleport player to destination with ship travel effects
     */
    public static void travelToDestination(ServerPlayer player, String destinationId) {
        Destination dest = DESTINATIONS.get(destinationId);

        if (dest == null) {
            player.sendSystemMessage(Component.literal("Unknown destination!").withStyle(ChatFormatting.RED));
            return;
        }

        if (!canAccessDestination(player, destinationId)) {
            player.sendSystemMessage(
                    Component.literal("The captain shakes his head. 'That route hasn't been charted yet...'")
                            .withStyle(ChatFormatting.GRAY));
            return;
        }

        // Get the destination world
        String[] parts = dest.dimensionId.split(":");
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION,
                Identifier.fromNamespaceAndPath(parts[0], parts[1]));
        ServerLevel destinationWorld = player.level().getServer().getLevel(worldKey);

        if (destinationWorld == null) {
            player.sendSystemMessage(Component.literal("Destination world not found!").withStyle(ChatFormatting.RED));
            return;
        }

        // Send departure message
        player.sendSystemMessage(
                Component.literal("════════════════════════════════")
                        .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(
                Component.literal("⛵ The ship sets sail for " + dest.name + "...")
                        .withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(
                Component.literal("════════════════════════════════")
                        .withStyle(ChatFormatting.AQUA));

        // Teleport with a slight delay for immersion
        player.level().getServer().execute(() -> {
            // Use the correct teleport method signature
            player.teleportTo(
                    destinationWorld,
                    dest.x,
                    dest.y,
                    dest.z,
                    Set.of(), // PositionFlags - empty set means no relative positioning
                    0.0f,     // yaw
                    0.0f,     // pitch
                    true      // spawn loading screen
            );

            // Send arrival message
            player.sendSystemMessage(
                    Component.literal("You have arrived at " + dest.name + "!")
                            .withStyle(ChatFormatting.GREEN));
        });
    }

    public static Destination getDestination(String id) {
        return DESTINATIONS.get(id);
    }
}