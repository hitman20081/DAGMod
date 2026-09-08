package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pins a coin pouch to whichever slot it's sitting in. PICKUP (plain left/right click on the
 * pouch's own slot) is handled entirely by CoinPouchItem#overrideOtherStackedOnMe -- that hook is
 * consulted before any of this method's own logic runs, so it already owns that case and this
 * mixin deliberately leaves ContainerInput.PICKUP alone. What it can't reach is every other way a
 * click can relocate or destroy a slot's contents: shift-click (QUICK_MOVE) to another inventory
 * section, the number-key/offhand swap (SWAP), and Q/Ctrl+Q on a hovered slot (THROW) -- all three
 * skip the item-level override entirely and go straight to this method's own removal logic.
 * Canceling at HEAD, before that logic runs, means the click is a no-op rather than a partial
 * mutation -- nothing has been removed from the slot yet at this point.
 *
 * SWAP is the odd one out: for that click type vanilla swaps the clicked slot against
 * player.getInventory().getItem(button) directly (button is a hotbar index, 0-8) rather than
 * against another menu Slot, so the pouch can be the swap's *target* without ever being the
 * clicked slotId -- e.g. hovering an empty slot and pressing the number key for the hotbar slot
 * the pouch happens to be in. Both sides of a SWAP have to be checked.
 */
@Mixin(AbstractContainerMenu.class)
public class CoinPouchContainerMixin {

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void dagmod$lockCoinPouchSlot(int slotId, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if (input != ContainerInput.QUICK_MOVE && input != ContainerInput.SWAP
                && input != ContainerInput.THROW && input != ContainerInput.PICKUP_ALL) {
            return;
        }

        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        if (slotId >= 0 && slotId < self.slots.size()) {
            Slot slot = self.getSlot(slotId);
            if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof CoinPouchItem) {
                ci.cancel();
                return;
            }
        }

        if (input == ContainerInput.SWAP && button >= 0 && button <= 8) {
            if (player.getInventory().getItem(button).getItem() instanceof CoinPouchItem) {
                ci.cancel();
            }
        }
    }
}
