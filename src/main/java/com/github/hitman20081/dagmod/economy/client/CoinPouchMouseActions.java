package com.github.hitman20081.dagmod.economy.client;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import com.github.hitman20081.dagmod.economy.CoinPouchUtil;
import com.github.hitman20081.dagmod.networking.CoinPouchScrollPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Lets scrolling the mouse wheel while hovering a Coin Pouch in any inventory screen cycle which
 * tier withdrawal mints -- the same UX vanilla's own BundleMouseActions gives real bundles.
 * Registered onto AbstractContainerScreen's itemSlotMouseActions list via
 * CoinPouchMouseActionMixin, alongside (not replacing) vanilla's own BundleMouseActions entry.
 *
 * Deliberately a scroll-wheel/GUI event rather than a hand interaction -- withdraw is a
 * right-click on the pouch's slot instead (see CoinPouchItem.overrideOtherStackedOnMe). An
 * earlier version used right-click/shift+right-click in hand for these, which collided with
 * Warrior Shield Bash (shift+right-click on a shield in either hand); see CoinPouchItem's class
 * doc for the full explanation. Scroll events never reach UseItemCallback at all, so there's
 * nothing to collide with.
 *
 * The local mutation below only updates the client's own copy of the stack, purely for instant
 * tooltip feedback -- it does NOT reach the server-authoritative stack. The packet send is what
 * actually makes the change stick; both apply the identical operation so they converge.
 */
public class CoinPouchMouseActions implements ItemSlotMouseAction {

    @Override
    public boolean matches(Slot slot) {
        return slot.hasItem() && slot.getItem().getItem() instanceof CoinPouchItem;
    }

    @Override
    public boolean onMouseScrolled(double scrollX, double scrollY, int slotIndex, ItemStack stack) {
        if (scrollY == 0) return false;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return false;
        int containerId = client.player.containerMenu.containerId;

        int direction = scrollY > 0 ? 1 : -1;
        if (direction > 0) {
            CoinPouchUtil.cycleSelected(stack);
        } else {
            CoinPouchUtil.cycleSelectedReverse(stack);
        }
        ClientPlayNetworking.send(new CoinPouchScrollPacket(containerId, slotIndex, direction));
        return true;
    }

    @Override
    public void onStopHovering(Slot slot) {
    }

    @Override
    public void onSlotClicked(Slot slot, ContainerInput input) {
    }
}
