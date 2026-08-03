package com.github.hitman20081.dagmod.enchantment;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Map;
import java.util.UUID;

/**
 * Gates the 7 race/class-specific enchantments so only a matching player can apply them
 * via the enchanting table or anvil. Enchanting a mismatched race/class produces no effect
 * in play (see the individual combat/passive handlers), but this gate stops the enchantment
 * from being applied to gear in the first place.
 */
public class RaceClassEnchantmentGate {

    private static final Map<String, String> REQUIRED_RACE = Map.of(
            "dwarf_deep_striker", "Dwarf",
            "elf_forest_blessing", "Elf",
            "orc_berserkers_fury", "Orc",
            "human_versatile", "Human"
    );

    private static final Map<String, String> REQUIRED_CLASS = Map.of(
            "warrior_immovable", "Warrior",
            "mage_arcane_amplification", "Mage",
            "rogue_shadow_step", "Rogue"
    );

    private RaceClassEnchantmentGate() {}

    /** True if this enchantment id isn't one of the 7 gated ones, or the player matches its required race/class. */
    public static boolean isAllowed(ServerPlayer player, String enchantId) {
        UUID uuid = player.getUUID();

        String requiredRace = REQUIRED_RACE.get(enchantId);
        if (requiredRace != null) {
            return requiredRace.equals(RaceSelectionAltarBlock.getPlayerRace(uuid));
        }

        String requiredClass = REQUIRED_CLASS.get(enchantId);
        if (requiredClass != null) {
            return requiredClass.equals(ClassSelectionAltarBlock.getPlayerClass(uuid));
        }

        return true;
    }

    /** True if this enchantment id is one of the 7 race/class-gated enchantments. */
    public static boolean isGated(String enchantId) {
        return REQUIRED_RACE.containsKey(enchantId) || REQUIRED_CLASS.containsKey(enchantId);
    }

    /**
     * Strips any of the 7 gated enchantments from the stack's ENCHANTMENTS/STORED_ENCHANTMENTS
     * components that {@code player} doesn't qualify for. Called from the anvil and enchanting
     * table mixins right before the resulting item reaches the player.
     */
    public static void stripDisallowed(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        stripComponent(player, stack, DataComponents.ENCHANTMENTS);
        stripComponent(player, stack, DataComponents.STORED_ENCHANTMENTS);
    }

    private static void stripComponent(ServerPlayer player, ItemStack stack, DataComponentType<ItemEnchantments> component) {
        ItemEnchantments current = stack.getOrDefault(component, ItemEnchantments.EMPTY);
        if (current.isEmpty()) return;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        mutable.removeIf(holder -> !isAllowedHolder(player, holder));
        stack.set(component, mutable.toImmutable());
    }

    private static boolean isAllowedHolder(ServerPlayer player, Holder<Enchantment> holder) {
        if (holder.unwrapKey().isEmpty()) return true;
        Identifier id = holder.unwrapKey().get().identifier();
        if (!id.getNamespace().equals("dagmod")) return true;
        return isAllowed(player, id.getPath());
    }
}
