package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.grave.GraveData;
import com.github.hitman20081.dagmod.grave.GraveManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class GraveCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("grave")
                .then(Commands.literal("status")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            GraveManager manager = GraveManager.getInstance();
                            GraveData grave = manager.getGraveForPlayer(player.getUUID());

                            player.sendSystemMessage(Component.literal("=== Grave Status ===")
                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                            if (grave == null) {
                                player.sendSystemMessage(Component.literal("No active grave.")
                                        .withStyle(ChatFormatting.GRAY));
                                return 1;
                            }

                            // Location and dimension
                            player.sendSystemMessage(Component.literal("Location: ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal("[" + grave.getPosition().getX() + ", "
                                            + grave.getPosition().getY() + ", "
                                            + grave.getPosition().getZ() + "]")
                                            .withStyle(ChatFormatting.WHITE)));

                            player.sendSystemMessage(Component.literal("Dimension: ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal(grave.getDimension().toString())
                                            .withStyle(ChatFormatting.WHITE)));

                            // Item count
                            player.sendSystemMessage(Component.literal("Items: ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal(String.valueOf(grave.getItems().size()))
                                            .withStyle(ChatFormatting.WHITE)));

                            // Time since death
                            long currentTick = manager.getCurrentTick();
                            long elapsed = currentTick - grave.getCreatedAt();
                            long elapsedSeconds = elapsed / 20;
                            long minutes = elapsedSeconds / 60;
                            long seconds = elapsedSeconds % 60;

                            player.sendSystemMessage(Component.literal("Time since death: ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal(minutes + "m " + seconds + "s")
                                            .withStyle(ChatFormatting.WHITE)));

                            // Loot delay remaining
                            long lootDelay = manager.getLootDelayTicks();
                            long delayRemaining = lootDelay - elapsed;

                            if (delayRemaining > 0) {
                                long delaySeconds = delayRemaining / 20;
                                long delayMin = delaySeconds / 60;
                                long delaySec = delaySeconds % 60;
                                player.sendSystemMessage(Component.literal("Loot protection: ")
                                        .withStyle(ChatFormatting.YELLOW)
                                        .append(Component.literal(delayMin + "m " + delaySec + "s remaining")
                                                .withStyle(ChatFormatting.GREEN)));
                            } else {
                                player.sendSystemMessage(Component.literal("Loot protection: ")
                                        .withStyle(ChatFormatting.YELLOW)
                                        .append(Component.literal("Expired (others can loot)")
                                                .withStyle(ChatFormatting.RED)));
                            }

                            return 1;
                        })
                )
        );
    }
}
