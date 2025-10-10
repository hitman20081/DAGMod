# DAGMod v1.3.1 - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft using Fabric, featuring an intricate quest system, progression system, class mechanics, race selection, synergy abilities, custom items, and immersive gameplay mechanics.

## Features

### Progression System (NEW in v1.3.0)
- **Level System (1-50)**: Gain experience through combat, mining, and quests
- **Automatic XP Gains**:
    - Mining ores: 5-40 XP (Diamond: 25 XP, Ancient Debris: 40 XP)
    - Killing mobs: 10-2000 XP (Zombie: 15 XP, Ender Dragon: 2000 XP)
    - Woodcutting: 2 XP per log
    - Farming: 1-3 XP per crop
    - Quest completion: 200-2500 XP based on difficulty
- **Level-Based Stat Scaling**:
    - +1 HP per level (49 bonus HP at max level)
    - +1 Attack Damage every 5 levels
    - +1 Armor every 10 levels
- **Visual Progression HUD**: Real-time XP bar and level display
- **Level Gates**: Quests locked behind level requirements
    - Apprentice quests: Level 5+
    - Expert quests: Level 15+
    - Master quests: Level 25+
- **Designed for 20+ hours** of engaging progression to reach max level

### Quest System
- **8 Quest Chains** with unique storylines and rewards:
    - **General Progression Chains** (4):
        - Adventurer's Path (3 quests) - Tutorial progression
        - Village Development (5 quests) - Settlement building
        - Master Craftsman (3 quests) - Crafting mastery
        - Combat Specialist (4 quests) - Combat prowess
    - **Race-Specific Chains** (4) - NEW in v1.3.0:
        - **The Forgemaster's Legacy** (Dwarf, 10 quests) - Mining and smithing mastery
        - **Guardian of the Wilds** (Elf, 10 quests) - Nature and archery path
        - **Jack of All Trades** (Human, 10 quests) - Versatile exploration and mastery
        - **Path of the Warlord** (Orc, 10 quests) - Combat and conquest
- **64 Total Quests** including:
    - 9 Class-specific quests (Warrior, Mage, Rogue)
    - 40 Race-specific quests with unique storylines
    - 15 General progression quests
- **Multiple Quest Types**:
    - Fetch Quests - Gather specific items
    - Kill Quests - Defeat enemies
    - Multi-objective Quests - Complex challenges
- **Quest Blocks**: Interactive blocks for accepting and completing quests
- **Dynamic Quest Tracking**: Real-time progress monitoring
- **Quest Book Upgrades**: Unlock higher-tier quests by completing challenges
- **Level-Gated Content**: Higher difficulty quests require appropriate levels

### RPG Class System
- **Three Distinct Classes**:
    - **Warrior**:
        - +4 hearts (+8 health)
        - +25% melee attack damage
        - -10% movement speed
        - 15% damage reduction from physical sources
        - Heavy armor proficiency
        - **3 class-specific quests** with unique storyline
  - **Mage**: (NEW in v1.3.1)
      - -2 hearts (-4 health)
      - -25% melee attack damage
      - **100 Mana Pool** with 2 mana/second regeneration
      - **7 Unique Spells** via spell scrolls
      - 50% reduced enchantment costs
      - 50% longer potion durations
      - Permanent night vision
      - **3 class-specific quests** with unique storyline
    - **Rogue**: (UPDATED in v1.4.0)
        - -1 heart (-2 health)
        - +30% movement speed
        - **100 Energy Pool** with 5 energy/second regeneration
        - **3 Unique Abilities** via Rogue Ability Tome
        - 25% critical hit chance (2x damage)
        - Backstab bonus (1.5x damage from behind)
        - 50% reduced fall damage
        - **3 class-specific quests** with unique storyline
- **Class Selection Altar**: Choose your path at the start of your journey
- **Class-Specific Abilities**: Unique powers and stat modifiers for each class
- **Class Reset Crystal**: Allows players to change their chosen class
- **Level-Based Class Reset**: Free class reset every 10 quests completed

