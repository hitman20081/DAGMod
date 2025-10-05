# Changelog

All notable changes to DAGMod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

### Changed
- Quest system now awards XP based on difficulty:
  - Novice: 200 XP
  - Apprentice: 500 XP
  - Expert: 1500 XP
  - Master: 2500 XP
- Quest availability now filtered by player level
- `QuestManager.startQuest()` now checks level requirements
- `QuestManager.getAvailableQuests()` now filters by level
- `QuestData.completeQuest()` now awards XP automatically

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