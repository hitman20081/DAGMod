package com.github.hitman20081.dagmod.economy;

import net.minecraft.world.item.ItemStack;

/**
 * Duck-typed accessor for the Coin Pouch's dedicated inventory slot -- implemented on every
 * Player (client and server both) by CoinPouchPlayerMixin. Persistence to disk is handled
 * separately by CoinPouchSlotMixin (ServerPlayer only); this interface is just the storage/access
 * contract other classes (CoinPouchMenuContainer, InnkeeperGarrickNPC's starter grant) code
 * against.
 */
public interface CoinPouchSlotAccess {
    ItemStack dagmod$getCoinPouchSlot();

    void dagmod$setCoinPouchSlot(ItemStack stack);
}