### Spell System (Mage Class) (Updated in v1.3.1)
Mages harness arcane power through a mana system and spell scrolls:

**Mana Mechanics**:
- 100 maximum mana pool
- 2 mana regenerated per second
- Visual mana bar above hunger display
- Mana persists across sessions

**Available Spells**:
- **Heal** (20 mana): Instantly restore 3 hearts with healing particles
- **Fireball** (30 mana): Launch an explosive fireball projectile
- **Arcane Shield** (25 mana): Gain 4 absorption hearts for 30 seconds
- **Lightning Bolt** (35 mana): Summon lightning at your target (30 block range)
- **Frost Nova** (40 mana): Freeze and damage all enemies within 8 blocks
- **Blink** (30 mana): Teleport to targeted block (20 block range)
- **Mana Shield** (15 mana): Gain Resistance II for 10 seconds

**Spell Mechanics**:
- Spells only usable by Mage class
- Mana consumed only on successful cast
- Failed casts (invalid targets) don't consume mana
- Each spell has unique visual and audio effects
- Spell scrolls stackable (16 per stack)


### Rogue Ability System (NEW in v1.4.0)
Rogues harness agility and stealth through an energy system and the Rogue Ability Tome:

**Energy Mechanics**:
- 100 maximum energy pool
- 5 energy regenerated per second (faster than mana for active gameplay)
- Visual purple energy bar above hunger display (right side)
- Energy persists in memory during session

**Rogue Ability Tome**:
- Multi-ability item containing all 3 Rogue abilities
- **Shift + Right-Click**: Cycle between abilities
- **Right-Click**: Use currently selected ability
- Item name dynamically shows selected ability
- Always has enchanted glint effect

**Available Abilities**:

1. **Smoke Bomb** (40 energy, 20 second cooldown)
    - Creates smoke cloud at your location
    - Grants **Invisibility** for 6 seconds
    - Grants **Speed II** for 6 seconds
    - Smoke cloud lingers for 5 seconds (3 block radius)
    - Enemies in cloud get **Blindness II**
    - Breaks mob targeting for clean escapes
    - Perfect for: Escaping, repositioning, breaking combat

2. **Poison Dagger** (25 energy, no cooldown)
    - Buffs your next melee attack (5-second window)
    - Applies **Poison III** for 8 seconds (high damage over time)
    - Applies **Weakness II** for 6 seconds (reduces enemy damage)
    - Applies **Nausea** for 5 seconds (disorients target)
    - Green particle effects on weapon and target
    - Buff is consumed on successful hit
    - Perfect for: Burst damage, weakening tough enemies

3. **Shadow Step** (50 energy, 30 second cooldown)
    - Teleports to targeted block (25 block range)
    - **Can phase through walls** (unlike Mage Blink)
    - Leaves shadow decoy at starting position
    - Decoy lasts 3 seconds and draws mob aggro
    - Grants **Invisibility** for 3 seconds after teleport
    - Brief **invulnerability** during teleport (0.5s)
    - Perfect for: Escaping, flanking, repositioning, tactical plays

**Combat Mechanics**:
- Abilities only usable by Rogue class
- Energy consumed only on successful use
- Failed attempts (invalid targets) don't consume energy
- Invisibility breaks when attacking
- Each ability has unique visual and audio effects
- Poison Dagger integrates with backstab mechanics

**Strategic Gameplay**:
Rogues excel at hit-and-run tactics:
1. Use **Shadow Step** to position behind enemies
2. Activate **Poison Dagger** for enhanced damage
3. Execute backstab (1.5x damage + poison effects)
4. Throw **Smoke Bomb** to escape and reset
5. Energy regenerates quickly for next engagement

**Ability Synergies**:
- **Assassin Combo**: Shadow Step → Poison Dagger → Backstab → Smoke Bomb escape
- **Sustained Damage**: Poison Dagger → Normal attacks while poison ticks
- **Tactical Repositioning**: Smoke Bomb → Move while invisible → Shadow Step to new position
- **Boss Fights**: Shadow Step decoy draws aggro while you deal damage safely

