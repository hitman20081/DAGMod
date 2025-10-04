package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registry;



import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassSelectionAltarBlock extends Block {

    // Store player classes in memory
    private static final Map<UUID, String> playerClasses = new HashMap<>();
    private static final Map<UUID, Integer> lastResetLevel = new HashMap<>();
    private static final int LEVEL_RESET_INTERVAL = 10; // Can reset every 10 levels

    public ClassSelectionAltarBlock(Settings settings) {
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

        // OPTION 1: Check for Class Reset Crystal
        if (heldItem.isOf(ModItems.CLASS_RESET_CRYSTAL)) {
            return handleClassReset(player, heldItem, pos, world, "crystal");
        }

        // Check if player already has a class
        if (playerClasses.containsKey(playerId)) {
            String playerClass = playerClasses.get(playerId);

            // OPTION 3: Check for level-based reset eligibility
            if (canResetByLevel(player)) {
                player.sendMessage(Text.literal("═══════════════════════════════")
                        .formatted(Formatting.GOLD), false);
                player.sendMessage(Text.literal("You've reached a milestone!")
                        .formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Current Class: " + playerClass)
                        .formatted(Formatting.WHITE), false);
                player.sendMessage(Text.empty(), false);
                player.sendMessage(Text.literal("You can now reset your class for free!")
                        .formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Sneak + Right-click to confirm reset")
                        .formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Regular right-click to cancel")
                        .formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal("═══════════════════════════════")
                        .formatted(Formatting.GOLD), false);

                // If they're sneaking, do the reset
                if (player.isSneaking()) {
                    return handleClassReset(player, null, pos, world, "level");
                }
                return ActionResult.SUCCESS;
            }

            // They have a class and can't reset
            player.sendMessage(Text.literal("You are already a " + playerClass + "!")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Your path has been chosen.")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("To change class, you need:")
                    .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("• Class Reset Crystal, or")
                    .formatted(Formatting.AQUA), false);

            int currentLevel = getTotalQuestsCompleted(player);
            int lastReset = lastResetLevel.getOrDefault(playerId, 0);
            int levelsUntilReset = LEVEL_RESET_INTERVAL - ((currentLevel - lastReset) % LEVEL_RESET_INTERVAL);
            player.sendMessage(Text.literal("• " + levelsUntilReset + " more quest completions")
                    .formatted(Formatting.AQUA), false);

            return ActionResult.SUCCESS;
        }

        // Handle class token selection
        if (heldItem.isOf(ModItems.WARRIOR_TOKEN)) {
            selectClass(player, world, pos, "Warrior", Formatting.RED);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        } else if (heldItem.isOf(ModItems.MAGE_TOKEN)) {
            selectClass(player, world, pos, "Mage", Formatting.AQUA);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        } else if (heldItem.isOf(ModItems.ROGUE_TOKEN)) {
            selectClass(player, world, pos, "Rogue", Formatting.DARK_GREEN);
            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        }

        // If no token held, give them the tome
        if (heldItem.isEmpty() || !isClassToken(heldItem)) {
            ItemStack tome = new ItemStack(ModItems.CLASS_SELECTION_TOME);
            player.giveItemStack(tome);

            player.sendMessage(Text.literal("═══════════════════════════════")
                    .formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("You have been given the Class Selection Tome!")
                    .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Read it to learn about each class.")
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

    private ActionResult handleClassReset(PlayerEntity player, ItemStack crystal,
                                          BlockPos pos, World world, String resetType) {
        UUID playerId = player.getUuid();

        if (!playerClasses.containsKey(playerId)) {
            player.sendMessage(Text.literal("You don't have a class to reset!")
                    .formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        String oldClass = playerClasses.get(playerId);
        playerClasses.remove(playerId);

        // Update last reset level if level-based reset
        if (resetType.equals("level")) {
            lastResetLevel.put(playerId, getTotalQuestsCompleted(player));
        }

        // Consume crystal if used
        if (crystal != null) {
            crystal.decrement(1);
        }

        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("CLASS RESET SUCCESSFUL")
                .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("You are no longer a " + oldClass)
                .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Right-click the altar again to choose a new path!")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.empty(), false);

        // Visual effects
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    50, 0.5, 0.5, 0.5, 0.5);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT,
                SoundCategory.BLOCKS, 1.0f, 0.8f);

        return ActionResult.SUCCESS;
    }

    private boolean canResetByLevel(PlayerEntity player) {
        UUID playerId = player.getUuid();
        int currentLevel = getTotalQuestsCompleted(player);
        int lastReset = lastResetLevel.getOrDefault(playerId, 0);

        // Can reset every LEVEL_RESET_INTERVAL quests completed
        return (currentLevel - lastReset) >= LEVEL_RESET_INTERVAL;
    }

    private int getTotalQuestsCompleted(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(serverPlayer);
            return playerData.getTotalQuestsCompleted();
        }
        return 0;
    }

    private void selectClass(PlayerEntity player, World world, BlockPos pos,
                             String className, Formatting color) {
        UUID playerId = player.getUuid();

        // Store class in memory
        playerClasses.put(playerId, className);

        // Remove the unused tokens from inventory
        removeUnusedTokens(player, className);

        // Apply class abilities immediately if server-side player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ClassAbilityManager.applyClassAbilities(serverPlayer);
        }

        // Apply class abilities immediately if server-side player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ClassAbilityManager.applyClassAbilities(serverPlayer);
            RaceAbilityManager.applyRaceAbilities(serverPlayer); // ADD THIS LINE
        }

        // Send messages
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("CLASS SELECTED: " + className.toUpperCase())
                .formatted(color).formatted(Formatting.BOLD), false);
        player.sendMessage(Text.literal("═══════════════════════════════")
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Your journey begins...")
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.empty(), false);

        // Initialize class
        switch (className.toLowerCase()) {
            case "warrior" -> initializeWarrior(player);
            case "mage" -> {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    initializeMage(serverPlayer);
                }
            }
            case "rogue" -> initializeRogue(player);
        }

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

    private void removeUnusedTokens(PlayerEntity player, String selectedClass) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!selectedClass.equals("Warrior") && stack.isOf(ModItems.WARRIOR_TOKEN)) {
                player.getInventory().removeStack(i);
            } else if (!selectedClass.equals("Mage") && stack.isOf(ModItems.MAGE_TOKEN)) {
                player.getInventory().removeStack(i);
            } else if (!selectedClass.equals("Rogue") && stack.isOf(ModItems.ROGUE_TOKEN)) {
                player.getInventory().removeStack(i);
            }
        }
    }

    private void initializeWarrior(PlayerEntity player) {
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.IRON_SWORD));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.IRON_CHESTPLATE));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.SHIELD));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.COOKED_BEEF, 16));

        player.sendMessage(Text.literal("⚔ Warrior abilities unlocked!")
                .formatted(Formatting.RED), false);
    }

    private void initializeMage(ServerPlayerEntity player) {
        // Give wooden sword
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.WOODEN_SWORD));

        // Create Healing Potion
        ItemStack healingPotion1 = new ItemStack(net.minecraft.item.Items.POTION);
        healingPotion1.set(DataComponentTypes.POTION_CONTENTS,
                new PotionContentsComponent(Potions.HEALING));
        player.giveItemStack(healingPotion1);

        ItemStack healingPotion2 = new ItemStack(net.minecraft.item.Items.POTION);
        healingPotion2.set(DataComponentTypes.POTION_CONTENTS,
                new PotionContentsComponent(Potions.HEALING));
        player.giveItemStack(healingPotion2);

        // Create Regeneration Potion
        ItemStack regenPotion = new ItemStack(net.minecraft.item.Items.POTION);
        regenPotion.set(DataComponentTypes.POTION_CONTENTS,
                new PotionContentsComponent(Potions.REGENERATION));
        player.giveItemStack(regenPotion);

        // Create Protection I Book
        ItemStack protectionBook = new ItemStack(net.minecraft.item.Items.ENCHANTED_BOOK);
        Registry<Enchantment> enchantmentRegistry = player.getWorld()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT);

        Enchantment protectionEnch = enchantmentRegistry.get(Enchantments.PROTECTION);
        RegistryEntry<Enchantment> protectionEnchantment = enchantmentRegistry.getEntry(protectionEnch);

        ItemEnchantmentsComponent.Builder enchantmentsBuilder = new ItemEnchantmentsComponent.Builder(
                ItemEnchantmentsComponent.DEFAULT);
        enchantmentsBuilder.add(protectionEnchantment, 1);
        protectionBook.set(DataComponentTypes.STORED_ENCHANTMENTS, enchantmentsBuilder.build());
        player.giveItemStack(protectionBook);

        // Create Power I Book
        ItemStack powerBook = new ItemStack(net.minecraft.item.Items.ENCHANTED_BOOK);
        Enchantment powerEnch = enchantmentRegistry.get(Enchantments.POWER);
        RegistryEntry<Enchantment> powerEnchantment = enchantmentRegistry.getEntry(powerEnch);

        ItemEnchantmentsComponent.Builder powerBuilder = new ItemEnchantmentsComponent.Builder(
                ItemEnchantmentsComponent.DEFAULT);
        powerBuilder.add(powerEnchantment, 1);
        powerBook.set(DataComponentTypes.STORED_ENCHANTMENTS, powerBuilder.build());
        player.giveItemStack(powerBook);

        // Give lapis and bread
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.LAPIS_LAZULI, 16));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.BREAD, 16));

        player.sendMessage(Text.literal("✦ Mage abilities unlocked!")
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Your potions and enchanted books are ready!")
                .formatted(Formatting.GRAY), false);
    }

    private void initializeRogue(PlayerEntity player) {
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.IRON_SWORD));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.LEATHER_CHESTPLATE));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.BOW));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.ARROW, 32));
        player.giveItemStack(new ItemStack(net.minecraft.item.Items.COOKED_CHICKEN, 16));

        player.sendMessage(Text.literal("⚡ Rogue abilities unlocked!")
                .formatted(Formatting.DARK_GREEN), false);
    }

    private boolean isClassToken(ItemStack stack) {
        return stack.isOf(ModItems.WARRIOR_TOKEN) ||
                stack.isOf(ModItems.MAGE_TOKEN) ||
                stack.isOf(ModItems.ROGUE_TOKEN);
    }

    public static String getPlayerClass(UUID playerId) {
        return playerClasses.getOrDefault(playerId, "none");
    }

    // OPTION 4: Method for command to call
    public static boolean resetPlayerClass(UUID playerId) {
        if (playerClasses.containsKey(playerId)) {
            playerClasses.remove(playerId);
            lastResetLevel.remove(playerId);
            return true;
        }
        return false;
    }
}