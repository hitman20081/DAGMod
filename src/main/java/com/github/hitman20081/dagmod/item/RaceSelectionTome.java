package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class RaceSelectionTome extends Item {

    public RaceSelectionTome(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            // Display race information
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("TOME OF ANCESTRAL HERITAGE")
                    .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.empty(), false);

            // Human
            player.sendMessage(Text.literal("HUMAN - The Balanced")
                    .formatted(Formatting.WHITE).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("• Jack of all trades")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("• Can gather all resources")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("• No special bonuses or penalties")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);

            // Dwarf
            player.sendMessage(Text.literal("DWARF - The Miner")
                    .formatted(Formatting.GOLD).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("• +20% mining speed")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• +1 heart (extra health)")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• -5% movement speed")
                    .formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("• Expert at mining rare ores")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);

            // Elf
            player.sendMessage(Text.literal("ELF - The Ranger")
                    .formatted(Formatting.GREEN).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("• +15% movement speed")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• +0.5 block reach")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Expert at woodcutting & hunting")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);

            // Orc
            player.sendMessage(Text.literal("ORC - The Warrior")
                    .formatted(Formatting.DARK_RED).formatted(Formatting.BOLD), false);
            player.sendMessage(Text.literal("• +15% melee attack damage")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• +2 hearts (extra health)")
                    .formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Expert at hunting & fishing")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);

            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("Four heritage tokens are in your inventory.")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Right-click the Altar of Heritage with your chosen token.")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
        }

        return ActionResult.SUCCESS;
    }
}