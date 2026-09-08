package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Pressing Q (or Ctrl+Q) with no screen open drops the selected hotbar item via
 * Inventory#removeFromSelected, which pulls the stack out of the slot and hands it to
 * Player#drop -- by the time Player#drop runs, the removal has already happened, so canceling
 * there alone would just delete the item instead of keeping it in place. This is the actual
 * origin point for that path, so it's where a coin pouch has to be stopped.
 */
@Mixin(Inventory.class)
public class CoinPouchInventoryMixin {

    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void dagmod$blockCoinPouchSelectedDrop(boolean useAll, CallbackInfoReturnable<ItemStack> cir) {
        Inventory self = (Inventory) (Object) this;
        ItemStack selected = self.getSelectedItem();
        if (!selected.isEmpty() && selected.getItem() instanceof CoinPouchItem) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
