package com.github.hitman20081.dagmod.progression;

import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

/**
 * Test command for the progression system
 * Usage: /testprogression <xp_amount>
 */
public class ProgressionTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {

        dispatcher.register(Commands.literal("testprogression")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.argument("xp", IntegerArgumentType.integer(0))
                        .executes(ProgressionTestCommand::executeAddXP))
                .executes(ProgressionTestCommand::executeInfo)
        );

        dispatcher.register(Commands.literal("testprogression")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("info")
                        .executes(ProgressionTestCommand::executeInfo))
                .then(Commands.literal("reset")
                        .executes(ProgressionTestCommand::executeReset))
                .then(Commands.literal("setlevel")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 200))
                                .executes(ProgressionTestCommand::executeSetLevel)))
                .then(Commands.literal("curve")
                        .executes(ProgressionTestCommand::executeCurve))
        );
    }

    /**
     * Add XP to player: /testprogression <amount>
     */
    private static int executeAddXP(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        int xpAmount = IntegerArgumentType.getInteger(context, "xp");

        // Use the real progression manager
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendSystemMessage(Component.literal("§eBefore: " + data.getDisplayString()));

        // Add XP through manager (handles sync and level-ups)
        int levelsGained = ProgressionManager.addXP(player, xpAmount);

        player.sendSystemMessage(Component.literal("§aAfter: " + data.getDisplayString()));

        return 1;
    }

    /**
     * Show progression info: /testprogression info
     */
    private static int executeInfo(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        // Get real data from manager
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendSystemMessage(Component.literal("§6=== Progression Info ==="));
        player.sendSystemMessage(Component.literal("§e" + data.getDisplayString()));
        player.sendSystemMessage(Component.literal("§7Short: " + data.getShortDisplayString()));
        player.sendSystemMessage(Component.literal("§7Total XP Earned: §f" + String.format("%,d", data.getTotalXPEarned())));
        player.sendSystemMessage(Component.literal("§7Max Level: §f" + data.isMaxLevel()));

        return 1;
    }

    /**
     * Reset progression: /testprogression reset
     */
    private static int executeReset(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        // Use manager to reset
        ProgressionManager.resetProgression(player);
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendSystemMessage(Component.literal("§cProgression reset to level 1"));
        player.sendSystemMessage(Component.literal("§e" + data.getDisplayString()));

        return 1;
    }

    /**
     * Set level: /testprogression setlevel <level>
     */
    private static int executeSetLevel(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        int level = IntegerArgumentType.getInteger(context, "level");

        // Use manager to set level
        ProgressionManager.setLevel(player, level);
        PlayerProgressionData data = ProgressionManager.getPlayerData(player);

        player.sendSystemMessage(Component.literal("§aSet level to " + level));
        player.sendSystemMessage(Component.literal("§e" + data.getDisplayString()));

        return 1;
    }

    /**
     * Show XP curve: /testprogression curve
     */
    private static int executeCurve(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();

        source.sendSuccess(() -> Component.literal("§6=== XP Curve (Every 5 Levels) ==="), false);

        int totalXP = 0;
        for (int level = 1; level <= 200; level++) {
            int xpForLevel = PlayerProgressionData.calculateXPForLevel(level);
            totalXP += xpForLevel;

            if (level == 1 || level % 10 == 0 || level == 200) {
                String formatted = String.format("§eLv%3d: §f%,7d XP §7(Total: %,d)",
                        level, xpForLevel, totalXP);
                final String message = formatted;
                source.sendSuccess(() -> Component.literal(message), false);
            }
        }

        int totalToMax = PlayerProgressionData.calculateTotalXPForLevel(200);
        source.sendSuccess(() -> Component.literal(String.format("§6Total to max: §f%,d XP", totalToMax)), false);

        return 1;
    }
}