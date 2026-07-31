package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

/**
 * Innkeeper Garrick - Tutorial NPC who teaches players the quest system
 *
 * This NPC uses a task-based pre-quest tutorial system to introduce new players
 * to basic game mechanics before granting access to the formal quest system.
 *
 * Tutorial Flow:
 * 1. **Task 1 (Resourcefulness)**: Gather 10 Oak Logs → Reward: First Quest Note
 * 2. **Task 2 (Courage)**: Defeat 5 hostile mobs → Reward: Second Quest Note
 * 3. **Task 3 (Dedication)**: Bring 1 Iron Ingot → Reward: Third Quest Note
 * 4. **Final Step**: Take all 3 notes to a Quest Block to combine into Novice Quest Book
 *
 * Quest System Gating:
 * - Players must interact with Garrick to unlock Quest Blocks and Job Boards
 * - No quests are available until player obtains the Novice Quest Book
 * - All task progress is tracked via PlayerDataManager and persists across sessions
 *
 * Task Tracking:
 * - Task 1: Manual inventory check for oak logs
 * - Task 2: Event-based hostile mob kill tracking (see DagMod.registerTutorialMobKillTracking)
 * - Task 3: Manual inventory check for iron ingot
 *
 * Dialogue States:
 * - First meeting: Welcome + Task 1 instructions
 * - Task 1/2/3 in progress: Progress check and guidance
 * - Task 1/2/3 complete: Reward + next task
 * - All tasks complete: Direct to Quest Block for final combination
 */
public class InnkeeperGarrickNPC extends PathfinderMob {

