# DAGMod v1.4.4-beta - Fantasy RPG Minecraft Mod

A comprehensive fantasy RPG modification for Minecraft using Fabric, featuring an intricate quest system, progression system, class mechanics, race selection, synergy abilities, custom items, procedurally generated dungeons, and immersive gameplay mechanics.

## What's New in v1.4.4-beta

### ⚔️ **Warriors - 3 New Abilities**

**1. Battle Standard** (Battle Shout ability)
- Powerful self-healing and combat buff activation
- **Healing**: Restores 6 hearts instantly
- **Strength II** for 10 seconds - devastating damage output
- **Regeneration II** for 8 seconds - sustained healing
- **Cooldown**: 45 seconds
- Perfect for turning the tide of difficult battles

**2. Whirlwind Axe** (Whirlwind ability)
- Execute a devastating 360° spin attack
- Hits **all enemies** within 5 block radius
- **8 damage** per enemy hit
- Applies knockback and slowness to affected targets
- Epic particle effects during the spin
- **Cooldown**: 30 seconds
- Ideal for crowd control and AoE damage

**3. Iron Talisman** (Iron Skin ability)
- Transform into an unstoppable tank
- **Resistance III** for 12 seconds - massive damage reduction
- 50% reduced knockback resistance
- Creates a protective damage reduction shield
- **Cooldown**: 60 seconds
- Essential for surviving boss encounters and tough fights

**Warriors now have 5 total abilities** - combining devastating offense with incredible survivability!

---

### 🔮 **Mages - 4 New Abilities**

**1. Arcane Orb** (Arcane Missiles ability)
- Unleash a barrage of homing magic missiles
- Fires **5 missiles** that automatically seek targets
- **4 damage per missile** (20 total damage potential)
- Auto-targets the nearest enemy within range
- Beautiful purple particle trails
- **Cooldown**: 20 seconds
- Excellent for consistent ranged damage

**2. Temporal Crystal** (Time Warp ability)
- Manipulate time itself to control the battlefield
- Slows **all enemies** within 10 block radius
- **Slowness IV** for 8 seconds - extreme movement reduction
- Mass crowd control for overwhelming situations
- Stunning blue time particle effects
- **Cooldown**: 40 seconds
- Perfect for kiting dangerous mobs or escaping

**3. Mana Catalyst** (Mana Burst ability)
- Detonate a massive magical explosion
- **8 block radius** AoE damage
- **12 damage** to all enemies in range
- Strong knockback effect
- Spectacular cyan explosion particles
- **Cooldown**: 35 seconds
- Ultimate room-clearing ability

**4. Barrier Charm** (Arcane Barrier ability)
- Conjure a protective magical shield
- Grants **10 absorption hearts** (5 full hearts)
- Lasts 30 seconds or until depleted
- Light blue barrier particles surround you
- **Cooldown**: 50 seconds
- Essential defensive tool for glass cannon mages

**Mages now have 4 permanent abilities** plus wands and scrolls for incredible versatility!

---

### 🗡️ **Rogues - 4 New Abilities**

**Important**: These abilities use **cooldown-based activation** (individual items), separate from the existing **energy-based** Rogue Ability Tome system!

**1. Void Blade** (Blink Strike ability)
- Teleport behind your target for a surprise attack
- Instantly teleport behind nearest enemy within **15 blocks**
- Brief **invisibility** (2 seconds) after teleport
- **Speed boost** for quick followup
- Perfect for engaging or disengaging combat
- Dark purple particle effects
- **Cooldown**: 25 seconds

**2. Vanish Cloak** (Vanish ability)
- Disappear completely from the battlefield
- **Invisibility** for 8 seconds
- **Speed II** for 8 seconds - enhanced mobility
- **Blinds nearby enemies** (6 block radius) for 4 seconds
- Perfect escape tool when overwhelmed
- Gray smoke cloud on activation
- **Cooldown**: 40 seconds

**3. Poison Vial** (Poison Strike ability)
- Apply deadly toxins to your target
- **Poison IV** for 8 seconds (~16 damage total)
- Also applies **Weakness II** and **Slowness I**
- **4 initial damage** on application
- Green poison particle effects
- **Cooldown**: 20 seconds
- Excellent for taking down tough single targets

**4. Assassin's Mark** (Assassinate ability)
- Execute a powerful manual backstab
- **20 damage (10 hearts)** when attacking from behind
- **10 damage (5 hearts)** from front as backup
- Red critical hit particles
- Blood effect particles on successful backstab
- **Cooldown**: 15 seconds
- Complements the passive backstab mechanic