### Consumable Powder System (NEW in v1.4.0)
Transform custom powders into powerful consumables for tactical advantages:

**Crafting Materials**:
- Amethyst Powder, Diamond Powder, Emerald Powder
- Echo Dust, Quartz Powder, Slimeball Dust
- Combined with vanilla materials for unique consumables

**Essential Consumables** (Uncommon - Stack 16):

**Resource Restoration**:
- **Mana Crystal** (Mage only) - Instantly restore 50 mana
    - Recipe: Amethyst Powder + Echo Dust + Amethyst Shard
- **Energy Tonic** (Rogue only) - Instantly restore 50 energy
    - Recipe: Quartz Powder + Slimeball Dust + Glass Bottle
- **Cooldown Elixir** (Warrior only) - Reduce all cooldowns
    - Recipe: Diamond Powder + Emerald Powder + Glass Bottle

**Combat Enhancers**:
- **Vampire Dust** - Lifesteal effect for 20 seconds
    - Recipe: Diamond Powder + Redstone + Rotten Flesh
- **Phantom Dust** - 50% dodge chance for 15 seconds
    - Recipe: Echo Dust + Phantom Membrane + Feather
- **Battle Frenzy** (Warrior only) - Strength, Speed, Haste for 15 seconds
    - Recipe: Diamond Powder + Redstone + Blaze Powder

**Utility Consumables**:
- **Fortune Dust** - Fortune III effect for mining
    - Recipe: Emerald Powder + Lapis Lazuli + Gold Nugget
- **Feather-fall Powder** - No fall damage for 60 seconds
    - Recipe: Slimeball Dust + Feather + Phantom Membrane
- **Shadow Blend** (Rogue only) - Invisibility until you attack
    - Recipe: Echo Dust + Coal + Fermented Spider Eye

**Class Power Boosts**:
- **Spell Echo** (Mage only) - Next spell casts twice
    - Recipe: Quartz Powder + Amethyst Powder + Echo Dust

**Advanced Consumables** (Epic - Stack 8):
- **Last Stand Powder** - Auto-revive if you die within 60 seconds
    - Recipe: Emerald Powder + Totem of Undying + Golden Apple
- **Time Distortion** - Slow motion effect for 10 seconds
    - Recipe: Echo Dust + Clock + Ender Pearl
- **Overcharge Dust** (Mage only) - Next spell has 2x power
    - Recipe: Diamond Powder + Amethyst Powder + Nether Star
- **Titan's Strength** (Warrior only) - +100% melee damage for 20 seconds
    - Recipe: Emerald Powder + Netherite Scrap + Blaze Rod
- **Perfect Dodge** (Rogue only) - 100% dodge chance for 10 seconds
    - Recipe: Amethyst Powder + Feather + Rabbit Foot

**Consumable Mechanics**:
- Right-click to consume and gain effect
- Class-locked consumables provide clear feedback
- Unique particle effects for each type
- Strategic resource management
- Craft in survival using custom powders

### Custom Armor Set Bonus System (NEW in v1.4.0)
Equip matching armor pieces to unlock powerful progressive bonuses!

**How It Works**:
- Wear 2 pieces of matching armor → Activate 2-piece bonus
- Wear all 4 pieces → Unlock 4-piece bonus (more powerful)
- Bonuses vary by class (Warrior, Mage, Rogue get different effects)
- Visual notifications appear when bonuses activate
- Remove armor → bonuses disappear instantly

---

#### **Armor Sets**

**🐉 Dragonscale Set** (Netherite - Epic):
- **Crafting**: Dragon Breath + Crying Obsidian + Netherite Armor
- **2-Piece Bonuses**:
    - Warrior: Resistance I (10% damage reduction)
    - Mage: Glowing + 15% reduced spell costs
    - Rogue: Speed I + 20% critical hit chance
