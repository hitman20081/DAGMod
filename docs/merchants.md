# Merchants

DAGMod features **13 unique merchant NPCs**, each specializing in different types of gear and supplies. Merchants offer both permanent static trades and some feature rotating premium inventories that cycle on a real-world timer.

**Current Version:** v1.7.1

---

## Merchant System Overview

### How Merchants Work

- **13 Merchant NPCs** found in the Hall of Champions and NPC villages
- **Static Trades** are always available for building currency and basic gear
- **Premium Rotating Trades** cycle every **72 hours** (real-world time, configurable 12-168h) on select merchants
- Rotation state persists across server restarts
- Some merchants are static-only (no rotating stock)

### Merchant Dialogue

When you open a merchant's trade screen, they greet you with unique dialogue:

- Each merchant has **4 unique greeting lines** that play randomly
- **30% chance** to receive a **stock hint** about their current rotating inventory (for merchants with rotations)
- Hints tell you what premium collection is currently available
- Dialogue appears in chat as `<Merchant Name> message`

---

## Merchant NPCs

### Armorer

**Specialty:** Armor sets and protective gear

**Static Trades:** Basic armor pieces, shields, and protective materials

**Rotating Premium Stock (8 sets):**
- Dragonscale armor collection
- Inferno armor collection
- Crystalforge armor collection
- Nature's Guard armor collection
- Shadow armor collection
- Frostbound armor collection
- Solarweave armor collection
- Stormcaller armor collection

**Dialogue Style:** Professional craftsman, proud of quality

---

### Mystery Merchant

**Specialty:** Rare and exotic weapons

**Static Trades:** Standard weapons and basic combat supplies

**Rotating Premium Stock (4 sets):**
- Shadow weapon collection
- Dragon weapon collection
- Elemental weapon collection
- Epic weapon collection

**Dialogue Style:** Mysterious, secretive, hints at hidden power

---

### Enchantsmith

**Specialty:** Enchanted books and magical gear

**Static Trades:** Basic enchanted books and enchanting materials

**Rotating Premium Stock (4 sets):**
- Combat enchantment books
- Utility enchantment books
- Bow enchantment books
- Special enchantment books

**Dialogue Style:** Scholarly, knowledgeable about arcane arts

---

### Voodoo Illusioner

**Specialty:** Dark magic items, resets, and shadow gear

