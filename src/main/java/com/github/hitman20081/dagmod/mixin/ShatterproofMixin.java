package com.github.hitman20081.dagmod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
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
            ItemEnchantments enchantments = self.getOrDefault(
                    DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            for (Holder<Enchantment> entry : enchantments.keySet()) {
                if (entry.unwrapKey().isPresent()) {
                    Identifier id = entry.unwrapKey().get().identifier();
                    if (id.getNamespace().equals("dagmod") && id.getPath().equals("shatterproof")) {
                        return getMaxDamage() - 1;
                    }
                }
            }
        }
        return damage;
    }
}
