package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;

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
    public boolean updateProgress(Player player, Object... params) {
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
    private int countTaggedItemsInInventory(Player player) {
        int count = 0;

        // Check main inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem().builtInRegistryHolder().is(itemTag) && !hasEnchantments(stack)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean hasEnchantments(ItemStack stack) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        return enchantments != null && !enchantments.isEmpty();
    }

    // Method to consume items when quest is turned in
    public boolean consumeItems(Player player) {
        // Check actual inventory, not cached progress
        if (!hasRequiredItems(player)) {
            return false;
        }

        int itemsToRemove = requiredAmount;

        for (int i = 0; i < player.getInventory().getContainerSize() && itemsToRemove > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem().builtInRegistryHolder().is(itemTag) && !hasEnchantments(stack)) {
                int removeFromStack = Math.min(itemsToRemove, stack.getCount());
                // Use removeStack instead of stack.shrink() so the inventory slot is
                // properly cleared and markDirty() is called to sync the change to the client
                player.getInventory().removeItem(i, removeFromStack);
                itemsToRemove -= removeFromStack;
            }
        }

        player.getInventory().setChanged();
        return itemsToRemove == 0;
    }

    // Check if player has enough items without consuming them
    public boolean hasRequiredItems(Player player) {
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
        TagKey<Item> tag = TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.Identifier.parse(tagId));
        return new TagCollectObjective(tag, amount, displayName);
    }

    @Override
    public QuestObjective copy() {
        return new TagCollectObjective(this.itemTag, this.requiredAmount, this.displayName);
    }
}