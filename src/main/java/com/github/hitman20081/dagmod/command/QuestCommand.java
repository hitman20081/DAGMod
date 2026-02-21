package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestObjective;
import com.github.hitman20081.dagmod.block.QuestBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("quest")
                .then(CommandManager.literal("skip")
                        .executes(QuestCommand::skipQuest))
                .then(CommandManager.literal("list")
                        .executes(QuestCommand::listQuests))
                .then(CommandManager.literal("abandon")
                        .then(CommandManager.argument("questId", StringArgumentType.string())
                                .executes(QuestCommand::abandonQuest)))
                .then(CommandManager.literal("abandonall")
                        .executes(QuestCommand::abandonAllQuests))
        );
    }

    private static int skipQuest(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        UUID playerId = player.getUuid();

        // Increment index and return to browse mode
        Integer currentIndex = QuestBlock.playerSelectedIndex.get(playerId);
        if (currentIndex != null) {
            int nextIndex = currentIndex + 1;
            // showBrowseQuests handles out-of-bounds by returning to main menu,
            // so just let it increment naturally
            QuestBlock.playerSelectedIndex.put(playerId, nextIndex);
            QuestBlock.playerMenuState.put(playerId, QuestBlock.MenuState.BROWSE_QUESTS);
            player.sendMessage(Text.literal("Skipping to next quest...").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Right-click the Quest Block to continue browsing.").formatted(Formatting.GRAY), false);
        } else {
            player.sendMessage(Text.literal("You're not currently browsing quests!").formatted(Formatting.RED), false);
        }

        return 1;
    }

    private static int listQuests(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        // Update progress first
        manager.updateQuestProgress(player);

        List<Quest> activeQuests = new ArrayList<>(playerData.getActiveQuests());

        player.sendMessage(Text.literal("=== Active Quests (" + activeQuests.size() + "/" + playerData.getMaxActiveQuests() + ") ===").formatted(Formatting.GOLD), false);

        if (activeQuests.isEmpty()) {
            player.sendMessage(Text.literal("No active quests.").formatted(Formatting.GRAY), false);
        } else {
            for (Quest quest : activeQuests) {
                boolean completed = quest.isCompleted();
                Formatting nameColor = completed ? Formatting.GREEN : Formatting.WHITE;
                String status = completed ? " [COMPLETE]" : "";

                player.sendMessage(Text.literal(""), false);
                player.sendMessage(Text.literal(quest.getName() + " (" + quest.getDifficulty().name() + ")" + status)
                        .formatted(nameColor), false);
                player.sendMessage(Text.literal("  ID: " + quest.getId()).formatted(Formatting.DARK_GRAY), false);

                if (quest.getObjectives() != null) {
                    for (QuestObjective obj : quest.getObjectives()) {
                        Formatting objColor = obj.isCompleted() ? Formatting.GREEN : Formatting.RED;
                        player.sendMessage(Text.literal("  " + (obj.isCompleted() ? "\u2713" : "\u2717") + " " +
                                obj.getDescription() + " (" + obj.getCurrentProgress() + "/" + obj.getRequiredProgress() + ")")
                                .formatted(objColor), false);
                    }
                } else {
                    player.sendMessage(Text.literal("  [No objectives - quest data may be corrupted]").formatted(Formatting.RED), false);
                }
            }
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Completed: " + playerData.getTotalQuestsCompleted() + " | Tier: " + playerData.getQuestBookTier().getDisplayName()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Use /quest abandon <questId> to drop a quest.").formatted(Formatting.DARK_GRAY), false);
        player.sendMessage(Text.literal("===================").formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int abandonQuest(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        String questId = StringArgumentType.getString(context, "questId");
        QuestManager manager = QuestManager.getInstance();
        manager.abandonQuest(player, questId);

        return 1;
    }

    private static int abandonAllQuests(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        QuestManager manager = QuestManager.getInstance();
        QuestData playerData = manager.getPlayerData(player);

        List<String> questIds = new ArrayList<>();
        for (Quest quest : playerData.getActiveQuests()) {
            questIds.add(quest.getId());
        }

        if (questIds.isEmpty()) {
            player.sendMessage(Text.literal("No active quests to abandon.").formatted(Formatting.GRAY), false);
            return 1;
        }

        for (String questId : questIds) {
            manager.abandonQuest(player, questId);
        }

        player.sendMessage(Text.literal("All " + questIds.size() + " active quests abandoned.").formatted(Formatting.YELLOW), false);
        return 1;
    }
}
