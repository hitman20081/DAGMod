package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import com.github.hitman20081.dagmod.economy.CoinPouchUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CoinPouchNetworkHandler {

    /**
     * Mirrors whatever CoinPouchMouseActions already did to the client's own copy of the stack,
     * onto the server's authoritative copy -- resolved via containerId + slotIndex rather than
     * trusting anything else about the client's claimed state. If the player switched screens (or
     * the pouch moved slots) between scrolling and this packet arriving, containerId simply won't
     * match the player's currently open menu and the packet is dropped.
     */
    public static void handleScroll(ServerPlayer player, CoinPouchScrollPacket packet) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null || menu.containerId != packet.containerId()) return;
        if (packet.slotIndex() < 0 || packet.slotIndex() >= menu.slots.size()) return;

        Slot slot = menu.getSlot(packet.slotIndex());
        ItemStack stack = slot.getItem();
        if (!(stack.getItem() instanceof CoinPouchItem)) return;

        if (packet.direction() > 0) {
            CoinPouchUtil.cycleSelected(stack);
        } else if (packet.direction() < 0) {
            CoinPouchUtil.cycleSelectedReverse(stack);
        } else {
            return;
        }

        slot.setChanged();
        menu.broadcastChanges();
    }
}
