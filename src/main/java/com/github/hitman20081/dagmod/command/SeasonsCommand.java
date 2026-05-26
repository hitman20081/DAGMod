package com.github.hitman20081.dagmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Objective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class SeasonsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {

        var root = Commands.literal("seasons")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(ctx -> showMenu(ctx.getSource()));

        root.then(Commands.literal("setup")
                .executes(ctx -> showMenu(ctx.getSource())));

        // Season length
        root.then(Commands.literal("length")
                .then(Commands.literal("7")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 7);  return showMenu(ctx.getSource()); }))
                .then(Commands.literal("14")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 14); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("20")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 20); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("28")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 28); return showMenu(ctx.getSource()); })));

        // Weather
        root.then(Commands.literal("weather")
                .then(Commands.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_weather", 1); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_weather", 0); return showMenu(ctx.getSource()); })));

        // Growth
        root.then(Commands.literal("growth")
                .then(Commands.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_growth", 1); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_growth", 0); return showMenu(ctx.getSource()); })));

        // Temperature
        root.then(Commands.literal("temperature")
                .then(Commands.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_temperature", 1); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_temperature", 0); return showMenu(ctx.getSource()); })));

        // Display
        root.then(Commands.literal("display")
                .then(Commands.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_display", 1); return showMenu(ctx.getSource()); }))
                .then(Commands.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_display", 0); return showMenu(ctx.getSource()); })));

        // Start
        root.then(Commands.literal("start")
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    source.getServer().getCommands().performPrefixedCommand(source, "function seasons:commands/start");
                    return showMenu(source);
                }));

        // Reset
        root.then(Commands.literal("reset")
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    source.getServer().getCommands().performPrefixedCommand(source, "function seasons:commands/reset_internal");
                    return showMenu(source);
                }));

        dispatcher.register(root);
    }

    // ── Score helpers ────────────────────────────────────────────────────────

    private static void setScore(MinecraftServer server, String playerName, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        Objective obj = scoreboard.getObjective("seasons_config");
        if (obj != null) {
            scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), obj).set(value);
        }
    }

    private static int getScore(Scoreboard scoreboard, String playerName, int defaultValue) {
        Objective obj = scoreboard.getObjective("seasons_config");
        if (obj == null) return defaultValue;
        var score = scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly(playerName), obj);
        return score != null ? score.value() : defaultValue;
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private static int showMenu(CommandSourceStack source) {
        Scoreboard scoreboard = source.getServer().getScoreboard();

        int seasonLength    = getScore(scoreboard, "#season_length", 20);
        boolean weather     = getScore(scoreboard, "#enable_weather", 1) == 1;
        boolean growth      = getScore(scoreboard, "#enable_growth", 1) == 1;
        boolean temp        = getScore(scoreboard, "#enable_temperature", 1) == 1;
        boolean display     = getScore(scoreboard, "#enable_display", 1) == 1;
        boolean initialized = getScore(scoreboard, "#seasons_initialized", 0) == 1;

        MutableComponent menu = Component.empty();

        menu.append(Component.literal("══════════════════════════════════\n").withStyle(ChatFormatting.GOLD));
        menu.append(Component.literal("        ⚙  SEASONS SETUP\n").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
        menu.append(Component.literal("══════════════════════════════════\n").withStyle(ChatFormatting.GOLD));
        menu.append(Component.literal("\n"));

        // Season length
        menu.append(Component.literal("  Season Length  ").withStyle(ChatFormatting.GRAY));
        menu.append(lengthButton(7,  seasonLength));
        menu.append(Component.literal("  "));
        menu.append(lengthButton(14, seasonLength));
        menu.append(Component.literal("  "));
        menu.append(lengthButton(20, seasonLength));
        menu.append(Component.literal("  "));
        menu.append(lengthButton(28, seasonLength));
        menu.append(Component.literal("\n"));
        menu.append(Component.literal("  Currently: ").withStyle(ChatFormatting.DARK_GRAY));
        menu.append(Component.literal(seasonLength + " days per season\n").withStyle(ChatFormatting.WHITE));
        menu.append(Component.literal("\n"));

        // Feature toggles
        menu.append(featureRow("  Weather Effects  ", weather,   "/seasons weather on",     "/seasons weather off"));
        menu.append(featureRow("  Growth Effects   ", growth,    "/seasons growth on",      "/seasons growth off"));
        menu.append(featureRow("  Temperature      ", temp,      "/seasons temperature on", "/seasons temperature off"));
        menu.append(featureRow("  Season Display   ", display,   "/seasons display on",     "/seasons display off"));
        menu.append(Component.literal("\n"));

        // Start / Reset
        if (!initialized) {
            menu.append(Component.literal("  "));
            menu.append(Component.literal("[ ▶  START SEASONS ]").withStyle(s -> s
                    .withColor(ChatFormatting.GREEN).withBold(true)
                    .withClickEvent(new ClickEvent.RunCommand("/seasons start"))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Save settings and start the seasons system")))
            ));
        } else {
            menu.append(Component.literal("  ✔ Seasons is running  ").withStyle(ChatFormatting.GREEN));
            menu.append(Component.literal("[ ⚠ Reset to Spring Day 1 ]").withStyle(s -> s
                    .withColor(ChatFormatting.DARK_RED)
                    .withClickEvent(new ClickEvent.RunCommand("/seasons reset"))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Reset season progression to Spring, Day 1")))
            ));
        }

        menu.append(Component.literal("\n"));
        menu.append(Component.literal("══════════════════════════════════").withStyle(ChatFormatting.GOLD));

        final MutableComponent finalMenu = menu;
        source.sendSuccess(() -> finalMenu, false);
        return 1;
    }

    private static MutableComponent lengthButton(int days, int current) {
        boolean active = (current == days);
        return Component.literal("[ " + days + "d ]").withStyle(s -> s
                .withColor(active ? ChatFormatting.GREEN : ChatFormatting.YELLOW)
                .withBold(active)
                .withClickEvent(new ClickEvent.RunCommand("/seasons length " + days))
                .withHoverEvent(new HoverEvent.ShowText(Component.literal(days + " days per season")))
        );
    }

    private static MutableComponent featureRow(String label, boolean enabled, String onCmd, String offCmd) {
        MutableComponent row = Component.literal(label).withStyle(ChatFormatting.GRAY);
        row.append(Component.literal("[✔ ON]").withStyle(s -> s
                .withColor(enabled ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY)
                .withBold(enabled)
                .withClickEvent(new ClickEvent.RunCommand(onCmd))
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Enable")))
        ));
        row.append(Component.literal("  "));
        row.append(Component.literal("[✘ OFF]").withStyle(s -> s
                .withColor(enabled ? ChatFormatting.DARK_GRAY : ChatFormatting.RED)
                .withBold(!enabled)
                .withClickEvent(new ClickEvent.RunCommand(offCmd))
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Disable")))
        ));
        row.append(Component.literal("\n"));
        return row;
    }
}
