package com.github.hitman20081.dagmod.mixin;

import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Removes the enchanting table's bookshelf requirement. Vanilla counts nearby valid bookshelves
 * (0-15) in EnchantmentMenu's slotsChanged lambda and feeds that count into
 * EnchantmentHelper.getEnchantmentCost() as the "power" argument, which determines how high a
 * level each of the 3 offered enchantments can roll. This forces that power to 15 (vanilla's own
 * maximum, i.e. as if surrounded by a full bookshelf ring) every time, regardless of how many
 * bookshelves are actually present.
 */
@Mixin(EnchantmentMenu.class)
public class EnchantmentTableBookshelfCapMixin {

    @ModifyVariable(
            method = "lambda$slotsChanged$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"
            ),
            index = 5
    )
    private int uncapBookshelfPower(int power) {
        return 15;
    }
}
