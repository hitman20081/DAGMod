package com.github.hitman20081.dagmod.class_system;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ClassAbilityManager {

    // Unique UUIDs for each modifier (these must be consistent)
    private static final UUID WARRIOR_HEALTH_UUID = UUID.fromString("a1b2c3d4-1234-5678-9abc-def012345678");
    private static final UUID WARRIOR_ATTACK_UUID = UUID.fromString("b2c3d4e5-2345-6789-abcd-ef0123456789");
    private static final UUID WARRIOR_SPEED_UUID = UUID.fromString("c3d4e5f6-3456-789a-bcde-f01234567890");

    public static void applyClassAbilities(ServerPlayerEntity player) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        // Remove any existing class modifiers first
        removeAllClassModifiers(player);

        // Apply new modifiers based on class
        switch (playerClass) {
            case "Warrior" -> applyWarriorAbilities(player);
            case "Mage" -> applyMageAbilities(player);
            case "Rogue" -> applyRogueAbilities(player);
        }
    }

    private static void applyWarriorAbilities(ServerPlayerEntity player) {
        // +4 hearts (8 health points)
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "warrior_health"),
                            8.0, // +4 hearts
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // +25% melee attack damage
        var attackAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "warrior_attack"),
                            0.25, // +25%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // -10% movement speed
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "warrior_speed"),
                            -0.10, // -10%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Heal to full health after gaining max health
        player.setHealth(player.getMaxHealth());
    }

    private static void applyMageAbilities(ServerPlayerEntity player) {
        // -2 hearts (4 health points)
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "mage_health"),
                            -4.0, // -2 hearts
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // -25% melee attack damage
        var attackAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "mage_attack"),
                            -0.25, // -25%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Cap health if current health exceeds new max
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void applyRogueAbilities(ServerPlayerEntity player) {
        // -1 heart (2 health points)
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "rogue_health"),
                            -2.0, // -1 heart
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // +30% movement speed
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(
                            Identifier.of("dagmod", "rogue_speed"),
                            0.30, // +30%
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // Cap health if current health exceeds new max
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void removeAllClassModifiers(ServerPlayerEntity player) {
        // Remove health modifiers
        var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(Identifier.of("dagmod", "warrior_health"));
            healthAttribute.removeModifier(Identifier.of("dagmod", "mage_health"));
            healthAttribute.removeModifier(Identifier.of("dagmod", "rogue_health"));
        }

        // Remove attack modifiers
        var attackAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(Identifier.of("dagmod", "warrior_attack"));
            attackAttribute.removeModifier(Identifier.of("dagmod", "mage_attack"));
        }

        // Remove speed modifiers
        var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(Identifier.of("dagmod", "warrior_speed"));
            speedAttribute.removeModifier(Identifier.of("dagmod", "rogue_speed"));
        }
    }
}