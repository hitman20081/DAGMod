package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.List;

/**
 * Command to locate Dragon Guardians
 * Shows currently spawned dragons and helps find valid spawn locations
 */
public class LocateDragonCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("locatedragon")
                .requires(source -> source.getPermissions().hasPermission(new net.minecraft.command.permission.Permission.Level(net.minecraft.command.permission.PermissionLevel.GAMEMASTERS)))
                .executes(LocateDragonCommand::findNearestDragon));
    }

    private static int findNearestDragon(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        ServerWorld world = (ServerWorld) source.getWorld();
        BlockPos playerPos = player.getBlockPos();

        // Find all dragons in the world
        List<DragonGuardianEntity> dragons = world.getEntitiesByClass(
                DragonGuardianEntity.class,
                player.getBoundingBox().expand(500), // Search 500 block radius
                dragon -> true
        );

        if (dragons.isEmpty()) {
            // No dragons found - check if player is in valid spawn location
            boolean isValidBiome = world.getBiome(playerPos).isIn(BiomeTags.IS_MOUNTAIN);
            int surfaceY = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, playerPos).getY();
            boolean isValidHeight = surfaceY >= 160;

            player.sendMessage(Text.literal("═══════════════════════════════════").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("🐉 Dragon Locator").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("No dragons found within 500 blocks").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal(""), false);

            // Show current location info
            player.sendMessage(Text.literal("Current Location:").formatted(Formatting.AQUA, Formatting.BOLD), false);
            player.sendMessage(Text.literal("  Position: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(String.format("X: %d, Y: %d, Z: %d", playerPos.getX(), playerPos.getY(), playerPos.getZ()))
                            .formatted(Formatting.WHITE)), false);
            player.sendMessage(Text.literal("  Biome: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(world.getBiome(playerPos).getKey().get().getValue().getPath())
                            .formatted(isValidBiome ? Formatting.GREEN : Formatting.RED)), false);
            player.sendMessage(Text.literal("  Surface Height: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal("Y: " + surfaceY)
                            .formatted(isValidHeight ? Formatting.GREEN : Formatting.RED)), false);

            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("Valid Spawn Location: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal((isValidBiome && isValidHeight) ? "✓ Yes" : "✗ No")
                            .formatted((isValidBiome && isValidHeight) ? Formatting.GREEN : Formatting.RED)), false);

            if (!isValidBiome) {
                player.sendMessage(Text.literal("  ✗ Not a mountain biome (need: Stony Peaks, Jagged Peaks, etc.)")
                        .formatted(Formatting.RED), false);
            }
            if (!isValidHeight) {
                player.sendMessage(Text.literal("  ✗ Surface too low (need Y≥160, current: " + surfaceY + ")")
                        .formatted(Formatting.RED), false);
            }

            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("Tip: Use /locatebiome minecraft:stony_peaks to find mountains")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), false);
            player.sendMessage(Text.literal("═══════════════════════════════════").formatted(Formatting.GRAY), false);

            return 1;
        }

        // Dragons found - show them
        player.sendMessage(Text.literal("═══════════════════════════════════").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🐉 Dragon Locator").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Found " + dragons.size() + " dragon(s) within 500 blocks:")
                .formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal(""), false);

        // Sort by distance
        dragons.sort((d1, d2) -> {
            double dist1 = player.squaredDistanceTo(d1);
            double dist2 = player.squaredDistanceTo(d2);
            return Double.compare(dist1, dist2);
        });

        // Show each dragon
        for (int i = 0; i < Math.min(dragons.size(), 5); i++) {
            DragonGuardianEntity dragon = dragons.get(i);
            BlockPos dragonPos = dragon.getBlockPos();
            double distance = Math.sqrt(player.squaredDistanceTo(dragon));

            String healthPercent = String.format("%.0f%%", (dragon.getHealth() / dragon.getMaxHealth()) * 100);
            String distanceStr = String.format("%.1f", distance);

            player.sendMessage(Text.literal("Dragon #" + (i + 1) + ":")
                    .formatted(Formatting.AQUA, Formatting.BOLD), false);
            player.sendMessage(Text.literal("  Location: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(String.format("X: %d, Y: %d, Z: %d", dragonPos.getX(), dragonPos.getY(), dragonPos.getZ()))
                            .formatted(Formatting.WHITE)), false);
            player.sendMessage(Text.literal("  Distance: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(distanceStr + " blocks")
                            .formatted(Formatting.YELLOW)), false);
            player.sendMessage(Text.literal("  Health: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(healthPercent)
                            .formatted(dragon.getHealth() > dragon.getMaxHealth() * 0.5 ? Formatting.GREEN : Formatting.RED)), false);

            if (i < dragons.size() - 1) {
                player.sendMessage(Text.literal(""), false);
            }
        }

        if (dragons.size() > 5) {
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("... and " + (dragons.size() - 5) + " more")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), false);
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("═══════════════════════════════════").formatted(Formatting.GRAY), false);

        return dragons.size();
    }
}
