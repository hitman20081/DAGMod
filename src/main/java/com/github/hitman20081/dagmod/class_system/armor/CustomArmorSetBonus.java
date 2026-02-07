package com.github.hitman20081.dagmod.class_system.armor;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Universal custom armor set bonus system
 * Supports multiple armor sets with class-specific bonuses
 */
public class CustomArmorSetBonus {

    /**
     * Enum for all custom armor sets
     */
    public enum ArmorSet {
        DRAGONSCALE("Dragonscale", Formatting.DARK_RED),
        CRYSTALFORGE("Crystalforge", Formatting.LIGHT_PURPLE),
        INFERNO("Inferno", Formatting.RED),
        NATURES_GUARD("Nature", Formatting.GREEN),  // Detects "Nature's" in name
        SHADOW("Shadow", Formatting.DARK_PURPLE),
        FORTUNA("Fortuna", Formatting.GOLD),
        NONE("None", Formatting.GRAY);

        private final String name;
        private final Formatting color;

        ArmorSet(String name, Formatting color) {
            this.name = name;
            this.color = color;
        }

        public String getName() { return name; }
        public Formatting getColor() { return color; }
    }

    // Track last bonus state per player to avoid spam
    private static final Map<UUID, ArmorSetState> playerSetStates = new HashMap<>();

    /**
     * Stores the current armor set state for a player
     */
    private static class ArmorSetState {
        ArmorSet currentSet;
        int pieceCount;

        ArmorSetState(ArmorSet set, int count) {
            this.currentSet = set;
            this.pieceCount = count;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArmorSetState other)) return false;
            return this.currentSet == other.currentSet && this.pieceCount == other.pieceCount;
        }
    }

    /**
     * Check which armor set (if any) a piece belongs to
     */
    private static ArmorSet getArmorSetType(ItemStack stack) {
        if (stack.isEmpty()) return ArmorSet.NONE;

        // Check for item_name component (used by your recipes)
        if (!stack.contains(DataComponentTypes.ITEM_NAME)) {
            return ArmorSet.NONE;
        }

        String name = stack.getName().getString();

        // Check each armor set
        for (ArmorSet set : ArmorSet.values()) {
            if (set != ArmorSet.NONE && name.contains(set.getName())) {
                return set;
            }
        }

        return ArmorSet.NONE;
    }

    /**
     * Get the player's current armor set and piece count
     */
    private static ArmorSetState getPlayerArmorSetState(ServerPlayerEntity player) {
        Map<ArmorSet, Integer> setCounts = new HashMap<>();

        // Count pieces for each set type
        ItemStack[] armorPieces = {
                player.getEquippedStack(EquipmentSlot.HEAD),
                player.getEquippedStack(EquipmentSlot.CHEST),
                player.getEquippedStack(EquipmentSlot.LEGS),
                player.getEquippedStack(EquipmentSlot.FEET)
        };

        for (ItemStack piece : armorPieces) {
            ArmorSet setType = getArmorSetType(piece);
            if (setType != ArmorSet.NONE) {
                setCounts.put(setType, setCounts.getOrDefault(setType, 0) + 1);
            }
        }

        // Find the set with the most pieces (if any)
        ArmorSet dominantSet = ArmorSet.NONE;
        int maxCount = 0;

        for (Map.Entry<ArmorSet, Integer> entry : setCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantSet = entry.getKey();
            }
        }

        return new ArmorSetState(dominantSet, maxCount);
    }

    /**
     * Apply appropriate set bonuses based on equipped armor
     */
    public static void applySetBonuses(ServerPlayerEntity player) {
        ArmorSetState currentState = getPlayerArmorSetState(player);

        // No armor or no matching set
        if (currentState.currentSet == ArmorSet.NONE || currentState.pieceCount == 0) {
            playerSetStates.remove(player.getUuid());
            activeSynergies.remove(player.getUuid()); // Clear weapon synergy tracking
            return;
        }

        // Check if state changed (to avoid spam)
        ArmorSetState lastState = playerSetStates.get(player.getUuid());
        if (lastState == null || !lastState.equals(currentState)) {
            notifySetBonus(player, currentState);
            playerSetStates.put(player.getUuid(), currentState);
        }

        // Apply bonuses based on set type
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        switch (currentState.currentSet) {
            case DRAGONSCALE -> applyDragonscaleBonuses(player, currentState.pieceCount, playerClass);
            case CRYSTALFORGE -> applyCrystalForgeBonuses(player, currentState.pieceCount, playerClass);
            case INFERNO -> applyInfernoBonuses(player, currentState.pieceCount, playerClass);
            case NATURES_GUARD -> applyNaturesGuardBonuses(player, currentState.pieceCount, playerClass);
            case SHADOW -> applyShadowBonuses(player, currentState.pieceCount, playerClass);
            case FORTUNA -> applyFortunaBonuses(player, currentState.pieceCount, playerClass);
        }

        // Apply weapon synergy bonuses if matching weapon equipped
        applyWeaponSynergy(player, currentState, playerClass);
    }

    // ===== DRAGONSCALE SET BONUSES =====

    private static void applyDragonscaleBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, 100, 0, true, false, false));
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING, 100, 0, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED, 100, 0, true, false, false));
            }
        }

        if (pieceCount >= 4) {
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, 100, 1, true, false, false));
                case "Mage" -> {} // Mana regen handled elsewhere
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED, 100, 1, true, false, false));
            }
        }
    }

    // ===== CRYSTALFORGE SET BONUSES =====

    private static void applyCrystalForgeBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            // Universal: +10% absorption & sneak speed (already on armor)
            // Add Night Vision for all classes
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 300, 0, true, false, false));

            // Class-specific 2pc bonus
            switch (playerClass) {
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 100, 0, true, false, false));
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION, 100, 0, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.JUMP_BOOST, 100, 0, true, false, false));
            }
        }

        if (pieceCount >= 4) {
            // Universal: Enhanced Night Vision + Glowing
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING, 100, 0, true, false, false));

            // Class-specific 4pc bonus
            switch (playerClass) {
                case "Mage" -> {
                    // -20% mana costs (handled in spell casting)
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION, 100, 1, true, false, false));
                }
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION, 100, 1, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.JUMP_BOOST, 100, 1, true, false, false));
            }
        }
    }

    // ===== INFERNO SET BONUSES =====

    private static void applyInfernoBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            // Fire Resistance for all classes
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 300, 0, true, false, false));

            // Class-specific 2pc bonus
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH, 100, 0, true, false, false));
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 100, 0, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SPEED, 100, 0, true, false, false));
            }
        }

        if (pieceCount >= 4) {
            // Full set: Permanent fire immunity + glowing
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 300, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING, 100, 0, true, false, false));

            // Class-specific 4pc enhancement
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH, 100, 1, true, false, false));
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION, 100, 1, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.HASTE, 100, 1, true, false, false));
            }
        }
    }

