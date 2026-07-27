package com.github.hitman20081.dagmod.party.command;

import com.github.hitman20081.dagmod.party.PartyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * Commands for party management
 */
public class PartyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("party")
                .then(Commands.literal("create")
                        .executes(PartyCommand::executeCreate))

                .then(Commands.literal("invite")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(PartyCommand::executeInvite)))

                .then(Commands.literal("accept")
                        .executes(PartyCommand::executeAccept))

                .then(Commands.literal("leave")
                        .executes(PartyCommand::executeLeave))

                .then(Commands.literal("kick")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(PartyCommand::executeKick)))

                .then(Commands.literal("disband")
                        .executes(PartyCommand::executeDisband))

                .then(Commands.literal("list")
                        .executes(PartyCommand::executeList))

                .then(Commands.literal("chat")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(PartyCommand::executeChat)))

                .then(Commands.literal("togglequests")
                        .executes(PartyCommand::executeToggleQuests))
        );

        // Short alias for party chat
        dispatcher.register(Commands.literal("pc")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(PartyCommand::executeChat))
        );
    }

    private static int executeCreate(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().createParty(player);
        return 1;
    }

    private static int executeInvite(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "player");

            if (target.equals(player)) {
                player.sendSystemMessage(Component.literal("You cannot invite yourself!").withStyle(ChatFormatting.RED));
                return 0;
            }

            PartyManager.getInstance().invitePlayer(player, target);
        } catch (CommandSyntaxException e) {
            player.sendSystemMessage(Component.literal("Player not found!").withStyle(ChatFormatting.RED));
            return 0;
        }

        return 1;
    }

    private static int executeAccept(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().acceptInvite(player);
        return 1;
    }

    private static int executeLeave(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().leaveParty(player);
        return 1;
    }

    private static int executeKick(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "player");
            PartyManager.getInstance().kickPlayer(player, target);
        } catch (CommandSyntaxException e) {
            player.sendSystemMessage(Component.literal("Player not found!").withStyle(ChatFormatting.RED));
            return 0;
        }

        return 1;
    }

    private static int executeDisband(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().disbandParty(player);
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().listPartyMembers(player);
        return 1;
    }

    private static int executeChat(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String message = StringArgumentType.getString(context, "message");
        PartyManager.getInstance().sendPartyChat(player, message);
        return 1;
    }

    private static int executeToggleQuests(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        com.github.hitman20081.dagmod.party.PartyQuestHandler.toggleQuestSharing(player);
        return 1;
    }
}