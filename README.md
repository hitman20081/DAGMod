# DAGMod v1.2.0 - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft using Fabric, featuring an intricate quest system, class progression, race selection, synergy abilities, custom items, and immersive gameplay mechanics.

## Features

### Quest System
- **Multi-tier Quest Books**: Progress from Novice to Master level
- **Quest Chains**: Interconnected storylines that guide player progression
- **Multiple Quest Types**:
    - Fetch Quests - Gather specific items
    - Kill Quests - Defeat enemies
    - Delivery Quests - Transport items to NPCs (Work in Progress)
    - Crafting Quests - Create specific items (Work in Progress)
    - Collection Quests - Gather diverse materials (Work in Progress)
- **Quest Blocks**: Interactive blocks for accepting and completing quests
- **Dynamic Quest Tracking**: Real-time progress monitoring
- **Quest Book Upgrades**: Unlock higher-tier quests by completing challenges

### RPG Class System
- **Three Distinct Classes**:
    - **Warrior**: 
        - +4 hearts (+8 health)
        - +25% melee attack damage
        - -10% movement speed
        - 15% damage reduction from physical sources
        - Heavy armor proficiency
    - **Mage**: 
        - -2 hearts (-4 health)
        - -25% melee attack damage
        - 50% reduced enchantment costs
        - 50% longer potion durations
        - Permanent night vision
    - **Rogue**: 
        - -1 heart (-2 health)
        - +30% movement speed
        - 25% critical hit chance (2x damage)
        - Backstab bonus (1.5x damage from behind)
        - 50% reduced fall damage
- **Class Selection Altar**: Choose your path at the start of your journey
- **Class-Specific Abilities**: Unique powers and stat modifiers for each class
- **Class Reset Crystal**: Allows players to change their chosen class
- **Level-Based Class Reset**: Free class reset every 10 quests completed

### Race System
- **Four Playable Races**:
    - **Human - The Balanced**:
        - +25% experience gain from all sources
        - Jack of all trades with no penalties
        - Versatile gameplay
    - **Dwarf - The Miner**:
        - +20% mining speed
        - +1 heart (+2 health)
        - -5% movement speed
        - 15% chance for bonus ore drops when mining
    - **Elf - The Ranger**:
        - +15% movement speed
        - +0.5 block interaction range
        - Permanent Hero of the Village effect
        - 20% chance for bonus wood drops
    - **Orc - The Warrior**:
        - +15% melee attack damage
        - +2 hearts (+4 health)
        - 20% chance for bonus meat from hunting
        - Better fishing luck
- **Race Selection Altar**: Discover your heritage at the Hall of Champions
- **Race Selection Tome**: Learn about each race before making your choice
- **Race + Class Combinations**: Stack racial bonuses with class abilities for unique builds
- **Permanent Heritage**: Race selection cannot be changed once chosen

### Race + Class Synergies
Special abilities unlock when combining specific races and classes:
- **Dwarf Warrior**: Resistance effect when underground (below Y=50)
- **Elf Rogue**: Invisibility in forest biomes when sneaking
- **Orc Warrior**: Berserker rage (Strength + Speed) when below 30% health
- **Human Mage**: Random regeneration bursts for adaptability
- **Dwarf Mage**: Permanent fire resistance from mountain forges
- **Elf Mage**: Haste effect in forest biomes
- **Orc Rogue**: +20% extra backstab damage (stacks with base backstab)
- **Human Warrior**: Absorption hearts when taking damage
- **Human Rogue**: Random jump boost for versatile movement

### World Generation
- **Hall of Champions**: Spawns naturally in new worlds
    - Contains both Race and Class Selection Altars
    - Generates on solid ground in the Overworld
    - Your journey begins here
- **Hall Locator**: New players receive a locator item to find the structure

### Race-Specific Gathering
- **Dwarves**: Bonus ore drops when mining (coal, iron, gold, diamond, emerald, etc.)
- **Elves**: Bonus wood drops and extra items from leaves
- **Orcs**: Bonus meat and leather from hunting animals
- **Humans**: Enhanced experience gain to level faster

### Custom Items & Food
Dozens of custom fantasy-themed items including:
- **Quest Books**: Novice, Apprentice, Expert, and Master tiers
- **Class Tokens**: Warrior, Mage, and Rogue selection tokens
- **Race Tokens**: Human, Dwarf, Elf, and Orc heritage tokens
- **Selection Tomes**: Detailed information about classes and races
- **Hall Locator**: Find the Hall of Champions
- **Crafting Materials**: Various powders (Diamond, Emerald, Amethyst, Quartz), Echo Dust, Slimeball Dust
- **Fantasy Foods**: Over 20 unique food items with special effects

