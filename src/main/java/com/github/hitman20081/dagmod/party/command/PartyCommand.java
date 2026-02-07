package com.github.hitman20081.dagmod.party.command;

import com.github.hitman20081.dagmod.party.PartyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Commands for party management
 */
public class PartyCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("party")
                .then(CommandManager.literal("create")
                        .executes(PartyCommand::executeCreate))

                .then(CommandManager.literal("invite")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(PartyCommand::executeInvite)))

                .then(CommandManager.literal("accept")
                        .executes(PartyCommand::executeAccept))

                .then(CommandManager.literal("leave")
                        .executes(PartyCommand::executeLeave))

                .then(CommandManager.literal("kick")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(PartyCommand::executeKick)))

                .then(CommandManager.literal("disband")
                        .executes(PartyCommand::executeDisband))

                .then(CommandManager.literal("list")
                        .executes(PartyCommand::executeList))

                .then(CommandManager.literal("chat")
                        .then(CommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(PartyCommand::executeChat)))

                .then(CommandManager.literal("togglequests")
                        .executes(PartyCommand::executeToggleQuests))
        );

        // Short alias for party chat
        dispatcher.register(CommandManager.literal("pc")
                .then(CommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(PartyCommand::executeChat))
        );
    }

    private static int executeCreate(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().createParty(player);
        return 1;
    }

    private static int executeInvite(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");

            if (target.equals(player)) {
                player.sendMessage(Text.literal("You cannot invite yourself!").formatted(Formatting.RED), false);
                return 0;
            }

            PartyManager.getInstance().invitePlayer(player, target);
        } catch (Exception e) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }

        return 1;
    }

    private static int executeAccept(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().acceptInvite(player);
        return 1;
    }

    private static int executeLeave(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().leaveParty(player);
        return 1;
    }

    private static int executeKick(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            PartyManager.getInstance().kickPlayer(player, target);
        } catch (Exception e) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }

        return 1;
    }

    private static int executeDisband(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().disbandParty(player);
        return 1;
    }

    private static int executeList(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PartyManager.getInstance().listPartyMembers(player);
        return 1;
    }

    private static int executeChat(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        String message = StringArgumentType.getString(context, "message");
        PartyManager.getInstance().sendPartyChat(player, message);
        return 1;
    }

    private static int executeToggleQuests(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        com.github.hitman20081.dagmod.party.PartyQuestHandler.toggleQuestSharing(player);
        return 1;
    }
}