package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ItemReward extends QuestReward {
    private final Item item;
    private final int amount;

    public ItemReward(Item item, int amount) {
        super(RewardType.ITEM, createDescription(item, amount));
        this.item = item;
        this.amount = amount;
    }

    // Create description text for the reward
    private static String createDescription(Item item, int amount) {
        String itemName = item.getName().getString();
        return amount + " " + itemName + (amount > 1 ? "s" : "");
    }

    @Override
    public boolean giveReward(PlayerEntity player, World world) {
        if (!canGiveReward(player)) {
            return false;
        }

        // Create the item stack to give
        ItemStack rewardStack = new ItemStack(item, amount);

        // Try to add to player's inventory
        boolean success = player.getInventory().insertStack(rewardStack);

        if (success) {
            // Send success message to player
            player.sendMessage(createSuccessMessage(), false);
            return true;
        } else {
            // If inventory insert failed, drop the item in the world
            player.dropItem(rewardStack, false);
            player.sendMessage(createSuccessMessage(), false);
            return true;
        }
    }

    @Override
    public boolean canGiveReward(PlayerEntity player) {
        // Check if player has inventory space for the reward
        ItemStack testStack = new ItemStack(item, amount);

        // Try to find space without actually inserting
        return hasInventorySpace(player, testStack);
    }

    // Check if player has enough inventory space
    private boolean hasInventorySpace(PlayerEntity player, ItemStack stack) {
        int remainingToInsert = stack.getCount();

        // Check each inventory slot
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack slotStack = player.getInventory().getStack(i);

            if (slotStack.isEmpty()) {
                // Empty slot can fit entire stack
                remainingToInsert -= Math.min(remainingToInsert, stack.getMaxCount());
            } else if (ItemStack.areItemsAndComponentsEqual(slotStack, stack)) {
                // Existing stack can be combined with reward
                int spaceInSlot = slotStack.getMaxCount() - slotStack.getCount();
                remainingToInsert -= Math.min(remainingToInsert, spaceInSlot);
            }

            if (remainingToInsert <= 0) {
                return true; // All items can fit
            }
        }

        return remainingToInsert <= 0;
    }

    // Getters
    public Item getItem() { return item; }
    public int getAmount() { return amount; }

    // Helper method to create ItemReward from item identifier
    public static ItemReward fromIdentifier(String itemId, int amount) {
        Item item = Registries.ITEM.get(Identifier.of(itemId));
        return new ItemReward(item, amount);
    }

    // Helper method for single item rewards
    public static ItemReward single(Item item) {
        return new ItemReward(item, 1);
    }

    // Override the success message to be more specific
    @Override
    protected net.minecraft.text.Text createSuccessMessage() {
        return net.minecraft.text.Text.literal("Quest reward received: " + description);
    }
}