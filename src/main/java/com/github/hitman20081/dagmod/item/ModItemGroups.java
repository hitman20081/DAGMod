package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ModItemGroups {

    // Create the custom creative tab
    public static final ResourceKey<CreativeModeTab> DAGMOD_GROUP = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dagmod")
    );

    public static void registerItemGroups() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DAGMOD_GROUP,
                FabricCreativeModeTab.builder()
                        .icon(() -> new ItemStack(ModItems.WARRIOR_TOKEN)) // Use warrior token as icon
                        .title(Component.translatable("itemGroup.dagmod"))
                        .displayItems((displayContext, entries) -> {

                            // === CLASS SELECTION TOKENS ===
                            entries.accept(ModItems.WARRIOR_TOKEN);
                            entries.accept(ModItems.MAGE_TOKEN);
                            entries.accept(ModItems.ROGUE_TOKEN);

                            // === RACE SELECTION TOKENS ===
                            entries.accept(ModItems.DWARF_TOKEN);
                            entries.accept(ModItems.ELF_TOKEN);
                            entries.accept(ModItems.HUMAN_TOKEN);
                            entries.accept(ModItems.ORC_TOKEN);

                            // === RESET ITEMS ===
                            entries.accept(ModItems.RACE_RESET_CRYSTAL);
                            entries.accept(ModItems.CLASS_RESET_CRYSTAL);
                            entries.accept(ModItems.CHARACTER_RESET_CRYSTAL);
                            entries.accept(ModItems.POTION_OF_RACIAL_REBIRTH);
                            entries.accept(ModItems.POTION_OF_CLASS_REBIRTH);
                            entries.accept(ModItems.POTION_OF_TOTAL_REBIRTH);

                            // === WARRIOR ABILITIES ===
                            entries.accept(ModItems.RAGE_TOTEM);
                            entries.accept(ModItems.WAR_HORN);
                            entries.accept(ModItems.BATTLE_STANDARD);
                            entries.accept(ModItems.WHIRLWIND_AXE);
                            entries.accept(ModItems.IRON_TALISMAN);

                            // === MAGE WANDS ===
                            entries.accept(ModItems.APPRENTICE_WAND);
                            entries.accept(ModItems.ADEPT_WAND);
                            entries.accept(ModItems.MASTER_WAND);

                            // === MAGE SCROLLS ===
                            entries.accept(ModItems.HEAL_SCROLL);
                            entries.accept(ModItems.FIREBALL_SCROLL);
                            entries.accept(ModItems.ABSORPTION_SCROLL);
                            entries.accept(ModItems.LIGHTNING_SCROLL);
                            entries.accept(ModItems.FROST_NOVA_SCROLL);
                            entries.accept(ModItems.TELEPORT_SCROLL);
                            entries.accept(ModItems.MANA_SHIELD_SCROLL);
                            entries.accept(ModItems.GRAVITY_WELL_SCROLL);
                            entries.accept(ModItems.CHAIN_LIGHTNING_SCROLL);
                            entries.accept(ModItems.ICE_WALL_SCROLL);
                            entries.accept(ModItems.METEOR_STORM_SCROLL);
                            entries.accept(ModItems.LIFE_DRAIN_SCROLL);
                            entries.accept(ModItems.DIMENSIONAL_RIFT_SCROLL);
                            entries.accept(ModItems.POLYMORPH_SCROLL);

                            // === MAGE ABILITIES ===
                            entries.accept(ModItems.ARCANE_ORB);
                            entries.accept(ModItems.TEMPORAL_CRYSTAL);
                            entries.accept(ModItems.MANA_CATALYST);
                            entries.accept(ModItems.BARRIER_CHARM);

                            // === ROGUE ABILITIES ===
                            entries.accept(ModItems.ROGUE_ABILITY_TOME);  // Existing
                            // New cooldown abilities:
                            entries.accept(ModItems.VOID_BLADE);
                            entries.accept(ModItems.VANISH_CLOAK);
                            entries.accept(ModItems.POISON_VIAL);
                            entries.accept(ModItems.ASSASSINS_MARK);

                            // === QUEST BOOKS ===
                            entries.accept(ModItems.NOVICE_QUEST_BOOK);
                            entries.accept(ModItems.APPRENTICE_QUEST_BOOK);
                            entries.accept(ModItems.EXPERT_QUEST_BOOK);
                            entries.accept(ModItems.MASTER_QUEST_TOME);

                            // === SPECIAL ITEMS ===
                            entries.accept(ModItems.COOLDOWN_ELIXIR);
                            entries.accept(ModItems.HALL_LOCATOR);

                            // === BLOCKS ===
                            entries.accept(ModBlocks.CLASS_SELECTION_ALTAR);
                            entries.accept(ModBlocks.RACE_SELECTION_ALTAR);
                            entries.accept(ModBlocks.QUEST_BLOCK);
                            entries.accept(ModBlocks.JOB_BOARD_BLOCK);


                            // === POWDERS ===
                            entries.accept(ModItems.AMETHYST_POWDER);
                            entries.accept(ModItems.DIAMOND_POWDER);
                            entries.accept(ModItems.EMERALD_POWDER);
                            entries.accept(ModItems.ECHO_DUST);
                            entries.accept(ModItems.QUARTZ_POWDER);
                            entries.accept(ModItems.SLIMEBALL_DUST);

                            // === FOOD (optional - you can remove if you want food in vanilla tabs) ===
                            // entries.accept(ModItems.CHICKEN_STEW);
                            // ... add food items if you want them here

                        })
                        .build()
        );

        DagMod.LOGGER.info("Registered DAGMod creative tab");
    }
}