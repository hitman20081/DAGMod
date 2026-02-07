package com.github.hitman20081.dagmod.tag;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class DagModItemTags {
    public static final TagKey<Item> DAGMOD_SHIELDS = TagKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, "shields"));
}