**Rogues now have TWO ability systems:**
- **Energy-based** (Rogue Ability Tome): Smoke Bomb, Poison Dagger, Shadow Step
- **Cooldown-based** (Individual items): Blink Strike, Vanish, Poison Strike, Assassinate
- **Total**: 7 abilities + passive backstab mechanic for ultimate versatility!

---

## What's New in v1.4.3-beta

### 🏰 Bone Dungeon Structure System (NEW!)
Explore procedurally generated underground dungeons with random layouts!

**Features**:
- **Jigsaw-based generation** - Every dungeon is unique
- **10 structure pieces**: Entryway, Hallway, Corner, Crossway, Stairway, 3 Rooms, Treasure Room, Portal Room, Endcap
- **Custom loot tables** with themed rewards for each room type
- **Smart generation** - Entryway spawns near surface, chambers spread underground
- **Weighted spawning** - Balanced mix of corridors and treasure rooms
- **Natural integration** - Uses terrain adaptation to blend with the world

**Loot**:
- **Treasure Room**: Enchanted books, diamonds, emeralds, golden apples, rare gear
- **Portal Room**: Ender pearls, obsidian, eyes of ender, portal-themed items
- **Regular Rooms**: Bones, arrows, iron, gold, basic supplies

### ✨ Enhanced Consumables (IMPROVED!)
Three consumables now work properly with full functionality:

**Shadow Blend** (Rogue) - *Now fully functional!*
- Grants invisibility for up to 5 minutes
- **Invisibility breaks when you attack** - Perfect for stealth gameplay
- Sneak up on enemies, then strike to break stealth

**Fortune Dust** - *Now fully functional!*
- Tracks exactly **10 blocks mined**
- Applies **Fortune III bonus drops** to each block
- Counter displays blocks remaining after each mine
- Works on all ores for maximum efficiency

**Cooldown Elixir** (Warrior) - *Now fully functional!*
- Reduces **all active Warrior ability cooldowns by 30 seconds**
- Affects Rage, Shield Bash, and War Cry
- Shows number of cooldowns reduced
- Perfect for intense combat situations

### 🧹 Cleanup & Polish
- Removed deprecated dag010 datapack
- Cleaned up TODO comments and debug code
- Improved data tracking system (HashMap-based storage)
- Better event handling for consumable items

---

## Features

### Bone Realm Boss System
Face a complete boss hierarchy with chain-summoning mechanics and epic loot!

**Boss Hierarchy**:
```
Skeleton King (Epic Boss)
│
├── Skeleton Lord (Mini-Boss)
    │
    ├── Skeleton Summoner (Elite Mob)
        │
        ├── Boneling (Weak Minion)
```

---

#### **Boss Entities**

**1. Skeleton King** (Epic Realm Boss):
- **Stats**: 60 HP, 8 attack damage, 2.0 scale (double size!)
- **Appearance**: Full netherite armor with purple custom names
- **UI**: Purple boss bar - "Skeleton King"
- **Sounds**: Intimidating wither skeleton sounds
- **Behavior**: Never despawns, extremely dangerous
- **Loot**:
    - Full enchanted netherite armor (Protection V, Thorns III, Unbreaking III)
    - Netherite sword (Sharpness V, Looting III, Fire Aspect II, Unbreaking III)
    - 1 Nether Star (guaranteed!)
    - 1-2 Wither Skeleton Skulls
    - 3-6 Netherite Ingots
    - 10-20 Diamonds
- **Chest**: Drops Skeleton King Chest (requires key to unlock)
- **Location**: Will spawn in special structure (under development)

**2. Skeleton Lord** (Mini-Boss):
- **Stats**: 45 HP, 6 attack damage, 1.5 scale (50% larger)
- **Appearance**: Full diamond armor with red custom names
- **UI**: Red boss bar - "Skeleton Lord"
- **Behavior**: Summons up to 3 Skeleton Summoners during combat
- **Spawning**: Naturally in Bone Realm dimension
- **Loot**:
    - Full enchanted diamond armor (Protection IV, Thorns II, Unbreaking III)
    - Diamond sword (Sharpness IV, Looting II, Unbreaking III)
    - 5-15 Bones
    - 1-3 Diamonds
- **Chest**: Drops Bone Realm Locked Chest (requires key)

