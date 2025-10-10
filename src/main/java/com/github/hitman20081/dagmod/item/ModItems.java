package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.item.QuestBookItem;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
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
    public static final Item DIAMOND_POWDER = register("diamond_powder", Item::new, new Item.Settings());
    public static final Item EMERALD_POWDER = register("emerald_powder", Item::new, new Item.Settings());
    public static final Item ECHO_DUST = register("echo_dust", Item::new, new Item.Settings());
    public static final Item QUARTZ_POWDER = register("quartz_powder", Item::new, new Item.Settings());
    public static final Item SLIMEBALL_DUST = register("slimeball_dust", Item::new, new Item.Settings());

    public static final Item CHICKEN_STEW = register("chicken_stew", Item::new, new Item.Settings().food(ModFoodComponents.CHICKEN_STEW));
    public static final Item BEEF_STEW = register("beef_stew", Item::new, new Item.Settings().food(ModFoodComponents.BEEF_STEW));
    public static final Item MYSTIC_STEW = register("mystic_stew", Item::new, new Item.Settings().food(ModFoodComponents.MYSTIC_STEW));
    public static final Item CANDIED_APPLE = register("candied_apple", Item::new, new Item.Settings().food(ModFoodComponents.CANDIED_APPLE));
    public static final Item CRIMSON_SOUP = register("crimson_soup", Item::new, new Item.Settings().food(ModFoodComponents.CRIMSON_SOUP));
    public static final Item DRAGONFRUIT_TART = register("dragonfruit_tart", Item::new, new Item.Settings().food(ModFoodComponents.DRAGONFRUIT_TART));
    public static final Item ELVEN_BREAD = register("elven_bread", Item::new, new Item.Settings().food(ModFoodComponents.ELVEN_BREAD));
    public static final Item ETHEREAL_COOKIES = register("ethereal_cookies", Item::new, new Item.Settings().food(ModFoodComponents.ETHEREAL_COOKIES));
    public static final Item FROSTBERRY_PIE = register("frostberry_pie", Item::new, new Item.Settings().food(ModFoodComponents.FROSTBERRY_PIE));
    public static final Item GLOWBERRY_JAM = register("glowberry_jam", Item::new, new Item.Settings().food(ModFoodComponents.GLOWBERRY_JAM));
    public static final Item GOLDEN_APPLE_STRUDEL = register("golden_apple_strudel", Item::new, new Item.Settings().food(ModFoodComponents.GOLDEN_APPLE_STRUDEL));
    public static final Item HONEY_BREAD = register("honey_bread", Item::new, new Item.Settings().food(ModFoodComponents.HONEY_BREAD));
    public static final Item MOLTEN_CHILI = register("molten_chili", Item::new, new Item.Settings().food(ModFoodComponents.MOLTEN_CHILI));
    public static final Item NETHER_SALAD = register("nether_salad", Item::new, new Item.Settings().food(ModFoodComponents.NETHER_SALAD));
    public static final Item PHOENIX_ROAST = register("phoenix_roast", Item::new, new Item.Settings().food(ModFoodComponents.PHOENIX_ROAST));
    public static final Item PUMPKIN_PARFAIT = register("pumpkin_parfait", Item::new, new Item.Settings().food(ModFoodComponents.PUMPKIN_PARFAIT));
    public static final Item SAVORY_BEEF_ROAST = register("savory_beef_roast", Item::new, new Item.Settings().food(ModFoodComponents.SAVORY_BEEF_ROAST));
    public static final Item SHADOW_CAKE = register("shadow_cake", Item::new, new Item.Settings().food(ModFoodComponents.SHADOW_CAKE));
    public static final Item SPICED_RABBIT = register("spiced_rabbit", Item::new, new Item.Settings().food(ModFoodComponents.SPICED_RABBIT));
    public static final Item STARFRUIT_SMOOTHIE = register("starfruit_smoothie", Item::new, new Item.Settings().food(ModFoodComponents.STARFRUIT_SMOOTHIE));
    public static final Item STORMFISH_FILLET = register("stormfish_fillet", Item::new, new Item.Settings().food(ModFoodComponents.STORMFISH_FILLET));
    public static final Item VOID_TRUFFLES = register("void_truffles", Item::new, new Item.Settings().food(ModFoodComponents.VOID_TRUFFLES));


    // Quest Books with actual functionality
    public static final Item NOVICE_QUEST_BOOK = register("novice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.NOVICE), new Item.Settings().maxCount(1));
    public static final Item APPRENTICE_QUEST_BOOK = register("apprentice_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.APPRENTICE), new Item.Settings().maxCount(1));
    public static final Item EXPERT_QUEST_BOOK = register("expert_quest_book",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.EXPERT), new Item.Settings().maxCount(1));
    public static final Item MASTER_QUEST_TOME = register("master_quest_tome",
            settings -> new QuestBookItem(settings, QuestData.QuestBookTier.MASTER), new Item.Settings().maxCount(1));

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

    // ===== ROGUE ITEMS =====

    /**
     * Rogue Ability Tome - Contains all three Rogue abilities
     * Shift+Right-click to cycle abilities, Right-click to use
     * Abilities: Smoke Bomb, Poison Dagger, Shadow Step
     */
    public static final Item ROGUE_ABILITY_TOME = register("rogue_ability_tome",
            RogueAbilityItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

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

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.HALL_LOCATOR));

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