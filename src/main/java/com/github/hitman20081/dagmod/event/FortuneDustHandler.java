package com.github.hitman20081.dagmod.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
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

import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.item.ModItems;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

public class FortuneDustHandler {

    // Track remaining blocks for each player
    private static final Map<UUID, Integer> fortuneBlocksRemaining = new HashMap<>();
    private static final RandomSource random = RandomSource.create();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                UUID playerId = serverPlayer.getUUID();

                if (fortuneBlocksRemaining.containsKey(playerId)) {
                    // Apply Fortune III bonus drops
                    applyFortuneBonus((ServerLevel) world, state, pos, serverPlayer);

                    int remaining = fortuneBlocksRemaining.get(playerId);
                    remaining--;

                    if (remaining <= 0) {
                        fortuneBlocksRemaining.remove(playerId);
                        serverPlayer.sendOverlayMessage(Component.literal("💎 Fortune Dust expired! 💎")
                                .withStyle(ChatFormatting.GREEN));
                    } else {
                        fortuneBlocksRemaining.put(playerId, remaining);
                        serverPlayer.sendOverlayMessage(Component.literal("💎 " + remaining + " blocks remaining 💎")
                                .withStyle(ChatFormatting.GREEN));
                    }
                }
            }
        });
    }

    private static void applyFortuneBonus(ServerLevel world, BlockState state, BlockPos pos, ServerPlayer player) {
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
        } else if (block == ModBlocks.CITRINE_ORE || block == ModBlocks.DEEPSLATE_CITRINE_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_CITRINE, fortuneBonus);
        } else if (block == ModBlocks.RUBY_ORE || block == ModBlocks.DEEPSLATE_RUBY_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_RUBY, fortuneBonus);
        } else if (block == ModBlocks.SAPPHIRE_ORE || block == ModBlocks.DEEPSLATE_SAPPHIRE_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_SAPPHIRE, fortuneBonus);
        } else if (block == ModBlocks.TANZANITE_ORE || block == ModBlocks.DEEPSLATE_TANZANITE_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_TANZANITE, fortuneBonus);
        } else if (block == ModBlocks.ZIRCON_ORE || block == ModBlocks.DEEPSLATE_ZIRCON_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_ZIRCON, fortuneBonus);
        } else if (block == ModBlocks.PINK_GARNET_DEEPSLATE_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_PINK_GARNET, fortuneBonus);
        } else if (block == ModBlocks.MYTHRIL_ORE) {
            bonusDrop = new ItemStack(ModItems.RAW_MYTHRIL, fortuneBonus);
        }

        // Drop the bonus items
        if (!bonusDrop.isEmpty()) {
            Block.popResource(world, pos, bonusDrop);
        }
    }

    public static void activateFortuneDust(UUID playerId, int blocks) {
        fortuneBlocksRemaining.put(playerId, blocks);
    }

    public static boolean hasFortuneDust(UUID playerId) {
        return fortuneBlocksRemaining.containsKey(playerId);
    }
}