    public InnkeeperGarrickNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    // This sets up the NPC's basic stats (health, speed, etc.)
    public static AttributeSupplier.Builder createMobAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2); // Slower than player (barkeep doesn't wander much)
    }

    // This sets up the NPC's AI behaviors
    @Override
    protected void registerGoals() {
        // Look at nearby players
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        // Wander around occasionally (barkeeps don't move much)
        // Look around randomly
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    // This handles what happens when a player right-clicks the NPC
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            net.minecraft.server.level.ServerPlayer serverPlayer = (net.minecraft.server.level.ServerPlayer) player;

            // Unlock quest system on very first interaction
            if (!PlayerDataManager.hasMetGarrick(serverPlayer)) {
                PlayerDataManager.markMetGarrick(serverPlayer);
                player.sendSystemMessage(
                    Component.literal("✓ Quest System Unlocked!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            }

            // Race and class selection always happen first, before any tasks
            String race = RaceSelectionAltarBlock.getPlayerRace(serverPlayer.getUUID());
            if (race.equals("none")) {
                handleFirstMeeting(serverPlayer);
                return InteractionResult.SUCCESS;
            }

            String playerClass = ClassSelectionAltarBlock.getPlayerClass(serverPlayer.getUUID());
            if (playerClass.equals("none")) {
                showClassMenu(serverPlayer);
                return InteractionResult.SUCCESS;
            }

            // Task flow — race and class are already registered
            boolean task1Done = PlayerDataManager.isTask1Complete(serverPlayer.getUUID());
            boolean task2Done = PlayerDataManager.isTask2Complete(serverPlayer.getUUID());
            boolean task3Done = PlayerDataManager.isTask3Complete(serverPlayer.getUUID());

            if (task1Done && task2Done && task3Done) {
                handleAllTasksComplete(serverPlayer);
            } else if (task1Done && task2Done) {
                handleTask3(serverPlayer, false);
            } else if (task1Done) {
                handleTask2(serverPlayer, false);
            } else {
                handleTask1(serverPlayer, false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Handle first meeting - Welcome and show race selection menu
     */
    private void handleFirstMeeting(net.minecraft.server.level.ServerPlayer player) {
        sendDialogue(player, "Welcome, traveler! I'm Innkeeper Garrick, keeper of this establishment.", ChatFormatting.GOLD);
        player.sendSystemMessage(Component.empty());
        sendDialogue(player, "Before I can enter you in the Guild Ledger, I need to know who you are.", ChatFormatting.WHITE);
        player.sendSystemMessage(Component.empty());
        showRaceMenu(player);
    }

    /**
     * Handle Task 1 - Check for 10 Oak Logs
     */
    private void handleTask1(net.minecraft.server.level.ServerPlayer player, boolean alreadyComplete) {
        if (alreadyComplete) {
            sendDialogue(player, "You've already completed the first task. Ready for the next one?", ChatFormatting.YELLOW);
            handleTask2(player, false);
            return;
        }

        // Check if player has 10 logs of any type
        int logCount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(holder -> holder.is(net.minecraft.tags.ItemTags.LOGS))) {
                logCount += stack.getCount();
            }
        }

        if (logCount >= 10) {
            // Task complete!
            // Remove 10 logs
            int toRemove = 10;
            for (int i = 0; i < player.getInventory().getContainerSize() && toRemove > 0; i++) {
                net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(holder -> holder.is(net.minecraft.tags.ItemTags.LOGS))) {
                    int removeFromStack = Math.min(toRemove, stack.getCount());
                    player.getInventory().removeItem(i, removeFromStack);
                    toRemove -= removeFromStack;
                }
            }
            player.getInventory().setChanged();

            // Mark complete and give note
            PlayerDataManager.markTask1Complete(player);
            player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_FIRST_NOTE));

            sendDialogue(player, "Excellent work! You've proven your resourcefulness.", ChatFormatting.GREEN);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("✓ Received: Garrick's First Note (1/3)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.empty());
            sendDialogue(player, "That's one task done. Let's move on to the next...", ChatFormatting.WHITE);
            player.sendSystemMessage(Component.empty());

            // Offer Task 2
            handleTask2(player, false);
        } else {
            // Show task 1 instructions (first time or reminder)
            String intro = logCount == 0
                ? "Good. Now that you're registered, let me give you three tasks before you access the quest system."
                : "Still gathering those logs? You're making progress!";
            sendDialogue(player, intro, logCount == 0 ? ChatFormatting.WHITE : ChatFormatting.YELLOW);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("📋 TASK 1: PROVE YOUR RESOURCEFULNESS").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("   I need wood for the inn's fireplace.").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("   ➤ Gather 10 logs (any wood type)").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("   ➤ Current progress: " + logCount + "/10").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
            player.sendSystemMessage(Component.empty());
            sendDialogue(player, "Any tree will do — chop whatever is nearby!", ChatFormatting.GREEN);
        }
    }

    /**
     * Handle Task 2 - Kill 5 hostile mobs
     */
    private void handleTask2(net.minecraft.server.level.ServerPlayer player, boolean alreadyComplete) {
        // Enforce prerequisite: Task 1 must be complete
        if (!PlayerDataManager.isTask1Complete(player.getUUID())) {
            sendDialogue(player, "You need to complete Task 1 first!", ChatFormatting.RED);
            handleTask1(player, false);
            return;
        }

        if (alreadyComplete) {
            // Task 2 done, give note if they don't have it
            boolean hasNote = player.getInventory().contains(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));

            if (!hasNote) {
                player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));
                sendDialogue(player, "You've proven your courage in battle!", ChatFormatting.GREEN);
                player.sendSystemMessage(Component.empty());
                player.sendSystemMessage(Component.literal("✓ Received: Garrick's Second Note (2/3)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.empty());
            }

            sendDialogue(player, "Ready for the final task?", ChatFormatting.YELLOW);
            player.sendSystemMessage(Component.empty());
            handleTask3(player, false);
            return;
        }

        // Check progress
        int mobKills = PlayerDataManager.getTask2MobKills(player.getUUID());

        if (mobKills >= 5) {
            // Just completed!
            // Mark Task 2 as complete BEFORE giving the note
            PlayerDataManager.markTask2Complete(player);
            player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));

            sendDialogue(player, "Impressive! You've proven your courage in battle!", ChatFormatting.GREEN);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("✓ Received: Garrick's Second Note (2/3)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.empty());
            sendDialogue(player, "Two down, one to go. Here's your final task...", ChatFormatting.WHITE);
            player.sendSystemMessage(Component.empty());

            handleTask3(player, false);
        } else {
            // Offer task 2
            player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("⚔ TASK 2: PROVE YOUR COURAGE").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("   The world is dangerous. Show me you can handle it.").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("   ➤ Defeat 5 hostile mobs (zombies, skeletons, etc.)").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("   ➤ Current progress: " + mobKills + "/5").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
            player.sendSystemMessage(Component.empty());

            sendDialogue(player, "Come back when you've proven yourself in combat!", ChatFormatting.GREEN);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("💡 TIP: Hostile mobs spawn at night or in dark places.").withStyle(ChatFormatting.AQUA));
        }
    }

    /**
     * Handle Task 3 - Bring 1 Iron Ingot
     */
    private void handleTask3(net.minecraft.server.level.ServerPlayer player, boolean alreadyComplete) {
        // Enforce prerequisite: Task 2 must be complete
        if (!PlayerDataManager.isTask2Complete(player.getUUID())) {
            sendDialogue(player, "You need to complete Task 2 first!", ChatFormatting.RED);
            handleTask2(player, false);
            return;
        }

        if (alreadyComplete) {
            handleAllTasksComplete(player);
            return;
        }

        // Check if player has iron ingot
        boolean hasIron = player.getInventory().contains(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT));

        if (hasIron) {
            // Task complete!
            // Remove 1 iron ingot
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == net.minecraft.world.item.Items.IRON_INGOT) {
                    player.getInventory().removeItem(i, 1);
                    player.getInventory().setChanged();
                    break;
                }
            }

            // Mark complete and give note
            PlayerDataManager.markTask3Complete(player);
            player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_THIRD_NOTE));

            sendDialogue(player, "Perfect! You've proven your dedication to the craft!", ChatFormatting.GREEN);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("✓ Received: Garrick's Third Note (3/3)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.empty());

            handleAllTasksComplete(player);
        } else {
            // Offer task 3
            player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("⛏ TASK 3: PROVE YOUR DEDICATION").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("   A true adventurer knows how to work with metal.").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("   ➤ Bring me 1 Iron Ingot").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("   ➤ You'll need to mine iron ore and smelt it").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
            player.sendSystemMessage(Component.empty());

            sendDialogue(player, "Find iron ore underground, then smelt it in a furnace!", ChatFormatting.GREEN);
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("💡 TIP: Iron ore is common below y-level 64.").withStyle(ChatFormatting.AQUA));
        }
    }

    /**
     * All tasks complete — show quest book instructions.
     */
    private void handleAllTasksComplete(net.minecraft.server.level.ServerPlayer player) {
        showRegistrationComplete(player);
    }

    // ===== STATIC DISPLAY METHODS (shared with GarrickRegistryCommand) =====

    public static void showRaceMenu(net.minecraft.server.level.ServerPlayer player) {
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("📜 GUILD REGISTRY — HERITAGE").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        sendStaticDialogue(player, "Every adventurer must be registered. First — your heritage.", ChatFormatting.WHITE);
        player.sendSystemMessage(Component.empty());

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ ⚖ Human ]")
                .withStyle(ChatFormatting.WHITE)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg race human"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Balanced and adaptable.\n+25% XP from all sources.\nBonus from fishing and farming.")))))
            .append(Component.literal("  Balanced. +25% XP.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ ⛏ Dwarf ]")
                .withStyle(ChatFormatting.GOLD)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg race dwarf"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Masters of stone and metal.\n+20% mining speed.\nBonus from smelting and smithing.")))))
            .append(Component.literal("  Mining masters. +20% speed underground.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ 🌿 Elf ]")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg race elf"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("One with nature.\nEnhanced woodcutting and archery.\nMovement speed bonus.")))))
            .append(Component.literal("  Nature affinity. Speed & archery bonuses.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ 💪 Orc ]")
                .withStyle(ChatFormatting.DARK_RED)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg race orc"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Born for battle.\nEnhanced combat and hunting.\nBonus damage to animals.")))))
            .append(Component.literal("  Combat-born. Enhanced damage & hunting.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("  ✦ Click your heritage to register it.").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void showClassMenu(net.minecraft.server.level.ServerPlayer player) {
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("📜 GUILD REGISTRY — CALLING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        sendStaticDialogue(player, "Good. Now — how do you fight? Choose your calling.", ChatFormatting.WHITE);
        player.sendSystemMessage(Component.empty());

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ ⚔ Warrior ]")
                .withStyle(ChatFormatting.RED)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg class warrior"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Heavy armor, shield, 5 combat abilities.\nResource: Cooldowns")))))
            .append(Component.literal("  Heavy armor, melee dominance, 5 abilities.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ ✦ Mage ]")
                .withStyle(ChatFormatting.AQUA)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg class mage"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Wands, spells, arcane power.\nResource: Mana pool")))))
            .append(Component.literal("  Arcane power, wands & spells, mana system.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(
            Component.literal("  ").append(
            Component.literal("[ ⚡ Rogue ]")
                .withStyle(ChatFormatting.DARK_GREEN)
                .withStyle(s -> s
                    .withClickEvent(new ClickEvent.RunCommand("/guildreg class rogue"))
                    .withHoverEvent(new HoverEvent.ShowText(
                        Component.literal("Speed, stealth, 7 energy abilities.\nResource: Energy pool")))))
            .append(Component.literal("  Fast strikes, stealth, 7 energy abilities.").withStyle(ChatFormatting.GRAY)));

        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("  ✦ Click your calling to register it.").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void showRegistrationComplete(net.minecraft.server.level.ServerPlayer player) {
        String raceName = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
        String className = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("✓ GUILD REGISTRY COMPLETE").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        sendStaticDialogue(player, "The ledger has your name: " + raceName + " " + className + ".", ChatFormatting.GOLD);
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("📚 FINAL STEP: GET YOUR QUEST BOOK").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("   You should now have all 3 of my quest notes:").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("   • Garrick's First Note").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("   • Garrick's Second Note").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("   • Garrick's Third Note").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("   ➤ Find a Quest Block (ornate bookshelf)").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("   ➤ Right-click it with all 3 notes to get your Novice Quest Book!").withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.empty());
        sendStaticDialogue(player, "Quest Blocks look like ornate bookshelves with decorated tops.", ChatFormatting.YELLOW);
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("⚔ ONE MORE THING").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("   When you reach the Hall of Champions, find the §dClass Trainer§r.").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("   They'll give you your class quest chain — that's how you unlock your abilities.").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.empty());
        player.sendSystemMessage(Component.literal("═══════════════════════════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        sendStaticDialogue(player, "Safe travels, " + raceName + " " + className + ". The world awaits!", ChatFormatting.GREEN);

        // Give Hall Locator once — only if player doesn't already have one
        boolean hasLocator = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() == com.github.hitman20081.dagmod.item.ModItems.HALL_LOCATOR) {
                hasLocator = true;
                break;
            }
        }
        if (!hasLocator) {
            player.addItem(new net.minecraft.world.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.HALL_LOCATOR));
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("✦ Received: Hall Locator").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("   Right-click it to find the Hall of Champions.").withStyle(ChatFormatting.GRAY));
        }
    }

    private static void sendStaticDialogue(net.minecraft.server.level.ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(
            Component.literal("[Innkeeper Garrick] ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(message).withStyle(color)));
    }


    /**
     * Helper method to send formatted dialogue to the player
     */
    private void sendDialogue(Player player, String message, ChatFormatting color) {
        player.sendSystemMessage(
                Component.literal("[Innkeeper Garrick] ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                        .append(Component.literal(message).withStyle(color)));
    }

    @Override
    public boolean isPersistenceRequired() {
        return true; // Innkeeper never despawns
    }

    public boolean damage(DamageSource source, float amount) {
        return false; // Ignore all damage - this is the main invulnerability method
    }

    public void pushAwayFrom(net.minecraft.world.entity.Entity entity) {
        // Don't get pushed by other entities
    }
}