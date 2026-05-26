package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.tags.BiomeTags;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import net.minecraft.server.permissions.Permissions;

/**
 * Command to locate Dragon Guardians
 * Shows currently spawned dragons and helps find valid spawn locations
 */
public class LocateDragonCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("locatedragon")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(LocateDragonCommand::findNearestDragon));
    }

    private static int findNearestDragon(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        ServerLevel world = (ServerLevel) source.getLevel();
        BlockPos playerPos = player.blockPosition();

        // Find all dragons in the world
        List<DragonGuardianEntity> dragons = world.getEntitiesOfClass(
                DragonGuardianEntity.class,
                player.getBoundingBox().inflate(500), // Search 500 block radius
                dragon -> true
        );

        if (dragons.isEmpty()) {
            // No dragons found - check if player is in valid spawn location
            boolean isValidBiome = world.getBiome(playerPos).is(BiomeTags.IS_MOUNTAIN);
            int surfaceY = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, playerPos).getY();
            boolean isValidHeight = surfaceY >= 160;

            player.sendSystemMessage(Component.literal("═══════════════════════════════════").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("🐉 Dragon Locator").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("No dragons found within 500 blocks").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal(""));

            // Show current location info
            player.sendSystemMessage(Component.literal("Current Location:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("  Position: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("X: %d, Y: %d, Z: %d", playerPos.getX(), playerPos.getY(), playerPos.getZ()))
                            .withStyle(ChatFormatting.WHITE)));
            player.sendSystemMessage(Component.literal("  Biome: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(world.getBiome(playerPos).unwrapKey().isPresent() ? world.getBiome(playerPos).unwrapKey().get().identifier().getPath() : "unknown")
                            .withStyle(isValidBiome ? ChatFormatting.GREEN : ChatFormatting.RED)));
            player.sendSystemMessage(Component.literal("  Surface Height: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Y: " + surfaceY)
                            .withStyle(isValidHeight ? ChatFormatting.GREEN : ChatFormatting.RED)));

            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("Valid Spawn Location: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal((isValidBiome && isValidHeight) ? "✓ Yes" : "✗ No")
                            .withStyle((isValidBiome && isValidHeight) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            if (!isValidBiome) {
                player.sendSystemMessage(Component.literal("  ✗ Not a mountain biome (need: Stony Peaks, Jagged Peaks, etc.)")
                        .withStyle(ChatFormatting.RED));
            }
            if (!isValidHeight) {
                player.sendSystemMessage(Component.literal("  ✗ Surface too low (need Y≥160, current: " + surfaceY + ")")
                        .withStyle(ChatFormatting.RED));
            }

            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("Tip: Use /locatebiome minecraft:stony_peaks to find mountains")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            player.sendSystemMessage(Component.literal("═══════════════════════════════════").withStyle(ChatFormatting.GRAY));

            return 1;
        }

        // Dragons found - show them
        player.sendSystemMessage(Component.literal("═══════════════════════════════════").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("🐉 Dragon Locator").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Found " + dragons.size() + " dragon(s) within 500 blocks:")
                .withStyle(ChatFormatting.GREEN));
        player.sendSystemMessage(Component.literal(""));

        // Sort by distance
        dragons.sort((d1, d2) -> {
            double dist1 = player.distanceToSqr(d1);
            double dist2 = player.distanceToSqr(d2);
            return Double.compare(dist1, dist2);
        });

        // Show each dragon
        for (int i = 0; i < Math.min(dragons.size(), 5); i++) {
            DragonGuardianEntity dragon = dragons.get(i);
            BlockPos dragonPos = dragon.blockPosition();
            double distance = Math.sqrt(player.distanceToSqr(dragon));

            String healthPercent = String.format("%.0f%%", (dragon.getHealth() / dragon.getMaxHealth()) * 100);
            String distanceStr = String.format("%.1f", distance);

            player.sendSystemMessage(Component.literal("Dragon #" + (i + 1) + ":")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("  Location: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("X: %d, Y: %d, Z: %d", dragonPos.getX(), dragonPos.getY(), dragonPos.getZ()))
                            .withStyle(ChatFormatting.WHITE)));
            player.sendSystemMessage(Component.literal("  Distance: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(distanceStr + " blocks")
                            .withStyle(ChatFormatting.YELLOW)));
            player.sendSystemMessage(Component.literal("  Health: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(healthPercent)
                            .withStyle(dragon.getHealth() > dragon.getMaxHealth() * 0.5 ? ChatFormatting.GREEN : ChatFormatting.RED)));

            if (i < dragons.size() - 1) {
                player.sendSystemMessage(Component.literal(""));
            }
        }

        if (dragons.size() > 5) {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("... and " + (dragons.size() - 5) + " more")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("═══════════════════════════════════").withStyle(ChatFormatting.GRAY));

        return dragons.size();
    }
}
