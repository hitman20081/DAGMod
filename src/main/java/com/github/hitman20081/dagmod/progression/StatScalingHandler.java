package com.github.hitman20081.dagmod.progression;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

/**
 * Handles stat scaling based on player level
 */
public class StatScalingHandler {

    // Modifier IDs for tracking our stat bonuses
    private static final Identifier HP_MODIFIER_ID = Identifier.fromNamespaceAndPath("dagmod", "level_hp_bonus");
    private static final Identifier ATTACK_MODIFIER_ID = Identifier.fromNamespaceAndPath("dagmod", "level_attack_bonus");
    private static final Identifier ARMOR_MODIFIER_ID = Identifier.fromNamespaceAndPath("dagmod", "level_armor_bonus");

    /**
     * Apply level-based stat bonuses to a player
     * Called when player levels up or joins server
     */
    public static void applyLevelStats(ServerPlayer player, int level) {
        // Remove old modifiers first
        removeAllLevelStats(player);

        // Calculate bonuses
        int hpBonus = calculateHPBonus(level);
        double attackBonus = calculateAttackBonus(level);
        double armorBonus = calculateArmorBonus(level);

        // Apply HP bonus (+1 heart every 10 levels)
        if (hpBonus > 0) {
            var hpModifier = new AttributeModifier(
                    HP_MODIFIER_ID,
                    hpBonus,
                    AttributeModifier.Operation.ADD_VALUE
            );

            var hpAttribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (hpAttribute != null && !hpAttribute.hasModifier(HP_MODIFIER_ID)) {
                hpAttribute.addPermanentModifier(hpModifier);
            }
        }

        // Apply attack bonus (every 5 levels)
        if (attackBonus > 0) {
            var attackModifier = new AttributeModifier(
                    ATTACK_MODIFIER_ID,
                    attackBonus,
                    AttributeModifier.Operation.ADD_VALUE
            );

            var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackAttribute != null && !attackAttribute.hasModifier(ATTACK_MODIFIER_ID)) {
                attackAttribute.addPermanentModifier(attackModifier);
            }
        }

        // Apply armor bonus (every 10 levels)
        if (armorBonus > 0) {
            var armorModifier = new AttributeModifier(
                    ARMOR_MODIFIER_ID,
                    armorBonus,
                    AttributeModifier.Operation.ADD_VALUE
            );

            var armorAttribute = player.getAttribute(Attributes.ARMOR);
            if (armorAttribute != null && !armorAttribute.hasModifier(ARMOR_MODIFIER_ID)) {
                armorAttribute.addPermanentModifier(armorModifier);
            }
        }

        // Update player's current health to match new max
        float currentHealthPercent = player.getHealth() / player.getMaxHealth();
        player.setHealth(player.getMaxHealth() * currentHealthPercent);
    }

    /**
     * Remove all level-based stat modifiers
     */
    public static void removeAllLevelStats(ServerPlayer player) {
        var hpAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttribute != null) {
            hpAttribute.removeModifier(HP_MODIFIER_ID);
        }

        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(ATTACK_MODIFIER_ID);
        }

        var armorAttribute = player.getAttribute(Attributes.ARMOR);
        if (armorAttribute != null) {
            armorAttribute.removeModifier(ARMOR_MODIFIER_ID);
        }
    }

    /**
     * Calculate HP bonus from level
     * +1 heart (2 HP) every 10 levels → 30 hearts at level 200
     */
    private static int calculateHPBonus(int level) {
        return (level / 10) * 2; // Level 10 = +2 HP, Level 100 = +20 HP, Level 200 = +40 HP
    }

    /**
     * Calculate attack damage bonus from level
     * +1 attack every 5 levels
     */
    private static double calculateAttackBonus(int level) {
        return (level / 5); // Level 5 = +1, Level 10 = +2, etc.
    }

    /**
     * Calculate armor bonus from level
     * +1 armor every 10 levels
     */
    private static double calculateArmorBonus(int level) {
        return (level / 10); // Level 10 = +1, Level 20 = +2, etc.
    }

    /**
     * Get stat summary for display
     */
    public static String getStatSummary(int level) {
        int hp = calculateHPBonus(level);
        double attack = calculateAttackBonus(level);
        double armor = calculateArmorBonus(level);

        StringBuilder summary = new StringBuilder();
        if (hp > 0) summary.append("+").append(hp).append(" HP ");
        if (attack > 0) summary.append("+").append((int)attack).append(" ATK ");
        if (armor > 0) summary.append("+").append((int)armor).append(" ARM");

        return summary.toString().trim();
    }
}