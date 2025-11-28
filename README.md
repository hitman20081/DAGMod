# DAGMod v1.5.3-beta - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft 1.21 using Fabric, featuring a dynamic quest system, player progression, class mechanics, race selection, custom abilities, procedurally generated dungeons, and immersive gameplay.

## Core Features

### 🎭 Race & Class System
Choose from **4 races** and **3 classes** for unique playstyles:

**Races**: Human, Dwarf, Elf, Orc
- Each race has unique passive bonuses (mining speed, movement, damage, etc.)
- Race-specific gathering bonuses (ore drops, wood, meat, XP)
- 9 unique race+class synergies with special abilities

**Classes**: Warrior, Mage, Rogue
- **Warrior**: 5 abilities - devastating AoE, self-healing, damage reduction
- **Mage**: 4 permanent abilities + wands & scrolls - homing missiles, time warp, explosions, barriers
- **Rogue**: 7 abilities (dual system) + passive backstab - stealth, teleportation, poisons, assassination

### 📊 Progression System
- **50 levels** with exponential XP scaling
- Earn XP from mining, combat, quests, farming, fishing
- **Stat bonuses every 10 levels**: +1 HP, +0.5 Attack, +1 Armor
- Visual XP bar HUD with real-time progress tracking
- Level requirements for quest access (Novice→Apprentice→Expert→Master)

### 📜 Quest System
**64+ Quests** across multiple categories:

**Tutorial System - Innkeeper Garrick**:
- **Pre-Quest Tutorial**: Complete 3 tasks to earn your Quest Book
  - **Task 1**: Gather 10 Oak Logs (teaches resource gathering)
  - **Task 2**: Defeat 5 hostile mobs (teaches combat)
  - **Task 3**: Bring 1 Iron Ingot (teaches mining/smelting)
- Each task rewards a Quest Note (3 total needed)
- Take all 3 notes to a Quest Block to combine into Novice Quest Book
- Quest system remains locked until you obtain the Quest Book
- Find Garrick at an inn or tavern to begin your adventure

**Quest Block** (Story & Side Content):
- Ornate bookshelf-like blocks where you accept quests
- MAIN category: Story progression quests
- SIDE category: Optional content
- CLASS category: Class-specific challenges
- Combines Quest Notes into Quest Book (tutorial completion)

**Job Board** (Quick Work):
- Wall-mounted boards for quick tasks
- JOB category: Simple gathering/combat tasks
- DAILY category: Repeatable quests (coming soon)
- Perfect for earning quick XP and resources

**Quest Features**:
- 4 difficulty tiers with level requirements (Novice, Apprentice, Expert, Master)
- Multiple objective types (collect, kill, explore, craft, deliver)
- XP and item rewards
- 40 race-specific quest chains
- Progress tracking and turn-in systems via Quest Book
- Real-time progress updates

### ⚔️ Combat & Abilities

**Warrior Abilities**:
- **Battle Standard**: Heal 6 hearts + Strength II + Regeneration II
- **Whirlwind Axe**: 360° AoE spin attack (5 block radius)
- **Iron Talisman**: Resistance III tank mode
- **Plus 2 more abilities**

**Mage Abilities**:
- **Arcane Orb**: 5 homing magic missiles
- **Temporal Crystal**: AoE time warp (Slowness IV)
- **Mana Catalyst**: Massive 8-block explosion
- **Barrier Charm**: 10 absorption hearts shield

**Rogue Abilities** (Dual System):
- **Energy-based** (Tome): Smoke Bomb, Poison Dagger, Shadow Step
- **Cooldown-based** (Items): Void Blade (teleport), Vanish (invisibility), Poison Vial, Assassin's Mark
- **Passive**: Backstab damage (1.5x from behind)

### 🏰 World Content

