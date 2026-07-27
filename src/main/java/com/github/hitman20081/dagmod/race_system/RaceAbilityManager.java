package com.github.hitman20081.dagmod.race_system;

import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

public class RaceAbilityManager {

    public static void applyRaceAbilities(ServerPlayer player) {
        String playerRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());

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

    private static void applyHumanAbilities(ServerPlayer player) {
        // Humans are balanced - no stat bonuses, but versatile
        // Bonus: Faster experience gain (can be handled in XP event listener)
        // No attribute modifiers needed here
    }

    private static void applyDwarfAbilities(ServerPlayer player) {
        // +20% mining speed (block break speed)
        var miningAttribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (miningAttribute != null) {
            miningAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "dwarf_mining"),
                            0.20, // +20%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // -5% movement speed (slow and sturdy)
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "dwarf_speed"),
                            -0.05, // -5%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +0.5 heart (1 health points)
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "dwarf_health"),
                            2.0, // +0.5 heart
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
    }

    private static void applyElfAbilities(ServerPlayer player) {
        // +15% movement speed (graceful and agile)
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "elf_speed"),
                            0.15, // +15%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +10% attack range (better with bows)
        var reachAttribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (reachAttribute != null) {
            reachAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "elf_reach"),
                            0.5, // +0.5 blocks
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // Hero of the Village - Applied continuously by tick handler
        // Duration: 300 ticks (15 seconds), reapplied constantly = effectively permanent
        // But can be cleared if player changes race
        if (!player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.HERO_OF_THE_VILLAGE,
                    300, // 15 seconds (will be reapplied by synergy system)
                    0,
                    true,  // ambient
                    false, // no particles
                    false  // no icon (changed from true to reduce clutter)
            ));
        }
    }

    private static void applyOrcAbilities(ServerPlayer player) {
        // +15% melee attack damage (fierce warriors)
        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "orc_attack"),
                            0.15, // +15%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +1.5 hearts (3 health points)
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "orc_health"),
                            3.0, // +1.5 hearts
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // Heal to full health after gaining max health
        player.setHealth(player.getMaxHealth());
    }

    private static void removeAllRaceModifiers(ServerPlayer player) {
        // Remove mining speed modifiers
        var miningAttribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (miningAttribute != null) {
            miningAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "dwarf_mining"));
        }

        // Remove health modifiers
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "dwarf_health"));
            healthAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "orc_health"));
        }

        // Remove speed modifiers
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "dwarf_speed"));
            speedAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "elf_speed"));
        }

        // Remove attack modifiers
        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "orc_attack"));
        }

        // Remove reach modifiers
        var reachAttribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (reachAttribute != null) {
            reachAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "elf_reach"));
        }

        // Remove Hero of the Village effect (Elf-specific)
        if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            player.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
        }
    }
}