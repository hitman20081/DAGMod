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

public class SynergyCommand {

    private static final String[][] SYNERGIES = {
            {"Dwarf", "Warrior", "Resistance underground"},
            {"Elf", "Rogue", "Invisibility in forests"},
            {"Orc", "Warrior", "Berserker rage at low health"},
            {"Human", "Mage", "RandomSource regeneration"},
            {"Dwarf", "Mage", "Fire resistance"},
            {"Elf", "Mage", "Haste in forests"},
            {"Orc", "Rogue", "Enhanced backstabs"},
            {"Human", "Warrior", "Absorption on damage"},
            {"Human", "Rogue", "RandomSource jump boost"},
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("synergy")
                .then(Commands.literal("list")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            player.sendSystemMessage(Component.literal("=== All Race+Class Synergies ===")
                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                            for (String[] synergy : SYNERGIES) {
                                player.sendSystemMessage(Component.literal("  " + synergy[0] + " " + synergy[1] + ": ")
                                        .withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal(synergy[2])
                                                .withStyle(ChatFormatting.WHITE)));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("check")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String race = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            player.sendSystemMessage(Component.literal("=== Your Synergy ===")
                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                            if ("none".equals(race) || "none".equals(playerClass)) {
                                player.sendSystemMessage(Component.literal("You need both a race and class to have a synergy.")
                                        .withStyle(ChatFormatting.GRAY));
                                return 1;
                            }

                            String synergy = getSynergyDescription(race, playerClass);
                            if (synergy != null) {
                                player.sendSystemMessage(Component.literal(race + " " + playerClass + ": ")
                                        .withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal(synergy)
                                                .withStyle(ChatFormatting.YELLOW)));
                            } else {
                                player.sendSystemMessage(Component.literal(race + " " + playerClass + " has no synergy bonus.")
                                        .withStyle(ChatFormatting.GRAY));
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
