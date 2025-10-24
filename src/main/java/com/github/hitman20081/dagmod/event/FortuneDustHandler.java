package com.github.hitman20081.dagmod.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FortuneDustHandler {

    // Track remaining blocks for each player
    private static final Map<UUID, Integer> fortuneBlocksRemaining = new HashMap<>();
    private static final Random random = new Random();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                UUID playerId = serverPlayer.getUuid();

                if (fortuneBlocksRemaining.containsKey(playerId)) {
                    // Apply Fortune III bonus drops
                    applyFortuneBonus((ServerWorld) world, state, pos, serverPlayer);

                    int remaining = fortuneBlocksRemaining.get(playerId);
                    remaining--;

                    if (remaining <= 0) {
                        fortuneBlocksRemaining.remove(playerId);
                        serverPlayer.sendMessage(Text.literal("💎 Fortune Dust expired! 💎")
                                .formatted(Formatting.GREEN), true);
                    } else {
                        fortuneBlocksRemaining.put(playerId, remaining);
                        serverPlayer.sendMessage(Text.literal("💎 " + remaining + " blocks remaining 💎")
                                .formatted(Formatting.GREEN), true);
                    }
                }
            }
        });
    }

    private static void applyFortuneBonus(ServerWorld world, BlockState state, BlockPos pos, ServerPlayerEntity player) {
        Block block = state.getBlock();
        ItemStack bonusDrop = ItemStack.EMPTY;
        int bonusAmount = 0;

        // Determine bonus drops based on block type (Fortune III typically gives 0-3 extra drops)
        int fortuneBonus = random.nextInt(4); // 0-3 extra drops

        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            bonusDrop = new ItemStack(Items.COAL, fortuneBonus);
        } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            bonusDrop = new ItemStack(Items.DIAMOND, fortuneBonus);
        } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            bonusDrop = new ItemStack(Items.EMERALD, fortuneBonus);
        } else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
            bonusDrop = new ItemStack(Items.LAPIS_LAZULI, fortuneBonus * 4); // Lapis drops more
        } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            bonusDrop = new ItemStack(Items.REDSTONE, fortuneBonus * 2); // Redstone drops more
        } else if (block == Blocks.NETHER_QUARTZ_ORE) {
            bonusDrop = new ItemStack(Items.QUARTZ, fortuneBonus);
        } else if (block == Blocks.NETHER_GOLD_ORE || block == Blocks.GILDED_BLACKSTONE) {
            bonusDrop = new ItemStack(Items.GOLD_NUGGET, fortuneBonus * 2);
        } else if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            bonusDrop = new ItemStack(Items.RAW_COPPER, fortuneBonus * 2);
        } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            bonusDrop = new ItemStack(Items.RAW_IRON, fortuneBonus);
        } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            bonusDrop = new ItemStack(Items.RAW_GOLD, fortuneBonus);
        }

        // Drop the bonus items
        if (!bonusDrop.isEmpty()) {
            Block.dropStack(world, pos, bonusDrop);
        }
    }

    public static void activateFortuneDust(UUID playerId, int blocks) {
        fortuneBlocksRemaining.put(playerId, blocks);
    }

    public static boolean hasFortuneDust(UUID playerId) {
        return fortuneBlocksRemaining.containsKey(playerId);
    }
}