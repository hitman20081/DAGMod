package com.github.hitman20081.dagmod.economy;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * All read/write logic for a Coin Pouch's contents. The pouch holds a single balance -- a long
 * count of Copper-equivalent units, stored as one NBT long on the pouch's own CUSTOM_DATA
 * component -- rather than 4 separate per-tier counts. Tiers only exist as a display/withdrawal
 * format layered on top of that number (see breakdown()); there is nothing to "convert" between
 * tiers anymore, so there's no cascading and no risk of an up-conversion eating coins you wanted
 * to keep at a lower tier.
 *
 * This also sidesteps the engine's hard 99-item stack cap entirely: the balance is never a
 * physical stack, so it's never subject to it. Physical coins only get minted, in whatever
 * quantity (always <= Item.ABSOLUTE_MAX_STACK_SIZE) a specific withdrawal or trade actually
 * needs, straight out of the balance.
 */
public class CoinPouchUtil {

    private static final String BALANCE_KEY = "Balance";
    private static final String SELECTED_KEY = "SelectedTier";

    public static long getBalance(ItemStack pouch) {
        CompoundTag tag = pouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getLongOr(BALANCE_KEY, 0L);
    }

    private static void setBalance(ItemStack pouch, long value) {
        long clamped = Math.max(0L, value);
        CustomData.update(DataComponents.CUSTOM_DATA, pouch, tag -> tag.putLong(BALANCE_KEY, clamped));
    }

    public static boolean isEmpty(ItemStack pouch) {
        return getBalance(pouch) <= 0L;
    }

    public static boolean hasAtLeast(ItemStack pouch, long amount) {
        return getBalance(pouch) >= amount;
    }

    public static void addBalance(ItemStack pouch, long amount) {
        if (amount <= 0) return;
        setBalance(pouch, getBalance(pouch) + amount);
    }

    /** Deducts amount if (and only if) the balance can cover it. Returns false otherwise, unchanged. */
    public static boolean removeBalance(ItemStack pouch, long amount) {
        if (amount <= 0) return true;
        long balance = getBalance(pouch);
        if (balance < amount) return false;
        setBalance(pouch, balance - amount);
        return true;
    }

    /** Deposits a stack of physical coins of the given tier into the pouch's balance. */
    public static void deposit(ItemStack pouch, CoinTier tier, int count) {
        if (count <= 0) return;
        addBalance(pouch, tier.getUnitValue() * count);
    }

    /**
     * Mints exactly `exactCount` physical coins of `tier`, deducting their value from the
     * pouch's balance. Returns ItemStack.EMPTY (balance untouched) if the balance can't cover
     * the full amount -- this never partially mints.
     */
    public static ItemStack mint(ItemStack pouch, CoinTier tier, int exactCount) {
        if (exactCount <= 0) return ItemStack.EMPTY;
        long cost = tier.getUnitValue() * exactCount;
        if (!removeBalance(pouch, cost)) return ItemStack.EMPTY;
        return new ItemStack(tier.getItem(), exactCount);
    }

    /**
     * Withdraws up to `amount` physical coins of `tier` (capped at the engine's absolute max
     * stack size), limited by what the balance can actually afford. Returns ItemStack.EMPTY if
     * nothing could be withdrawn.
     */
    public static ItemStack withdraw(ItemStack pouch, CoinTier tier, int amount) {
        int want = Math.min(amount, Item.ABSOLUTE_MAX_STACK_SIZE);
        if (want <= 0) return ItemStack.EMPTY;

        long affordable = getBalance(pouch) / tier.getUnitValue();
        int take = (int) Math.min(want, affordable);
        if (take <= 0) return ItemStack.EMPTY;

        return mint(pouch, tier, take);
    }

    /**
     * Display-only breakdown of the balance into tiers, largest first -- purely a formatting of
     * the single balance number, recomputed fresh every call. Index matches CoinTier.ordinal().
     */
    public static long[] breakdown(ItemStack pouch) {
        long remaining = getBalance(pouch);
        CoinTier[] tiers = CoinTier.values();
        long[] counts = new long[tiers.length];
        for (int i = tiers.length - 1; i >= 0; i--) {
            counts[i] = remaining / tiers[i].getUnitValue();
            remaining %= tiers[i].getUnitValue();
        }
        return counts;
    }

    public static int getSelectedIndex(ItemStack pouch) {
        CompoundTag tag = pouch.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return Math.floorMod(tag.getIntOr(SELECTED_KEY, 0), CoinTier.values().length);
    }

    public static CoinTier getSelectedTier(ItemStack pouch) {
        return CoinTier.values()[getSelectedIndex(pouch)];
    }

    public static void cycleSelected(ItemStack pouch) {
        setSelected(pouch, getSelectedIndex(pouch) + 1);
    }

    public static void cycleSelectedReverse(ItemStack pouch) {
        setSelected(pouch, getSelectedIndex(pouch) - 1);
    }

    private static void setSelected(ItemStack pouch, int index) {
        int wrapped = Math.floorMod(index, CoinTier.values().length);
        CustomData.update(DataComponents.CUSTOM_DATA, pouch, tag -> tag.putInt(SELECTED_KEY, wrapped));
    }

    /**
     * Finds a Coin Pouch the player is carrying -- their main inventory/hotbar first, then
     * offhand -- or ItemStack.EMPTY if they aren't carrying one at all. Used to auto-credit
     * earnings and auto-fund trades without the player having to dig it out themselves.
     */
    public static ItemStack findPouch(Player player) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.getItem() instanceof CoinPouchItem) return stack;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof CoinPouchItem) return offhand;
        return ItemStack.EMPTY;
    }
}
