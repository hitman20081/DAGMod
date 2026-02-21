# DAGMod Development Roadmap

**Current Version**: v1.7.0
**Last Updated**: 2026-02-16

This document tracks the development progress of DAGMod features, comparing planned features against implemented functionality.

---

## 📌 **Semantic Versioning**

DAGMod follows **Semantic Versioning 2.0.0** (https://semver.org/):

**Format**: `MAJOR.MINOR.PATCH-beta`

- **MAJOR** (x.0.0) - Incompatible API changes, major milestones, game-changing features
- **MINOR** (0.y.0) - New features added in a backwards-compatible manner
- **PATCH** (0.0.z) - Backwards-compatible bug fixes only

**Examples**:
- `1.5.0-beta` → New features (Garrick NPC, quest gating)
- `1.5.1-beta` → Bug fixes for v1.5.0 (no new features)
- `1.6.0-beta` → New features (enhanced consumables)
- `2.0.0` → Major release (removes beta tag, major milestone)

---

## ✅ **Fully Implemented Features**

### **Core Systems**
- ✅ **Race System** (4 races: Human, Dwarf, Elf, Orc)
  - Race selection altars
  - Passive bonuses (mining, movement, damage, XP)
  - Race-specific gathering bonuses
  - 9 unique race+class synergies

- ✅ **Class System** (3 classes: Warrior, Mage, Rogue)
  - Class selection altars
  - Warrior: 5 abilities (Battle Standard, Whirlwind, Iron Talisman, Shield Bash, War Cry)
  - Mage: 4 core abilities + wands/scrolls (Arcane Missiles, Mana Burst, Time Warp, Arcane Barrier)
  - Rogue: 7 abilities (dual energy/cooldown system + backstab passive)
  - Class-specific items and mechanics

- ✅ **Progression System**
  - 200 levels with gentle exponential XP scaling (1.05x per level)
  - Multiple XP sources (mining, combat, quests, farming, fishing)
  - +1 heart (2 HP) every 10 levels (30 hearts at level 200)
  - +1 attack every 5 levels (+40 at level 200)
  - +1 armor every 10 levels (+20 at level 200)
  - Visual XP bar HUD
  - Level requirements for quest tiers
  - **Fixed in v1.4.5**: Health/stats persistence on death
  - **Reworked in v1.7.0**: Level cap 50→200, heart scaling rework

- ✅ **Quest System**
  - 64+ quests across multiple categories (MAIN, SIDE, CLASS, JOB)
  - Quest Block for story/side quests
  - Job Board for jobs/daily quests (v1.4.5)
  - 4 difficulty tiers (Novice, Apprentice, Expert, Master)
  - Quest chains with sequential progression
  - Multiple objective types (collect, kill, craft, delivery, tag-collect)
  - Quest book tier system
  - 40 race-specific quest chains
  - **Added in v1.4.5**: Tutorial gating via Innkeeper Garrick NPC

- ✅ **Party System** (v1.4.4-v1.4.5)
  - Party creation and invitations
  - Shared XP distribution (`PartyXPHandler`)
  - Boss loot distribution (`PartyLootHandler`)
  - Party quests with shared objectives (`PartyQuestManager`)
  - Party quest difficulties (EASY, MEDIUM, HARD, ELITE)
  - Party commands (/party create, invite, leave, kick, disband, list)
  - Party HUD display

### **World Content**
- ✅ **Bone Realm Dimension**
  - Custom dimension with portal system (Ancient Bone blocks + Necrotic Key)
  - 4-tier boss hierarchy (Skeleton King → Lords → Summoners → Bonelings)
  - Locked treasure chests with key system
  - Boss-specific loot tables

- ✅ **Bone Dungeons**
  - Procedurally generated underground structures
  - 10 unique room types
  - Custom loot tables with themed rewards
  - Natural terrain integration

- ✅ **Hall of Champions**
  - Surface structure with Race & Class Selection Altars
  - Hall Locator item for navigation
  - Respawn point integration

### **Items & Equipment**
- ✅ **50+ Custom Items**
  - 15 consumable powders (Shadow Blend, Fortune Dust, Cooldown Elixir, etc.)
  - 6 themed armor sets with progressive bonuses
  - 9 themed weapons and 9 shields
  - Wands and spell scrolls for Mages
  - Class ability items
  - Fantasy foods and potions

- ✅ **Dragon Realm Exclusive Materials** (v1.5.5+)
  - Mythril system (ore, ingot, nugget, tools, armor)
  - 8 Dragon Realm gems (Citrine, Pink Garnet, Ruby, Sapphire, Tanzanite, Zircon, Topaz, Silmaril)
  - 21 gem-related blocks (gem blocks, raw gem blocks, ores, deepslate ores)
  - Full mining → smelting progression
  - Prepared for future gem socket system (v2.2.0)

- ✅ **Custom Armor Sets**
  - Dragonscale, Crystalforge, Inferno, Nature's Guard, Shadow, Fortuna
  - Set bonuses when wearing full sets
  - Unique textures and models

### **NPCs & Interactions**
- ✅ **Innkeeper Garrick Tutorial NPC** (v1.4.5)
  - Tutorial quest giver
  - Quest system introduction
  - Unlocks Quest Blocks and Job Boards on interaction
  - `/summon_garrick` command for testing
  - Persistent interaction tracking

- ✅ **Simple NPC System**
  - Base NPC entity framework
  - Dialogue system
  - Custom renderers

- ✅ **Merchant NPC Rotating Trade System** (v1.6.0)
  - 8 merchant NPCs with rotating inventories (Armorer, Mystery Merchant, Enchantsmith, Voodoo Illusioner, Trophy Dealer, Miner, Hunter, Lumberjack)
  - Static trades always available for currency building
  - Premium rotating trades that cycle every 72 hours (real-world time)
  - Rotation persists across server restarts via NBT storage
  - Configurable rotation interval (12h-168h range)
  - Merchant dialogue system with greetings and rotation hints

- ✅ **Expanded Merchant Roster** (v1.6.5)
  - 5 new specialized merchant NPCs (13 total): Baker, Blacksmith, Jeweler, Alchemist, Village Merchant
  - Baker: Food and baked goods (8 trades)
  - Blacksmith: Buys all raw ores for emeralds, sells repair materials (21 trades)
  - Jeweler: Buys processed gems, sells gem tools and Silmaril (12 trades)
  - Alchemist: All brewing equipment and potion ingredients (21 trades)
  - Village Merchant: General store with everyday supplies (19 trades)
  - Merchant trade overlap cleanup for clear specialization

### **QoL Features**
- ✅ **Ship Travel System** (`ShipTravelManager`)
  - Fast travel via `/travel` command
  - Destination-based teleportation
  - Ship Helm block (v1.4.5)

- ✅ **Death Recovery System** (Java-based, v1.6.6)
  - Lodestone grave blocks placed at death location
  - All non-soulbound items stored in grave, persisted to disk
  - Right-click grave to recover items; 5-minute loot delay for non-owners
  - One grave per player with automatic old-grave cleanup
  - Replaces old non-functional datapack grave system

- ✅ **Enhanced Consumables** (v1.6.0)
  - Lifesteal System (Vampire Dust) - 10% healing from damage dealt
  - Dodge/Evasion System (Phantom Dust 50%, Perfect Dodge 100%)
  - Spell Modification System (Spell Echo doubles spells, Overcharge 2x power/range)
  - Death Prevention System (Last Stand Powder auto-revive at 50% HP)
  - Time Distortion AoE (Speed II for player, Slowness IV for nearby enemies)

---

## 🟡 **Partially Implemented Features**

### **Daily Quests** (Target: v1.9.0)
- 🟡 **Framework exists** - Job Board has DAILY category support
- ❌ **Implementation needed** - Daily quest rotation, reset timers, reward scaling

### **Custom Enchantments** (Target: v1.10.0)
- 🟡 **Framework exists** - `/data/dag009/enchantment/` directory with basic examples
- ❌ **Implementation needed** - Race/class-specific enchantments, discovery system

---

## ❌ **Planned Features**

### **v1.8.0 - Spells & Crafting**
Priority: **MEDIUM**

- ❌ **5-7 New Spell Scrolls**
  - Gravity Well (pull enemies together)
  - Chain Lightning (bouncing lightning)
  - Ice Wall (creates barrier)
  - Meteor Storm (raining meteors)
  - Life Drain (healing damage)
  - Dimensional Rift (short-range teleport)
  - Polymorph (temporary mob transformation)

- ❌ **Spell Scroll Crafting**
  - Crafting recipes for all spell scrolls
  - Rare ingredient requirements
  - Crafting station integration

### **v1.9.0 - Daily Quests**
Priority: **MEDIUM**

- ❌ **Daily Quest System**
  - Daily quest rotation (3-5 quests per day)
  - 24-hour reset timers
  - Scaling rewards based on player level
  - Daily quest completion tracking
  - Streak bonuses for consecutive days

### **v1.10.0 - Custom Enchantments**
Priority: **MEDIUM**

- ❌ **Race-Specific Enchantments**
  - Dwarf: Deep Striker (bonus damage underground)
  - Elf: Forest's Blessing (bonus in forest biomes)
  - Orc: Berserker's Fury (damage scales with missing health)
  - Human: Versatile (bonus XP from all sources)

- ❌ **Class-Specific Enchantments**
  - Warrior: Immovable (knockback resistance)
  - Mage: Arcane Amplification (spell power boost)
  - Rogue: Shadow Step (chance to dodge on hit)

### **v2.0.0 - Major Feature Release**
Priority: **MEDIUM** (Major milestone - removes beta tag)

- ❌ **Boss Encounters in Bone Dungeons**
  - Mini-bosses in special dungeon rooms
  - Unique mechanics per boss
  - Enhanced loot tables
  - Boss key fragments for final boss unlock

- ❌ **Race Quest Chain Expansions**
  - Additional quest chains for each race
  - Race-specific storylines
  - Legendary race rewards

- ❌ **Major Polish Pass**
  - Performance optimization
  - Balance adjustments
  - Bug fixes
  - Quality of life improvements

### **v2.1.0 - Guild System**
Priority: **LOW** (Long-term)

- ❌ **Guild Creation & Management**
  - Create/join/leave guilds
  - Guild ranks and permissions
  - Guild chat channel
  - Guild MOTD (Message of the Day)

- ❌ **Guild Progression**
  - Guild levels and XP
  - Guild perks unlock system
  - Guild quests (shared objectives)
  - Guild reputation

- ❌ **Guild Features**
  - Guild bank/storage
  - Guild halls/headquarters
  - Guild vs Guild events
  - Guild leaderboards

### **v2.2.0 - Economy & Trading**
Priority: **LOW** (Long-term)

- ❌ **Transmog System**
  - Keep stats, change appearance
  - Appearance library/collection
  - Transmog station block
  - Transmog costs (gold/materials)

- ❌ **Item Reforging**
  - Reroll gear stats
  - Stat ranges and quality tiers
  - Reforge costs scale with quality
  - Lock stats during reforge

- ❌ **Gem Socket System**
  - Add sockets to gear (1-3 sockets based on item tier)
  - Dragon Realm gems (Citrine, Pink Garnet, Ruby, Sapphire, Tanzanite, Zircon, Topaz, Silmaril)
  - Gem stat bonuses:
    - Ruby → +Attack Damage / Strength
    - Sapphire → +Max Health
    - Tanzanite → +Movement Speed
    - Citrine → +Attack Speed
    - Topaz → +Armor / Defense
    - Zircon → +Luck (Fortune bonus)
    - Pink Garnet → TBD (playtesting)
    - Silmaril → Legendary tier (multiple bonuses)
  - Socket insertion mechanic (Smithing Table or custom Gem Socketing Station)
  - Gem removal/replacement system
  - Visual indicators for socketed items
  - Component-based implementation (MC 1.21.11 item components)

- ❌ **Auction House**
  - Player marketplace
  - Bidding system
  - Search and filter
  - Auction duration and fees
  - Buy-out prices

### **v2.3.0 - Jewelry & Trinkets System**
Priority: **LOW** (Long-term - inspired by Trinkets mod)

- ❌ **Jewelry Slots**
  - 2 Ring slots
  - 1 Necklace/Amulet slot
  - 1 Bracelet/Charm slot (optional)
  - Custom inventory GUI integration
  - Equipment slot renderer

- ❌ **Craftable Jewelry**
  - Ring crafting (various tiers: Iron, Gold, Diamond, Mythril, Legendary)
  - Necklace/Amulet crafting
  - Gem-socketed jewelry (combine with v2.2.0 socket system)
  - Unique jewelry effects (speed boost, regeneration, mana regen, etc.)
  - Class-specific jewelry (Mage rings, Warrior amulets, Rogue charms)
  - Race-specific jewelry bonuses

- ❌ **Jewelry Stats & Effects**
  - Passive stat bonuses (health, damage, armor, etc.)
  - Unique effects (water breathing, fire resistance, night vision, etc.)
  - Set bonuses for wearing matching jewelry
  - Enchantable jewelry
  - Jewelry durability (optional - may not degrade)

- ❌ **Integration with Existing Systems**
  - Jewelry as quest rewards
  - Boss-specific legendary jewelry drops
  - Jewelry upgrade paths
  - Combine with gem socket system for enhanced customization

### **v3.0.0 - Endgame Expansion**
Priority: **VERY LOW** (Long-term vision - Major expansion)

- ❌ **Dynamic Difficulty System**
  - Adaptive mob scaling with player level
  - Hardcore mode toggle (permadeath or severe penalties)
  - Nightmare dungeons (increased difficulty, better loot)
  - Difficulty multipliers

- ❌ **Raid Bosses**
  - Multi-player required encounters (4-10 players)
  - Complex boss mechanics
  - Enrage timers
  - Epic loot tables
  - Weekly lockouts

- ❌ **Endless Dungeon Mode**
  - Wave survival system
  - Progressive difficulty scaling
  - Wave leaderboards
  - Endless-specific rewards

- ❌ **Prestige System**
  - Reset to level 1 with permanent bonuses
  - Prestige levels (1-10+)
  - Unique prestige perks
  - Prestige-only cosmetics

- ❌ **Seasonal Events**
  - Limited-time content
  - Seasonal quests and bosses
  - Exclusive seasonal rewards
  - Event calendar

### **Future Quality of Life**
Priority: **AS NEEDED**

- ❌ **Waypoint System**
  - Discoverable waypoints in world
  - Fast travel between waypoints
  - Cooldown/cost system
  - Waypoint sharing in parties

- ❌ **Auto-Sort Inventory**
  - One-click inventory organization
  - Customizable sort rules
  - Quick deposit to storage
  - Sort by type/rarity/value

- ❌ **Skill Loadouts**
  - Save ability configurations
  - Switch loadouts on the fly
  - Loadout presets (PvE, PvP, Solo, Party)
  - Keybind per loadout

- ❌ **Quest Markers**
  - Show quest locations on compass
  - Waypoint markers for objectives
  - Distance indicators
  - Quest path highlighting

### **Future RPG Features**
Priority: **LOW**

- ❌ **Title System**
  - Earn titles from achievements
  - Display titles above player name
  - Title-based stat bonuses
  - Rare/legendary titles

- ❌ **Reputation Factions**
  - Multiple NPC factions
  - Earn favor through quests/actions
  - Reputation tiers (Hostile → Exalted)
  - Reputation-gated rewards and quests

- ❌ **Collection Book**
  - Track discovered items/mobs
  - Collection completion rewards
  - Collection milestones
  - Rare/legendary collection entries

- ❌ **Achievement System**
  - Integrated with Minecraft achievements
  - Custom DAGMod achievements
  - Hidden achievements
  - Achievement-based rewards

- ❌ **Leaderboards**
  - Server rankings
  - Multiple categories (level, quests, kills, boss kills, etc.)
  - Seasonal leaderboards
  - Rewards for top players

### **Future Cosmetic Features**
Priority: **VERY LOW**

- ❌ **Mounts System**
  - Rideable creatures
  - Mount speeds and abilities
  - Mount collection
  - Mount customization

- ❌ **Pet Companions**
  - Non-combat followers
  - Pet abilities and bonuses
  - Pet leveling
  - Pet collection

- ❌ **Player Housing**
  - Personal base with storage
  - Furniture and decorations
  - Storage upgrades
  - Housing customization

- ❌ **Character Customization**
  - Enhanced appearance options
  - Cosmetic armor slots
  - Dye system for armor
  - Skin overlays

### **Future World Features**
Priority: **LOW**

- 🟡 **NPC Vendors** (Partially complete)
  - ✅ 13 merchant NPCs with trading (v1.6.0-v1.6.5)
  - ✅ Quest giver NPC (Innkeeper Garrick, v1.5.0)
  - ❌ Reputation-locked vendors
  - ❌ Wandering vendors

- ❌ **Mining Expeditions**
  - Deep-dive mining events
  - Progressive depth challenges
  - Rare ore discoveries at depth
  - Mining party bonuses

- ❌ **Cooking System Overhaul**
  - Food combination mechanics
  - Recipe discovery
  - Cooking stations
  - Complex buff stacking

- ❌ **Fishing Overhaul**
  - Special rare fish
  - Fish-based buffs and consumables
  - Fishing quests
  - Fishing tournaments

- ❌ **Dynamic Weather Effects**
  - Environmental challenges
  - Weather-based buffs/debuffs
  - Seasonal weather patterns
  - Weather-triggered events

- ❌ **PvP Arena**
  - Dedicated PvP zones
  - Arena rankings
  - PvP-specific rewards
  - Tournament system

---

## 🚫 **Scrapped/Deprecated Features**

*(None currently - all planned features remain on roadmap)*

---

## 📊 **Feature Comparison Matrix**

| Feature Category | Discussed | Implemented | In Progress | Planned |
|-----------------|-----------|-------------|-------------|---------|
| Social/Multiplayer | 6 | 1 (Party) | 0 | 5 (Guilds, etc.) |
| Endgame Systems | 5 | 0 | 0 | 5 (Raids, Prestige, etc.) |
| Consumables | 5 | 0 | 5 (placeholders) | 0 |
| RPG Enhancement | 7 | 0 | 0 | 7 |
| Crafting/Economy | 5 | 0 | 0 | 5 |
| Quality of Life | 6 | 2 (Travel, Graves) | 0 | 4 |
| Progression | 4 | 1 (Achievements) | 0 | 3 |
| Cosmetic | 4 | 0 | 0 | 4 |
| NPC/World | 4 | 2 (Garrick, 13 Merchants) | 0 | 2 |

---

## 🗓️ **Development Timeline**

### **Released**
- ✅ **v1.4.5-beta** (Oct 2024) - Job Board, quest categorization
- ✅ **v1.5.0-beta** (Jan 2025) - Garrick NPC, quest gating, health persistence fix
- ✅ **v1.5.1-beta** (Jan 2025) - Patch: Quest reward fixes, backward compatibility
- ✅ **v1.5.2-beta** (Jan 2025) - CRITICAL: Quest persistence, memory leaks, thread safety
- ✅ **v1.5.3-beta** (Dec 2025) - CRITICAL: Progression reset fix, new world data leakage fix, quest level 20 blocker fix
- ✅ **v1.6.0-beta** (Dec 2025-Feb 2026) - Rotating trade system, enhanced consumables, Dragon Realm, custom enchantments
- ✅ **v1.6.5-beta** (Feb 2026) - 5 new merchant NPCs (13 total), quest progression fixes, loot table fixes
- ✅ **v1.6.6-beta** (Feb 2026) - Java-based death recovery (grave) system, replaces datapack graves
- ✅ **v1.7.0** (Feb 2026) - Level cap 50→200, heart scaling rework (+1 heart/10 levels)

### **Short-term** - Next 3-6 months
- 🎯 **v1.7.1** (Patch) - Enhanced consumable effects (lifesteal, dodge, spell mods)
- 🎯 **v1.8.0** (Minor) - New spell scrolls and crafting system
- 🎯 **v1.9.0** (Minor) - Daily quest system with rotation
- 🎯 **v1.10.0** (Minor) - Custom race/class enchantments

### **Medium-term** - 6-12 months
- 🎯 **v2.0.0** (Major) - Dungeon bosses, race quest expansions, removes beta tag
- 🎯 **v2.1.0** (Minor) - Guild system foundation
- 🎯 **v2.2.0** (Minor) - Economy, trading, and gem socket system
- 🎯 **v2.3.0** (Minor) - Jewelry and trinkets system

### **Long-term** - 12+ months
- 🎯 **v3.0.0** (Major) - Endgame expansion (raids, prestige, seasonal events)
- 🎯 Future major expansions as needed

---

## 📝 **Notes & Guidelines**

### **Version Numbering (Semantic Versioning)**

Following **SemVer 2.0.0** format: `MAJOR.MINOR.PATCH-beta`

**MAJOR version** (x.0.0):
- Incompatible API changes or breaking changes
- Major milestones (e.g., v2.0.0 removes beta tag)
- Game-changing features that fundamentally alter gameplay
- Major expansions (e.g., v3.0.0 endgame expansion)

**MINOR version** (0.y.0):
- New features added in backwards-compatible manner
- New content (spells, enchantments, quests, systems)
- Feature enhancements
- Examples: v1.6.0 (consumables), v1.7.0 (progression rework), v1.8.0 (spells), v2.1.0 (guilds)

**PATCH version** (0.0.z):
- Backwards-compatible bug fixes ONLY
- No new features
- Performance improvements
- Code cleanup
- Examples: v1.5.1, v1.6.1, v2.0.1

### **Priority Levels**
- **HIGH** = Critical for player experience, fixes placeholder content
- **MEDIUM** = Significant content addition, enhances existing systems
- **LOW** = Nice-to-have, long-term vision
- **VERY LOW** = Aspirational, may be cut or significantly delayed

### **Implementation Status**
- ✅ = Fully implemented and released
- 🟡 = Partially implemented, needs completion
- ❌ = Not started, in planning
- 🚫 = Scrapped, removed from roadmap

### **Community Feedback**
This roadmap is a living document and will be updated based on:
- Player feedback and suggestions
- Development complexity discoveries
- Bug reports and priority shifts
- Community votes on feature priorities

Submit feedback at: https://github.com/hitman20081/DAGMod/issues

---

**Last Updated**: 2026-02-16
**Maintained By**: hitman20081
**Current Version**: v1.7.0
**License**: See LICENSE file
