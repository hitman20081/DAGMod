package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class ResourceCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("resource")
                .then(Commands.literal("mana")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            if (!"Mage".equals(playerClass)) {
                                player.sendSystemMessage(Component.literal("You are not a Mage.")
                                        .withStyle(ChatFormatting.RED));
                                return 1;
                            }

                            ManaData data = ManaManager.getManaData(player);
                            player.sendSystemMessage(Component.literal("Mana: ")
                                    .withStyle(ChatFormatting.AQUA)
                                    .append(Component.literal((int) data.getCurrentMana() + "/" + data.getMaxMana())
                                            .withStyle(ChatFormatting.WHITE)));
                            return 1;
                        })
                        .then(Commands.literal("set")
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                                            if (!"Mage".equals(playerClass)) {
                                                player.sendSystemMessage(Component.literal("You are not a Mage.")
                                                        .withStyle(ChatFormatting.RED));
                                                return 1;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            ManaData data = ManaManager.getManaData(player);
                                            data.setMana(amount);
                                            ManaNetworking.sendManaUpdate(player, data.getCurrentMana(), data.getMaxMana());

                                            player.sendSystemMessage(Component.literal("Mana set to " + amount + ".")
                                                    .withStyle(ChatFormatting.GREEN));
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("energy")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                            if (!"Rogue".equalsIgnoreCase(playerClass)) {
                                player.sendSystemMessage(Component.literal("You are not a Rogue.")
                                        .withStyle(ChatFormatting.RED));
                                return 1;
                            }

                            int energy = EnergyManager.getEnergy(player);
                            int max = EnergyManager.getMaxEnergy(player);
                            player.sendSystemMessage(Component.literal("Energy: ")
                                    .withStyle(ChatFormatting.GREEN)
                                    .append(Component.literal(energy + "/" + max)
                                            .withStyle(ChatFormatting.WHITE)));
                            return 1;
                        })
                        .then(Commands.literal("set")
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                                            if (!"Rogue".equalsIgnoreCase(playerClass)) {
                                                player.sendSystemMessage(Component.literal("You are not a Rogue.")
                                                        .withStyle(ChatFormatting.RED));
                                                return 1;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            EnergyManager.setEnergy(player, amount);

                                            player.sendSystemMessage(Component.literal("Energy set to " + amount + ".")
                                                    .withStyle(ChatFormatting.GREEN));
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