**3. Skeleton Summoner** (Elite Mob):
- **Stats**: 30 HP, 4 attack damage, 1.1 scale (slightly larger)
- **Appearance**: Iron helmet + gray leather robes
- **Behavior**: Summons up to 4 Bonelings every 6-12 seconds
- **Summoning**: Called by Skeleton Lords during battle
- **Effects**: Purple witch particles when summoning, evoker sounds
- **Drops**: Standard skeleton drops (no special loot)

**4. Boneling** (Weak Minion):
- **Stats**: 12 HP, 2.5 attack damage, 0.7 scale (small - 70% size)
- **Speed**: Very fast (35% movement speed)
- **Appearance**: Tiny skeletons with no armor
- **Sounds**: Higher-pitched skeleton sounds (1.4x pitch)
- **Lifetime**: Auto-despawns after 3 minutes
- **Effects**:
    - Ash particles while alive
    - Explodes into bone meal + soul particles on death
- **Drops**: Nothing (3 XP only)

---

#### **Locked Treasure Chests**

**Skeleton King Chest**:
- **Appearance**: Glowing bone-themed texture (light level 10)
- **Strength**: Nearly indestructible (50 hardness, 1200 blast resistance)
- **Key**: Skeleton King's Key (dropped by Skeleton King)
- **Unlock**: One-time unlock, then works as normal chest
- **Location**: Spawns at King's death location with epic particle effects

**Bone Realm Locked Chest**:
- **Appearance**: Eerie bone texture with dim glow (light level 5)
- **Strength**: Reinforced (5 hardness, 6 blast resistance)
- **Key**: Bone Realm Chest Key (from Skeleton Lord)
- **Unlock**: Key consumed on unlock (creative mode exempt)
- **Location**: Spawns at Lord's death location

**Chest Mechanics**:
- Completely locked until correct key is used
- Visual feedback: Particles when attempting to open
- Unlock effects: Epic particle pillar + dragon growl sound
- Wrong key: Clear feedback, no unlock
- Custom textures via advanced mixin rendering system

---

#### **Combat Strategy**

**Fighting Skeleton Lords**:
1. Focus fire on Summoners first - prevent Boneling swarms
2. Kite and use ranged attacks to manage adds
3. Lords summon Summoners every 10-15 seconds - be ready!
4. High armor and knockback resistance make them tanky

**Managing Summons**:
- Each Summoner spawns 4 Bonelings
- Lords can have up to 3 Summoners active (12+ Bonelings!)
- Bonelings despawn after 3 minutes if not killed
- AOE attacks very effective against Boneling swarms

**Boss Armor Benefits**:
- Significantly increases boss survivability
- High knockback resistance prevents cheese strategies
- Makes boss fights feel epic and challenging

**Preparation Tips**:
- Bring strong gear - full diamond/netherite recommended
- Potions of Strength and Regeneration help immensely
- Bow/crossbow for managing adds at range
- Shield for blocking powerful attacks
- Food for sustain during long fights

---

#### **Rewards**

**Why Fight Skeleton Lords?**
- Full enchanted diamond armor set
- Bone Realm Locked Chest with rare loot
- Practice for Skeleton King fight
- Good XP and materials

**Why Fight Skeleton King?**
- Full enchanted netherite armor (best in game!)
- Nether Star (required for beacons)
- Wither Skeleton Skulls (summon Wither boss)
- Massive diamond and netherite rewards
- Ultimate bragging rights

---


### Progression System
- **Level System (1-50)**: Gain experience through combat, mining, and quests
- **Automatic XP Gains**:
    - Mining ores: 5-40 XP (Diamond: 25 XP, Ancient Debris: 40 XP)
    - Killing mobs: 10-2000 XP (Zombie: 15 XP, Ender Dragon: 2000 XP)
    - Woodcutting: 2 XP per log
    - Farming: 1-5 XP per crop (Nether Wart: 5 XP)
    - Fishing: 2-15 XP (Fish: 2 XP, Treasure: 15 XP)
    - Quest completion: 200-2500 XP based on difficulty
- **Stat Scaling**: +1 HP, +0.5 attack, +1 armor per 10 levels
- **Visual HUD**: Real-time XP bar with level display
- **Level Gates**: Quests unlock at levels 1, 5, 15, and 25
- **Persistent Progress**: All data saves to world files

### Quest System
- **64 Total Quests** across 4 difficulty tiers
- **Tier Levels**: Novice (Lv1+), Apprentice (Lv5+), Expert (Lv15+), Master (Lv25+)
- **40 Race-Specific Quests** with unique storylines:
    - **The Forgemaster's Legacy** (Dwarf) - 10 quests
    - **Guardian of the Wilds** (Elf) - 10 quests
    - **Jack of All Trades** (Human) - 10 quests
    - **Path of the Warlord** (Orc) - 10 quests
