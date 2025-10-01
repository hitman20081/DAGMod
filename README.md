# DAGMod - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft using Fabric, featuring an intricate quest system, class progression, custom items, and immersive gameplay mechanics.

## Features

### Quest System
- **Multi-tier Quest Books**: Progress from Novice to Master level
- **Quest Chains**: Interconnected storylines that guide player progression
- **Multiple Quest Types**:
    - Fetch Quests - Gather specific items
    - Kill Quests - Defeat enemies
    - Delivery Quests - Transport items to NPCs
    - Crafting Quests - Create specific items
    - Collection Quests - Gather diverse materials
- **Quest Blocks**: Interactive blocks for accepting and completing quests
- **Dynamic Quest Tracking**: Real-time progress monitoring
- **Quest Book Upgrades**: Unlock higher-tier quests by completing challenges

### RPG Class System
- **Three Distinct Classes**:
    - **Warrior**: High health, melee damage bonuses, heavy armor proficiency
    - **Mage**: Enhanced potion effects, spell-like abilities, mana system
    - **Rogue**: Speed bonuses, critical hit chance, stealth mechanics
- **Class Selection Altar**: Choose your path at the start of your journey
- **Class-Specific Abilities**: Unique powers for each class
- **Class Reset Crystal**: Allows players to change their chosen class

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

1. **Begin Your Journey**: Look for a Quest Block in your world or craft one
2. **Choose Your Class**: Find or craft a Class Selection Altar to select your character class
3. **Start Questing**: Right-click the Quest Block to view and accept quests
4. **Level Up**: Complete quests to earn rewards and upgrade your quest book
5. **Master Your Class**: Use class-specific abilities and develop your character

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