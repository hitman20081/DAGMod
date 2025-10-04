package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

public class OrcHuntingHandler {

    /**
     * Gives Orcs bonus drops when hunting animals
     */
    public static void handleOrcHunting(ServerPlayerEntity player, LivingEntity killed) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());

        if (!"Orc".equals(playerRace)) {
            return;
        }

        Random random = player.getRandom();
        ServerWorld world = (ServerWorld) killed.getWorld();

        // 20% chance for bonus meat from animals
        if (random.nextFloat() < 0.20f) {
            ItemStack bonusDrop = null;

            if (killed instanceof CowEntity || killed instanceof MooshroomEntity) {
                bonusDrop = new ItemStack(Items.BEEF, random.nextInt(2) + 1);
            } else if (killed instanceof PigEntity) {
                bonusDrop = new ItemStack(Items.PORKCHOP, random.nextInt(2) + 1);
            } else if (killed instanceof ChickenEntity) {
                bonusDrop = new ItemStack(Items.CHICKEN, random.nextInt(2) + 1);
            } else if (killed instanceof SheepEntity) {
                bonusDrop = new ItemStack(Items.MUTTON, random.nextInt(2) + 1);
            } else if (killed instanceof RabbitEntity) {
                bonusDrop = new ItemStack(Items.RABBIT, random.nextInt(2) + 1);
            }

            if (bonusDrop != null) {
                killed.dropStack(world, bonusDrop);
                player.sendMessage(
                        Text.literal("💪 Orcish Hunter's Bounty!").formatted(Formatting.DARK_RED),
                        true
                );
            }
        }

        // 15% chance for bonus leather
        if ((killed instanceof CowEntity || killed instanceof HorseEntity) && random.nextFloat() < 0.15f) {
            killed.dropStack(world, new ItemStack(Items.LEATHER, 1));
        }
    }

    /**
     * Bonus fishing luck for Orcs
     */
    public static int modifyFishingLuck(ServerPlayerEntity player, int baseLuck) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());

        if ("Orc".equals(playerRace)) {
            return baseLuck + 1; // +1 luck of the sea equivalent
        }

        return baseLuck;
    }
}