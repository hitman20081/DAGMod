package com.github.hitman20081.dagmod.economy;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A 1-slot Container that IS the player's CoinPouchSlotAccess field, not a copy of it kept in
 * sync -- getItem/setItem read and write straight through, so there's exactly one place this data
 * lives regardless of whether it's touched via CoinPouchItem's click overrides, this container, or
 * the raw accessor.
 */
public class CoinPouchMenuContainer implements Container {

    private final Player player;

    public CoinPouchMenuContainer(Player player) {
        this.player = player;
    }

    private CoinPouchSlotAccess access() {
        return (CoinPouchSlotAccess) player;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return access().dagmod$getCoinPouchSlot().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return access().dagmod$getCoinPouchSlot();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = access().dagmod$getCoinPouchSlot();
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemStack split = current.split(amount);
        access().dagmod$setCoinPouchSlot(current.isEmpty() ? ItemStack.EMPTY : current);
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = access().dagmod$getCoinPouchSlot();
        access().dagmod$setCoinPouchSlot(ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        access().dagmod$setCoinPouchSlot(stack);
    }

    @Override
    public void setChanged() {
        // No separate backing copy to flush -- the accessor field is the store.
    }

    @Override
    public void clearContent() {
        access().dagmod$setCoinPouchSlot(ItemStack.EMPTY);
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.player;
    }
}