**Village NPC Structures**:
- Naturally generated village clusters in temperate biomes
- **Village Inn & Tavern** - Home of Innkeeper Garrick (tutorial NPC)
- **Village Shops** - Two unique shop buildings
- **Village Docks** - Waterfront structure
- Spawns in plains, forests, meadows, taiga, and river biomes
- Structures cluster together using jigsaw generation
- Tutorial starting point - find Garrick to begin your adventure

**Bone Realm Boss System**:
- 4-tier boss hierarchy with summoning mechanics
- Skeleton King → Skeleton Lords → Summoners → Bonelings
- Epic loot and boss-specific drops
- Locked treasure chests with key system

**Bone Dungeons**:
- Procedurally generated underground structures (15 blocks below surface)
- 10 unique room types (hallways, crossways, treasure rooms)
- Custom loot tables with themed rewards
- Natural terrain integration
- Accessible entryway for discovery

**Hall of Champions**:
- Surface structure containing Race & Class Selection Altars
- Visually distinct altars with themed textures
- Starting location for new players
- Hall Locator item guides you there

### 🎒 Items & Consumables

**50+ Custom Items** including:
- Class ability items (Battle Standard, Arcane Orb, Void Blade, etc.)
- 15 consumable powders (Shadow Blend, Fortune Dust, Cooldown Elixir, etc.)
- 6 themed armor sets with progressive bonuses
- 9 themed weapons and 9 shields
- Wands and spell scrolls for Mages
- Fantasy foods and potions

### 🎨 Synergy System
9 unique race+class combinations with special abilities:
- **Dwarf Warrior**: Resistance underground
- **Elf Rogue**: Invisibility in forests while sneaking
- **Orc Warrior**: Berserker rage at low health
- **Human Mage**: Random regeneration bursts
- **Dwarf Mage**: Permanent fire resistance
- **Elf Mage**: Haste in forest biomes
- **Orc Rogue**: +20% backstab damage
- **Human Warrior**: Absorption hearts when damaged
- **Human Rogue**: Random jump boost

## Getting Started

1. **Install Prerequisites**:
    - Minecraft 1.21
    - Fabric Loader
    - Fabric API

2. **Install DAGMod**:
    - Download latest release
    - Place in `mods` folder
    - Launch Minecraft with Fabric

3. **Begin Your Adventure**:
    - Spawn with a Hall Locator
    - Find the Hall of Champions structure
    - Choose your race at the Race Selection Altar (ancient stone design)
    - Choose your class at the Class Selection Altar (dark blackstone with enchanting table-top)
    - **Find Innkeeper Garrick** at a Village Inn/Tavern to begin the tutorial
      - Village structures spawn in temperate biomes (plains, forests, meadows, taiga)
      - Look for clustered buildings near rivers and flat terrain
    - **Complete 3 Tutorial Tasks**:
      - Task 1: Gather 10 Oak Logs → Garrick's First Note
      - Task 2: Defeat 5 hostile mobs → Garrick's Second Note
      - Task 3: Bring 1 Iron Ingot → Garrick's Third Note
    - **Combine Quest Notes** at a Quest Block → Novice Quest Book
      - Quest Block has ornate bookshelf design with chiseled top
    - Now you can accept quests, track progress, and level up!

## Progression Tips

### Early Game (Levels 1-10)
- **First Priority**: Complete Garrick's 3 tutorial tasks to get Quest Book
- Focus on mining and basic Novice quests
- Explore to find Quest Blocks and Job Boards
- Complete race-specific quest chains
- Gather materials for consumables
- Tutorial tasks teach you gathering, combat, and mining/smelting basics

### Mid Game (Levels 10-25)
- Unlock Apprentice (level 5) and Expert (level 15) quests
- Combat becomes more rewarding with better XP
- Craft class-specific ability items
- Explore Bone Dungeons for loot

### Late Game (Levels 25-50)
- Master quests provide huge XP rewards (2500 XP)
- Boss fights: Ender Dragon (2000 XP), Wither (1500 XP), Warden (1000 XP)
- Complete race quest chains for legendary rewards
- Master your class abilities and synergies

