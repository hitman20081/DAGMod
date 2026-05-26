package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.Map;
import net.minecraft.server.permissions.Permissions;

public class CooldownCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("cooldown")
                .then(Commands.literal("info")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            player.sendSystemMessage(Component.literal("=== Active Cooldowns ===")
                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                            boolean hasCooldowns = false;

                            switch (playerClass) {
                                case "Warrior" -> {
                                    Map<WarriorAbility, Integer> cooldowns = CooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<WarriorAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendSystemMessage(Component.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .withStyle(ChatFormatting.RED)
                                                    .append(Component.literal(seconds + "s")
                                                            .withStyle(ChatFormatting.WHITE)));
                                        }
                                    }
                                }
                                case "Mage" -> {
                                    Map<MageAbility, Integer> cooldowns = MageCooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<MageAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendSystemMessage(Component.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .withStyle(ChatFormatting.AQUA)
                                                    .append(Component.literal(seconds + "s")
                                                            .withStyle(ChatFormatting.WHITE)));
                                        }
                                    }
                                }
                                case "Rogue" -> {
                                    Map<RogueAbility, Integer> cooldowns = RogueCooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<RogueAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendSystemMessage(Component.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .withStyle(ChatFormatting.DARK_RED)
                                                    .append(Component.literal(seconds + "s")
                                                            .withStyle(ChatFormatting.WHITE)));
                                        }
                                    }
                                }
                                default -> {
                                    player.sendSystemMessage(Component.literal("No class selected.")
                                            .withStyle(ChatFormatting.GRAY));
                                    return 1;
                                }
                            }

                            if (!hasCooldowns) {
                                player.sendSystemMessage(Component.literal("No active cooldowns.")
                                        .withStyle(ChatFormatting.GRAY));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("clear")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            switch (playerClass) {
                                case "Warrior" -> CooldownManager.clearPlayerCooldowns(player.getUUID());
                                case "Mage" -> MageCooldownManager.clearPlayerCooldowns(player.getUUID());
                                case "Rogue" -> RogueCooldownManager.clearPlayerCooldowns(player.getUUID());
                                default -> {
                                    player.sendSystemMessage(Component.literal("No class selected.")
                                            .withStyle(ChatFormatting.GRAY));
                                    return 1;
                                }
                            }

                            player.sendSystemMessage(Component.literal("All cooldowns cleared!")
                                    .withStyle(ChatFormatting.GREEN));
                            return 1;
                        })
                )
        );
    }
}
