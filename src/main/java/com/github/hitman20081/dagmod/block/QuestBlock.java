package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.entity.RedDragonEntity;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestChain;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestBlock extends Block {

    // Track what menu state each player is in
    public static final Map<UUID, MenuState> playerMenuState = new ConcurrentHashMap<>();
    private static final Map<UUID, List<Quest>> playerAvailableQuests = new ConcurrentHashMap<>();
    private static final Map<UUID, List<Quest>> playerCompletedQuests = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> playerSelectedIndex = new ConcurrentHashMap<>();

    public enum MenuState {
        MAIN_MENU,
        BROWSE_QUESTS,      // NEW: Browse without accepting
        CONFIRM_ACCEPT,     // NEW: Confirm before accepting
        ACTIVE_QUESTS,
        TURN_IN_QUESTS
    }

    public QuestBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) { // Server side only
            ServerPlayer serverPlayer = (ServerPlayer) player;

            // Check if player has met Innkeeper Garrick (tutorial gate)
            if (!PlayerDataManager.hasMetGarrick(serverPlayer)) {
                player.sendSystemMessage(
                    Component.literal("🔒 This Quest Block is locked!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(
                    Component.literal("Find Innkeeper Garrick to learn how to use the quest system.").withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(
                    Component.literal("(He can usually be found at an inn or tavern)").withStyle(ChatFormatting.GRAY));
                return InteractionResult.CONSUME;
            }

            // Check if player has all 3 quest notes (tutorial completion)
            boolean hasNote1 = hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.GARRICKS_FIRST_NOTE);
            boolean hasNote2 = hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE);
            boolean hasNote3 = hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.GARRICKS_THIRD_NOTE);
            boolean hasQuestBook = hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.NOVICE_QUEST_BOOK) ||
                                   hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.APPRENTICE_QUEST_BOOK) ||
                                   hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.EXPERT_QUEST_BOOK) ||
                                   hasItemInInventory(player, com.github.hitman20081.dagmod.item.ModItems.MASTER_QUEST_TOME);

            // If player has all 3 notes, combine them into Quest Book
            if (hasNote1 && hasNote2 && hasNote3 && !hasQuestBook) {
                combineNotesIntoQuestBook(serverPlayer);
                return InteractionResult.CONSUME;
            }

            // If player doesn't have Quest Book and doesn't have all notes, block access
            if (!hasQuestBook) {
                player.sendSystemMessage(
                    Component.literal("📚 You need a Quest Book to use this!").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal(""));

                if (hasNote1 || hasNote2 || hasNote3) {
                    int noteCount = (hasNote1 ? 1 : 0) + (hasNote2 ? 1 : 0) + (hasNote3 ? 1 : 0);
                    player.sendSystemMessage(
                        Component.literal("You have " + noteCount + "/3 of Garrick's Quest Notes.").withStyle(ChatFormatting.GRAY));
                    player.sendSystemMessage(
                        Component.literal("Complete all of Garrick's tasks to get all 3 notes,").withStyle(ChatFormatting.GRAY));
                    player.sendSystemMessage(
                        Component.literal("then return here to combine them into a Quest Book.").withStyle(ChatFormatting.GRAY));
                } else {
                    player.sendSystemMessage(
                        Component.literal("Complete Garrick's 3 tasks to earn Quest Notes.").withStyle(ChatFormatting.GRAY));
                    player.sendSystemMessage(
                        Component.literal("Find Innkeeper Garrick to begin your tutorial!").withStyle(ChatFormatting.GRAY));
                }

                return InteractionResult.CONSUME;
            }

            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(player);
            UUID playerId = player.getUUID();

            // Update quest progress first
            questManager.updateQuestProgress(player);

            MenuState currentState = playerMenuState.getOrDefault(playerId, MenuState.MAIN_MENU);

            switch (currentState) {
                case MAIN_MENU -> showMainMenu(serverPlayer, questManager, playerData);
                case BROWSE_QUESTS -> showBrowseQuests(serverPlayer, questManager, playerData);
                case CONFIRM_ACCEPT -> showConfirmAccept(serverPlayer, questManager, playerData);
                case ACTIVE_QUESTS -> showActiveQuests(serverPlayer, questManager, playerData);
                case TURN_IN_QUESTS -> showTurnInQuests(serverPlayer, questManager, playerData);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void showMainMenu(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();

        player.sendSystemMessage(Component.literal("=== Quest Master ==="));
        player.sendSystemMessage(Component.literal("Welcome, adventurer! How can I help you today?"));
        player.sendSystemMessage(Component.literal(""));

        // Show quick stats
        player.sendSystemMessage(Component.literal("Your Quest Progress:"));
        player.sendSystemMessage(Component.literal("• Active Quests: " + playerData.getActiveQuestCount() + "/" + playerData.getMaxActiveQuests()));
        player.sendSystemMessage(Component.literal("• Completed Quests: " + playerData.getTotalQuestsCompleted()));
        player.sendSystemMessage(Component.literal("• Quest Book Tier: " + playerData.getQuestBookTier().getDisplayName()));
        player.sendSystemMessage(Component.literal(""));

        // Check for completed quests ready to turn in FIRST
        List<Quest> completedQuests = playerData.getActiveQuestsList().stream()
                .filter(Quest::isCompleted)
                .toList();

        if (!completedQuests.isEmpty()) {
            player.sendSystemMessage(Component.literal("✓ You have " + completedQuests.size() + " completed quest(s) ready to turn in!"));
            player.sendSystemMessage(Component.literal("Right-click to turn in quests!"));
            player.sendSystemMessage(Component.literal("==================="));

            // Set up for quest turn-in
            playerMenuState.put(playerId, MenuState.TURN_IN_QUESTS);
            playerCompletedQuests.put(playerId, completedQuests);
            playerSelectedIndex.put(playerId, 0);
            return; // Exit early to go to turn-in
        }

        // If no completed quests, show other options
        player.sendSystemMessage(Component.literal("Right-click again to:"));

        // FILTER: MAIN and SIDE quests only. CLASS quests are handled by the Class Trainer NPC.
        List<Quest> availableQuests = questManager.getAvailableQuests(player).stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.MAIN
                          || q.getCategory() == Quest.QuestCategory.SIDE)
                .toList();

        if (!availableQuests.isEmpty() && playerData.canAcceptMoreQuests()) {
            player.sendSystemMessage(Component.literal("→ Browse Available Quests (" + availableQuests.size() + " available)"));
            playerMenuState.put(playerId, MenuState.BROWSE_QUESTS);
            playerAvailableQuests.put(playerId, availableQuests);
            playerSelectedIndex.put(playerId, 0);
        } else if (!playerData.canAcceptMoreQuests()) {
            player.sendSystemMessage(Component.literal("→ View Active Quests (quest slots full)"));
            playerMenuState.put(playerId, MenuState.ACTIVE_QUESTS);
        } else if (!playerData.getActiveQuests().isEmpty()) {
            player.sendSystemMessage(Component.literal("→ View Active Quests"));
            playerMenuState.put(playerId, MenuState.ACTIVE_QUESTS);
        } else {
            player.sendSystemMessage(Component.literal("No quests available at your current level."));
        }

        // Quest book upgrade information
        showQuestBookUpgradeInfo(player, questManager, playerData);

        player.sendSystemMessage(Component.literal("==================="));
    }

    private void showBrowseQuests(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> availableQuests = playerAvailableQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (availableQuests == null || availableQuests.isEmpty()) {
            player.sendSystemMessage(Component.literal("No quests available for your current level."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        if (selectedIndex >= availableQuests.size()) {
            player.sendSystemMessage(Component.literal("=== End of Quest List ==="));
            player.sendSystemMessage(Component.literal("Returning to main menu..."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerSelectedIndex.put(playerId, 0);
            return;
        }

        Quest currentQuest = availableQuests.get(selectedIndex);

        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Quest " + (selectedIndex + 1) + "/" + availableQuests.size()).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));

        if (currentQuest.isClassRestricted()) {
            player.sendSystemMessage(Component.literal("[" + currentQuest.getRequiredClass() + " Only]").withStyle(ChatFormatting.AQUA));
        }
        if (currentQuest.isRaceRestricted()) {
            player.sendSystemMessage(Component.literal("[" + currentQuest.getRequiredRace() + " Only]").withStyle(ChatFormatting.AQUA));
        }

        Component questTitle = Component.literal("📜 " + currentQuest.getName())
                .append(Component.literal(" (" + currentQuest.getDifficulty().getDisplayName() + ")")
                        .withStyle(style -> style.withColor(currentQuest.getDifficulty().getColor())));
        player.sendSystemMessage(questTitle);
        player.sendSystemMessage(Component.literal(currentQuest.getDescription()).withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal("Objectives:").withStyle(ChatFormatting.YELLOW));
        for (var objective : currentQuest.getObjectives()) {
            player.sendSystemMessage(Component.literal("  • " + objective.getDescription()));
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Rewards:").withStyle(ChatFormatting.YELLOW));
        for (var reward : currentQuest.getRewards()) {
            player.sendSystemMessage(Component.literal("  ").append(reward.getDisplayText()));
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal(">> Right-click to: ACCEPT THIS QUEST <<").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(">> OR type: /quest skip <<").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));

        // Next click always goes to confirmation
        playerMenuState.put(playerId, MenuState.CONFIRM_ACCEPT);
    }

    private void showConfirmAccept(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> availableQuests = playerAvailableQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (availableQuests == null || availableQuests.isEmpty() || selectedIndex < 0 || selectedIndex >= availableQuests.size()) {
            player.sendSystemMessage(Component.literal("Invalid quest selection. Returning to menu."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest questToAccept = availableQuests.get(selectedIndex);

        player.sendSystemMessage(Component.literal("=== CONFIRM QUEST ACCEPTANCE ==="));
        player.sendSystemMessage(Component.literal("Quest: " + questToAccept.getName()));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Right-click to CONFIRM and accept this quest"));
        player.sendSystemMessage(Component.literal("==================="));

        // Accept the quest
        if (questManager.startQuest(player, questToAccept.getId())) {
            player.sendSystemMessage(Component.literal("✓ Quest accepted: " + questToAccept.getName()));
            player.sendSystemMessage(Component.literal("Check your active quests to track progress!"));

            // Spawn quest-specific entities on acceptance
            if (questToAccept.getId().equals("red_dragon_fury")) {
                spawnRedDragon(player);
            }
        } else {
            player.sendSystemMessage(Component.literal("✗ Failed to accept quest!"));
        }

        // Return to main menu
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
        playerSelectedIndex.put(playerId, 0);
    }

    private void showActiveQuests(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();

        player.sendSystemMessage(Component.literal("=== Your Active Quests ==="));

        if (playerData.getActiveQuests().isEmpty()) {
            player.sendSystemMessage(Component.literal("No active quests."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        for (Quest quest : playerData.getActiveQuests()) {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("📜 " + quest.getName() + " (" + quest.getDifficulty().getDisplayName() + ")"));

            for (var objective : quest.getObjectives()) {
                player.sendSystemMessage(Component.literal("  " + objective.getDisplayText().getString()));
            }

            if (quest.isCompleted()) {
                player.sendSystemMessage(Component.literal("  ✓ Ready to turn in!"));
            }
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Right-click again to return to main menu."));
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
    }

    private void showTurnInQuests(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> completedQuests = playerCompletedQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (completedQuests == null || completedQuests.isEmpty()) {
            // Refresh the completed quests list
            completedQuests = playerData.getActiveQuestsList().stream()
                    .filter(Quest::isCompleted)
                    .toList();
            playerCompletedQuests.put(playerId, completedQuests);

            if (completedQuests.isEmpty()) {
                player.sendSystemMessage(Component.literal("No completed quests to turn in."));
                playerMenuState.put(playerId, MenuState.MAIN_MENU);
                return;
            }
        }

        if (selectedIndex >= completedQuests.size()) {
            player.sendSystemMessage(Component.literal("All completed quests turned in!"));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerCompletedQuests.remove(playerId);
            return;
        }

        Quest questToTurnIn = completedQuests.get(selectedIndex);

        // Refresh quest progress BEFORE checking completion
        // This is critical for collect quests - ensures items are still in inventory
        for (var objective : questToTurnIn.getObjectives()) {
            objective.updateProgress(player);
        }

        player.sendSystemMessage(Component.literal("=== Turn In Quest " + (selectedIndex + 1) + "/" + completedQuests.size() + " ==="));
        player.sendSystemMessage(Component.literal("📜 " + questToTurnIn.getName()));
        player.sendSystemMessage(Component.literal(""));

        // Show what rewards they'll get
        player.sendSystemMessage(Component.literal("You will receive:"));
        for (var reward : questToTurnIn.getRewards()) {
            player.sendSystemMessage(reward.getDisplayText());
        }
        player.sendSystemMessage(Component.literal(""));

        // Check if quest is STILL completed after refresh
        if (!questToTurnIn.isCompleted()) {
            player.sendSystemMessage(Component.literal("✗ This quest is no longer completed!"));
            player.sendSystemMessage(Component.literal("(Collect quests require items in inventory at turn-in)").withStyle(net.minecraft.ChatFormatting.GRAY));
            List<Quest> mutableCompletedQuests = new ArrayList<>(completedQuests);
            mutableCompletedQuests.remove(selectedIndex);
            playerCompletedQuests.put(playerId, mutableCompletedQuests);
            return;
        }

        player.sendSystemMessage(Component.literal("Right-click to confirm turn-in..."));

        // Turn in the quest
        boolean success = questManager.turnInQuest(player, questToTurnIn.getId());

        if (success) {
            player.sendSystemMessage(Component.literal("✓ Quest completed successfully!"));
            player.sendSystemMessage(Component.literal("Check your inventory for rewards!"));

            List<Quest> mutableCompletedQuests = new ArrayList<>(completedQuests);
            mutableCompletedQuests.remove(selectedIndex);
            playerCompletedQuests.put(playerId, mutableCompletedQuests);
        } else {
            player.sendSystemMessage(Component.literal("✗ Failed to turn in quest. Check your inventory space!"));
            playerSelectedIndex.put(playerId, selectedIndex + 1);
        }
    }

    /**
     * Combine 3 Quest Notes into a Novice Quest Book
     */
    private void combineNotesIntoQuestBook(ServerPlayer player) {
        player.sendSystemMessage(
            Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(
            Component.literal("📚 QUEST NOTES DETECTED!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(
            Component.literal("You have all 3 of Garrick's Quest Notes!").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
            Component.literal("The Quest Block combines them into a proper Quest Book...").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal(""));

        // Remove the 3 notes from inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == com.github.hitman20081.dagmod.item.ModItems.GARRICKS_FIRST_NOTE ||
                stack.getItem() == com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE ||
                stack.getItem() == com.github.hitman20081.dagmod.item.ModItems.GARRICKS_THIRD_NOTE) {
                player.getInventory().removeItemNoUpdate(i);
            }
        }
        player.getInventory().setChanged();

        // Give Novice Quest Book
        player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.NOVICE_QUEST_BOOK));

        player.sendSystemMessage(
            Component.literal("✓ Received: Novice Quest Book!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(
            Component.literal("Congratulations! You've completed Garrick's tutorial!").withStyle(ChatFormatting.YELLOW));

        // Auto-start Garrick's Welcome as the first chain quest
        QuestManager questManager = QuestManager.getInstance();
        boolean questStarted = questManager.startQuest(player, "garricks_special_brew");
        if (questStarted) {
            questManager.savePlayerQuestData(player);
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(
                Component.literal("📜 First quest assigned: Garrick's Welcome").withStyle(ChatFormatting.AQUA));
            player.sendSystemMessage(
                Component.literal("   Right-click this Quest Block to track your progress.").withStyle(ChatFormatting.GRAY));
        } else {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(
                Component.literal("Right-click this Quest Block again to start questing!").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        }

        player.sendSystemMessage(
            Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
    }

    /**
     * Spawns a Red Dragon 100–200 blocks away from the player when the red_dragon_fury quest is accepted.
     */
    private void spawnRedDragon(ServerPlayer player) {
        ServerLevel world = (ServerLevel) player.level();

        double angle = world.getRandom().nextDouble() * 2 * Math.PI;
        double distance = 100 + world.getRandom().nextInt(101); // 100–200 blocks
        int spawnX = (int) (player.getX() + Math.cos(angle) * distance);
        int spawnZ = (int) (player.getZ() + Math.sin(angle) * distance);
        int spawnY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ) + 10;

        RedDragonEntity dragon = new RedDragonEntity(ModEntities.RED_DRAGON, world);
        dragon.snapTo(spawnX, spawnY, spawnZ, world.getRandom().nextFloat() * 360, 0);
        dragon.setTarget(player);
        world.addFreshEntity(dragon);

        player.sendSystemMessage(
            Component.literal("[!] A Red Dragon has been spotted nearby! Hunt it down.")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
    }

    /**
     * Helper method to check if player has a specific item in their inventory
     */
    private boolean hasItemInInventory(Player player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show information about quest book upgrades and which chains unlock them
     */
    private void showQuestBookUpgradeInfo(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        QuestData.QuestBookTier nextTier = playerData.getNextQuestBookTier();

        // Already at max tier
        if (nextTier == null) {
            return;
        }

        // Find chains that reward this tier
        boolean foundChain = false;
        for (QuestChain chain : questManager.getAllChains()) {
            if (chain.getRewardTier() == nextTier) {
                float progress = chain.getChainProgress(playerData);
                int completed = (int)(progress * chain.getChainLength());
                int total = chain.getChainLength();

                if (completed == total) {
                    // Chain completed, upgrade available
                    player.sendSystemMessage(Component.literal("⭐ Quest Book Upgrade (AVAILABLE!)").withStyle(ChatFormatting.GOLD));
                    player.sendSystemMessage(Component.literal("   Right-click to upgrade to: " + nextTier.getDisplayName()).withStyle(ChatFormatting.YELLOW));
                } else {
                    // Chain in progress
                    player.sendSystemMessage(Component.literal(""));
                    player.sendSystemMessage(Component.literal("📖 Next Quest Book: " + nextTier.getDisplayName()).withStyle(ChatFormatting.AQUA));
                    player.sendSystemMessage(Component.literal("   Complete: " + chain.getChainName()).withStyle(ChatFormatting.GRAY));
                    player.sendSystemMessage(Component.literal("   Progress: " + completed + "/" + total + " quests").withStyle(ChatFormatting.GRAY));
                }
                foundChain = true;
                break;
            }
        }

        // If no specific chain, just show the tier info
        if (!foundChain) {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("📖 Next Quest Book: " + nextTier.getDisplayName()).withStyle(ChatFormatting.AQUA));
            player.sendSystemMessage(Component.literal("   Complete quest chains to unlock!").withStyle(ChatFormatting.GRAY));
        }
    }
}