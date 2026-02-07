package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.block.QuestBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class QuestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("quest")
                .then(CommandManager.literal("skip")
                        .executes(QuestCommand::skipQuest))
        );
    }

    private static int skipQuest(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        UUID playerId = player.getUuid();

        // Increment index and return to browse mode
        Integer currentIndex = QuestBlock.playerSelectedIndex.get(playerId);
        if (currentIndex != null) {
            QuestBlock.playerSelectedIndex.put(playerId, currentIndex + 1);
            QuestBlock.playerMenuState.put(playerId, QuestBlock.MenuState.BROWSE_QUESTS);
            player.sendMessage(Text.literal("Skipping to next quest...").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Right-click the Quest Block to continue browsing.").formatted(Formatting.GRAY), false);
        } else {
            player.sendMessage(Text.literal("You're not currently browsing quests!").formatted(Formatting.RED), false);
        }

        return 1;
    }
}