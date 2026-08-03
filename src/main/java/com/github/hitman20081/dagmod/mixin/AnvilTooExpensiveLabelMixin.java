package com.github.hitman20081.dagmod.mixin;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Matches the client-side "Too Expensive!" label to the server-side reality established by
 * AnvilCostCapMixin. AnvilScreen independently re-checks "cost >= 40 && !hasInfiniteMaterials()"
 * purely to decide what text/color to render — it never looks at whether the result slot is
 * actually empty. Without this, the label kept showing (and rendering red) even though the
 * anvil had already stopped blocking the take.
 */
@Mixin(AnvilScreen.class)
public class AnvilTooExpensiveLabelMixin {

    @ModifyConstant(method = "extractLabels", constant = @Constant(intValue = 40))
    private int removeTooExpensiveLabelThreshold(int original) {
        return Integer.MAX_VALUE;
    }
}
