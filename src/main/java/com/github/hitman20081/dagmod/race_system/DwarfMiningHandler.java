package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class DwarfMiningHandler {

    /**
     * Gives Dwarves a chance to get bonus ores when mining
     */
    public static void handleDwarfMining(ServerPlayer player, BlockState state, BlockPos pos, ServerLevel world) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if (!"Dwarf".equals(playerRace)) {
            return;
        }

        Block block = state.getBlock();
        RandomSource random = world.getRandom();

        // 15% chance for bonus drops when mining ores
        if (random.nextFloat() < 0.15f) {
            ItemStack bonusDrop = null;

            if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
                bonusDrop = new ItemStack(Items.COAL, 1);
            } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
                bonusDrop = new ItemStack(Items.RAW_IRON, 1);
            } else if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
                bonusDrop = new ItemStack(Items.RAW_COPPER, 1);
            } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
                bonusDrop = new ItemStack(Items.RAW_GOLD, 1);
            } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                bonusDrop = new ItemStack(Items.DIAMOND, 1);
            } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
                bonusDrop = new ItemStack(Items.EMERALD, 1);
            } else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
                bonusDrop = new ItemStack(Items.LAPIS_LAZULI, random.nextInt(3) + 1);
            } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
                bonusDrop = new ItemStack(Items.REDSTONE, random.nextInt(2) + 1);
            }

            if (bonusDrop != null) {
                Block.popResource(world, pos, bonusDrop);
                player.sendOverlayMessage(
                        Component.literal("⛏ Dwarven Mining Bonus!").withStyle(ChatFormatting.GOLD)
                );
            }
        }
    }
}