package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchMenuContainer;
import com.github.hitman20081.dagmod.economy.CoinPouchMenuSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the Coin Pouch's dedicated slot to the player's own inventory screen. InventoryMenu is
 * constructed exactly once per Player instance (Player#inventoryMenu is final), same lifetime as
 * the inventory itself, on both the client's local screen and the server's copy -- so this runs
 * once and the slot exists for the whole session on both sides, no per-open logic needed.
 *
 * Position (152, 62), top right of the inventory grid next to the recipe book toggle -- visually
 * confirmed in-game; nudged 5px right from the original guess of (147, 62) to line up with the
 * main inventory columns below it.
 */
@Mixin(InventoryMenu.class)
public class CoinPouchMenuMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dagmod$addCoinPouchSlot(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
        InventoryMenu self = (InventoryMenu) (Object) this;
        self.addSlot(new CoinPouchMenuSlot(new CoinPouchMenuContainer(owner), 0, 152, 62));
    }
}