**Static Trades:**
- Dark ingredients (ender pearls, echo shards, wither roses, skeleton skulls)
- Rebirth potions (Racial Rebirth, Class Rebirth, Total Rebirth)
- Reset crystals (Race Reset, Class Reset, Character Reset)
- Rogue items (Void Blade, Vanish Cloak, Poison Vial, Assassin's Mark, Rogue Ability Tome)
- Shadow weapons (Shadowfang Dagger, Shadowfang Sword, Shadow Shield)
- Mystical consumables (Vampire Dust, Phantom Dust, Shadow Blend, Last Stand Powder, Time Distortion, Perfect Dodge)
- Echo Dust
- Buys bones, spider eyes, rotten flesh, and phantom membrane from players

**Rotating Premium Stock (3 sets):**
- Rebirth potions
- Reset crystals (class/race resets)
- Shadow weapons

**Dialogue Style:** Ominous, speaks of spirits and dark secrets

---

### Luxury Merchant (Trophy Dealer)

**Specialty:** Boss trophies and rare collectibles

**Static Trades:** Basic trophies and decorative items

**Rotating Premium Stock (4 sets):**
- Dragon boss trophies
- Wither boss trophies
- End boss trophies
- Mod boss trophies

**Dialogue Style:** Enthusiastic collector, celebrates heroic feats

---

### Miner

**Specialty:** Ores, raw gems, and mining supplies

**Static Trades:**
- Pickaxes (iron, diamond, mythril)
- Mining supplies (torches, ladders, rails, powered rails, minecarts, TNT)
- Common ores & materials (coal, iron, copper, gold, redstone, lapis, diamond)
- Raw ores for smelting (raw iron, raw gold, raw copper)
- Mod materials (raw mythril, mythril ingot)
- Raw gems (ruby, sapphire, citrine, tanzanite, topaz, zircon, pink garnet)
- Buys coal, iron, and raw rubies from players

**Rotating Premium Stock (3 sets):**
- Raw Ruby & Sapphire
- Raw Citrine & Tanzanite
- Raw Topaz, Zircon & Pink Garnet

**Dialogue Style:** Friendly dwarf-like, passionate about the depths

---

### Hunter

**Specialty:** Ranged weapons, leather goods, and tracking gear

**Static Trades:** Bows, arrows, leather armor, and basic hunting supplies

**Rotating Premium Stock (3 sets):**
- Premium horse armor
- Tracking equipment
- Exotic arrows

**Dialogue Style:** Ranger-like, speaks of the hunt and tracking prey

---

### Lumberjack

**Specialty:** Axes, wood, saplings, and nature materials

**Static Trades:** Basic wood types, saplings, and wood tools

**Rotating Premium Stock (3 sets):**
- Frostbite Axe
- Rare saplings (Cherry, Dark Oak)
- Nether wood varieties (Crimson, Warped)

**Dialogue Style:** Friendly woodsman, respects nature's bounty

---

### Baker

**Specialty:** Food and baked goods

**Static Trades:**
- Bread (3 emeralds for 8)
- Cookies (4 emeralds for 16)
- Pumpkin Pie (5 emeralds for 4)
- Cake (8 emeralds for 1)
- Golden Apples (12 emeralds for 2)
- Golden Carrots (6 emeralds for 8)
- Cooked Beef (3 emeralds for 8)
- Cooked Porkchop (3 emeralds for 8)

**Rotating Premium Stock:** None (static only)

**Dialogue Style:** Warm and welcoming, passionate about cooking

**Summon:** `/summon dagmod:baker_npc`

---

### Blacksmith

**Specialty:** Ore buying and repair materials

**Static Trades - Sells Repair Materials:**
- Anvil (8 emeralds + 3 iron ingots)
- Iron Ingots (2 emeralds for 4)
- Diamonds (12 emeralds for 1)
- Mythril Ingots (16 emeralds + 4 iron ingots for 1)

**Static Trades - Buys Raw Ores from Players:**
- Vanilla ores: Raw Iron, Raw Gold, Raw Copper, Coal, Redstone, Lapis Lazuli, Diamond, Nether Quartz, Amethyst Shard
- Mod ores: Raw Mythril, Raw Ruby, Raw Sapphire, Raw Citrine, Raw Tanzanite, Raw Topaz, Raw Zircon, Raw Pink Garnet

**Rotating Premium Stock:** None (static only)

**Dialogue Style:** Gruff and direct, values hard work

**Summon:** `/summon dagmod:blacksmith_npc`

---

### Jeweler

**Specialty:** Processed gems and gem tools

**Static Trades - Buys Processed Gems from Players:**
- Ruby, Sapphire, Citrine, Tanzanite, Topaz, Zircon, Pink Garnet (emeralds paid per gem)
- Diamond and Amethyst Shard (emeralds paid per gem)

**Static Trades - Sells Gem Products:**
- Gem Cutter Tool (16 emeralds + 2 iron ingots)
- Citrine Powder (8 emeralds for 4)
- Silmaril (48 emeralds + 4 diamonds - premium item)

**Rotating Premium Stock:** None (static only)

**Dialogue Style:** Refined and appreciative of beauty

**Summon:** `/summon dagmod:jeweler_npc`

---

### Alchemist

**Specialty:** Brewing equipment and potion ingredients

**Static Trades:**
- **Equipment:** Brewing Stand, Cauldron, Glass Bottles
- **Essential Ingredients:** Blaze Rods, Blaze Powder, Nether Wart
- **Potion Modifiers:** Redstone Dust, Glowstone Dust, Gunpowder, Dragon's Breath
- **Effect Ingredients:** Sugar, Glistering Melon Slice, Spider Eye, Fermented Spider Eye, Magma Cream, Ghast Tear, Rabbit's Foot, Phantom Membrane, Golden Carrot, Pufferfish, Turtle Scute

**Rotating Premium Stock:** None (static only)

**Dialogue Style:** Eccentric and knowledgeable, excited about experiments

**Summon:** `/summon dagmod:alchemist_npc`

---

### Village Merchant (General Store)

**Specialty:** Everyday supplies and general goods

**Static Trades:**
- **Lighting:** Torches, Lanterns, Campfires
- **Home Supplies:** Beds, Glass, Chests, Crafting Tables
- **Tools:** Buckets, Compass, Clock, Spyglass
- **Travel:** Maps, Leads, Saddles, Name Tags
- **Farming:** Bone Meal, Hay Bales
- **Decorations:** Paintings, Flower Pots

**Rotating Premium Stock:** None (static only)

**Dialogue Style:** Friendly shopkeeper, welcoming to all

**Summon:** `/summon dagmod:village_merchant_npc`

---

## Merchant Summary Table

| Merchant | Specialty | Static Trades | Rotating Sets | Trade Sound |
|----------|-----------|:------------:|:-------------:|-------------|
| Armorer | Armor & protection | Yes | 8 | Anvil |
| Mystery Merchant | Rare weapons | Yes | 4 | Wandering Trader |
| Enchantsmith | Enchanted books | Yes | 4 | Enchanting |
| Voodoo Illusioner | Dark magic & resets | Yes | 3 | Illusioner Spell |
| Luxury Merchant | Boss trophies | Yes | 4 | Trading |
| Miner | Ores & mining | Yes | 3 | Anvil |
| Hunter | Ranged & tracking | Yes | 3 | Trading |
| Lumberjack | Wood & axes | Yes | 3 | Trading |
| Baker | Food & baked goods | Yes | - | Campfire |
| Blacksmith | Ore buying & repair | Yes | - | Anvil |
| Jeweler | Gems & gem tools | Yes | - | Amethyst Chime |
| Alchemist | Brewing & potions | Yes | - | Brewing Stand |
| Village Merchant | General store | Yes | - | Trading |

---

## Rotation System Details

### Timing

| Setting | Value |
|---------|-------|
| Default rotation cycle | 72 hours (real-world time) |
| Minimum configurable | 12 hours |
| Maximum configurable | 168 hours (1 week) |
| Persistence | Saved to world data, survives restarts |

### How Rotations Work

1. Each merchant independently tracks their own rotation index
2. When the timer expires, the index advances to the next premium set
3. The cycle wraps around (e.g., set 8 returns to set 1)
4. All players see the same rotation at the same time
5. Static trades are unaffected by rotation
6. Not all merchants have rotating stock (Baker, Blacksmith, Jeweler, Alchemist, Village Merchant are static-only)

### Tips

- Check merchants regularly to catch premium stock you need
- Listen for **stock hints** (30% chance per visit) to learn what's currently available
- Plan purchases around rotation timing for rare items
- Premium items may not be available again for days depending on the number of rotation sets
- Use the Blacksmith to sell raw ores for emeralds, then spend emeralds at other merchants
- The Alchemist is your one-stop shop for all brewing ingredients

---

## Related Guides

- [Items Overview](./items-overview.md) - Complete item catalog
- [Bosses & Dungeons](./bosses_dungeons.md) - Boss loot that complements merchant gear
- [Getting Started](./getting_started.md) - Finding the Hall of Champions
