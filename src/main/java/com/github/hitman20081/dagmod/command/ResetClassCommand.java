package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ResetClassCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("resetclass")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                .executes(ResetClassCommand::resetOwnClass)
                .then(CommandManager.argument("player",
                                net.minecraft.command.argument.EntityArgumentType.player())
                        .executes(ResetClassCommand::resetPlayerClass)));
    }

    private static int resetOwnClass(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            if (ClassSelectionAltarBlock.resetPlayerClass(player.getUuid())) {
                player.sendMessage(Text.literal("Your class has been reset!")
                        .formatted(Formatting.GREEN), false);
                return 1;
            } else {
                player.sendMessage(Text.literal("You don't have a class to reset!")
                        .formatted(Formatting.RED), false);
                return 0;
            }
        }

        source.sendMessage(Text.literal("This command must be run by a player!")
                .formatted(Formatting.RED));
        return 0;
    }

    private static int resetPlayerClass(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            ServerPlayerEntity targetPlayer = net.minecraft.command.argument.EntityArgumentType
                    .getPlayer(context, "player");

            if (ClassSelectionAltarBlock.resetPlayerClass(targetPlayer.getUuid())) {
                targetPlayer.sendMessage(Text.literal("Your class has been reset by an admin!")
                        .formatted(Formatting.GOLD), false);
                source.sendMessage(Text.literal("Reset class for " + targetPlayer.getName().getString())
                        .formatted(Formatting.GREEN));
                return 1;
            } else {
                source.sendMessage(Text.literal(targetPlayer.getName().getString() +
                                " doesn't have a class to reset!")
                        .formatted(Formatting.RED));
                return 0;
            }
        } catch (Exception e) {
            source.sendMessage(Text.literal("Failed to reset class: " + e.getMessage())
                    .formatted(Formatting.RED));
            return 0;
        }
    }
}