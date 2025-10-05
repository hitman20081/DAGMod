package com.github.hitman20081.dagmod.progression;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Test command for the progression system
 * Usage: /testprogression <xp_amount>
 */
public class ProgressionTestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("testprogression")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP
                .then(CommandManager.argument("xp", IntegerArgumentType.integer(0))
                        .executes(ProgressionTestCommand::executeAddXP))
                .executes(ProgressionTestCommand::executeInfo)
        );

        dispatcher.register(CommandManager.literal("testprogression")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("info")
                        .executes(ProgressionTestCommand::executeInfo))
                .then(CommandManager.literal("reset")
                        .executes(ProgressionTestCommand::executeReset))
                .then(CommandManager.literal("setlevel")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(1, 50))
                                .executes(ProgressionTestCommand::executeSetLevel)))
                .then(CommandManager.literal("curve")
                        .executes(ProgressionTestCommand::executeCurve))
        );
    }

    /**
     * Add XP to player: /testprogression <amount>
     */
    private static int executeAddXP(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        int xpAmount = IntegerArgumentType.getInteger(context, "xp");

        // Use the real progression manager
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendMessage(Text.literal("§eBefore: " + data.getDisplayString()), false);

        // Add XP through manager (handles sync and level-ups)
        int levelsGained = ProgressionManager.addXP(player, xpAmount);

        player.sendMessage(Text.literal("§aAfter: " + data.getDisplayString()), false);

        return 1;
    }

    /**
     * Show progression info: /testprogression info
     */
    private static int executeInfo(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        // Get real data from manager
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendMessage(Text.literal("§6=== Progression Info ==="), false);
        player.sendMessage(Text.literal("§e" + data.getDisplayString()), false);
        player.sendMessage(Text.literal("§7Short: " + data.getShortDisplayString()), false);
        player.sendMessage(Text.literal("§7Total XP Earned: §f" + String.format("%,d", data.getTotalXPEarned())), false);
        player.sendMessage(Text.literal("§7Max Level: §f" + data.isMaxLevel()), false);

        return 1;
    }

    /**
     * Reset progression: /testprogression reset
     */
    private static int executeReset(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        // Use manager to reset
        ProgressionManager.resetProgression(player);
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendMessage(Text.literal("§cProgression reset to level 1"), false);
        player.sendMessage(Text.literal("§e" + data.getDisplayString()), false);

        return 1;
    }

    /**
     * Set level: /testprogression setlevel <level>
     */
    private static int executeSetLevel(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        int level = IntegerArgumentType.getInteger(context, "level");

        // Use manager to set level
        ProgressionManager.setLevel(player, level);
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendMessage(Text.literal("§aSet level to " + level), false);
        player.sendMessage(Text.literal("§e" + data.getDisplayString()), false);

        return 1;
    }

    /**
     * Show XP curve: /testprogression curve
     */
    private static int executeCurve(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();

        source.sendFeedback(() -> Text.literal("§6=== XP Curve (Every 5 Levels) ==="), false);

        int totalXP = 0;
        for (int level = 1; level <= 50; level++) {
            int xpForLevel = PlayerProgressionData.calculateXPForLevel(level);
            totalXP += xpForLevel;

            if (level == 1 || level % 5 == 0 || level == 50) {
                String formatted = String.format("§eLv%2d: §f%,7d XP §7(Total: %,d)",
                        level, xpForLevel, totalXP);
                final String message = formatted;
                source.sendFeedback(() -> Text.literal(message), false);
            }
        }

        int totalToMax = PlayerProgressionData.calculateTotalXPForLevel(50);
        source.sendFeedback(() -> Text.literal(String.format("§6Total to max: §f%,d XP", totalToMax)), false);

        return 1;
    }
}