// ===== NATURE'S GUARD SET BONUSES =====

    private static void applyNaturesGuardBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            // Regeneration for all classes
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 100, 0, true, false, false));

            // Class-specific 2pc bonus
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, 100, 0, true, false, false));
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LUCK, 100, 0, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.JUMP_BOOST, 100, 1, true, false, false));
            }
        }

        if (pieceCount >= 4) {
            // Full set: Enhanced regeneration + saturation
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SATURATION, 100, 0, true, false, false));

            // Class-specific 4pc enhancement
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION, 100, 1, true, false, false));
                case "Mage" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LUCK, 100, 1, true, false, false));
                case "Rogue" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 100, 0, true, false, false));
            }
        }
    }

// ===== SHADOW SET BONUSES =====

    private static void applyShadowBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            // Speed and Night Vision for all
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 0, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 300, 0, true, false, false));

            // Rogue gets extra benefit
            if ("Rogue".equals(playerClass)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 100, 0, true, false, false));
            }
        }

        if (pieceCount >= 4) {
            // Full set: Enhanced stealth abilities
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 300, 0, true, false, false));

            // Class-specific 4pc bonus
            switch (playerClass) {
                case "Warrior" -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, 100, 0, true, false, false));
                case "Mage" -> {} // Teleportation bonus (would need custom implementation)
                case "Rogue" -> {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.INVISIBILITY, 100, 0, true, false, false));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.JUMP_BOOST, 100, 1, true, false, false));
                }
            }
        }
    }

