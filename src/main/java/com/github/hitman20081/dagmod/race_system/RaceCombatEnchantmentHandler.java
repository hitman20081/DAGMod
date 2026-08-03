package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

/**
 * Attack-damage multipliers for the 3 race-specific combat enchantments: Dwarf's Deep Striker,
 * Elf's Forest's Blessing, and Orc's Berserker's Fury. Applied to the attacking player's
 * outgoing damage from RaceEnchantmentCombatMixin. Each checks the attacker's current race,
 * the enchantment's level on their held weapon, and its condition, returning 1.0f (no bonus)
 * otherwise.
 */
public class RaceCombatEnchantmentHandler {

    private RaceCombatEnchantmentHandler() {}

    public static float getDamageMultiplier(ServerPlayer attacker, ServerLevel world) {
        String race = RaceSelectionAltarBlock.getPlayerRace(attacker.getUUID());
        return switch (race) {
            case "Dwarf" -> deepStrikerMultiplier(attacker, world);
            case "Elf" -> forestBlessingMultiplier(attacker, world);
            case "Orc" -> berserkersFuryMultiplier(attacker);
            default -> 1.0f;
        };
    }

    /** Dwarf: bonus damage while attacking from underground (below sea level). */
    private static float deepStrikerMultiplier(ServerPlayer attacker, ServerLevel world) {
        int level = enchantLevel(attacker, world, "dwarf_deep_striker");
        if (level <= 0) return 1.0f;
        if (attacker.getY() >= world.getSeaLevel()) return 1.0f;
        return 1.0f + 0.1f * level;
    }

    /** Elf: bonus damage while standing in a forest-tagged biome. */
    private static float forestBlessingMultiplier(ServerPlayer attacker, ServerLevel world) {
        int level = enchantLevel(attacker, world, "elf_forest_blessing");
        if (level <= 0) return 1.0f;
        BlockPos pos = attacker.blockPosition();
        Holder<Biome> biome = world.getBiome(pos);
        if (!biome.is(BiomeTags.IS_FOREST)) return 1.0f;
        return 1.0f + 0.1f * level;
    }

    /** Orc: bonus damage that scales up the more health the attacker is missing. */
    private static float berserkersFuryMultiplier(ServerPlayer attacker) {
        int level = enchantLevel(attacker, attacker.level(), "orc_berserkers_fury");
        if (level <= 0) return 1.0f;
        float missingHealthFraction = 1.0f - (attacker.getHealth() / attacker.getMaxHealth());
        return 1.0f + (0.08f * level) * missingHealthFraction;
    }

    private static int enchantLevel(ServerPlayer player, ServerLevel world, String enchantId) {
        return CustomEnchantmentEffects.getEnchantmentLevel(player.getMainHandItem(), world, enchantId);
    }
}
