package com.github.hitman20081.dagmod.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Removes vanilla's "Too Expensive!" cap on the anvil. Vanilla's AnvilMenu#createResult()
 * clears the result slot to empty once the computed cost reaches 40 levels (unless the player
 * has infinite materials, i.e. Creative). This replaces that literal 40 with Integer.MAX_VALUE
 * so the check never trips for any player — the result is always shown, and onTake() already
 * deducts XP levels equal to the true cost with no cap of its own.
 *
 * Note: createResult() also has an unrelated "bipush 40" used only to cap the *displayed* cost
 * to 39 in a pure-rename edge case (cosmetic only, doesn't block anything) — ordinal 2 is the
 * one that actually gates whether the result is nulled out, so only that occurrence is touched.
 */
@Mixin(AnvilMenu.class)
public class AnvilCostCapMixin {

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int removeTooExpensiveCap(int original) {
        return Integer.MAX_VALUE;
    }
}