- **Quest Types**: Gather, Kill, Explore, Craft
- **Progressive Rewards**: Items, XP, enchantments
- **Quest Tracking**: Active quest display and objective progress
- **Quest Block**: Central hub for quest management

### Class System
Choose from 3 distinct classes, each with unique abilities and playstyles:

#### ⚔️ **Warrior** - Tank and Melee Damage Dealer
- **Base Stats**:
    - +2 max hearts (4 HP)
    - +2 attack damage
    - +2 armor
    - 15% physical damage reduction
- **Class Abilities** (5 total):
    - **Rage**: +50% damage for 10 seconds (60s cooldown)
    - **Shield Bash**: 6 damage + knockback + stun (20s cooldown)
    - **War Cry**: AoE fear + weakness (45s cooldown)
    - **Battle Standard** ✨NEW: Self-heal + Strength II + Regeneration II (45s cooldown)
    - **Whirlwind Axe** ✨NEW: 360° spin attack, 8 damage AoE (30s cooldown)
    - **Iron Talisman** ✨NEW: Resistance III shield, 50% knockback reduction (60s cooldown)
- **Consumables**:
    - Cooldown Elixir: Reduces all ability cooldowns by 30 seconds
- **Playstyle**: Frontline tank with crowd control and sustain

#### 🔮 **Mage** - Spellcaster and Ranged DPS
- **Base Stats**:
    - +1 max heart (2 HP)
    - +50 max mana
    - Permanent Night Vision
- **Passive Abilities**:
    - 50% reduced enchantment costs
    - 50% longer potion durations
- **Class Abilities** (4 permanent abilities + spell scrolls + wands):
    - **Arcane Orb** ✨NEW: 5 homing missiles, 4 damage each (20s cooldown)
    - **Temporal Crystal** ✨NEW: AoE Slowness IV, 10 block radius (40s cooldown)
    - **Mana Catalyst** ✨NEW: Massive 8-block AoE explosion, 12 damage (35s cooldown)
    - **Barrier Charm** ✨NEW: 10 absorption hearts, lasts 30 seconds (50s cooldown)
- **Spell Scrolls**: Fireball, Lightning, Ice Blast (mana-based)
- **Wands**: Flame Wand, Frost Wand, Storm Wand, Shadow Wand (mana-based)
- **Consumables**:
    - Mana Potion, Arcane Essence, various magical powders
- **Playstyle**: Versatile spellcaster with offensive and defensive options

#### 🗡️ **Rogue** - Stealth and Critical Strikes
- **Base Stats**:
    - +1 attack damage
    - +1 max heart (2 HP)
    - 25% critical hit chance
    - 50% reduced fall damage
