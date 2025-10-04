package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RaceAbilityManager {

    public static void applyRaceAbilities(ServerPlayerEntity player) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUuid());

        // Remove any existing race modifiers first
        removeAllRaceModifiers(player);

        // Apply new modifiers based on race
        switch (playerRace) {
            case "Human" -> applyHumanAbilities(player);
            case "Dwarf" -> applyDwarfAbilities(player);
            case "Elf" -> applyElfAbilities(player);
            case "Orc" -> applyOrcAbilities(player);
        }
    }

    private static void applyHumanAbilities(ServerPlayerEntity player) {
        // Humans are balanced - no stat bonuses, but versatile
        // Bonus: Faster experience gain (can be handled in XP event listener)
        // No attribute modifiers needed here
    }

    private static void applyDwarfAbilities(ServerPlayerEntity player) {
        // +20% mining speed (block break speed)
        var miningAttribute = player.getAttributeInstance(EntityAttributes.BLOCK_BREAK_SPEED);
        if (miningAttribute != null) {
            miningAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "dwarf_mining"),
                            0.20, // +20%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // -5% movement speed (slow and sturdy)
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "dwarf_speed"),
                            -0.05, // -5%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +1 heart (2 health points)
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "dwarf_health"),
                            2.0, // +1 heart
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
    }

    private static void applyElfAbilities(ServerPlayerEntity player) {
        // +15% movement speed (graceful and agile)
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "elf_speed"),
                            0.15, // +15%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +10% attack range (better with bows)
        var reachAttribute = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (reachAttribute != null) {
            reachAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "elf_reach"),
                            0.5, // +0.5 blocks
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // Hero of the Village - Elves are respected by villagers
        if (!player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HERO_OF_THE_VILLAGE,
                    Integer.MAX_VALUE, // Permanent
                    0,
                    true,  // ambient
                    false, // no particles
                    true   // show icon
            ));
        }
    }

    private static void applyOrcAbilities(ServerPlayerEntity player) {
        // +15% melee attack damage (fierce warriors)
        var attackAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "orc_attack"),
                            0.15, // +15%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +2 hearts (4 health points)
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "orc_health"),
                            4.0, // +2 hearts
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // Heal to full health after gaining max health
        player.setHealth(player.getMaxHealth());
    }

    private static void removeAllRaceModifiers(ServerPlayerEntity player) {
        // Remove mining speed modifiers
        var miningAttribute = player.getAttributeInstance(EntityAttributes.BLOCK_BREAK_SPEED);
        if (miningAttribute != null) {
            miningAttribute.removeModifier(Identifier.of("dagmod", "dwarf_mining"));
        }

        // Remove health modifiers
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(Identifier.of("dagmod", "dwarf_health"));
            healthAttribute.removeModifier(Identifier.of("dagmod", "orc_health"));
        }

        // Remove speed modifiers
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(Identifier.of("dagmod", "dwarf_speed"));
            speedAttribute.removeModifier(Identifier.of("dagmod", "elf_speed"));
        }

        // Remove attack modifiers
        var attackAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(Identifier.of("dagmod", "orc_attack"));
        }

        // Remove reach modifiers
        var reachAttribute = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (reachAttribute != null) {
            reachAttribute.removeModifier(Identifier.of("dagmod", "elf_reach"));
        }
    }
}