package com.github.hitman20081.dagmod.block;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import com.mojang.serialization.MapCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobBoardBlock extends HorizontalFacingBlock {
    public static final MapCodec<JobBoardBlock> CODEC = createCodec(JobBoardBlock::new);

    @Override
    public MapCodec<JobBoardBlock> getCodec() {
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

    public JobBoardBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        return switch (facing) {
            case NORTH -> Block.createCuboidShape(0, 0, 13, 16, 16, 16);
            case SOUTH -> Block.createCuboidShape(0, 0, 0, 16, 16, 3);
            case WEST -> Block.createCuboidShape(13, 0, 0, 16, 16, 16);
            case EAST -> Block.createCuboidShape(0, 0, 0, 3, 16, 16);
            default -> Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            // Check if player has met Innkeeper Garrick (tutorial gate)
            if (!PlayerDataManager.hasMetGarrick(serverPlayer)) {
                player.sendMessage(
                    Text.literal("🔒 This Job Board is locked!").formatted(Formatting.RED, Formatting.BOLD),
                    false
                );
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(
                    Text.literal("Find Innkeeper Garrick to learn how to use the quest system.").formatted(Formatting.YELLOW),
                    false
                );
                player.sendMessage(
                    Text.literal("(He can usually be found at an inn or tavern)").formatted(Formatting.GRAY),
                    false
                );
                return ActionResult.CONSUME;
            }

            // Check if player has a Quest Book (tutorial completion requirement)
            boolean hasQuestBook = player.getInventory().contains(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.NOVICE_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.APPRENTICE_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.EXPERT_QUEST_BOOK)) ||
                                   player.getInventory().contains(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.MASTER_QUEST_TOME));

            if (!hasQuestBook) {
                player.sendMessage(
                    Text.literal("📚 You need a Quest Book to use this!").formatted(Formatting.YELLOW, Formatting.BOLD),
                    false
                );
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(
                    Text.literal("Complete all 3 of Garrick's tasks to earn Quest Notes.").formatted(Formatting.GRAY),
                    false
                );
                player.sendMessage(
                    Text.literal("Then take the notes to a Quest Block to combine them").formatted(Formatting.GRAY),
                    false
                );
                player.sendMessage(
                    Text.literal("into a Novice Quest Book!").formatted(Formatting.GRAY),
                    false
                );
                return ActionResult.CONSUME;
            }

            QuestManager questManager = QuestManager.getInstance();
            QuestData playerData = questManager.getPlayerData(player);
            UUID playerId = player.getUuid();

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
        return ActionResult.SUCCESS;
    }

    private void showMainMenu(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();

        player.sendMessage(Text.literal("=== Job Board ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Looking for work, adventurer?"), false);
        player.sendMessage(Text.literal(""), false);

        // Show quick stats
        player.sendMessage(Text.literal("Your Status:"), false);
        player.sendMessage(Text.literal("• Active Jobs: " + playerData.getActiveQuestCount() + "/" + playerData.getMaxActiveQuests()), false);
        player.sendMessage(Text.literal("• Completed: " + playerData.getTotalQuestsCompleted()), false);
        player.sendMessage(Text.literal(""), false);

        // Check for completed jobs ready to turn in FIRST
        List<Quest> completedJobs = playerData.getActiveQuestsList().stream()
                .filter(Quest::isCompleted)
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .toList();

        if (!completedJobs.isEmpty()) {
            player.sendMessage(Text.literal("✓ You have " + completedJobs.size() + " completed job(s) to turn in!"), false);
            player.sendMessage(Text.literal("Right-click to collect your payment!"), false);
            player.sendMessage(Text.literal("==================="), false);

            playerMenuState.put(playerId, MenuState.TURN_IN_JOBS);
            playerCompletedJobs.put(playerId, completedJobs);
            playerSelectedIndex.put(playerId, 0);
            return;
        }

        // If no completed jobs, show available jobs
        player.sendMessage(Text.literal("Right-click again to:"), false);

        // FILTER: Only show JOB and DAILY category quests
        List<Quest> availableJobs = questManager.getAvailableQuests(player).stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .collect(Collectors.toList());

        if (!availableJobs.isEmpty() && playerData.canAcceptMoreQuests()) {
            player.sendMessage(Text.literal("→ Browse Available Jobs (" + availableJobs.size() + " posted)"), false);
            playerMenuState.put(playerId, MenuState.BROWSE_JOBS);
            playerAvailableJobs.put(playerId, availableJobs);
            playerSelectedIndex.put(playerId, 0);
        } else if (!playerData.canAcceptMoreQuests()) {
            player.sendMessage(Text.literal("→ View Active Jobs (job slots full)"), false);
            playerMenuState.put(playerId, MenuState.ACTIVE_JOBS);
        } else if (!playerData.getActiveQuests().isEmpty()) {
            player.sendMessage(Text.literal("→ View Active Jobs"), false);
            playerMenuState.put(playerId, MenuState.ACTIVE_JOBS);
        } else {
            player.sendMessage(Text.literal("No jobs available. Check back later!"), false);
        }

        player.sendMessage(Text.literal("==================="), false);
    }

    private void showBrowseJobs(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> availableJobs = playerAvailableJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.get(playerId);

        if (availableJobs == null || availableJobs.isEmpty()) {
            player.sendMessage(Text.literal("No jobs available."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        if (selectedIndex >= availableJobs.size()) {
            player.sendMessage(Text.literal("No more jobs to browse."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest currentJob = availableJobs.get(selectedIndex);

        player.sendMessage(Text.literal("=== Available Jobs ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Job " + (selectedIndex + 1) + "/" + availableJobs.size()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("===================").formatted(Formatting.GOLD), false);

        // Show if it's a daily job
        if (currentJob.getCategory() == Quest.QuestCategory.DAILY) {
            player.sendMessage(Text.literal("[DAILY JOB]").formatted(Formatting.AQUA), false);
        }

        Text jobTitle = Text.literal("📋 " + currentJob.getName())
                .formatted(Formatting.BOLD)
                .formatted(Formatting.YELLOW);
        player.sendMessage(jobTitle, false);
        player.sendMessage(Text.literal("Difficulty: " + currentJob.getDifficulty().getDisplayName()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);

        player.sendMessage(Text.literal(currentJob.getDescription()).formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);

        player.sendMessage(Text.literal("Objectives:").formatted(Formatting.GREEN), false);
        for (var objective : currentJob.getObjectives()) {
            player.sendMessage(Text.literal("  • " + objective.getDescription()).formatted(Formatting.WHITE), false);
        }
        player.sendMessage(Text.literal(""), false);

        player.sendMessage(Text.literal("Payment:").formatted(Formatting.GOLD), false);
        for (var reward : currentJob.getRewards()) {
            player.sendMessage(Text.literal("  • ").append(reward.getDisplayText()), false);
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click to ACCEPT this job"), false);
        player.sendMessage(Text.literal("Or sneak + right-click to SKIP"), false);
        player.sendMessage(Text.literal("===================").formatted(Formatting.GOLD), false);

        if (player.isSneaking()) {
            playerSelectedIndex.put(playerId, selectedIndex + 1);
            return;
        }

        playerMenuState.put(playerId, MenuState.CONFIRM_ACCEPT);
    }

    private void showConfirmAccept(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> availableJobs = playerAvailableJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.get(playerId);

        if (selectedIndex < 0 || selectedIndex >= availableJobs.size()) {
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        Quest jobToAccept = availableJobs.get(selectedIndex);

        player.sendMessage(Text.literal("=== CONFIRM JOB ACCEPTANCE ==="), false);
        player.sendMessage(Text.literal("Job: " + jobToAccept.getName()), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click to CONFIRM"), false);
        player.sendMessage(Text.literal("==================="), false);

        // Accept the job
        if (questManager.startQuest(player, jobToAccept.getId())) {
            player.sendMessage(Text.literal("✓ Job accepted: " + jobToAccept.getName()), false);
            player.sendMessage(Text.literal("Get to work!"), false);
        } else {
            player.sendMessage(Text.literal("✗ Failed to accept job!"), false);
        }

        playerMenuState.put(playerId, MenuState.MAIN_MENU);
        playerSelectedIndex.put(playerId, 0);
    }

    private void showActiveJobs(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();

        player.sendMessage(Text.literal("=== Your Active Jobs ==="), false);

        // FILTER: Only show JOB and DAILY category quests
        List<Quest> activeJobs = playerData.getActiveQuests().stream()
                .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                .toList();

        if (activeJobs.isEmpty()) {
            player.sendMessage(Text.literal("No active jobs."), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            return;
        }

        for (Quest job : activeJobs) {
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("📋 " + job.getName() + " (" + job.getDifficulty().getDisplayName() + ")"), false);

            for (var objective : job.getObjectives()) {
                player.sendMessage(Text.literal("  " + objective.getDisplayText().getString()), false);
            }

            if (job.isCompleted()) {
                player.sendMessage(Text.literal("  ✓ Ready to collect payment!"), false);
            }
        }

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Right-click again to return to main menu."), false);
        playerMenuState.put(playerId, MenuState.MAIN_MENU);
    }

    private void showTurnInJobs(ServerPlayerEntity player, QuestManager questManager, QuestData playerData) {
        UUID playerId = player.getUuid();
        List<Quest> completedJobs = playerCompletedJobs.get(playerId);
        int selectedIndex = playerSelectedIndex.getOrDefault(playerId, 0);

        if (completedJobs == null || completedJobs.isEmpty()) {
            completedJobs = playerData.getActiveQuestsList().stream()
                    .filter(Quest::isCompleted)
                    .filter(q -> q.getCategory() == Quest.QuestCategory.JOB || q.getCategory() == Quest.QuestCategory.DAILY)
                    .toList();
            playerCompletedJobs.put(playerId, completedJobs);

            if (completedJobs.isEmpty()) {
                player.sendMessage(Text.literal("No completed jobs to turn in."), false);
                playerMenuState.put(playerId, MenuState.MAIN_MENU);
                return;
            }
        }

        if (selectedIndex >= completedJobs.size()) {
            player.sendMessage(Text.literal("All jobs completed and paid!"), false);
            playerMenuState.put(playerId, MenuState.MAIN_MENU);
            playerCompletedJobs.remove(playerId);
            return;
        }

        Quest jobToTurnIn = completedJobs.get(selectedIndex);

        player.sendMessage(Text.literal("=== Collect Payment " + (selectedIndex + 1) + "/" + completedJobs.size() + " ==="), false);
        player.sendMessage(Text.literal("📋 " + jobToTurnIn.getName()), false);
        player.sendMessage(Text.literal(""), false);

        player.sendMessage(Text.literal("You will receive:"), false);
        for (var reward : jobToTurnIn.getRewards()) {
            player.sendMessage(reward.getDisplayText(), false);
        }
        player.sendMessage(Text.literal(""), false);

        if (!jobToTurnIn.isCompleted()) {
            player.sendMessage(Text.literal("✗ This job is not yet completed!"), false);
            List<Quest> mutableCompletedJobs = new ArrayList<>(completedJobs);
            mutableCompletedJobs.remove(selectedIndex);
            playerCompletedJobs.put(playerId, mutableCompletedJobs);
            return;
        }

        player.sendMessage(Text.literal("Right-click to collect payment..."), false);

        boolean success = questManager.turnInQuest(player, jobToTurnIn.getId());

        if (success) {
            player.sendMessage(Text.literal("✓ Job completed! Payment received!"), false);
            player.sendMessage(Text.literal("Check your inventory!"), false);

            List<Quest> mutableCompletedJobs = new ArrayList<>(completedJobs);
            mutableCompletedJobs.remove(selectedIndex);
            playerCompletedJobs.put(playerId, mutableCompletedJobs);
        } else {
            player.sendMessage(Text.literal("✗ Failed to collect payment. Check your inventory space!"), false);
            playerSelectedIndex.put(playerId, selectedIndex + 1);
        }
    }
}