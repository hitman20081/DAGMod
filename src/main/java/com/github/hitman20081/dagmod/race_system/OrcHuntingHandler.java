package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;

public class OrcHuntingHandler {

    /**
     * Gives Orcs bonus drops when hunting animals
     */
    public static void handleOrcHunting(ServerPlayer player, LivingEntity killed) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if (!"Orc".equals(playerRace)) {
            return;
        }

        RandomSource random = player.getRandom();
        ServerLevel world = (ServerLevel) killed.level();

        // 20% chance for bonus meat from animals
        if (random.nextFloat() < 0.20f) {
            ItemStack bonusDrop = null;

            if (killed instanceof Cow || killed instanceof MushroomCow) {
                bonusDrop = new ItemStack(Items.BEEF, random.nextInt(2) + 1);
            } else if (killed instanceof Pig) {
                bonusDrop = new ItemStack(Items.PORKCHOP, random.nextInt(2) + 1);
            } else if (killed instanceof Chicken) {
                bonusDrop = new ItemStack(Items.CHICKEN, random.nextInt(2) + 1);
            } else if (killed instanceof Sheep) {
                bonusDrop = new ItemStack(Items.MUTTON, random.nextInt(2) + 1);
            } else if (killed instanceof Rabbit) {
                bonusDrop = new ItemStack(Items.RABBIT, random.nextInt(2) + 1);
            }

            if (bonusDrop != null) {
                killed.spawnAtLocation(world, bonusDrop);
                player.sendOverlayMessage(
                        Component.literal("💪 Orcish Hunter's Bounty!").withStyle(ChatFormatting.DARK_RED));
            }
        }

        // 15% chance for bonus leather
        if ((killed instanceof Cow || killed instanceof Horse) && random.nextFloat() < 0.15f) {
            killed.spawnAtLocation(world, new ItemStack(Items.LEATHER, 1));
        }
    }

    /**
     * Bonus fishing luck for Orcs
     */
    public static int modifyFishingLuck(ServerPlayer player, int baseLuck) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

        if ("Orc".equals(playerRace)) {
            return baseLuck + 1; // +1 luck of the sea equivalent
        }

        return baseLuck;
    }
}