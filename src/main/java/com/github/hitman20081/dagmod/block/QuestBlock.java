package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class QuestBlock extends Block {

    // Track what menu state each player is in
    private static final Map<UUID, MenuState> playerMenuState = new HashMap<>();
    private static final Map<UUID, List<Quest>> playerAvailableQuests = new HashMap<>();
    private static final Map<UUID, List<Quest>> playerCompletedQuests = new HashMap<>();
    private static final Map<UUID, Integer> playerSelectedIndex = new HashMap<>();

    public enum MenuState {
        MAIN_MENU,
        BROWSE_QUESTS,      // NEW: Browse without accepting
        CONFIRM_ACCEPT,     // NEW: Confirm before accepting
        ACTIVE_QUESTS,
        TURN_IN_QUESTS,
        UPGRADE_MENU
    }

    public QuestBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) { // Server side only
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(player);
            UUID playerId = player.getUuid();

            // Update quest progress first
            questManager.updateQuestProgress(player);

            MenuState currentState = playerMenuState.getOrDefault(playerId, MenuState.MAIN_MENU);

            switch (currentState) {
                case MAIN_MENU -> showMainMenu(serverPlayer, questManager, playerData);
                case BROWSE_QUESTS -> showBrowseQuests(serverPlayer, questManager, playerData);
                case CONFIRM_ACCEPT -> showConfirmAccept(serverPlayer, questManager, playerData);
                case ACTIVE_QUESTS -> showActiveQuests(serverPlayer, questManager, playerData);
                case TURN_IN_QUESTS -> showTurnInQuests(serverPlayer, questManager, playerData);
                case UPGRADE_MENU -> showUpgradeMenu(serverPlayer, questManager, playerData);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void showMainMenu(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();

        player.sendMessage(Text.literal("=== Quest Master ==="), false);
        player.sendMessage(Text.literal("Welcome, adventurer! How can I help you today?"), false);
        player.sendMessage(Text.literal(""), false);

        // Show quick stats
        player.sendMessage(Text.literal("Your Quest Progress:"), false);
        player.sendMessage(Text.literal("• Active Quests: " + playerData.getActiveQuestCount() + "/" + playerData.getMaxActiveQuests()), false);
        player.sendMessage(Text.literal("• Completed Quests: " + playerData.getTotalQuestsCompleted()), false);
        player.sendMessage(Text.literal("• Quest Book Tier: " + playerData.getQuestBookTier().getDisplayName()), false);
        player.sendMessage(Text.literal(""), false);

        // Check for completed quests ready to turn in FIRST
        List<Quest> completedQuests = playerData.getActiveQuestsList().stream()
                .filter(Quest::isCompleted)
                .toList();

        if (!completedQuests.isEmpty()) {
            player.sendMessage(Text.literal("✓ You have " + completedQuests.size() + " completed quest(s) ready to turn in!"), false);
            player.sendMessage(Text.literal("Right-click to turn in quests!"), false);
            player.sendMessage(Text.literal("==================="), false);

            // Set up for quest turn-in
            playerMenuState.put(playerId, MenuState.TURN_IN_QUESTS);
            playerCompletedQuests.put(playerId, completedQuests);
            playerSelectedIndex.put(playerId, 0);
            return; // Exit early to go to turn-in
        }

        // If no completed quests, show other options
        player.sendMessage(Text.literal("Right-click again to:"), false);

        List<Quest> availableQuests = questManager.getAvailableQuests(player).stream()
                .toList();

        if (!availableQuests.isEmpty() && playerData.canAcceptMoreQuests()) {
            player.sendMessage(Text.literal("→ Browse Available Quests (" + availableQuests.size() + " available)"), false);
            playerMenuState.put(playerId, MenuState.BROWSE_QUESTS);
            playerAvailableQuests.put(playerId, availableQuests);
            playerSelectedIndex.put(playerId, 0);
        } else if (!playerData.canAcceptMoreQuests()) {
            player.sendMessage(Text.literal("→ View Active Quests (quest slots full)"), false);
            playerMenuState.put(playerId, MenuState.ACTIVE_QUESTS);
        } else if (!playerData.getActiveQuests().isEmpty()) {
            player.sendMessage(Text.literal("→ View Active Quests"), false);
            playerMenuState.put(playerId, MenuState.ACTIVE_QUESTS);
        } else {
            player.sendMessage(Text.literal("No quests available at your current level."), false);
        }

        // Quest book upgrade
        if (playerData.canUpgradeQuestBook()) {
            player.sendMessage(Text.literal("⭐ Upgrade Quest Book (AVAILABLE!)"), false);
        }

        player.sendMessage(Text.literal("==================="), false);
    }

    private void showBrowseQuests(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> availableQuests = playerAvailableQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        // DEBUG
        player.sendMessage(Text.literal("DEBUG: showBrowseQuests - index=" + selectedIndex + ", sneaking=" + player.isSneaking()), false);

        if (availableQuests == null || availableQuests.isEmpty()) {
            player.sendMessage(Text.literal("No quests available for your current level."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        if (selectedIndex >= availableQuests.size()) {
            // Finished browsing, return to main menu
            player.sendMessage(Text.literal("=== End of Quest List ==="), false);
            player.sendMessage(Text.literal("Returning to main menu..."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerSelectedIndex.put(playerId, 0);
            return;
        }

        Quest currentQuest = availableQuests.get(selectedIndex);

        player.sendMessage(Text.literal("=== Quest " + (selectedIndex + 1) + "/" + availableQuests.size() + " ==="), false);

        // Show class requirement if it exists
        if (currentQuest.isClassRestricted()) {
            player.sendMessage(Text.literal("[" + currentQuest.getRequiredClass() + " Only]"), false);
        }

        Text questTitle = Text.literal("📜 " + currentQuest.getName())
                .append(Text.literal(" (" + currentQuest.getDifficulty().getDisplayName() + ")")
                        .styled(style -> style.withColor(currentQuest.getDifficulty().getColor())));
        player.sendMessage(questTitle, false);
        player.sendMessage(Text.literal(currentQuest.getDescription()), false);
        player.sendMessage(Text.literal(""), false);

        // Show objectives
        player.sendMessage(Text.literal("Objectives:"), false);
        for (var objective : currentQuest.getObjectives()) {
            player.sendMessage(Text.literal("• " + objective.getDescription()), false);
        }

        // Show rewards
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Rewards:"), false);
        for (var reward : currentQuest.getRewards()) {
            player.sendMessage(reward.getDisplayText(), false);
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click to VIEW NEXT quest"), false);
        player.sendMessage(Text.literal("Sneak + Right-click to ACCEPT this quest"), false);
        player.sendMessage(Text.literal("==================="), false);

        // Check if player is sneaking (shift + right click)
        if (player.isSneaking()) {
            // Player wants to accept this quest - go to confirmation
            playerMenuState.put(playerId, MenuState.CONFIRM_ACCEPT);
            return;
        }

        // Move to next quest for browsing
        playerSelectedIndex.put(playerId, selectedIndex + 1);
    }

    private void showConfirmAccept(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> availableQuests = playerAvailableQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.get(playerId) - 1; // Go back one since we incremented

        // DEBUG - Add these lines
        player.sendMessage(Text.literal("DEBUG: showConfirmAccept called"), false);
        player.sendMessage(Text.literal("DEBUG: selectedIndex after -1 = " + selectedIndex), false);
        player.sendMessage(Text.literal("DEBUG: availableQuests size = " + (availableQuests != null ? availableQuests.size() : "NULL")), false);

        if (selectedIndex < 0 || selectedIndex >= availableQuests.size()) {
            player.sendMessage(Text.literal("DEBUG: Index out of bounds! Returning to menu."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest questToAccept = availableQuests.get(selectedIndex);

        // DEBUG - Add this line
        player.sendMessage(Text.literal("DEBUG: Quest to accept = " + questToAccept.getId() + " (" + questToAccept.getName() + ")"), false);

        player.sendMessage(Text.literal("=== CONFIRM QUEST ACCEPTANCE ==="), false);
        player.sendMessage(Text.literal("Quest: " + questToAccept.getName()), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click to CONFIRM and accept this quest"), false);
        player.sendMessage(Text.literal("==================="), false);

        // Accept the quest
        if (questManager.startQuest(player, questToAccept.getId())) {
            player.sendMessage(Text.literal("✓ Quest accepted: " + questToAccept.getName()), false);
            player.sendMessage(Text.literal("Check your active quests to track progress!"), false);
        } else {
            player.sendMessage(Text.literal("✗ Failed to accept quest!"), false);
        }

        // Return to main menu
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
        playerSelectedIndex.put(playerId, 0);
    }

    private void showActiveQuests(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();

        player.sendMessage(Text.literal("=== Your Active Quests ==="), false);

        if (playerData.getActiveQuests().isEmpty()) {
            player.sendMessage(Text.literal("No active quests."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        for (Quest quest : playerData.getActiveQuests()) {
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("📜 " + quest.getName() + " (" + quest.getDifficulty().getDisplayName() + ")"), false);

            for (var objective : quest.getObjectives()) {
                player.sendMessage(Text.literal("  " + objective.getDisplayText().getString()), false);
            }

            if (quest.isCompleted()) {
                player.sendMessage(Text.literal("  ✓ Ready to turn in!"), false);
            }
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click again to return to main menu."), false);
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
    }

    private void showTurnInQuests(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> completedQuests = playerCompletedQuests.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (completedQuests == null || completedQuests.isEmpty()) {
            // Refresh the completed quests list
            completedQuests = playerData.getActiveQuestsList().stream()
                    .filter(Quest::isCompleted)
                    .toList();
            playerCompletedQuests.put(playerId, completedQuests);

            if (completedQuests.isEmpty()) {
                player.sendMessage(Text.literal("No completed quests to turn in."), false);
                playerMenuState.put(playerId, MenuState.MAIN_MENU);
                return;
            }
        }

        if (selectedIndex >= completedQuests.size()) {
            player.sendMessage(Text.literal("All completed quests turned in!"), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerCompletedQuests.remove(playerId);
            return;
        }

        Quest questToTurnIn = completedQuests.get(selectedIndex);

        player.sendMessage(Text.literal("=== Turn In Quest " + (selectedIndex + 1) + "/" + completedQuests.size() + " ==="), false);
        player.sendMessage(Text.literal("📜 " + questToTurnIn.getName()), false);
        player.sendMessage(Text.literal(""), false);

        // Show what rewards they'll get
        player.sendMessage(Text.literal("You will receive:"), false);
        for (var reward : questToTurnIn.getRewards()) {
            player.sendMessage(reward.getDisplayText(), false);
        }
        player.sendMessage(Text.literal(""), false);

        if (!questToTurnIn.isCompleted()) {
            player.sendMessage(Text.literal("✗ This quest is not yet completed!"), false);
            List<Quest> mutableCompletedQuests = new ArrayList<>(completedQuests);
            mutableCompletedQuests.remove(selectedIndex);
            playerCompletedQuests.put(playerId, mutableCompletedQuests);
            return;
        }

        player.sendMessage(Text.literal("Right-click to confirm turn-in..."), false);

        // Turn in the quest
        boolean success = questManager.turnInQuest(player, questToTurnIn.getId());

        if (success) {
            player.sendMessage(Text.literal("✓ Quest completed successfully!"), false);
            player.sendMessage(Text.literal("Check your inventory for rewards!"), false);

            List<Quest> mutableCompletedQuests = new ArrayList<>(completedQuests);
            mutableCompletedQuests.remove(selectedIndex);
            playerCompletedQuests.put(playerId, mutableCompletedQuests);
        } else {
            player.sendMessage(Text.literal("✗ Failed to turn in quest. Check your inventory space!"), false);
            playerSelectedIndex.put(playerId, selectedIndex + 1);
        }
    }

    private void showUpgradeMenu(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();

        if (!playerData.canUpgradeQuestBook()) {
            player.sendMessage(Text.literal("Quest book upgrade not available yet."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        QuestData.QuestBookTier nextTier = playerData.getNextQuestBookTier();

        player.sendMessage(Text.literal("=== Quest Book Upgrade ==="), false);
        player.sendMessage(Text.literal("Current: " + playerData.getQuestBookTier().getDisplayName()), false);
        player.sendMessage(Text.literal("Upgrade to: " + nextTier.getDisplayName()), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("New benefits:"), false);
        player.sendMessage(Text.literal("• Max quest slots: " + nextTier.getMaxActiveQuests()), false);
        player.sendMessage(Text.literal("• Access to: " + nextTier.getAllowedDifficulties()), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click to UPGRADE!"), false);

        // Perform upgrade
        playerData.setQuestBookTier(nextTier);
        player.sendMessage(Text.literal("✓ Quest book upgraded to: " + nextTier.getDisplayName()), false);

        playerMenuState.put(playerId, MenuState.MAIN_MENU);
    }
}