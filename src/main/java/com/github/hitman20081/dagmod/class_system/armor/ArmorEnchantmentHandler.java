package com.github.hitman20081.dagmod.class_system.armor;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.RegistryKey;

import java.util.*;

public class ArmorEnchantmentHandler {

    private record EnchantmentEntry(RegistryKey<Enchantment> enchantment, int level) {}

    private static final Map<Item, List<EnchantmentEntry>> ITEM_ENCHANTMENTS = new HashMap<>();

    static {
        // === MYTHRIL - Balanced ===
        List<EnchantmentEntry> mythrilBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> mythrilBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_HELMET, mythrilBase);
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_CHESTPLATE, mythrilBase);
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_LEGGINGS, mythrilBase);
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_BOOTS, mythrilBoots);

        // === DRAGONSCALE - Elite ===
        List<EnchantmentEntry> dragonBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 4),
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> dragonBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 4),
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 4)
        );
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONSCALE_HELMET, dragonBase);
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONSCALE_CHESTPLATE, dragonBase);
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONSCALE_LEGGINGS, dragonBase);
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONSCALE_BOOTS, dragonBoots);

        // === INFERNO - Fire ===
        List<EnchantmentEntry> infernoBase = List.of(
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 4),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> infernoBoots = List.of(
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 4),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_HELMET, infernoBase);
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_CHESTPLATE, infernoBase);
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_LEGGINGS, infernoBase);
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_BOOTS, infernoBoots);

        // === CRYSTALFORGE - Arcane/Blast ===
        List<EnchantmentEntry> crystalBase = List.of(
                new EnchantmentEntry(Enchantments.BLAST_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> crystalBoots = List.of(
                new EnchantmentEntry(Enchantments.BLAST_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTALFORGE_HELMET, crystalBase);
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTALFORGE_CHESTPLATE, crystalBase);
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTALFORGE_LEGGINGS, crystalBase);
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTALFORGE_BOOTS, crystalBoots);

        // === NATURESGUARD - Nature/Thorns ===
        List<EnchantmentEntry> natureBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 2)
        );
        List<EnchantmentEntry> natureChest = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 2),
                new EnchantmentEntry(Enchantments.THORNS, 2)
        );
        List<EnchantmentEntry> natureBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 2),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.NATURESGUARD_HELMET, natureBase);
        ITEM_ENCHANTMENTS.put(ModItems.NATURESGUARD_CHESTPLATE, natureChest);
        ITEM_ENCHANTMENTS.put(ModItems.NATURESGUARD_LEGGINGS, natureBase);
        ITEM_ENCHANTMENTS.put(ModItems.NATURESGUARD_BOOTS, natureBoots);

        // === SHADOW - Stealth ===
        List<EnchantmentEntry> shadowBase = List.of(
                new EnchantmentEntry(Enchantments.PROJECTILE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> shadowLegs = List.of(
                new EnchantmentEntry(Enchantments.PROJECTILE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.SWIFT_SNEAK, 2)
        );
        List<EnchantmentEntry> shadowBoots = List.of(
                new EnchantmentEntry(Enchantments.PROJECTILE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 4)
        );
        ITEM_ENCHANTMENTS.put(ModItems.SHADOW_HELMET, shadowBase);
        ITEM_ENCHANTMENTS.put(ModItems.SHADOW_CHESTPLATE, shadowBase);
        ITEM_ENCHANTMENTS.put(ModItems.SHADOW_LEGGINGS, shadowLegs);
        ITEM_ENCHANTMENTS.put(ModItems.SHADOW_BOOTS, shadowBoots);

        // === FROSTBOUND - Frost ===
        List<EnchantmentEntry> frostBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> frostBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FROST_WALKER, 2),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.FROSTBOUND_HELMET, frostBase);
        ITEM_ENCHANTMENTS.put(ModItems.FROSTBOUND_CHESTPLATE, frostBase);
        ITEM_ENCHANTMENTS.put(ModItems.FROSTBOUND_LEGGINGS, frostBase);
        ITEM_ENCHANTMENTS.put(ModItems.FROSTBOUND_BOOTS, frostBoots);

        // === SOLARWEAVE - Sun ===
        List<EnchantmentEntry> solarBase = List.of(
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> solarBoots = List.of(
                new EnchantmentEntry(Enchantments.FIRE_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.SOLARWEAVE_HELMET, solarBase);
        ITEM_ENCHANTMENTS.put(ModItems.SOLARWEAVE_CHESTPLATE, solarBase);
        ITEM_ENCHANTMENTS.put(ModItems.SOLARWEAVE_LEGGINGS, solarBase);
        ITEM_ENCHANTMENTS.put(ModItems.SOLARWEAVE_BOOTS, solarBoots);

        // === STORMCALLER - Storm ===
        List<EnchantmentEntry> stormBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROJECTILE_PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        List<EnchantmentEntry> stormBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 3),
                new EnchantmentEntry(Enchantments.PROJECTILE_PROTECTION, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.STORMCALLER_HELMET, stormBase);
        ITEM_ENCHANTMENTS.put(ModItems.STORMCALLER_CHESTPLATE, stormBase);
        ITEM_ENCHANTMENTS.put(ModItems.STORMCALLER_LEGGINGS, stormBase);
        ITEM_ENCHANTMENTS.put(ModItems.STORMCALLER_BOOTS, stormBoots);

        // === OBSIDIAN - Tank ===
        List<EnchantmentEntry> obsidianBase = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 4),
                new EnchantmentEntry(Enchantments.BLAST_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 4)
        );
        List<EnchantmentEntry> obsidianChest = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 4),
                new EnchantmentEntry(Enchantments.BLAST_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 4),
                new EnchantmentEntry(Enchantments.THORNS, 3)
        );
        List<EnchantmentEntry> obsidianBoots = List.of(
                new EnchantmentEntry(Enchantments.PROTECTION, 4),
                new EnchantmentEntry(Enchantments.BLAST_PROTECTION, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 4),
                new EnchantmentEntry(Enchantments.FEATHER_FALLING, 4)
        );
        ITEM_ENCHANTMENTS.put(ModItems.OBSIDIAN_HELMET, obsidianBase);
        ITEM_ENCHANTMENTS.put(ModItems.OBSIDIAN_CHESTPLATE, obsidianChest);
        ITEM_ENCHANTMENTS.put(ModItems.OBSIDIAN_LEGGINGS, obsidianBase);
        ITEM_ENCHANTMENTS.put(ModItems.OBSIDIAN_BOOTS, obsidianBoots);

        // =============================================
        // MYTHRIL TOOLS
        // =============================================

        // Mythril Sword - Balanced melee
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_SWORD, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Mythril Pickaxe - Mining
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_PICKAXE, List.of(
                new EnchantmentEntry(Enchantments.EFFICIENCY, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.FORTUNE, 2)
        ));

        // Mythril Axe - Chopping + Combat
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_AXE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 3),
                new EnchantmentEntry(Enchantments.EFFICIENCY, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Mythril Shovel - Digging
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_SHOVEL, List.of(
                new EnchantmentEntry(Enchantments.EFFICIENCY, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Mythril Hoe - Farming
        ITEM_ENCHANTMENTS.put(ModItems.MYTHRIL_HOE, List.of(
                new EnchantmentEntry(Enchantments.EFFICIENCY, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // =============================================
        // SPECIAL WEAPONS
        // =============================================

        // Dragonscale Sword - Elite fire dragon blade
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONSCALE_SWORD, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.FIRE_ASPECT, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.LOOTING, 2)
        ));

        // Inferno Sword - Pure fire
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_SWORD, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 3),
                new EnchantmentEntry(Enchantments.FIRE_ASPECT, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Bloodthirster Blade - Vampiric, max damage + loot
        ITEM_ENCHANTMENTS.put(ModItems.BLOODTHIRSTER_BLADE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 5),
                new EnchantmentEntry(Enchantments.LOOTING, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Crystal Katana - Precise, fast strikes
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTAL_KATANA, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.SWEEPING_EDGE, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Ethereal Blade - Top-tier ethereal power
        ITEM_ENCHANTMENTS.put(ModItems.ETHEREAL_BLADE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 5),
                new EnchantmentEntry(Enchantments.SMITE, 4),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.LOOTING, 2)
        ));

        // Frostbite Axe - Frost-infused heavy strikes
        ITEM_ENCHANTMENTS.put(ModItems.FROSTBITE_AXE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.EFFICIENCY, 4),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Gilded Rapier - Lucky gold-themed duelist
        ITEM_ENCHANTMENTS.put(ModItems.GILDED_RAPIER, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 3),
                new EnchantmentEntry(Enchantments.LOOTING, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 2)
        ));

        // Poison Fang Spear - Nature/poison
        ITEM_ENCHANTMENTS.put(ModItems.POISON_FANG_SPEAR, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 3),
                new EnchantmentEntry(Enchantments.BANE_OF_ARTHROPODS, 4),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Shadowfang Dagger - Quick stealth strikes
        ITEM_ENCHANTMENTS.put(ModItems.SHADOWFANG_DAGGER, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Shadowfang Sword - Shadow sweep attacks
        ITEM_ENCHANTMENTS.put(ModItems.SHADOWFANG_SWORD, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.SWEEPING_EDGE, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Thunder Pike - Storm knockback
        ITEM_ENCHANTMENTS.put(ModItems.THUNDER_PIKE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.KNOCKBACK, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Crystalhammer - Heavy crushing blows
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTALHAMMER, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.KNOCKBACK, 2),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Solar Bow - Fire-infused ranged
        ITEM_ENCHANTMENTS.put(ModItems.SOLAR_BOW, List.of(
                new EnchantmentEntry(Enchantments.POWER, 5),
                new EnchantmentEntry(Enchantments.FLAME, 3),
                new EnchantmentEntry(Enchantments.PUNCH, 2),
                new EnchantmentEntry(Enchantments.INFINITY, 1),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // Phantom Blade - Fast stealth strikes
        ITEM_ENCHANTMENTS.put(ModItems.PHANTOM_BLADE, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 4),
                new EnchantmentEntry(Enchantments.SWEEPING_EDGE, 3),
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        ));

        // True King Sword - Supreme royal blade
        ITEM_ENCHANTMENTS.put(ModItems.TRUE_KING_SWORD, List.of(
                new EnchantmentEntry(Enchantments.SHARPNESS, 10),
                new EnchantmentEntry(Enchantments.SMITE, 10),
                new EnchantmentEntry(Enchantments.UNBREAKING, 10)
        ));

        // =============================================
        // SHIELDS
        // =============================================

        // Uncommon tier shields - Unbreaking II
        List<EnchantmentEntry> shieldUncommon = List.of(
                new EnchantmentEntry(Enchantments.UNBREAKING, 2)
        );
        ITEM_ENCHANTMENTS.put(ModItems.CRYSTAL_SHIELD, shieldUncommon);
        ITEM_ENCHANTMENTS.put(ModItems.NATURE_SHIELD, shieldUncommon);

        // Rare tier shields - Unbreaking III
        List<EnchantmentEntry> shieldRare = List.of(
                new EnchantmentEntry(Enchantments.UNBREAKING, 3)
        );
        ITEM_ENCHANTMENTS.put(ModItems.INFERNO_SHIELD, shieldRare);
        ITEM_ENCHANTMENTS.put(ModItems.SHADOW_SHIELD, shieldRare);
        ITEM_ENCHANTMENTS.put(ModItems.FROST_SHIELD, shieldRare);
        ITEM_ENCHANTMENTS.put(ModItems.SOLAR_SHIELD, shieldRare);

        // Epic tier shields - Unbreaking III + Mending
        List<EnchantmentEntry> shieldEpic = List.of(
                new EnchantmentEntry(Enchantments.UNBREAKING, 3),
                new EnchantmentEntry(Enchantments.MENDING, 1)
        );
        ITEM_ENCHANTMENTS.put(ModItems.DRAGONBONE_SHIELD, shieldEpic);
        ITEM_ENCHANTMENTS.put(ModItems.STORMGUARD_SHIELD, shieldEpic);
        ITEM_ENCHANTMENTS.put(ModItems.CELESTIAL_SHIELD, shieldEpic);
    }

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    public static void tick(ServerPlayerEntity player) {
        for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty()) continue;

            List<EnchantmentEntry> entries = ITEM_ENCHANTMENTS.get(stack.getItem());
            if (entries == null) continue;

            // Check if already enchanted by us
            NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
            if (nbt.getBoolean("dagmod_enchanted").orElse(false)) continue;

            // Apply enchantments
            var enchReg = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
                    stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT));

            for (EnchantmentEntry entry : entries) {
                Optional<RegistryEntry.Reference<Enchantment>> enchEntry = enchReg.getOptional(entry.enchantment());
                enchEntry.ifPresent(ref -> builder.add(ref, entry.level()));
            }

            stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());

            // Mark as enchanted
            nbt.putBoolean("dagmod_enchanted", true);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }
}
