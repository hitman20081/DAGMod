package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class InfoCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dagmod")
                .then(CommandManager.literal("info")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            player.sendMessage(Text.literal("═══════════════════════════════")
                                    .formatted(Formatting.GOLD), false);
                            player.sendMessage(Text.literal("YOUR CHARACTER INFO")
                                    .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
                            player.sendMessage(Text.literal("═══════════════════════════════")
                                    .formatted(Formatting.GOLD), false);

                            if (race.equals("none")) {
                                player.sendMessage(Text.literal("Race: Not Selected")
                                        .formatted(Formatting.GRAY), false);
                            } else {
                                player.sendMessage(Text.literal("Race: " + race)
                                        .formatted(Formatting.AQUA), false);
                            }

                            if (playerClass.equals("none")) {
                                player.sendMessage(Text.literal("Class: Not Selected")
                                        .formatted(Formatting.GRAY), false);
                            } else {
                                player.sendMessage(Text.literal("Class: " + playerClass)
                                        .formatted(Formatting.GREEN), false);
                            }

                            // Show synergy if applicable
                            if (!race.equals("none") && !playerClass.equals("none")) {
                                String synergy = getSynergyDescription(race, playerClass);
                                if (synergy != null) {
                                    player.sendMessage(Text.empty(), false);
                                    player.sendMessage(Text.literal("✦ Synergy: " + synergy)
                                            .formatted(Formatting.YELLOW), false);
                                }
                            }

                            player.sendMessage(Text.literal("═══════════════════════════════")
                                    .formatted(Formatting.GOLD), false);

                            return 1;
                        })
                )
        );
    }

    private static String getSynergyDescription(String race, String playerClass) {
        String combo = race + " " + playerClass;
        return switch (combo) {
            case "Dwarf Warrior" -> "Resistance underground";
            case "Elf Rogue" -> "Invisibility in forests";
            case "Orc Warrior" -> "Berserker rage at low health";
            case "Human Mage" -> "Random regeneration";
            case "Dwarf Mage" -> "Fire resistance";
            case "Elf Mage" -> "Haste in forests";
            case "Orc Rogue" -> "Enhanced backstabs";
            case "Human Warrior" -> "Absorption on damage";
            case "Human Rogue" -> "Random jump boost";
            default -> null;
        };
    }
}