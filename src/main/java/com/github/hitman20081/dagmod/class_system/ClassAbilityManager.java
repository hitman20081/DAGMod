package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public class ClassAbilityManager {

    // Unique UUIDs for each modifier (these must be consistent)
    private static final UUID WARRIOR_HEALTH_UUID = UUID.fromString("a1b2c3d4-1234-5678-9abc-def012345678");
    private static final UUID WARRIOR_ATTACK_UUID = UUID.fromString("b2c3d4e5-2345-6789-abcd-ef0123456789");
    private static final UUID WARRIOR_SPEED_UUID = UUID.fromString("c3d4e5f6-3456-789a-bcde-f01234567890");

    public static void applyClassAbilities(ServerPlayer player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        // Remove any existing class modifiers first
        removeAllClassModifiers(player);

        // Apply new modifiers based on class
        switch (playerClass) {
            case "Warrior" -> applyWarriorAbilities(player);
            case "Mage" -> applyMageAbilities(player);
            case "Rogue" -> applyRogueAbilities(player);
        }
    }

    private static void applyWarriorAbilities(ServerPlayer player) {
        // +4 hearts (8 health points)
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "warrior_health"),
                            8.0, // +4 hearts
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // +25% melee attack damage
        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "warrior_attack"),
                            0.25, // +25%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // -10% movement speed
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "warrior_speed"),
                            -0.10, // -10%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Heal to full health after gaining max health
        player.setHealth(player.getMaxHealth());

        // Tip: inform the player about Shield Bash
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("§6[Warrior] §7Tip: §fEquip a Shield and right-click to activate §6Shield Bash§f — dash forward, deal damage, and knock back enemies!"));
    }

    private static void applyMageAbilities(ServerPlayer player) {
        // +1 hearts (2 health points)
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "mage_health"),
                            2.0, // +1 hearts
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // -25% melee attack damage
        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "mage_attack"),
                            -0.25, // -25%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Cap health if current health exceeds new max
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void applyRogueAbilities(ServerPlayer player) {
        // +2 heart (4 health points)
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "rogue_health"),
                            4.0, // +2 heart
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // +30% movement speed
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addOrUpdateTransientModifier(
                    new AttributeModifier(
                            Identifier.fromNamespaceAndPath("dagmod", "rogue_speed"),
                            0.30, // +30%
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Cap health if current health exceeds new max
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void removeAllClassModifiers(ServerPlayer player) {
        // Remove health modifiers
        var healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "warrior_health"));
            healthAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "mage_health"));
            healthAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "rogue_health"));
        }

        // Remove attack modifiers
        var attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "warrior_attack"));
            attackAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "mage_attack"));
        }

        // Remove speed modifiers
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "warrior_speed"));
            speedAttribute.removeModifier(Identifier.fromNamespaceAndPath("dagmod", "rogue_speed"));
        }
    }
}