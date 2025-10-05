package com.github.hitman20081.dagmod.progression;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * Handles automatic XP rewards from gameplay events
 */
public class XPEventHandler {

    /**
     * Register all XP event handlers
     * Call this during mod initialization
     */
    public static void register() {
        // Block breaking (mining/gathering)
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                onBlockBroken(serverPlayer, state.getBlock());
            }
        });

        // Mob kills are handled via mixin (see below)
    }

    /**
     * Award XP for breaking blocks (mining/gathering)
     */
    private static void onBlockBroken(ServerPlayerEntity player, Block block) {
        int xp = 0;

        // Ores (primary mining XP)
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            xp = 5;
        } else if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            xp = 8;
        } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            xp = 10;
        } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            xp = 12;
        } else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
            xp = 12;
        } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            xp = 12;
        } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            xp = 25;
        } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            xp = 30;
        } else if (block == Blocks.ANCIENT_DEBRIS) {
            xp = 40;
        }

        // Logs (woodcutting) - check all log types explicitly
        else if (block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG ||
                block == Blocks.BIRCH_LOG || block == Blocks.JUNGLE_LOG ||
                block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
                block == Blocks.MANGROVE_LOG || block == Blocks.CHERRY_LOG ||
                block == Blocks.CRIMSON_STEM || block == Blocks.WARPED_STEM ||
                block == Blocks.STRIPPED_OAK_LOG || block == Blocks.STRIPPED_SPRUCE_LOG ||
                block == Blocks.STRIPPED_BIRCH_LOG || block == Blocks.STRIPPED_JUNGLE_LOG ||
                block == Blocks.STRIPPED_ACACIA_LOG || block == Blocks.STRIPPED_DARK_OAK_LOG ||
                block == Blocks.STRIPPED_MANGROVE_LOG || block == Blocks.STRIPPED_CHERRY_LOG ||
                block == Blocks.STRIPPED_CRIMSON_STEM || block == Blocks.STRIPPED_WARPED_STEM) {
            xp = 2;
        }

        // Crops (farming)
        else if (block == Blocks.WHEAT || block == Blocks.CARROTS ||
                block == Blocks.POTATOES || block == Blocks.BEETROOTS) {
            xp = 1;
        } else if (block == Blocks.NETHER_WART) {
            xp = 3;
        }

        // Award XP if any
        if (xp > 0) {
            ProgressionManager.addXP(player, xp);
        }
    }

    /**
     * Award XP for killing mobs
     * Call this from your mob death mixin/event
     */
    public static void onMobKilled(ServerPlayerEntity player, LivingEntity killed) {
        int xp = calculateMobXP(killed);

        if (xp > 0) {
            ProgressionManager.addXP(player, xp);
        }
    }

    /**
     * Calculate XP reward for killing a mob
     */
    private static int calculateMobXP(LivingEntity entity) {
        // Bosses
        if (entity instanceof EnderDragonEntity) {
            return 2000;
        } else if (entity instanceof WitherEntity) {
            return 1500;
        } else if (entity instanceof WardenEntity) {
            return 1000;
        } else if (entity instanceof ElderGuardianEntity) {
            return 500;
        }

        // Hostile mobs
        else if (entity instanceof ZombieEntity || entity instanceof SkeletonEntity) {
            return 15;
        } else if (entity instanceof CreeperEntity) {
            return 18;
        } else if (entity instanceof SpiderEntity) {
            return 12;
        } else if (entity instanceof EndermanEntity) {
            return 25;
        } else if (entity instanceof BlazeEntity) {
            return 30;
        } else if (entity instanceof WitherSkeletonEntity) {
            return 35;
        } else if (entity instanceof GhastEntity) {
            return 28;
        } else if (entity instanceof PhantomEntity) {
            return 20;
        } else if (entity instanceof ShulkerEntity) {
            return 40;
        } else if (entity instanceof GuardianEntity) {
            return 25;
        } else if (entity instanceof RavagerEntity) {
            return 45;
        } else if (entity instanceof PiglinBruteEntity) {
            return 35;
        } else if (entity instanceof PiglinEntity) {
            return 18;
        } else if (entity instanceof ZoglinEntity) {
            return 30;
        } else if (entity instanceof HoglinEntity) {
            return 22;
        } else if (entity instanceof VindicatorEntity || entity instanceof EvokerEntity) {
            return 30;
        } else if (entity instanceof PillagerEntity || entity instanceof WitchEntity) {
            return 20;
        }

        // Passive mobs (low/no XP to discourage farming)
        else if (entity instanceof AnimalEntity) {
            return 0; // No XP for killing passive animals
        }

        return 10; // Default for other hostile mobs
    }

    /**
     * Award XP for quest completion
     * Call this from your quest system
     */
    public static void onQuestCompleted(ServerPlayerEntity player, String difficulty) {
        int xp = switch (difficulty.toUpperCase()) {
            case "NOVICE" -> 200;
            case "APPRENTICE" -> 500;
            case "EXPERT" -> 1500;
            case "MASTER" -> 2500;
            default -> 100;
        };

        ProgressionManager.addXP(player, xp);
    }
}