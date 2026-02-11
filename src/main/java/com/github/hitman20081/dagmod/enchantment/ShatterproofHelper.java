package com.github.hitman20081.dagmod.enchantment;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

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
        if (stack.getDamage() < stack.getMaxDamage() - 1) return false;

        ItemEnchantmentsComponent enchantments = stack.getOrDefault(
                DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.getKey().isPresent()) {
                Identifier id = entry.getKey().get().getValue();
                if (id.getNamespace().equals("dag009") && id.getPath().equals("shatterproof")) {
                    return true;
                }
            }
        }
        return false;
    }
}
