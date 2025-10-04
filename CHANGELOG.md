# Changelog

All notable changes to DAGMod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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