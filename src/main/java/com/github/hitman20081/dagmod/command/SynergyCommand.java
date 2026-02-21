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

public class SynergyCommand {

    private static final String[][] SYNERGIES = {
            {"Dwarf", "Warrior", "Resistance underground"},
            {"Elf", "Rogue", "Invisibility in forests"},
            {"Orc", "Warrior", "Berserker rage at low health"},
            {"Human", "Mage", "Random regeneration"},
            {"Dwarf", "Mage", "Fire resistance"},
            {"Elf", "Mage", "Haste in forests"},
            {"Orc", "Rogue", "Enhanced backstabs"},
            {"Human", "Warrior", "Absorption on damage"},
            {"Human", "Rogue", "Random jump boost"},
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("synergy")
                .then(CommandManager.literal("list")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                            player.sendMessage(Text.literal("=== All Race+Class Synergies ===")
                                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

                            for (String[] synergy : SYNERGIES) {
                                player.sendMessage(Text.literal("  " + synergy[0] + " " + synergy[1] + ": ")
                                        .formatted(Formatting.AQUA)
                                        .append(Text.literal(synergy[2])
                                                .formatted(Formatting.WHITE)), false);
                            }

                            return 1;
                        })
                )
                .then(CommandManager.literal("check")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            player.sendMessage(Text.literal("=== Your Synergy ===")
                                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

                            if ("none".equals(race) || "none".equals(playerClass)) {
                                player.sendMessage(Text.literal("You need both a race and class to have a synergy.")
                                        .formatted(Formatting.GRAY), false);
                                return 1;
                            }

                            String synergy = getSynergyDescription(race, playerClass);
                            if (synergy != null) {
                                player.sendMessage(Text.literal(race + " " + playerClass + ": ")
                                        .formatted(Formatting.AQUA)
                                        .append(Text.literal(synergy)
                                                .formatted(Formatting.YELLOW)), false);
                            } else {
                                player.sendMessage(Text.literal(race + " " + playerClass + " has no synergy bonus.")
                                        .formatted(Formatting.GRAY), false);
                            }

                            return 1;
                        })
                )
        );
    }

    private static String getSynergyDescription(String race, String playerClass) {
        for (String[] synergy : SYNERGIES) {
            if (synergy[0].equals(race) && synergy[1].equals(playerClass)) {
                return synergy[2];
            }
        }
        return null;
    }
}
