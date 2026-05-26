package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.util.ModTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;

public class ModArmorMaterials {
    static ResourceKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static final ResourceKey<EquipmentAsset> MYTHRIL_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "mythril"));
    public static final ResourceKey<EquipmentAsset> DRAGONSCALE_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dragonscale"));
    public static final ResourceKey<EquipmentAsset> INFERNO_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "inferno"));
    public static final ResourceKey<EquipmentAsset> CRYSTALFORGE_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "crystalforge"));
    public static final ResourceKey<EquipmentAsset> NATURESGUARD_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "naturesguard"));
    public static final ResourceKey<EquipmentAsset> SHADOW_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "shadow"));
    public static final ResourceKey<EquipmentAsset> FROSTBOUND_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "frostbound"));
    public static final ResourceKey<EquipmentAsset> SOLARWEAVE_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "solarweave"));
    public static final ResourceKey<EquipmentAsset> STORMCALLER_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "stormcaller"));
    public static final ResourceKey<EquipmentAsset> OBSIDIAN_KEY =
            ResourceKey.create(REGISTRY_KEY, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "obsidian"));

    public static final ArmorMaterial MYTHRIL = new ArmorMaterial(
            40,  // Durability multiplier (above Netherite: 37)
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);       // Netherite: 3
                map.put(ArmorType.LEGGINGS, 7);    // Netherite: 6
                map.put(ArmorType.CHESTPLATE, 9);  // Netherite: 8
                map.put(ArmorType.HELMET, 4);      // Netherite: 3
                map.put(ArmorType.BODY, 12);
            }),
            18,  // Enchantability (Netherite: 15)
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.5f,  // Toughness (Netherite: 3.0f)
            0.12f, // Knockback Resistance (Netherite: 0.1f)
            ModTags.Items.MYTHRIL_REPAIR,
            MYTHRIL_KEY
    );

    public static final ArmorMaterial DRAGONSCALE = new ArmorMaterial(
            48,  // Elite tier - highest durability
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 5);
                map.put(ArmorType.LEGGINGS, 9);
                map.put(ArmorType.CHESTPLATE, 11);
                map.put(ArmorType.HELMET, 5);
                map.put(ArmorType.BODY, 15);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5.0f,
            0.20f,
            ModTags.Items.DRAGONSCALE_REPAIR,
            DRAGONSCALE_KEY
    );

    public static final ArmorMaterial INFERNO = new ArmorMaterial(
            42,  // High-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 5);
                map.put(ArmorType.LEGGINGS, 8);
                map.put(ArmorType.CHESTPLATE, 10);
                map.put(ArmorType.HELMET, 5);
                map.put(ArmorType.BODY, 14);
            }),
            22,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0f,
            0.15f,
            ModTags.Items.INFERNO_REPAIR,
            INFERNO_KEY
    );

    public static final ArmorMaterial CRYSTALFORGE = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            28,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            3.0f,
            0.10f,
            ModTags.Items.CRYSTALFORGE_REPAIR,
            CRYSTALFORGE_KEY
    );

    public static final ArmorMaterial NATURESGUARD = new ArmorMaterial(
            36,  // Mid-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            3.0f,
            0.10f,
            ModTags.Items.NATURESGUARD_REPAIR,
            NATURESGUARD_KEY
    );

    public static final ArmorMaterial SHADOW = new ArmorMaterial(
            35,  // Mid-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            3.0f,
            0.10f,
            ModTags.Items.SHADOW_REPAIR,
            SHADOW_KEY
    );

    public static final ArmorMaterial FROSTBOUND = new ArmorMaterial(
            42,  // High-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            3.5f,
            0.15f,
            ModTags.Items.FROSTBOUND_REPAIR,
            FROSTBOUND_KEY
    );

    public static final ArmorMaterial SOLARWEAVE = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_GOLD,
            3.0f,
            0.10f,
            ModTags.Items.SOLARWEAVE_REPAIR,
            SOLARWEAVE_KEY
    );

    public static final ArmorMaterial STORMCALLER = new ArmorMaterial(
            38,  // Mid-tier
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 12);
            }),
            22,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            3.0f,
            0.10f,
            ModTags.Items.STORMCALLER_REPAIR,
            STORMCALLER_KEY
    );

    public static final ArmorMaterial OBSIDIAN = new ArmorMaterial(
            45,  // High-tier tank
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 5);
                map.put(ArmorType.LEGGINGS, 8);
                map.put(ArmorType.CHESTPLATE, 10);
                map.put(ArmorType.HELMET, 5);
                map.put(ArmorType.BODY, 14);
            }),
            18,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.5f,
            0.25f,
            ModTags.Items.OBSIDIAN_REPAIR,
            OBSIDIAN_KEY
    );
}
