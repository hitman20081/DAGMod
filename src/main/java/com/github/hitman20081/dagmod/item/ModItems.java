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

    }

    public static void registerModItems() {
    }
}