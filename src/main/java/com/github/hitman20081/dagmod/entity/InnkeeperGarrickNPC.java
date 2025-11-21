package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

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
public class InnkeeperGarrickNPC extends PathAwareEntity {

    public InnkeeperGarrickNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    // This sets up the NPC's basic stats (health, speed, etc.)
    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.2); // Slower than player (barkeep doesn't wander much)
    }

    // This sets up the NPC's AI behaviors
    @Override
    protected void initGoals() {
        // Look at nearby players
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        // Wander around occasionally (barkeeps don't move much)
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.6));
        // Look around randomly
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    // This handles what happens when a player right-clicks the NPC
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getEntityWorld().isClient()) { // Only run on server side
            net.minecraft.server.network.ServerPlayerEntity serverPlayer = (net.minecraft.server.network.ServerPlayerEntity) player;

            // Track if this is first meeting
            boolean firstMeeting = !PlayerDataManager.hasMetGarrick(serverPlayer);

            // Mark that this player has met Garrick (unlocks quest blocks/job boards)
            if (firstMeeting) {
                PlayerDataManager.markMetGarrick(serverPlayer);
                player.sendMessage(
                    Text.literal("✓ Quest System Unlocked!").formatted(Formatting.GREEN, Formatting.BOLD),
                    false
                );
            }

            // Check task completion status
            boolean task1Done = PlayerDataManager.isTask1Complete(serverPlayer.getUuid());
            boolean task2Done = PlayerDataManager.isTask2Complete(serverPlayer.getUuid());
            boolean task3Done = PlayerDataManager.isTask3Complete(serverPlayer.getUuid());
            boolean allTasksDone = task1Done && task2Done && task3Done;

            // Handle dialogue based on progress
            if (allTasksDone) {
                // All tasks complete - direct player to Quest Block
                handleAllTasksComplete(serverPlayer);
            } else if (task3Done) {
                // Should never happen, but safety check
                handleAllTasksComplete(serverPlayer);
            } else if (task2Done) {
                // Task 2 done, offer Task 3 or check if they have iron
                handleTask3(serverPlayer, task3Done);
            } else if (task1Done) {
                // Task 1 done, offer Task 2 or check mob kills
                handleTask2(serverPlayer, task2Done);
            } else if (firstMeeting) {
                // First meeting - offer Task 1
                handleFirstMeeting(serverPlayer);
            } else {
                // Return visit before Task 1 complete - check if they have logs
                handleTask1(serverPlayer, task1Done);
            }
        }
        return ActionResult.SUCCESS;
    }

    /**
     * Handle first meeting - Welcome and offer Task 1
     */
    private void handleFirstMeeting(net.minecraft.server.network.ServerPlayerEntity player) {
        sendDialogue(player, "Welcome, traveler! I'm Innkeeper Garrick, keeper of this establishment.", Formatting.GOLD);
        player.sendMessage(Text.empty(), false);
        sendDialogue(player, "I see you're new to adventuring. Let me teach you the ropes!", Formatting.WHITE);
        player.sendMessage(Text.empty(), false);
        sendDialogue(player, "Before you can access the quest system, you'll need to prove yourself.", Formatting.WHITE);
        sendDialogue(player, "Complete 3 simple tasks for me, and I'll give you a proper Quest Book.", Formatting.WHITE);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
        player.sendMessage(Text.empty(), false);

        // Task 1 instructions
        player.sendMessage(Text.literal("📋 TASK 1: PROVE YOUR RESOURCEFULNESS").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("   I need wood for the inn's fireplace.").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("   ➤ Gather 10 Oak Logs").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("   ➤ Bring them back to me").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
        player.sendMessage(Text.empty(), false);

        sendDialogue(player, "Go gather those logs and come back when you're ready!", Formatting.GREEN);
    }

    /**
     * Handle Task 1 - Check for 10 Oak Logs
     */
    private void handleTask1(net.minecraft.server.network.ServerPlayerEntity player, boolean alreadyComplete) {
        if (alreadyComplete) {
            sendDialogue(player, "You've already completed the first task. Ready for the next one?", Formatting.YELLOW);
            handleTask2(player, false);
            return;
        }

        // Check if player has 10 oak logs
        int oakLogCount = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            net.minecraft.item.ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == net.minecraft.item.Items.OAK_LOG) {
                oakLogCount += stack.getCount();
            }
        }

        if (oakLogCount >= 10) {
            // Task complete!
            // Remove 10 oak logs
            int toRemove = 10;
            for (int i = 0; i < player.getInventory().size() && toRemove > 0; i++) {
                net.minecraft.item.ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == net.minecraft.item.Items.OAK_LOG) {
                    int removeFromStack = Math.min(toRemove, stack.getCount());
                    stack.decrement(removeFromStack);
                    toRemove -= removeFromStack;
                }
            }

            // Mark complete and give note
            PlayerDataManager.markTask1Complete(player);
            player.giveItemStack(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_FIRST_NOTE));

            sendDialogue(player, "Excellent work! You've proven your resourcefulness.", Formatting.GREEN);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("✓ Received: Garrick's First Note (1/3)").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.empty(), false);
            sendDialogue(player, "That's one task done. Let's move on to the next...", Formatting.WHITE);
            player.sendMessage(Text.empty(), false);

            // Offer Task 2
            handleTask2(player, false);
        } else {
            // Still working on it
            sendDialogue(player, "Still gathering those logs? Take your time!", Formatting.YELLOW);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("📋 TASK 1 REMINDER:").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("   ➤ Bring me 10 Oak Logs").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("   ➤ Current progress: " + oakLogCount + "/10").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);
            sendDialogue(player, "Oak trees are common in forests. Keep looking!", Formatting.WHITE);
        }
    }

    /**
     * Handle Task 2 - Kill 5 hostile mobs
     */
    private void handleTask2(net.minecraft.server.network.ServerPlayerEntity player, boolean alreadyComplete) {
        if (alreadyComplete) {
            // Task 2 done, give note if they don't have it
            boolean hasNote = player.getInventory().contains(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));

            if (!hasNote) {
                player.giveItemStack(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));
                sendDialogue(player, "You've proven your courage in battle!", Formatting.GREEN);
                player.sendMessage(Text.empty(), false);
                player.sendMessage(Text.literal("✓ Received: Garrick's Second Note (2/3)").formatted(Formatting.GOLD, Formatting.BOLD), false);
                player.sendMessage(Text.empty(), false);
            }

            sendDialogue(player, "Ready for the final task?", Formatting.YELLOW);
            player.sendMessage(Text.empty(), false);
            handleTask3(player, false);
            return;
        }

        // Check progress
        int mobKills = PlayerDataManager.getTask2MobKills(player.getUuid());

        if (mobKills >= 5) {
            // Just completed!
            player.giveItemStack(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_SECOND_NOTE));

            sendDialogue(player, "Impressive! You've proven your courage in battle!", Formatting.GREEN);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("✓ Received: Garrick's Second Note (2/3)").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.empty(), false);
            sendDialogue(player, "Two down, one to go. Here's your final task...", Formatting.WHITE);
            player.sendMessage(Text.empty(), false);

            handleTask3(player, false);
        } else {
            // Offer task 2
            player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("⚔ TASK 2: PROVE YOUR COURAGE").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("   The world is dangerous. Show me you can handle it.").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("   ➤ Defeat 5 hostile mobs (zombies, skeletons, etc.)").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("   ➤ Current progress: " + mobKills + "/5").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
            player.sendMessage(Text.empty(), false);

            sendDialogue(player, "Come back when you've proven yourself in combat!", Formatting.GREEN);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("💡 TIP: Hostile mobs spawn at night or in dark places.").formatted(Formatting.AQUA), false);
        }
    }

    /**
     * Handle Task 3 - Bring 1 Iron Ingot
     */
    private void handleTask3(net.minecraft.server.network.ServerPlayerEntity player, boolean alreadyComplete) {
        if (alreadyComplete) {
            handleAllTasksComplete(player);
            return;
        }

        // Check if player has iron ingot
        boolean hasIron = player.getInventory().contains(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_INGOT));

        if (hasIron) {
            // Task complete!
            // Remove 1 iron ingot
            for (int i = 0; i < player.getInventory().size(); i++) {
                net.minecraft.item.ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == net.minecraft.item.Items.IRON_INGOT) {
                    stack.decrement(1);
                    break;
                }
            }

            // Mark complete and give note
            PlayerDataManager.markTask3Complete(player);
            player.giveItemStack(new net.minecraft.item.ItemStack(com.github.hitman20081.dagmod.item.ModItems.GARRICKS_THIRD_NOTE));

            sendDialogue(player, "Perfect! You've proven your dedication to the craft!", Formatting.GREEN);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("✓ Received: Garrick's Third Note (3/3)").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.empty(), false);

            handleAllTasksComplete(player);
        } else {
            // Offer task 3
            player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("⛏ TASK 3: PROVE YOUR DEDICATION").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("   A true adventurer knows how to work with metal.").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("   ➤ Bring me 1 Iron Ingot").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("   ➤ You'll need to mine iron ore and smelt it").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
            player.sendMessage(Text.empty(), false);

            sendDialogue(player, "Find iron ore underground, then smelt it in a furnace!", Formatting.GREEN);
            player.sendMessage(Text.empty(), false);
            player.sendMessage(Text.literal("💡 TIP: Iron ore is common below y-level 64.").formatted(Formatting.AQUA), false);
        }
    }

    /**
     * Handle all tasks complete - Direct to Quest Block
     */
    private void handleAllTasksComplete(net.minecraft.server.network.ServerPlayerEntity player) {
        sendDialogue(player, "Excellent! You've completed all three tasks!", Formatting.GREEN);
        player.sendMessage(Text.empty(), false);
        sendDialogue(player, "You've proven your resourcefulness, courage, and dedication.", Formatting.WHITE);
        sendDialogue(player, "Now you're ready to become a true adventurer!", Formatting.WHITE);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("📚 FINAL STEP: GET YOUR QUEST BOOK").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("   You should now have all 3 of my quest notes:").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("   • Garrick's First Note").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("   • Garrick's Second Note").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("   • Garrick's Third Note").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("   ➤ Find a Quest Block (ornate bookshelf)").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("   ➤ Right-click it with all 3 notes in your inventory").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("   ➤ Your notes will be combined into a Novice Quest Book!").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.empty(), false);
        player.sendMessage(Text.literal("═══════════════════════════════════════════").formatted(Formatting.DARK_GRAY), false);
        player.sendMessage(Text.empty(), false);
        sendDialogue(player, "Quest Blocks look like ornate bookshelves with decorated tops.", Formatting.YELLOW);
        sendDialogue(player, "There should be one nearby. Good luck, adventurer!", Formatting.GREEN);
    }


    /**
     * Helper method to send formatted dialogue to the player
     */
    private void sendDialogue(PlayerEntity player, String message, Formatting color) {
        player.sendMessage(
                Text.literal("[Innkeeper Garrick] ").formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal(message).formatted(color)),
                false
        );
    }

    @Override
    public boolean cannotDespawn() {
        return true; // Innkeeper never despawns
    }

    public boolean damage(DamageSource source, float amount) {
        return false; // Ignore all damage - this is the main invulnerability method
    }

    public void pushAwayFrom(net.minecraft.entity.Entity entity) {
        // Don't get pushed by other entities
    }
}