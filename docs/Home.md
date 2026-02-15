# Welcome to the DAGMod Wiki

This documentation provides a comprehensive overview of the features, mechanics, and content available in DAGMod.

## Navigation

*   [Classes](/classes/overview)
*   [Items](/items/overview)
*   [Races](/races/overview)
*   [Bosses & Dungeons](/bosses_dungeons)
*   [Merchants](/merchants)
*   [Party System](/party)
*   [Dimensions](/dimensions)
*   [Getting Started](/getting_started)
*   [Progression](/progression)
*   [Quests](/quests)

---

## What is DAGMod

Choose your destiny by selecting from four distinct races and three powerful classes. Each combination unlocks unique synergies and abilities, allowing you to forge your own path through the world of Minecraft. Progress through 50 levels, master powerful abilities, explore procedurally generated dungeons, and face legendary bosses!

Current Version v1.6.6-beta (February 2026)

---

## 🎮 What's New in v1.6.6-beta

### Death Recovery (Grave) System

- **Grave blocks** now appear at your death location as a **Lodestone**, storing all your non-soulbound items
- Right-click the grave to recover your items — coordinates are shown in chat when you die
- Other players can loot your grave after **5 minutes**; you can collect immediately
- Dying again before collecting drops old items at the old grave location, then creates a new grave
- Graves persist across server restarts — your items are safe even if the server goes down
- Fully replaces the old non-functional datapack grave system
- Works alongside Soul Bound enchantment (soulbound items still return on respawn)
- Respects `keepInventory` gamerule — no grave is created when enabled

---

## 🎮 What's New in v1.6.5-beta

### Merchant NPC Expansion (8 → 13 Merchants!)

🏪 **5 New Merchant NPCs** added to the world:
- **Baker** - Food merchant selling bread, cookies, pies, cakes, golden apples, and cooked meats
- **Blacksmith** - Buys ALL raw ores for emeralds and sells repair materials (anvils, iron, diamonds, mythril)
- **Jeweler** - Buys processed gems for emeralds, sells Gem Cutter, Citrine Powder, and the premium Silmaril
- **Alchemist** - One-stop shop for ALL brewing ingredients: equipment, modifiers, and all 11 effect ingredients
- **Village Merchant** - Upgraded to a full General Store with 19 trades across 6 categories

