package com.github.hitman20081.dagmod.class_system.armor;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomArmorSetBonus {

    public enum ArmorSet {
        MYTHRIL("Mythril",         ChatFormatting.AQUA),
        DRAGONSCALE("Dragonscale", ChatFormatting.DARK_RED),
        INFERNO("Inferno",         ChatFormatting.RED),
        CRYSTALFORGE("Crystalforge", ChatFormatting.LIGHT_PURPLE),
        NATURESGUARD("Nature's Guard", ChatFormatting.GREEN),
        SHADOW("Shadow",           ChatFormatting.DARK_PURPLE),
        FROSTBOUND("Frostbound",   ChatFormatting.BLUE),
        SOLARWEAVE("Solarweave",   ChatFormatting.YELLOW),
        STORMCALLER("Stormcaller", ChatFormatting.DARK_AQUA),
        OBSIDIAN("Obsidian",       ChatFormatting.DARK_GRAY),
        FORTUNA("Fortuna",         ChatFormatting.GOLD),
        NONE("None",               ChatFormatting.GRAY);

        private final String name;
        private final ChatFormatting color;

        ArmorSet(String name, ChatFormatting color) {
            this.name = name;
            this.color = color;
        }

        public String getName()          { return name; }
        public ChatFormatting getColor() { return color; }
    }

    // Item → ArmorSet lookup (reliable; no name-matching needed)
    private static final Map<Item, ArmorSet> ITEM_TO_SET = new HashMap<>();

    static {
        ITEM_TO_SET.put(ModItems.MYTHRIL_HELMET,           ArmorSet.MYTHRIL);
        ITEM_TO_SET.put(ModItems.MYTHRIL_CHESTPLATE,       ArmorSet.MYTHRIL);
        ITEM_TO_SET.put(ModItems.MYTHRIL_LEGGINGS,         ArmorSet.MYTHRIL);
        ITEM_TO_SET.put(ModItems.MYTHRIL_BOOTS,            ArmorSet.MYTHRIL);

        ITEM_TO_SET.put(ModItems.DRAGONSCALE_HELMET,       ArmorSet.DRAGONSCALE);
        ITEM_TO_SET.put(ModItems.DRAGONSCALE_CHESTPLATE,   ArmorSet.DRAGONSCALE);
        ITEM_TO_SET.put(ModItems.DRAGONSCALE_LEGGINGS,     ArmorSet.DRAGONSCALE);
        ITEM_TO_SET.put(ModItems.DRAGONSCALE_BOOTS,        ArmorSet.DRAGONSCALE);

        ITEM_TO_SET.put(ModItems.INFERNO_HELMET,           ArmorSet.INFERNO);
        ITEM_TO_SET.put(ModItems.INFERNO_CHESTPLATE,       ArmorSet.INFERNO);
        ITEM_TO_SET.put(ModItems.INFERNO_LEGGINGS,         ArmorSet.INFERNO);
        ITEM_TO_SET.put(ModItems.INFERNO_BOOTS,            ArmorSet.INFERNO);

        ITEM_TO_SET.put(ModItems.CRYSTALFORGE_HELMET,      ArmorSet.CRYSTALFORGE);
        ITEM_TO_SET.put(ModItems.CRYSTALFORGE_CHESTPLATE,  ArmorSet.CRYSTALFORGE);
        ITEM_TO_SET.put(ModItems.CRYSTALFORGE_LEGGINGS,    ArmorSet.CRYSTALFORGE);
        ITEM_TO_SET.put(ModItems.CRYSTALFORGE_BOOTS,       ArmorSet.CRYSTALFORGE);

        ITEM_TO_SET.put(ModItems.NATURESGUARD_HELMET,      ArmorSet.NATURESGUARD);
        ITEM_TO_SET.put(ModItems.NATURESGUARD_CHESTPLATE,  ArmorSet.NATURESGUARD);
        ITEM_TO_SET.put(ModItems.NATURESGUARD_LEGGINGS,    ArmorSet.NATURESGUARD);
        ITEM_TO_SET.put(ModItems.NATURESGUARD_BOOTS,       ArmorSet.NATURESGUARD);

        ITEM_TO_SET.put(ModItems.SHADOW_HELMET,            ArmorSet.SHADOW);
        ITEM_TO_SET.put(ModItems.SHADOW_CHESTPLATE,        ArmorSet.SHADOW);
        ITEM_TO_SET.put(ModItems.SHADOW_LEGGINGS,          ArmorSet.SHADOW);
        ITEM_TO_SET.put(ModItems.SHADOW_BOOTS,             ArmorSet.SHADOW);

        ITEM_TO_SET.put(ModItems.FROSTBOUND_HELMET,        ArmorSet.FROSTBOUND);
        ITEM_TO_SET.put(ModItems.FROSTBOUND_CHESTPLATE,    ArmorSet.FROSTBOUND);
        ITEM_TO_SET.put(ModItems.FROSTBOUND_LEGGINGS,      ArmorSet.FROSTBOUND);
        ITEM_TO_SET.put(ModItems.FROSTBOUND_BOOTS,         ArmorSet.FROSTBOUND);

        ITEM_TO_SET.put(ModItems.SOLARWEAVE_HELMET,        ArmorSet.SOLARWEAVE);
        ITEM_TO_SET.put(ModItems.SOLARWEAVE_CHESTPLATE,    ArmorSet.SOLARWEAVE);
        ITEM_TO_SET.put(ModItems.SOLARWEAVE_LEGGINGS,      ArmorSet.SOLARWEAVE);
        ITEM_TO_SET.put(ModItems.SOLARWEAVE_BOOTS,         ArmorSet.SOLARWEAVE);

        ITEM_TO_SET.put(ModItems.STORMCALLER_HELMET,       ArmorSet.STORMCALLER);
        ITEM_TO_SET.put(ModItems.STORMCALLER_CHESTPLATE,   ArmorSet.STORMCALLER);
        ITEM_TO_SET.put(ModItems.STORMCALLER_LEGGINGS,     ArmorSet.STORMCALLER);
        ITEM_TO_SET.put(ModItems.STORMCALLER_BOOTS,        ArmorSet.STORMCALLER);

        ITEM_TO_SET.put(ModItems.OBSIDIAN_HELMET,          ArmorSet.OBSIDIAN);
        ITEM_TO_SET.put(ModItems.OBSIDIAN_CHESTPLATE,      ArmorSet.OBSIDIAN);
        ITEM_TO_SET.put(ModItems.OBSIDIAN_LEGGINGS,        ArmorSet.OBSIDIAN);
        ITEM_TO_SET.put(ModItems.OBSIDIAN_BOOTS,           ArmorSet.OBSIDIAN);
    }

    private static final Map<UUID, ArmorSetState> playerSetStates = new HashMap<>();
    private static final Map<UUID, ArmorSet>      activeSynergies = new HashMap<>();

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

    private static ArmorSet getArmorSetType(ItemStack stack) {
        if (stack.isEmpty()) return ArmorSet.NONE;
        return ITEM_TO_SET.getOrDefault(stack.getItem(), ArmorSet.NONE);
    }

    private static ArmorSetState getPlayerArmorSetState(ServerPlayer player) {
        // Fixed-size int[] indexed by ordinal instead of Map<ArmorSet,Integer> — avoids
        // HashMap allocation + autoboxing on every call (this runs once/sec per player
        // from the tick loop, plus once per ability-power/mana-cost/crit lookup).
        int[] counts = new int[ArmorSet.values().length];

        countPiece(counts, player.getItemBySlot(EquipmentSlot.HEAD));
        countPiece(counts, player.getItemBySlot(EquipmentSlot.CHEST));
        countPiece(counts, player.getItemBySlot(EquipmentSlot.LEGS));
        countPiece(counts, player.getItemBySlot(EquipmentSlot.FEET));

        ArmorSet dominant = ArmorSet.NONE;
        int max = 0;
        for (ArmorSet set : ArmorSet.values()) {
            int count = counts[set.ordinal()];
            if (count > max) { max = count; dominant = set; }
        }

        return new ArmorSetState(dominant, max);
    }

    private static void countPiece(int[] counts, ItemStack piece) {
        ArmorSet set = getArmorSetType(piece);
        if (set != ArmorSet.NONE) counts[set.ordinal()]++;
    }

    public static void applySetBonuses(ServerPlayer player) {
        ArmorSetState current = getPlayerArmorSetState(player);

        if (current.currentSet == ArmorSet.NONE || current.pieceCount == 0) {
            playerSetStates.remove(player.getUUID());
            activeSynergies.remove(player.getUUID());
            return;
        }

        ArmorSetState last = playerSetStates.get(player.getUUID());
        if (last == null || !last.equals(current)) {
            notifySetBonus(player, current);
            playerSetStates.put(player.getUUID(), current);
        }

        String cls = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

        switch (current.currentSet) {
            case MYTHRIL      -> applyMythrilBonuses(player, current.pieceCount, cls);
            case DRAGONSCALE  -> applyDragonscaleBonuses(player, current.pieceCount, cls);
            case INFERNO      -> applyInfernoBonuses(player, current.pieceCount, cls);
            case CRYSTALFORGE -> applyCrystalForgeBonuses(player, current.pieceCount, cls);
            case NATURESGUARD -> applyNaturesGuardBonuses(player, current.pieceCount, cls);
            case SHADOW       -> applyShadowBonuses(player, current.pieceCount, cls);
            case FROSTBOUND   -> applyFrostboundBonuses(player, current.pieceCount, cls);
            case SOLARWEAVE   -> applySolarweaveBonuses(player, current.pieceCount, cls);
            case STORMCALLER  -> applyStormcallerBonuses(player, current.pieceCount, cls);
            case OBSIDIAN     -> applyObsidianBonuses(player, current.pieceCount, cls);
            case FORTUNA      -> applyFortunaBonuses(player, current.pieceCount, cls);
            default           -> {}
        }

        applyWeaponSynergy(player, current, cls);
    }

    // ===== MYTHRIL =====
    // Balanced protection set — rewards full commitment with enhanced endurance.

    private static void applyMythrilBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            switch (cls) {
                case "Warrior" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 0, true, false, false));
                }
                case "Mage" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                }
                case "Rogue" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 0, true, false, false));
                }
            }
        }
    }

    // ===== DRAGONSCALE =====
    // Elite dragon set — Fire Resistance for all; class bonuses escalate into elite territory.

    private static void applyDragonscaleBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                case "Rogue"   -> {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED,   100, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.HASTE,   100, 0, true, false, false));
                }
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,    100, 1, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,  100, 0, true, false, false));
                }
                case "Mage" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,100, 1, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.HASTE,       100, 0, true, false, false));
                }
                case "Rogue" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED,       100, 1, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.HASTE,       100, 0, true, false, false));
                }
            }
        }
    }

    // ===== INFERNO =====
    // Fire-aspected set — permanent fire immunity; class bonuses amplify offence.

    private static void applyInfernoBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING,         100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 1, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 1, true, false, false));
            }
        }
    }

    // ===== CRYSTALFORGE =====
    // Arcane-infused set — Night Vision baseline; class roles enhanced.

    private static void applyCrystalForgeBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING,      100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 1, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 1, true, false, false));
            }
        }
    }

    // ===== NATURE'S GUARD =====
    // Regenerative nature set — sustain-focused with class specialisations.

    private static void applyNaturesGuardBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.LUCK,          100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 1, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION,   100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.LUCK,          100, 1, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,  100, 0, true, false, false));
            }
        }
    }

    // ===== SHADOW =====
    // Assassin set — speed and stealth; Rogue gains enhanced invisibility.

    private static void applyShadowBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,  300, 0, true, false, false));
            if ("Rogue".equals(cls)) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,  300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
                case "Mage"    -> {} // mage teleport bonus handled elsewhere
                case "Rogue"   -> {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,   100, 1, true, false, false));
                }
            }
        }
    }

    // ===== FROSTBOUND =====
    // Frost warden set — slow-fall baseline; class bonuses add survivability.

    private static void applyFrostboundBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,  300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,  300, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 1, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,  300, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            }
        }
    }

    // ===== SOLARWEAVE =====
    // Radiant sun set — fire immunity baseline; class bonuses reward active play.

    private static void applySolarweaveBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,    100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 1, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 1, true, false, false));
            }
        }
    }

    // ===== STORMCALLER =====
    // Storm striker set — speed-focused; class bonuses push offensive output.

    private static void applyStormcallerBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 1, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,      100, 1, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.HASTE,         100, 1, true, false, false));
                case "Rogue"   -> {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    100, 1, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 2, true, false, false));
                }
            }
        }
    }

    // ===== OBSIDIAN =====
    // Fortress tank set — heavy resistance; 4pc escalates into Resistance II.

    private static void applyObsidianBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 0, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 0, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 0, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 0, true, false, false));
            }
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,    100, 1, true, false, false));
            switch (cls) {
                case "Warrior" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,    100, 2, true, false, false));
                case "Mage"    -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,  100, 1, true, false, false));
                case "Rogue"   -> player.addEffect(new MobEffectInstance(MobEffects.SPEED,         100, 1, true, false, false));
            }
        }
    }

    // ===== FORTUNA =====
    // Luck-focused set — trading combat power for exceptional loot fortune.

    private static void applyFortunaBonuses(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK,  300, 2, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0, true, false, false));
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK,               300, 4, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE,300, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,              100, 1, true, false, false));
        }
    }

    // ===== WEAPON SYNERGY =====

    private static boolean hasMatchingWeapon(ServerPlayer player, ArmorSet set) {
        ItemStack main = player.getMainHandItem();
        if (main.isEmpty()) return false;
        String name = main.getHoverName().getString();
        return switch (set) {
            case DRAGONSCALE  -> name.contains("Dragonscale");
            case INFERNO      -> name.contains("Inferno");
            case SHADOW       -> name.contains("Shadow") || name.contains("Phantom");
            case NATURESGUARD -> name.contains("Poison Fang");
            case CRYSTALFORGE -> name.contains("Crystal");
            default           -> false;
        };
    }

    private static void applyWeaponSynergy(ServerPlayer player, ArmorSetState state, String cls) {
        if (state.pieceCount < 2) return;
        if (!hasMatchingWeapon(player, state.currentSet)) return;

        UUID id = player.getUUID();
        if (!activeSynergies.containsKey(id) || !activeSynergies.get(id).equals(state.currentSet)) {
            notifyWeaponSynergy(player, state.currentSet);
            activeSynergies.put(id, state.currentSet);
        }

        switch (state.currentSet) {
            case DRAGONSCALE  -> applyDragonscaleWeaponSynergy(player, state.pieceCount, cls);
            case INFERNO      -> applyInfernoWeaponSynergy(player, state.pieceCount, cls);
            case SHADOW       -> applyShadowWeaponSynergy(player, state.pieceCount, cls);
            case NATURESGUARD -> applyNaturesGuardWeaponSynergy(player, state.pieceCount, cls);
            case CRYSTALFORGE -> applyCrystalforgeWeaponSynergy(player, state.pieceCount, cls);
            default           -> {}
        }
    }

    private static void applyDragonscaleWeaponSynergy(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 100, 0, true, false));
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH,       100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,300, 0, true, false, false));
            if ("Warrior".equals(cls)) player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1, true, false, false));
        }
    }

    private static void applyInfernoWeaponSynergy(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 100, 0, true, false, false));
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.HASTE,    100, 1, true, false, false));
        }
    }

    private static void applyShadowWeaponSynergy(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 1, true, false, false));
            if ("Rogue".equals(cls)) player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, true, false, false));
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED,      100, 2, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 100, 2, true, false, false));
            if ("Rogue".equals(cls)) player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, true, false, false));
        }
    }

    private static void applyNaturesGuardWeaponSynergy(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.LUCK,         100, 0, true, false, false));
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.LUCK,         100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION,   100, 0, true, false, false));
        }
    }

    private static void applyCrystalforgeWeaponSynergy(ServerPlayer player, int pieces, String cls) {
        if (pieces >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 0, true, false, false));
        }
        if (pieces >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 2, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 1, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 100, 2, true, false, false));
        }
    }

    // ===== NOTIFICATIONS =====

    private static void notifySetBonus(ServerPlayer player, ArmorSetState state) {
        String cls = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (state.pieceCount == 2) {
            player.sendOverlayMessage(Component.literal("⚡ " + state.currentSet.getName() + " 2-Piece Bonus! ⚡")
                .withStyle(state.currentSet.getColor()));
            player.sendOverlayMessage(Component.literal(get2PieceBonusText(state.currentSet, cls))
                .withStyle(ChatFormatting.YELLOW));
        } else if (state.pieceCount == 4) {
            player.sendOverlayMessage(Component.literal("★ " + state.currentSet.getName() + " 4-Piece Bonus! ★")
                .withStyle(state.currentSet.getColor()).withStyle(ChatFormatting.BOLD));
            player.sendOverlayMessage(Component.literal(get4PieceBonusText(state.currentSet, cls))
                .withStyle(ChatFormatting.GOLD));
        }
    }

    private static void notifyWeaponSynergy(ServerPlayer player, ArmorSet set) {
        player.sendOverlayMessage(Component.literal("⚔ " + set.getName() + " Weapon Synergy Activated! ⚔")
            .withStyle(set.getColor()).withStyle(ChatFormatting.BOLD));
    }

    private static String get2PieceBonusText(ArmorSet set, String cls) {
        return switch (set) {
            case MYTHRIL      -> switch (cls) { case "Warrior" -> "Resistance I";   case "Mage" -> "Haste I";   default -> "Speed I"; };
            case DRAGONSCALE  -> "Fire Resistance + " + switch (cls) { case "Warrior" -> "Strength I"; case "Mage" -> "Regeneration I"; default -> "Speed I + Haste I"; };
            case INFERNO      -> "Fire Resistance + " + switch (cls) { case "Warrior" -> "Strength I"; case "Mage" -> "Regeneration I"; default -> "Speed I"; };
            case CRYSTALFORGE -> "Night Vision + " + switch (cls) { case "Warrior" -> "Absorption I"; case "Mage" -> "Regeneration I"; default -> "Jump Boost I"; };
            case NATURESGUARD -> "Regeneration I + " + switch (cls) { case "Warrior" -> "Resistance I"; case "Mage" -> "Luck I"; default -> "Jump Boost II"; };
            case SHADOW       -> "Warrior".equals(cls) || "Mage".equals(cls) ? "Speed I + Night Vision" : "Speed I + Night Vision + Invisibility";
            case FROSTBOUND   -> "Slow Falling + " + switch (cls) { case "Warrior" -> "Resistance I"; case "Mage" -> "Night Vision"; default -> "Jump Boost II"; };
            case SOLARWEAVE   -> "Fire Resistance + " + switch (cls) { case "Warrior" -> "Strength I"; case "Mage" -> "Haste I"; default -> "Speed I"; };
            case STORMCALLER  -> "Speed I + " + switch (cls) { case "Warrior" -> "Strength I"; case "Mage" -> "Haste I"; default -> "Jump Boost I"; };
            case OBSIDIAN     -> "Resistance I + " + switch (cls) { case "Warrior" -> "Absorption I"; case "Mage" -> "Regeneration I"; default -> "Speed I"; };
            case FORTUNA      -> "Luck III + Speed I";
            default           -> "Set Bonus Active";
        };
    }

    private static String get4PieceBonusText(ArmorSet set, String cls) {
        return switch (set) {
            case MYTHRIL      -> switch (cls) { case "Warrior" -> "Resistance I + Absorption I"; case "Mage" -> "Haste I + Regeneration I"; default -> "Speed I + Jump Boost I"; };
            case DRAGONSCALE  -> "Fire Resistance + " + switch (cls) { case "Warrior" -> "Strength II + Resistance I"; case "Mage" -> "Regeneration II + Haste I"; default -> "Speed II + Haste I"; };
            case INFERNO      -> "Fire Immunity + " + switch (cls) { case "Warrior" -> "Strength II"; case "Mage" -> "Regeneration II"; default -> "Haste II"; };
            case CRYSTALFORGE -> "Night Vision + Glowing + " + switch (cls) { case "Warrior" -> "Absorption II"; case "Mage" -> "Regeneration II"; default -> "Jump Boost II"; };
            case NATURESGUARD -> "Regeneration II + Saturation + " + switch (cls) { case "Warrior" -> "Absorption II"; case "Mage" -> "Luck II"; default -> "Invisibility"; };
            case SHADOW       -> switch (cls) { case "Warrior" -> "Speed II + Resistance I"; case "Mage" -> "Speed II"; default -> "Speed II + Invisibility + Jump Boost II"; };
            case FROSTBOUND   -> "Slow Falling + " + switch (cls) { case "Warrior" -> "Resistance II"; case "Mage" -> "Resistance I + Regeneration I"; default -> "Resistance I + Speed I"; };
            case SOLARWEAVE   -> "Fire Resistance + Regeneration I + " + switch (cls) { case "Warrior" -> "Absorption II"; case "Mage" -> "Haste II"; default -> "Speed II"; };
            case STORMCALLER  -> switch (cls) { case "Warrior" -> "Speed II + Strength II"; case "Mage" -> "Speed II + Haste II"; default -> "Speed III + Jump Boost II"; };
            case OBSIDIAN     -> switch (cls) { case "Warrior" -> "Resistance II + Absorption III"; case "Mage" -> "Resistance II + Regeneration II"; default -> "Resistance II + Speed II"; };
            case FORTUNA      -> "Luck V + Hero of the Village + Speed II";
            default           -> "Full Set Bonus";
        };
    }

    // ===== INTEGRATION GETTERS =====

    public static float getManaCostReduction(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!"Mage".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) return 0f;
        return switch (state.currentSet) {
            case DRAGONSCALE  -> state.pieceCount >= 2 ? 0.15f : 0f;
            case CRYSTALFORGE -> state.pieceCount >= 4 ? 0.20f : 0f;
            case INFERNO      -> state.pieceCount >= 4 ? 0.10f : 0f;
            case SHADOW       -> state.pieceCount >= 2 ? 0.10f : 0f;
            default           -> 0f;
        };
    }

    public static float getManaRegenBonus(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!"Mage".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) return 0f;
        return switch (state.currentSet) {
            case DRAGONSCALE  -> state.pieceCount >= 4 ? 0.50f : 0f;
            case CRYSTALFORGE -> state.pieceCount >= 2 ? 0.25f : 0f;
            case NATURESGUARD -> state.pieceCount >= 4 ? 0.30f : 0f;
            default           -> 0f;
        };
    }

    public static float getCritBonus(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!"Rogue".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) return 0f;
        return switch (state.currentSet) {
            case DRAGONSCALE  -> state.pieceCount >= 2 ? 0.20f : 0f;
            case SHADOW       -> state.pieceCount >= 2 ? 0.25f : 0f;
            case NATURESGUARD -> state.pieceCount >= 4 ? 0.15f : 0f;
            default           -> 0f;
        };
    }

    public static float getEnergyRegenBonus(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!"Rogue".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) return 0f;
        return switch (state.currentSet) {
            case DRAGONSCALE  -> state.pieceCount >= 4 ? 0.25f : 0f;
            case SHADOW       -> state.pieceCount >= 4 ? 0.35f : 0f;
            case CRYSTALFORGE -> state.pieceCount >= 2 ? 0.15f : 0f;
            default           -> 0f;
        };
    }

    public static float getWeaponSynergyBackstabBonus(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!"Rogue".equals(ClassSelectionAltarBlock.getPlayerClass(player.getUUID()))) return 0f;
        if (!hasMatchingWeapon(player, state.currentSet)) return 0f;
        return switch (state.currentSet) {
            case SHADOW      -> state.pieceCount >= 4 ? 0.75f : state.pieceCount >= 2 ? 0.35f : 0f;
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.25f : 0f;
            default          -> 0f;
        };
    }

    public static float getWeaponSynergyDamageBonus(ServerPlayer player) {
        ArmorSetState state = getPlayerArmorSetState(player);
        if (!hasMatchingWeapon(player, state.currentSet)) return 0f;
        return switch (state.currentSet) {
            case DRAGONSCALE -> state.pieceCount >= 4 ? 0.30f : state.pieceCount >= 2 ? 0.15f : 0f;
            case INFERNO     -> state.pieceCount >= 4 ? 0.25f : state.pieceCount >= 2 ? 0.15f : 0f;
            default          -> 0f;
        };
    }
}
