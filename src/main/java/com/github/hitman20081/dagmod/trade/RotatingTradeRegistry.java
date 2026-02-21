package com.github.hitman20081.dagmod.trade;

import com.github.hitman20081.dagmod.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;

import java.util.*;

/**
 * Registry of rotating trade pools for each merchant type.
 * Each merchant has multiple rotation sets that cycle every 72 hours.
 */
public class RotatingTradeRegistry {

    // Map of merchant type -> list of rotation sets (each set is a list of trades)
    private static final Map<MerchantType, List<List<TradeOffer>>> ROTATING_TRADES = new EnumMap<>(MerchantType.class);

    static {
        initializeArmorerTrades();
        initializeMysteryMerchantTrades();
        initializeEnchantsmithTrades();
        initializeVoodooIllusionerTrades();
        initializeTrophyDealerTrades();
        initializeMinerTrades();
        initializeHunterTrades();
        initializeLumberjackTrades();
    }

    // ==================== ARMORER TRADES ====================
    private static void initializeArmorerTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Dragonscale Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 12),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 5)),
                        new ItemStack(ModItems.DRAGONSCALE_HELMET),
                        1, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 8)),
                        new ItemStack(ModItems.DRAGONSCALE_CHESTPLATE),
                        1, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 14),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 7)),
                        new ItemStack(ModItems.DRAGONSCALE_LEGGINGS),
                        1, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 10),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 4)),
                        new ItemStack(ModItems.DRAGONSCALE_BOOTS),
                        1, 20, 0.05F
                )
        ));

        // Rotation 1: Inferno Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 10),
                        Optional.of(new TradedItem(Items.BLAZE_ROD, 6)),
                        new ItemStack(ModItems.INFERNO_HELMET),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 14),
                        Optional.of(new TradedItem(Items.BLAZE_ROD, 10)),
                        new ItemStack(ModItems.INFERNO_CHESTPLATE),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 12),
                        Optional.of(new TradedItem(Items.BLAZE_ROD, 8)),
                        new ItemStack(ModItems.INFERNO_LEGGINGS),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.BLAZE_ROD, 5)),
                        new ItemStack(ModItems.INFERNO_BOOTS),
                        1, 18, 0.05F
                )
        ));

        // Rotation 2: Crystalforge Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 10),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 16)),
                        new ItemStack(ModItems.CRYSTALFORGE_HELMET),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 14),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 24)),
                        new ItemStack(ModItems.CRYSTALFORGE_CHESTPLATE),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 12),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 20)),
                        new ItemStack(ModItems.CRYSTALFORGE_LEGGINGS),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 12)),
                        new ItemStack(ModItems.CRYSTALFORGE_BOOTS),
                        1, 18, 0.05F
                )
        ));

        // Rotation 3: Nature's Guard Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 24),
                        Optional.of(new TradedItem(Items.OAK_LOG, 16)),
                        new ItemStack(ModItems.NATURESGUARD_HELMET),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 36),
                        Optional.of(new TradedItem(Items.OAK_LOG, 24)),
                        new ItemStack(ModItems.NATURESGUARD_CHESTPLATE),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 32),
                        Optional.of(new TradedItem(Items.OAK_LOG, 20)),
                        new ItemStack(ModItems.NATURESGUARD_LEGGINGS),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 20),
                        Optional.of(new TradedItem(Items.OAK_LOG, 12)),
                        new ItemStack(ModItems.NATURESGUARD_BOOTS),
                        1, 15, 0.05F
                )
        ));

        // Rotation 4: Shadow Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 32),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                        new ItemStack(ModItems.SHADOW_HELMET),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 48),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                        new ItemStack(ModItems.SHADOW_CHESTPLATE),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 44),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 5)),
                        new ItemStack(ModItems.SHADOW_LEGGINGS),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 28),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 3)),
                        new ItemStack(ModItems.SHADOW_BOOTS),
                        1, 15, 0.05F
                )
        ));

        // Rotation 5: Frostbound Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 8)),
                        new ItemStack(ModItems.FROSTBOUND_HELMET),
                        1, 16, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 12),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 12)),
                        new ItemStack(ModItems.FROSTBOUND_CHESTPLATE),
                        1, 16, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 10),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 10)),
                        new ItemStack(ModItems.FROSTBOUND_LEGGINGS),
                        1, 16, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 6),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 6)),
                        new ItemStack(ModItems.FROSTBOUND_BOOTS),
                        1, 16, 0.05F
                )
        ));

        // Rotation 6: Solarweave Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 28),
                        Optional.of(new TradedItem(Items.SUNFLOWER, 8)),
                        new ItemStack(ModItems.SOLARWEAVE_HELMET),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 40),
                        Optional.of(new TradedItem(Items.SUNFLOWER, 12)),
                        new ItemStack(ModItems.SOLARWEAVE_CHESTPLATE),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 36),
                        Optional.of(new TradedItem(Items.SUNFLOWER, 10)),
                        new ItemStack(ModItems.SOLARWEAVE_LEGGINGS),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 24),
                        Optional.of(new TradedItem(Items.SUNFLOWER, 6)),
                        new ItemStack(ModItems.SOLARWEAVE_BOOTS),
                        1, 15, 0.05F
                )
        ));

        // Rotation 7: Stormcaller Armor
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 10),
                        Optional.of(new TradedItem(Items.LIGHTNING_ROD, 2)),
                        new ItemStack(ModItems.STORMCALLER_HELMET),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 14),
                        Optional.of(new TradedItem(Items.LIGHTNING_ROD, 3)),
                        new ItemStack(ModItems.STORMCALLER_CHESTPLATE),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 12),
                        Optional.of(new TradedItem(Items.LIGHTNING_ROD, 2)),
                        new ItemStack(ModItems.STORMCALLER_LEGGINGS),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.LIGHTNING_ROD, 1)),
                        new ItemStack(ModItems.STORMCALLER_BOOTS),
                        1, 18, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.ARMORER, rotations);
    }

    // ==================== MYSTERY MERCHANT TRADES ====================
    private static void initializeMysteryMerchantTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Shadow Weapons
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 40),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                        new ItemStack(ModItems.SHADOWFANG_DAGGER),
                        1, 15, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 52),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                        new ItemStack(ModItems.SHADOWFANG_SWORD),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 40),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                        new ItemStack(ModItems.SHADOW_SHIELD),
                        1, 15, 0.05F
                )
        ));

        // Rotation 1: Dragon Weapons
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 48),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 2)),
                        new ItemStack(ModItems.DRAGONSCALE_SWORD),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(ModItems.DRAGON_BONE, 4)),
                        new ItemStack(ModItems.DRAGONBONE_SHIELD),
                        1, 22, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 20),
                        Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 4)),
                        new ItemStack(ModItems.DRAGONSCALE_SWORD),
                        1, 22, 0.05F
                )
        ));

        // Rotation 2: Elemental Weapons
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 48),
                        Optional.of(new TradedItem(Items.BLAZE_ROD, 8)),
                        new ItemStack(ModItems.INFERNO_SWORD),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 56),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 16)),
                        new ItemStack(ModItems.FROSTBITE_AXE),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 20),
                        Optional.of(new TradedItem(Items.TRIDENT, 1)),
                        new ItemStack(ModItems.THUNDER_PIKE),
                        1, 22, 0.05F
                )
        ));

        // Rotation 3: Epic Weapons
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 24),
                        Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                        new ItemStack(ModItems.ETHEREAL_BLADE),
                        1, 25, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 28),
                        Optional.of(new TradedItem(ModItems.SILMARIL, 1)),
                        new ItemStack(ModItems.CRYSTALHAMMER),
                        1, 28, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(Items.GHAST_TEAR, 4)),
                        new ItemStack(ModItems.BLOODTHIRSTER_BLADE),
                        1, 20, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.MYSTERY_MERCHANT, rotations);
    }

    // ==================== ENCHANTSMITH TRADES ====================
    private static void initializeEnchantsmithTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Combat Enchants
        rotations.add(Arrays.asList(
                createEnchantedBookPlaceholder("Protection IV", 32, 1, 25),
                createEnchantedBookPlaceholder("Sharpness V", 36, 1, 28),
                createEnchantedBookPlaceholder("Mending", 40, 1, 30)
        ));

        // Rotation 1: Utility Enchants
        rotations.add(Arrays.asList(
                createEnchantedBookPlaceholder("Unbreaking III", 28, 2, 22),
                createEnchantedBookPlaceholder("Efficiency V", 32, 1, 25),
                createEnchantedBookPlaceholder("Fortune III", 36, 1, 28)
        ));

        // Rotation 2: Bow Enchants
        rotations.add(Arrays.asList(
                createEnchantedBookPlaceholder("Power V", 32, 1, 25),
                createEnchantedBookPlaceholder("Infinity", 36, 1, 28),
                createEnchantedBookPlaceholder("Flame", 24, 2, 20)
        ));

        // Rotation 3: Special Enchants
        rotations.add(Arrays.asList(
                createEnchantedBookPlaceholder("Looting III", 36, 1, 28),
                createEnchantedBookPlaceholder("Silk Touch", 28, 2, 22),
                createEnchantedBookPlaceholder("Feather Falling IV", 24, 2, 20)
        ));

        ROTATING_TRADES.put(MerchantType.ENCHANTSMITH, rotations);
    }

    // Helper to create enchanted book placeholder (actual enchantment applied at runtime)
    private static TradeOffer createEnchantedBookPlaceholder(String name, int emeraldCost, int maxUses, int xp) {
        return new TradeOffer(
                new TradedItem(Items.EMERALD, emeraldCost),
                Optional.of(new TradedItem(Items.BOOK, 1)),
                new ItemStack(Items.ENCHANTED_BOOK),
                maxUses, xp, 0.05F
        );
    }

    // ==================== VOODOO ILLUSIONER TRADES ====================
    private static void initializeVoodooIllusionerTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Rebirth Potions
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 24),
                        Optional.of(new TradedItem(Items.GHAST_TEAR, 2)),
                        new ItemStack(ModItems.POTION_OF_RACIAL_REBIRTH),
                        1, 25, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 24),
                        Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                        new ItemStack(ModItems.POTION_OF_CLASS_REBIRTH),
                        1, 25, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                        new ItemStack(ModItems.POTION_OF_TOTAL_REBIRTH),
                        1, 30, 0.05F
                )
        ));

        // Rotation 1: Reset Crystals
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 32),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 16)),
                        new ItemStack(ModItems.RACE_RESET_CRYSTAL),
                        2, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                        new ItemStack(ModItems.CLASS_RESET_CRYSTAL),
                        1, 25, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(ModItems.DRAGON_HEART, 1)),
                        new ItemStack(ModItems.CHARACTER_RESET_CRYSTAL),
                        1, 30, 0.05F
                )
        ));

        // Rotation 2: Shadow Weapons
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 28),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                        new ItemStack(ModItems.SHADOWFANG_DAGGER),
                        1, 18, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 36),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                        new ItemStack(ModItems.SHADOWFANG_SWORD),
                        1, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 32),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                        new ItemStack(ModItems.SHADOW_SHIELD),
                        1, 18, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.VOODOO_ILLUSIONER, rotations);
    }

    // ==================== TROPHY DEALER TRADES ====================
    private static void initializeTrophyDealerTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Dragon Trophies
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 32),
                        Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                        new ItemStack(ModItems.DRAGON_HEART),
                        1, 50, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 64),
                        Optional.of(new TradedItem(ModItems.DRAGON_HEART, 2)),
                        new ItemStack(Items.DRAGON_EGG),
                        1, 100, 0.05F
                )
        ));

        // Rotation 1: Wither Trophies
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 24),
                        Optional.of(new TradedItem(Items.SOUL_SAND, 16)),
                        new ItemStack(Items.WITHER_SKELETON_SKULL),
                        2, 35, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 48),
                        Optional.of(new TradedItem(Items.WITHER_SKELETON_SKULL, 3)),
                        new ItemStack(Items.NETHER_STAR),
                        1, 60, 0.05F
                )
        ));

        // Rotation 2: End Treasures
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 32),
                        Optional.of(new TradedItem(Items.PHANTOM_MEMBRANE, 16)),
                        new ItemStack(Items.ELYTRA),
                        1, 50, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 8),
                        Optional.of(new TradedItem(Items.CHORUS_FRUIT, 8)),
                        new ItemStack(Items.SHULKER_SHELL, 2),
                        3, 20, 0.05F
                )
        ));

        // Rotation 3: Mod Boss Trophies
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 24),
                        Optional.of(new TradedItem(Items.AMETHYST_SHARD, 32)),
                        new ItemStack(ModItems.SILMARIL),
                        1, 40, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.DIAMOND, 16),
                        Optional.of(new TradedItem(Items.NETHERITE_SCRAP, 4)),
                        new ItemStack(ModItems.KINGS_SCALE),
                        1, 35, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.TROPHY_DEALER, rotations);
    }

    // ==================== MINER TRADES ====================
    private static void initializeMinerTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Raw Gems Set 1 (Ruby & Sapphire)
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 6),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_RUBY, 4),
                        4, 10, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 6),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_SAPPHIRE, 4),
                        4, 10, 0.05F
                )
        ));

        // Rotation 1: Raw Gems Set 2 (Citrine & Tanzanite)
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 5),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_CITRINE, 4),
                        4, 8, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 7),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_TANZANITE, 4),
                        4, 12, 0.05F
                )
        ));

        // Rotation 2: Raw Gems Set 3 (Topaz, Zircon & Pink Garnet)
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 5),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_TOPAZ, 4),
                        4, 8, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 6),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_ZIRCON, 4),
                        4, 10, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 6),
                        Optional.empty(),
                        new ItemStack(ModItems.RAW_PINK_GARNET, 4),
                        4, 10, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.MINER, rotations);
    }

    // ==================== HUNTER TRADES ====================
    private static void initializeHunterTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Premium Horse Gear
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 16),
                        Optional.of(new TradedItem(Items.DIAMOND, 2)),
                        new ItemStack(Items.DIAMOND_HORSE_ARMOR),
                        2, 20, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 24),
                        Optional.of(new TradedItem(Items.GOLD_INGOT, 8)),
                        new ItemStack(Items.GOLDEN_HORSE_ARMOR),
                        2, 18, 0.05F
                )
        ));

        // Rotation 1: Tracking Items
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 20),
                        Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                        new ItemStack(Items.RECOVERY_COMPASS),
                        1, 25, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 12),
                        Optional.of(new TradedItem(Items.ENDER_PEARL, 4)),
                        new ItemStack(Items.ENDER_EYE, 8),
                        3, 15, 0.05F
                )
        ));

        // Rotation 2: Exotic Arrows
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 6),
                        Optional.of(new TradedItem(Items.GLOWSTONE_DUST, 8)),
                        new ItemStack(Items.SPECTRAL_ARROW, 16),
                        4, 10, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 12),
                        Optional.empty(),
                        new ItemStack(Items.NAME_TAG, 2),
                        3, 15, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.HUNTER, rotations);
    }

    // ==================== LUMBERJACK TRADES ====================
    private static void initializeLumberjackTrades() {
        List<List<TradeOffer>> rotations = new ArrayList<>();

        // Rotation 0: Special Axes
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 36),
                        Optional.of(new TradedItem(Items.BLUE_ICE, 12)),
                        new ItemStack(ModItems.FROSTBITE_AXE),
                        1, 22, 0.05F
                )
        ));

        // Rotation 1: Rare Saplings
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 4),
                        Optional.empty(),
                        new ItemStack(Items.CHERRY_SAPLING, 4),
                        4, 8, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 4),
                        Optional.empty(),
                        new ItemStack(Items.DARK_OAK_SAPLING, 8),
                        4, 8, 0.05F
                )
        ));

        // Rotation 2: Nether Wood
        rotations.add(Arrays.asList(
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 4),
                        Optional.empty(),
                        new ItemStack(Items.CRIMSON_STEM, 32),
                        4, 10, 0.05F
                ),
                new TradeOffer(
                        new TradedItem(Items.EMERALD, 4),
                        Optional.empty(),
                        new ItemStack(Items.WARPED_STEM, 32),
                        4, 10, 0.05F
                )
        ));

        ROTATING_TRADES.put(MerchantType.LUMBERJACK, rotations);
    }

    // ==================== PUBLIC API ====================

    /**
     * Get the number of rotation sets for a merchant type.
     */
    public static int getRotationCount(MerchantType type) {
        List<List<TradeOffer>> rotations = ROTATING_TRADES.get(type);
        return rotations != null ? rotations.size() : 0;
    }

    /**
     * Get the current rotating trades for a merchant type.
     *
     * @param type The merchant type
     * @param rotationIndex The current rotation index
     * @return List of trade offers for the current rotation, or empty list if none
     */
    public static List<TradeOffer> getRotatingTrades(MerchantType type, int rotationIndex) {
        List<List<TradeOffer>> rotations = ROTATING_TRADES.get(type);
        if (rotations == null || rotations.isEmpty()) {
            return Collections.emptyList();
        }

        // Ensure index is within bounds
        int safeIndex = rotationIndex % rotations.size();
        return new ArrayList<>(rotations.get(safeIndex));
    }

    /**
     * Get a description of the current rotation for a merchant.
     * Used for merchant dialogue.
     */
    public static String getRotationDescription(MerchantType type, int rotationIndex) {
        switch (type) {
            case ARMORER:
                String[] armorerDescs = {"Dragonscale", "Inferno", "Crystalforge", "Nature's Guard",
                        "Shadow", "Frostbound", "Solarweave", "Stormcaller"};
                return armorerDescs[rotationIndex % armorerDescs.length] + " armor";
            case MYSTERY_MERCHANT:
                String[] mysteryDescs = {"Shadow weapons", "Dragon weapons", "Elemental weapons", "Epic weapons"};
                return mysteryDescs[rotationIndex % mysteryDescs.length];
            case ENCHANTSMITH:
                String[] enchantDescs = {"Combat enchants", "Utility enchants", "Bow enchants", "Special enchants"};
                return enchantDescs[rotationIndex % enchantDescs.length];
            case VOODOO_ILLUSIONER:
                String[] voodooDescs = {"Rebirth potions", "Reset crystals", "Shadow weapons"};
                return voodooDescs[rotationIndex % voodooDescs.length];
            case TROPHY_DEALER:
                String[] trophyDescs = {"Dragon trophies", "Wither trophies", "End treasures", "Boss trophies"};
                return trophyDescs[rotationIndex % trophyDescs.length];
            case MINER:
                String[] minerDescs = {"Raw Ruby & Sapphire", "Raw Citrine & Tanzanite", "Raw Topaz, Zircon & Pink Garnet"};
                return minerDescs[rotationIndex % minerDescs.length];
            case HUNTER:
                String[] hunterDescs = {"Premium horse gear", "Tracking items", "Exotic arrows"};
                return hunterDescs[rotationIndex % hunterDescs.length];
            case LUMBERJACK:
                String[] lumberDescs = {"Frostbite Axe", "Rare saplings", "Nether wood"};
                return lumberDescs[rotationIndex % lumberDescs.length];
            default:
                return "special items";
        }
    }
}
