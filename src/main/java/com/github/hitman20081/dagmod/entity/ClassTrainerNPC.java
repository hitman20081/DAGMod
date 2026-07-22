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

    private static final List<String> WARRIOR_QUESTS = List.of(
            "trial_of_fury", "battle_hardened", "whirlwind_mastery", "iron_skin_trial", "war_cry"
    );
    private static final List<String> MAGE_QUESTS = List.of(
            "arcane_missiles_unlock", "temporal_mastery", "mana_burst_unlock", "arcane_barrier_unlock", "archmage_trial"
    );
    private static final List<String> ROGUE_QUESTS = List.of(
            "shadows_calling", "blink_strike_unlock", "poison_strike_unlock", "assassinate_unlock", "vanish_unlock"
    );

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

        UUID uid = player.getUUID();
        TrainerState state = playerState.getOrDefault(uid, TrainerState.OVERVIEW);

        switch (state) {
            case OVERVIEW -> handleOverview(serverPlayer, playerClass, chain);
            case TURN_IN  -> handleTurnIn(serverPlayer, chain);
            case ACCEPT   -> handleAccept(serverPlayer, chain);
        }

        return InteractionResult.CONSUME;
    }

    private void handleOverview(ServerPlayer player, String playerClass, List<String> chain) {
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

        for (String questId : chain) {
            Quest template = qm.getQuest(questId);
            if (template == null) continue;

            boolean isCompleted = data.isQuestCompleted(questId);
            Quest activeQuest = data.getActiveQuest(questId);

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
                boolean prereqMet = template.getPrerequisites().isEmpty()
                        || template.getPrerequisites().stream().allMatch(data::isQuestCompleted);
                boolean levelMet = playerLevel >= template.getMinLevel();

                if (prereqMet && levelMet && nextAvailable == null) {
                    player.sendSystemMessage(Component.literal("  ◈ " + template.getName() + " [Lv." + template.getMinLevel() + "] — AVAILABLE")
                            .withStyle(ChatFormatting.WHITE));
                    nextAvailable = template;
                } else {
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

    private void handleAccept(ServerPlayer player, List<String> chain) {
        QuestManager qm = QuestManager.getInstance();
        QuestData data = qm.getPlayerData(player);
        int playerLevel = getPlayerLevel(player);

        for (String questId : chain) {
            Quest template = qm.getQuest(questId);
            if (template == null) continue;
            if (data.isQuestCompleted(questId) || data.getActiveQuest(questId) != null) continue;

            boolean prereqMet = template.getPrerequisites().isEmpty()
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
        return switch (playerClass) {
            case "Warrior" -> WARRIOR_QUESTS;
            case "Mage"    -> MAGE_QUESTS;
            case "Rogue"   -> ROGUE_QUESTS;
            default        -> null;
        };
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
