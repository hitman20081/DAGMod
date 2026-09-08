package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchSlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Backing field for the Coin Pouch's dedicated slot, on the common Player class rather than
 * ServerPlayer specifically -- this same field has to exist on the client's own Player instance
 * too (LocalPlayer), since CoinPouchMenuContainer casts whatever Player owns the inventory screen
 * to CoinPouchSlotAccess, and that runs on both sides. Actual disk persistence is server-only (see
 * CoinPouchSlotMixin); the client's copy is just what the container reads/writes for rendering
 * and click prediction, kept in sync with the server the same way every other slot is.
 */
@Mixin(Player.class)
public class CoinPouchPlayerMixin implements CoinPouchSlotAccess {

    @Unique
    private ItemStack dagmod$coinPouchSlot = ItemStack.EMPTY;

    @Override
    public ItemStack dagmod$getCoinPouchSlot() {
        return this.dagmod$coinPouchSlot;
    }

    @Override
    public void dagmod$setCoinPouchSlot(ItemStack stack) {
        this.dagmod$coinPouchSlot = stack;
    }
}
