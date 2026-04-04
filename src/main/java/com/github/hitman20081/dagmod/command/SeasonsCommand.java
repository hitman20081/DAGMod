package com.github.hitman20081.dagmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SeasonsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        var root = CommandManager.literal("seasons")
                .requires(source -> source.getPermissions().hasPermission(
                        new Permission.Level(PermissionLevel.GAMEMASTERS)))
                .executes(ctx -> showMenu(ctx.getSource()));

        root.then(CommandManager.literal("setup")
                .executes(ctx -> showMenu(ctx.getSource())));

        // Season length
        root.then(CommandManager.literal("length")
                .then(CommandManager.literal("7")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 7);  return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("14")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 14); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("20")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 20); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("28")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#season_length", 28); return showMenu(ctx.getSource()); })));

        // Weather
        root.then(CommandManager.literal("weather")
                .then(CommandManager.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_weather", 1); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_weather", 0); return showMenu(ctx.getSource()); })));

        // Growth
        root.then(CommandManager.literal("growth")
                .then(CommandManager.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_growth", 1); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_growth", 0); return showMenu(ctx.getSource()); })));

        // Temperature
        root.then(CommandManager.literal("temperature")
                .then(CommandManager.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_temperature", 1); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_temperature", 0); return showMenu(ctx.getSource()); })));

        // Display
        root.then(CommandManager.literal("display")
                .then(CommandManager.literal("on")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_display", 1); return showMenu(ctx.getSource()); }))
                .then(CommandManager.literal("off")
                        .executes(ctx -> { setScore(ctx.getSource().getServer(), "#enable_display", 0); return showMenu(ctx.getSource()); })));

        // Start
        root.then(CommandManager.literal("start")
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    source.getServer().getCommandManager().parseAndExecute(source, "function seasons:commands/start");
                    return showMenu(source);
                }));

        // Reset
        root.then(CommandManager.literal("reset")
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    source.getServer().getCommandManager().parseAndExecute(source, "function seasons:commands/reset_internal");
                    return showMenu(source);
                }));

        dispatcher.register(root);
    }

    // ── Score helpers ────────────────────────────────────────────────────────

    private static void setScore(MinecraftServer server, String playerName, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective obj = scoreboard.getNullableObjective("seasons_config");
        if (obj != null) {
            scoreboard.getOrCreateScore(ScoreHolder.fromName(playerName), obj).setScore(value);
        }
    }

    private static int getScore(Scoreboard scoreboard, String playerName, int defaultValue) {
        ScoreboardObjective obj = scoreboard.getNullableObjective("seasons_config");
        if (obj == null) return defaultValue;
        var score = scoreboard.getScore(ScoreHolder.fromName(playerName), obj);
        return score != null ? score.getScore() : defaultValue;
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private static int showMenu(ServerCommandSource source) {
        Scoreboard scoreboard = source.getServer().getScoreboard();

        int seasonLength    = getScore(scoreboard, "#season_length", 20);
        boolean weather     = getScore(scoreboard, "#enable_weather", 1) == 1;
        boolean growth      = getScore(scoreboard, "#enable_growth", 1) == 1;
        boolean temp        = getScore(scoreboard, "#enable_temperature", 1) == 1;
        boolean display     = getScore(scoreboard, "#enable_display", 1) == 1;
        boolean initialized = getScore(scoreboard, "#seasons_initialized", 0) == 1;

        MutableText menu = Text.empty();

        menu.append(Text.literal("══════════════════════════════════\n").formatted(Formatting.GOLD));
        menu.append(Text.literal("        ⚙  SEASONS SETUP\n").formatted(Formatting.GOLD).formatted(Formatting.BOLD));
        menu.append(Text.literal("══════════════════════════════════\n").formatted(Formatting.GOLD));
        menu.append(Text.literal("\n"));

        // Season length
        menu.append(Text.literal("  Season Length  ").formatted(Formatting.GRAY));
        menu.append(lengthButton(7,  seasonLength));
        menu.append(Text.literal("  "));
        menu.append(lengthButton(14, seasonLength));
        menu.append(Text.literal("  "));
        menu.append(lengthButton(20, seasonLength));
        menu.append(Text.literal("  "));
        menu.append(lengthButton(28, seasonLength));
        menu.append(Text.literal("\n"));
        menu.append(Text.literal("  Currently: ").formatted(Formatting.DARK_GRAY));
        menu.append(Text.literal(seasonLength + " days per season\n").formatted(Formatting.WHITE));
        menu.append(Text.literal("\n"));

        // Feature toggles
        menu.append(featureRow("  Weather Effects  ", weather,   "/seasons weather on",     "/seasons weather off"));
        menu.append(featureRow("  Growth Effects   ", growth,    "/seasons growth on",      "/seasons growth off"));
        menu.append(featureRow("  Temperature      ", temp,      "/seasons temperature on", "/seasons temperature off"));
        menu.append(featureRow("  Season Display   ", display,   "/seasons display on",     "/seasons display off"));
        menu.append(Text.literal("\n"));

        // Start / Reset
        if (!initialized) {
            menu.append(Text.literal("  "));
            menu.append(Text.literal("[ ▶  START SEASONS ]").styled(s -> s
                    .withColor(Formatting.GREEN).withBold(true)
                    .withClickEvent(new ClickEvent.RunCommand("/seasons start"))
                    .withHoverEvent(new HoverEvent.ShowText(Text.literal("Save settings and start the seasons system")))
            ));
        } else {
            menu.append(Text.literal("  ✔ Seasons is running  ").formatted(Formatting.GREEN));
            menu.append(Text.literal("[ ⚠ Reset to Spring Day 1 ]").styled(s -> s
                    .withColor(Formatting.DARK_RED)
                    .withClickEvent(new ClickEvent.RunCommand("/seasons reset"))
                    .withHoverEvent(new HoverEvent.ShowText(Text.literal("Reset season progression to Spring, Day 1")))
            ));
        }

        menu.append(Text.literal("\n"));
        menu.append(Text.literal("══════════════════════════════════").formatted(Formatting.GOLD));

        final MutableText finalMenu = menu;
        source.sendFeedback(() -> finalMenu, false);
        return 1;
    }

    private static MutableText lengthButton(int days, int current) {
        boolean active = (current == days);
        return Text.literal("[ " + days + "d ]").styled(s -> s
                .withColor(active ? Formatting.GREEN : Formatting.YELLOW)
                .withBold(active)
                .withClickEvent(new ClickEvent.RunCommand("/seasons length " + days))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal(days + " days per season")))
        );
    }

    private static MutableText featureRow(String label, boolean enabled, String onCmd, String offCmd) {
        MutableText row = Text.literal(label).formatted(Formatting.GRAY);
        row.append(Text.literal("[✔ ON]").styled(s -> s
                .withColor(enabled ? Formatting.GREEN : Formatting.DARK_GRAY)
                .withBold(enabled)
                .withClickEvent(new ClickEvent.RunCommand(onCmd))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Enable")))
        ));
        row.append(Text.literal("  "));
        row.append(Text.literal("[✘ OFF]").styled(s -> s
                .withColor(enabled ? Formatting.DARK_GRAY : Formatting.RED)
                .withBold(!enabled)
                .withClickEvent(new ClickEvent.RunCommand(offCmd))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Disable")))
        ));
        row.append(Text.literal("\n"));
        return row;
    }
}
