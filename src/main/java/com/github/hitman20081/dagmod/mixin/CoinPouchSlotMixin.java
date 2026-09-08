package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchSlotAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Persists the Coin Pouch's dedicated slot (see CoinPouchPlayerMixin for the field itself, shared
 * with the client) to the player's own save data, the same file/lifecycle as everything else
 * about them -- not a separate store to keep in sync, and not scanned/touched by anything that
 * only iterates player.getInventory() (shift-click, quick-move, Q-drop), which is what actually
 * makes it structurally safe from those rather than needing each one individually blocked.
 */
@Mixin(ServerPlayer.class)
public class CoinPouchSlotMixin {

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void dagmod$saveCoinPouchSlot(ValueOutput output, CallbackInfo ci) {
        ItemStack pouch = ((CoinPouchSlotAccess) this).dagmod$getCoinPouchSlot();
        if (!pouch.isEmpty()) {
            output.store("dagmod_coin_pouch_slot", ItemStack.CODEC, pouch);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void dagmod$loadCoinPouchSlot(ValueInput input, CallbackInfo ci) {
        ItemStack pouch = input.read("dagmod_coin_pouch_slot", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        ((CoinPouchSlotAccess) this).dagmod$setCoinPouchSlot(pouch);
    }
}
