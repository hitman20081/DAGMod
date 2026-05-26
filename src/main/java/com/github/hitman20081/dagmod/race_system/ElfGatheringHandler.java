package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class ElfGatheringHandler {

    /**
     * Gives Elves bonuses when woodcutting and foraging
     */
    public static void handleElfGathering(ServerPlayer player, BlockState state, BlockPos pos, ServerLevel world) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if (!"Elf".equals(playerRace)) {
            return;
        }

        Block block = state.getBlock();
        RandomSource random = world.getRandom();

        // 20% chance for bonus drops from wood
        if (isLog(block) && random.nextFloat() < 0.20f) {
            ItemStack bonusDrop = new ItemStack(block.asItem(), 1);
            Block.popResource(world, pos, bonusDrop);
            player.sendOverlayMessage(
                    Component.literal("🌿 Elven Woodcutting Bonus!").withStyle(ChatFormatting.GREEN));
        }

        // 25% chance for bonus drops from leaves
        if (isLeaves(block) && random.nextFloat() < 0.25f) {
            ItemStack bonusDrop;
            if (random.nextFloat() < 0.5f) {
                bonusDrop = new ItemStack(Items.STICK, random.nextInt(2) + 1);
            } else {
                bonusDrop = new ItemStack(Items.APPLE, 1);
            }
            Block.popResource(world, pos, bonusDrop);
        }
    }

    private static boolean isLog(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG ||
                block == Blocks.SPRUCE_LOG || block == Blocks.JUNGLE_LOG ||
                block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
                block == Blocks.MANGROVE_LOG || block == Blocks.CHERRY_LOG ||
                block == Blocks.CRIMSON_STEM || block == Blocks.WARPED_STEM;
    }

    private static boolean isLeaves(Block block) {
        return block == Blocks.OAK_LEAVES || block == Blocks.BIRCH_LEAVES ||
                block == Blocks.SPRUCE_LEAVES || block == Blocks.JUNGLE_LEAVES ||
                block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES ||
                block == Blocks.MANGROVE_LEAVES || block == Blocks.CHERRY_LEAVES ||
                block == Blocks.AZALEA_LEAVES || block == Blocks.FLOWERING_AZALEA_LEAVES;
    }
}