// ===== FORTUNA SET BONUSES =====

    private static void applyFortunaBonuses(ServerPlayerEntity player, int pieceCount, String playerClass) {
        if (pieceCount >= 2) {
            // Luck and Speed for all
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.LUCK, 300, 2, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 0, true, false, false));
        }

        if (pieceCount >= 4) {
            // Full set: Massive luck + Hero of the Village
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.LUCK, 300, 4, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HERO_OF_THE_VILLAGE, 300, 0, true, false, false));

            // Universal bonus: Extra movement speed
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 1, true, false, false));
        }
    }

    // ===== WEAPON SYNERGY SYSTEM =====

    /**
     * Check if player is wielding a weapon that matches their armor set
     */
    private static boolean hasMatchingWeapon(ServerPlayerEntity player, ArmorSet armorSet) {
        ItemStack mainHand = player.getMainHandStack();
        if (mainHand.isEmpty()) return false;

        String weaponName = mainHand.getName().getString();

        return switch (armorSet) {
            case DRAGONSCALE -> weaponName.contains("Dragonscale Blade");
            case INFERNO -> weaponName.contains("Inferno Blade");
            case SHADOW -> weaponName.contains("Shadow Blade") || weaponName.contains("Phantom Blade");
            case NATURES_GUARD -> weaponName.contains("Poison Fang Spear");
            case CRYSTALFORGE -> weaponName.contains("Crystal Hammer");
            // Fortuna doesn't have a specific weapon
            default -> false;
        };
    }

    /**
     * Apply weapon synergy bonuses when player has matching armor + weapon
     */
    private static void applyWeaponSynergy(ServerPlayerEntity player, ArmorSetState state, String playerClass) {
        if (state.pieceCount < 2) return; // Need at least 2 pieces for synergy
        if (!hasMatchingWeapon(player, state.currentSet)) return;

        // Notify player of synergy (only once)
        UUID playerId = player.getUuid();
        if (!activeSynergies.containsKey(playerId) || !activeSynergies.get(playerId).equals(state.currentSet)) {
            notifyWeaponSynergy(player, state.currentSet);
            activeSynergies.put(playerId, state.currentSet);
        }

        // Apply set-specific weapon synergies
        switch (state.currentSet) {
            case DRAGONSCALE -> applyDragonscaleWeaponSynergy(player, state.pieceCount, playerClass);
            case INFERNO -> applyInfernoWeaponSynergy(player, state.pieceCount, playerClass);
            case SHADOW -> applyShadowWeaponSynergy(player, state.pieceCount, playerClass);
            case NATURES_GUARD -> applyNaturesGuardWeaponSynergy(player, state.pieceCount, playerClass);
            case CRYSTALFORGE -> applyCrystalforgeWeaponSynergy(player, state.pieceCount, playerClass);
        }
    }

    // Track active synergies to avoid spam
    private static final Map<UUID, ArmorSet> activeSynergies = new HashMap<>();

    /**
     * Notify player when weapon synergy activates
     */
    private static void notifyWeaponSynergy(ServerPlayerEntity player, ArmorSet set) {
        player.sendMessage(
                Text.literal("⚔️ " + set.getName() + " Weapon Synergy Activated! ⚔️")
                        .formatted(set.getColor()).formatted(Formatting.BOLD),
                true
        );
    }

// ===== DRAGONSCALE WEAPON SYNERGY =====

    private static void applyDragonscaleWeaponSynergy(ServerPlayerEntity player, int pieceCount, String playerClass) {
        // 2pc: Fire Aspect enhancement + Strength
        if (pieceCount >= 2) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 100, 0, true, false, false));
        }

        // 4pc: Enhanced fire damage + chance to ignite ground
        if (pieceCount >= 4) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE, 300, 0, true, false, false));

            // Warrior gets extra damage
            if ("Warrior".equals(playerClass)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION, 100, 1, true, false, false));
            }
        }
    }

// ===== INFERNO WEAPON SYNERGY =====

    private static void applyInfernoWeaponSynergy(ServerPlayerEntity player, int pieceCount, String playerClass) {
        // 2pc: Enhanced fire damage
        if (pieceCount >= 2) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 100, 0, true, false, false));
        }

        // 4pc: Burning aura - enemies near you take fire damage
        if (pieceCount >= 4) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 100, 1, true, false, false));

            // Create fire aura effect (damage nearby entities)
            // This would need a custom effect or mixin to implement fully
            // For now, give player bonuses
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.HASTE, 100, 1, true, false, false));
        }
    }

