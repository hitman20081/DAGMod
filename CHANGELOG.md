# Changelog

All notable changes to DAGMod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.1] - 2025-10-11

### Changed
- Updated to Minecraft 1.21.10
- Updated Fabric Loader to 0.17.3
- Updated Fabric API to 0.135.0+1.21.10

### Fixed
- Fixed all `.getWorld()` method calls to use `.getEntityWorld()`
- Fixed all `.getPos()` method calls to use `.getTrackedPosition().getPos()`
- Updated entity position tracking for 1.21.10 API changes
- Fixed particle spawn parameters to use doubles instead of integers
- Added missing `drawBorder()` helper methods to GUI classes
- Updated world generation files for 1.21.10 format

### Technical
- Replaced deprecated Minecraft API methods with 1.21.10 equivalents
- ServerPlayerEntity now uses `.getEntityWorld()` instead of `.getWorld()`
- All entities use `.getTrackedPosition().getPos()` for position data
- Particle effects updated to new API format
- GUI rendering updated for 1.21.10 DrawContext API

## [1.4.0] - 2025-10-08

### Added - Rogue Ability System
- **Energy System for Rogues**: 100 energy pool exclusive to Rogue class
- **Automatic Energy Regeneration**: 5 energy per second (faster than mana for active gameplay)
- **Visual Energy Bar HUD**:
    - Purple/dark energy bar displayed above hunger bar (right side)
    - Real-time energy tracking with current/max display
    - Only visible to Rogue players
