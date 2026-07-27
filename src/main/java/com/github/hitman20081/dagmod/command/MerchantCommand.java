package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.Permissions;

public class MerchantCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("merchant")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("status")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            RotatingTradeManager manager = RotatingTradeManager.getInstance();

                            player.sendSystemMessage(Component.literal("=== Merchant Rotation Status ===")
                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                            // Time until next rotation
                            player.sendSystemMessage(Component.literal("Next rotation: ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal(manager.getTimeUntilNextRotationFormatted())
                                            .withStyle(ChatFormatting.WHITE)));

                            player.sendSystemMessage(Component.empty());

                            // Current rotation index per type
                            for (MerchantType type : MerchantType.values()) {
                                int index = manager.getRotationIndex(type);
                                player.sendSystemMessage(Component.literal("  " + type.getId() + ": ")
                                        .withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal("rotation #" + index)
                                                .withStyle(ChatFormatting.WHITE)));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("rotate")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            RotatingTradeManager manager = RotatingTradeManager.getInstance();

                            manager.forceRotation();

                            player.sendSystemMessage(Component.literal("Forced merchant trade rotation!")
                                    .withStyle(ChatFormatting.GREEN));
                            return 1;
                        })
                )
        );
    }
}
