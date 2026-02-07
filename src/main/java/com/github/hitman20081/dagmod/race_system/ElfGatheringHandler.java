package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ElfGatheringHandler {

    /**
     * Gives Elves bonuses when woodcutting and foraging
     */
    public static void handleElfGathering(ServerPlayerEntity player, BlockState state, BlockPos pos, ServerWorld world) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());

        if (!"Elf".equals(playerRace)) {
            return;
        }

        Block block = state.getBlock();
        Random random = world.getRandom();

        // 20% chance for bonus drops from wood
        if (isLog(block) && random.nextFloat() < 0.20f) {
            ItemStack bonusDrop = new ItemStack(block.asItem(), 1);
            Block.dropStack(world, pos, bonusDrop);
            player.sendMessage(
                    Text.literal("🌿 Elven Woodcutting Bonus!").formatted(Formatting.GREEN),
                    true
            );
        }

        // 25% chance for bonus drops from leaves
        if (isLeaves(block) && random.nextFloat() < 0.25f) {
            ItemStack bonusDrop;
            if (random.nextFloat() < 0.5f) {
                bonusDrop = new ItemStack(Items.STICK, random.nextInt(2) + 1);
            } else {
                bonusDrop = new ItemStack(Items.APPLE, 1);
            }
            Block.dropStack(world, pos, bonusDrop);
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