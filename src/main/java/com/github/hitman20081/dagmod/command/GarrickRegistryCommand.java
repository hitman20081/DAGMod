package com.github.hitman20081.dagmod.command;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GarrickRegistryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {

        dispatcher.register(Commands.literal("guildreg")
            .then(Commands.literal("race")
                .then(Commands.argument("race", StringArgumentType.word())
                    .executes(GarrickRegistryCommand::selectRace)))
            .then(Commands.literal("class")
                .then(Commands.argument("class", StringArgumentType.word())
                    .executes(GarrickRegistryCommand::selectClass))));
    }

    private static int selectRace(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return 0;

        if (!PlayerDataManager.hasCompletedAllTasks(player.getUUID())) {
            sendGarrick(player, "Complete your tasks first before registering!", ChatFormatting.RED);
            return 0;
        }

        if (!RaceSelectionAltarBlock.getPlayerRace(player.getUUID()).equals("none")) {
            sendGarrick(player, "Your heritage is already registered in the Guild Ledger!", ChatFormatting.GOLD);
            return 0;
        }

        String raceName = resolveRace(StringArgumentType.getString(context, "race"));
        if (raceName == null) {
            sendGarrick(player, "Unknown heritage. Choose: Human, Dwarf, Elf, or Orc.", ChatFormatting.RED);
            return 0;
        }

        RaceSelectionAltarBlock.applyRaceSelection(player, raceName);
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);

        player.sendSystemMessage(Component.empty());
        sendGarrick(player, "Registered! Heritage: " + raceName + ".", ChatFormatting.GREEN);
        player.sendSystemMessage(Component.empty());

        if (ClassSelectionAltarBlock.getPlayerClass(player.getUUID()).equals("none")) {
            InnkeeperGarrickNPC.showClassMenu(player);
        } else {
            InnkeeperGarrickNPC.showRegistrationComplete(player);
        }

        return 1;
    }

    private static int selectClass(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return 0;

        if (!PlayerDataManager.hasCompletedAllTasks(player.getUUID())) {
            sendGarrick(player, "Complete your tasks first before registering!", ChatFormatting.RED);
            return 0;
        }

        if (RaceSelectionAltarBlock.getPlayerRace(player.getUUID()).equals("none")) {
            sendGarrick(player, "Register your heritage first!", ChatFormatting.RED);
            InnkeeperGarrickNPC.showRaceMenu(player);
            return 0;
        }

        if (!ClassSelectionAltarBlock.getPlayerClass(player.getUUID()).equals("none")) {
            sendGarrick(player, "Your calling is already registered in the Guild Ledger!", ChatFormatting.GOLD);
            return 0;
        }

        String className = resolveClass(StringArgumentType.getString(context, "class"));
        if (className == null) {
            sendGarrick(player, "Unknown calling. Choose: Warrior, Mage, or Rogue.", ChatFormatting.RED);
            return 0;
        }

        ClassSelectionAltarBlock.applyClassSelection(player, className);
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);

        player.sendSystemMessage(Component.empty());
        sendGarrick(player, "Registered! Calling: " + className + ".", ChatFormatting.GREEN);
        player.sendSystemMessage(Component.empty());

        InnkeeperGarrickNPC.showRegistrationComplete(player);

        return 1;
    }

    private static String resolveRace(String input) {
        return switch (input.toLowerCase()) {
            case "human" -> "Human";
            case "dwarf" -> "Dwarf";
            case "elf" -> "Elf";
            case "orc" -> "Orc";
            default -> null;
        };
    }

    private static String resolveClass(String input) {
        return switch (input.toLowerCase()) {
            case "warrior" -> "Warrior";
            case "mage" -> "Mage";
            case "rogue" -> "Rogue";
            default -> null;
        };
    }

    private static void sendGarrick(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(
            Component.literal("[Innkeeper Garrick] ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(message).withStyle(color)));
    }
}
