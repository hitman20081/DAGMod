package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaceSelectionAltarBlock extends Block {

    // Store player races in memory
    private static final Map<UUID, String> playerRaces = new HashMap<>();

    public RaceSelectionAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        UUID playerId = player.getUuid();
        ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);

        // Check if player already has a race
        if (playerRaces.containsKey(playerId)) {
            String playerRace = playerRaces.get(playerId);
            player.sendMessage(Text.literal("You are already a " + playerRace + "!")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Your heritage cannot be changed.")
                    .formatted(Formatting.GRAY), false);
            return ActionResult.SUCCESS;
        }

        // Handle race token selection
        if (heldItem.isOf(ModItems.HUMAN_TOKEN)) {
            selectRace(player, world, pos, "Human", Formatting.WHITE);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        } else if (heldItem.isOf(ModItems.DWARF_TOKEN)) {
            selectRace(player, world, pos, "Dwarf", Formatting.GOLD);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        } else if (heldItem.isOf(ModItems.ELF_TOKEN)) {
            selectRace(player, world, pos, "Elf", Formatting.GREEN);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        } else if (heldItem.isOf(ModItems.ORC_TOKEN)) {
            selectRace(player, world, pos, "Orc", Formatting.DARK_RED);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        }

        // If no token held, give them the race tome
        if (heldItem.isEmpty() || !isRaceToken(heldItem)) {
            ItemStack tome = new ItemStack(ModItems.RACE_SELECTION_TOME);
            player.giveItemStack(tome);

            // Give all race tokens
            player.giveItemStack(new ItemStack(ModItems.HUMAN_TOKEN));
            player.giveItemStack(new ItemStack(ModItems.DWARF_TOKEN));
            player.giveItemStack(new ItemStack(ModItems.ELF_TOKEN));
            player.giveItemStack(new ItemStack(ModItems.ORC_TOKEN));

            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("You have been given the Race Selection Tome!")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Read it to learn about each race.")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);

            // Particle effect
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        20, 0.5, 0.5, 0.5, 0.1);
            }

            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        return ActionResult.SUCCESS;
    }

    private void selectRace(PlayerEntity player, World world, BlockPos pos,
                            String raceName, Formatting color) {
        UUID playerId = player.getUuid();

        // Store race in memory
        playerRaces.put(playerId, raceName);

        // Save to NBT
        if (player instanceof ServerPlayerEntity serverPlayer) {
            PlayerDataManager.savePlayerData(serverPlayer);
        }

        // Remove unused tokens from inventory
        removeUnusedTokens(player, raceName);

        // Remove the race selection tome
        removeRaceTome(player);

        // Apply race abilities immediately if server-side player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            RaceAbilityManager.applyRaceAbilities(serverPlayer);
        }

        // Send messages
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("RACE SELECTED: " + raceName.toUpperCase())
                .formatted(color).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Your heritage awakens...")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.empty(), false);

        // Initialize race-specific items
        initializeRace(player, raceName);

        // Celebration effects
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    50, 1.0, 1.0, 1.0, 0.2);
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    30, 0.8, 0.8, 0.8, 0.1);
        }

        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void removeUnusedTokens(PlayerEntity player, String selectedRace) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!selectedRace.equals("Human") && stack.isOf(ModItems.HUMAN_TOKEN)) {
                player.getInventory().removeStack(i);
            } else if (!selectedRace.equals("Dwarf") && stack.isOf(ModItems.DWARF_TOKEN)) {
                player.getInventory().removeStack(i);
            } else if (!selectedRace.equals("Elf") && stack.isOf(ModItems.ELF_TOKEN)) {
                player.getInventory().removeStack(i);
            } else if (!selectedRace.equals("Orc") && stack.isOf(ModItems.ORC_TOKEN)) {
                player.getInventory().removeStack(i);
            }
        }
    }

    private void removeRaceTome(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.RACE_SELECTION_TOME)) {
                player.getInventory().removeStack(i);
                break; // Only remove one
            }
        }
    }

    private void initializeRace(PlayerEntity player, String raceName) {
        switch (raceName.toLowerCase()) {
            case "human" -> initializeHuman(player);
            case "dwarf" -> initializeDwarf(player);
            case "elf" -> initializeElf(player);
            case "orc" -> initializeOrc(player);
        }
    }

    private void initializeHuman(PlayerEntity player) {
        // Humans are balanced - give basic tools with standard mining capabilities
        ItemStack pickaxe = new ItemStack(net.minecraft.item.Items.IRON_PICKAXE);
        ItemStack axe = new ItemStack(net.minecraft.item.Items.IRON_AXE);
        ItemStack fishingRod = new ItemStack(net.minecraft.item.Items.FISHING_ROD);

        player.giveItemStack(pickaxe);
        player.giveItemStack(axe);
        player.giveItemStack(fishingRod);
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.BREAD, 8));

        player.sendMessage(Text.literal("⚖ Human abilities unlocked!")
                .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Jack of all trades, master of none.")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("+25% experience gain from all sources!")
                .formatted(Formatting.GREEN), false);
    }

    private void initializeDwarf(PlayerEntity player) {
        // Dwarves get enhanced pickaxes with access to rare ores
        ItemStack dwarfPickaxe1 = new ItemStack(net.minecraft.item.Items.IRON_PICKAXE);
        dwarfPickaxe1.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Dwarven Mining Pick").formatted(Formatting.GOLD));

        ItemStack dwarfPickaxe2 = new ItemStack(net.minecraft.item.Items.IRON_PICKAXE);
        dwarfPickaxe2.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Dwarven Mining Pick").formatted(Formatting.GOLD));

        player.giveItemStack(dwarfPickaxe1);
        player.giveItemStack(dwarfPickaxe2);
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.TORCH, 32));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.COOKED_BEEF, 8));

        player.sendMessage(Text.literal("⛏ Dwarf abilities unlocked!")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Masters of stone and metal!")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Mining speed increased by 20%!")
                .formatted(Formatting.GREEN), false);
    }

    private void initializeElf(PlayerEntity player) {
        // Elves get enhanced axes and nature tools
        ItemStack elfAxe1 = new ItemStack(net.minecraft.item.Items.IRON_AXE);
        elfAxe1.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Elven Woodland Axe").formatted(Formatting.GREEN));

        ItemStack elfAxe2 = new ItemStack(net.minecraft.item.Items.IRON_AXE);
        elfAxe2.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Elven Woodland Axe").formatted(Formatting.GREEN));

        ItemStack elfBow = new ItemStack(net.minecraft.item.Items.BOW);
        elfBow.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Elven Hunting Bow").formatted(Formatting.GREEN));

        player.giveItemStack(elfAxe1);
        player.giveItemStack(elfAxe2);
        player.giveItemStack(elfBow);
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.ARROW, 16));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.APPLE, 8));

        player.sendMessage(Text.literal("🌿 Elf abilities unlocked!")
                .formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("One with nature and the forest!")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Enhanced woodcutting and mobility!")
                .formatted(Formatting.GREEN), false);
    }

    private void initializeOrc(PlayerEntity player) {
        // Orcs get combat and hunting tools
        ItemStack orcSword = new ItemStack(net.minecraft.item.Items.IRON_SWORD);
        orcSword.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Orcish War Blade").formatted(Formatting.DARK_RED));

        ItemStack orcBow = new ItemStack(net.minecraft.item.Items.BOW);
        orcBow.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Orcish Hunter's Bow").formatted(Formatting.DARK_RED));

        ItemStack fishingRod = new ItemStack(net.minecraft.item.Items.FISHING_ROD);
        fishingRod.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,
                Text.literal("Orcish Fishing Spear").formatted(Formatting.DARK_RED));

        player.giveItemStack(orcSword);
        player.giveItemStack(fishingRod);
        player.giveItemStack(orcBow);
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.ARROW, 16));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.COOKED_PORKCHOP, 8));

        player.sendMessage(Text.literal("💪 Orc abilities unlocked!")
                .formatted(Formatting.DARK_RED), false);
        player.sendMessage(Text.literal("Fierce hunters and warriors!")
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Enhanced combat and hunting prowess!")
                .formatted(Formatting.GREEN), false);
    }

    private boolean isRaceToken(ItemStack stack) {
        return stack.isOf(ModItems.HUMAN_TOKEN) ||
                stack.isOf(ModItems.DWARF_TOKEN) ||
                stack.isOf(ModItems.ELF_TOKEN) ||
                stack.isOf(ModItems.ORC_TOKEN);
    }

    public static String getPlayerRace(UUID playerId) {
        return playerRaces.getOrDefault(playerId, "none");
    }

    public static boolean resetPlayerRace(UUID playerId) {
        if (playerRaces.containsKey(playerId)) {
            playerRaces.remove(playerId);
            return true;
        }
        return false;
    }
    public static void setPlayerRace(UUID playerId, String race) {
        playerRaces.put(playerId, race);
    }
}