package com.github.hitman20081.dagmod.tag;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;

public class DagModItemTags {
    public static final TagKey<Item> DAGMOD_SHIELDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "shields"));
}
