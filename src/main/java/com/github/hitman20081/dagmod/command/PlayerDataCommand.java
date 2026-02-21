package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerDataCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("playerdata")
                .then(CommandManager.literal("info")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            showPlayerInfo(player, player);
                            return 1;
                        })
                        .then(CommandManager.argument("player", StringArgumentType.word())
                                .requires(source -> source.getPermissions().hasPermission(new net.minecraft.command.permission.Permission.Level(net.minecraft.command.permission.PermissionLevel.GAMEMASTERS)))
                                .executes(context -> {
                                    ServerPlayerEntity source = context.getSource().getPlayerOrThrow();
                                    String targetName = StringArgumentType.getString(context, "player");

                                    ServerPlayerEntity target = context.getSource().getServer()
                                            .getPlayerManager().getPlayer(targetName);

                                    if (target == null) {
                                        source.sendMessage(Text.literal("Player not found: " + targetName)
                                                .formatted(Formatting.RED), false);
                                        return 0;
                                    }

                                    showPlayerInfo(source, target);
                                    return 1;
                                })
                        )
                )
        );
    }

    private static void showPlayerInfo(ServerPlayerEntity viewer, ServerPlayerEntity target) {
        String race = RaceSelectionAltarBlock.getPlayerRace(target.getUuid());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(target.getUuid());

        boolean isSelf = viewer.getUuid().equals(target.getUuid());
        String header = isSelf ? "=== Your Player Data ===" : "=== " + target.getName().getString() + "'s Data ===";

        viewer.sendMessage(Text.literal(header)
                .formatted(Formatting.GOLD, Formatting.BOLD), false);

        // Race
        viewer.sendMessage(Text.literal("Race: ")
                .formatted(Formatting.YELLOW)
                .append(Text.literal("none".equals(race) ? "Not Selected" : race)
                        .formatted("none".equals(race) ? Formatting.GRAY : Formatting.AQUA)), false);

        // Class
        viewer.sendMessage(Text.literal("Class: ")
                .formatted(Formatting.YELLOW)
                .append(Text.literal("none".equals(playerClass) ? "Not Selected" : playerClass)
                        .formatted("none".equals(playerClass) ? Formatting.GRAY : Formatting.GREEN)), false);

        // Progression
        PlayerProgressionData progression = ProgressionManager.getPlayerData(target);
        if (progression != null) {
            viewer.sendMessage(Text.literal("Level: ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(String.valueOf(progression.getCurrentLevel()))
                            .formatted(Formatting.WHITE)), false);
            viewer.sendMessage(Text.literal("XP: ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(progression.getCurrentXP() + "/" + progression.getXPRequiredForNextLevel())
                            .formatted(Formatting.WHITE)), false);
        }

        // Tutorial progress
        boolean metGarrick = PlayerDataManager.hasMetGarrick(target.getUuid());
        boolean task1 = PlayerDataManager.isTask1Complete(target.getUuid());
        boolean task2 = PlayerDataManager.isTask2Complete(target.getUuid());
        boolean task3 = PlayerDataManager.isTask3Complete(target.getUuid());

        viewer.sendMessage(Text.literal("Tutorial: ")
                .formatted(Formatting.YELLOW)
                .append(Text.literal("Garrick[" + (metGarrick ? "Y" : "N") + "] ")
                        .formatted(metGarrick ? Formatting.GREEN : Formatting.GRAY))
                .append(Text.literal("T1[" + (task1 ? "Y" : "N") + "] ")
                        .formatted(task1 ? Formatting.GREEN : Formatting.GRAY))
                .append(Text.literal("T2[" + (task2 ? "Y" : "N") + "] ")
                        .formatted(task2 ? Formatting.GREEN : Formatting.GRAY))
                .append(Text.literal("T3[" + (task3 ? "Y" : "N") + "]")
                        .formatted(task3 ? Formatting.GREEN : Formatting.GRAY)), false);

        if (!task2 && task1) {
            int kills = PlayerDataManager.getTask2MobKills(target.getUuid());
            viewer.sendMessage(Text.literal("  Task 2 kills: ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(kills + "/5")
                            .formatted(Formatting.WHITE)), false);
        }
    }
}