### XP Sources (Best to Worst)
1. **Quest Completion**: 200-2500 XP (highest value)
2. **Boss Fights**: 1000-2000 XP
3. **Mining Rare Ores**: Ancient Debris (40 XP), Diamond (25 XP)
4. **Combat**: 10-45 XP per mob
5. **Gathering**: 1-5 XP per action

## Recommended Race + Class Combos

**For Tanks**:
- **Dwarf Warrior**: Ultimate survivability with resistance underground

**For DPS**:
- **Orc Warrior**: Berserker damage with rage mode
- **Elf Rogue**: Speed + forest invisibility for hit-and-run

**For Magic**:
- **Human Mage**: Versatile with fast XP gain
- **Elf Mage**: Enhanced mobility with forest haste

**For Stealth**:
- **Elf Rogue**: Best stealth with forest invisibility
- **Orc Rogue**: Brutal backstabs (1.5x + 20% extra)

**For Beginners**:
- **Human** (any class): +25% XP gain for faster progression

## Development

### Building from Source
```bash
./gradlew build
```
Compiled mod will be in `build/libs/`

### Project Structure
```
src/main/java/com/github/hitman20081/dagmod/
├── block/          - Quest Block, Job Board, Altars
├── class_system/   - Class abilities and mechanics
├── race_system/    - Race selection and bonuses
├── progression/    - Level system, XP, stats
├── quest/          - Quest system and objectives
├── item/           - Custom items and consumables
├── entity/         - Boss entities and NPCs
└── bone_realm/     - Bone Realm content

data/dagmod/
├── structures/     - World structures
├── loot_table/     - Custom loot
└── worldgen/       - Generation configs
```

## Version History

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

**Latest**: v1.5.3-beta - CRITICAL: Fixed quest progression blocker at level 20 (circular dependency in quest book upgrade system), improved quest book upgrade information display

## Roadmap

See [ROADMAP.md](ROADMAP.md) for detailed development roadmap.

### v1.6.0 (Next Release)
- Enhanced consumable effects (lifesteal, dodge system, spell modifications)
- Improved consumable balance and clarity

### v1.7.0 (Planned)
- Additional spell scrolls and crafting recipes
- Expanded magic system for Mages

### v1.8.0 (Planned)
- Daily repeatable quest system
- Quest board rotation mechanics

### v1.9.0 (Planned)
- Custom enchantments (race/class specific)
- Expanded gear progression

### v2.0.0 (Major Release)
- Boss encounters in Bone Dungeons
- Complete race-specific quest chains
- Removes beta tag

### Future Features (v2.1.0+)
- Guild/Party system enhancements
- Economy & Trading system
- Reputation system with factions
- PvP arena
- Endgame expansion (v3.0.0)

## Known Issues

Some consumables use placeholder effects pending full implementation:
- Vampire Dust: Temporary regeneration (lifesteal planned for v1.6.0)
- Phantom Dust/Perfect Dodge: Temporary resistance (dodge system planned for v1.6.0)
- Spell Echo/Overcharge Dust: Limited functionality (spell system integration planned for v1.7.0)
- Last Stand Powder: Temporary absorption (death prevention planned for v1.6.0)
- Time Distortion: Player-only effect (AoE planned for v1.6.0)

See [GitHub Issues](https://github.com/hitman20081/DAGMod/issues) for complete bug tracking.

## Support & Contributing

**Bug Reports & Features**: [Open an issue on GitHub](https://github.com/hitman20081/DAGMod/issues)

**Contact**: deadactiongaming@gmail.com

**Repository**: [GitHub](https://github.com/hitman20081/DAGMod)

Contributions welcome! Feel free to submit pull requests.

## Credits

**Mod Developer**: hitman20081  
**License**: See LICENSE file

---

**Choose your race, select your class, master powerful abilities, explore dungeons, level up through epic quests, and embark on a complete fantasy RPG experience in Minecraft!**