- **Backstab Mechanic**:
    - Passive: 1.5x damage from behind
    - Active (Assassin's Mark): 20 damage from behind, 10 from front
- **Energy-Based Abilities** (via Rogue Ability Tome):
    - **Smoke Bomb**: AoE blind + invisibility (30 energy)
    - **Poison Dagger**: Apply poison to target (40 energy)
    - **Shadow Step**: Short-range teleport (50 energy)
- **Cooldown-Based Abilities** ✨NEW (Individual items):
    - **Void Blade** (Blink Strike): Teleport behind enemy + invisibility (25s cooldown)
    - **Vanish Cloak** (Vanish): Invisibility + Speed II + blind enemies (40s cooldown)
    - **Poison Vial** (Poison Strike): Deadly Poison IV + Weakness + Slowness (20s cooldown)
    - **Assassin's Mark** (Assassinate): Manual backstab, 20 damage from behind (15s cooldown)
- **Total**: 7 abilities (3 energy + 4 cooldown) + passive backstab
- **Consumables**:
    - Shadow Blend, Perfect Dodge, various stealth-enhancing items
- **Playstyle**: High-risk, high-reward assassin with incredible mobility and burst damage

### Race System
Choose from 4 unique races with distinct bonuses and gathering abilities:

#### 🧑 **Human** - Versatile and Adaptable
- **Racial Bonuses**:
    - +25% experience gain from all sources
    - Well-rounded stat distribution
- **Gathering Bonus**: 25% XP bonus from all activities
- **Synergies**:
    - Human Warrior: Absorption hearts when taking damage
    - Human Mage: Random regeneration bursts
    - Human Rogue: Random jump boosts for mobility

#### ⛏️ **Dwarf** - Master Craftsmen and Miners
- **Racial Bonuses**:
    - +20% mining speed
    - +1 max heart (2 HP)
    - -5% movement speed
- **Gathering Bonus**: 15% chance for bonus ore drops when mining
- **Synergies**:
    - Dwarf Warrior: Resistance effect underground (below Y=50)
    - Dwarf Mage: Permanent fire resistance
    - Dwarf Rogue: Extra survivability from health bonus

#### 🏹 **Elf** - Swift and Graceful
- **Racial Bonuses**:
    - +15% movement speed
    - +0.5 block reach distance
    - Permanent Hero of the Village effect
- **Gathering Bonuses**:
    - 20% chance for bonus wood drops
    - 25% chance for extra items from leaves
- **Synergies**:
    - Elf Warrior: Balanced mobility tank
    - Elf Mage: Haste effect in forest biomes
    - Elf Rogue: Invisibility in forests when sneaking

#### 🗡️ **Orc** - Fierce Warriors and Hunters
- **Racial Bonuses**:
    - +15% melee damage
    - +2 max hearts (4 HP)
    - Improved fishing luck
- **Gathering Bonuses**:
    - 20% chance for bonus meat from hunting
    - 15% chance for bonus leather
- **Synergies**:
    - Orc Warrior: Berserker rage when below 30% health (Strength + Speed)
    - Orc Mage: Tanky spellcaster
    - Orc Rogue: +20% extra backstab damage (stacks with base backstab)

### Custom Items & Gear

#### Consumable Powders (15 types)
Class-specific consumables with powerful temporary effects:
- **Warrior Powders**: Berserker Blood, Titan Powder, Battle Dust, Last Stand Powder
- **Mage Powders**: Arcane Powder, Spell Echo, Overcharge Dust, Time Distortion
- **Rogue Powders**: Shadow Blend, Phantom Dust, Perfect Dodge
- **Universal Powders**: Vampire Dust, Fortune Dust, Haste Dust, Luck Dust

#### Armor Sets (6 complete sets)
Progressive armor tiers with mana/energy bonuses:
1. **Apprentice Set** (Leather): +10 mana, +5 energy per piece
2. **Adept Set** (Chainmail): +15 mana, +8 energy per piece
3. **Mystic Set** (Iron): +20 mana, +10 energy per piece
4. **Archmage Set** (Gold): +25 mana, +12 energy per piece
5. **Exalted Set** (Diamond): +30 mana, +15 energy per piece
6. **Ethereal Set** (Netherite): +40 mana, +20 energy per piece

Full set bonuses integrate with class abilities for enhanced power.

#### Weapons & Shields (18 items)
9 themed weapons with matching shields:
- **Flame Series**: Fire damage and ignition
- **Frost Series**: Slowness and ice effects
- **Storm Series**: Lightning and speed
- **Shadow Series**: Weakness and blindness
- **Radiant Series**: Healing and regeneration
- **Venom Series**: Poison damage
- **Earth Series**: Mining and resistance
- **Arcane Series**: Mana regeneration
- **Blood Series**: Lifesteal effects

**Weapon + Armor Synergy**: Wearing matching armor set enhances weapon abilities!

### World Generation

#### Hall of Champions
- Naturally spawning surface structure
- Contains Race Selection Altar and Class Selection Altar
- Given Hall Locator on first join to help find it
- Surface-adaptive placement for accessibility

#### Bone Dungeons ✨NEW
- Procedurally generated underground dungeons
- Jigsaw-based random layouts
- 10 unique structure pieces
- Custom loot tables with themed rewards
- Naturally integrated into terrain

### Bone Realm Dimension
Custom dimension with unique features:
- Bone-themed terrain and structures
- Skeleton Lord boss spawns naturally
- Exclusive materials and loot
- Challenging environment for high-level players

## Requirements

- **Minecraft Version**: 1.21.10
- **Mod Loader**: Fabric Loader 0.17.3+
- **Required Dependencies**:
    - Fabric API 0.135.0+1.21.10

## Installation

### Prerequisites
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.10
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) 0.135.0+1.21.10

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
    - **Mages**: Now have 4 powerful permanent abilities plus spell scrolls and wands for incredible versatility ✨
    - **Rogues**: Master TWO ability systems - energy-based tome abilities AND cooldown-based individual abilities for 7 total abilities ✨
    - **Warriors**: Dominate the battlefield with 5 powerful abilities including devastating AoE and incredible survivability ✨
