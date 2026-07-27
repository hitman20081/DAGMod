package com.github.hitman20081.dagmod.enchantment;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

/**
 * Utility class for checking if an item is in the "shatterproof broken" state.
 * An item is shatterproof-broken when it has the Shatterproof enchantment
 * and its durability is at maxDamage - 1 (the cap imposed by ShatterproofMixin).
 */
public class ShatterproofHelper {

    /**
     * Returns true if the item has the Shatterproof enchantment and is at minimum durability
     * (maxDamage - 1). Items in this state have 99% reduced effectiveness.
     */
    public static boolean isShatterproofBroken(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getMaxDamage() <= 0) return false;
        if (stack.getDamageValue() < stack.getMaxDamage() - 1) return false;

        ItemEnchantments enchantments = stack.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Holder<Enchantment> entry : enchantments.keySet()) {
            if (entry.unwrapKey().isPresent()) {
                var id = entry.unwrapKey().get().identifier();
                if (id.getNamespace().equals("dagmod") && id.getPath().equals("shatterproof")) {
                    return true;
                }
            }
        }
        return false;
    }
}
