package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import com.mojang.serialization.MapCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobBoardBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<JobBoardBlock> CODEC = simpleCodec(JobBoardBlock::new);

    @Override
    public MapCodec<JobBoardBlock> codec() {
        return CODEC;
    }

    // Track what menu state each player is in
    private static final Map<UUID, MenuState> playerMenuState = new HashMap<>();
    private static final Map<UUID, List<Quest>> playerAvailableJobs = new HashMap<>();
    private static final Map<UUID, List<Quest>> playerCompletedJobs = new HashMap<>();
    private static final Map<UUID, Integer> playerSelectedIndex = new HashMap<>();

    public enum MenuState {
        MAIN_MENU,
        BROWSE_JOBS,
        CONFIRM_ACCEPT,
        ACTIVE_JOBS,
        TURN_IN_JOBS
    }

    public JobBoardBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH -> Block.box(0, 0, 13, 16, 16, 16);
            case SOUTH -> Block.box(0, 0, 0, 16, 16, 3);
            case WEST -> Block.box(13, 0, 0, 16, 16, 16);
            case EAST -> Block.box(0, 0, 0, 3, 16, 16);
            default -> Block.box(0, 0, 0, 16, 16, 16);
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            // Check if player has met Innkeeper Garrick (tutorial gate)
            if (!PlayerDataManager.hasMetGarrick(serverPlayer)) {
                player.sendSystemMessage(
                    Component.literal("🔒 This Job Board is locked!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(
                    Component.literal("Find Innkeeper Garrick to learn how to use the quest system.").withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(
                    Component.literal("(He can usually be found at an inn or tavern)").withStyle(ChatFormatting.GRAY));
                return InteractionResult.CONSUME;
            }

            // Check if player has a Quest Book (tutorial completion requirement)
            boolean hasQuestBook = player.getInventory().contains(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.NOVICE_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.APPRENTICE_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.EXPERT_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.MASTER_QUEST_TOME));

            if (!hasQuestBook) {
                player.sendSystemMessage(
                    Component.literal("📚 You need a Quest Book to use this!").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(
                    Component.literal("Complete all 3 of Garrick's tasks to earn Quest Notes.").withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(
                    Component.literal("Then take the notes to a Quest Block to combine them").withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(
                    Component.literal("into a Novice Quest Book!").withStyle(ChatFormatting.GRAY));
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
                case BROWSE_JOBS -> showBrowseJobs(serverPlayer, questManager, playerData);
                case CONFIRM_ACCEPT -> showConfirmAccept(serverPlayer, questManager, playerData);
                case ACTIVE_JOBS -> showActiveJobs(serverPlayer, questManager, playerData);
                case TURN_IN_JOBS -> showTurnInJobs(serverPlayer, questManager, playerData);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void showMainMenu(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();

        player.sendSystemMessage(Component.literal("=== Job Board ===").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Looking for work, adventurer?"));
        player.sendSystemMessage(Component.literal(""));

        // Show quick stats
        player.sendSystemMessage(Component.literal("Your Status:"));
        player.sendSystemMessage(Component.literal("• Active Jobs: " + playerData.getActiveQuestCount() + "/" + playerData.getMaxActiveQuests()));
        player.sendSystemMessage(Component.literal("• Completed: " + playerData.getTotalQuestsCompleted()));
        player.sendSystemMessage(Component.literal(""));

        // Check for completed jobs ready to turn in FIRST
        List<Quest> completedJobs = playerData.getActiveQuestsList().stream()
                .filter(Quest::isCompleted)
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .toList();

        if (!completedJobs.isEmpty()) {
            player.sendSystemMessage(Component.literal("✓ You have " + completedJobs.size() + " completed job(s) to turn in!"));
            player.sendSystemMessage(Component.literal("Right-click to collect your payment!"));
            player.sendSystemMessage(Component.literal("==================="));

            playerMenuState.put(playerId, MenuState.TURN_IN_JOBS);
            playerCompletedJobs.put(playerId, completedJobs);
            playerSelectedIndex.put(playerId, 0);
            return;
        }

        // If no completed jobs, show available jobs
        player.sendSystemMessage(Component.literal("Right-click again to:"));

        // FILTER: Only show JOB and DAILY category quests
        List<Quest> availableJobs = questManager.getAvailableQuests(player).stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .collect(Collectors.toList());

        if (!availableJobs.isEmpty() && playerData.canAcceptMoreQuests()) {
            player.sendSystemMessage(Component.literal("→ Browse Available Jobs (" + availableJobs.size() + " posted)"));
            playerMenuState.put(playerId, MenuState.BROWSE_JOBS);
            playerAvailableJobs.put(playerId, availableJobs);
            playerSelectedIndex.put(playerId, 0);
        } else if (!playerData.canAcceptMoreQuests()) {
            player.sendSystemMessage(Component.literal("→ View Active Jobs (job slots full)"));
            playerMenuState.put(playerId, MenuState.ACTIVE_JOBS);
        } else if (!playerData.getActiveQuests().isEmpty()) {
            player.sendSystemMessage(Component.literal("→ View Active Jobs"));
            playerMenuState.put(playerId, MenuState.ACTIVE_JOBS);
        } else {
            player.sendSystemMessage(Component.literal("No jobs available. Check back later!"));
        }

        player.sendSystemMessage(Component.literal("==================="));
    }

    private void showBrowseJobs(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> availableJobs = playerAvailableJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.get(playerId);

        if (availableJobs == null || availableJobs.isEmpty()) {
            player.sendSystemMessage(Component.literal("No jobs available."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        if (selectedIndex >= availableJobs.size()) {
            player.sendSystemMessage(Component.literal("No more jobs to browse."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest currentJob = availableJobs.get(selectedIndex);

        player.sendSystemMessage(Component.literal("=== Available Jobs ===").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("Job " + (selectedIndex + 1) + "/" + availableJobs.size()).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));

        // Show if it's a daily job
        if (currentJob.getCategory() == Quest.QuestCategory.DAILY) {
            player.sendSystemMessage(Component.literal("[DAILY JOB]").withStyle(ChatFormatting.AQUA));
        }

        Component jobTitle = Component.literal("📋 " + currentJob.getName())
                .withStyle(ChatFormatting.BOLD)
                .withStyle(ChatFormatting.YELLOW);
        player.sendSystemMessage(jobTitle);
        player.sendSystemMessage(Component.literal("Difficulty: " + currentJob.getDifficulty().getDisplayName()).withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal(currentJob.getDescription()).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal("Objectives:").withStyle(ChatFormatting.GREEN));
        for (var objective : currentJob.getObjectives()) {
            player.sendSystemMessage(Component.literal("  • " + objective.getDescription()).withStyle(ChatFormatting.WHITE));
        }
        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal("Payment:").withStyle(ChatFormatting.GOLD));
        for (var reward : currentJob.getRewards()) {
            player.sendSystemMessage(Component.literal("  • ").append(reward.getDisplayText()));
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Right-click to ACCEPT this job"));
        player.sendSystemMessage(Component.literal("Or sneak + right-click to SKIP"));
        player.sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));

        if (player.isShiftKeyDown()) {
            playerSelectedIndex.put(playerId, selectedIndex + 1);
            return;
        }

        playerMenuState.put(playerId, MenuState.CONFIRM_ACCEPT);
    }

    private void showConfirmAccept(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> availableJobs = playerAvailableJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.get(playerId);

        if (selectedIndex < 0 || selectedIndex >= availableJobs.size()) {
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest jobToAccept = availableJobs.get(selectedIndex);

        player.sendSystemMessage(Component.literal("=== CONFIRM JOB ACCEPTANCE ==="));
        player.sendSystemMessage(Component.literal("Job: " + jobToAccept.getName()));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Right-click to CONFIRM"));
        player.sendSystemMessage(Component.literal("==================="));

        // Accept the job
        if (questManager.startQuest(player, jobToAccept.getId())) {
            player.sendSystemMessage(Component.literal("✓ Job accepted: " + jobToAccept.getName()));
            player.sendSystemMessage(Component.literal("Get to work!"));
        } else {
            player.sendSystemMessage(Component.literal("✗ Failed to accept job!"));
        }

        playerMenuState.put(playerId, MenuState.MAIN_MENU);
        playerSelectedIndex.put(playerId, 0);
    }

    private void showActiveJobs(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();

        player.sendSystemMessage(Component.literal("=== Your Active Jobs ==="));

        // FILTER: Only show JOB and DAILY category quests
        List<Quest> activeJobs = playerData.getActiveQuests().stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .toList();

        if (activeJobs.isEmpty()) {
            player.sendSystemMessage(Component.literal("No active jobs."));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        for (Quest job : activeJobs) {
            player.sendSystemMessage(Component.literal(""));
            player.sendSystemMessage(Component.literal("📋 " + job.getName() + " (" + job.getDifficulty().getDisplayName() + ")"));

            for (var objective : job.getObjectives()) {
                player.sendSystemMessage(Component.literal("  " + objective.getDisplayText().getString()));
            }

            if (job.isCompleted()) {
                player.sendSystemMessage(Component.literal("  ✓ Ready to collect payment!"));
            }
        }

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("Right-click again to return to main menu."));
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
    }

    private void showTurnInJobs(ServerPlayer player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUUID();
        List<Quest> completedJobs = playerCompletedJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (completedJobs == null || completedJobs.isEmpty()) {
            completedJobs = playerData.getActiveQuestsList().stream()
                    .filter(Quest::isCompleted)
                    .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                    .toList();
            playerCompletedJobs.put(playerId, completedJobs);

            if (completedJobs.isEmpty()) {
                player.sendSystemMessage(Component.literal("No completed jobs to turn in."));
                playerMenuState.put(playerId, MenuState.MAIN_MENU);
                return;
            }
        }

        if (selectedIndex >= completedJobs.size()) {
            player.sendSystemMessage(Component.literal("All jobs completed and paid!"));
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerCompletedJobs.remove(playerId);
            return;
        }

        Quest jobToTurnIn = completedJobs.get(selectedIndex);

        player.sendSystemMessage(Component.literal("=== Collect Payment " + (selectedIndex + 1) + "/" + completedJobs.size() + " ==="));
        player.sendSystemMessage(Component.literal("📋 " + jobToTurnIn.getName()));
        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal("You will receive:"));
        for (var reward : jobToTurnIn.getRewards()) {
            player.sendSystemMessage(reward.getDisplayText());
        }
        player.sendSystemMessage(Component.literal(""));

        if (!jobToTurnIn.isCompleted()) {
            player.sendSystemMessage(Component.literal("✗ This job is not yet completed!"));
            List<Quest> mutableCompletedJobs = new ArrayList<>(completedJobs);
            mutableCompletedJobs.remove(selectedIndex);
            playerCompletedJobs.put(playerId, mutableCompletedJobs);
            return;
        }

        player.sendSystemMessage(Component.literal("Right-click to collect payment..."));

        boolean success = questManager.turnInQuest(player, jobToTurnIn.getId());

        if (success) {
            player.sendSystemMessage(Component.literal("✓ Job completed! Payment received!"));
            player.sendSystemMessage(Component.literal("Check your inventory!"));

            List<Quest> mutableCompletedJobs = new ArrayList<>(completedJobs);
            mutableCompletedJobs.remove(selectedIndex);
            playerCompletedJobs.put(playerId, mutableCompletedJobs);
        } else {
            player.sendSystemMessage(Component.literal("✗ Failed to collect payment. Check your inventory space!"));
            playerSelectedIndex.put(playerId, selectedIndex + 1);
        }
    }
}