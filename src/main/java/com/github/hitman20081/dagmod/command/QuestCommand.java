package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestObjective;
import com.github.hitman20081.dagmod.block.QuestBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("quest")
                .then(Commands.literal("skip")
                        .executes(QuestCommand::skipQuest))
                .then(Commands.literal("list")
                        .executes(QuestCommand::listQuests))
                .then(Commands.literal("abandon")
                        .then(Commands.argument("questId", StringArgumentType.string())
                                .executes(QuestCommand::abandonQuest)))
                .then(Commands.literal("abandonall")
                        .executes(QuestCommand::abandonAllQuests))
        );
    }

    private static int skipQuest(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        UUID playerId = player.getUUID();

        // Increment index and return to browse mode
        Integer currentIndex = QuestBlock.playerSelectedIndex.get(playerId);
        if (currentIndex != null) {
            int nextIndex = currentIndex + 1;
            // showBrowseQuests handles out-of-bounds by returning to main menu,
            // so just let it increment naturally
            QuestBlock.playerSelectedIndex.put(playerId, nextIndex);
            QuestBlock.playerMenuState.put(playerId, QuestBlock.MenuState.BROWSE_QUESTS);
            player.sendSystemMessage(Component.literal("Skipping to next quest...").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Right-click the Quest Block to continue browsing.").withStyle(ChatFormatting.GRAY));
        } else {
            player.sendSystemMessage(Component.literal("You're not currently browsing quests!").withStyle(ChatFormatting.RED));
        }

        return 1;
    }

    private static int listQuests(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        // Update progress first
        manager.updateQuestProgress(player);

        List<Quest> activeQuests = new ArrayList<>(playerData.getActiveQuests());

        player.sendSystemMessage(Component.literal("=== Active Quests (" + activeQuests.size() + "/" + playerData.getMaxActiveQuests() + ") ===").withStyle(ChatFormatting.GOLD));

        if (activeQuests.isEmpty()) {
            player.sendSystemMessage(Component.literal("No active quests.").withStyle(ChatFormatting.GRAY));
        } else {
            for (Quest quest : activeQuests) {
                boolean completed = quest.isCompleted();
                ChatFormatting nameColor = completed ? ChatFormatting.GREEN : ChatFormatting.WHITE;
                String status = completed ? " [COMPLETE]" : "";

                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(Component.literal(quest.getName() + " (" + quest.getDifficulty().name() + ")" + status)
                        .withStyle(nameColor));
                player.sendSystemMessage(Component.literal("  ID: " + quest.getId()).withStyle(ChatFormatting.DARK_GRAY));

                if (quest.getObjectives() != null) {
                    for (QuestObjective obj : quest.getObjectives()) {
                        ChatFormatting objColor = obj.isCompleted() ? ChatFormatting.GREEN : ChatFormatting.RED;
                        player.sendSystemMessage(Component.literal("  " + (obj.isCompleted() ? "\u2713" : "\u2717") + " " +
                                obj.getDescription() + " (" + obj.getCurrentProgress() + "/" + obj.getRequiredProgress() + ")")
                                .withStyle(objColor));
                    }
                } else {
                    player.sendSystemMessage(Component.literal("  [No objectives - quest data may be corrupted]").withStyle(ChatFormatting.RED));
                }
            }
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Completed: " + playerData.getTotalQuestsCompleted() + " | Tier: " + playerData.getQuestBookTier().getDisplayName()).withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Use /quest abandon <questId> to drop a quest.").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));

        return 1;
    }

    private static int abandonQuest(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String questId = StringArgumentType.getString(context, "questId");
        QuestManager manager = QuestManager.getInstance();
        manager.abandonQuest(player, questId);

        return 1;
    }

    private static int abandonAllQuests(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        List<String> questIds = new ArrayList<>();
        for (Quest quest : playerData.getActiveQuests()) {
            questIds.add(quest.getId());
        }

        if (questIds.isEmpty()) {
            player.sendSystemMessage(Component.literal("No active quests to abandon.").withStyle(ChatFormatting.GRAY));
            return 1;
        }

        for (String questId : questIds) {
            manager.abandonQuest(player, questId);
        }

        player.sendSystemMessage(Component.literal("All " + questIds.size() + " active quests abandoned.").withStyle(ChatFormatting.YELLOW));
        return 1;
    }
}