// ===== SHADOW WEAPON SYNERGY =====

    private static void applyShadowWeaponSynergy(ServerPlayerEntity player, int pieceCount, String playerClass) {
        // 2pc: Enhanced stealth attacks
        if (pieceCount >= 2) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 1, true, false, false));

            // Rogue gets invisibility boost
            if ("Rogue".equals(playerClass)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 100, 0, true, false, false));
            }
        }

        // 4pc: Backstab damage massively increased + teleport on kill
        if (pieceCount >= 4) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 2, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST, 100, 2, true, false, false));

            // Rogue gets enhanced invisibility
            if ("Rogue".equals(playerClass)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 100, 0, true, false, false));
            }
        }
    }

// ===== NATURE'S GUARD WEAPON SYNERGY =====

    private static void applyNaturesGuardWeaponSynergy(ServerPlayerEntity player, int pieceCount, String playerClass) {
        // 2pc: Poison enhancement + regeneration
        if (pieceCount >= 2) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.LUCK, 100, 0, true, false, false));
        }

        // 4pc: Poison spreads to nearby enemies + heal on poisoned kill
        if (pieceCount >= 4) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 100, 2, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.LUCK, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SATURATION, 100, 0, true, false, false));
        }
    }

// ===== CRYSTALFORGE WEAPON SYNERGY =====

    private static void applyCrystalforgeWeaponSynergy(ServerPlayerEntity player, int pieceCount, String playerClass) {
        // 2pc: Enhanced density damage + absorption
        if (pieceCount >= 2) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 100, 0, true, false, false));
        }

        // 4pc: Smash attacks create shockwaves
        if (pieceCount >= 4) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 100, 2, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE, 100, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST, 100, 2, true, false, false));
        }
    }

    // ===== NOTIFICATION SYSTEM =====

    private static void notifySetBonus(ServerPlayerEntity player, ArmorSetState state) {
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (state.pieceCount == 2) {
            player.sendMessage(
                    Text.literal("⚡ " + state.currentSet.getName() + " 2-Piece Bonus! ⚡")
                            .formatted(state.currentSet.getColor()),
                    true
            );

            String bonus = get2PieceBonusText(state.currentSet, playerClass);
            player.sendMessage(Text.literal(bonus).formatted(Formatting.YELLOW), true);

        } else if (state.pieceCount == 4) {
            player.sendMessage(
                    Text.literal("🔥 " + state.currentSet.getName() + " 4-Piece Bonus! 🔥")
                            .formatted(state.currentSet.getColor()).formatted(Formatting.BOLD),
                    true
            );

            String bonus = get4PieceBonusText(state.currentSet, playerClass);
            player.sendMessage(Text.literal(bonus).formatted(Formatting.GOLD), true);
        }
    }

    private static String get2PieceBonusText(ArmorSet set, String playerClass) {
        return switch (set) {
            case DRAGONSCALE -> switch (playerClass) {
                case "Warrior" -> "+10% Damage Reduction";
                case "Mage" -> "-15% Spell Costs";
                case "Rogue" -> "+20% Crit Chance";
                default -> "Set Bonus Active";
            };
            case CRYSTALFORGE -> switch (playerClass) {
                case "Warrior" -> "Absorption + Night Vision";
                case "Mage" -> "Regeneration + Night Vision";
                case "Rogue" -> "Jump Boost + Night Vision";
                default -> "Set Bonus Active";
            };
            case INFERNO -> "Fire Resistance + " + switch (playerClass) {
                case "Warrior" -> "Strength";
                case "Mage" -> "Regeneration";
                case "Rogue" -> "Speed";
                default -> "Power";
            };
            case NATURES_GUARD -> "Regeneration + " + switch (playerClass) {
                case "Warrior" -> "Resistance";
                case "Mage" -> "Luck";
                case "Rogue" -> "Jump Boost II";
                default -> "Nature's Blessing";
            };
            case SHADOW -> playerClass.equals("Rogue") ? "Speed + Night Vision + Invisibility" : "Speed + Night Vision";
            case FORTUNA -> "Luck III + Speed";
            default -> "Set Bonus Active";
        };
    }

    private static String get4PieceBonusText(ArmorSet set, String playerClass) {
        return switch (set) {
            case DRAGONSCALE -> switch (playerClass) {
                case "Warrior" -> "Thorns III + Enhanced Protection";
                case "Mage" -> "+50% Mana Regeneration";
                case "Rogue" -> "+25% Energy Regeneration";
                default -> "Full Set Bonus";
            };
            case CRYSTALFORGE -> switch (playerClass) {
                case "Warrior" -> "Enhanced Absorption + Glowing";
                case "Mage" -> "-20% Spell Costs + Regen II";
                case "Rogue" -> "Jump Boost II + Glowing";
                default -> "Full Set Bonus";
            };
            case INFERNO -> "Fire Immunity + " + switch (playerClass) {
                case "Warrior" -> "Strength II";
                case "Mage" -> "Regeneration II";
                case "Rogue" -> "Haste II";
                default -> "Enhanced Power";
            };
            case NATURES_GUARD -> "Regeneration II + Saturation + " + switch (playerClass) {
                case "Warrior" -> "Absorption II";
                case "Mage" -> "Luck II";
                case "Rogue" -> "Invisibility";
                default -> "Nature's Gift";
            };
            case SHADOW -> switch (playerClass) {
                case "Warrior" -> "Speed II + Resistance";
                case "Mage" -> "Speed II + Teleport Power";
                case "Rogue" -> "Speed II + Invisibility + Jump Boost II";
                default -> "Shadow Mastery";
            };
            case FORTUNA -> "Luck V + Hero of Village + Speed II";
            default -> "Full Set Bonus";
        };
    }

    // ===== BONUS GETTERS FOR INTEGRATION =====

    /**
     * Get mana cost reduction for player's current armor set
     */
    public static float getManaCostReduction(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Mage".equals(playerClass)) return 0f;

        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 2 ? 0.15f : 0f; // 15% at 2pc
            case CRYSTALFORGE -> state.pieceCount >= 4 ? 0.20f : 0f; // 20% at 4pc
            case INFERNO -> state.pieceCount >= 4 ? 0.10f : 0f; // 10% at 4pc (fire mage)
            case SHADOW -> state.pieceCount >= 2 ? 0.10f : 0f; // 10% at 2pc (shadow magic)
            default -> 0f;
        };
    }

    /**
     * Get mana regen bonus for player's current armor set
     */
    public static float getManaRegenBonus(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Mage".equals(playerClass)) return 0f;

        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.50f : 0f; // 50% at 4pc
            case CRYSTALFORGE -> state.pieceCount >= 2 ? 0.25f : 0f; // 25% at 2pc
            case NATURES_GUARD -> state.pieceCount >= 4 ? 0.30f : 0f; // 30% at 4pc (nature's energy)
            default -> 0f;
        };
    }

    /**
     * Get crit chance bonus for player's current armor set
     */
    public static float getCritBonus(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Rogue".equals(playerClass)) return 0f;

        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 2 ? 0.20f : 0f; // 20% at 2pc
            case SHADOW -> state.pieceCount >= 2 ? 0.25f : 0f; // 25% at 2pc (shadow assassin)
            case NATURES_GUARD -> state.pieceCount >= 4 ? 0.15f : 0f; // 15% at 4pc (hunter)
            default -> 0f;
        };
    }

    /**
     * Get energy regen bonus for player's current armor set
     */
    public static float getEnergyRegenBonus(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Rogue".equals(playerClass)) return 0f;

        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.25f : 0f; // 25% at 4pc
            case SHADOW -> state.pieceCount >= 4 ? 0.35f : 0f; // 35% at 4pc (shadow energy)
            case CRYSTALFORGE -> state.pieceCount >= 2 ? 0.15f : 0f; // 15% at 2pc (crystal energy)
            default -> 0f;
        };
    }
    /**
     * Get backstab damage multiplier for weapon synergy
     */
    public static float getWeaponSynergyBackstabBonus(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

        if (!"Rogue".equals(playerClass)) return 0f;
        if (!hasMatchingWeapon(player, state.currentSet)) return 0f;

        return switch (state.currentSet) {
            case SHADOW -> state.pieceCount >= 4 ? 0.75f : state.pieceCount >= 2 ? 0.35f : 0f; // 35% at 2pc, 75% at 4pc!
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.25f : 0f; // 25% at 4pc
            default -> 0f;
        };
    }

    /**
     * Get melee damage bonus for weapon synergy
     */
    public static float getWeaponSynergyDamageBonus(ServerPlayerEntity player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!hasMatchingWeapon(player, state.currentSet)) return 0f;

        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.30f : state.pieceCount >= 2 ? 0.15f : 0f;
            case INFERNO -> state.pieceCount >= 4 ? 0.25f : state.pieceCount >= 2 ? 0.15f : 0f;
            default -> 0f;
        };
    }
}