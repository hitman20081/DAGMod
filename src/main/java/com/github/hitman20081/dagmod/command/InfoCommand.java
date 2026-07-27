package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;

public class InfoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("dagmod")
                .then(Commands.literal("info")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                                    .withStyle(ChatFormatting.GOLD));
                            player.sendSystemMessage(Component.literal("YOUR CHARACTER INFO")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
                            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                                    .withStyle(ChatFormatting.GOLD));

                            if (race.equals("none")) {
                                player.sendSystemMessage(Component.literal("Race: Not Selected")
                                        .withStyle(ChatFormatting.GRAY));
                            } else {
                                player.sendSystemMessage(Component.literal("Race: " + race)
                                        .withStyle(ChatFormatting.AQUA));
                            }

                            if (playerClass.equals("none")) {
                                player.sendSystemMessage(Component.literal("Class: Not Selected")
                                        .withStyle(ChatFormatting.GRAY));
                            } else {
                                player.sendSystemMessage(Component.literal("Class: " + playerClass)
                                        .withStyle(ChatFormatting.GREEN));
                            }

                            // Show synergy if applicable
                            if (!race.equals("none") && !playerClass.equals("none")) {
                                String synergy = getSynergyDescription(race, playerClass);
                                if (synergy != null) {
                                    player.sendSystemMessage(Component.empty());
                                    player.sendSystemMessage(Component.literal("✦ Synergy: " + synergy)
                                            .withStyle(ChatFormatting.YELLOW));
                                }
                            }

                            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                                    .withStyle(ChatFormatting.GOLD));

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
            case "Human Mage" -> "RandomSource regeneration";
            case "Dwarf Mage" -> "Fire resistance";
            case "Elf Mage" -> "Haste in forests";
            case "Orc Rogue" -> "Enhanced backstabs";
            case "Human Warrior" -> "Absorption on damage";
            case "Human Rogue" -> "RandomSource jump boost";
            default -> null;
        };
    }
}