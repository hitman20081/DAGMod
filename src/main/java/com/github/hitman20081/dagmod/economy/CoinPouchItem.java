package com.github.hitman20081.dagmod.economy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Deliberately has NO in-hand use() interaction at all -- every interaction happens through
 * inventory slot clicks while the pouch sits in a slot, the same model real Bundles use (see
 * CoinPouchMouseActions/CoinPouchMouseActionMixin for scroll-to-select, and
 * overrideOtherStackedOnMe below for insert/withdraw). This is deliberate, not just flavor: an
 * earlier version used shift+right-click in hand to withdraw, which collided with Warrior Shield
 * Bash (ShieldBashListener hooks UseItemCallback and claims shift+right-click for whichever hand
 * holds a shield -- if the pouch was in the other hand, vanilla's main-hand-first dispatch let
 * Shield Bash's shield check fire before the pouch ever got a chance to act, or the shield's own
 * block-raise use() ate the interaction outright). Slot clicks in an open inventory screen go
 * through AbstractContainerMenu#clicked, a completely separate path from UseItemCallback /
 * Item#use(), so there's no hand-priority ambiguity to collide with -- structurally immune, not
 * just moved to a different keybind.
 *
 * Right-click (empty cursor) on the pouch's slot withdraws a stack of the selected tier, minted
 * fresh out of the pouch's balance, same gesture real bundles use to pop out the selected item.
 * Left-click (or right-click) with a coin stack on the cursor deposits it into the balance.
 * Scroll (see CoinPouchMouseActions) cycles which tier withdrawal mints -- there's nothing to
 * convert between tiers anymore, since the pouch only ever holds one number.
 */
public class CoinPouchItem extends Item {

    public CoinPouchItem(Properties properties) {
        super(properties);
    }

    /**
     * Pouch sits in `slot`; `carried` is whatever's on the player's cursor (possibly empty).
     * Non-empty coin stack -> deposit into the pouch. Empty cursor + right-click -> withdraw the
     * selected tier onto the cursor.
     */
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pouchStack, ItemStack carried, Slot slot, ClickAction action,
                                             Player player, SlotAccess cursorAccess) {
        if (!slot.allowModification(player)) return false;

        if (carried.isEmpty()) {
            if (action != ClickAction.SECONDARY) return false; // left-click empty cursor: let vanilla pick the pouch up as normal

            CoinTier tier = CoinPouchUtil.getSelectedTier(pouchStack);
            ItemStack withdrawn = CoinPouchUtil.withdraw(pouchStack, tier, Item.ABSOLUTE_MAX_STACK_SIZE);
            if (withdrawn.isEmpty()) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUNDLE_INSERT_FAIL, SoundSource.PLAYERS, 0.5f, 0.8f);
                return true; // consume the click so an empty selected tier doesn't fall through to picking up the pouch
            }

            cursorAccess.set(withdrawn);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 0.8f, 1.0f);
            return true;
        }

        CoinTier tier = CoinTier.fromItem(carried.getItem());
        if (tier == null) return false;

        CoinPouchUtil.deposit(pouchStack, tier, carried.getCount());
        cursorAccess.set(ItemStack.EMPTY);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.8f, 1.0f);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                 Consumer<Component> tooltipAdder, TooltipFlag flag) {
        long[] breakdown = CoinPouchUtil.breakdown(stack);
        boolean any = false;
        CoinTier[] tiers = CoinTier.values();
        for (int i = tiers.length - 1; i >= 0; i--) {
            if (breakdown[i] > 0) {
                any = true;
                tooltipAdder.accept(Component.literal(tiers[i].getDisplayName() + ": " + breakdown[i]).withStyle(ChatFormatting.GRAY));
            }
        }
        if (!any) {
            tooltipAdder.accept(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
        }

        CoinTier selected = CoinPouchUtil.getSelectedTier(stack);
        tooltipAdder.accept(Component.empty());
        tooltipAdder.accept(Component.literal("Selected: " + selected.getDisplayName()).withStyle(ChatFormatting.YELLOW));
        tooltipAdder.accept(Component.literal("Scroll (in inventory) to select tier").withStyle(ChatFormatting.DARK_GRAY));
        tooltipAdder.accept(Component.literal("Right-click (empty hand): withdraw selected tier").withStyle(ChatFormatting.DARK_GRAY));
        tooltipAdder.accept(Component.literal("Merchant trades draw straight from this pouch").withStyle(ChatFormatting.DARK_GRAY));
    }
}
