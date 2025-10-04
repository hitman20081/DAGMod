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
  - Orc: +15% melee damage, +2 hearts, 20% bonus meat from hunting
- Race Selection Altar and Race Selection Tome
- Race tokens (Human, Dwarf, Elf, Orc)
- **Race + Class Synergy System**: 9 unique combinations with special abilities
  - Dwarf Warrior: Resistance effect when underground
  - Elf Rogue: Invisibility in forests when sneaking
  - Orc Warrior: Berserker rage (Strength + Speed) at low health
  - Human Mage: Random regeneration bursts
  - Dwarf Mage: Permanent fire resistance
  - Elf Mage: Haste effect in forest biomes
  - Orc Rogue: +20% extra backstab damage
  - Human Warrior: Absorption hearts when taking damage
  - Human Rogue: Random jump boost
- Race-specific gathering bonuses
  - Dwarves: Bonus ore drops when mining
  - Elves: Bonus wood and items from woodcutting
  - Orcs: Bonus meat and leather from hunting
- **World Generation**: Hall of Champions structure
  - Spawns naturally on world creation
  - Contains both Race and Class Selection Altars
  - Surface-adaptive placement system

### Changed
- Enhanced class system with detailed stat breakdowns
- Warrior now has 15% physical damage reduction
- Mage gains 50% reduced enchantment costs and 50% longer potion durations
- Rogue gains 25% critical hit chance, backstab mechanics, and 50% reduced fall damage
- Improved selection feedback and visual effects

### Technical
- Added mixins for race gathering bonuses
- Implemented synergy checking system with periodic tick handler
- Enhanced race/class ability management

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