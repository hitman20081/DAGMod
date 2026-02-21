package com.github.hitman20081.dagmod.quest.objectives;

import com.github.hitman20081.dagmod.quest.QuestObjective;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MultiItemCollectObjective extends QuestObjective {
    private final Item[] acceptedItems;
    private final int requiredAmount;
    private final String displayName;

    public MultiItemCollectObjective(String displayName, int requiredAmount, Item... items) {
        super("Collect " + requiredAmount + " " + displayName, requiredAmount);
        this.displayName = displayName;
        this.requiredAmount = requiredAmount;
        this.acceptedItems = items.clone();
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.COLLECT;
    }

    @Override
    public boolean updateProgress(PlayerEntity player, Object... params) {
        int itemCount = countAllMatchingItems(player);

        int oldProgress = currentProgress;
        setProgress(itemCount);

        return currentProgress > oldProgress;
    }

    private int countAllMatchingItems(PlayerEntity player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && isAcceptedItem(stack.getItem()) && !hasEnchantments(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private boolean isAcceptedItem(Item item) {
        for (Item accepted : acceptedItems) {
            if (item == accepted) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasEnchantments(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        return enchantments != null && !enchantments.isEmpty();
    }

    public boolean consumeItems(PlayerEntity player) {
        if (!hasRequiredItems(player)) {
            return false;
        }

        int itemsToRemove = requiredAmount;

        for (int i = 0; i < player.getInventory().size() && itemsToRemove > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && isAcceptedItem(stack.getItem()) && !hasEnchantments(stack)) {
                int removeFromStack = Math.min(itemsToRemove, stack.getCount());
                stack.decrement(removeFromStack);
                itemsToRemove -= removeFromStack;
            }
        }

        return itemsToRemove == 0;
    }

    public boolean hasRequiredItems(PlayerEntity player) {
        return countAllMatchingItems(player) >= requiredAmount;
    }

    public Item[] getAcceptedItems() { return acceptedItems.clone(); }
    public int getRequiredAmount() { return requiredAmount; }

    @Override
    public QuestObjective copy() {
        return new MultiItemCollectObjective(this.displayName, this.requiredAmount, this.acceptedItems);
    }
}
