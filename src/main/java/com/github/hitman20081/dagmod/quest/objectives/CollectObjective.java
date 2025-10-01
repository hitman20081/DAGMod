package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CollectObjective extends QuestObjective {
    private final Item targetItem;
    private final int requiredAmount;

    public CollectObjective(Item targetItem, int requiredAmount) {
        super(createDescription(targetItem, requiredAmount), requiredAmount);
        this.targetItem = targetItem;
        this.requiredAmount = requiredAmount;
    }

    private static String createDescription(Item item, int amount) {
        String itemName = item.getName().getString();
        return "Collect " + amount + " " + itemName;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.COLLECT;
    }

    @Override
    public boolean updateProgress(PlayerEntity player, Object... params) {
        // Count how many of the target item the player has
        int itemCount = countItemInInventory(player, targetItem);

        // Update progress based on current inventory
        int oldProgress = currentProgress;
        setProgress(itemCount);

        // Return true if progress was made
        return currentProgress > oldProgress;
    }

    // Count specific item in player's inventory
    private int countItemInInventory(PlayerEntity player, Item targetItem) {
        int count = 0;

        // Check main inventory
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem) {
                count += stack.getCount();
            }
        }

        return count;
    }

    // Method to consume items when quest is turned in
    public boolean consumeItems(PlayerEntity player) {
        if (!isCompleted()) {
            return false;
        }

        int itemsToRemove = requiredAmount;

        // Remove items from inventory
        for (int i = 0; i < player.getInventory().size() && itemsToRemove > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem) {
                int removeFromStack = Math.min(itemsToRemove, stack.getCount());
                stack.decrement(removeFromStack);
                itemsToRemove -= removeFromStack;
            }
        }

        return itemsToRemove == 0; // Returns true if all items were successfully removed
    }

    // Check if player has enough items without consuming them
    public boolean hasRequiredItems(PlayerEntity player) {
        return countItemInInventory(player, targetItem) >= requiredAmount;
    }

    // Getters
    public Item getTargetItem() { return targetItem; }
    public int getRequiredAmount() { return requiredAmount; }

    // Helper method to create CollectObjective from item identifier
    public static CollectObjective fromIdentifier(String itemId, int amount) {
        Item item = Registries.ITEM.get(Identifier.of(itemId));
        return new CollectObjective(item, amount);
    }
}