🔧 **Merchant Trade Cleanup** - Removed overlap between merchants:
- Voodoo Illusioner no longer sells brewing ingredients (now Alchemist's domain)
- Miner no longer sells processed gems (now Jeweler's domain), only raw gems

### Quest Progression Fixes
- Fixed `master_crafter` quest circular dependency blocking Master Tome unlock
- Fixed 30 race-specific quests being invisible (wrong method call)
- Wired up Quest Book upgrade menu in Quest Block
- Lowered Master tier threshold from 30 to 25 completed quests

### Enchanted Book & Loot Fixes
- Bone dungeon chests now correctly drop **tiered enchanted books** (common, uncommon, rare)
- All datapacks consolidated from `dag00x`/`dag01x` namespaces to unified **`dagmod` namespace**
- Treasure rooms, portal rooms, and regular rooms have **distinct loot tables**
- Updated to **Fabric Loader 0.18.4** and **Fabric API 0.141.1+1.21.11**

---

## 🎮 What's New in v1.6.0-beta

### Merchant Rotating Trade System!

🛒 **Dynamic Merchant Inventories** - All 8 merchant NPCs now feature rotating premium trades
- Premium items cycle every 72 hours (real-world time)
- Static trades always available for currency building
- Each merchant has 3-8 unique rotation sets

💬 **Merchant Dialogue** - NPCs now speak when you trade!
- Unique greetings per merchant type
- Occasional hints about current rotating stock
- Adds personality and immersion to trading

**Rotating Stock by Merchant**:
- **Armorer**: Dragonscale, Inferno, Crystalforge, and 5 more premium armor sets
- **Mystery Merchant**: Shadow, Dragon, Elemental, and Epic weapon collections
- **Enchantsmith**: Combat, Utility, Bow, and Special enchantment rotations
- **Voodoo Illusioner**: Rebirth potions, reset crystals, shadow weapons
- **Trophy Dealer**: Dragon, Wither, and End boss trophies
- **Miner**: Raw gem collections (Ruby, Sapphire, Citrine, Tanzanite, Topaz, Zircon, Pink Garnet)
- **Hunter**: Premium horse armor and tracking equipment
- **Lumberjack**: Frostbite Axe, rare saplings, Nether wood

---

## Previous Updates

### v1.5.4-beta - Minecraft Version Update

✅ **Updated to Minecraft 1.21.11** - Final obfuscated version before mappings change
✅ **Updated Fabric Loader to 0.18.4** - Latest stable loader
✅ **Updated Fabric API to 0.141.1+1.21.11** - Compatible with 1.21.11

### v1.5.3-beta - Critical Quest Fix

### Critical Quest Progression Fix!

✅ **Quest Progression Blocker Fixed** - Players can no longer get stuck at level 20 unable to progress
✅ **Quest Book Upgrade Info** - Quest Block now shows which chain unlocks the next quest book tier
✅ **Circular Dependency Resolved** - Final chain quests no longer require the tier they unlock

v1.5.3-beta fixes the game-breaking quest progression wall and improves quest information display!

---

## Features from v1.4.4-beta - 11 New Class Abilities!

### 11 New Class Abilities!

⚔️ Warriors - 3 New Abilities (5 Total)
- Battle Standard Massive self-healing (6 hearts) + Strength II + Regeneration II
- Whirlwind Axe 360° spin attack hitting all enemies in 5 block radius
- Iron Talisman Transform into an unstoppable tank with Resistance III

🔮 Mages - 4 New Abilities
- Arcane Orb Fire 5 homing magic missiles that auto-track enemies
- Temporal Crystal Slow all enemies in 10 block radius with time manipulation
- Mana Catalyst Massive 8 block radius magical explosion
- Barrier Charm Conjure 10 absorption hearts for protection

🗡️ Rogues - 4 New Cooldown-Based Abilities (7 Total)
- Void Blade Teleport behind enemies for surprise attacks
- Vanish Cloak Complete invisibility + speed boost + blind nearby enemies
- Poison Vial Deadly Poison IV with debuffs
- Assassin's Mark Manual backstab dealing 20 damage from behind

Rogues now have TWO ability systems Energy-based (Tome) + Cooldown-based (Items) for ultimate versatility!

---

## Key Features

### 🎭 Race System
Select from four unique races, each with their own stat bonuses and gathering advantages

- Human - Balanced and versatile (+25% XP gain from all sources)
- Dwarf - Masters of mining and resilience (+1 heart, +20% mining speed, ore bonuses)
- Elf - Swift and precise (+15% movement speed, +0.5 reach, Hero of the Village)
- Orc - Powerful and fearsome (+3 hearts, +15% melee damage, hunting bonuses)

Plus 40 race-specific quests in 4 epic storylines!

### ⚔️ Class System
Master one of three distinct classes with powerful abilities

- Warrior - Melee combat specialists with 5 devastating abilities
- Mage - Harness magical powers with 4 permanent abilities plus wands and scrolls
- Rogue - Agile fighters with dual ability systems (7 total abilities + passive backstab)

### 🔄 Synergy System
Discover 9 unique race-class combinations with special abilities
- Dwarf Warriors gain Resistance underground
- Elf Rogues become invisible in forests when sneaking
- Orc Warriors enter Berserker rage when low on health
- And 6 more powerful synergies!

### 📊 Progression System (Levels 1-50)
- Gain XP from mining, combat, quests, and gathering
- Earn stat bonuses as you level: +1 HP per level, +1 attack every 5 levels, +1 armor every 10 levels
- Visual progression HUD showing XP and level
- Total of +49 HP, +10 attack, and +5 armor at level 50!

### 📜 Quest System
Progress through an epic quest system
- 4 difficulty tiers Novice → Apprentice → Expert → Master
- 64 total quests including 40 race-specific quests
- 4 epic quest chains (10 quests each)
  - The Forgemaster's Legacy (Dwarf)
  - Guardian of the Wilds (Elf)
  - Jack of All Trades (Human)
  - Path of the Warlord (Orc)
- Level gates unlock higher tiers (5, 15, 25)
- Massive XP rewards 200-2500 XP per quest

### 🏰 Bone Dungeons
Explore procedurally generated underground dungeons
- Jigsaw-based generation - every dungeon is unique
- 10 structure pieces including treasure rooms and portal rooms
- Custom loot tables with enchanted gear, diamonds, and rare items
- Natural terrain integration

### 👑 Boss System
Face the Bone Realm Boss hierarchy
- Skeleton King (Epic Boss) - 60 HP, netherite armor, summons minions
- Skeleton Lord (Mini-Boss) - 40 HP, summons elite mobs
- Skeleton Summoner (Elite) - Summons weak minions
- Boneling (Minion) - Weak fodder
- Locked treasure chests with epic loot!

### ⚡ Custom Abilities & Items
- 11 new class ability items with cooldowns (v1.4.4-beta)
- 15 consumable powders with class-specific effects
- Rogue energy system with 3 abilities (Smoke Bomb, Poison Dagger, Shadow Step)
- 11 custom armor sets (6 with progressive set bonuses)
- 15 special weapons + 5 Mythril tools + 9 shields with synergy bonuses
- 26 custom enchantments (combat, utility, and passive effects)
- Enhanced consumables Shadow Blend, Fortune Dust, Cooldown Elixir

### 🛒 Merchant System
- 13 unique merchant NPCs with distinct specialties
- **Original 8** with rotating premium trades: Armorer, Mystery Merchant, Enchantsmith, Voodoo Illusioner, Luxury Merchant, Miner, Hunter, Lumberjack
- **5 new specialized merchants**: Baker (food), Blacksmith (ore buying/repair), Jeweler (gems), Alchemist (brewing), Village Merchant (general store)
- Rotating premium trade inventories cycling every 72 hours
- Merchant dialogue with unique greetings and stock hints

### 👥 Party System
- Form groups of up to 5 players with shared XP and party chat
- XP bonuses scaling from +5% (2 players) to +20% (5 players)
- 50-block XP sharing radius with cooperative party quests
- Party quest difficulty tiers from Easy to Legendary

### 🌐 Custom Dimensions
- **Bone Realm** - Undead dimension with boss hierarchy and procedural dungeons
- **Dragon Realm** - Volcanic dimension with Dragon Guardian boss (200 HP flying boss)
- 6 additional themed dimensions (Badlands, Crimson Forest, Deep Dark, Pale Garden, Snowy Plains, Swamp)

### 🌍 Custom Content
- 100+ custom items including fantasy foods, tokens, tomes, and magical scrolls
- Hall of Champions structure with Race and Class Selection Altars
- Persistent data storage - your choices save across sessions
- Custom crafting recipes for unique items and materials

---

## Getting Started

Ready to begin your adventure? Read the [Getting Started Guide](/getting_started) for your first steps in DAGMod!
