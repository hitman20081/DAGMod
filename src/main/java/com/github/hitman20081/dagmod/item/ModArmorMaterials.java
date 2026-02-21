package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.util.ModTags;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;

public class ModArmorMaterials {
    static RegistryKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset"));

    public static final RegistryKey<EquipmentAsset> MYTHRIL_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "mythril"));
    public static final RegistryKey<EquipmentAsset> DRAGONSCALE_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "dragonscale"));
    public static final RegistryKey<EquipmentAsset> INFERNO_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "inferno"));
    public static final RegistryKey<EquipmentAsset> CRYSTALFORGE_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "crystalforge"));
    public static final RegistryKey<EquipmentAsset> NATURESGUARD_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "naturesguard"));
    public static final RegistryKey<EquipmentAsset> SHADOW_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "shadow"));
    public static final RegistryKey<EquipmentAsset> FROSTBOUND_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "frostbound"));
    public static final RegistryKey<EquipmentAsset> SOLARWEAVE_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "solarweave"));
    public static final RegistryKey<EquipmentAsset> STORMCALLER_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "stormcaller"));
    public static final RegistryKey<EquipmentAsset> OBSIDIAN_KEY =
            RegistryKey.of(REGISTRY_KEY, Identifier.of(DagMod.MOD_ID, "obsidian"));

    public static final ArmorMaterial MYTHRIL = new ArmorMaterial(
            40,  // Durability multiplier (above Netherite: 37)
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);       // Netherite: 3
                map.put(EquipmentType.LEGGINGS, 7);    // Netherite: 6
                map.put(EquipmentType.CHESTPLATE, 9);  // Netherite: 8
                map.put(EquipmentType.HELMET, 4);      // Netherite: 3
                map.put(EquipmentType.BODY, 12);
            }),
            18,  // Enchantability (Netherite: 15)
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            3.5f,  // Toughness (Netherite: 3.0f)
            0.12f, // Knockback Resistance (Netherite: 0.1f)
            ModTags.Items.MYTHRIL_REPAIR,
            MYTHRIL_KEY
    );

    public static final ArmorMaterial DRAGONSCALE = new ArmorMaterial(
            48,  // Elite tier - highest durability
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 5);
                map.put(EquipmentType.LEGGINGS, 9);
                map.put(EquipmentType.CHESTPLATE, 11);
                map.put(EquipmentType.HELMET, 5);
                map.put(EquipmentType.BODY, 15);
            }),
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            5.0f,
            0.20f,
            ModTags.Items.DRAGONSCALE_REPAIR,
            DRAGONSCALE_KEY
    );

    public static final ArmorMaterial INFERNO = new ArmorMaterial(
            42,  // High-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 5);
                map.put(EquipmentType.LEGGINGS, 8);
                map.put(EquipmentType.CHESTPLATE, 10);
                map.put(EquipmentType.HELMET, 5);
                map.put(EquipmentType.BODY, 14);
            }),
            22,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            4.0f,
            0.15f,
            ModTags.Items.INFERNO_REPAIR,
            INFERNO_KEY
    );

    public static final ArmorMaterial CRYSTALFORGE = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            28,
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            3.0f,
            0.10f,
            ModTags.Items.CRYSTALFORGE_REPAIR,
            CRYSTALFORGE_KEY
    );

    public static final ArmorMaterial NATURESGUARD = new ArmorMaterial(
            36,  // Mid-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            3.0f,
            0.10f,
            ModTags.Items.NATURESGUARD_REPAIR,
            NATURESGUARD_KEY
    );

    public static final ArmorMaterial SHADOW = new ArmorMaterial(
            35,  // Mid-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            25,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            3.0f,
            0.10f,
            ModTags.Items.SHADOW_REPAIR,
            SHADOW_KEY
    );

    public static final ArmorMaterial FROSTBOUND = new ArmorMaterial(
            42,  // High-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            3.5f,
            0.15f,
            ModTags.Items.FROSTBOUND_REPAIR,
            FROSTBOUND_KEY
    );

    public static final ArmorMaterial SOLARWEAVE = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            25,
            SoundEvents.ITEM_ARMOR_EQUIP_GOLD,
            3.0f,
            0.10f,
            ModTags.Items.SOLARWEAVE_REPAIR,
            SOLARWEAVE_KEY
    );

    public static final ArmorMaterial STORMCALLER = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 4);
                map.put(EquipmentType.LEGGINGS, 7);
                map.put(EquipmentType.CHESTPLATE, 9);
                map.put(EquipmentType.HELMET, 4);
                map.put(EquipmentType.BODY, 12);
            }),
            22,
            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
            3.0f,
            0.10f,
            ModTags.Items.STORMCALLER_REPAIR,
            STORMCALLER_KEY
    );

    public static final ArmorMaterial OBSIDIAN = new ArmorMaterial(
            45,  // High-tier tank
            Util.make(new EnumMap<>(EquipmentType.class), map -> {
                map.put(EquipmentType.BOOTS, 5);
                map.put(EquipmentType.LEGGINGS, 8);
                map.put(EquipmentType.CHESTPLATE, 10);
                map.put(EquipmentType.HELMET, 5);
                map.put(EquipmentType.BODY, 14);
            }),
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            4.5f,
            0.25f,
            ModTags.Items.OBSIDIAN_REPAIR,
            OBSIDIAN_KEY
    );
}
