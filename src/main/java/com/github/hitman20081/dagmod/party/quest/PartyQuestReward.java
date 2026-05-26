package com.github.hitman20081.dagmod.party.quest;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

/**
 * Represents a reward for completing a party quest
 */
public class PartyQuestReward {
    private final ItemStack itemStack;
    private final int quantity;

    public PartyQuestReward(ItemStack itemStack, int quantity) {
        this.itemStack = itemStack;
        this.quantity = quantity;
    }

    public PartyQuestReward(ItemStack itemStack) {
        this(itemStack, 1);
    }

    // Helper constructor for creating rewards from item IDs
    public static PartyQuestReward fromId(String itemId, int count) {
        ItemStack stack = new ItemStack(
                BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId))
        );
        return new PartyQuestReward(stack, count);
    }

    public ItemStack getItemStack() {
        return itemStack.copy();
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemStack createRewardStack() {
        ItemStack reward = itemStack.copy();
        reward.setCount(quantity);
        return reward;
    }

    @Override
    public String toString() {
        return quantity + "x " + itemStack.getHoverName().getString();
    }
}