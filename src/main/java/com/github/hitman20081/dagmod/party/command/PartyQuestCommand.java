package com.github.hitman20081.dagmod.party.command;

import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PartyQuestCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("partyquest")
                .then(CommandManager.literal("start")
                        .then(CommandManager.argument("questId", StringArgumentType.word())
                                .executes(PartyQuestCommand::executeStart)))
                
                .then(CommandManager.literal("progress")
                        .executes(PartyQuestCommand::executeProgress))
                
                .then(CommandManager.literal("abandon")
                        .executes(PartyQuestCommand::executeAbandon))
                
                .then(CommandManager.literal("list")
                        .executes(PartyQuestCommand::executeList))
        );
        
        // Short alias
        dispatcher.register(CommandManager.literal("pq")
                .then(CommandManager.literal("start")
                        .then(CommandManager.argument("questId", StringArgumentType.word())
                                .executes(PartyQuestCommand::executeStart)))
                
                .then(CommandManager.literal("progress")
                        .executes(PartyQuestCommand::executeProgress))
                
                .then(CommandManager.literal("abandon")
                        .executes(PartyQuestCommand::executeAbandon))
                
                .then(CommandManager.literal("list")
                        .executes(PartyQuestCommand::executeList))
        );
    }
    
    private static int executeStart(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        String questId = StringArgumentType.getString(context, "questId");
        PartyQuestManager.getInstance().startQuest(player, questId);
        return 1;
    }
    
    private static int executeProgress(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().showProgress(player);
        return 1;
    }
    
    private static int executeAbandon(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().abandonQuest(player);
        return 1;
    }
    
    private static int executeList(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().listQuests(player);
        return 1;
    }
}