### Custom Potions & Effects
- **XP Effect**: Custom experience gain mechanics
- **Potion of the Phoenix**: Special revival properties
- **Potion of the Supes**: Enhanced abilities
- **Super Stamina Boost**: Extended endurance
- **Essence of Thanos**: Powerful endgame potion

### Interactive Elements
- **Quest Block**: Central hub for quest interactions
- **Class Selection Altar**: Choose and customize your character class
- **Race Selection Altar**: Determine your ancestral heritage
- **Simple NPC**: Quest giver and interaction system

### Persistent Data Storage
- Race and class selections are saved across server restarts
- Player data stored in world save files
- Seamless experience for server play

## Installation

### Requirements
- Minecraft 1.21.x
- Fabric Loader
- Fabric API

### Installation Steps
1. Download the latest release from the [Releases](https://github.com/hitman20081/DAGMod/releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Launch Minecraft with Fabric Loader
4. Enjoy your fantasy RPG adventure!

## Getting Started

1. **Create a New World**: Generate a new world to experience the full mod
2. **Use the Hall Locator**: Right-click the item you receive on first join to find the Hall of Champions
3. **Discover Your Heritage**: Locate the Hall of Champions and approach the Race Selection Altar
4. **Choose Your Race**: Read the Race Selection Tome and select from Human, Dwarf, Elf, or Orc
5. **Choose Your Class**: Approach the Class Selection Altar and select Warrior, Mage, or Rogue
6. **Explore Synergies**: Experiment with different race+class combinations for unique abilities
7. **Begin Questing**: Find Quest Blocks to start your adventure
8. **Level Up**: Complete quests to earn rewards and upgrade your quest book

## Recommended Race + Class Combinations

- **Dwarf Warrior**: Ultimate tank - massive health, resistance underground, mining bonuses
- **Elf Rogue**: Stealth assassin - incredible speed, invisibility in forests, deadly backstabs
- **Orc Warrior**: Berserker - devastating damage, rage mode at low health, extra meat from hunting
- **Human Mage**: Versatile spellcaster - fast leveling, balanced stats, adaptable playstyle
- **Elf Mage**: Nature mage - enhanced mobility, haste in forests, Hero of the Village
- **Dwarf Mage**: Forge mage - fire resistance, tanky caster, mining prowess
- **Orc Rogue**: Brutal assassin - extreme backstab damage, extra health, hunting expertise
- **Human Warrior**: Adaptable tank - absorption hearts, balanced combat, fast progression
- **Human Rogue**: Versatile scout - mobility boosts, fast leveling, jack of all trades

## Crafting Recipes

The mod includes numerous custom crafting recipes for:
- Converting flesh to leather (multiple methods: smelting, smoking, blasting, campfire)
- Creating magical powders from gems
- Crafting fantasy foods and potions
- Processing materials into rare substances

Check the recipe book in-game for complete crafting information.

## Development

### Building from Source
```bash
./gradlew build
```

The compiled mod will be in `build/libs/`

### Project Structure
```
src/main/java/com/github/hitman20081/dagmod/
├── block/          - Custom blocks (Quest Block, Altars)
├── class_system/   - RPG class mechanics and abilities
├── race_system/    - Race selection and racial bonuses
├── data/           - Persistent data management
├── command/        - Custom commands
├── effect/         - Custom status effects
├── entity/         - Custom entities
├── gui/            - User interface screens
├── item/           - Custom items (tokens, tomes, food)
├── mixin/          - Mixins for modifying game behavior
├── potion/         - Custom potions
└── quest/          - Quest system implementation
    ├── objectives/ - Quest objective types
    ├── rewards/    - Quest reward types
    └── registry/   - Quest registration

data/dag011/        - Datapack integration
├── structures/     - World-generated structures
├── worldgen/       - World generation configs
└── functions/      - Datapack functions
```

## Credits

- **Mod Developer**: hitman20081
- **Contact**: deadactiongaming@gmail.com
- **Repository**: [GitHub](https://github.com/hitman20081/DAGMod)

## License

This project is licensed under the terms specified in the LICENSE file.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## Roadmap

Future planned features:
- Additional quest types and chains
- More class abilities and specializations
- Race-specific quests and storylines
- Custom structures and dungeons
- Boss encounters
- Reputation system with factions
- Magic spell system expansion
- Guild system

## Support

For bug reports, feature requests, or questions:
- Open an issue on [GitHub](https://github.com/hitman20081/DAGMod/issues)
- Contact: deadactiongaming@gmail.com

---

**Choose your race, select your class, unlock powerful synergies, and embark on an epic fantasy RPG adventure in Minecraft!**

Key additions:
- Updated race bonuses with gathering percentages
- Added synergy abilities section
- Added Hall Locator feature
- Added persistent data storage note
- Updated recommended combinations with synergy details
- Added data/ folder to project structure
- More detailed getting started guide