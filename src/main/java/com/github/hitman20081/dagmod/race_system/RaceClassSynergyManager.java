package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.BiomeTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class RaceClassSynergyManager {

    /**
     * Check and apply synergy bonuses based on race + class combination
     * This should be called periodically (e.g., every second)
     */
    public static void applySynergyBonuses(ServerPlayer player) {
        String race = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

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
    private static void applyDwarfWarriorSynergy(ServerPlayer player) {
        // If below Y=50 (underground), give resistance
        if (player.getY() < 50) {
            if (!player.hasEffect(MobEffects.RESISTANCE)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.RESISTANCE, 60, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Elf Rogue - Invisibility in forest biomes
     */
    private static void applyElfRogueSynergy(ServerPlayer player) {
        ServerLevel world = player.level();
        BlockPos pos = player.blockPosition();

        // Check if in forest biome
        if (world.getBiome(pos).is(BiomeTags.IS_FOREST)) {
            // Give brief invisibility when sneaking
            if (player.isShiftKeyDown() && !player.hasEffect(MobEffects.INVISIBILITY)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.INVISIBILITY, 40, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Orc Warrior - Berserker rage at low health
     */
    private static void applyOrcWarriorSynergy(ServerPlayer player) {
        float healthPercent = player.getHealth() / player.getMaxHealth();

        // If below 30% health, activate berserker mode
        if (healthPercent < 0.3f) {
            if (!player.hasEffect(MobEffects.STRENGTH)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.STRENGTH, 100, 0, true, false, true
                ));
                player.addEffect(new MobEffectInstance(
                        MobEffects.SPEED, 100, 0, true, false, true
                ));

                player.sendOverlayMessage(
                        Component.literal("⚔ BERSERKER RAGE ACTIVATED!").withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    /**
     * Human Mage - Better mana regeneration (shorter potion cooldowns)
     */
    private static void applyHumanMageSynergy(ServerPlayer player) {
        // Humans naturally adapt - give slight regeneration
        if (!player.hasEffect(MobEffects.REGENERATION)) {
            if (player.getRandom().nextFloat() < 0.01f) { // 1% chance per tick
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION, 100, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Dwarf Mage - Fire resistance (forged in mountains)
     */
    private static void applyDwarfMageSynergy(ServerPlayer player) {
        if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            // Always have low-level fire resistance
            player.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE, 60, 0, true, false, true
            ));
        }
    }

    /**
     * Elf Mage - Night vision in forests
     */
    private static void applyElfMageSynergy(ServerPlayer player) {
        ServerLevel world = player.level();
        BlockPos pos = player.blockPosition();

        if (world.getBiome(pos).is(BiomeTags.IS_FOREST)) {
            if (!player.hasEffect(MobEffects.HASTE)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.HASTE, 300, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Orc Rogue - Extra damage when attacking from behind (stacks with backstab)
     */
    private static void applyOrcRogueSynergy(ServerPlayer player) {
        // This is handled in RogueCombatHandler as a passive bonus
        // No active effect needed here
    }

    /**
     * Human Warrior - Balanced combat boost
     */
    private static void applyHumanWarriorSynergy(ServerPlayer player) {
        // Humans are adaptable - small boost to absorption when in combat
        if (player.getLastDamageSource() != null) {
            if (!player.hasEffect(MobEffects.ABSORPTION)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.ABSORPTION, 200, 0, true, false, true
                ));
            }
        }
    }

    /**
     * Human Rogue - Adaptable movement
     */
    private static void applyHumanRogueSynergy(ServerPlayer player) {
        // Humans learn quickly - small jump boost
        if (!player.hasEffect(MobEffects.JUMP_BOOST)) {
            if (player.getRandom().nextFloat() < 0.005f) { // 0.5% chance per tick
                player.addEffect(new MobEffectInstance(
                        MobEffects.JUMP_BOOST, 200, 0, true, false, true
                ));
            }
        }
    }
}