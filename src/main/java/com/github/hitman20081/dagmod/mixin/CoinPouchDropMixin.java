package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.economy.CoinPouchItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Backstop for {@link CoinPouchInventoryMixin} / {@link CoinPouchContainerMixin} -- those two
 * cancel the coin pouch's removal from the inventory before it ever reaches a drop call, which is
 * what actually matters (canceling only here, after the item's already been pulled out of its
 * slot by the caller, would just delete it instead of keeping it). This mixin exists purely so any
 * OTHER path that manages to construct a drop of a coin pouch stack (creative-mode drop, a future
 * mod interaction) still fails safe instead of spawning a duplicate pouch on the ground.
 */
@Mixin(Player.class)
public class CoinPouchDropMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void dagmod$blockCoinPouchDrop(ItemStack droppedItem, boolean randomizeVelocity, CallbackInfoReturnable<ItemEntity> cir) {
        if (!droppedItem.isEmpty() && droppedItem.getItem() instanceof CoinPouchItem) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "handleCreativeModeItemDrop", at = @At("HEAD"), cancellable = true)
    private void dagmod$blockCoinPouchCreativeDrop(ItemStack itemStack, CallbackInfo ci) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof CoinPouchItem) {
            ci.cancel();
        }
    }
}