- **4-Piece Bonuses**:
    - Warrior: Resistance II (20% damage reduction)
    - Mage: +50% mana regeneration (3 mana/second)
    - Rogue: Speed II + 25% energy regeneration (6.25 energy/second)

**💎 Crystalforge Set** (Diamond - Epic):
- **Crafting**: Amethyst Cluster + Quartz + Diamond Armor
- **2-Piece Bonuses**:
    - All Classes: Night Vision
    - Warrior: Absorption I
    - Mage: Regeneration I + 25% mana regeneration
    - Rogue: Jump Boost I + 15% energy regeneration
- **4-Piece Bonuses**:
    - All Classes: Glowing
    - Warrior: Absorption II
    - Mage: Regeneration II + 20% reduced spell costs
    - Rogue: Jump Boost II

**🔥 Inferno Set** (Iron - Epic):
- **Crafting**: Fire Charge + Blaze Powder + Iron Armor
- **2-Piece Bonuses**:
    - All Classes: Fire Resistance
    - Warrior: Strength I
    - Mage: Regeneration I + 10% reduced spell costs
    - Rogue: Speed I
- **4-Piece Bonuses**:
    - All Classes: Fire Resistance II + Glowing
    - Warrior: Strength II
    - Mage: Regeneration II
    - Rogue: Haste II

**🌿 Nature's Guard Set** (Leather - Epic):
- **Crafting**: Enchanted Golden Apple + Golden Carrot + Leather Armor
- **2-Piece Bonuses**:
    - All Classes: Regeneration I
    - Warrior: Resistance I
    - Mage: Luck I
    - Rogue: Jump Boost II
- **4-Piece Bonuses**:
    - All Classes: Regeneration II + Saturation
    - Warrior: Absorption II
    - Mage: Luck II + 30% mana regeneration
    - Rogue: Invisibility + 15% critical hit chance

**🌑 Shadow Set** (Leather - Epic):
- **Crafting**: Ender Pearl + Purpur Block + Leather Armor
- **2-Piece Bonuses**:
    - All Classes: Speed I + Night Vision
    - Mage: 10% reduced spell costs
    - Rogue: Invisibility + 25% critical hit chance
- **4-Piece Bonuses**:
    - All Classes: Speed II + Night Vision
    - Warrior: Resistance I
    - Rogue: Invisibility + Jump Boost II + 35% energy regeneration

**🍀 Fortuna Set** (Gold - Epic):
- **Crafting**: Rabbit Foot + Emerald + Gold Armor
- **2-Piece Bonuses**:
    - All Classes: Luck III + Speed I
- **4-Piece Bonuses**:
    - All Classes: Luck V + Hero of the Village + Speed II
    - Massive fortune for looting and trading!

---

### Weapon + Armor Synergy System (NEW in v1.4.0)
Combine matching weapons with armor sets for devastating bonuses!

**How It Works**:
- Wear 2+ pieces of armor set + wield matching weapon
- Unlock enhanced combat bonuses and effects
- 4-piece armor + weapon = maximum power
- "⚔️ Weapon Synergy Activated!" notification appears

---

#### **Weapon Synergies**

**🐉 Dragonscale Blade + Dragonscale Armor**:
- **2-Piece**: Strength I + 15% melee damage bonus
- **4-Piece**: Strength II + Fire Resistance + 30% melee damage
    - Warrior Bonus: Absorption II (extra survivability)

**🔥 Inferno Blade + Inferno Armor**:
- **2-Piece**: Strength I + 15% melee damage
- **4-Piece**: Strength II + Haste II + 25% melee damage
    - Fire-themed warrior build

**🌑 Shadow Blade/Phantom Blade + Shadow Armor** (Rogue):
- **2-Piece**: Speed II + Invisibility + **35% backstab damage** (total 2.05x)
- **4-Piece**: Speed III + Jump Boost III + Invisibility + **75% backstab damage** (total 2.45x!)
- **Orc Rogue Ultimate**: Orc (+20%) + Shadow 4pc (+75%) = **2.65x backstab damage!**

