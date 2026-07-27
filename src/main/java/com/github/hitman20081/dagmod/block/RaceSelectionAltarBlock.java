package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaceSelectionAltarBlock extends Block {

    // Store player races in memory
    private static final Map<UUID, String> playerRaces = new HashMap<>();

    public RaceSelectionAltarBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
                                 Player player, BlockHitResult hit) {

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Save Hall location on first interaction
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerDataManager.saveHallLocation(player.level().getServer(), pos);
        }

        UUID playerId = player.getUUID();
        ItemStack heldItem = player.getMainHandItem();

        // ===== RESET OPTIONS =====

        // Check for Race Reset Crystal
        if (heldItem.getItem() == ModItems.RACE_RESET_CRYSTAL) {
            return handleRaceReset(player, heldItem, pos, world, "crystal");
        }

        // Check for Racial Rebirth Potion
        if (heldItem.getItem() == ModItems.POTION_OF_RACIAL_REBIRTH) {
            return handleRaceReset(player, heldItem, pos, world, "potion");
        }

        // Check for Character Reset Crystal (resets BOTH race and class)
        if (heldItem.getItem() == ModItems.CHARACTER_RESET_CRYSTAL) {
            return handleCharacterReset(player, heldItem, pos, world, "crystal");
        }

        // Check for Total Rebirth Potion (resets BOTH race and class)
        if (heldItem.getItem() == ModItems.POTION_OF_TOTAL_REBIRTH) {
            return handleCharacterReset(player, heldItem, pos, world, "potion");
        }

        // Check if player already has a race
        if (playerRaces.containsKey(playerId)) {
            String playerRace = playerRaces.get(playerId);
            player.sendSystemMessage(Component.literal("You are already a " + playerRace + "!")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Your heritage cannot be changed.")
                    .withStyle(ChatFormatting.GRAY));
            return InteractionResult.SUCCESS;
        }

        // Handle race token selection
        if (heldItem.getItem() == ModItems.HUMAN_TOKEN) {
            selectRace(player, world, pos, "Human", ChatFormatting.WHITE);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() == ModItems.DWARF_TOKEN) {
            selectRace(player, world, pos, "Dwarf", ChatFormatting.GOLD);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() == ModItems.ELF_TOKEN) {
            selectRace(player, world, pos, "Elf", ChatFormatting.GREEN);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() == ModItems.ORC_TOKEN) {
            selectRace(player, world, pos, "Orc", ChatFormatting.DARK_RED);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        }

        // If no token held, give them the race tome
        if (heldItem.isEmpty() || !isRaceToken(heldItem)) {
            ItemStack tome = new ItemStack(ModItems.RACE_SELECTION_TOME);
            player.addItem(tome);

            // Give all race tokens
            player.addItem(new ItemStack(ModItems.HUMAN_TOKEN));
            player.addItem(new ItemStack(ModItems.DWARF_TOKEN));
            player.addItem(new ItemStack(ModItems.ELF_TOKEN));
            player.addItem(new ItemStack(ModItems.ORC_TOKEN));

            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal("You have been given the Race Selection Tome!")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Read it to learn about each race.")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));

            // Particle effect
            if (world instanceof ServerLevel serverWorld) {
                serverWorld.sendParticles(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        20, 0.5, 0.5, 0.5, 0.1);
            }

            world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Applies race selection without requiring a block interaction.
     * Called by GarrickRegistryCommand for the guild registry flow.
     */
    public static void applyRaceSelection(ServerPlayer player, String raceName) {
        setPlayerRace(player.getUUID(), raceName);
        PlayerDataManager.savePlayerData(player);
        RaceAbilityManager.applyRaceAbilities(player);
        initializeRace(player, raceName);
    }

    private void selectRace(Player player, Level world, BlockPos pos,
                            String raceName, ChatFormatting color) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        applyRaceSelection(serverPlayer, raceName);

        // Remove unused tokens and tome (altar-specific cleanup)
        removeUnusedTokens(player, raceName);
        removeRaceTome(player);

        // Send messages
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("RACE SELECTED: " + raceName.toUpperCase())
                .withStyle(color).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Your heritage awakens...")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());

        // Initialize race-specific items — already called inside applyRaceSelection
        // (initializeRace already ran)

        // Celebration effects
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    50, 1.0, 1.0, 1.0, 0.2);
            serverWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    30, 0.8, 0.8, 0.8, 0.1);
        }

        world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private void removeUnusedTokens(Player player, String selectedRace) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!selectedRace.equals("Human") && stack.getItem() == ModItems.HUMAN_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            } else if (!selectedRace.equals("Dwarf") && stack.getItem() == ModItems.DWARF_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            } else if (!selectedRace.equals("Elf") && stack.getItem() == ModItems.ELF_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            } else if (!selectedRace.equals("Orc") && stack.getItem() == ModItems.ORC_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            }
        }
    }

    private void removeRaceTome(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == ModItems.RACE_SELECTION_TOME) {
                player.getInventory().removeItemNoUpdate(i);
                break; // Only remove one
            }
        }
    }

    private static void initializeRace(Player player, String raceName) {
        switch (raceName.toLowerCase()) {
            case "human" -> initializeHuman(player);
            case "dwarf" -> initializeDwarf(player);
            case "elf" -> initializeElf(player);
            case "orc" -> initializeOrc(player);
        }
    }

    private static void initializeHuman(Player player) {
        // Humans are balanced - give basic tools with standard mining capabilities
        ItemStack pickaxe = new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE);
        ItemStack axe = new ItemStack(net.minecraft.world.item.Items.IRON_AXE);
        ItemStack fishingRod = new ItemStack(net.minecraft.world.item.Items.FISHING_ROD);

        player.addItem(pickaxe);
        player.addItem(axe);
        player.addItem(fishingRod);
        player.addItem(new ItemStack(net.minecraft.world.item.Items.BREAD, 8));

        player.sendSystemMessage(Component.literal("⚖ Human abilities unlocked!")
                .withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal("Jack of all trades, master of none.")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("+25% experience gain from all sources!")
                .withStyle(ChatFormatting.GREEN));
    }

    private static void initializeDwarf(Player player) {
        // Dwarves get enhanced pickaxes with access to rare ores
        ItemStack dwarfPickaxe1 = new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE);
        dwarfPickaxe1.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Dwarven Mining Pick").withStyle(ChatFormatting.GOLD));

        ItemStack dwarfPickaxe2 = new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE);
        dwarfPickaxe2.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Dwarven Mining Pick").withStyle(ChatFormatting.GOLD));

        player.addItem(dwarfPickaxe1);
        player.addItem(dwarfPickaxe2);
        player.addItem(new ItemStack(net.minecraft.world.item.Items.TORCH, 32));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.COOKED_BEEF, 8));

        player.sendSystemMessage(Component.literal("⛏ Dwarf abilities unlocked!")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Masters of stone and metal!")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Mining speed increased by 20%!")
                .withStyle(ChatFormatting.GREEN));
    }

    private static void initializeElf(Player player) {
        // Elves get enhanced axes and nature tools
        ItemStack elfAxe1 = new ItemStack(net.minecraft.world.item.Items.IRON_AXE);
        elfAxe1.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Elven Woodland Axe").withStyle(ChatFormatting.GREEN));

        ItemStack elfAxe2 = new ItemStack(net.minecraft.world.item.Items.IRON_AXE);
        elfAxe2.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Elven Woodland Axe").withStyle(ChatFormatting.GREEN));

        ItemStack elfBow = new ItemStack(net.minecraft.world.item.Items.BOW);
        elfBow.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Elven Hunting Bow").withStyle(ChatFormatting.GREEN));

        player.addItem(elfAxe1);
        player.addItem(elfAxe2);
        player.addItem(elfBow);
        player.addItem(new ItemStack(net.minecraft.world.item.Items.ARROW, 16));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.APPLE, 8));

        player.sendSystemMessage(Component.literal("🌿 Elf abilities unlocked!")
                .withStyle(ChatFormatting.GREEN));
        player.sendSystemMessage(Component.literal("One with nature and the forest!")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Enhanced woodcutting and mobility!")
                .withStyle(ChatFormatting.GREEN));
    }

    private static void initializeOrc(Player player) {
        // Orcs get combat and hunting tools
        ItemStack orcSword = new ItemStack(net.minecraft.world.item.Items.IRON_SWORD);
        orcSword.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Orcish War Blade").withStyle(ChatFormatting.DARK_RED));

        ItemStack orcBow = new ItemStack(net.minecraft.world.item.Items.BOW);
        orcBow.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Orcish Hunter's Bow").withStyle(ChatFormatting.DARK_RED));

        ItemStack fishingRod = new ItemStack(net.minecraft.world.item.Items.FISHING_ROD);
        fishingRod.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal("Orcish Fishing Spear").withStyle(ChatFormatting.DARK_RED));

        player.addItem(orcSword);
        player.addItem(fishingRod);
        player.addItem(orcBow);
        player.addItem(new ItemStack(net.minecraft.world.item.Items.ARROW, 16));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.COOKED_PORKCHOP, 8));

        player.sendSystemMessage(Component.literal("💪 Orc abilities unlocked!")
                .withStyle(ChatFormatting.DARK_RED));
        player.sendSystemMessage(Component.literal("Fierce hunters and warriors!")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Enhanced combat and hunting prowess!")
                .withStyle(ChatFormatting.GREEN));
    }

    // ===== RESET HANDLER METHODS =====

    /**
     * Handle race reset (Race Reset Crystal or Racial Rebirth Potion)
     */
    private InteractionResult handleRaceReset(Player player, ItemStack item,
                                         BlockPos pos, Level world, String resetType) {
        UUID playerId = player.getUUID();

        if (!playerRaces.containsKey(playerId)) {
            player.sendSystemMessage(Component.literal("You don't have a race to reset!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        String oldRace = playerRaces.get(playerId);
        playerRaces.remove(playerId);

        // Consume the item
        if (item != null) {
            item.shrink(1);
        }

        // Give race tokens back
        player.addItem(new ItemStack(ModItems.HUMAN_TOKEN));
        player.addItem(new ItemStack(ModItems.DWARF_TOKEN));
        player.addItem(new ItemStack(ModItems.ELF_TOKEN));
        player.addItem(new ItemStack(ModItems.ORC_TOKEN));
        player.addItem(new ItemStack(ModItems.RACE_SELECTION_TOME));

        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("RACE RESET SUCCESSFUL")
                .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("You are no longer a " + oldRace)
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Your heritage has been cleansed.")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Choose your new path!")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());

        // Visual effects
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    50, 0.5, 0.5, 0.5, 0.5);
            serverWorld.sendParticles(ParticleTypes.SOUL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        world.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE,
                SoundSource.BLOCKS, 1.0f, 0.8f);

        return InteractionResult.SUCCESS;
    }

    /**
     * Handle character reset (Character Reset Crystal or Total Rebirth Potion)
     * Resets BOTH race and class
     */
    private InteractionResult handleCharacterReset(Player player, ItemStack item,
                                              BlockPos pos, Level world, String resetType) {
        UUID playerId = player.getUUID();

        if (!playerRaces.containsKey(playerId)) {
            player.sendSystemMessage(Component.literal("You don't have a race to reset!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        String oldRace = playerRaces.get(playerId);
        String oldClass = ClassSelectionAltarBlock.getPlayerClass(playerId);

        // Reset both race and class
        playerRaces.remove(playerId);
        ClassSelectionAltarBlock.resetPlayerClass(playerId);

        // Consume the item
        if (item != null) {
            item.shrink(1);
        }

        // Give both race and class tokens back
        player.addItem(new ItemStack(ModItems.HUMAN_TOKEN));
        player.addItem(new ItemStack(ModItems.DWARF_TOKEN));
        player.addItem(new ItemStack(ModItems.ELF_TOKEN));
        player.addItem(new ItemStack(ModItems.ORC_TOKEN));
        player.addItem(new ItemStack(ModItems.RACE_SELECTION_TOME));

        player.addItem(new ItemStack(ModItems.WARRIOR_TOKEN));
        player.addItem(new ItemStack(ModItems.MAGE_TOKEN));
        player.addItem(new ItemStack(ModItems.ROGUE_TOKEN));
        player.addItem(new ItemStack(ModItems.CLASS_SELECTION_TOME));

        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("CHARACTER RESET SUCCESSFUL")
                .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Race: " + oldRace + " → Not Selected")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Class: " + oldClass + " → Not Selected")
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Visit both altars to forge a new identity!")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());

        // Visual effects
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    60, 0.5, 0.5, 0.5, 0.5);
            serverWorld.sendParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    40, 0.5, 0.5, 0.5, 0.1);
            serverWorld.sendParticles(ParticleTypes.SOUL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        world.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT,
                SoundSource.BLOCKS, 1.0f, 0.6f);

        return InteractionResult.SUCCESS;
    }

    private boolean isRaceToken(ItemStack stack) {
        return stack.getItem() == ModItems.HUMAN_TOKEN ||
                stack.getItem() == ModItems.DWARF_TOKEN ||
                stack.getItem() == ModItems.ELF_TOKEN ||
                stack.getItem() == ModItems.ORC_TOKEN;
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