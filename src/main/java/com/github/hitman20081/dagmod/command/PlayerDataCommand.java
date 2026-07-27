package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class PlayerDataCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("playerdata")
                .then(Commands.literal("info")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            showPlayerInfo(player, player);
                            return 1;
                        })
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .executes(context -> {
                                    ServerPlayer source = context.getSource().getPlayerOrException();
                                    String targetName = StringArgumentType.getString(context, "player");

                                    ServerPlayer target = context.getSource().getServer()
                                            .getPlayerList().getPlayer(targetName);

                                    if (target == null) {
                                        source.sendSystemMessage(Component.literal("Player not found: " + targetName)
                                                .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    showPlayerInfo(source, target);
                                    return 1;
                                })
                        )
                )
        );
    }

    private static void showPlayerInfo(ServerPlayer viewer, ServerPlayer target) {
        String race = RaceSelectionAltarBlock.getPlayerRace(target.getUUID());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(target.getUUID());

        boolean isSelf = viewer.getUUID().equals(target.getUUID());
        String header = isSelf ? "=== Your Player Data ===" : "=== " + target.getName().getString() + "'s Data ===";

        viewer.sendSystemMessage(Component.literal(header)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Race
        viewer.sendSystemMessage(Component.literal("Race: ")
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("none".equals(race) ? "Not Selected" : race)
                        .withStyle("none".equals(race) ? ChatFormatting.GRAY : ChatFormatting.AQUA)));

        // Class
        viewer.sendSystemMessage(Component.literal("Class: ")
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("none".equals(playerClass) ? "Not Selected" : playerClass)
                        .withStyle("none".equals(playerClass) ? ChatFormatting.GRAY : ChatFormatting.GREEN)));

        // Progression
        PlayerProgressionData progression = ProgressionManager.getPlayerData(target);
        if (progression != null) {
            viewer.sendSystemMessage(Component.literal("Level: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.valueOf(progression.getCurrentLevel()))
                            .withStyle(ChatFormatting.WHITE)));
            viewer.sendSystemMessage(Component.literal("XP: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(progression.getCurrentXP() + "/" + progression.getXPRequiredForNextLevel())
                            .withStyle(ChatFormatting.WHITE)));
        }

        // Tutorial progress
        boolean metGarrick = PlayerDataManager.hasMetGarrick(target.getUUID());
        boolean task1 = PlayerDataManager.isTask1Complete(target.getUUID());
        boolean task2 = PlayerDataManager.isTask2Complete(target.getUUID());
        boolean task3 = PlayerDataManager.isTask3Complete(target.getUUID());

        viewer.sendSystemMessage(Component.literal("Tutorial: ")
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("Garrick[" + (metGarrick ? "Y" : "N") + "] ")
                        .withStyle(metGarrick ? ChatFormatting.GREEN : ChatFormatting.GRAY))
                .append(Component.literal("T1[" + (task1 ? "Y" : "N") + "] ")
                        .withStyle(task1 ? ChatFormatting.GREEN : ChatFormatting.GRAY))
                .append(Component.literal("T2[" + (task2 ? "Y" : "N") + "] ")
                        .withStyle(task2 ? ChatFormatting.GREEN : ChatFormatting.GRAY))
                .append(Component.literal("T3[" + (task3 ? "Y" : "N") + "]")
                        .withStyle(task3 ? ChatFormatting.GREEN : ChatFormatting.GRAY)));

        if (!task2 && task1) {
            int kills = PlayerDataManager.getTask2MobKills(target.getUUID());
            viewer.sendSystemMessage(Component.literal("  Task 2 kills: ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(kills + "/5")
                            .withStyle(ChatFormatting.WHITE)));
        }
    }
}
