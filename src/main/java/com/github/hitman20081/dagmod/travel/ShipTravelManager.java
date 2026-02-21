package com.github.hitman20081.dagmod.travel;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.TeleportTarget;
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
    public static boolean canAccessDestination(ServerPlayerEntity player, String destinationId) {
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
    private static boolean checkQuestCompleted(ServerPlayerEntity player, String questId) {
        // Replace this with your actual quest checking logic
        // Example possibilities:
        // return QuestManager.getInstance().isQuestCompleted(player, questId);
        // or check player data directly
        return true; // Temporary - always allow for testing
    }

    /**
     * Get all destinations accessible to this player
     */
    public static Map<String, Destination> getAccessibleDestinations(ServerPlayerEntity player) {
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
    public static void travelToDestination(ServerPlayerEntity player, String destinationId) {
        Destination dest = DESTINATIONS.get(destinationId);

        if (dest == null) {
            player.sendMessage(Text.literal("Unknown destination!").formatted(Formatting.RED), false);
            return;
        }

        if (!canAccessDestination(player, destinationId)) {
            player.sendMessage(
                    Text.literal("The captain shakes his head. 'That route hasn't been charted yet...'")
                            .formatted(Formatting.GRAY),
                    false
            );
            return;
        }

        // Get the destination world
        String[] parts = dest.dimensionId.split(":");
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD,
                Identifier.of(parts[0], parts[1]));
        ServerWorld destinationWorld = player.getEntityWorld().getServer().getWorld(worldKey);

        if (destinationWorld == null) {
            player.sendMessage(Text.literal("Destination world not found!").formatted(Formatting.RED), false);
            return;
        }

        // Send departure message
        player.sendMessage(
                Text.literal("════════════════════════════════")
                        .formatted(Formatting.AQUA),
                false
        );
        player.sendMessage(
                Text.literal("⛵ The ship sets sail for " + dest.name + "...")
                        .formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
                false
        );
        player.sendMessage(
                Text.literal("════════════════════════════════")
                        .formatted(Formatting.AQUA),
                false
        );

        // Teleport with a slight delay for immersion
        player.getEntityWorld().getServer().execute(() -> {
            // Use the correct teleport method signature
            player.teleport(
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
            player.sendMessage(
                    Text.literal("You have arrived at " + dest.name + "!")
                            .formatted(Formatting.GREEN),
                    false
            );
        });
    }

    public static Destination getDestination(String id) {
        return DESTINATIONS.get(id);
    }
}