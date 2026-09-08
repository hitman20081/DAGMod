package com.github.hitman20081.dagmod.economy;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The player's dedicated Coin Pouch slot -- lives outside the normal 36-slot inventory and the
 * 9-slot hotbar entirely (see CoinPouchMenuContainer/CoinPouchPlayerMixin for where its contents
 * actually live), positioned next to the hotbar/offhand in the inventory screen. Not reachable by
 * a number key and not part of hotbar selection, same as offhand.
 *
 * mayPlace() rejecting anything but a Coin Pouch is what actually blocks a SWAP click from
 * dropping some other item in here (in either direction -- vanilla requires the destination to
 * accept the incoming stack for the swap to proceed at all, so this alone covers both "swap the
 * pouch out" and "swap something else in").
 */
public class CoinPouchMenuSlot extends Slot {

    public CoinPouchMenuSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof CoinPouchItem;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
