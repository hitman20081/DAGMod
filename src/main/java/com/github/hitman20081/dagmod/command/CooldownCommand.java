package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mage.MageAbility;
import com.github.hitman20081.dagmod.class_system.mage.MageCooldownManager;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbility;
import com.github.hitman20081.dagmod.class_system.rogue.RogueCooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.WarriorAbility;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class CooldownCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("cooldown")
                .then(CommandManager.literal("info")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            player.sendMessage(Text.literal("=== Active Cooldowns ===")
                                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

                            boolean hasCooldowns = false;

                            switch (playerClass) {
                                case "Warrior" -> {
                                    Map<WarriorAbility, Integer> cooldowns = CooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<WarriorAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendMessage(Text.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .formatted(Formatting.RED)
                                                    .append(Text.literal(seconds + "s")
                                                            .formatted(Formatting.WHITE)), false);
                                        }
                                    }
                                }
                                case "Mage" -> {
                                    Map<MageAbility, Integer> cooldowns = MageCooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<MageAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendMessage(Text.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .formatted(Formatting.AQUA)
                                                    .append(Text.literal(seconds + "s")
                                                            .formatted(Formatting.WHITE)), false);
                                        }
                                    }
                                }
                                case "Rogue" -> {
                                    Map<RogueAbility, Integer> cooldowns = RogueCooldownManager.getActiveCooldowns(player);
                                    if (!cooldowns.isEmpty()) {
                                        hasCooldowns = true;
                                        for (Map.Entry<RogueAbility, Integer> entry : cooldowns.entrySet()) {
                                            int seconds = (int) Math.ceil(entry.getValue() / 20.0);
                                            player.sendMessage(Text.literal("  " + entry.getKey().getDisplayName() + ": ")
                                                    .formatted(Formatting.DARK_RED)
                                                    .append(Text.literal(seconds + "s")
                                                            .formatted(Formatting.WHITE)), false);
                                        }
                                    }
                                }
                                default -> {
                                    player.sendMessage(Text.literal("No class selected.")
                                            .formatted(Formatting.GRAY), false);
                                    return 1;
                                }
                            }

                            if (!hasCooldowns) {
                                player.sendMessage(Text.literal("No active cooldowns.")
                                        .formatted(Formatting.GRAY), false);
                            }

                            return 1;
                        })
                )
                .then(CommandManager.literal("clear")
                        .requires(source -> source.getPermissions().hasPermission(new net.minecraft.command.permission.Permission.Level(net.minecraft.command.permission.PermissionLevel.GAMEMASTERS)))
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            switch (playerClass) {
                                case "Warrior" -> CooldownManager.clearPlayerCooldowns(player.getUuid());
                                case "Mage" -> MageCooldownManager.clearPlayerCooldowns(player.getUuid());
                                case "Rogue" -> RogueCooldownManager.clearPlayerCooldowns(player.getUuid());
                                default -> {
                                    player.sendMessage(Text.literal("No class selected.")
                                            .formatted(Formatting.GRAY), false);
                                    return 1;
                                }
                            }

                            player.sendMessage(Text.literal("All cooldowns cleared!")
                                    .formatted(Formatting.GREEN), false);
                            return 1;
                        })
                )
        );
    }
}
