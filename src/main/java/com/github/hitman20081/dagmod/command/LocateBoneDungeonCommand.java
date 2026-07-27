package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.item.BoneDungeonLocatorItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class LocateBoneDungeonCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("locatedungeon")
                .executes(LocateBoneDungeonCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by players."));
            return 0;
        }

        ServerLevel serverWorld = (ServerLevel) source.getLevel();
        BoneDungeonLocatorItem.locate(serverWorld, player);
        return 1;
    }
}
