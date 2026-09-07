package com.github.hitman20081.dagmod.quest.rewards;

import com.github.hitman20081.dagmod.economy.CoinPouchUtil;
import com.github.hitman20081.dagmod.economy.CoinTier;
import com.github.hitman20081.dagmod.quest.QuestReward;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Pays out coins directly into a Coin Pouch balance if the player is carrying one, so earnings
 * never have to be manually picked up and deposited. Falls back to dropping loose coin items
 * (same insertion behavior as ItemReward) for players who don't have a pouch yet.
 */
public class CoinReward extends QuestReward {
    private final CoinTier tier;
    private final int amount;

    public CoinReward(CoinTier tier, int amount) {
        super(RewardType.CURRENCY, amount + "x " + tier.getDisplayName() + " Coin" + (amount == 1 ? "" : "s"));
        this.tier = tier;
        this.amount = amount;
    }

    private MutableComponent buildRewardText() {
        return Component.literal(amount + "x " + tier.getDisplayName() + " Coin" + (amount == 1 ? "" : "s"));
    }

    @Override
    public boolean giveReward(Player player, Level world) {
        ItemStack pouch = CoinPouchUtil.findPouch(player);
        if (!pouch.isEmpty()) {
            CoinPouchUtil.deposit(pouch, tier, amount);
        } else {
            ItemStack rewardStack = new ItemStack(tier.getItem(), amount);
            boolean success = player.getInventory().add(rewardStack);
            if (!success) {
                player.drop(rewardStack, false);
            }
        }

        player.sendSystemMessage(createSuccessMessage());
        return true;
    }

    @Override
    public boolean canGiveReward(Player player) {
        return true; // Always deliverable -- drops on the ground if the inventory is full
    }

    public CoinTier getTier() { return tier; }
    public int getAmount() { return amount; }

    @Override
    public Component getDisplayText() {
        return Component.literal("• ").append(buildRewardText());
    }

    @Override
    protected Component createSuccessMessage() {
        return Component.literal("Quest reward received: ").append(buildRewardText());
    }
}
