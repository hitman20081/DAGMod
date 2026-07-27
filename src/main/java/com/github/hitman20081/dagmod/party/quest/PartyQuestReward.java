package com.github.hitman20081.dagmod.party.quest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Represents a reward for completing a party quest.
 * Stores Item + count rather than ItemStack to avoid constructing stacks
 * during mod initialization (before DataComponents are bound).
 */
public class PartyQuestReward {
    private final Item item;
    private final int quantity;

    public PartyQuestReward(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public PartyQuestReward(Item item) {
        this(item, 1);
    }

    public static PartyQuestReward fromId(String itemId, int count) {
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
        return new PartyQuestReward(item, count);
    }

    public ItemStack getItemStack() {
        return new ItemStack(item);
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemStack createRewardStack() {
        return new ItemStack(item, quantity);
    }

    @Override
    public String toString() {
        return quantity + "x " + createRewardStack().getHoverName().getString();
    }
}
