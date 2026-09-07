package com.github.hitman20081.dagmod.mixin.client;

import com.github.hitman20081.dagmod.economy.client.CoinPouchMouseActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Adds a CoinPouchMouseActions entry to every inventory screen's itemSlotMouseActions list, the
 * same extension point vanilla registers its own BundleMouseActions through (see init(), which
 * clears the list and re-adds BundleMouseActions each time a screen opens). Injects at TAIL, not
 * HEAD, specifically so this runs AFTER that clear()+re-add -- otherwise vanilla's own init()
 * would immediately wipe this addition on the very next line.
 */
@Mixin(AbstractContainerScreen.class)
@Environment(EnvType.CLIENT)
public class CoinPouchMouseActionMixin {

    @Shadow
    @Final
    private List<ItemSlotMouseAction> itemSlotMouseActions;

    @Inject(method = "init", at = @At("TAIL"))
    private void dagmod$addCoinPouchMouseAction(CallbackInfo ci) {
        itemSlotMouseActions.add(new CoinPouchMouseActions());
    }
}
