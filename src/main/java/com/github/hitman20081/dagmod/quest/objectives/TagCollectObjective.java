package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

/**
 * Quest objective that accepts any item from a specified tag.
 * Perfect for quests like "Collect any logs" or "Collect any fish"
 */
public class TagCollectObjective extends QuestObjective {
    private final TagKey<Item> itemTag;
    private final int requiredAmount;
    private final String displayName;

    public TagCollectObjective(TagKey<Item> itemTag, int requiredAmount, String displayName) {
        super(createDescription(displayName, requiredAmount), requiredAmount);
        this.itemTag = itemTag;
        this.requiredAmount = requiredAmount;
        this.displayName = displayName;
    }

    private static String createDescription(String displayName, int amount) {
        return "Collect " + amount + " " + displayName;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.COLLECT;
    }

    @Override
    public boolean updateProgress(PlayerEntity player, Object... params) {
        // Count how many items matching the tag the player has
        int itemCount = countTaggedItemsInInventory(player);

        // Update progress based on current inventory
        int oldProgress = currentProgress;
        setProgress(itemCount);

        // Return true if progress was made
        return currentProgress > oldProgress;
    }

    @Override
    public boolean isCompleted() {
        // Check cached progress - this is updated by updateProgress()
        // Note: progress is ALWAYS refreshed before completion checks in the quest system
        return currentProgress >= requiredProgress;
    }

    // Count all items matching the tag in player's inventory
    private int countTaggedItemsInInventory(PlayerEntity player) {
        int count = 0;

        // Check main inventory
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isIn(itemTag)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    // Method to consume items when quest is turned in
    public boolean consumeItems(PlayerEntity player) {
        // Check actual inventory, not cached progress
        if (!hasRequiredItems(player)) {
            return false;
        }

        int itemsToRemove = requiredAmount;

        for (int i = 0; i < player.getInventory().size() && itemsToRemove > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isIn(itemTag)) {
                int removeFromStack = Math.min(itemsToRemove, stack.getCount());
                stack.decrement(removeFromStack);
                itemsToRemove -= removeFromStack;
            }
        }

        return itemsToRemove == 0;
    }

    // Check if player has enough items without consuming them
    public boolean hasRequiredItems(PlayerEntity player) {
        return countTaggedItemsInInventory(player) >= requiredAmount;
    }

    // Getters
    public TagKey<Item> getItemTag() { return itemTag; }
    public int getRequiredAmount() { return requiredAmount; }
    public String getDisplayName() { return displayName; }

    // Helper method to create TagCollectObjective from tag identifier
    public static TagCollectObjective fromTagIdentifier(String tagId, int amount, String displayName) {
        // For common tags like "logs", "planks", "wool", etc.
        // This is a convenience method for creating objectives
        TagKey<Item> tag = TagKey.of(net.minecraft.registry.RegistryKeys.ITEM,
                net.minecraft.util.Identifier.of(tagId));
        return new TagCollectObjective(tag, amount, displayName);
    }
}