package com.github.hitman20081.dagmod.party.command;

import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class PartyQuestCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("partyquest")
                .then(Commands.literal("start")
                        .then(Commands.argument("questId", StringArgumentType.word())
                                .executes(PartyQuestCommand::executeStart)))
                
                .then(Commands.literal("progress")
                        .executes(PartyQuestCommand::executeProgress))
                
                .then(Commands.literal("abandon")
                        .executes(PartyQuestCommand::executeAbandon))
                
                .then(Commands.literal("list")
                        .executes(PartyQuestCommand::executeList))
        );
        
        // Short alias
        dispatcher.register(Commands.literal("pq")
                .then(Commands.literal("start")
                        .then(Commands.argument("questId", StringArgumentType.word())
                                .executes(PartyQuestCommand::executeStart)))
                
                .then(Commands.literal("progress")
                        .executes(PartyQuestCommand::executeProgress))
                
                .then(Commands.literal("abandon")
                        .executes(PartyQuestCommand::executeAbandon))
                
                .then(Commands.literal("list")
                        .executes(PartyQuestCommand::executeList))
        );
    }
    
    private static int executeStart(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        String questId = StringArgumentType.getString(context, "questId");
        PartyQuestManager.getInstance().startQuest(player, questId);
        return 1;
    }
    
    private static int executeProgress(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().showProgress(player);
        return 1;
    }
    
    private static int executeAbandon(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().abandonQuest(player);
        return 1;
    }
    
    private static int executeList(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        PartyQuestManager.getInstance().listQuests(player);
        return 1;
    }
}