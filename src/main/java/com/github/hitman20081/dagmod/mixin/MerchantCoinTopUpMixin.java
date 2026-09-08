package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchSlotAccess;
import com.github.hitman20081.dagmod.economy.CoinPouchUtil;
import com.github.hitman20081.dagmod.economy.CoinTier;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla's MerchantMenu#tryMoveItems (fired when a player clicks a trade offer) only ever pulls
 * coins that already exist as physical ItemStacks in the player's own inventory slots (index
 * range 3..38, the same range it scans -- see moveFromInventoryToPaymentSlot) into the trade's
 * payment slots. It has no idea a Coin Pouch balance exists. This mints exactly the shortfall of
 * whichever coin tier an offer's cost requires -- straight out of the trading player's Coin Pouch
 * (its own dedicated slot, not part of this scan range -- see CoinPouchSlotAccess) -- into an
 * empty inventory slot right before vanilla's own scan runs, so trading against a coin-cost offer
 * just works off the pouch balance without ever manually withdrawing.
 *
 * The player reference comes off the same inventory-mirror slots already being scanned (any of
 * them, they're all backed by the same Inventory, which carries a `player` field) rather than
 * needing MerchantMenu to expose one directly.
 *
 * Requires the pouch to actually have a balance -- no pouch/empty pouch, no top-up, same as not
 * carrying your wallet. Applies uniformly to every merchant offer, not just coin-specific ones:
 * non-coin costs (ore, gems, etc.) simply don't match any CoinTier and are left completely alone.
 */
@Mixin(MerchantMenu.class)
public class MerchantCoinTopUpMixin {

    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 39; // exclusive, matches vanilla's own scan range

    @Inject(method = "tryMoveItems", at = @At("HEAD"))
    private void dagmod$topUpFromPouch(int index, CallbackInfo ci) {
        MerchantMenu self = (MerchantMenu) (Object) this;
        MerchantOffers offers = self.getOffers();
        if (index < 0 || index >= offers.size()) return;

        MerchantOffer offer = offers.get(index);
        dagmod$topUp(self, offer.getItemCostA(), offer.getCostA().getCount());
        offer.getItemCostB().ifPresent(costB -> dagmod$topUp(self, costB, offer.getCostB().getCount()));
    }

    private void dagmod$topUp(MerchantMenu menu, ItemCost cost, int required) {
        if (required <= 0) return;
        CoinTier tier = CoinTier.fromItem(cost.item().value());
        if (tier == null) return;

        NonNullList<Slot> slots = menu.slots;
        int end = Math.min(INV_SLOT_END, slots.size());
        if (INV_SLOT_START >= end) return;

        int have = 0;
        int firstEmptySlot = -1;
        for (int i = INV_SLOT_START; i < end; i++) {
            ItemStack stack = slots.get(i).getItem();
            if (stack.isEmpty()) {
                if (firstEmptySlot < 0) firstEmptySlot = i;
            } else if (CoinTier.fromItem(stack.getItem()) == tier) {
                have += stack.getCount();
            }
        }

        int shortfall = required - have;
        if (shortfall <= 0 || firstEmptySlot < 0) return;

        Container inventoryContainer = slots.get(INV_SLOT_START).container;
        if (!(inventoryContainer instanceof Inventory playerInventory)) return;

        ItemStack pouchStack = ((CoinPouchSlotAccess) playerInventory.player).dagmod$getCoinPouchSlot();
        if (pouchStack.isEmpty()) return;

        ItemStack minted = CoinPouchUtil.mint(pouchStack, tier, shortfall);
        if (minted.isEmpty()) return; // balance can't cover it -- trade will simply be unaffordable

        slots.get(firstEmptySlot).set(minted);
    }
}
