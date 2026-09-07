package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.progression.LevelRequirements;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClassTrainerNPC extends PathfinderMob {

    private static final Map<UUID, TrainerState> playerState = new ConcurrentHashMap<>();

    private enum TrainerState { OVERVIEW, TURN_IN, ACCEPT }

    // Offered by the trainer once the class's 5-quest chain is fully complete -- an NPC-category
    // quest (not shown at the Quest Block), so this is its only path to being offered/accepted.
    // Deliberately NOT part of ClassQuestChains itself: that list is also used by
    // QuestManager.canStartQuest's own path_of_destiny gate to check "is the chain complete",
    // and including the capstone in its own completion check would be circular.
    private static final String CAPSTONE_QUEST_ID = "path_of_destiny";

    public ClassTrainerNPC(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) return InteractionResult.SUCCESS;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        if (playerClass == null || playerClass.equals("none")) {
            sendLine(player, "You haven't chosen a class yet. Visit the Class Selection Altar at the Hall of Champions first.", ChatFormatting.YELLOW);
            return InteractionResult.CONSUME;
        }

        List<String> chain = getChain(playerClass);
        if (chain == null) {
            sendLine(player, "I have no training program for the " + playerClass + " class.", ChatFormatting.RED);
            return InteractionResult.CONSUME;
        }

        List<String> displayChain = new java.util.ArrayList<>(chain);
        displayChain.add(CAPSTONE_QUEST_ID);

        UUID uid = player.getUUID();
        TrainerState state = playerState.getOrDefault(uid, TrainerState.OVERVIEW);

        switch (state) {
            case OVERVIEW -> handleOverview(serverPlayer, playerClass, chain, displayChain);
            case TURN_IN  -> handleTurnIn(serverPlayer, displayChain);
            case ACCEPT   -> handleAccept(serverPlayer, chain, displayChain);
        }

        return InteractionResult.CONSUME;
    }

    private void handleOverview(ServerPlayer player, String playerClass, List<String> chain, List<String> displayChain) {
        QuestManager qm = QuestManager.getInstance();
        qm.updateQuestProgress(player);
        QuestData data = qm.getPlayerData(player);
        UUID uid = player.getUUID();

        String greeting = switch (playerClass) {
            case "Warrior" -> "Ready to forge your legend in battle, warrior?";
            case "Mage"    -> "Your arcane potential is boundless, apprentice. Let us review your progress.";
            case "Rogue"   -> "Step into the shadow, rogue. Patience and precision — that is the path.";
            default        -> "Welcome, adventurer. Let us review your progress.";
        };
        sendLine(player, greeting, ChatFormatting.GOLD);
        player.sendSystemMessage(Component.literal(""));

        String chainName = switch (playerClass) {
            case "Warrior" -> "Path of the Berserker";
            case "Mage"    -> "Path of the Archmage";
            case "Rogue"   -> "Path of Shadows";
            default        -> playerClass + " Class Chain";
        };
        player.sendSystemMessage(Component.literal("═══ " + chainName + " ═══").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(""));

        Quest pendingTurnIn = null;
        Quest nextAvailable = null;
        int playerLevel = getPlayerLevel(player);

        for (String questId : displayChain) {
            Quest template = qm.getQuest(questId);
            if (template == null) continue;

            boolean isCompleted = data.isQuestCompleted(questId);
            Quest activeQuest = data.getActiveQuest(questId);
            boolean isCapstone = questId.equals(CAPSTONE_QUEST_ID);

            if (isCompleted) {
                player.sendSystemMessage(Component.literal("  ✓ " + template.getName() + " [Lv." + template.getMinLevel() + "]")
                        .withStyle(ChatFormatting.GREEN));
            } else if (activeQuest != null) {
                if (activeQuest.isCompleted()) {
                    player.sendSystemMessage(Component.literal("  ★ " + template.getName() + " — READY TO TURN IN!")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                    if (pendingTurnIn == null) pendingTurnIn = activeQuest;
                } else {
                    player.sendSystemMessage(Component.literal("  ⟳ " + template.getName() + " [in progress]")
                            .withStyle(ChatFormatting.AQUA));
                    for (var obj : activeQuest.getObjectives()) {
                        player.sendSystemMessage(Component.literal("     " + obj.getDisplayText().getString())
                                .withStyle(ChatFormatting.GRAY));
                    }
                }
            } else {
                // Path of Destiny has no addPrerequisite() on the quest itself (the required
                // chain differs per class), so its real gate is "has completed every quest in
                // this class's own chain" rather than template.getPrerequisites().
                boolean prereqMet = isCapstone
                        ? chain.stream().allMatch(data::isQuestCompleted)
                        : template.getPrerequisites().isEmpty()
                            || template.getPrerequisites().stream().allMatch(data::isQuestCompleted);
                boolean levelMet = playerLevel >= template.getMinLevel();

                if (prereqMet && levelMet && nextAvailable == null) {
                    player.sendSystemMessage(Component.literal("  ◈ " + template.getName() + " [Lv." + template.getMinLevel() + "] — AVAILABLE")
                            .withStyle(ChatFormatting.WHITE));
                    nextAvailable = template;
                } else if (!isCapstone || prereqMet) {
                    // Once the chain is complete, still show Path of Destiny locked-by-level if
                    // that's the only thing missing; otherwise it stays implicit (no "complete
                    // previous quest" callout for a capstone that isn't reachable yet).
                    String lockReason = !prereqMet ? "complete previous quest" : "Lv." + template.getMinLevel() + " required";
                    player.sendSystemMessage(Component.literal("  🔒 " + template.getName() + " [" + lockReason + "]")
                            .withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }

        player.sendSystemMessage(Component.literal(""));

        if (pendingTurnIn != null) {
            player.sendSystemMessage(Component.literal(">> Right-click to turn in: " + pendingTurnIn.getName())
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
            playerState.put(uid, TrainerState.TURN_IN);
        } else if (nextAvailable != null) {
            player.sendSystemMessage(Component.literal(">> Right-click to accept: " + nextAvailable.getName())
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            playerState.put(uid, TrainerState.ACCEPT);
        } else {
            player.sendSystemMessage(Component.literal("Return when you have more progress to report.")
                    .withStyle(ChatFormatting.GRAY));
            playerState.put(uid, TrainerState.OVERVIEW);
        }
    }

    private void handleTurnIn(ServerPlayer player, List<String> chain) {
        QuestManager qm = QuestManager.getInstance();
        QuestData data = qm.getPlayerData(player);

        for (String questId : chain) {
            Quest active = data.getActiveQuest(questId);
            if (active != null && active.isCompleted()) {
                boolean success = qm.turnInQuest(player, questId);
                if (success) {
                    sendLine(player, "Well done. " + active.getName() + " complete — check your inventory for rewards.", ChatFormatting.GREEN);
                } else {
                    sendLine(player, "Turn-in failed. Make sure you have space in your inventory.", ChatFormatting.RED);
                }
                break;
            }
        }

        playerState.put(player.getUUID(), TrainerState.OVERVIEW);
    }

    private void handleAccept(ServerPlayer player, List<String> chain, List<String> displayChain) {
        QuestManager qm = QuestManager.getInstance();
        QuestData data = qm.getPlayerData(player);
        int playerLevel = getPlayerLevel(player);

        for (String questId : displayChain) {
            Quest template = qm.getQuest(questId);
            if (template == null) continue;
            if (data.isQuestCompleted(questId) || data.getActiveQuest(questId) != null) continue;

            boolean isCapstone = questId.equals(CAPSTONE_QUEST_ID);
            boolean prereqMet = isCapstone
                    ? chain.stream().allMatch(data::isQuestCompleted)
                    : template.getPrerequisites().isEmpty()
                        || template.getPrerequisites().stream().allMatch(data::isQuestCompleted);
            boolean levelMet = playerLevel >= template.getMinLevel();

            if (prereqMet && levelMet) {
                boolean success = qm.startClassQuest(player, questId);
                if (success) {
                    sendLine(player, "Your training begins. Return when " + template.getName() + " is complete.", ChatFormatting.AQUA);
                }
                break;
            }
        }

        playerState.put(player.getUUID(), TrainerState.OVERVIEW);
    }

    private int getPlayerLevel(ServerPlayer player) {
        var data = ProgressionManager.getPlayerData(player);
        return data != null ? data.getCurrentLevel() : 0;
    }

    private List<String> getChain(String playerClass) {
        List<String> chain = com.github.hitman20081.dagmod.quest.ClassQuestChains.forClass(playerClass);
        return chain.isEmpty() ? null : chain;
    }

    private void sendLine(Player player, String message, ChatFormatting color) {
        player.sendSystemMessage(
                Component.literal("[Class Trainer] ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD)
                        .append(Component.literal(message).withStyle(color))
        );
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.dagmod.class_trainer");
    }

    @Override
    public boolean isPersistenceRequired() { return true; }

    public boolean damage(DamageSource source, float amount) { return false; }

    public void pushAwayFrom(net.minecraft.world.entity.Entity entity) {}
}
