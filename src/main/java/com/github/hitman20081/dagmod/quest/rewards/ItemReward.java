package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class ItemReward extends QuestReward {
    private final Item item;
    private final int amount;

    public ItemReward(Item item, int amount) {
        super(RewardType.ITEM, amount + "x " + BuiltInRegistries.ITEM.getKey(item).getPath().replace('_', ' '));
        this.item = item;
        this.amount = amount;
    }

    // Build a Component with the translatable item name so the client resolves it
    private MutableComponent buildRewardText() {
        MutableComponent text = Component.literal(amount + "x ");
        text.append(item.getName(new ItemStack(item)));
        return text;
    }

    @Override
    public boolean giveReward(Player player, Level world) {
        if (!canGiveReward(player)) {
            return false;
        }

        // Create the item stack to give
        ItemStack rewardStack = new ItemStack(item, amount);

        // Try to add to player's inventory
        boolean success = player.getInventory().add(rewardStack);

        if (success) {
            // Send success message to player
            player.sendSystemMessage(createSuccessMessage());
            return true;
        } else {
            // If inventory insert failed, drop the item in the world
            player.drop(rewardStack, false);
            player.sendSystemMessage(createSuccessMessage());
            return true;
        }
    }

    @Override
    public boolean canGiveReward(Player player) {
        // Check if player has inventory space for the reward
        ItemStack testStack = new ItemStack(item, amount);

        // Try to find space without actually inserting
        return hasInventorySpace(player, testStack);
    }

    // Check if player has enough inventory space
    private boolean hasInventorySpace(Player player, ItemStack stack) {
        int remainingToInsert = stack.getCount();

        // Check each inventory slot
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);

            if (slotStack.isEmpty()) {
                // Empty slot can fit entire stack
                remainingToInsert -= Math.min(remainingToInsert, stack.getItem().getDefaultMaxStackSize());
            } else if (ItemStack.matches(slotStack, stack)) {
                // Existing stack can be combined with reward
                int spaceInSlot = slotStack.getItem().getDefaultMaxStackSize() - slotStack.getCount();
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
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
        return new ItemReward(item, amount);
    }

    // Helper method for single item rewards
    public static ItemReward single(Item item) {
        return new ItemReward(item, 1);
    }

    @Override
    public Component getDisplayText() {
        return Component.literal("• ").append(buildRewardText());
    }

    @Override
    protected Component createSuccessMessage() {
        return Component.literal("Quest reward received: ").append(buildRewardText());
    }

    @Override
    protected Component createFailureMessage() {
        return Component.literal("Could not receive: ").append(buildRewardText()).append(" (inventory full?)");
    }
}