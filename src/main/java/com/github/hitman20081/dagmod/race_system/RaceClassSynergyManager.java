package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class RaceClassSynergyManager {

    /**
     * Check and apply synergy bonuses based on race + class combination
     * This should be called periodically (e.g., every second)
     */
    public static void applySynergyBonuses(ServerPlayerEntity player) {
        String race = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (race.equals("none") || playerClass.equals("none")) {
            return;
        }

        // Dwarf + Warrior synergy
        if (race.equals("Dwarf") && playerClass.equals("Warrior")) {
            applyDwarfWarriorSynergy(player);
        }

        // Elf + Rogue synergy
        if (race.equals("Elf") && playerClass.equals("Rogue")) {
            applyElfRogueSynergy(player);
        }

        // Orc + Warrior synergy
        if (race.equals("Orc") && playerClass.equals("Warrior")) {
            applyOrcWarriorSynergy(player);
        }

        // Human + Mage synergy
        if (race.equals("Human") && playerClass.equals("Mage")) {
            applyHumanMageSynergy(player);
        }

        // Dwarf + Mage synergy
        if (race.equals("Dwarf") && playerClass.equals("Mage")) {
            applyDwarfMageSynergy(player);
        }

        // Elf + Mage synergy
        if (race.equals("Elf") && playerClass.equals("Mage")) {
            applyElfMageSynergy(player);
        }

        // Orc + Rogue synergy
        if (race.equals("Orc") && playerClass.equals("Rogue")) {
            applyOrcRogueSynergy(player);
        }

        // Human + Warrior synergy
        if (race.equals("Human") && playerClass.equals("Warrior")) {
            applyHumanWarriorSynergy(player);
        }

        // Human + Rogue synergy
        if (race.equals("Human") && playerClass.equals("Rogue")) {
            applyHumanRogueSynergy(player);
        }
    }

    /**
     * Dwarf Warrior - Extra resistance when underground
     */
    private static void applyDwarfWarriorSynergy(ServerPlayerEntity player) {
        // If below Y=50 (underground), give resistance
        if (player.getY() < 50) {
            if (!player.hasStatusEffect(StatusEffects.RESISTANCE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, 60, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Elf Rogue - Invisibility in forest biomes
     */
    private static void applyElfRogueSynergy(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        BlockPos pos = player.getBlockPos();

        // Check if in forest biome
        if (world.getBiome(pos).isIn(BiomeTags.IS_FOREST)) {
            // Give brief invisibility when sneaking
            if (player.isSneaking() && !player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 40, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Orc Warrior - Berserker rage at low health
     */
    private static void applyOrcWarriorSynergy(ServerPlayerEntity player) {
        float healthPercent = player.getHealth() / player.getMaxHealth();

        // If below 30% health, activate berserker mode
        if (healthPercent < 0.3f) {
            if (!player.hasStatusEffect(StatusEffects.STRENGTH)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH, 100, 0, true, false, true
                ));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED, 100, 0, true, false, true
                ));

                player.sendMessage(
                        Text.literal("⚔ BERSERKER RAGE ACTIVATED!").formatted(Formatting.DARK_RED),
                        true
                );
            }
        }
    }

    /**
     * Human Mage - Better mana regeneration (shorter potion cooldowns)
     */
    private static void applyHumanMageSynergy(ServerPlayerEntity player) {
        // Humans naturally adapt - give slight regeneration
        if (!player.hasStatusEffect(StatusEffects.REGENERATION)) {
            if (player.getRandom().nextFloat() < 0.01f) { // 1% chance per tick
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 100, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Dwarf Mage - Fire resistance (forged in mountains)
     */
    private static void applyDwarfMageSynergy(ServerPlayerEntity player) {
        if (!player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            // Always have low-level fire resistance
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 60, 0, true, false, true
            ));
        }
    }

    /**
     * Elf Mage - Night vision in forests
     */
    private static void applyElfMageSynergy(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        BlockPos pos = player.getBlockPos();

        if (world.getBiome(pos).isIn(BiomeTags.IS_FOREST)) {
            if (!player.hasStatusEffect(StatusEffects.HASTE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.HASTE, 300, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Orc Rogue - Extra damage when attacking from behind (stacks with backstab)
     */
    private static void applyOrcRogueSynergy(ServerPlayerEntity player) {
        // This is handled in RogueCombatHandler as a passive bonus
        // No active effect needed here
    }

    /**
     * Human Warrior - Balanced combat boost
     */
    private static void applyHumanWarriorSynergy(ServerPlayerEntity player) {
        // Humans are adaptable - small boost to absorption when in combat
        if (player.getRecentDamageSource() != null) {
            if (!player.hasStatusEffect(StatusEffects.ABSORPTION)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION, 200, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Human Rogue - Adaptable movement
     */
    private static void applyHumanRogueSynergy(ServerPlayerEntity player) {
        // Humans learn quickly - small jump boost
        if (!player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            if (player.getRandom().nextFloat() < 0.005f) { // 0.5% chance per tick
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.JUMP_BOOST, 200, 0, true, false, true
                ));
            }
        }
    }
}