**🌿 Poison Fang Spear + Nature's Guard**:
- **2-Piece**: Regeneration II + Luck I
- **4-Piece**: Regeneration III + Luck II + Saturation
    - Sustain-focused melee build

**💎 Crystal Hammer + Crystalforge Armor**:
- **2-Piece**: Absorption II + Resistance I
- **4-Piece**: Absorption III + Resistance II + Jump Boost III
    - Tank build with mace smash attacks

---

#### **Backstab Damage Breakdown** (Rogue)
Stack bonuses for maximum assassination damage:
- **Base Rogue**: 1.5x backstab damage
- **Orc Rogue**: 1.7x damage (+20% racial bonus)
- **Shadow 2pc + Shadow Blade**: 2.05x damage (+35% synergy)
- **Shadow 4pc + Shadow Blade**: 2.45x damage (+75% synergy)
- **Orc + Shadow 4pc + Shadow Blade**: **2.65x damage** (ultimate assassin!)

---

### Themed Weapons & Shields (NEW in v1.4.0)

**Custom Weapons**:
- **Dragonscale Blade** (Netherite Sword) - Dragon-themed, fire aspect
- **Inferno Blade** (Iron Sword) - Fire aspect, magma-themed
- **Shadow Blade** (Netherite Sword) - Stealth-focused, movement bonuses
- **Phantom Blade** (Netherite Sword) - Reduced gravity, phantom-themed
- **Sword of the True King** (Netherite Sword) - Legendary balanced stats
- **Solar Bow** - Fire and piercing enchantments
- **Frostbite Axe** (Netherite Axe) - Ice-themed
- **Crystal Hammer** (Mace) - Enhanced density damage
- **Poison Fang Spear** (Trident) - Bane of Arthropods, poison-themed
- **Thunder Pike** (Trident) - Channeling, lightning-themed

**Custom Shields**:
- **Dragonbone Shield** - Explosion resistance, absorption
- **Blazing Aegis** - Fire-themed stats
- **Nature's Bulwark** - Nature-themed defense
- **Shadowguard Shield** - Movement efficiency bonus
- **Crystal Ward** - Absorption bonuses
- **Solar Protector** - Max health +3 hearts, attack speed
- **Frostbound Shield** - Cold-themed defense
- **Celestial Aegis** - High-tier universal shield
- **Stormguard Shield** - Storm-themed defense

All weapons and shields craftable in survival mode!

### Race System
- **Four Playable Races**:
    - **Human - The Balanced**:
        - +25% experience gain from all sources
        - Jack of all trades with no penalties
        - Versatile gameplay
        - **10-quest unique storyline**: "Jack of All Trades" - Master exploration, trading, and diverse skills
    - **Dwarf - The Miner**:
        - +20% mining speed
        - +1 heart (+2 health)
        - -5% movement speed
        - 15% chance for bonus ore drops when mining
        - **10-quest unique storyline**: "The Forgemaster's Legacy" - Master mining and ancient smithing
    - **Elf - The Ranger**:
        - +15% movement speed
        - +0.5 block interaction range
        - Permanent Hero of the Village effect
        - 20% chance for bonus wood drops
        - **10-quest unique storyline**: "Guardian of the Wilds" - Protect nature and master archery
    - **Orc - The Warrior**:
        - +15% melee attack damage
        - +2 hearts (+4 health)
        - 20% chance for bonus meat from hunting
        - Better fishing luck
        - **10-quest unique storyline**: "Path of the Warlord" - Conquer through strength and combat
- **Race Selection Altar**: Discover your heritage in the Hall of Champions
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
- **Spell Scrolls**: 7 unique mage spells (Heal, Fireball, Absorption, Lightning, Frost Nova, Blink, Mana Shield)
- **Rogue Ability Tome**: Multi-ability item with Smoke Bomb, Poison Dagger, and Shadow Step
- **Consumables**: 15 powder-based consumables for tactical advantages (NEW in v1.4.0)
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
- Player progression data (level, XP) persists between sessions
- Player data stored in world save files
- Seamless experience for server play

