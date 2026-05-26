package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class CollectObjective extends QuestObjective {
    private final Item targetItem;
    private final int requiredAmount;

    public CollectObjective(Item targetItem, int requiredAmount) {
        super(createDescription(targetItem, requiredAmount), requiredAmount);
        this.targetItem = targetItem;
        this.requiredAmount = requiredAmount;
    }

    private static String createDescription(Item item, int amount) {
        String itemName = item.getName(new ItemStack(item)).getString();
        return "Collect " + amount + " " + itemName;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.COLLECT;
    }

    @Override
    public boolean updateProgress(Player player, Object... params) {
        // Count how many of the target item the player has
        int itemCount = countItemInInventory(player, targetItem);

        // Update progress based on current inventory
        int oldProgress = currentProgress;
        setProgress(itemCount);

        // Return true if progress was made
        return currentProgress > oldProgress;
    }

    // Count specific item in player's inventory (skips enchanted items)
    private int countItemInInventory(Player player, Item targetItem) {
        int count = 0;

        // Check main inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem && !hasEnchantments(stack)) {
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
        // Check if player actually has the required items in inventory
        if (!hasRequiredItems(player)) {
            return false;
        }

        int itemsToRemove = requiredAmount;

        // Remove items from inventory
        for (int i = 0; i < player.getInventory().getContainerSize() && itemsToRemove > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem && !hasEnchantments(stack)) {
                int removeFromStack = Math.min(itemsToRemove, stack.getCount());
                player.getInventory().removeItem(i, removeFromStack);
                itemsToRemove -= removeFromStack;
            }
        }

        player.getInventory().setChanged();
        return itemsToRemove == 0; // Returns true if all items were successfully removed
    }

    // Check if player has enough items without consuming them
    public boolean hasRequiredItems(Player player) {
        return countItemInInventory(player, targetItem) >= requiredAmount;
    }

    // Getters
    public Item getTargetItem() { return targetItem; }
    public int getRequiredAmount() { return requiredAmount; }

    // Helper method to create CollectObjective from item identifier
    public static CollectObjective fromIdentifier(String itemId, int amount) {
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
        return new CollectObjective(item, amount);
    }

    @Override
    public QuestObjective copy() {
        return new CollectObjective(this.targetItem, this.requiredAmount);
    }
}