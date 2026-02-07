package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    // Create the custom creative tab
    public static final RegistryKey<ItemGroup> DAGMOD_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(DagMod.MOD_ID, "dagmod")
    );

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, DAGMOD_GROUP,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.WARRIOR_TOKEN)) // Use warrior token as icon
                        .displayName(Text.translatable("itemGroup.dagmod"))
                        .entries((displayContext, entries) -> {

                            // === CLASS SELECTION TOKENS ===
                            entries.add(ModItems.WARRIOR_TOKEN);
                            entries.add(ModItems.MAGE_TOKEN);
                            entries.add(ModItems.ROGUE_TOKEN);

                            // === RACE SELECTION TOKENS ===
                            entries.add(ModItems.DWARF_TOKEN);
                            entries.add(ModItems.ELF_TOKEN);
                            entries.add(ModItems.HUMAN_TOKEN);
                            entries.add(ModItems.ORC_TOKEN);

                            // === RESET ITEMS ===
                            entries.add(ModItems.RACE_RESET_CRYSTAL);
                            entries.add(ModItems.CLASS_RESET_CRYSTAL);
                            entries.add(ModItems.CHARACTER_RESET_CRYSTAL);
                            entries.add(ModItems.POTION_OF_RACIAL_REBIRTH);
                            entries.add(ModItems.POTION_OF_CLASS_REBIRTH);
                            entries.add(ModItems.POTION_OF_TOTAL_REBIRTH);

                            // === WARRIOR ABILITIES ===
                            entries.add(ModItems.RAGE_TOTEM);
                            entries.add(ModItems.WAR_HORN);
                            entries.add(ModItems.BATTLE_STANDARD);
                            entries.add(ModItems.WHIRLWIND_AXE);
                            entries.add(ModItems.IRON_TALISMAN);

                            // === MAGE WANDS ===
                            entries.add(ModItems.APPRENTICE_WAND);
                            entries.add(ModItems.ADEPT_WAND);
                            entries.add(ModItems.MASTER_WAND);

                            // === MAGE SCROLLS ===
                            entries.add(ModItems.HEAL_SCROLL);
                            entries.add(ModItems.FIREBALL_SCROLL);
                            entries.add(ModItems.ABSORPTION_SCROLL);
                            entries.add(ModItems.LIGHTNING_SCROLL);
                            entries.add(ModItems.FROST_NOVA_SCROLL);
                            entries.add(ModItems.TELEPORT_SCROLL);
                            entries.add(ModItems.MANA_SHIELD_SCROLL);

                            // === MAGE ABILITIES ===
                            entries.add(ModItems.ARCANE_ORB);
                            entries.add(ModItems.TEMPORAL_CRYSTAL);
                            entries.add(ModItems.MANA_CATALYST);
                            entries.add(ModItems.BARRIER_CHARM);

                            // === ROGUE ABILITIES ===
                            entries.add(ModItems.ROGUE_ABILITY_TOME);  // Existing
                            // New cooldown abilities:
                            entries.add(ModItems.VOID_BLADE);
                            entries.add(ModItems.VANISH_CLOAK);
                            entries.add(ModItems.POISON_VIAL);
                            entries.add(ModItems.ASSASSINS_MARK);

                            // === QUEST BOOKS ===
                            entries.add(ModItems.NOVICE_QUEST_BOOK);
                            entries.add(ModItems.APPRENTICE_QUEST_BOOK);
                            entries.add(ModItems.EXPERT_QUEST_BOOK);
                            entries.add(ModItems.MASTER_QUEST_TOME);

                            // === SPECIAL ITEMS ===
                            entries.add(ModItems.COOLDOWN_ELIXIR);
                            entries.add(ModItems.HALL_LOCATOR);

                            // === BLOCKS ===
                            entries.add(ModBlocks.CLASS_SELECTION_ALTAR);
                            entries.add(ModBlocks.RACE_SELECTION_ALTAR);
                            entries.add(ModBlocks.QUEST_BLOCK);
                            entries.add(ModBlocks.JOB_BOARD_BLOCK);


                            // === POWDERS ===
                            entries.add(ModItems.AMETHYST_POWDER);
                            entries.add(ModItems.DIAMOND_POWDER);
                            entries.add(ModItems.EMERALD_POWDER);
                            entries.add(ModItems.ECHO_DUST);
                            entries.add(ModItems.QUARTZ_POWDER);
                            entries.add(ModItems.SLIMEBALL_DUST);

                            // === FOOD (optional - you can remove if you want food in vanilla tabs) ===
                            // entries.add(ModItems.CHICKEN_STEW);
                            // ... add food items if you want them here

                        })
                        .build()
        );

        DagMod.LOGGER.info("Registered DAGMod creative tab");
    }
}