## Installation

### Requirements
- Minecraft 1.21.8
- Fabric Loader 0.17.2+
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
    - **Mages**: Use spell scrolls to cast powerful magic using your mana pool
    - **Rogues**: Use the Rogue Ability Tome to execute stealth abilities with your energy pool
    - **Warriors**: Utilize your enhanced combat stats and damage reduction
6. **Explore Synergies**: Experiment with different race+class combinations for unique abilities
7. **Begin Your Journey**: Start gaining XP through mining, combat, and exploration
8. **Level Up**: Watch your character grow stronger with each level
9. **Begin Questing**: Find Quest Blocks to start your adventure and earn massive XP
10. **Progress Through Tiers**: Complete quests to unlock higher difficulties and better rewards

## Progression Guide

### Leveling Tips
- **Early Game (Levels 1-10)**: Focus on mining and basic quests for steady XP
- **Mid-Game (Levels 10-25)**: Combat becomes more rewarding, unlock Apprentice and Expert quests
- **Late Game (Levels 25-40)**: Master quests provide huge XP rewards
- **End Game (Levels 40-50)**: Boss fights and difficult content for final levels

### XP Sources Ranked
1. **Quest Completion**: Highest XP per time (200-2500 XP)
2. **Boss Fights**: Ender Dragon (2000 XP), Wither (1500 XP), Warden (1000 XP)
3. **Mining Rare Ores**: Ancient Debris (40 XP), Diamonds (25 XP)
4. **Combat**: Consistent 10-45 XP per mob
5. **Gathering**: Passive XP while building (1-5 XP)

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
- **Rogue Combinations**:
- **Elf Rogue**: Ultimate stealth - forest invisibility, high speed, deadly backstabs
- **Orc Rogue**: Brutal assassin - extreme backstab damage (1.5x + 20%), extra health
- **Human Rogue**: Versatile scout - mobility boosts, fast leveling, balanced playstyle
- **Dwarf Rogue**: Unconventional pick - extra health for survivability, mining bonuses

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
├── progression/    - Level system, XP, stat scaling (NEW)
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

### v1.4.0 (RELEASED - October 2025)
- ✅ Rogue ability system (Smoke Bomb, Poison Dagger, Shadow Step)
- ✅ Energy system for Rogues with visual HUD
- ✅ Rogue Ability Tome with ability cycling
- ✅ 15 consumable powders with class-specific effects
- ✅ 6 custom armor sets with progressive bonuses
- ✅ Weapon + armor synergy system
- ✅ 9 themed weapons and 9 themed shields
- ✅ Integrated mana/energy bonuses from armor
- ✅ Spell cost reduction system
- ✅ Enhanced backstab damage stacking

### v1.5.0 (Planned)
- More spell scrolls for Mages
- Spell crafting recipes
- Custom enchantments (race/class specific)
- Additional armor sets
- More weapon synergies
- Enhanced consumable effects

### Future Features
- Additional quest types and chains
- More class abilities and specializations
- Custom structures and dungeons
- Boss encounters with unique mechanics
- Reputation system with factions
- Guild/Party system
- PvP arena system

### Future Features
- Additional quest types and chains
- More class abilities and specializations
- Ability cooldown systems
- Custom structures and dungeons
- Boss encounters with unique mechanics
- Reputation system with factions
- Guild/Party system

### Future Features
- Additional quest types and chains
- More class abilities and specializations
- Custom structures and dungeons
- Boss encounters with unique mechanics
- Reputation system with factions
- Magic spell system expansion
- Guild/Party system
- PvP arena system

## Support

For bug reports, feature requests, or questions:
- Open an issue on [GitHub](https://github.com/hitman20081/DAGMod/issues)
- Contact: deadactiongaming@gmail.com

---

**Choose your race, select your class, unlock powerful synergies, level up through epic adventures, and embark on a complete fantasy RPG experience in Minecraft!**