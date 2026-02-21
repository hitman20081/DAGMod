package com.github.hitman20081.dagmod.util;

import com.github.hitman20081.dagmod.DagMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_MYTHRIL_TOOL =
                TagKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, "incorrect_for_mythril_tool"));

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(DagMod.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> MYTHRIL_REPAIR = createTag("mythril_repair");
        public static final TagKey<Item> DRAGONSCALE_REPAIR = createTag("dragonscale_repair");
        public static final TagKey<Item> INFERNO_REPAIR = createTag("inferno_repair");
        public static final TagKey<Item> CRYSTALFORGE_REPAIR = createTag("crystalforge_repair");
        public static final TagKey<Item> NATURESGUARD_REPAIR = createTag("naturesguard_repair");
        public static final TagKey<Item> SHADOW_REPAIR = createTag("shadow_repair");
        public static final TagKey<Item> FROSTBOUND_REPAIR = createTag("frostbound_repair");
        public static final TagKey<Item> SOLARWEAVE_REPAIR = createTag("solarweave_repair");
        public static final TagKey<Item> STORMCALLER_REPAIR = createTag("stormcaller_repair");
        public static final TagKey<Item> OBSIDIAN_REPAIR = createTag("obsidian_repair");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(DagMod.MOD_ID, name));
        }
    }
}
