package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.grave.GraveData;
import com.github.hitman20081.dagmod.grave.GraveManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GraveCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("grave")
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            GraveManager manager = GraveManager.getInstance();
                            GraveData grave = manager.getGraveForPlayer(player.getUuid());

                            player.sendMessage(Text.literal("=== Grave Status ===")
                                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

                            if (grave == null) {
                                player.sendMessage(Text.literal("No active grave.")
                                        .formatted(Formatting.GRAY), false);
                                return 1;
                            }

                            // Location and dimension
                            player.sendMessage(Text.literal("Location: ")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal("[" + grave.getPosition().getX() + ", "
                                            + grave.getPosition().getY() + ", "
                                            + grave.getPosition().getZ() + "]")
                                            .formatted(Formatting.WHITE)), false);

                            player.sendMessage(Text.literal("Dimension: ")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(grave.getDimension().toString())
                                            .formatted(Formatting.WHITE)), false);

                            // Item count
                            player.sendMessage(Text.literal("Items: ")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(String.valueOf(grave.getItems().size()))
                                            .formatted(Formatting.WHITE)), false);

                            // Time since death
                            long currentTick = manager.getCurrentTick();
                            long elapsed = currentTick - grave.getCreatedAt();
                            long elapsedSeconds = elapsed / 20;
                            long minutes = elapsedSeconds / 60;
                            long seconds = elapsedSeconds % 60;

                            player.sendMessage(Text.literal("Time since death: ")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(minutes + "m " + seconds + "s")
                                            .formatted(Formatting.WHITE)), false);

                            // Loot delay remaining
                            long lootDelay = manager.getLootDelayTicks();
                            long delayRemaining = lootDelay - elapsed;

                            if (delayRemaining > 0) {
                                long delaySeconds = delayRemaining / 20;
                                long delayMin = delaySeconds / 60;
                                long delaySec = delaySeconds % 60;
                                player.sendMessage(Text.literal("Loot protection: ")
                                        .formatted(Formatting.YELLOW)
                                        .append(Text.literal(delayMin + "m " + delaySec + "s remaining")
                                                .formatted(Formatting.GREEN)), false);
                            } else {
                                player.sendMessage(Text.literal("Loot protection: ")
                                        .formatted(Formatting.YELLOW)
                                        .append(Text.literal("Expired (others can loot)")
                                                .formatted(Formatting.RED)), false);
                            }

                            return 1;
                        })
                )
        );
    }
}
