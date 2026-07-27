package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.dragon_realm.boss.DragonRespawnTimerManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class DragonRespawnCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("dragonrespawn")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("status")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            player.sendSystemMessage(Component.literal("=== Dragon Respawn Timer ===")
                                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));

                            // Check all loaded worlds for active timers
                            boolean foundTimer = false;
                            for (ServerLevel world : server.getAllLevels()) {
                                if (manager.hasActiveTimer(world)) {
                                    foundTimer = true;
                                    long remaining = manager.getTimeRemaining(world);
                                    long seconds = remaining / 20;
                                    long min = seconds / 60;
                                    long sec = seconds % 60;

                                    player.sendSystemMessage(Component.literal(world.dimension().identifier().toString() + ": ")
                                            .withStyle(ChatFormatting.YELLOW)
                                            .append(Component.literal(min + "m " + sec + "s remaining")
                                                    .withStyle(ChatFormatting.WHITE)));
                                }
                            }

                            if (!foundTimer) {
                                player.sendSystemMessage(Component.literal("No active respawn timers.")
                                        .withStyle(ChatFormatting.GRAY));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            int resetCount = 0;
                            for (ServerLevel world : server.getAllLevels()) {
                                if (manager.hasActiveTimer(world)) {
                                    manager.cancelTimer(world);
                                }
                                manager.startTimer(world);
                                resetCount++;
                            }

                            player.sendSystemMessage(Component.literal("Reset dragon respawn timers for " + resetCount + " dimension(s).")
                                    .withStyle(ChatFormatting.GREEN));
                            return 1;
                        })
                )
                .then(Commands.literal("cancel")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            MinecraftServer server = context.getSource().getServer();
                            DragonRespawnTimerManager manager = DragonRespawnTimerManager.get(server);

                            int cancelCount = 0;
                            for (ServerLevel world : server.getAllLevels()) {
                                if (manager.hasActiveTimer(world)) {
                                    manager.cancelTimer(world);
                                    cancelCount++;
                                }
                            }

                            if (cancelCount > 0) {
                                player.sendSystemMessage(Component.literal("Cancelled " + cancelCount + " dragon respawn timer(s).")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                player.sendSystemMessage(Component.literal("No active timers to cancel.")
                                        .withStyle(ChatFormatting.GRAY));
                            }

                            return 1;
                        })
                )
        );
    }
}
