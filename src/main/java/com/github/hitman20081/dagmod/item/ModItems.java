package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.item.QuestBookItem;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ModItems {

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance", Item::new, new Item.Properties());
    public static final Item AMETHYST_POWDER = register("amethyst_powder", Item::new, new Item.Properties());
    public static final Item CITRINE_POWDER = register("citrine_powder", Item::new, new Item.Properties());
    public static final Item DIAMOND_POWDER = register("diamond_powder", Item::new, new Item.Properties());
    public static final Item EMERALD_POWDER = register("emerald_powder", Item::new, new Item.Properties());
    public static final Item ECHO_DUST = register("echo_dust", Item::new, new Item.Properties());
    public static final Item QUARTZ_POWDER = register("quartz_powder", Item::new, new Item.Properties());
    public static final Item SLIMEBALL_DUST = register("slimeball_dust", Item::new, new Item.Properties());

    // Gem Items
    public static final Item CITRINE = register("citrine", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RUBY = register("ruby", Item::new, new Item.Properties().stacksTo(64));
    public static final Item SAPPHIRE = register("sapphire", Item::new, new Item.Properties().stacksTo(64));
    public static final Item TANZANITE = register("tanzanite", Item::new, new Item.Properties().stacksTo(64));
    public static final Item TOPAZ = register("topaz", Item::new, new Item.Properties().stacksTo(64));
    public static final Item ZIRCON = register("zircon", Item::new, new Item.Properties().stacksTo(64));
    public static final Item SILMARIL = register("silmaril", Item::new, new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    public static final Item PINK_GARNET = register("pink_garnet", Item::new, new Item.Properties().stacksTo(64));

    // Raw Gem Items
    public static final Item RAW_CITRINE = register("raw_citrine", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_RUBY = register("raw_ruby", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_SAPPHIRE = register("raw_sapphire", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_TANZANITE = register("raw_tanzanite", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_TOPAZ = register("raw_topaz", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_ZIRCON = register("raw_zircon", Item::new, new Item.Properties().stacksTo(64));
    public static final Item RAW_PINK_GARNET = register("raw_pink_garnet", Item::new, new Item.Properties().stacksTo(64));

    // Gem Crafting Tools
    public static final Item GEM_CUTTER_TOOL = register("gem_cutter_tool", Item::new,
            new Item.Properties().stacksTo(1).durability(32));

    // Mythril Materials
    public static final Item RAW_MYTHRIL = register("raw_mythril", Item::new, new Item.Properties().stacksTo(64).fireResistant());
    public static final Item MYTHRIL_INGOT = register("mythril_ingot", Item::new, new Item.Properties().stacksTo(64).fireResistant());
    public static final Item MYTHRIL_NUGGET = register("mythril_nugget", Item::new, new Item.Properties().stacksTo(64).fireResistant());

    // Mythril Tools
    public static final Item MYTHRIL_SWORD = register("mythril_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 3, -2.4f)),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_PICKAXE = register("mythril_pickaxe",
            settings -> new Item(settings.pickaxe(ModToolMaterials.MYTHRIL, 1, -2.8f)),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_AXE = register("mythril_axe",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 5.0f, -3.0f, settings),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_SHOVEL = register("mythril_shovel",
            settings -> new ShovelItem(ModToolMaterials.MYTHRIL, 1.5f, -3.0f, settings),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_HOE = register("mythril_hoe",
            settings -> new HoeItem(ModToolMaterials.MYTHRIL, 0, -3f, settings),
            new Item.Properties().fireResistant());

    // Mythril Armor
    public static final Item MYTHRIL_HELMET = register("mythril_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.MYTHRIL, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_CHESTPLATE = register("mythril_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.MYTHRIL, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_LEGGINGS = register("mythril_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.MYTHRIL, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item MYTHRIL_BOOTS = register("mythril_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.MYTHRIL, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Dragonscale Armor
    public static final Item DRAGONSCALE_HELMET = register("dragonscale_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.DRAGONSCALE, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item DRAGONSCALE_CHESTPLATE = register("dragonscale_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.DRAGONSCALE, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item DRAGONSCALE_LEGGINGS = register("dragonscale_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.DRAGONSCALE, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item DRAGONSCALE_BOOTS = register("dragonscale_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.DRAGONSCALE, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Inferno Armor
    public static final Item INFERNO_HELMET = register("inferno_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.INFERNO, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item INFERNO_CHESTPLATE = register("inferno_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.INFERNO, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item INFERNO_LEGGINGS = register("inferno_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.INFERNO, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item INFERNO_BOOTS = register("inferno_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.INFERNO, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Crystalforge Armor
    public static final Item CRYSTALFORGE_HELMET = register("crystalforge_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.CRYSTALFORGE, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTALFORGE_CHESTPLATE = register("crystalforge_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.CRYSTALFORGE, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTALFORGE_LEGGINGS = register("crystalforge_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.CRYSTALFORGE, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTALFORGE_BOOTS = register("crystalforge_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.CRYSTALFORGE, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Naturesguard Armor
    public static final Item NATURESGUARD_HELMET = register("naturesguard_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.NATURESGUARD, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item NATURESGUARD_CHESTPLATE = register("naturesguard_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.NATURESGUARD, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item NATURESGUARD_LEGGINGS = register("naturesguard_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.NATURESGUARD, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item NATURESGUARD_BOOTS = register("naturesguard_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.NATURESGUARD, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Shadow Armor
    public static final Item SHADOW_HELMET = register("shadow_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SHADOW, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item SHADOW_CHESTPLATE = register("shadow_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SHADOW, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item SHADOW_LEGGINGS = register("shadow_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SHADOW, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item SHADOW_BOOTS = register("shadow_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SHADOW, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Frostbound Armor
    public static final Item FROSTBOUND_HELMET = register("frostbound_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.FROSTBOUND, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item FROSTBOUND_CHESTPLATE = register("frostbound_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.FROSTBOUND, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item FROSTBOUND_LEGGINGS = register("frostbound_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.FROSTBOUND, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item FROSTBOUND_BOOTS = register("frostbound_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.FROSTBOUND, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Solarweave Armor
    public static final Item SOLARWEAVE_HELMET = register("solarweave_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SOLARWEAVE, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item SOLARWEAVE_CHESTPLATE = register("solarweave_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SOLARWEAVE, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item SOLARWEAVE_LEGGINGS = register("solarweave_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SOLARWEAVE, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item SOLARWEAVE_BOOTS = register("solarweave_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.SOLARWEAVE, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Stormcaller Armor
    public static final Item STORMCALLER_HELMET = register("stormcaller_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.STORMCALLER, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item STORMCALLER_CHESTPLATE = register("stormcaller_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.STORMCALLER, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item STORMCALLER_LEGGINGS = register("stormcaller_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.STORMCALLER, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item STORMCALLER_BOOTS = register("stormcaller_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.STORMCALLER, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Obsidian Armor
    public static final Item OBSIDIAN_HELMET = register("obsidian_helmet",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.OBSIDIAN, ArmorType.HELMET)),
            new Item.Properties().fireResistant());

    public static final Item OBSIDIAN_CHESTPLATE = register("obsidian_chestplate",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.OBSIDIAN, ArmorType.CHESTPLATE)),
            new Item.Properties().fireResistant());

    public static final Item OBSIDIAN_LEGGINGS = register("obsidian_leggings",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.OBSIDIAN, ArmorType.LEGGINGS)),
            new Item.Properties().fireResistant());

    public static final Item OBSIDIAN_BOOTS = register("obsidian_boots",
            settings -> new Item(settings.humanoidArmor(ModArmorMaterials.OBSIDIAN, ArmorType.BOOTS)),
            new Item.Properties().fireResistant());

    // Bone Realm
    public static final Item KINGS_RECALL_STONE = register("kings_recall_stone",
            KingsRecallStoneItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // Dragon Materials
    public static final Item DRAGON_SCALE = register("dragon_scale", Item::new, new Item.Properties().stacksTo(64).rarity(Rarity.RARE).fireResistant());
    public static final Item DRAGON_BONE = register("dragon_bone", Item::new, new Item.Properties().stacksTo(64).rarity(Rarity.RARE).fireResistant());
    public static final Item DRAGON_SKIN = register("dragon_skin", Item::new, new Item.Properties().stacksTo(64).rarity(Rarity.RARE).fireResistant());
    public static final Item DRAGON_HEART = register("dragon_heart", Item::new, new Item.Properties().stacksTo(16).rarity(Rarity.EPIC).fireResistant());
    public static final Item KINGS_SCALE = register("kings_scale", Item::new, new Item.Properties().stacksTo(16).rarity(Rarity.EPIC).fireResistant());

    // Dragon Eggs (dragon_egg is the block item - see ModBlocks.DRAGON_EGG_BLOCK)
    public static final Item ICE_DRAGON_EGG = register("ice_dragon_egg", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    public static final Item LAVA_DRAGON_EGG = register("lava_dragon_egg", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    public static final Item RED_DRAGON_EGG = register("red_dragon_egg", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    public static final Item EARTH_DRAGON_EGG = register("earth_dragon_egg", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    public static final Item WIND_DRAGON_EGG = register("wind_dragon_egg", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());

    // Special Weapons
    public static final Item DRAGONSCALE_SWORD = register("dragonscale_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f)),
            new Item.Properties().fireResistant());

    public static final Item INFERNO_SWORD = register("inferno_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f)),
            new Item.Properties().fireResistant());

    public static final Item BLOODTHIRSTER_BLADE = register("bloodthirster_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 10, -2.4f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTAL_KATANA = register("crystal_katana",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.0f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item ETHEREAL_BLADE = register("ethereal_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 11, -2.2f).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item FROSTBITE_AXE = register("frostbite_axe",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 11.0f, -3.0f, settings.rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item GILDED_RAPIER = register("gilded_rapier",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 8, -1.8f).rarity(Rarity.UNCOMMON)),
            new Item.Properties().fireResistant());

    public static final Item POISON_FANG_SPEAR = register("poison_fang_spear",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 8, -2.6f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item SHADOWFANG_DAGGER = register("shadowfang_dagger",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 7, -1.6f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item SHADOWFANG_SWORD = register("shadowfang_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -2.4f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item THUNDER_PIKE = register("thunder_pike",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 11, -2.8f).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTALHAMMER = register("crystalhammer",
            settings -> new AxeItem(ModToolMaterials.MYTHRIL, 12.0f, -3.2f, settings.rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item SOLAR_BOW = register("solar_bow",
            BowItem::new,
            new Item.Properties().durability(500).rarity(Rarity.RARE).fireResistant());

    public static final Item PHANTOM_BLADE = register("phantom_blade",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 9, -1.8f).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item TRUE_KING_SWORD = register("true_king_sword",
            settings -> new Item(settings.sword(ModToolMaterials.MYTHRIL, 12, -2.4f).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    // Special Shields - Using DagModShieldItem for custom shield rendering
    public static final Item INFERNO_SHIELD = register("inferno_shield",
            settings -> new DagModShieldItem("inferno", settings.durability(500).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item SHADOW_SHIELD = register("shadow_shield",
            settings -> new DagModShieldItem("shadow", settings.durability(450).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item CRYSTAL_SHIELD = register("crystal_shield",
            settings -> new DagModShieldItem("crystal", settings.durability(400).rarity(Rarity.UNCOMMON)),
            new Item.Properties().fireResistant());

    public static final Item DRAGONBONE_SHIELD = register("dragonbone_shield",
            settings -> new DagModShieldItem("dragonbone", settings.durability(600).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item FROST_SHIELD = register("frost_shield",
            settings -> new DagModShieldItem("frost", settings.durability(450).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item NATURE_SHIELD = register("nature_shield",
            settings -> new DagModShieldItem("nature", settings.durability(400).rarity(Rarity.UNCOMMON)),
            new Item.Properties().fireResistant());

    public static final Item SOLAR_SHIELD = register("solar_shield",
            settings -> new DagModShieldItem("solar", settings.durability(500).rarity(Rarity.RARE)),
            new Item.Properties().fireResistant());

    public static final Item STORMGUARD_SHIELD = register("stormguard_shield",
            settings -> new DagModShieldItem("stormguard", settings.durability(550).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item CELESTIAL_SHIELD = register("celestial_shield",
            settings -> new DagModShieldItem("celestial", settings.durability(700).rarity(Rarity.EPIC)),
            new Item.Properties().fireResistant());

    public static final Item CHICKEN_STEW = register("chicken_stew", Item::new, new Item.Properties().food(ModFoodComponents.CHICKEN_STEW));
    public static final Item BEEF_STEW = register("beef_stew", Item::new, new Item.Properties().food(ModFoodComponents.BEEF_STEW_FOOD, ModFoodComponents.BEEF_STEW_CONSUMABLE));
    public static final Item MYSTIC_STEW = register("mystic_stew", Item::new, new Item.Properties().food(ModFoodComponents.MYSTIC_STEW_FOOD, ModFoodComponents.MYSTIC_STEW_CONSUMABLE));
    public static final Item CANDIED_APPLE = register("candied_apple", Item::new, new Item.Properties().food(ModFoodComponents.CANDIED_APPLE_FOOD, ModFoodComponents.CANDIED_APPLE_CONSUMABLE));
    public static final Item CRIMSON_SOUP = register("crimson_soup", Item::new, new Item.Properties().food(ModFoodComponents.CRIMSON_SOUP_FOOD, ModFoodComponents.CRIMSON_SOUP_CONSUMABLE));
    public static final Item DRAGONFRUIT_TART = register("dragonfruit_tart", Item::new, new Item.Properties().food(ModFoodComponents.DRAGONFRUIT_TART_FOOD, ModFoodComponents.DRAGONFRUIT_TART_CONSUMABLE));
    public static final Item ELVEN_BREAD = register("elven_bread", Item::new, new Item.Properties().food(ModFoodComponents.ELVEN_BREAD_FOOD, ModFoodComponents.ELVEN_BREAD_CONSUMABLE));
    public static final Item ETHEREAL_COOKIES = register("ethereal_cookies", Item::new, new Item.Properties().food(ModFoodComponents.ETHEREAL_COOKIES_FOOD, ModFoodComponents.ETHEREAL_COOKIES_CONSUMABLE));
    public static final Item FROSTBERRY_PIE = register("frostberry_pie", Item::new, new Item.Properties().food(ModFoodComponents.FROSTBERRY_PIE_FOOD, ModFoodComponents.FROSTBERRY_PIE_CONSUMABLE));
    public static final Item GLOWBERRY_JAM = register("glowberry_jam", Item::new, new Item.Properties().food(ModFoodComponents.GLOWBERRY_JAM_FOOD, ModFoodComponents.GLOWBERRY_JAM_CONSUMABLE));
    public static final Item GOLDEN_APPLE_STRUDEL = register("golden_apple_strudel", Item::new, new Item.Properties().food(ModFoodComponents.GOLDEN_APPLE_STRUDEL_FOOD, ModFoodComponents.GOLDEN_APPLE_STRUDEL_CONSUMABLE));
    public static final Item HONEY_BREAD = register("honey_bread", Item::new, new Item.Properties().food(ModFoodComponents.HONEY_BREAD_FOOD, ModFoodComponents.HONEY_BREAD_CONSUMABLE));
    public static final Item MOLTEN_CHILI = register("molten_chili", Item::new, new Item.Properties().food(ModFoodComponents.MOLTEN_CHILI_FOOD, ModFoodComponents.MOLTEN_CHILI_CONSUMABLE));
    public static final Item NETHER_SALAD = register("nether_salad", Item::new, new Item.Properties().food(ModFoodComponents.NETHER_SALAD_FOOD, ModFoodComponents.NETHER_SALAD_CONSUMABLE));
    public static final Item PHOENIX_ROAST = register("phoenix_roast", Item::new, new Item.Properties().food(ModFoodComponents.PHOENIX_ROAST_FOOD, ModFoodComponents.PHOENIX_ROAST_CONSUMABLE));
    public static final Item PUMPKIN_PARFAIT = register("pumpkin_parfait", Item::new, new Item.Properties().food(ModFoodComponents.PUMPKIN_PARFAIT_FOOD, ModFoodComponents.PUMPKIN_PARFAIT_CONSUMABLE));
    public static final Item SAVORY_BEEF_ROAST = register("savory_beef_roast", Item::new, new Item.Properties().food(ModFoodComponents.SAVORY_BEEF_ROAST_FOOD, ModFoodComponents.SAVORY_BEEF_ROAST_CONSUMABLE));
    public static final Item SHADOW_CAKE = register("shadow_cake", Item::new, new Item.Properties().food(ModFoodComponents.SHADOW_CAKE_FOOD, ModFoodComponents.SHADOW_CAKE_CONSUMABLE));
    public static final Item SPICED_RABBIT = register("spiced_rabbit", Item::new, new Item.Properties().food(ModFoodComponents.SPICED_RABBIT_FOOD, ModFoodComponents.SPICED_RABBIT_CONSUMABLE));
    public static final Item STARFRUIT_SMOOTHIE = register("starfruit_smoothie", Item::new, new Item.Properties().food(ModFoodComponents.STARFRUIT_SMOOTHIE_FOOD, ModFoodComponents.STARFRUIT_SMOOTHIE_CONSUMABLE));
    public static final Item STORMFISH_FILLET = register("stormfish_fillet", Item::new, new Item.Properties().food(ModFoodComponents.STORMFISH_FILLET_FOOD, ModFoodComponents.STORMFISH_FILLET_CONSUMABLE));
    public static final Item VOID_TRUFFLES = register("void_truffles", Item::new, new Item.Properties().food(ModFoodComponents.VOID_TRUFFLES_FOOD, ModFoodComponents.VOID_TRUFFLES_CONSUMABLE));


    // Quest Books with actual functionality
    public static final Item NOVICE_QUEST_BOOK = register("novice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.NOVICE), new Item.Properties().stacksTo(1));
    public static final Item APPRENTICE_QUEST_BOOK = register("apprentice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.APPRENTICE), new Item.Properties().stacksTo(1));
    public static final Item EXPERT_QUEST_BOOK = register("expert_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.EXPERT), new Item.Properties().stacksTo(1));
    public static final Item MASTER_QUEST_TOME = register("master_quest_tome",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.MASTER), new Item.Properties().stacksTo(1));

    // Tutorial Quest Notes - Given by Innkeeper Garrick
    public static final Item GARRICKS_FIRST_NOTE = register("garricks_first_note",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item GARRICKS_SECOND_NOTE = register("garricks_second_note",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item GARRICKS_THIRD_NOTE = register("garricks_third_note",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    // NEW: Class Selection System Items
    public static final Item CLASS_SELECTION_TOME = register("class_selection_tome",
            ClassSelectionTomeItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final Item WARRIOR_TOKEN = register("warrior_token",
            settings -> new ClassTokenItem(settings, "Warrior", ChatFormatting.RED, "⚔"),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item MAGE_TOKEN = register("mage_token",
            settings -> new ClassTokenItem(settings, "Mage", ChatFormatting.AQUA, "✦"),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item ROGUE_TOKEN = register("rogue_token",
            settings -> new ClassTokenItem(settings, "Rogue", ChatFormatting.DARK_GREEN, "⚡"),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item CLASS_RESET_CRYSTAL = register("class_reset_crystal",
            ClassResetCrystalItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // Race & Character Reset Items
    public static final Item RACE_RESET_CRYSTAL = register("race_reset_crystal",
            Item::new, new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final Item CHARACTER_RESET_CRYSTAL = register("character_reset_crystal",
            Item::new, new Item.Properties().stacksTo(16).rarity(Rarity.EPIC));

    public static final Item POTION_OF_RACIAL_REBIRTH = register("potion_of_racial_rebirth",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item POTION_OF_CLASS_REBIRTH = register("potion_of_class_rebirth",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item POTION_OF_TOTAL_REBIRTH = register("potion_of_total_rebirth",
            Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // Race Tokens
    public static final Item HUMAN_TOKEN = register("human_token",
            Item::new, new Item.Properties());

    public static final Item DWARF_TOKEN = register("dwarf_token",
            Item::new, new Item.Properties());

    public static final Item ELF_TOKEN = register("elf_token",
            Item::new, new Item.Properties());

    public static final Item ORC_TOKEN = register("orc_token",
            Item::new, new Item.Properties());

    // Race Selection Tome
    public static final Item RACE_SELECTION_TOME = register("race_selection_tome",
            RaceSelectionTome::new, new Item.Properties().stacksTo(1));

    // Hall Locator
    public static final Item HALL_LOCATOR = register("hall_locator",
            HallLocatorItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    // Bone Dungeon Locator
    public static final Item BONE_DUNGEON_LOCATOR = register("bone_dungeon_locator",
            BoneDungeonLocatorItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    // Mage Spell Scrolls
    public static final Item HEAL_SCROLL = register("heal_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.HEAL),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item FIREBALL_SCROLL = register("fireball_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.FIREBALL),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item ABSORPTION_SCROLL = register("absorption_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.ABSORPTION),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item LIGHTNING_SCROLL = register("lightning_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.LIGHTNING),
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final Item FROST_NOVA_SCROLL = register("frost_nova_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.FROST_NOVA),
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final Item TELEPORT_SCROLL = register("teleport_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.TELEPORT),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item MANA_SHIELD_SCROLL = register("mana_shield_scroll",
            settings -> new SpellScrollItem(settings, SpellScrollItem.SpellType.MANA_SHIELD),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    // Apprentice Wand - Early game Mage weapon
    public static final Item APPRENTICE_WAND = register("apprentice_wand",
            settings -> new ApprenticeWandItem(settings),
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    // Adept Wand - Mid-game Mage weapon
    public static final Item ADEPT_WAND = register("adept_wand",
            settings -> new AdeptWandItem(settings),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    // Master Wand - Late game Mage weapon
    public static final Item MASTER_WAND = register("master_wand",
            settings -> new MasterWandItem(settings),
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // === WARRIOR ABILITY ITEMS ===

    /**
     * Rage Totem - Activates Rage ability
     * Grants +50% attack damage, +30% speed, +2 hearts absorption for 10 seconds
     * Cooldown: 60 seconds
     */
    public static final Item RAGE_TOTEM = register("rage_totem",
            RageTotemItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    /**
     * War Horn - Activates War Cry ability
     * Buffs allies and debuffs enemies in 15 block radius
     * Cooldown: 90 seconds
     */
    public static final Item WAR_HORN = register("war_horn",
            WarHornItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    // Note: Shield Bash uses vanilla shields, no new item needed
    /**
     * Battle Standard - Activates Battle Shout ability
     * Heals 6 hearts, buffs damage, removes debuffs
     * Cooldown: 45 seconds
     */
    public static final Item BATTLE_STANDARD = register("battle_standard",
            BattleStandardItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    /**
     * Whirlwind Axe - Activates Whirlwind ability
     * AoE spin attack hitting all nearby enemies
     * Cooldown: 30 seconds
     */
    public static final Item WHIRLWIND_AXE = register("whirlwind_axe",
            WhirlwindAxeItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    /**
     * Iron Talisman - Activates Iron Skin ability
     * Massive damage reduction + absorption hearts
     * Cooldown: 120 seconds
     */
    public static final Item IRON_TALISMAN = register("iron_talisman",
            IronTalismanItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // ===== NEW MAGE ABILITY ITEMS =====

    public static final Item ARCANE_ORB = register("arcane_orb",
            ArcaneOrbItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item TEMPORAL_CRYSTAL = register("temporal_crystal",
            TemporalCrystalItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item MANA_CATALYST = register("mana_catalyst",
            ManaCatalystItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item BARRIER_CHARM = register("barrier_charm",
            BarrierCharmItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // ===== ROGUE ITEMS =====

    /**
     * Rogue Ability Tome - Contains all three Rogue abilities
     * Shift+Right-click to cycle abilities, Right-click to use
     * Abilities: Smoke Bomb, Poison Dagger, Shadow Step
     */
    public static final Item ROGUE_ABILITY_TOME = register("rogue_ability_tome",
            RogueAbilityItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    // ===== COOLDOWN-BASED ROGUE ABILITIES =====

    public static final Item VOID_BLADE = register("void_blade",
            VoidBladeItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item VANISH_CLOAK = register("vanish_cloak",
            VanishCloakItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item POISON_VIAL = register("poison_vial",
            PoisonVialItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final Item ASSASSINS_MARK = register("assassins_mark",
            AssassinsMarkItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // ===== CONSUMABLE ITEMS (Made from Powders) =====

    // Essential Consumables
    public static final Item MANA_CRYSTAL = register("mana_crystal",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.MANA_CRYSTAL),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item ENERGY_TONIC = register("energy_tonic",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.ENERGY_TONIC),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item COOLDOWN_ELIXIR = register("cooldown_elixir",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.COOLDOWN_ELIXIR),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item VAMPIRE_DUST = register("vampire_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.VAMPIRE_DUST),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item PHANTOM_DUST = register("phantom_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.PHANTOM_DUST),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item SPELL_ECHO = register("spell_echo",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.SPELL_ECHO),
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final Item BATTLE_FRENZY = register("battle_frenzy",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.BATTLE_FRENZY),
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

    public static final Item SHADOW_BLEND = register("shadow_blend",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.SHADOW_BLEND),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item FORTUNE_DUST = register("fortune_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.FORTUNE_DUST),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final Item FEATHERFALL_POWDER = register("featherfall_powder",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.FEATHERFALL_POWDER),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    // Advanced Consumables
    public static final Item LAST_STAND_POWDER = register("last_stand_powder",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.LAST_STAND_POWDER),
            new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    public static final Item TIME_DISTORTION = register("time_distortion",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.TIME_DISTORTION),
            new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    public static final Item OVERCHARGE_DUST = register("overcharge_dust",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.OVERCHARGE_DUST),
            new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    public static final Item TITAN_STRENGTH = register("titan_strength",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.TITAN_STRENGTH),
            new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    public static final Item PERFECT_DODGE = register("perfect_dodge",
            settings -> new ConsumableItem(settings, ConsumableItem.ConsumableType.PERFECT_DODGE),
            new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));


    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.

        DagMod.LOGGER.info("Registering items for " + DagMod.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.AMETHYST_POWDER));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DIAMOND_POWDER));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.ECHO_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.EMERALD_POWDER));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.QUARTZ_POWDER));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.SLIMEBALL_DUST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.SUSPICIOUS_SUBSTANCE));

        // Gem Items
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.CITRINE);
                    itemGroup.accept(ModItems.RUBY);
                    itemGroup.accept(ModItems.SAPPHIRE);
                    itemGroup.accept(ModItems.TANZANITE);
                    itemGroup.accept(ModItems.TOPAZ);
                    itemGroup.accept(ModItems.ZIRCON);
                    itemGroup.accept(ModItems.SILMARIL);
                    itemGroup.accept(ModItems.PINK_GARNET);
                });

        // Raw Gem Items
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.RAW_CITRINE);
                    itemGroup.accept(ModItems.RAW_RUBY);
                    itemGroup.accept(ModItems.RAW_SAPPHIRE);
                    itemGroup.accept(ModItems.RAW_TANZANITE);
                    itemGroup.accept(ModItems.RAW_TOPAZ);
                    itemGroup.accept(ModItems.RAW_ZIRCON);
                    itemGroup.accept(ModItems.RAW_PINK_GARNET);
                });

        // Mythril Materials
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.RAW_MYTHRIL);
                    itemGroup.accept(ModItems.MYTHRIL_INGOT);
                    itemGroup.accept(ModItems.MYTHRIL_NUGGET);
                });

        // Gem Crafting Tools
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.GEM_CUTTER_TOOL);
                });

        // Mythril Tools & Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.MYTHRIL_SWORD);
                    itemGroup.accept(ModItems.MYTHRIL_AXE);
                    itemGroup.accept(ModItems.MYTHRIL_HELMET);
                    itemGroup.accept(ModItems.MYTHRIL_CHESTPLATE);
                    itemGroup.accept(ModItems.MYTHRIL_LEGGINGS);
                    itemGroup.accept(ModItems.MYTHRIL_BOOTS);
                });

        // Dragonscale Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.DRAGONSCALE_HELMET);
                    itemGroup.accept(ModItems.DRAGONSCALE_CHESTPLATE);
                    itemGroup.accept(ModItems.DRAGONSCALE_LEGGINGS);
                    itemGroup.accept(ModItems.DRAGONSCALE_BOOTS);
                });

        // Inferno Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.INFERNO_HELMET);
                    itemGroup.accept(ModItems.INFERNO_CHESTPLATE);
                    itemGroup.accept(ModItems.INFERNO_LEGGINGS);
                    itemGroup.accept(ModItems.INFERNO_BOOTS);
                });

        // Crystalforge Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.CRYSTALFORGE_HELMET);
                    itemGroup.accept(ModItems.CRYSTALFORGE_CHESTPLATE);
                    itemGroup.accept(ModItems.CRYSTALFORGE_LEGGINGS);
                    itemGroup.accept(ModItems.CRYSTALFORGE_BOOTS);
                });

        // Naturesguard Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.NATURESGUARD_HELMET);
                    itemGroup.accept(ModItems.NATURESGUARD_CHESTPLATE);
                    itemGroup.accept(ModItems.NATURESGUARD_LEGGINGS);
                    itemGroup.accept(ModItems.NATURESGUARD_BOOTS);
                });

        // Shadow Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.SHADOW_HELMET);
                    itemGroup.accept(ModItems.SHADOW_CHESTPLATE);
                    itemGroup.accept(ModItems.SHADOW_LEGGINGS);
                    itemGroup.accept(ModItems.SHADOW_BOOTS);
                });

        // Frostbound Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.FROSTBOUND_HELMET);
                    itemGroup.accept(ModItems.FROSTBOUND_CHESTPLATE);
                    itemGroup.accept(ModItems.FROSTBOUND_LEGGINGS);
                    itemGroup.accept(ModItems.FROSTBOUND_BOOTS);
                });

        // Solarweave Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.SOLARWEAVE_HELMET);
                    itemGroup.accept(ModItems.SOLARWEAVE_CHESTPLATE);
                    itemGroup.accept(ModItems.SOLARWEAVE_LEGGINGS);
                    itemGroup.accept(ModItems.SOLARWEAVE_BOOTS);
                });

        // Stormcaller Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.STORMCALLER_HELMET);
                    itemGroup.accept(ModItems.STORMCALLER_CHESTPLATE);
                    itemGroup.accept(ModItems.STORMCALLER_LEGGINGS);
                    itemGroup.accept(ModItems.STORMCALLER_BOOTS);
                });

        // Obsidian Armor - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.OBSIDIAN_HELMET);
                    itemGroup.accept(ModItems.OBSIDIAN_CHESTPLATE);
                    itemGroup.accept(ModItems.OBSIDIAN_LEGGINGS);
                    itemGroup.accept(ModItems.OBSIDIAN_BOOTS);
                });

        // Special Weapons - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.DRAGONSCALE_SWORD);
                    itemGroup.accept(ModItems.INFERNO_SWORD);
                    itemGroup.accept(ModItems.BLOODTHIRSTER_BLADE);
                    itemGroup.accept(ModItems.CRYSTAL_KATANA);
                    itemGroup.accept(ModItems.ETHEREAL_BLADE);
                    itemGroup.accept(ModItems.FROSTBITE_AXE);
                    itemGroup.accept(ModItems.GILDED_RAPIER);
                    itemGroup.accept(ModItems.POISON_FANG_SPEAR);
                    itemGroup.accept(ModItems.SHADOWFANG_DAGGER);
                    itemGroup.accept(ModItems.SHADOWFANG_SWORD);
                    itemGroup.accept(ModItems.THUNDER_PIKE);
                    itemGroup.accept(ModItems.CRYSTALHAMMER);
                    itemGroup.accept(ModItems.SOLAR_BOW);
                    itemGroup.accept(ModItems.PHANTOM_BLADE);
                    itemGroup.accept(ModItems.TRUE_KING_SWORD);
                });

        // Special Shields - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.INFERNO_SHIELD);
                    itemGroup.accept(ModItems.SHADOW_SHIELD);
                    itemGroup.accept(ModItems.CRYSTAL_SHIELD);
                    itemGroup.accept(ModItems.DRAGONBONE_SHIELD);
                    itemGroup.accept(ModItems.FROST_SHIELD);
                    itemGroup.accept(ModItems.NATURE_SHIELD);
                    itemGroup.accept(ModItems.SOLAR_SHIELD);
                    itemGroup.accept(ModItems.STORMGUARD_SHIELD);
                    itemGroup.accept(ModItems.CELESTIAL_SHIELD);
                });

        // Mythril Tools - Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.MYTHRIL_PICKAXE);
                    itemGroup.accept(ModItems.MYTHRIL_SHOVEL);
                    itemGroup.accept(ModItems.MYTHRIL_HOE);
                });

        // Dragon Materials
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DRAGON_SCALE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DRAGON_BONE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DRAGON_SKIN));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DRAGON_HEART));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.KINGS_SCALE));

        // Dragon Eggs (dragon_egg block item added via ModBlocks)
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.ICE_DRAGON_EGG);
                    itemGroup.accept(ModItems.LAVA_DRAGON_EGG);
                    itemGroup.accept(ModItems.RED_DRAGON_EGG);
                    itemGroup.accept(ModItems.EARTH_DRAGON_EGG);
                    itemGroup.accept(ModItems.WIND_DRAGON_EGG);
                });


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.BEEF_STEW));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.CANDIED_APPLE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.CHICKEN_STEW));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.CRIMSON_SOUP));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.DRAGONFRUIT_TART));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.ELVEN_BREAD));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.ETHEREAL_COOKIES));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.FROSTBERRY_PIE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.GLOWBERRY_JAM));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.GOLDEN_APPLE_STRUDEL));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.HONEY_BREAD));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.MOLTEN_CHILI));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.MYSTIC_STEW));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.NETHER_SALAD));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.PHOENIX_ROAST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.PUMPKIN_PARFAIT));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.SAVORY_BEEF_ROAST));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.SHADOW_CAKE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.SPICED_RABBIT));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.STARFRUIT_SMOOTHIE));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.STORMFISH_FILLET));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.VOID_TRUFFLES));


        // Quest Books - Add to Tools group since they're functional items
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.NOVICE_QUEST_BOOK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.APPRENTICE_QUEST_BOOK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.EXPERT_QUEST_BOOK));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.MASTER_QUEST_TOME));

        // NEW: Class Selection Items - Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.CLASS_SELECTION_TOME));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.WARRIOR_TOKEN));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.MAGE_TOKEN));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.ROGUE_TOKEN));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) ->
                        itemGroup.accept(ModItems.CLASS_RESET_CRYSTAL));
        // Reset Items - Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.RACE_RESET_CRYSTAL);
                    itemGroup.accept(ModItems.CHARACTER_RESET_CRYSTAL);
                    itemGroup.accept(ModItems.POTION_OF_RACIAL_REBIRTH);
                    itemGroup.accept(ModItems.POTION_OF_CLASS_REBIRTH);
                    itemGroup.accept(ModItems.POTION_OF_TOTAL_REBIRTH);
                });


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                        itemGroup.accept(ModItems.HALL_LOCATOR);
                        itemGroup.accept(ModItems.BONE_DUNGEON_LOCATOR);
                });

        // Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.APPRENTICE_WAND);
                    itemGroup.accept(ModItems.ADEPT_WAND);
                    itemGroup.accept(ModItems.MASTER_WAND);
                    // ... other items
                });


        // Spell Scrolls - Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.HEAL_SCROLL);
                    itemGroup.accept(ModItems.FIREBALL_SCROLL);
                    itemGroup.accept(ModItems.ABSORPTION_SCROLL);
                    itemGroup.accept(ModItems.LIGHTNING_SCROLL);
                    itemGroup.accept(ModItems.FROST_NOVA_SCROLL);
                    itemGroup.accept(ModItems.TELEPORT_SCROLL);
                    itemGroup.accept(ModItems.MANA_SHIELD_SCROLL);
                });

        // Warrior Ability Items - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.RAGE_TOTEM);
                    itemGroup.accept(ModItems.WAR_HORN);
                });

        // New Warrior Abilities
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.BATTLE_STANDARD);
                    itemGroup.accept(ModItems.WHIRLWIND_AXE);
                    itemGroup.accept(ModItems.IRON_TALISMAN);
                });

        // === NEW MAGE ABILITIES ===
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.ARCANE_ORB);
                    itemGroup.accept(ModItems.TEMPORAL_CRYSTAL);
                    itemGroup.accept(ModItems.MANA_CATALYST);
                    itemGroup.accept(ModItems.BARRIER_CHARM);
                });

        // Rogue Ability Items - Add to Combat group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.accept(ModItems.ROGUE_ABILITY_TOME);
                });

        // Consumable Items - Add to Tools group
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((itemGroup) -> {
                    // Essential Consumables
                    itemGroup.accept(ModItems.MANA_CRYSTAL);
                    itemGroup.accept(ModItems.ENERGY_TONIC);
                    itemGroup.accept(ModItems.COOLDOWN_ELIXIR);
                    itemGroup.accept(ModItems.VAMPIRE_DUST);
                    itemGroup.accept(ModItems.PHANTOM_DUST);
                    itemGroup.accept(ModItems.SPELL_ECHO);
                    itemGroup.accept(ModItems.BATTLE_FRENZY);
                    itemGroup.accept(ModItems.SHADOW_BLEND);
                    itemGroup.accept(ModItems.FORTUNE_DUST);
                    itemGroup.accept(ModItems.FEATHERFALL_POWDER);

                    // Advanced Consumables
                    itemGroup.accept(ModItems.LAST_STAND_POWDER);
                    itemGroup.accept(ModItems.TIME_DISTORTION);
                    itemGroup.accept(ModItems.OVERCHARGE_DUST);
                    itemGroup.accept(ModItems.TITAN_STRENGTH);
                    itemGroup.accept(ModItems.PERFECT_DODGE);
                });
    }

    public static void registerModItems() {
    }
}