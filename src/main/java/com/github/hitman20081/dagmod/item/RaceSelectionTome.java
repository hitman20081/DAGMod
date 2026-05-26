package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ModBlocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public class RaceSelectionTome extends Item {

    public RaceSelectionTome(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            // Display race information
            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal("TOME OF ANCESTRAL HERITAGE")
                    .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.empty());

            // Human
            player.sendSystemMessage(Component.literal("HUMAN - The Balanced")
                    .withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("• Jack of all trades")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("• Can gather all resources")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("• No special bonuses or penalties")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());

            // Dwarf
            player.sendSystemMessage(Component.literal("DWARF - The Miner")
                    .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("• +20% mining speed")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• +1 heart (extra health)")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• -5% movement speed")
                    .withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("• Expert at mining rare ores")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());

            // Elf
            player.sendSystemMessage(Component.literal("ELF - The Ranger")
                    .withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("• +15% movement speed")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• +0.5 block reach")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• Expert at woodcutting & hunting")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());

            // Orc
            player.sendSystemMessage(Component.literal("ORC - The Warrior")
                    .withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("• +15% melee attack damage")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• +2 hearts (extra health)")
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("• Expert at hunting & fishing")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());

            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal("Four heritage tokens are in your inventory.")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Right-click the Altar of Heritage with your chosen token.")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
        }

        return InteractionResult.SUCCESS;
    }
}