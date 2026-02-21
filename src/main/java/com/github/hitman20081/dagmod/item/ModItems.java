package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.item.QuestBookItem;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance", Item::new, new Item.Settings());
    public static final Item AMETHYST_POWDER = register("amethyst_powder", Item::new, new Item.Settings());
    public static final Item CITRINE_POWDER = register("citrine_powder", Item::new, new Item.Settings());
    public static final Item DIAMOND_POWDER = register("diamond_powder", Item::new, new Item.Settings());
    public static final Item EMERALD_POWDER = register("emerald_powder", Item::new, new Item.Settings());
    public static final Item ECHO_DUST = register("echo_dust", Item::new, new Item.Settings());
    public static final Item QUARTZ_POWDER = register("quartz_powder", Item::new, new Item.Settings());
    public static final Item SLIMEBALL_DUST = register("slimeball_dust", Item::new, new Item.Settings());

    // Gem Items
    public static final Item CITRINE = register("citrine", Item::new, new Item.Settings().maxCount(64));
    public static final Item RUBY = register("ruby", Item::new, new Item.Settings().maxCount(64));
    public static final Item SAPPHIRE = register("sapphire", Item::new, new Item.Settings().maxCount(64));
    public static final Item TANZANITE = register("tanzanite", Item::new, new Item.Settings().maxCount(64));
    public static final Item TOPAZ = register("topaz", Item::new, new Item.Settings().maxCount(64));
    public static final Item ZIRCON = register("zircon", Item::new, new Item.Settings().maxCount(64));
    public static final Item SILMARIL = register("silmaril", Item::new, new Item.Settings().maxCount(64).rarity(Rarity.RARE));
    public static final Item PINK_GARNET = register("pink_garnet", Item::new, new Item.Settings().maxCount(64));

    // Raw Gem Items
    public static final Item RAW_CITRINE = register("raw_citrine", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_RUBY = register("raw_ruby", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_SAPPHIRE = register("raw_sapphire", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_TANZANITE = register("raw_tanzanite", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_TOPAZ = register("raw_topaz", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_ZIRCON = register("raw_zircon", Item::new, new Item.Settings().maxCount(64));
    public static final Item RAW_PINK_GARNET = register("raw_pink_garnet", Item::new, new Item.Settings().maxCount(64));

    // Gem Crafting Tools
    public static final Item GEM_CUTTER_TOOL = register("gem_cutter_tool", Item::new,
            new Item.Settings().maxCount(1).maxDamage(32));

    // Mythril Materials
    public static final Item RAW_MYTHRIL = register("raw_mythril", Item::new, new Item.Settings().maxCount(64).fireproof());
    public static final Item MYTHRIL_INGOT = register("mythril_ingot", Item::new, new Item.Settings().maxCount(64).fireproof());
    public static final Item MYTHRIL_NUGGET = register("mythril_nugget", Item::new, new Item.Settings().maxCount(64).fireproof());

    // Mythril Tools
    public static final Item MYTHRIL_SWORD = register("mythril_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 3, -2.4f)),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_PICKAXE = register("mythril_pickaxe",
            settings -> new Item(settings.pickaxe(ModToolMaterials.MYTHRIL, 1, -2.8f)),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_AXE = register("mythril_axe",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 5.0f, -3.0f, settings),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_SHOVEL = register("mythril_shovel",
            settings -> new ShovelItem(ModToolMaterials.MYTHRIL, 1.5f, -3.0f, settings),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_HOE = register("mythril_hoe",
            settings -> new HoeItem(ModToolMaterials.MYTHRIL, 0, -3f, settings),
            new Item.Settings().fireproof());

    // Mythril Armor
    public static final Item MYTHRIL_HELMET = register("mythril_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.MYTHRIL, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_CHESTPLATE = register("mythril_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.MYTHRIL, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_LEGGINGS = register("mythril_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.MYTHRIL, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item MYTHRIL_BOOTS = register("mythril_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.MYTHRIL, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Dragonscale Armor
    public static final Item DRAGONSCALE_HELMET = register("dragonscale_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.DRAGONSCALE, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item DRAGONSCALE_CHESTPLATE = register("dragonscale_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.DRAGONSCALE, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item DRAGONSCALE_LEGGINGS = register("dragonscale_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.DRAGONSCALE, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item DRAGONSCALE_BOOTS = register("dragonscale_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.DRAGONSCALE, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Inferno Armor
    public static final Item INFERNO_HELMET = register("inferno_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.INFERNO, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item INFERNO_CHESTPLATE = register("inferno_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.INFERNO, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item INFERNO_LEGGINGS = register("inferno_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.INFERNO, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item INFERNO_BOOTS = register("inferno_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.INFERNO, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Crystalforge Armor
    public static final Item CRYSTALFORGE_HELMET = register("crystalforge_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.CRYSTALFORGE, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item CRYSTALFORGE_CHESTPLATE = register("crystalforge_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.CRYSTALFORGE, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item CRYSTALFORGE_LEGGINGS = register("crystalforge_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.CRYSTALFORGE, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item CRYSTALFORGE_BOOTS = register("crystalforge_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.CRYSTALFORGE, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Naturesguard Armor
    public static final Item NATURESGUARD_HELMET = register("naturesguard_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.NATURESGUARD, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item NATURESGUARD_CHESTPLATE = register("naturesguard_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.NATURESGUARD, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item NATURESGUARD_LEGGINGS = register("naturesguard_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.NATURESGUARD, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item NATURESGUARD_BOOTS = register("naturesguard_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.NATURESGUARD, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Shadow Armor
    public static final Item SHADOW_HELMET = register("shadow_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.SHADOW, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item SHADOW_CHESTPLATE = register("shadow_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.SHADOW, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item SHADOW_LEGGINGS = register("shadow_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.SHADOW, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item SHADOW_BOOTS = register("shadow_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.SHADOW, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Frostbound Armor
    public static final Item FROSTBOUND_HELMET = register("frostbound_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.FROSTBOUND, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item FROSTBOUND_CHESTPLATE = register("frostbound_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.FROSTBOUND, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item FROSTBOUND_LEGGINGS = register("frostbound_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.FROSTBOUND, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item FROSTBOUND_BOOTS = register("frostbound_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.FROSTBOUND, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Solarweave Armor
    public static final Item SOLARWEAVE_HELMET = register("solarweave_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.SOLARWEAVE, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item SOLARWEAVE_CHESTPLATE = register("solarweave_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.SOLARWEAVE, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item SOLARWEAVE_LEGGINGS = register("solarweave_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.SOLARWEAVE, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item SOLARWEAVE_BOOTS = register("solarweave_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.SOLARWEAVE, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Stormcaller Armor
    public static final Item STORMCALLER_HELMET = register("stormcaller_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.STORMCALLER, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item STORMCALLER_CHESTPLATE = register("stormcaller_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.STORMCALLER, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item STORMCALLER_LEGGINGS = register("stormcaller_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.STORMCALLER, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item STORMCALLER_BOOTS = register("stormcaller_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.STORMCALLER, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Obsidian Armor
    public static final Item OBSIDIAN_HELMET = register("obsidian_helmet",
            settings -> new Item(settings.armor(ModArmorMaterials.OBSIDIAN, EquipmentType.HELMET)),
            new Item.Settings().fireproof());

    public static final Item OBSIDIAN_CHESTPLATE = register("obsidian_chestplate",
            settings -> new Item(settings.armor(ModArmorMaterials.OBSIDIAN, EquipmentType.CHESTPLATE)),
            new Item.Settings().fireproof());

    public static final Item OBSIDIAN_LEGGINGS = register("obsidian_leggings",
            settings -> new Item(settings.armor(ModArmorMaterials.OBSIDIAN, EquipmentType.LEGGINGS)),
            new Item.Settings().fireproof());

    public static final Item OBSIDIAN_BOOTS = register("obsidian_boots",
            settings -> new Item(settings.armor(ModArmorMaterials.OBSIDIAN, EquipmentType.BOOTS)),
            new Item.Settings().fireproof());

    // Dragon Materials
    public static final Item DRAGON_SCALE = register("dragon_scale", Item::new, new Item.Settings().maxCount(64).rarity(Rarity.RARE).fireproof());
    public static final Item DRAGON_BONE = register("dragon_bone", Item::new, new Item.Settings().maxCount(64).rarity(Rarity.RARE).fireproof());
    public static final Item DRAGON_SKIN = register("dragon_skin", Item::new, new Item.Settings().maxCount(64).rarity(Rarity.RARE).fireproof());
    public static final Item DRAGON_HEART = register("dragon_heart", Item::new, new Item.Settings().maxCount(16).rarity(Rarity.EPIC).fireproof());
    public static final Item KINGS_SCALE = register("kings_scale", Item::new, new Item.Settings().maxCount(16).rarity(Rarity.EPIC).fireproof());

    // Dragon Eggs (dragon_egg is the block item - see ModBlocks.DRAGON_EGG_BLOCK)
    public static final Item ICE_DRAGON_EGG = register("ice_dragon_egg", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item LAVA_DRAGON_EGG = register("lava_dragon_egg", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item RED_DRAGON_EGG = register("red_dragon_egg", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item EARTH_DRAGON_EGG = register("earth_dragon_egg", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item WIND_DRAGON_EGG = register("wind_dragon_egg", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());

    // Special Weapons
    public static final Item DRAGONSCALE_SWORD = register("dragonscale_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f)),
            new Item.Settings().fireproof());

    public static final Item INFERNO_SWORD = register("inferno_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f)),
            new Item.Settings().fireproof());

    public static final Item BLOODTHIRSTER_BLADE = register("bloodthirster_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 10, -2.4f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item CRYSTAL_KATANA = register("crystal_katana",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.0f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item ETHEREAL_BLADE = register("ethereal_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 11, -2.2f).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item FROSTBITE_AXE = register("frostbite_axe",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 11.0f, -3.0f, settings.rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item GILDED_RAPIER = register("gilded_rapier",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 8, -1.8f).rarity(Rarity.UNCOMMON)),
            new Item.Settings().fireproof());

    public static final Item POISON_FANG_SPEAR = register("poison_fang_spear",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 8, -2.6f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item SHADOWFANG_DAGGER = register("shadowfang_dagger",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 7, -1.6f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item SHADOWFANG_SWORD = register("shadowfang_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item THUNDER_PIKE = register("thunder_pike",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 11, -2.8f).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item CRYSTALHAMMER = register("crystalhammer",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 12.0f, -3.2f, settings.rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item SOLAR_BOW = register("solar_bow",
            BowItem::new,
            new Item.Settings().maxDamage(500).rarity(Rarity.RARE).fireproof());

    public static final Item PHANTOM_BLADE = register("phantom_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -1.8f).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item TRUE_KING_SWORD = register("true_king_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 12, -2.4f).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    // Special Shields - Using DagModShieldItem for custom shield rendering
    public static final Item INFERNO_SHIELD = register("inferno_shield",
            settings -> new DagModShieldItem("inferno", settings.maxDamage(500).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item SHADOW_SHIELD = register("shadow_shield",
            settings -> new DagModShieldItem("shadow", settings.maxDamage(450).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item CRYSTAL_SHIELD = register("crystal_shield",
            settings -> new DagModShieldItem("crystal", settings.maxDamage(400).rarity(Rarity.UNCOMMON)),
            new Item.Settings().fireproof());

    public static final Item DRAGONBONE_SHIELD = register("dragonbone_shield",
            settings -> new DagModShieldItem("dragonbone", settings.maxDamage(600).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item FROST_SHIELD = register("frost_shield",
            settings -> new DagModShieldItem("frost", settings.maxDamage(450).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item NATURE_SHIELD = register("nature_shield",
            settings -> new DagModShieldItem("nature", settings.maxDamage(400).rarity(Rarity.UNCOMMON)),
            new Item.Settings().fireproof());

    public static final Item SOLAR_SHIELD = register("solar_shield",
            settings -> new DagModShieldItem("solar", settings.maxDamage(500).rarity(Rarity.RARE)),
            new Item.Settings().fireproof());

    public static final Item STORMGUARD_SHIELD = register("stormguard_shield",
            settings -> new DagModShieldItem("stormguard", settings.maxDamage(550).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item CELESTIAL_SHIELD = register("celestial_shield",
            settings -> new DagModShieldItem("celestial", settings.maxDamage(700).rarity(Rarity.EPIC)),
            new Item.Settings().fireproof());

    public static final Item CHICKEN_STEW = register("chicken_stew", Item::new, new Item.Settings().food(ModFoodComponents.CHICKEN_STEW));
    public static final Item BEEF_STEW = register("beef_stew", Item::new, new Item.Settings().food(ModFoodComponents.BEEF_STEW_FOOD, ModFoodComponents.BEEF_STEW_CONSUMABLE));
    public static final Item MYSTIC_STEW = register("mystic_stew", Item::new, new Item.Settings().food(ModFoodComponents.MYSTIC_STEW_FOOD, ModFoodComponents.MYSTIC_STEW_CONSUMABLE));
    public static final Item CANDIED_APPLE = register("candied_apple", Item::new, new Item.Settings().food(ModFoodComponents.CANDIED_APPLE_FOOD, ModFoodComponents.CANDIED_APPLE_CONSUMABLE));
    public static final Item CRIMSON_SOUP = register("crimson_soup", Item::new, new Item.Settings().food(ModFoodComponents.CRIMSON_SOUP_FOOD, ModFoodComponents.CRIMSON_SOUP_CONSUMABLE));
    public static final Item DRAGONFRUIT_TART = register("dragonfruit_tart", Item::new, new Item.Settings().food(ModFoodComponents.DRAGONFRUIT_TART_FOOD, ModFoodComponents.DRAGONFRUIT_TART_CONSUMABLE));
    public static final Item ELVEN_BREAD = register("elven_bread", Item::new, new Item.Settings().food(ModFoodComponents.ELVEN_BREAD_FOOD, ModFoodComponents.ELVEN_BREAD_CONSUMABLE));
    public static final Item ETHEREAL_COOKIES = register("ethereal_cookies", Item::new, new Item.Settings().food(ModFoodComponents.ETHEREAL_COOKIES_FOOD, ModFoodComponents.ETHEREAL_COOKIES_CONSUMABLE));
    public static final Item FROSTBERRY_PIE = register("frostberry_pie", Item::new, new Item.Settings().food(ModFoodComponents.FROSTBERRY_PIE_FOOD, ModFoodComponents.FROSTBERRY_PIE_CONSUMABLE));
    public static final Item GLOWBERRY_JAM = register("glowberry_jam", Item::new, new Item.Settings().food(ModFoodComponents.GLOWBERRY_JAM_FOOD, ModFoodComponents.GLOWBERRY_JAM_CONSUMABLE));
    public static final Item GOLDEN_APPLE_STRUDEL = register("golden_apple_strudel", Item::new, new Item.Settings().food(ModFoodComponents.GOLDEN_APPLE_STRUDEL_FOOD, ModFoodComponents.GOLDEN_APPLE_STRUDEL_CONSUMABLE));
    public static final Item HONEY_BREAD = register("honey_bread", Item::new, new Item.Settings().food(ModFoodComponents.HONEY_BREAD_FOOD, ModFoodComponents.HONEY_BREAD_CONSUMABLE));
    public static final Item MOLTEN_CHILI = register("molten_chili", Item::new, new Item.Settings().food(ModFoodComponents.MOLTEN_CHILI_FOOD, ModFoodComponents.MOLTEN_CHILI_CONSUMABLE));
    public static final Item NETHER_SALAD = register("nether_salad", Item::new, new Item.Settings().food(ModFoodComponents.NETHER_SALAD_FOOD, ModFoodComponents.NETHER_SALAD_CONSUMABLE));
    public static final Item PHOENIX_ROAST = register("phoenix_roast", Item::new, new Item.Settings().food(ModFoodComponents.PHOENIX_ROAST_FOOD, ModFoodComponents.PHOENIX_ROAST_CONSUMABLE));
    public static final Item PUMPKIN_PARFAIT = register("pumpkin_parfait", Item::new, new Item.Settings().food(ModFoodComponents.PUMPKIN_PARFAIT_FOOD, ModFoodComponents.PUMPKIN_PARFAIT_CONSUMABLE));
    public static final Item SAVORY_BEEF_ROAST = register("savory_beef_roast", Item::new, new Item.Settings().food(ModFoodComponents.SAVORY_BEEF_ROAST_FOOD, ModFoodComponents.SAVORY_BEEF_ROAST_CONSUMABLE));
    public static final Item SHADOW_CAKE = register("shadow_cake", Item::new, new Item.Settings().food(ModFoodComponents.SHADOW_CAKE_FOOD, ModFoodComponents.SHADOW_CAKE_CONSUMABLE));
    public static final Item SPICED_RABBIT = register("spiced_rabbit", Item::new, new Item.Settings().food(ModFoodComponents.SPICED_RABBIT_FOOD, ModFoodComponents.SPICED_RABBIT_CONSUMABLE));
    public static final Item STARFRUIT_SMOOTHIE = register("starfruit_smoothie", Item::new, new Item.Settings().food(ModFoodComponents.STARFRUIT_SMOOTHIE_FOOD, ModFoodComponents.STARFRUIT_SMOOTHIE_CONSUMABLE));
    public static final Item STORMFISH_FILLET = register("stormfish_fillet", Item::new, new Item.Settings().food(ModFoodComponents.STORMFISH_FILLET_FOOD, ModFoodComponents.STORMFISH_FILLET_CONSUMABLE));
    public static final Item VOID_TRUFFLES = register("void_truffles", Item::new, new Item.Settings().food(ModFoodComponents.VOID_TRUFFLES_FOOD, ModFoodComponents.VOID_TRUFFLES_CONSUMABLE));


    // Quest Books with actual functionality
    public static final Item NOVICE_QUEST_BOOK = register("novice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.NOVICE), new Item.Settings().maxCount(1));
    public static final Item APPRENTICE_QUEST_BOOK = register("apprentice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.APPRENTICE), new Item.Settings().maxCount(1));
    public static final Item EXPERT_QUEST_BOOK = register("expert_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.EXPERT), new Item.Settings().maxCount(1));
    public static final Item MASTER_QUEST_TOME = register("master_quest_tome",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.MASTER), new Item.Settings().maxCount(1));

    // Tutorial Quest Notes - Given by Innkeeper Garrick
    public static final Item GARRICKS_FIRST_NOTE = register("garricks_first_note",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item GARRICKS_SECOND_NOTE = register("garricks_second_note",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item GARRICKS_THIRD_NOTE = register("garricks_third_note",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    // NEW: Class Selection System Items
    public static final Item CLASS_SELECTION_TOME = register("class_selection_tome",
            ClassSelectionTomeItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final Item WARRIOR_TOKEN = register("warrior_token",
            settings -> new ClassTokenItem(settings, "Warrior", Formatting.RED, "⚔"),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item MAGE_TOKEN = register("mage_token",
            settings -> new ClassTokenItem(settings, "Mage", Formatting.AQUA, "✦"),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item ROGUE_TOKEN = register("rogue_token",
            settings -> new ClassTokenItem(settings, "Rogue", Formatting.DARK_GREEN, "⚡"),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item CLASS_RESET_CRYSTAL = register("class_reset_crystal",
            ClassResetCrystalItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // Race & Character Reset Items
    public static final Item RACE_RESET_CRYSTAL = register("race_reset_crystal",
            Item::new, new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item CHARACTER_RESET_CRYSTAL = register("character_reset_crystal",
            Item::new, new Item.Settings().maxCount(16).rarity(Rarity.EPIC));

    public static final Item POTION_OF_RACIAL_REBIRTH = register("potion_of_racial_rebirth",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item POTION_OF_CLASS_REBIRTH = register("potion_of_class_rebirth",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item POTION_OF_TOTAL_REBIRTH = register("potion_of_total_rebirth",
            Item::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // Race Tokens
    public static final Item HUMAN_TOKEN = register("human_token",
            Item::new, new Item.Settings());

    public static final Item DWARF_TOKEN = register("dwarf_token",
            Item::new, new Item.Settings());

    public static final Item ELF_TOKEN = register("elf_token",
            Item::new, new Item.Settings());

    public static final Item ORC_TOKEN = register("orc_token",
            Item::new, new Item.Settings());

    // Race Selection Tome
    public static final Item RACE_SELECTION_TOME = register("race_selection_tome",
            RaceSelectionTome::new, new Item.Settings().maxCount(1));

    // Hall Locator
    public static final Item HALL_LOCATOR = register("hall_locator",
            HallLocatorItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    // Mage Spell Scrolls
    public static final Item HEAL_SCROLL = register("heal_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.HEAL),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item FIREBALL_SCROLL = register("fireball_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.FIREBALL),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item ABSORPTION_SCROLL = register("absorption_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.ABSORPTION),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item LIGHTNING_SCROLL = register("lightning_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.LIGHTNING),
            new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item FROST_NOVA_SCROLL = register("frost_nova_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.FROST_NOVA),
            new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item TELEPORT_SCROLL = register("teleport_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.TELEPORT),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item MANA_SHIELD_SCROLL = register("mana_shield_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.MANA_SHIELD),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    // Apprentice Wand - Early game Mage weapon
    public static final Item APPRENTICE_WAND = register("apprentice_wand",
            settings -> new ApprenticeWandItem(settings),
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    // Adept Wand - Mid-game Mage weapon
    public static final Item ADEPT_WAND = register("adept_wand",
            settings -> new AdeptWandItem(settings),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    // Master Wand - Late game Mage weapon
    public static final Item MASTER_WAND = register("master_wand",
            settings -> new MasterWandItem(settings),
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // === WARRIOR ABILITY ITEMS ===

    /**
     * Rage Totem - Activates Rage ability
     * Grants +50% attack damage, +30% speed, +2 hearts absorption for 10 seconds
     * Cooldown: 60 seconds
     */
    public static final Item RAGE_TOTEM = register("rage_totem",
            RageTotemItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    /**
     * War Horn - Activates War Cry ability
     * Buffs allies and debuffs enemies in 15 block radius
     * Cooldown: 90 seconds
     */
    public static final Item WAR_HORN = register("war_horn",
            WarHornItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    // Note: Shield Bash uses vanilla shields, no new item needed
    /**
     * Battle Standard - Activates Battle Shout ability
     * Heals 6 hearts, buffs damage, removes debuffs
     * Cooldown: 45 seconds
     */
    public static final Item BATTLE_STANDARD = register("battle_standard",
            BattleStandardItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    /**
     * Whirlwind Axe - Activates Whirlwind ability
     * AoE spin attack hitting all nearby enemies
     * Cooldown: 30 seconds
     */
    public static final Item WHIRLWIND_AXE = register("whirlwind_axe",
            WhirlwindAxeItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    /**
     * Iron Talisman - Activates Iron Skin ability
     * Massive damage reduction + absorption hearts
     * Cooldown: 120 seconds
     */
    public static final Item IRON_TALISMAN = register("iron_talisman",
            IronTalismanItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // ===== NEW MAGE ABILITY ITEMS =====

    public static final Item ARCANE_ORB = register("arcane_orb",
            ArcaneOrbItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item TEMPORAL_CRYSTAL = register("temporal_crystal",
            TemporalCrystalItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item MANA_CATALYST = register("mana_catalyst",
            ManaCatalystItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item BARRIER_CHARM = register("barrier_charm",
            BarrierCharmItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // ===== ROGUE ITEMS =====

    /**
     * Rogue Ability Tome - Contains all three Rogue abilities
     * Shift+Right-click to cycle abilities, Right-click to use
     * Abilities: Smoke Bomb, Poison Dagger, Shadow Step
     */
    public static final Item ROGUE_ABILITY_TOME = register("rogue_ability_tome",
            RogueAbilityItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    // ===== COOLDOWN-BASED ROGUE ABILITIES =====

    public static final Item VOID_BLADE = register("void_blade",
            VoidBladeItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item VANISH_CLOAK = register("vanish_cloak",
            VanishCloakItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item POISON_VIAL = register("poison_vial",
            PoisonVialItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    public static final Item ASSASSINS_MARK = register("assassins_mark",
            AssassinsMarkItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // ===== CONSUMABLE ITEMS (Made from Powders) =====

    // Essential Consumables
    public static final Item MANA_CRYSTAL = register("mana_crystal",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.MANA_CRYSTAL),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item ENERGY_TONIC = register("energy_tonic",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.ENERGY_TONIC),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item COOLDOWN_ELIXIR = register("cooldown_elixir",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.COOLDOWN_ELIXIR),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item VAMPIRE_DUST = register("vampire_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.VAMPIRE_DUST),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item PHANTOM_DUST = register("phantom_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.PHANTOM_DUST),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item SPELL_ECHO = register("spell_echo",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.SPELL_ECHO),
            new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item BATTLE_FRENZY = register("battle_frenzy",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.BATTLE_FRENZY),
            new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item SHADOW_BLEND = register("shadow_blend",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.SHADOW_BLEND),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item FORTUNE_DUST = register("fortune_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.FORTUNE_DUST),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item FEATHERFALL_POWDER = register("featherfall_powder",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.FEATHERFALL_POWDER),
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    // Advanced Consumables
    public static final Item LAST_STAND_POWDER = register("last_stand_powder",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.LAST_STAND_POWDER),
            new Item.Settings().maxCount(8).rarity(Rarity.EPIC));

    public static final Item TIME_DISTORTION = register("time_distortion",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.TIME_DISTORTION),
            new Item.Settings().maxCount(8).rarity(Rarity.EPIC));

    public static final Item OVERCHARGE_DUST = register("overcharge_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.OVERCHARGE_DUST),
            new Item.Settings().maxCount(8).rarity(Rarity.EPIC));

    public static final Item TITAN_STRENGTH = register("titan_strength",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.TITAN_STRENGTH),
            new Item.Settings().maxCount(8).rarity(Rarity.EPIC));

    public static final Item PERFECT_DODGE = register("perfect_dodge",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.PERFECT_DODGE),
            new Item.Settings().maxCount(8).rarity(Rarity.EPIC));


    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.

        DagMod.LOGGER.info("Registering items for " + DagMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.AMETHYST_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DIAMOND_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.ECHO_DUST));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.EMERALD_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.QUARTZ_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.SLIMEBALL_DUST));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE));

        // Gem Items
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.CITRINE);
                    itemGroup.add(ModItems.RUBY);
                    itemGroup.add(ModItems.SAPPHIRE);
                    itemGroup.add(ModItems.TANZANITE);
                    itemGroup.add(ModItems.TOPAZ);
                    itemGroup.add(ModItems.ZIRCON);
                    itemGroup.add(ModItems.SILMARIL);
                    itemGroup.add(ModItems.PINK_GARNET);
                });

        // Raw Gem Items
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.RAW_CITRINE);
                    itemGroup.add(ModItems.RAW_RUBY);
                    itemGroup.add(ModItems.RAW_SAPPHIRE);
                    itemGroup.add(ModItems.RAW_TANZANITE);
                    itemGroup.add(ModItems.RAW_TOPAZ);
                    itemGroup.add(ModItems.RAW_ZIRCON);
                    itemGroup.add(ModItems.RAW_PINK_GARNET);
                });

        // Mythril Materials
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.RAW_MYTHRIL);
                    itemGroup.add(ModItems.MYTHRIL_INGOT);
                    itemGroup.add(ModItems.MYTHRIL_NUGGET);
                });

        // Gem Crafting Tools
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.GEM_CUTTER_TOOL);
                });

        // Mythril Tools & Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.MYTHRIL_SWORD);
                    itemGroup.add(ModItems.MYTHRIL_AXE);
                    itemGroup.add(ModItems.MYTHRIL_HELMET);
                    itemGroup.add(ModItems.MYTHRIL_CHESTPLATE);
                    itemGroup.add(ModItems.MYTHRIL_LEGGINGS);
                    itemGroup.add(ModItems.MYTHRIL_BOOTS);
                });

        // Dragonscale Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.DRAGONSCALE_HELMET);
                    itemGroup.add(ModItems.DRAGONSCALE_CHESTPLATE);
                    itemGroup.add(ModItems.DRAGONSCALE_LEGGINGS);
                    itemGroup.add(ModItems.DRAGONSCALE_BOOTS);
                });

        // Inferno Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.INFERNO_HELMET);
                    itemGroup.add(ModItems.INFERNO_CHESTPLATE);
                    itemGroup.add(ModItems.INFERNO_LEGGINGS);
                    itemGroup.add(ModItems.INFERNO_BOOTS);
                });

        // Crystalforge Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.CRYSTALFORGE_HELMET);
                    itemGroup.add(ModItems.CRYSTALFORGE_CHESTPLATE);
                    itemGroup.add(ModItems.CRYSTALFORGE_LEGGINGS);
                    itemGroup.add(ModItems.CRYSTALFORGE_BOOTS);
                });

        // Naturesguard Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.NATURESGUARD_HELMET);
                    itemGroup.add(ModItems.NATURESGUARD_CHESTPLATE);
                    itemGroup.add(ModItems.NATURESGUARD_LEGGINGS);
                    itemGroup.add(ModItems.NATURESGUARD_BOOTS);
                });

        // Shadow Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.SHADOW_HELMET);
                    itemGroup.add(ModItems.SHADOW_CHESTPLATE);
                    itemGroup.add(ModItems.SHADOW_LEGGINGS);
                    itemGroup.add(ModItems.SHADOW_BOOTS);
                });

        // Frostbound Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.FROSTBOUND_HELMET);
                    itemGroup.add(ModItems.FROSTBOUND_CHESTPLATE);
                    itemGroup.add(ModItems.FROSTBOUND_LEGGINGS);
                    itemGroup.add(ModItems.FROSTBOUND_BOOTS);
                });

        // Solarweave Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.SOLARWEAVE_HELMET);
                    itemGroup.add(ModItems.SOLARWEAVE_CHESTPLATE);
                    itemGroup.add(ModItems.SOLARWEAVE_LEGGINGS);
                    itemGroup.add(ModItems.SOLARWEAVE_BOOTS);
                });

        // Stormcaller Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.STORMCALLER_HELMET);
                    itemGroup.add(ModItems.STORMCALLER_CHESTPLATE);
                    itemGroup.add(ModItems.STORMCALLER_LEGGINGS);
                    itemGroup.add(ModItems.STORMCALLER_BOOTS);
                });

        // Obsidian Armor - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.OBSIDIAN_HELMET);
                    itemGroup.add(ModItems.OBSIDIAN_CHESTPLATE);
                    itemGroup.add(ModItems.OBSIDIAN_LEGGINGS);
                    itemGroup.add(ModItems.OBSIDIAN_BOOTS);
                });

        // Special Weapons - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.DRAGONSCALE_SWORD);
                    itemGroup.add(ModItems.INFERNO_SWORD);
                    itemGroup.add(ModItems.BLOODTHIRSTER_BLADE);
                    itemGroup.add(ModItems.CRYSTAL_KATANA);
                    itemGroup.add(ModItems.ETHEREAL_BLADE);
                    itemGroup.add(ModItems.FROSTBITE_AXE);
                    itemGroup.add(ModItems.GILDED_RAPIER);
                    itemGroup.add(ModItems.POISON_FANG_SPEAR);
                    itemGroup.add(ModItems.SHADOWFANG_DAGGER);
                    itemGroup.add(ModItems.SHADOWFANG_SWORD);
                    itemGroup.add(ModItems.THUNDER_PIKE);
                    itemGroup.add(ModItems.CRYSTALHAMMER);
                    itemGroup.add(ModItems.SOLAR_BOW);
                    itemGroup.add(ModItems.PHANTOM_BLADE);
                    itemGroup.add(ModItems.TRUE_KING_SWORD);
                });

        // Special Shields - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.INFERNO_SHIELD);
                    itemGroup.add(ModItems.SHADOW_SHIELD);
                    itemGroup.add(ModItems.CRYSTAL_SHIELD);
                    itemGroup.add(ModItems.DRAGONBONE_SHIELD);
                    itemGroup.add(ModItems.FROST_SHIELD);
                    itemGroup.add(ModItems.NATURE_SHIELD);
                    itemGroup.add(ModItems.SOLAR_SHIELD);
                    itemGroup.add(ModItems.STORMGUARD_SHIELD);
                    itemGroup.add(ModItems.CELESTIAL_SHIELD);
                });

        // Mythril Tools - Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.MYTHRIL_PICKAXE);
                    itemGroup.add(ModItems.MYTHRIL_SHOVEL);
                    itemGroup.add(ModItems.MYTHRIL_HOE);
                });

        // Dragon Materials
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DRAGON_SCALE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DRAGON_BONE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DRAGON_SKIN));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DRAGON_HEART));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.KINGS_SCALE));

        // Dragon Eggs (dragon_egg block item added via ModBlocks)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ICE_DRAGON_EGG);
                    itemGroup.add(ModItems.LAVA_DRAGON_EGG);
                    itemGroup.add(ModItems.RED_DRAGON_EGG);
                    itemGroup.add(ModItems.EARTH_DRAGON_EGG);
                    itemGroup.add(ModItems.WIND_DRAGON_EGG);
                });


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.BEEF_STEW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.CANDIED_APPLE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.CHICKEN_STEW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.CRIMSON_SOUP));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DRAGONFRUIT_TART));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.ELVEN_BREAD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.ETHEREAL_COOKIES));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.FROSTBERRY_PIE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.GLOWBERRY_JAM));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.GOLDEN_APPLE_STRUDEL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.HONEY_BREAD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.MOLTEN_CHILI));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.MYSTIC_STEW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.NETHER_SALAD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.PHOENIX_ROAST));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.PUMPKIN_PARFAIT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.SAVORY_BEEF_ROAST));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.SHADOW_CAKE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.SPICED_RABBIT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.STARFRUIT_SMOOTHIE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.STORMFISH_FILLET));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.VOID_TRUFFLES));


        // Quest Books - Add to Tools group since they're functional items
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.NOVICE_QUEST_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.APPRENTICE_QUEST_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.EXPERT_QUEST_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.MASTER_QUEST_TOME));

        // NEW: Class Selection Items - Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.CLASS_SELECTION_TOME));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.WARRIOR_TOKEN));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.MAGE_TOKEN));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.ROGUE_TOKEN));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.CLASS_RESET_CRYSTAL));
        // Reset Items - Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.RACE_RESET_CRYSTAL);
                    itemGroup.add(ModItems.CHARACTER_RESET_CRYSTAL);
                    itemGroup.add(ModItems.POTION_OF_RACIAL_REBIRTH);
                    itemGroup.add(ModItems.POTION_OF_CLASS_REBIRTH);
                    itemGroup.add(ModItems.POTION_OF_TOTAL_REBIRTH);
                });


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.HALL_LOCATOR));

        // Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.APPRENTICE_WAND);
                    itemGroup.add(ModItems.ADEPT_WAND);
                    itemGroup.add(ModItems.MASTER_WAND);
                    // ... other items
                });


        // Spell Scrolls - Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.HEAL_SCROLL);
                    itemGroup.add(ModItems.FIREBALL_SCROLL);
                    itemGroup.add(ModItems.ABSORPTION_SCROLL);
                    itemGroup.add(ModItems.LIGHTNING_SCROLL);
                    itemGroup.add(ModItems.FROST_NOVA_SCROLL);
                    itemGroup.add(ModItems.TELEPORT_SCROLL);
                    itemGroup.add(ModItems.MANA_SHIELD_SCROLL);
                });

        // Warrior Ability Items - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.RAGE_TOTEM);
                    itemGroup.add(ModItems.WAR_HORN);
                });

        // New Warrior Abilities
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.BATTLE_STANDARD);
                    itemGroup.add(ModItems.WHIRLWIND_AXE);
                    itemGroup.add(ModItems.IRON_TALISMAN);
                });

        // === NEW MAGE ABILITIES ===
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ARCANE_ORB);
                    itemGroup.add(ModItems.TEMPORAL_CRYSTAL);
                    itemGroup.add(ModItems.MANA_CATALYST);
                    itemGroup.add(ModItems.BARRIER_CHARM);
                });

        // Rogue Ability Items - Add to Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(ModItems.ROGUE_ABILITY_TOME);
                });

        // Consumable Items - Add to Tools group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> {
                    // Essential Consumables
                    itemGroup.add(ModItems.MANA_CRYSTAL);
                    itemGroup.add(ModItems.ENERGY_TONIC);
                    itemGroup.add(ModItems.COOLDOWN_ELIXIR);
                    itemGroup.add(ModItems.VAMPIRE_DUST);
                    itemGroup.add(ModItems.PHANTOM_DUST);
                    itemGroup.add(ModItems.SPELL_ECHO);
                    itemGroup.add(ModItems.BATTLE_FRENZY);
                    itemGroup.add(ModItems.SHADOW_BLEND);
                    itemGroup.add(ModItems.FORTUNE_DUST);
                    itemGroup.add(ModItems.FEATHERFALL_POWDER);

                    // Advanced Consumables
                    itemGroup.add(ModItems.LAST_STAND_POWDER);
                    itemGroup.add(ModItems.TIME_DISTORTION);
                    itemGroup.add(ModItems.OVERCHARGE_DUST);
                    itemGroup.add(ModItems.TITAN_STRENGTH);
                    itemGroup.add(ModItems.PERFECT_DODGE);
                });
    }

    public static void registerModItems() {
    }
}