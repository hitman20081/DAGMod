package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class ResetClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {

        dispatcher.register(Commands.literal("resetclass")
                .executes(ResetClassCommand::resetOwnClass)
                .then(Commands.argument("player",
                                net.minecraft.commands.arguments.EntityArgument.player())
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(ResetClassCommand::resetPlayerClass)));
    }

    private static int resetOwnClass(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer player) {
            if (ClassSelectionAltarBlock.resetPlayerClass(player.getUUID())) {
                player.sendSystemMessage(Component.literal("Your class has been reset!")
                        .withStyle(ChatFormatting.GREEN));
                return 1;
            } else {
                player.sendSystemMessage(Component.literal("You don't have a class to reset!")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        source.sendSystemMessage(Component.literal("This command must be run by a player!")
                .withStyle(ChatFormatting.RED));
        return 0;
    }

    private static int resetPlayerClass(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            ServerPlayer targetPlayer = net.minecraft.commands.arguments.EntityArgument
                    .getPlayer(context, "player");

            if (ClassSelectionAltarBlock.resetPlayerClass(targetPlayer.getUUID())) {
                targetPlayer.sendSystemMessage(Component.literal("Your class has been reset by an admin!")
                        .withStyle(ChatFormatting.GOLD));
                source.sendSystemMessage(Component.literal("Reset class for " + targetPlayer.getName().getString())
                        .withStyle(ChatFormatting.GREEN));
                return 1;
            } else {
                source.sendSystemMessage(Component.literal(targetPlayer.getName().getString() +
                                " doesn't have a class to reset!")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }
        } catch (Exception e) {
            source.sendSystemMessage(Component.literal("Failed to reset class: " + e.getMessage())
                    .withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}