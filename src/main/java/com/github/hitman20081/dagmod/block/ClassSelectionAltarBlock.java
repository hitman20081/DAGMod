package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;



import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassSelectionAltarBlock extends Block {

    // Store player classes in memory
    private static final Map<UUID, String> playerClasses = new HashMap<>();
    private static final Map<UUID, Integer> lastResetLevel = new HashMap<>();
    private static final int LEVEL_RESET_INTERVAL = 10; // Can reset every 10 levels

    public ClassSelectionAltarBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
                                 Player player, BlockHitResult hit) {

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        UUID playerId = player.getUUID();
        ItemStack heldItem = player.getMainHandItem();

        // OPTION 1: Check for Class Reset Crystal
        if (heldItem.getItem() == ModItems.CLASS_RESET_CRYSTAL) {
            return handleClassReset(player, heldItem, pos, world, "crystal");
        }

        // Check if player already has a class
        if (playerClasses.containsKey(playerId)) {
            String playerClass = playerClasses.get(playerId);

            // OPTION 3: Check for level-based reset eligibility
            if (canResetByLevel(player)) {
                player.sendSystemMessage(Component.literal("═══════════════════════════════")
                        .withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("You've reached a milestone!")
                        .withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("Current Class: " + playerClass)
                        .withStyle(ChatFormatting.WHITE));
                player.sendSystemMessage(Component.empty());
                player.sendSystemMessage(Component.literal("You can now reset your class for free!")
                        .withStyle(ChatFormatting.GREEN));
                player.sendSystemMessage(Component.literal("Sneak + Right-click to confirm reset")
                        .withStyle(ChatFormatting.AQUA));
                player.sendSystemMessage(Component.literal("Regular right-click to cancel")
                        .withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(Component.literal("═══════════════════════════════")
                        .withStyle(ChatFormatting.GOLD));

                // If they're sneaking, do the reset
                if (player.isShiftKeyDown()) {
                    return handleClassReset(player, null, pos, world, "level");
                }
                return InteractionResult.SUCCESS;
            }

            // They have a class and can't reset
            player.sendSystemMessage(Component.literal("You are already a " + playerClass + "!")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Your path has been chosen.")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("To change class, you need:")
                    .withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("• Class Reset Crystal, or")
                    .withStyle(ChatFormatting.AQUA));

            int currentLevel = getTotalQuestsCompleted(player);
            int lastReset = lastResetLevel.getOrDefault(playerId, 0);
            int levelsUntilReset = LEVEL_RESET_INTERVAL - ((currentLevel - lastReset) % LEVEL_RESET_INTERVAL);
            player.sendSystemMessage(Component.literal("• " + levelsUntilReset + " more quest completions")
                    .withStyle(ChatFormatting.AQUA));

            return InteractionResult.SUCCESS;
        }

        // Handle class token selection
        if (heldItem.getItem() == ModItems.WARRIOR_TOKEN) {
            selectClass(player, world, pos, "Warrior", ChatFormatting.RED);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() == ModItems.MAGE_TOKEN) {
            selectClass(player, world, pos, "Mage", ChatFormatting.AQUA);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        } else if (heldItem.getItem() == ModItems.ROGUE_TOKEN) {
            selectClass(player, world, pos, "Rogue", ChatFormatting.DARK_GREEN);
            heldItem.shrink(1);
            return InteractionResult.SUCCESS;
        }

        // If no token held, give them the tome
        if (heldItem.isEmpty() || !isClassToken(heldItem)) {
            ItemStack tome = new ItemStack(ModItems.CLASS_SELECTION_TOME);
            player.addItem(tome);

            player.sendSystemMessage(Component.literal("═══════════════════════════════")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.literal("You have been given the Class Selection Tome!")
                    .withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.literal("Read it to learn about each class.")
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

    private InteractionResult handleClassReset(Player player, ItemStack crystal,
                                          BlockPos pos, Level world, String resetType) {
        UUID playerId = player.getUUID();

        if (!playerClasses.containsKey(playerId)) {
            player.sendSystemMessage(Component.literal("You don't have a class to reset!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        String oldClass = playerClasses.get(playerId);
        playerClasses.remove(playerId);

        // Update last reset level if level-based reset
        if (resetType.equals("level")) {
            lastResetLevel.put(playerId, getTotalQuestsCompleted(player));
        }

        // Consume crystal if used
        if (crystal != null) {
            crystal.shrink(1);
        }

        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("CLASS RESET SUCCESSFUL")
                .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("You are no longer a " + oldClass)
                .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("Right-click the altar again to choose a new path!")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());

        // Visual effects
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    50, 0.5, 0.5, 0.5, 0.5);
        }

        world.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT,
                SoundSource.BLOCKS, 1.0f, 0.8f);

        return InteractionResult.SUCCESS;
    }

    private boolean canResetByLevel(Player player) {
        UUID playerId = player.getUUID();
        int currentLevel = getTotalQuestsCompleted(player);
        int lastReset = lastResetLevel.getOrDefault(playerId, 0);

        // Can reset every LEVEL_RESET_INTERVAL quests completed
        return (currentLevel - lastReset) >= LEVEL_RESET_INTERVAL;
    }

    private int getTotalQuestsCompleted(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(serverPlayer);
            return playerData.getTotalQuestsCompleted();
        }
        return 0;
    }

    private void selectClass(Player player, Level world, BlockPos pos,
                             String className, ChatFormatting color) {
        UUID playerId = player.getUUID();

        // Store class in memory
        playerClasses.put(playerId, className);

        // Save to NBT
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerDataManager.savePlayerData(serverPlayer);
        }

        // Remove the unused tokens from inventory
        removeUnusedTokens(player, className);

        // Apply class abilities immediately if server-side player
        if (player instanceof ServerPlayer serverPlayer) {
            ClassAbilityManager.applyClassAbilities(serverPlayer);
        }

        // Apply class abilities immediately if server-side player
        if (player instanceof ServerPlayer serverPlayer) {
            ClassAbilityManager.applyClassAbilities(serverPlayer);
            RaceAbilityManager.applyRaceAbilities(serverPlayer); // ADD THIS LINE
        }

        // Send messages
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("CLASS SELECTED: " + className.toUpperCase())
                .withStyle(color).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("═══════════════════════════════")
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Your journey begins...")
                .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());

        // Initialize class
        switch (className.toLowerCase()) {
            case "warrior" -> initializeWarrior(player);
            case "mage" -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    initializeMage(serverPlayer);
                }
            }
            case "rogue" -> initializeRogue(player);
        }

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

    /**
     * Applies class selection without requiring a block interaction.
     * Called by GarrickRegistryCommand for the guild registry flow.
     */
    public static void applyClassSelection(ServerPlayer player, String className) {
        setPlayerClass(player.getUUID(), className);
        PlayerDataManager.savePlayerData(player);
        ClassAbilityManager.applyClassAbilities(player);
        RaceAbilityManager.applyRaceAbilities(player);
        switch (className.toLowerCase()) {
            case "warrior" -> initializeWarrior(player);
            case "mage" -> initializeMage(player);
            case "rogue" -> initializeRogue(player);
        }
    }

    private void removeUnusedTokens(Player player, String selectedClass) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!selectedClass.equals("Warrior") && stack.getItem() == ModItems.WARRIOR_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            } else if (!selectedClass.equals("Mage") && stack.getItem() == ModItems.MAGE_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            } else if (!selectedClass.equals("Rogue") && stack.getItem() == ModItems.ROGUE_TOKEN) {
                player.getInventory().removeItemNoUpdate(i);
            }
        }
    }

    private static void initializeWarrior(Player player) {
        // Weapon & Shield
        player.addItem(new ItemStack(net.minecraft.world.item.Items.IRON_SWORD));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.SHIELD));

        // Full Chainmail Armor Set (Heavy armor for Warriors)
        player.addItem(new ItemStack(net.minecraft.world.item.Items.CHAINMAIL_HELMET));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.CHAINMAIL_LEGGINGS));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.CHAINMAIL_BOOTS));

        // Food & Potions
        player.addItem(new ItemStack(net.minecraft.world.item.Items.COOKED_BEEF, 32));

        ItemStack healingPotion1 = new ItemStack(net.minecraft.world.item.Items.POTION);
        healingPotion1.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.addItem(healingPotion1);

        ItemStack healingPotion2 = new ItemStack(net.minecraft.world.item.Items.POTION);
        healingPotion2.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.addItem(healingPotion2);

        ItemStack healingPotion3 = new ItemStack(net.minecraft.world.item.Items.POTION);
        healingPotion3.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.addItem(healingPotion3);

        player.sendSystemMessage(Component.literal("⚔ Warrior abilities unlocked!")
                .withStyle(ChatFormatting.RED));
        player.sendSystemMessage(Component.literal("Heavy armor equipped - ready for battle!")
                .withStyle(ChatFormatting.GRAY));
    }

    private static void initializeMage(ServerPlayer player) {
        // Weapon
        player.addItem(new ItemStack(ModItems.APPRENTICE_WAND));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.IRON_SWORD));

        // Full Leather Armor Set (Light armor for Mages - mobility)
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_CHESTPLATE));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_LEGGINGS));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_BOOTS));

        // Create Healing Potion
        ItemStack healingPotion1 = new ItemStack(net.minecraft.world.item.Items.POTION);
        healingPotion1.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.addItem(healingPotion1);

        ItemStack healingPotion2 = new ItemStack(net.minecraft.world.item.Items.POTION);
        healingPotion2.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.addItem(healingPotion2);

        // Create Regeneration Potion
        ItemStack regenPotion = new ItemStack(net.minecraft.world.item.Items.POTION);
        regenPotion.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.REGENERATION));
        player.addItem(regenPotion);

        // Create Protection I Book
        ItemStack protectionBook = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup =
                player.level().getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> protectionEnchantment = enchantmentLookup.getOrThrow(Enchantments.PROTECTION);
        ItemEnchantments.Mutable enchantmentsBuilder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantmentsBuilder.set(protectionEnchantment, 1);
        protectionBook.set(DataComponents.STORED_ENCHANTMENTS, enchantmentsBuilder.toImmutable());
        player.addItem(protectionBook);

        // Create Power I Book
        ItemStack powerBook = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
        Holder<Enchantment> powerEnchantment = enchantmentLookup.getOrThrow(Enchantments.POWER);
        ItemEnchantments.Mutable powerBuilder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        powerBuilder.set(powerEnchantment, 1);
        powerBook.set(DataComponents.STORED_ENCHANTMENTS, powerBuilder.toImmutable());
        player.addItem(powerBook);

        // Give lapis and bread
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LAPIS_LAZULI, 16));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.BREAD, 32));

        player.sendSystemMessage(Component.literal("✦ Mage abilities unlocked!")
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.literal("Light armor equipped - magic and mobility!")
                .withStyle(ChatFormatting.GRAY));
    }

    private static void initializeRogue(Player player) {
        // Weapons
        player.addItem(new ItemStack(net.minecraft.world.item.Items.IRON_SWORD));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.BOW));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.ARROW, 32));

        // Full Leather Armor Set (Light armor for Rogues - stealth & speed)
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_CHESTPLATE));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_LEGGINGS));
        player.addItem(new ItemStack(net.minecraft.world.item.Items.LEATHER_BOOTS));

        // Food
        player.addItem(new ItemStack(net.minecraft.world.item.Items.COOKED_CHICKEN, 32));

        player.sendSystemMessage(Component.literal("⚡ Rogue abilities unlocked!")
                .withStyle(ChatFormatting.DARK_GREEN));
        player.sendSystemMessage(Component.literal("Light armor equipped - move fast, strike hard!")
                .withStyle(ChatFormatting.GRAY));
    }

    private boolean isClassToken(ItemStack stack) {
        return stack.getItem() == ModItems.WARRIOR_TOKEN ||
                stack.getItem() == ModItems.MAGE_TOKEN ||
                stack.getItem() == ModItems.ROGUE_TOKEN;
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
    public static void setPlayerClass(UUID playerId, String playerClass) {
        playerClasses.put(playerId, playerClass);
    }
}