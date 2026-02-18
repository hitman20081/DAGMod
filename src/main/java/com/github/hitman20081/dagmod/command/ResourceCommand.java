package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.mana.ManaData;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.mana.ManaNetworking;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ResourceCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("resource")
                .then(CommandManager.literal("mana")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            if (!"Mage".equals(playerClass)) {
                                player.sendMessage(Text.literal("You are not a Mage.")
                                        .formatted(Formatting.RED), false);
                                return 1;
                            }

                            ManaData data = ManaManager.getManaData(player);
                            player.sendMessage(Text.literal("Mana: ")
                                    .formatted(Formatting.AQUA)
                                    .append(Text.literal((int) data.getCurrentMana() + "/" + data.getMaxMana())
                                            .formatted(Formatting.WHITE)), false);
                            return 1;
                        })
                        .then(CommandManager.literal("set")
                                // TODO: Re-add OP permission check using Fabric Permissions API
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                                            if (!"Mage".equals(playerClass)) {
                                                player.sendMessage(Text.literal("You are not a Mage.")
                                                        .formatted(Formatting.RED), false);
                                                return 1;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            ManaData data = ManaManager.getManaData(player);
                                            data.setMana(amount);
                                            ManaNetworking.sendManaUpdate(player, data.getCurrentMana(), data.getMaxMana());

                                            player.sendMessage(Text.literal("Mana set to " + amount + ".")
                                                    .formatted(Formatting.GREEN), false);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("energy")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                            if (!"Rogue".equalsIgnoreCase(playerClass)) {
                                player.sendMessage(Text.literal("You are not a Rogue.")
                                        .formatted(Formatting.RED), false);
                                return 1;
                            }

                            int energy = EnergyManager.getEnergy(player);
                            int max = EnergyManager.getMaxEnergy();
                            player.sendMessage(Text.literal("Energy: ")
                                    .formatted(Formatting.GREEN)
                                    .append(Text.literal(energy + "/" + max)
                                            .formatted(Formatting.WHITE)), false);
                            return 1;
                        })
                        .then(CommandManager.literal("set")
                                // TODO: Re-add OP permission check using Fabric Permissions API
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                                            if (!"Rogue".equalsIgnoreCase(playerClass)) {
                                                player.sendMessage(Text.literal("You are not a Rogue.")
                                                        .formatted(Formatting.RED), false);
                                                return 1;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            EnergyManager.setEnergy(player, amount);

                                            player.sendMessage(Text.literal("Energy set to " + amount + ".")
                                                    .formatted(Formatting.GREEN), false);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
