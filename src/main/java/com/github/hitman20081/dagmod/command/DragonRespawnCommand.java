package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.dragon_realm.boss.DragonRespawnTimerManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DragonRespawnCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dragonrespawn")
                .requires(source -> source.getPermissions().hasPermission(new net.minecraft.command.permission.Permission.Level(net.minecraft.command.permission.PermissionLevel.GAMEMASTERS)))
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            player.sendMessage(Text.literal("=== Dragon Respawn Timer ===")
                                    .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

                            // Check all loaded worlds for active timers
                            boolean foundTimer = false;
                            for (ServerWorld world : server.getWorlds()) {
                                if (manager.hasActiveTimer(world)) {
                                    foundTimer = true;
                                    long remaining = manager.getTimeRemaining(world);
                                    long seconds = remaining / 20;
                                    long min = seconds / 60;
                                    long sec = seconds % 60;

                                    player.sendMessage(Text.literal(world.getRegistryKey().getValue().toString() + ": ")
                                            .formatted(Formatting.YELLOW)
                                            .append(Text.literal(min + "m " + sec + "s remaining")
                                                    .formatted(Formatting.WHITE)), false);
                                }
                            }

                            if (!foundTimer) {
                                player.sendMessage(Text.literal("No active respawn timers.")
                                        .formatted(Formatting.GRAY), false);
                            }

                            return 1;
                        })
                )
                .then(CommandManager.literal("reset")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            int resetCount = 0;
                            for (ServerWorld world : server.getWorlds()) {
                                if (manager.hasActiveTimer(world)) {
                                    manager.cancelTimer(world);
                                }
                                manager.startTimer(world);
                                resetCount++;
                            }

                            player.sendMessage(Text.literal("Reset dragon respawn timers for " + resetCount + " dimension(s).")
                                    .formatted(Formatting.GREEN), false);
                            return 1;
                        })
                )
                .then(CommandManager.literal("cancel")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            int cancelCount = 0;
                            for (ServerWorld world : server.getWorlds()) {
                                if (manager.hasActiveTimer(world)) {
                                    manager.cancelTimer(world);
                                    cancelCount++;
                                }
                            }

                            if (cancelCount > 0) {
                                player.sendMessage(Text.literal("Cancelled " + cancelCount + " dragon respawn timer(s).")
                                        .formatted(Formatting.YELLOW), false);
                            } else {
                                player.sendMessage(Text.literal("No active timers to cancel.")
                                        .formatted(Formatting.GRAY), false);
                            }

                            return 1;
                        })
                )
        );
    }
}
