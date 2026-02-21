package com.github.hitman20081.dagmod.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Prevents items with the Shatterproof enchantment from breaking.
 * Caps durability damage at maxDamage - 1 so the item sits at 1 durability.
 */
@Mixin(ItemStack.class)
public abstract class ShatterproofMixin {

    @Shadow
    public abstract int getMaxDamage();

    @ModifyVariable(method = "setDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int preventBreak(int damage) {
        ItemStack self = (ItemStack) (Object) this;
        if (damage >= getMaxDamage()) {
            // Check for shatterproof enchantment
            ItemEnchantmentsComponent enchantments = self.getOrDefault(
                    DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                if (entry.getKey().isPresent()) {
                    Identifier id = entry.getKey().get().getValue();
                    if (id.getNamespace().equals("dagmod") && id.getPath().equals("shatterproof")) {
                        return getMaxDamage() - 1;
                    }
                }
            }
        }
        return damage;
    }
}
