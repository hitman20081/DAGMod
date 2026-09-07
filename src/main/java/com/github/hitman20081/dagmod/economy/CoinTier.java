package com.github.hitman20081.dagmod.economy;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.item.Item;

/**
 * The 4 coin tiers, low to high, and their value in the pouch's base unit (copper). Ordinal
 * order matters -- next()/prev() walk the enum in declaration order.
 */
public enum CoinTier {
    COPPER("Copper", "Copper", ModItems.COIN_COPPER, 1L),
    SILVER("Silver", "Silver", ModItems.COIN_SILVER, 100L),
    GOLD("Gold", "Gold", ModItems.COIN_GOLD, 100L * 100L),
    PLATINUM("Platinum", "Platinum", ModItems.COIN_PLATINUM, 100L * 100L * 100L);

    /** 100 of a tier is worth 1 of the next tier -- used only for display/withdrawal math now. */
    public static final int EXCHANGE_RATE = 100;

    private final String displayName;
    private final String nbtKey;
    private final Item item;
    private final long unitValue;

    CoinTier(String displayName, String nbtKey, Item item, long unitValue) {
        this.displayName = displayName;
        this.nbtKey = nbtKey;
        this.item = item;
        this.unitValue = unitValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNbtKey() {
        return nbtKey;
    }

    public Item getItem() {
        return item;
    }

    /** How many base (Copper) units one coin of this tier is worth. */
    public long getUnitValue() {
        return unitValue;
    }

    /** The next tier up, or null if this is already the top (Platinum). */
    public CoinTier next() {
        int i = ordinal() + 1;
        CoinTier[] values = values();
        return i < values.length ? values[i] : null;
    }

    /** The tier one step down, or null if this is already the bottom (Copper). */
    public CoinTier prev() {
        int i = ordinal() - 1;
        return i >= 0 ? values()[i] : null;
    }

    public static CoinTier fromItem(Item item) {
        for (CoinTier tier : values()) {
            if (tier.item == item) return tier;
        }
        return null;
    }
}
