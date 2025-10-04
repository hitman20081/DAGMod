# DAGMod - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft using Fabric, featuring an intricate quest system, class progression, custom items, and immersive gameplay mechanics.

## Features

### Quest System
- **Multi-tier Quest Books**: Progress from Novice to Master level
- **Quest Chains**: Interconnected storylines that guide player progression
- **Multiple Quest Types**:
    - Fetch Quests - Gather specific items
    - Kill Quests - Defeat enemies (Work in Progress)
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
        - Enhanced spell-like abilities
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

### Race System (NEW!)
- **Four Playable Races**:
    - **Human - The Balanced**:
        - Jack of all trades
        - Can gather all resources
        - Versatile gameplay with no penalties
    - **Dwarf - The Miner**:
        - +20% mining speed
        - +1 heart (+2 health)
        - -5% movement speed
        - Expert at mining rare ores
        - Born for the depths
    - **Elf - The Ranger**:
        - +15% movement speed
        - +0.5 block interaction range
        - Expert at woodcutting and hunting
        - One with nature
    - **Orc - The Warrior**:
        - +15% melee attack damage
        - +2 hearts (+4 health)
        - Expert at hunting and fishing
        - Fierce and strong
- **Race Selection Altar**: Discover your heritage at the Hall of Champions
- **Race Selection Tome**: Learn about each race before making your choice
- **Race + Class Combinations**: Stack racial bonuses with class abilities for unique builds
- **Permanent Heritage**: Race selection cannot be changed once chosen

### World Generation
- **Hall of Champions**: Spawns naturally in new worlds
    - Contains both Race and Class Selection Altars
    - Generates on solid ground in the Overworld
    - Your journey begins here

### Custom Items & Food
Dozens of custom fantasy-themed items including:
- **Quest Books**: Novice, Apprentice, Expert, and Master tiers
- **Class Tokens**: Warrior, Mage, and Rogue tokens
- **Crafting Materials**: Various powders (Diamond, Emerald, Amethyst, Quartz), Echo Dust, Slimeball Dust
- **Fantasy Foods**:
    - Elven Bread, Mystic Stew, Phoenix Roast
    - Dragonfruit Tart, Frostberry Pie, Pumpkin Parfait
    - Void Truffles, Ethereal Cookies, Shadow Cake
    - Nether Salad, Crimson Soup, Molten Chili
    - And many more delicious items!

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

## Installation

### Requirements
- Minecraft (version specified in fabric.mod.json)
- Fabric Loader
- Fabric API

### Installation Steps
1. Download the latest release from the [Releases](https://github.com/hitman20081/DAGMod/releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Launch Minecraft with Fabric Loader
4. Enjoy your fantasy RPG adventure!

## Getting Started

1. **Create a New World**: Generate a new world to find the Hall of Champions
2. **Discover Your Heritage**: Find the Hall of Champions structure and approach the Race Selection Altar
3. **Choose Your Race**: Read the Race Selection Tome and select from Human, Dwarf, Elf, or Orc
4. **Choose Your Class**: Approach the Class Selection Altar and select Warrior, Mage, or Rogue
5. **Begin Your Journey**: Look for Quest Blocks or craft one to start questing
6. **Level Up**: Complete quests to earn rewards and upgrade your quest book
7. **Master Your Build**: Use race and class synergies to create your unique playstyle

## Recommended Race + Class Combinations

- **Dwarf Warrior**: Ultimate tank with massive health and mining prowess
- **Elf Rogue**: Lightning-fast assassin with incredible mobility
- **Orc Warrior**: Raw damage powerhouse with devastating melee attacks
- **Human Mage**: Balanced spellcaster with maximum versatility
- **Elf Mage**: Mobile magic user excelling at ranged combat
- **Dwarf Mage**: Tanky spellcaster who can survive in the depths

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
├── block/          - Custom blocks
├── class_system/   - RPG class mechanics
├── command/        - Custom commands
├── effect/         - Custom status effects
├── entity/         - Custom entities
├── gui/            - User interface screens
├── item/           - Custom items
├── mixin/          - Mixins for modifying game behavior
├── potion/         - Custom potions
└── quest/          - Quest system implementation
    ├── objectives/ - Quest objective types
    ├── rewards/    - Quest reward types
    └── registry/   - Quest registration
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
- Custom structures and dungeons
- Boss encounters
- Reputation system with factions
- Magic spell system expansion

## Support

For bug reports, feature requests, or questions:
- Open an issue on [GitHub](https://github.com/hitman20081/DAGMod/issues)
- Contact: deadactiongaming@gmail.com

---

**Enjoy your fantasy RPG adventure in Minecraft!**