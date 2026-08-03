package com.github.hitman20081.dagmod.class_system.mage;

import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Applies the Mage-only Arcane Amplification enchantment (worn on a helmet) to a spell's power
 * multiplier. Folds in alongside the existing Overcharge Dust multiplier from SpellModifierHandler
 * at each ability's activate() call site.
 */
public class MageEnchantmentBonus {

    private MageEnchantmentBonus() {}

    public static float applyAmplification(ServerPlayer player, float basePower) {
        int level = CustomEnchantmentEffects.getEnchantmentLevel(
                player.getItemBySlot(EquipmentSlot.HEAD), player.level(), "mage_arcane_amplification");
        return basePower * (1.0f + 0.1f * level);
    }
}
