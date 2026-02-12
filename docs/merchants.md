# Merchants

DAGMod features 8 unique merchant NPCs, each specializing in different types of gear and supplies. Merchants offer both permanent static trades and rotating premium inventories that cycle on a real-world timer.

**Current Version:** v1.6.5-beta

---

## Merchant System Overview

### How Merchants Work

- **8 Merchant NPCs** found in the Hall of Champions and NPC villages
- **Static Trades** are always available for building currency and basic gear
- **Premium Rotating Trades** cycle every **72 hours** (real-world time, configurable 12-168h)
- Each merchant has **3-8 unique rotation sets** of premium items
- Rotation state persists across server restarts

### Merchant Dialogue

When you open a merchant's trade screen, they greet you with unique dialogue:

- Each merchant has **4 unique greeting lines** that play randomly
- **30% chance** to receive a **stock hint** about their current rotating inventory
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
- 5 additional premium armor sets

**Dialogue Style:** Professional craftsman, proud of quality

---

### Mystery Merchant

**Specialty:** Rare and exotic weapons

**Static Trades:** Standard weapons and basic combat supplies

**Rotating Premium Stock:**
- Shadow weapon collection
- Dragon weapon collection
- Elemental weapon collection
- Epic weapon collection

**Dialogue Style:** Mysterious, secretive, hints at hidden power

---

### Enchantsmith

**Specialty:** Enchanted books and magical gear

**Static Trades:** Basic enchanted books and enchanting materials

**Rotating Premium Stock:**
- Combat enchantment books
- Utility enchantment books
- Bow enchantment books
- Special enchantment books

**Dialogue Style:** Scholarly, knowledgeable about arcane arts

---

### Voodoo Illusioner

**Specialty:** Dark magic items, resets, and shadow gear

**Static Trades:** Basic potions and magical consumables

**Rotating Premium Stock:**
- Rebirth potions
- Reset crystals (class/race resets)
- Shadow weapons

**Dialogue Style:** Ominous, speaks of spirits and dark secrets

---

### Trophy Dealer

**Specialty:** Boss trophies and rare collectibles

**Static Trades:** Basic trophies and decorative items

**Rotating Premium Stock:**
- Dragon boss trophies
- Wither boss trophies
- End boss trophies

**Dialogue Style:** Enthusiastic collector, celebrates heroic feats

---

### Miner

**Specialty:** Ores, gems, and mining supplies

**Static Trades:** Basic ores, tools, and mining materials

**Rotating Premium Stock:**
- Rare processed gem collections

**Dialogue Style:** Friendly dwarf-like, passionate about the depths

---

### Hunter

**Specialty:** Ranged weapons, leather goods, and tracking gear

**Static Trades:** Bows, arrows, leather armor, and basic hunting supplies

**Rotating Premium Stock:**
- Premium horse armor
- Tracking equipment

**Dialogue Style:** Ranger-like, speaks of the hunt and tracking prey

---

### Lumberjack

**Specialty:** Axes, wood, saplings, and nature materials

**Static Trades:** Basic wood types, saplings, and wood tools

**Rotating Premium Stock:**
- Frostbite Axe
- Rare saplings
- Nether wood varieties

**Dialogue Style:** Friendly woodsman, respects nature's bounty

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

### Tips

- Check merchants regularly to catch premium stock you need
- Listen for **stock hints** (30% chance per visit) to learn what's currently available
- Plan purchases around rotation timing for rare items
- Premium items may not be available again for days depending on the number of rotation sets

---

## Related Guides

- [Items Overview](./items/overview.md) - Complete item catalog
- [Bosses & Dungeons](./bosses_dungeons.md) - Boss loot that complements merchant gear
- [Getting Started](./getting_started.md) - Finding the Hall of Champions
