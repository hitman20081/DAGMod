package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance", Item::new, new Item.Settings());
    public static final Item AMETHYST_POWDER = register("amethyst_powder", Item::new, new Item.Settings());
    public static final Item DIAMOND_POWDER = register("diamond_powder", Item::new, new Item.Settings());
    public static final Item EMERALD_POWDER = register("emerald_powder", Item::new, new Item.Settings());
    public static final Item QUARTZ_POWDER = register("quartz_powder", Item::new, new Item.Settings());


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
                        itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.AMETHYST_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.DIAMOND_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.EMERALD_POWDER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) ->
                        itemGroup.add(ModItems.QUARTZ_POWDER));
    }

    public static void registerModItems() {
    }
}