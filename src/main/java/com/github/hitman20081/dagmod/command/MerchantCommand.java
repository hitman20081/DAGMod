package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MerchantCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("merchant")
                .requires(source -> source.getPermissions().hasPermission(new net.minecraft.command.permission.Permission.Level(net.minecraft.command.permission.PermissionLevel.GAMEMASTERS)))
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            RotatingTradeManager manager = RotatingTradeManager.getInstance();

                            player.sendMessage(Text.literal("=== Merchant Rotation Status ===")
                                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

                            // Time until next rotation
                            player.sendMessage(Text.literal("Next rotation: ")
                                    .formatted(Formatting.YELLOW)
                                    .append(Text.literal(manager.getTimeUntilNextRotationFormatted())
                                            .formatted(Formatting.WHITE)), false);

                            player.sendMessage(Text.empty(), false);

                            // Current rotation index per type
                            for (MerchantType type : MerchantType.values()) {
                                int index = manager.getRotationIndex(type);
                                player.sendMessage(Text.literal("  " + type.getId() + ": ")
                                        .formatted(Formatting.AQUA)
                                        .append(Text.literal("rotation #" + index)
                                                .formatted(Formatting.WHITE)), false);
                            }

                            return 1;
                        })
                )
                .then(CommandManager.literal("rotate")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            RotatingTradeManager manager = RotatingTradeManager.getInstance();

                            manager.forceRotation();

                            player.sendMessage(Text.literal("Forced merchant trade rotation!")
                                    .formatted(Formatting.GREEN), false);
                            return 1;
                        })
                )
        );
    }
}
