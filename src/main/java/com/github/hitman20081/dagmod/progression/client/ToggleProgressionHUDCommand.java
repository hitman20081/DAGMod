package com.github.hitman20081.dagmod.progression.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

/**
 * Client-side command to toggle the progression HUD background on/off and change its color
 * Usage:
 *   /toggleprogressionbg or /tpbg - Toggle background on/off
 *   /toggleprogressionbg <color> or /tpbg <color> - Set background color and enable it
 */
public class ToggleProgressionHUDCommand {

    // Suggestion provider for color names
    private static final SuggestionProvider<FabricClientCommandSource> COLOR_SUGGESTIONS =
            (context, builder) -> {
                String[] colors = ProgressionHUD.getAvailableColors();
                for (String color : colors) {
                    builder.suggest(color);
                }
                return builder.buildFuture();
            };

    /**
     * Register the command
     */
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Full command name with optional color argument
        dispatcher.register(
                ClientCommandManager.literal("toggleprogressionbg")
                        .executes(ToggleProgressionHUDCommand::executeToggle)
                        .then(ClientCommandManager.argument("color", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .executes(ToggleProgressionHUDCommand::executeWithColor))
        );

        // Short alias with optional color argument
        dispatcher.register(
                ClientCommandManager.literal("tpbg")
                        .executes(ToggleProgressionHUDCommand::executeToggle)
                        .then(ClientCommandManager.argument("color", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .executes(ToggleProgressionHUDCommand::executeWithColor))
        );
    }

    /**
     * Execute the toggle command (no color specified)
     */
    private static int executeToggle(CommandContext<FabricClientCommandSource> context) {
        boolean newState = ProgressionHUD.toggleBackground();

        if (newState) {
            context.getSource().sendFeedback(
                    Text.literal("§aProgression HUD background enabled")
            );
        } else {
            context.getSource().sendFeedback(
                    Text.literal("§cProgression HUD background disabled")
            );
        }

        return 1;
    }

    /**
     * Execute the command with a color argument
     */
    private static int executeWithColor(CommandContext<FabricClientCommandSource> context) {
        String color = StringArgumentType.getString(context, "color");

        if (ProgressionHUD.setBackgroundColor(color)) {
            // Enable background if it was disabled
            ProgressionHUD.setBackgroundEnabled(true);

            context.getSource().sendFeedback(
                    Text.literal("§aProgression HUD background set to " + color)
            );
        } else {
            // Invalid color
            String[] availableColors = ProgressionHUD.getAvailableColors();
            context.getSource().sendFeedback(
                    Text.literal("§cInvalid color! Available colors: " + String.join(", ", availableColors))
            );
        }

        return 1;
    }
}