- **3 Unique Rogue Abilities** via Rogue Ability Tome:
    - **Smoke Bomb** (40 energy, 20s cooldown) - Creates smoke cloud for invisibility and escape
        - Grants Invisibility for 6 seconds
        - Grants Speed II for 6 seconds
        - Creates lingering smoke cloud (3 block radius, 5 seconds)
        - Enemies in smoke get Blindness II
        - Breaks mob targeting for clean escapes
    - **Poison Dagger** (25 energy, no cooldown) - Empowers next melee attack with deadly poison
        - Active for 5 seconds after activation
        - Applies Poison III for 8 seconds
        - Applies Weakness II for 6 seconds
        - Applies Nausea for 5 seconds
        - Green particle effects on weapon and target
    - **Shadow Step** (50 energy, 30s cooldown) - Advanced teleportation with tactical advantages
        - Teleports to targeted block (25 block range)
        - Can phase through walls (unlike Mage's Blink)
        - Leaves shadow decoy at starting position (lasts 3 seconds, draws aggro)
        - Grants Invisibility for 3 seconds after teleport
        - Brief invulnerability during teleport (0.5s)
- **Rogue Ability Tome**: Multi-ability item with ability cycling
    - Shift+Right-click to cycle between abilities
    - Right-click to use selected ability
    - Dynamic name display shows currently selected ability
    - Enchanted glint for visual appeal
- **Smart Energy Consumption**: Abilities only consume energy on successful use
    - Failed teleports don't consume energy
    - Proper feedback for failed ability attempts
- **Class-Locked Abilities**: Only Rogues can use the ability tome
- **Enhanced Combat Integration**:
    - Poison Dagger integrates with existing backstab mechanics
    - Invisibility breaks on attack for balance
    - Visual and audio feedback for all abilities

### Technical - Rogue System
- Created rogue subsystem in `class_system/rogue/`:
    - `EnergyData`: Core energy storage and manipulation
    - `EnergyManager`: Server-side energy management and regeneration
    - `EnergyNetworking`: Custom payload for client-server energy sync
    - `RogueCooldownData`: Separate cooldown system for Rogue abilities
    - `RogueAbilityManager`: Ability implementations (Smoke Bomb, Poison Dagger, Shadow Step)
- Client-side rendering:
    - `EnergyHudRenderer`: Custom HUD overlay for energy bar (purple theme)
    - `ClientEnergyNetworking`: Client-side packet handling
- Item system:
    - `RogueAbilityItem`: Multi-ability tome with cycling functionality
- Updated `RogueCombatHandler`: Enhanced with Poison Dagger integration
- Efficient energy regeneration (1 energy per 4 ticks = 5/second)
- Shadow decoy uses area effect clouds for mob aggro manipulation

### Changed
- Rogue class now has functional ability system with strategic resource management
- Updated documentation to reflect new Rogue abilities
- Energy bar positioned opposite mana bar for visual balance
- Rogue combat flow now includes ability rotations and combos

### Fixed
- Energy data properly stores in memory (matches mana system architecture)
- Energy regeneration only applies to Rogue class
- Abilities provide proper feedback when requirements not met
- Shadow Step validates target before consuming energy
- Invisibility properly breaks on attack

### Added - Consumable Powder System
- **15 New Consumable Items** crafted from custom powders (Amethyst, Diamond, Emerald, Echo, Quartz, Slimeball)
- **Essential Consumables** (10 items):
    - **Mana Crystal** (Amethyst Powder + Echo Dust + Glass Bottle) - Restores 50 mana instantly
    - **Energy Tonic** (Quartz Powder + Slimeball Dust + Glass Bottle) - Restores 50 energy instantly
    - **Cooldown Elixir** (Diamond Powder + Emerald Powder + Glass Bottle) - Reduces cooldowns
    - **Vampire Dust** (Diamond Powder + Redstone + Rotten Flesh) - Lifesteal effect for 20 seconds
    - **Phantom Dust** (Echo Dust + Phantom Membrane + Feather) - 50% dodge chance for 15 seconds
    - **Spell Echo** (Quartz Powder + Amethyst Powder + Echo Dust) - Next spell casts twice
    - **Battle Frenzy** (Diamond Powder + Redstone + Blaze Powder) - Massive combat boost for 15 seconds
    - **Shadow Blend** (Echo Dust + Coal + Fermented Spider Eye) - Invisibility until attack
    - **Fortune Dust** (Emerald Powder + Lapis + Gold Nugget) - Fortune III on next 10 blocks
    - **Featherfall Powder** (Slimeball Dust + Feather + Phantom Membrane) - No fall damage for 60 seconds
- **Advanced Consumables** (5 items - Epic rarity):
    - **Last Stand Powder** (Emerald Powder + Totem of Undying + Golden Apple) - Auto-revive within 60 seconds
    - **Time Distortion** (Echo Dust + Clock + Ender Pearl) - Slow motion effect for 10 seconds
    - **Overcharge Dust** (Diamond Powder + Amethyst Powder + Nether Star) - Next spell has 2x power
    - **Titan's Strength** (Emerald Powder + Netherite Scrap + Blaze Rod) - +100% melee damage for 20 seconds
    - **Perfect Dodge** (Amethyst Powder + Feather + Rabbit Foot) - 100% dodge for 10 seconds
- **Class-Specific Consumables**:
    - Mana Crystal and Spell Echo - Mage only
    - Energy Tonic and Shadow Blend - Rogue only
    - Cooldown Elixir, Battle Frenzy, Titan's Strength - Warrior only
    - Vampire Dust, Phantom Dust, Fortune Dust, Featherfall Powder, Perfect Dodge - Universal
    - Last Stand Powder and Time Distortion - Universal (Epic)
- **Crafting System**: All consumables craftable in survival mode using custom powders
- **Resource Sink**: Custom powders now have meaningful gameplay purpose
- **Stackable**: Essential consumables stack to 16, advanced consumables stack to 8
- **Visual Feedback**: Unique particle effects for each consumable type

### Technical - Consumable System
- Created `ConsumableItem` class with enum-based consumable types
- 15 new items registered in ModItems
- 15 crafting recipes using custom powders and vanilla materials
- Integration with existing class systems (mana, energy, cooldowns)
- Proper class-locking with feedback messages
- Item models and lang file entries for all consumables

### Changed
- Custom powders (Amethyst, Diamond, Emerald, Echo, Quartz, Slimeball) now have crafting recipes
- Enhanced resource economy with powder-based consumables
- Added utility for previously underutilized custom materials

### Added - Custom Armor Set Bonus System
- **6 Unique Armor Sets** with progressive bonuses (2-piece and 4-piece)
- **Universal Set Detection System**: Automatically detects and applies bonuses
- **Class-Specific Bonuses**: Different effects for Warrior, Mage, and Rogue
- **Visual Notifications**: Action bar messages when equipping sets
- **Real-time Bonus Application**: Effects update immediately when changing armor

**Armor Sets:**
1. **Dragonscale** (Netherite - Dark Red):
    - 2pc: Warrior (Resistance I), Mage (Glowing + 15% spell cost reduction), Rogue (Speed I + 20% crit)
    - 4pc: Warrior (Resistance II), Mage (50% mana regen), Rogue (Speed II + 25% energy regen)

2. **Crystalforge** (Diamond - Purple):
    - 2pc: Night Vision + class bonuses (Warrior: Absorption, Mage: Regen + 25% mana regen, Rogue: Jump Boost + 15% energy regen)
    - 4pc: Glowing + enhanced class bonuses (Mage: -20% spell costs)

3. **Inferno** (Iron - Red):
    - 2pc: Fire Resistance + class bonuses (Warrior: Strength, Mage: Regen + 10% spell cost reduction, Rogue: Speed)
    - 4pc: Fire Resistance II + Glowing + enhanced bonuses

4. **Nature's Guard** (Leather - Green):
    - 2pc: Regeneration + class bonuses (Warrior: Resistance, Mage: Luck + 30% mana regen at 4pc, Rogue: Jump Boost II + 15% crit at 4pc)
    - 4pc: Regen II + Saturation + class bonuses (Rogue: Invisibility)

5. **Shadow** (Leather - Purple):
    - 2pc: Speed + Night Vision (Rogue: Invisibility + 25% crit + 10% spell cost reduction)
    - 4pc: Speed II + Night Vision + class bonuses (Rogue: Invisibility + Jump Boost II + 35% energy regen)

6. **Fortuna** (Gold - Orange):
    - 2pc: Luck III + Speed I
    - 4pc: Luck V + Hero of the Village + Speed II

**Integrated Bonuses:**
- Mana regeneration bonuses apply to mage spell system
- Spell cost reductions apply to all spell scrolls
- Energy regeneration bonuses apply to rogue ability system
- Critical hit bonuses apply to rogue combat mechanics

### Added - Weapon + Armor Synergy System
- **Enhanced bonuses when wearing matching armor set + weapon**
- **5 Weapon Sets** with 2-piece and 4-piece synergy bonuses
- **Dynamic damage scaling** based on armor pieces worn
- **Action bar notifications** when synergy activates

**Weapon Synergies:**

1. **Dragonscale Blade + Dragonscale Armor**:
    - 2pc: Strength I + 15% melee damage bonus
    - 4pc: Strength II + Fire Resistance + 30% melee damage (Warrior: Absorption II)

2. **Inferno Blade + Inferno Armor**:
    - 2pc: Strength I + 15% melee damage
    - 4pc: Strength II + Haste II + 25% melee damage

3. **Shadow Blade/Phantom Blade + Shadow Armor** (Rogue):
    - 2pc: Speed II + Invisibility + 35% backstab bonus (total 2.05x)
    - 4pc: Speed III + Jump Boost III + Invisibility + 75% backstab bonus (total 2.45x!)
    - **Orc Rogue + 4pc Shadow + Shadow Blade**: 2.65x backstab damage!

4. **Poison Fang Spear + Nature's Guard**:
    - 2pc: Regeneration II + Luck I
    - 4pc: Regeneration III + Luck II + Saturation

5. **Crystal Hammer + Crystalforge Armor**:
    - 2pc: Absorption II + Resistance I
    - 4pc: Absorption III + Resistance II + Jump Boost III

**Backstab Damage Stacking**:
- Base Rogue backstab: 1.5x damage
- Orc Rogue: 1.7x damage (+20%)
- Shadow 2pc + Shadow Blade: 2.05x damage (+35%)
- Shadow 4pc + Shadow Blade: 2.45x damage (+75%)
- Orc Rogue + Shadow 4pc + Shadow Blade: 2.65x damage (devastating!)

### Added - Themed Weapons & Shields
**9 Custom Weapons**:
- Dragonscale Blade, Inferno Blade, Shadow Blade, Phantom Blade
- Sword of the True King, Solar Bow, Frostbite Axe
- Crystal Hammer (Mace), Poison Fang Spear, Thunder Pike (Tridents)

**9 Custom Shields**:
- Dragonbone Shield, Blazing Aegis, Nature's Bulwark, Shadowguard Shield
- Crystal Ward, Solar Protector, Frostbound Shield, Celestial Aegis, Stormguard Shield
- All shields provide unique stat bonuses and themed effects

### Technical - Armor & Weapon Systems
- Created `CustomArmorSetBonus` class in `class_system/armor/`
- Enum-based armor set detection system
- Per-player state tracking to prevent notification spam
- Integration with existing mana/energy systems via bonus getters
- Weapon detection through item name matching
- Real-time bonus application through server tick events
- Modified `RogueDamageMixin` to apply weapon synergy damage bonuses
- Updated `SpellScrollItem` to apply mana cost reductions
- Updated `ManaManager` and `EnergyManager` for armor regen bonuses

### Changed
- Mana and energy regeneration now scale with armor bonuses
- Spell costs dynamically reduce based on equipped armor
- Backstab damage now stacks: base + race + armor synergy
- Weapon damage applies synergy multipliers when matching armor equipped
- Action bar shows reduced mana costs when applicable

### Fixed
- Hero of the Village now only applies to Elves (or Fortuna 4pc armor)
- Hero of the Village uses 300 tick duration (reapplied by synergy system) instead of infinite duration
- Non-Elf races no longer receive Hero of the Village on login
- Shadow Blade interaction range fixed (was -500%, now +50%)
- All weapons now deal proper damage
- Armor set detection works with `item_name` component (not just `custom_name`)
- Energy and mana systems properly sync with armor bonus changes

## [1.3.1] - 2025-10-06

### Added - Mage Mana System
- **Mana Pool for Mages**: 100 mana pool exclusive to Mage class
- **Automatic Mana Regeneration**: 2 mana per second passive regeneration
- **Visual Mana Bar HUD**:
    - Blue mana bar displayed above hunger bar
    - Real-time mana tracking with current/max display
    - Only visible to Mage players
- **7 Spell Scrolls** with unique magical effects:
    - **Heal Scroll** (20 mana) - Restores 3 hearts instantly with heart particles
    - **Fireball Scroll** (30 mana) - Launches explosive fireball projectile
    - **Absorption Scroll** (25 mana) - Grants 4 absorption hearts for 30 seconds
    - **Lightning Scroll** (35 mana) - Summons lightning bolt at target location (30 block range)
    - **Frost Nova Scroll** (40 mana) - Freezes and damages enemies in 8 block radius
    - **Blink Scroll** (30 mana) - Teleports to targeted block (20 block range)
    - **Mana Shield Scroll** (15 mana) - Grants Resistance II for 10 seconds
- **Smart Mana Consumption**: Spells only consume mana on successful cast
    - Failed teleports (no valid target) don't consume mana
    - Proper feedback for failed spell attempts
- **Class-Locked Spells**: Only Mages can use spell scrolls
- **Visual Spell Effects**: Unique particle effects for each spell type

### Technical - Mana System
- Created mana subsystem in `class_system/mana/`:
    - `ManaData`: Core mana storage and manipulation
    - `ManaManager`: Server-side mana management and regeneration
    - `ManaNetworking`: Custom payload for client-server mana sync
    - `ClientManaData`: Client-side mana state tracking
    - `ManaHudRenderer`: Custom HUD overlay for mana bar
- Integrated with existing class system
- Persistent mana data storage in player data files
- Real-time networking for seamless mana updates
- Efficient tick-based regeneration (every 20 ticks)

### Changed
- Mage class now has functional mana system replacing placeholder benefits
- Updated Class Selection Tome to accurately reflect mana mechanics
- Mana bar positioned above hunger bar for optimal visibility
- DagModClient now handles mana packet registration and HUD rendering

### Fixed
- Mana data properly persists across server restarts
- Mana regeneration only applies to Mage class
- Spell scrolls provide proper feedback when requirements not met
- Blink spell validates target before consuming mana

## [1.3.0] - 2025-10-05

### Added - Progression System
- **Level System (1-50)**: Complete progression system with exponential XP curve
    - Designed for ~20 hours of gameplay to reach max level
    - ~500,000 total XP required for level 50
- **Automatic XP Gains** from multiple sources:
    - Mining ores: 5-40 XP based on rarity (Coal: 5 XP, Diamond: 25 XP, Ancient Debris: 40 XP)
    - Killing mobs: 10-2000 XP based on difficulty (Zombie: 15 XP, Ender Dragon: 2000 XP, Wither: 1500 XP)
    - Woodcutting: 2 XP per log (all wood types)
    - Farming: 1-3 XP per crop harvested
    - Quest completion: 200-2500 XP based on difficulty tier
- **Level-Based Stat Scaling**:
    - +1 HP per level (total +49 HP at level 50)
    - +1 Attack Damage every 5 levels (total +10 at level 50)
    - +1 Armor every 10 levels (total +5 at level 50)
    - Stats automatically apply on level up and persist across sessions
- **Visual Progression HUD**:
    - Real-time XP bar with level display
    - Gold-colored level text, white XP progress text
    - Material Design green progress bar
    - Configurable position (top-left, top-right, above hotbar, etc.)
    - Shows current XP, required XP, and percentage to next level
- **Level-Up Experience**:
    - Visual/audio feedback (sound effects, particles, messages)
    - Full health restoration on level up
    - Stat bonus display in level-up message
- **Level Gates for Quests**:
    - Novice quests: Available at level 1
    - Apprentice quests: Require level 5
    - Expert quests: Require level 15
    - Master quests: Require level 25
    - Clear messaging when level requirements not met
- **Persistent Progression Data**:
    - Level and XP saved to world files
    - Data stored in `world/data/dagmod/progression/<uuid>.dat`
    - Automatic loading on player join
    - Stat bonuses reapply on server restart

### Added - Race-Specific Quest Chains
- **40 New Race-Specific Quests** organized into 4 unique quest chains:
    - **The Forgemaster's Legacy** (Dwarf, 10 quests):
        - Journey from Apprentice to Forgemaster
        - Focus on mining, crafting, and underground mastery
        - Rewards: Full Netherite gear, enchantments, legendary items
        - Final reward: Nether Star and complete smithing mastery
    - **Guardian of the Wilds** (Elf, 10 quests):
        - Path from Seedling to Forest Lord
        - Nature protection, archery, and forest harmony
        - Rewards: Elytra, Totems of Undying, nature-themed items
        - Final reward: Complete nature mastery and flight
    - **Jack of All Trades** (Human, 10 quests):
        - Rise from Wanderer to Legend
        - Versatile challenges: exploration, trading, diverse skills
        - Rewards: Full Netherite set, Dragon Egg, legendary status
        - Final reward: Ultimate versatility and legendary items
    - **Path of the Warlord** (Orc, 10 quests):
        - Ascent from Grunt to Warlord
        - Combat mastery, hunting, and conquest
        - Rewards: Full Netherite combat gear, boss trophies
        - Final reward: Warlord status with Dragon Egg

### Changed - Quest System Overhaul
- Removed 4 basic placeholder quests (gather stone, wood, etc.)
- Increased total quest count from 24 to 64 quests
- All race-specific quests require appropriate race selection
- Quest chains now provide epic storylines with meaningful progression
- Race quest chains unlock at character creation and progress through all difficulty tiers
- Enhanced quest rewards to match increased challenge and commitment
- Quest chains now tell complete stories from novice to master tier

### Technical - Progression System
- Created progression package with modular architecture:
    - `PlayerProgressionData`: Core data class with XP calculations
    - `ProgressionManager`: Server-side data management and caching
    - `ProgressionStorage`: NBT file I/O for persistence
    - `ProgressionPackets`: Server→Client networking for real-time updates
    - `ProgressionEvents`: Lifecycle event handlers
    - `XPEventHandler`: Automatic XP from gameplay events
    - `StatScalingHandler`: Level-based attribute modifiers
    - `LevelRequirements`: Quest level gate system
- Client-side rendering:
    - `ClientProgressionData`: Client-side data storage
    - `ProgressionHUD`: Custom HUD overlay rendering
- Integration:
    - `LivingEntityMixin`: Mob kill XP rewards
    - Block break events for mining/gathering XP
    - Quest system integration for quest completion XP
- Networking:
    - Custom payload system for progression sync
    - Real-time XP updates to client
    - Efficient data synchronization

### Changed - Quest System Integration
- Quest system now awards XP based on difficulty:
    - Novice: 200 XP
    - Apprentice: 500 XP
    - Expert: 1500 XP
    - Master: 2500 XP
- Quest availability now filtered by player level
- `QuestManager.startQuest()` now checks level requirements
- `QuestManager.getAvailableQuests()` now filters by level
- `QuestData.completeQuest()` now awards XP automatically
- Added race requirement checking to quest system

### Fixed
- Quest difficulty getter now properly returns enum type
- Progression data properly syncs between client and server
- Stat bonuses correctly persist across sessions
- Level-up healing now accounts for new max health

## [1.2.0] - 2025-10-04

### Added
- **Race System**: 4 playable races with unique abilities
    - Human: +25% experience gain from all sources
    - Dwarf: +20% mining speed, +1 heart, -5% movement speed, 15% bonus ore drops
    - Elf: +15% movement speed, +0.5 block reach, permanent Hero of the Village, 20% bonus wood drops
    - Orc: +15% melee damage, +2 hearts, 20% bonus meat from hunting, improved fishing luck
- Race Selection Altar and Race Selection Tome
- Race tokens (Human, Dwarf, Elf, Orc)
- **Race + Class Synergy System**: 9 unique combinations with special abilities
    - Dwarf Warrior: Resistance effect when underground (below Y=50)
    - Elf Rogue: Invisibility in forests when sneaking
    - Orc Warrior: Berserker rage (Strength + Speed) when below 30% health
    - Human Mage: Random regeneration bursts for adaptability
    - Dwarf Mage: Permanent fire resistance
    - Elf Mage: Haste effect in forest biomes
    - Orc Rogue: +20% extra backstab damage (stacks with base backstab)
    - Human Warrior: Absorption hearts when taking damage
    - Human Rogue: Random jump boost for mobility
- **Race-specific gathering bonuses**
    - Dwarves: 15% chance for bonus ore drops when mining (coal, iron, gold, diamond, emerald, etc.)
    - Elves: 20% chance for bonus wood, 25% for extra items from leaves
    - Orcs: 20% chance for bonus meat from hunting, 15% for bonus leather
    - Humans: 25% experience bonus from all sources
- **World Generation**: Hall of Champions structure
    - Spawns naturally in new worlds on the surface
    - Contains both Race and Class Selection Altars
    - Surface-adaptive placement system
- **Hall Locator**: Item given to new players to help find the Hall of Champions
- **Persistent Data Storage**: Race and class selections now save across server restarts
    - Data stored in world save files
    - Automatic loading on player login

### Changed
- Enhanced class system with detailed stat breakdowns
- Warrior: Added 15% physical damage reduction
- Mage: 50% reduced enchantment costs, 50% longer potion durations, permanent night vision
- Rogue: 25% critical hit chance, backstab mechanics (1.5x damage from behind), 50% reduced fall damage
- Improved selection feedback and visual effects
- Better player onboarding with Hall Locator system

### Technical
- Added mixins for race gathering bonuses (RaceMiningMixin, RaceHuntingMixin)
- Implemented synergy checking system with periodic tick handler (PlayerTickHandler)
- Enhanced race/class ability management (RaceAbilityManager, ClassAbilityManager)
- Created persistent data storage system (PlayerDataManager)
- Added data management for player race/class information
- Integrated race bonuses into existing class handlers (RogueCombatHandler synergy)

### Fixed
- Player data now persists across server restarts
- Race and class selections properly load on player login
- Synergy effects check every second for responsive gameplay

## [1.1.0] - Initial Release

### Added
- Complete quest system with 4 tier levels (Novice, Apprentice, Expert, Master)
- 3 playable classes (Warrior, Mage, Rogue)
- Class Selection Altar and Class Selection Tome
- Class tokens and Class Reset Crystal
- 50+ custom items and foods
- Custom potions and effects
- Quest chains and progression system
- Quest Block for accepting and completing quests
- Simple NPC system