6. **Explore Synergies**: Experiment with different race+class combinations for unique abilities
7. **Begin Your Journey**: Start gaining XP through mining, combat, and exploration
8. **Level Up**: Watch your character grow stronger with each level
9. **Begin Questing**: Find Quest Blocks to start your adventure and earn massive XP
10. **Progress Through Tiers**: Complete quests to unlock higher difficulties and better rewards
11. **Explore Dungeons**: Search for Bone Dungeons naturally generating underground *(NEW!)*

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

### Rogue Combinations Detailed
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
├── progression/    - Level system, XP, stat scaling
├── data/           - Persistent data management
├── command/        - Custom commands
├── effect/         - Custom status effects
├── entity/         - Custom entities
├── event/          - Event handlers (NEW in v1.4.3)
├── gui/            - User interface screens
├── item/           - Custom items (tokens, tomes, food)
├── mixin/          - Mixins for modifying game behavior
├── potion/         - Custom potions
└── quest/          - Quest system implementation
    ├── objectives/ - Quest objective types
    ├── rewards/    - Quest reward types
    └── registry/   - Quest registration

data/dag011/        - Datapack integration
├── structures/     - World-generated structures (NEW: Bone Dungeon)
├── worldgen/       - World generation configs
│   ├── template_pool/ - Jigsaw structure pools
│   └── structure/     - Structure definitions
├── loot_table/     - Custom loot tables (NEW: Dungeon chests)
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

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

## Roadmap

### v1.4.4-beta (CURRENT RELEASE - October 2025)
- ✅ 11 new class abilities (3 Warrior, 4 Mage, 4 Rogue)
- ✅ Warrior abilities: Battle Standard, Whirlwind Axe, Iron Talisman
- ✅ Mage abilities: Arcane Orb, Temporal Crystal, Mana Catalyst, Barrier Charm
- ✅ Rogue cooldown-based abilities: Void Blade, Vanish Cloak, Poison Vial, Assassin's Mark
- ✅ Dual ability system for Rogues (energy + cooldown)
- ✅ Custom textures and models for all new abilities

### v1.4.3-beta (October 2025)
- ✅ Bone Dungeon structure generation system
- ✅ Custom loot tables for dungeon chests
- ✅ Enhanced Shadow Blend (breaks on attack)
- ✅ Enhanced Fortune Dust (tracks 10 blocks)
- ✅ Enhanced Cooldown Elixir (reduces all cooldowns)
- ✅ Cleaned up deprecated code and TODOs
- ✅ Improved data tracking systems

### v1.4.2-beta (October 2025)
- ✅ Bone Realm Boss System
- ✅ Skeleton King, Skeleton Lord, and minion hierarchy
- ✅ Locked treasure chests with keys
- ✅ Boss summoning mechanics

### v1.4.0 (October 2025)
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
- Full implementation of placeholder consumables:
    - Vampire Dust: True lifesteal mechanic
    - Phantom Dust / Perfect Dodge: Actual dodge system
    - Spell Echo / Overcharge Dust: Spell modification
    - Last Stand Powder: Death prevention system
    - Time Distortion: AoE slow effect
- More spell scrolls for Mages
- Spell crafting recipes
- Custom enchantments (race/class specific)
- Additional armor sets
- More weapon synergies
- Boss encounters in Bone Dungeons

### Future Features
- Additional quest types and chains
- More class abilities and specializations
- Custom structures and dungeons
- Enhanced boss encounters with unique mechanics
- Reputation system with factions
- Guild/Party system
- PvP arena system
- Dimension expansion

## Known Issues

### v1.4.4-beta
- Some consumables use placeholder effects (marked in code):
    - Vampire Dust: Uses Regeneration (lifesteal planned for v1.5.0)
    - Phantom Dust / Perfect Dodge: Uses Resistance (dodge system planned)
    - Spell Echo / Overcharge Dust: Not fully functional (requires spell system integration)
    - Last Stand Powder: Uses Absorption (death prevention planned)
    - Time Distortion: Only affects player (AoE planned)

See GitHub Issues for complete bug tracking.

## Support

For bug reports, feature requests, or questions:
- Open an issue on [GitHub](https://github.com/hitman20081/DAGMod/issues)
- Contact: deadactiongaming@gmail.com

---

**Choose your race, select your class, master powerful abilities, explore procedurally generated dungeons, level up through epic adventures, and embark on a complete fantasy RPG experience in Minecraft!**