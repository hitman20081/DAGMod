# Changelog

All notable changes to DAGMod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.3-beta] - 2025-11-28

### Fixed - CRITICAL: Quest Progression Blocker at Level 20

**Circular Dependency in Quest Book Upgrade System**:
- **CRITICAL FIX**: Players can no longer get stuck at level 20 unable to progress
- Fixed circular dependency where final chain quests required EXPERT difficulty book to accept, but completing those quests was needed to unlock EXPERT book
- Changed 4 tier-unlocking quests from EXPERT → APPRENTICE difficulty:
  - `village_master` (Village Development chain → unlocks Expert Quest Book)
  - `shield_bearers_trial` (Warrior class chain)
  - `master_scrolls` (Mage class chain)
  - `echoes_of_the_deep` (Rogue class chain)

**Logic**: Quests that unlock a tier should be completable BEFORE you have that tier.

**Impact**:
- Village Development chain (5 quests) now fully completable with APPRENTICE book
- All 3 class chains now completable without EXPERT book requirement
- Players can reach Expert Quest Book tier at appropriate level progression
- Removes game-breaking progression wall at level 20

### Added - Quest Book Upgrade Information Display

**Enhanced Quest Block UI**:
- Added `showQuestBookUpgradeInfo()` method to Quest Block interactions
- Players now see which quest chain unlocks the next quest book tier
- Displays chain progress (e.g., "4/5 quests completed")
- Clear, actionable information replaces generic "upgrade available" message

**Before**:
```
⭐ Upgrade Quest Book (AVAILABLE!)
[No other information]
```

**After**:
```
📖 Next Quest Book: Expert Quest Book
   Complete: Village Development
   Progress: 4/5 quests
```

**Implementation Details**:
- Quest Block now queries `QuestChain.getRewardTier()` to identify tier-unlocking chains
- Displays real-time progress using `QuestChain.getChainProgress(playerData)`
- Shows upgrade message when chain is complete
- Provides clear instructions when chain is in progress

**Files Modified** (2 files):
- `quest/registry/QuestRegistry.java` - Changed 4 quests from EXPERT → APPRENTICE difficulty (lines 1050-1066, 1120-1135, 1189-1204, 1351-1363)
- `block/QuestBlock.java` - Added import for QuestChain, added showQuestBookUpgradeInfo() method (lines 515-556)

### Technical Notes

**Quest Chain Completion System**:
- Quest chains automatically reward quest book tier upgrades upon completion
- `QuestManager.checkChainCompletion()` triggers on last quest turn-in
- Auto-upgrades player's quest book tier and gives physical book item
- Three chains unlock tiers:
  - Adventurer's Path (3 quests) → APPRENTICE Book
  - Village Development (5 quests) → EXPERT Book
  - Master Craftsman (3 quests) → MASTER Book

**Testing Performed**:
- Verified all class chains completable with APPRENTICE book
- Verified Village Development chain completable without EXPERT book
- Confirmed Expert Quest Book unlocks correctly after Village Development
- Tested Quest Block upgrade information display accuracy

## [1.5.2-beta] - 2025-11-24

### Fixed - CRITICAL: Quest Persistence System

**Quest Data Loss Bug**:
- **CRITICAL FIX**: Quest progress is now saved to disk and persists across sessions
- Previously ALL quest data was lost on disconnect (in-memory only storage)
- Quest completion, active quests, quest book tier, and objective progress now saved to NBT files
- Storage location: `world/data/dagmod/quests/{uuid}.dat`

**Implementation Details**:
- Added `QuestStorage.java` - Complete NBT-based persistence system (246 lines)
- Quest data automatically saved on quest accept, quest completion, and player disconnect
- Quest data automatically loaded on player join
- Added `copy()` methods to `Quest`, `QuestObjective`, and all objective subclasses for per-player instances
- Modified `QuestManager` with save/load/clear methods for proper lifecycle management

**Files Modified** (12 files):
- **New**: `quest/QuestStorage.java` - NBT persistence layer
- `quest/QuestData.java` - Added persistence helper methods
- `quest/Quest.java` - Added deep copy() method
- `quest/QuestObjective.java` - Added abstract copy() method
- `quest/objectives/CollectObjective.java` - Implemented copy()
- `quest/objectives/KillObjective.java` - Implemented copy()
- `quest/objectives/TagCollectObjective.java` - Implemented copy()
- `quest/QuestManager.java` - Added save/load/clear methods, auto-save on accept/complete
- `class_system/rogue/EnergyManager.java` - Added clearPlayerData() alias
- `DagMod.java` - Added quest load on join, comprehensive disconnect handler with save + cleanup

### Fixed - Memory Leaks

**Player Data Cleanup**:
- Added comprehensive disconnect handler that saves and cleans up all player data
- Fixed memory leaks in `QuestManager`, `ManaManager`, `EnergyManager`, `CooldownManager`
- Player data now properly removed from static HashMaps on disconnect
- Prevents OutOfMemoryError on long-running servers with many players

**Implementation**:
- Centralized disconnect handler in `DagMod.java` (lines 394-418)
- Removed duplicate disconnect handler from `registerWarriorSystems()`
- All manager classes now properly clear player data on disconnect

### Fixed - Thread Safety Issues

**HashMap → ConcurrentHashMap Conversion**:
- **CRITICAL FIX**: Converted `HashMap` to `ConcurrentHashMap` in all manager classes
- Prevents `ConcurrentModificationException` crashes on busy servers
- Eliminates race conditions in mana/energy updates
- Prevents data corruption from simultaneous thread access

**Files Modified**:
- `class_system/mana/ManaManager.java` - Now uses `ConcurrentHashMap` for playerManaData and regenTicks
- `class_system/rogue/EnergyManager.java` - Now uses `ConcurrentHashMap` for playerEnergyMap and regenTickCounter

**Impact**:
- Server stability improved on multi-player servers (10+ concurrent players)
- Eliminates random crashes during high ability usage
- Thread-safe concurrent access to player data maps

### Technical Notes

**Save/Load Flow**:
1. Player joins → `QuestManager.loadPlayerQuestData()` called from `ServerEntityEvents.ENTITY_LOAD`
2. Quest accepted → Auto-save immediately
3. Quest completed → Auto-save immediately
4. Player disconnects → Save all data (race/class, quests, progression), then clear from memory

**Data Integrity**:
- Quest objective progress restored with correct current/required values
- Quest book tier persists (Novice/Apprentice/Expert/Master upgrades are permanent)
- Completed quest IDs and completion timestamps preserved
- Active quest state restored with all objective progress

**Testing Performed**:
- Quest completion persistence verified
- Quest book tier persistence verified
- Memory cleanup verified (no data remains after disconnect)
- Multi-session quest progress continuity verified

## [1.5.1-beta] - 2025-11-23

### Fixed - Tutorial & Quest System Bugs

**Tutorial Task Ordering**:
- Fixed tutorial tasks completing out of order
- Added prerequisite checks: Task 1 must complete before Task 2, Task 2 before Task 3
- Added Task 1 completion check to mob kill event handler (DagMod.java:584-586)

**Tutorial Task 2 Note Issue**:
- Fixed Task 2 not giving Garrick's Second Note to player
- Removed auto-complete logic from `PlayerDataManager.incrementTask2MobKills()` (line 389-395)
- Only Innkeeper Garrick dialogue now marks Task 2 complete and gives the note
- Added sequential task enforcement in `InnkeeperGarrickNPC` dialogue logic (lines 98-118)

**Quest Note Consumption**:
- Fixed Quest Notes not being consumed when creating Quest Book at Quest Block
- Replaced unreliable `.contains()` with custom `hasItemInInventory()` helper method (QuestBlock.java:499-507)
- Notes are now properly removed from inventory when combining

**Quest System**:
- Removed debug messages from QuestRegistry and QuestBlock
- Added CLASS category to Quest Block filter so class-specific quests appear (QuestBlock.java:176-181)
- Removed "The Beginning" quest due to buggy TagCollectObjective with ItemTags.LOGS

### Changed - Quest Reward Rebalancing

**Fixed Backwards Progression** (Stone/Leather → Iron/Chainmail minimum):
- **Equip Yourself**: Stone Sword + Stone Pickaxe → Iron Sword + Iron Pickaxe + Iron Axe
- **Ready for Adventure**: Leather Boots + Leather Helmet → Iron Boots + Iron Helmet + Beef Stew + Golden Apples
- **The Wanderer** (Human): Stone Sword + Stone Pickaxe → Iron Sword + Iron Pickaxe + Mystic Stew + Golden Apple Strudel
- **The Grunt** (Orc): Leather Chestplate → Chainmail Chestplate + Iron Axe + Savory Beef Roast + Molten Chili
- **Shadow's Calling** (Rogue): Leather Boots → Chainmail Boots + Golden Apples

**Added Custom Foods to Quest Rewards**:
- **Garrick's Welcome**: Added Honey Bread + Candied Apple (replaced plain Bread)
- **Apprentice of the Forge** (Dwarf): Added Beef Stew + Honey Bread
- **The Seedling** (Elf): Added Elven Bread + Glowberry Jam
- All custom foods are universal items (safe for any class to use)

**Design Philosophy**:
- NOVICE tier quests now give Iron/Chainmail minimum (no stone/leather gear)
- Race quests include culturally themed custom foods
- Class-specific items (scrolls, ability items) ONLY appear in matching CLASS quests
- Created `CUSTOM_ITEM_CATEGORIES.md` reference guide

### Removed
- **"The Beginning" quest**: Collection quest with 3 oak logs removed due to TagCollectObjective bugs
- Castle structure files: Removed castle_fort.nbt (12MB) and castle_large.nbt (2.3MB) to reduce mod size
- Kept castle_medieval.nbt and castle_pale_garden.nbt for future dimension implementations

## [1.5.0-beta] - 2025-11-19

### Added - Comprehensive Tutorial System

> **Note**: This version follows Semantic Versioning (MINOR bump from v1.4.5).
> New features added (task-based tutorial, quest gating, Quest Notes) warrant a MINOR version increment, not just a PATCH.

**Task-Based Tutorial System**:
- Players must complete 3 tasks before accessing the quest system
- **Task 1 (Resourcefulness)**: Gather 10 Oak Logs → Garrick's First Note
- **Task 2 (Courage)**: Defeat 5 hostile mobs → Garrick's Second Note
- **Task 3 (Dedication)**: Bring 1 Iron Ingot → Garrick's Third Note
- All 3 Quest Notes combine at Quest Block → Novice Quest Book
- Quest system remains locked until Quest Book is obtained
- Task progress persists across sessions via NBT data

**Innkeeper Garrick Tutorial NPC**:
- Comprehensive tutorial guide with state-based dialogue
- Different dialogue for each task phase (not started, in progress, complete)
- Real-time inventory checking for Task 1 and Task 3
- Event-based mob kill tracking for Task 2
- Clear, helpful instructions for each task
- Progress feedback and encouragement
- `/summon_garrick` command for testing/structure placement (OP level 2)
- NPC cannot despawn and is invulnerable to damage

**Quest Note Items**:
- 3 new items: Garrick's First Note, Second Note, Third Note
- Uncommon rarity, max stack 1
- Given as rewards for completing tutorial tasks
- Required to craft Novice Quest Book at Quest Block
- Custom item models and language entries

**Quest System Gating**:
- Quest Blocks locked until meeting Garrick
- Job Boards locked until meeting Garrick
- Quest Blocks require Quest Book to access quests
- Job Boards require Quest Book to access jobs
- Clear messaging at each gate explaining requirements
- Shows progress (e.g., "You have 2/3 Quest Notes")

**Quest Note Combination System**:
- Quest Block detects all 3 Quest Notes in inventory
- Automatically consumes notes and gives Novice Quest Book
- Celebratory dialogue on completion
- Tutorial completion unlocks full quest system

**Mob Kill Tracking**:
- Real-time hostile mob kill counter for Task 2
- Action bar progress display (e.g., "3/5 hostile mobs defeated")
- Completion notification when 5 kills reached
- Auto-saves progress after each kill
- Only counts hostile mobs (zombies, skeletons, creepers, etc.)

### Fixed - Critical Bugs

**Progression System**:
- **CRITICAL**: Fixed health/stats not persisting after player death
  - Extra hearts from leveling now persist through death and respawn
  - Attack damage bonuses now persist through death
  - Armor bonuses now persist through death
  - Stats are reapplied in `ServerPlayerEvents.AFTER_RESPAWN` handler
  - Progression stats from `StatScalingHandler` now properly reapply

**Code Cleanup**:
- Removed all debug messages from InnkeeperGarrickNPC
- Cleaned up debug output for production readiness

### Changed - User Experience

**Quest Block**:
- Updated texture to use multiple faces (ornate bookshelf design)
- Bottom: Oak planks
- Top: Chiseled bookshelf top
- Sides: Bookshelf and chiseled bookshelf variants
- More visually distinct from regular bookshelves

**Tutorial Flow**:
- No formal quests available until Quest Book obtained
- Garrick's tasks are simple errands, not formal quest system
- Tasks teach basic game mechanics (gathering, combat, mining/smelting)
- Progressive difficulty (easy → medium → harder)

### Technical - System Integration

**PlayerDataManager Updates**:
- Added `MET_GARRICK_KEY` NBT tracking for NPC interactions
- Added task completion tracking (Task 1, 2, 3)
- Added mob kill counter for Task 2
- In-memory cache with file persistence
- Auto-save on task completion and NPC interaction
- `hasCompletedAllTasks()` helper method

**Quest Block Updates**:
- Quest Note detection system
- `combineNotesIntoQuestBook()` method
- Quest Book requirement check (all tiers)
- Helpful progress messages for incomplete tutorials

**Job Board Updates**:
- Quest Book requirement check (all tiers)
- Clear tutorial progression messaging

**DagMod Main Class**:
- Added `/summon_garrick` command registration
- Added `registerTutorialMobKillTracking()` event handler
- Hostile mob kill tracking via `ServerLivingEntityEvents.AFTER_DEATH`
- Action bar progress feedback on kills

**Garrick NPC Architecture**:
- State-based dialogue system with 4 handler methods
- `handleFirstMeeting()` - Welcome and Task 1
- `handleTask1()` - Oak log inventory check
- `handleTask2()` - Mob kill progress check
- `handleTask3()` - Iron ingot inventory check
- `handleAllTasksComplete()` - Final instructions
- Clean separation of concerns

### Added - World Generation

**Village NPC Structures**:
- New village-like structure generation system with 5 buildings
- **Village Inn** - Large tavern building (primary Garrick spawn location)
- **Village Tavern** - Alternative Garrick spawn location
- **Village Shop 1** - First shop building
- **Village Shop 2** - Second shop building
- **Village Docks** - Waterfront structure
- Structures cluster together in small village groups (jigsaw system)
- Spawn in temperate biomes: plains, meadows, forests, taiga, rivers
- Excluded from harsh environments (desert, badlands, savanna)
- Spacing: 28 chunks apart with 10 chunk minimum separation
- Template pool system for varied village layouts

**Structure Overlap Prevention**:
- Added exclusion zones to all 11 structure sets to prevent overlapping
- Bone Dungeons: 10 chunk exclusion from village NPCs
- Hall of Champions: 20 chunk exclusion from village NPCs
- Skeleton Kingdom: 12 chunk exclusion from village NPCs
- All 4 Castles: 25 chunk exclusion from village NPCs each
- Houses (NPC/Villager): 6-8 chunk exclusion from village NPCs
- Increased spacing on most structures for better world distribution
- Prevents structures from generating inside each other and causing air block conflicts

**Bone Dungeon Improvements**:
- Adjusted entryway depth from -5 to -15 blocks below surface
- Proper underground positioning while keeping entryway accessible
- Prevents surface structure overlap issues
- Maintains "discoverable underground dungeon" feel

### Changed - Visual Polish

**Quest Block Textures**:
- Multi-face design (changed from uniform bookshelf)
- Bottom: Oak Planks
- Top: Chiseled Bookshelf Top
- North/South: Bookshelf
- East/West: Chiseled Bookshelf Side
- More ornate and visually distinct appearance

**Race Selection Altar Textures**:
- Multi-face "Mystical/Ancient" theme
- Top: Chiseled Stone Bricks (decorative patterns)
- Bottom: Smooth Stone (solid foundation)
- North/South: Cracked Stone Bricks (ancient weathering)
- East/West: Stone Bricks (classic sides)
- Represents permanent, ancient racial choice

**Class Selection Altar Textures**:
- Multi-face "Ancient Power/Stronghold" theme
- Top: Enchanting Table Top (glowing magical runes)
- Bottom: Obsidian (dark power base)
- North/South: Polished Blackstone Bricks (refined stronghold)
- East/West: Chiseled Polished Blackstone (shield emblem)
- Dark blackstone theme represents strength and power

**Block Visual Differentiation**:
- Quest Block: Wood/Books theme (library aesthetic)
- Race Altar: Stone theme (ancient/permanent)
- Class Altar: Blackstone theme (power/stronghold)
- All three blocks now have completely distinct appearances

### Technical - World Generation Architecture

**Template Pool System**:
- Created `village_npc/town_center.json` - Inn/tavern as main anchor
- Created `village_npc/buildings.json` - Surrounding shops and docks
- Created `village_npc/terminators.json` - Structure endpoint pool
- Jigsaw structure size 4 allows 4 iterations from center
- `terrain_adaptation: beard_thin` for natural landscape blending

**Structure Set Configuration**:
- All structure sets now use `exclusion_zone` parameter
- Format: `{"other_set": "structure_set_name", "chunk_count": N}`
- Prevents structures within N chunks of other structure types
- Improved spacing/separation values across all structures

**Biome Tag System**:
- Created `has_structure/village_npc.json` biome tag
- Includes 12 temperate biome types
- Plains, forests, taiga, and river biomes only
- Excludes extreme/harsh biomes for immersive placement

---

## [1.4.5-beta] - 2025-10-29

### Added - Quest System Enhancements

**Job Board Block**:
- New Job Board block for quest categorization and organization
- Separate interface for JOB and DAILY category quests
- Independent turn-in system from Quest Block
- Wall-mountable with directional facing
- Custom voxel shape for proper collision detection

**Quest Categories**:
- MAIN - Main story progression quests (Quest Block)
- SIDE - Optional side content quests (Quest Block)
- CLASS - Class-specific quests (Quest Block)
- JOB - Simple gathering/combat tasks (Job Board)
- DAILY - Repeatable daily quests (Job Board, future use)

**Quest Block Improvements**:
- Quest Block now filters to show only MAIN and SIDE category quests
- Improved turn-in flow - completed quests now get priority
- Better menu state management
- Enhanced user feedback with clear quest categorization

### Changed - Quest System Organization

**Quest Categorization**:
- Added `.setCategory()` method to Quest class for proper categorization
- Quest Block and Job Board now filter quests by category
- Improved quest discovery - players can find story quests at Quest Blocks and work at Job Boards
- Better separation of quest types for clearer player experience

**Quest Turn-In Flow**:
- Quest Block immediately shows turn-in screen when completed quests are available
- No more cycling through menus to turn in completed quests
- Separate turn-in systems for Quest Block and Job Board
- Improved priority handling for completed quests

### Technical - Quest System Architecture

**Block System**:
- Created JobBoardBlock extending HorizontalFacingBlock
- Implemented menu state management system for Job Board
- Added quest filtering by category in both Quest Block and Job Board
- Proper blockstate, model, and item registration

**Code Organization**:
- Quest category enum in Quest.java
- Filtering logic in QuestBlock.java and JobBoardBlock.java
- Improved ModBlocks registration system
- Cleaned up duplicate registration methods

**Quest Registry**:
- Added quest categorization support throughout QuestRegistry
- Simple job quests ready for Job Board (gathering, hunting, basic tasks)
- Framework for daily repeatable quests

### Fixed
- Quest Block turn-in flow now properly prioritizes completed quests
- Removed debug code from JobBoardBlock
- Fixed block registration issues with proper registry key setting
- Cleaned up duplicate item registration

---

## [1.4.4-beta] - 2025-10-25

### Added - 11 New Class Abilities

#### ⚔️ **Warrior Abilities (3 new)**

**Battle Standard** (Battle Shout ability):
- **Healing**: Instantly restores 6 hearts (12 HP)
- **Strength II**: 10 seconds duration for devastating damage
- **Regeneration II**: 8 seconds of sustained healing
- **Cooldown**: 45 seconds
- Perfect for turning the tide of battle with self-sustain and damage boost
- Custom textures and epic visual effects

**Whirlwind Axe** (Whirlwind ability):
- **360° Spin Attack**: Hits all enemies within 5 block radius
- **Damage**: 8 damage per enemy hit
- **Effects**: Applies knockback and slowness to all targets
- **Cooldown**: 30 seconds
- Spectacular particle effects during execution
- Ideal for crowd control and AoE situations

**Iron Talisman** (Iron Skin ability):
- **Resistance III**: 12 seconds of massive damage reduction
- **Knockback Reduction**: 50% reduced knockback
- **Shield Effect**: Creates protective damage absorption
- **Cooldown**: 60 seconds
- Essential defensive tool for tanking boss encounters
- Custom protective particle effects

#### 🔮 **Mage Abilities (4 new)**

**Arcane Orb** (Arcane Missiles ability):
- **Homing Missiles**: Fires 5 auto-tracking magical projectiles
- **Damage**: 4 damage per missile (20 total potential)
- **Targeting**: Automatically seeks nearest enemy
- **Cooldown**: 20 seconds
- Beautiful purple particle trails
- Reliable ranged damage output

**Temporal Crystal** (Time Warp ability):
- **AoE Crowd Control**: Affects all enemies in 10 block radius
- **Slowness IV**: 8 seconds of extreme movement reduction
- **Cooldown**: 40 seconds
- Stunning blue time manipulation particle effects
- Perfect for kiting or escaping dangerous situations
- Mass crowd control tool

**Mana Catalyst** (Mana Burst ability):
- **Massive Explosion**: 8 block radius AoE damage
- **Damage**: 12 damage to all enemies in range
- **Knockback**: Strong knockback effect
- **Cooldown**: 35 seconds
- Spectacular cyan explosion particles
- Ultimate room-clearing ability

**Barrier Charm** (Arcane Barrier ability):
- **Absorption Shield**: Grants 10 absorption hearts (5 full hearts)
- **Duration**: Lasts 30 seconds or until depleted
- **Cooldown**: 50 seconds
- Light blue barrier particles surround player
- Essential defensive tool for glass cannon builds
- Visible shield effect for tactical awareness

#### 🗡️ **Rogue Abilities (4 new - Cooldown-based)**

**Void Blade** (Blink Strike ability):
- **Teleportation**: Instantly teleport behind nearest enemy within 15 blocks
- **Invisibility**: Brief invisibility (2 seconds) after teleport
- **Speed Boost**: Enhanced movement for quick followup
- **Cooldown**: 25 seconds
- Dark purple particle effects
- Perfect for engaging or disengaging combat

**Vanish Cloak** (Vanish ability):
- **Invisibility**: 8 seconds of complete invisibility
- **Speed II**: 8 seconds of enhanced mobility
- **Enemy Blind**: Blinds all enemies within 6 block radius for 4 seconds
- **Cooldown**: 40 seconds
- Gray smoke cloud on activation
- Ultimate escape tool when overwhelmed

**Poison Vial** (Poison Strike ability):
- **Deadly Toxin**: Poison IV for 8 seconds (~16 damage total)
- **Debuffs**: Applies Weakness II and Slowness I
- **Initial Damage**: 4 damage on application
- **Cooldown**: 20 seconds
- Green poison particle effects
- Excellent for taking down tough single targets

**Assassin's Mark** (Assassinate ability):
- **Manual Backstab**: 20 damage (10 hearts) from behind
- **Front Attack**: 10 damage (5 hearts) as backup
- **Visual Feedback**: Red critical hit particles
- **Blood Effect**: Special particles on successful backstab
- **Cooldown**: 15 seconds
- Complements passive backstab mechanic for combo potential

### Changed - Class System Enhancement

**Warrior Class**:
- Now has **5 total abilities** with diverse offensive and defensive options
- Enhanced survivability with self-healing and damage reduction
- Added AoE crowd control capability
- Improved combat flow with complementary ability cooldowns

**Mage Class**:
- Now has **4 permanent abilities** plus wands and spell scrolls
- Balanced offensive and defensive options
- Added homing projectile capability for reliable damage
- Enhanced crowd control with time manipulation
- Improved survivability with absorption shield

**Rogue Class**:
- **Dual Ability System**:
    - Energy-based (existing): Smoke Bomb, Poison Dagger, Shadow Step via Rogue Ability Tome
    - Cooldown-based (NEW): Blink Strike, Vanish, Poison Strike, Assassinate via individual items
- Now has **7 total abilities** plus passive backstab
- Added teleportation and advanced stealth mechanics
- Enhanced burst damage with manual backstab activation
- Improved versatility with two distinct ability systems

### Technical - Ability Implementation

**Item Creation**:
- Created 11 new ability items with custom textures and models
- Implemented custom tooltips with cooldown and effect descriptions
- Added proper item registration and creative tab integration

**Cooldown Management**:
- Extended cooldown system to support new abilities
- Implemented separate cooldown tracking for each ability
- Added visual feedback for cooldown states
- Proper cooldown persistence across sessions

**Effect Systems**:
- Warrior abilities use attribute modifiers and potion effects
- Mage abilities use particle systems and custom damage calculations
- Rogue abilities use teleportation, stealth, and debuff mechanics
- All abilities have unique particle effects and sound feedback

**Code Organization**:
- Warrior abilities in `class_system/warrior/` package
- Mage abilities in `class_system/mage/` package
- Rogue abilities in `class_system/rogue/` package
- Ability items in `item/ability/` package
- Shared utility code in `class_system/AbilityUtils.java`

### Balance Changes
- Warrior cooldowns balanced for sustained combat (15-60s range)
- Mage abilities balanced for burst rotation (20-50s range)
- Rogue abilities balanced for tactical gameplay (15-40s range)
- All abilities designed to complement existing class mechanics
- Power levels tuned for progression through game stages

---

## [1.4.3-beta] - 2025-10-22

### Added
- **Bone Dungeon Structure System**
    - New jigsaw-based dungeon generation with random layouts
    - 10 unique structure pieces: Entryway, Hallway, (Corners Left and Right), Crossway, Stairway, 3 Rooms, Treasure Room, Portal Room
    - Loot tables for dungeon chests with themed rewards
    - Dungeon spawns near surface with underground chambers
    - Endcap structures to cleanly terminate dungeon paths

- **Consumable Items - Enhanced**
    - Shadow Blend (Rogue): Invisibility now properly breaks when attacking
    - Fortune Dust: Tracks exactly 10 blocks mined with Fortune III bonus drops
    - Cooldown Elixir (Warrior): Reduces all active ability cooldowns by 30 seconds

### Fixed
- Consumable items now use proper data storage system (HashMap-based)
- Improved consumable effect tracking and reliability

### Removed
- dag010 datapack (deprecated/unused)
- Cleaned up unused debug code and TODOs

### Technical
- Implemented event handlers for Shadow Blend and Fortune Dust
- Added `ShadowBlendHandler.java` for invisibility breaking on attack
- Added `FortuneDustHandler.java` for tracked Fortune drops
- Updated `CooldownManager.java` with `reduceAllCooldowns()` method
- Bone dungeon uses weighted pool system for balanced generation

---

## [1.4.2-beta] - 2025-10-20

### Added - Complete Boss Hierarchy System
- **Skeleton King Entity**: Epic realm boss with full netherite armor
    - 60 health, 8 attack damage, 2.0 scale (double size)
    - Purple boss bar with "Skeleton King" title
    - Custom named netherite equipment (Crown of the Bone Sovereign, etc.)
    - Wither skeleton sounds for intimidation
    - Drops full enchanted netherite armor set + legendary loot
    - Never despawns - must be defeated
    - **Loot**: Full netherite set (Protection V, Thorns III, Unbreaking III), Sharpness V sword, Nether Star, 1-2 Wither Skeleton Skulls, 3-6 Netherite Ingots, 10-20 Diamonds

- **Skeleton Lord Entity**: Mini-boss with full diamond armor
    - 45 health, 6 attack damage, 1.5 scale (50% larger)
    - Red boss bar with "Skeleton Lord" title
    - Custom named diamond equipment (Helm of the Bone Lord, etc.)
    - Summons up to 3 Skeleton Summoners to fight alongside it
    - Drops full enchanted diamond armor set + loot
    - Spawns naturally in Bone Realm dimension
    - Never despawns
    - **Loot**: Full diamond set (Protection IV, Thorns II, Unbreaking III), Sharpness IV sword, 5-15 Bones, 1-3 Diamonds

- **Skeleton Summoner Entity**: Elite mob that spawns Bonelings
    - 30 health, 4 attack damage, 1.1 scale (slightly larger)
    - Iron helmet + leather robes (gray theme)
    - Summons up to 4 Bonelings periodically
    - Summoned by Skeleton Lords during combat
    - Purple witch particle effects when summoning
    - Evoker casting sounds
    - Never despawns while active

- **Boneling Entity**: Weak skeleton minion
    - 12 health, 2.5 attack damage, 0.7 scale (70% size, small!)
    - Fast movement (35% speed)
    - No armor, fragile
    - **Limited lifetime**: Despawns after 3 minutes with poof particles
    - **Ambient particles**: Ash particles while alive
    - **Death effects**: Explodes into bone meal + soul particles
    - **Higher-pitched sounds**: 1.4x pitch for creepy effect
    - No loot drops - summoned creatures
    - 3 XP only

- **Boss Hierarchy System**:
    - Skeleton King (planned for structure spawning)
    - Skeleton Lord (spawns naturally in Bone Realm)
    - Skeleton Summoner (summoned by Lords)
    - Boneling (summoned by Summoners)
    - Complete chain-summon combat system

- **Enchanted Boss Loot Tables**:
    - **Skeleton King drops**: Protection V, Thorns III, Unbreaking III, Respiration III, Feather Falling IV, Sharpness V, Looting III, Fire Aspect II
    - **Skeleton Lord drops**: Protection IV, Thorns II, Unbreaking III, Respiration II, Feather Falling III, Sharpness IV, Looting II
    - Loot tables handle all drops including armor and bonus items
    - Boss equipment doesn't drop separately (0% drop chance)

- **Custom Locked Chest Rendering System**:
    - **Skeleton King Chest**: Requires Skeleton King's Key to unlock
        - Custom bone-themed texture with glowing effects
        - 50.0F hardness, 1200.0F blast resistance
        - Luminance: 10 (glows in the dark)
    - **Bone Realm Locked Chest**: Requires Bone Realm Chest Key to unlock
        - Custom eerie bone texture
        - 5.0F hardness, 6.0F blast resistance
        - Luminance: 5 (dim glow)
    - Chests display unique textures using advanced rendering mixins
    - Visual/audio feedback when attempting to open locked chests
    - Particle effects on successful unlock
    - Keys consumed on unlock (except in creative mode)
    - Boss chests spawn automatically on boss death with epic effects

### Technical - Boss & Chest Systems
- Extended `SkeletonEntity` for all boss entities for proper armor rendering
- Implemented `AbstractSkeletonEntityRenderer` for proper armor display
- Created complete boss hierarchy:
    - `SkeletonKingEntity`: Epic boss with boss bar and chest spawning
    - `SkeletonLordEntity`: Mini-boss with boss bar, summons Summoners
    - `SkeletonSummonerEntity`: Elite mob that summons Bonelings
    - `BonelingEntity`: Enhanced minion with particles and lifetime limit
- Created custom chest rendering system:
    - `ChestRenderStateAccessor`: Interface for storing custom chest data in render state
    - `ChestRenderStateMixin`: Implements custom chest type tracking
    - `LockedChestTextureMixin`: Intercepts texture lookups for custom rendering
    - `ChestTextureHolder`: ThreadLocal helper for mixin communication
- Boss entities registered with proper attributes and AI goals
- `BossChestSpawner`: Handles chest spawning with particle effects and key distribution
- Proper VoxelShape definitions for accurate chest hitboxes
- Entity renderers registered for all boss entities
- Loot table system for enchanted armor and bonus drops

### Changed
- Skeleton-based bosses now properly display equipped armor
- Custom chest blocks updated to `ENTITYBLOCK_ANIMATED` render type
- Chest hitboxes match visual model size (14x14x14 pixels)
- Boss combat now involves managing summoned mobs
- Bonelings auto-despawn after 3 minutes to prevent overwhelming the world
- Summoners use witch particles instead of soul particles for visual distinction

### Fixed
- Boss armor now renders correctly (was invisible before)
- Custom chest textures display properly for both chest types
- No visual gaps around chests showing underground areas
- Texture persistence when moving around chests
- Skeleton Lord now properly spawns chests on death
- Equipment drop chances set to 0% (loot tables handle all drops)

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
- Entity position tracking updated to use `.getTrackedPosition().getPos()`
- Particle spawning uses proper double parameters
- GUI rendering updated for 1.21.10 API changes
- World generation configs updated to 1.21.10 format specifications

## [1.4.0] - 2025-10-10

### Added - Rogue Class Abilities & Energy System
- **Rogue Ability Tome**: Right-click to cycle abilities, sneak+right-click to use
    - Smoke Bomb: AoE invisibility + blinds enemies (30 energy)
    - Poison Dagger: Apply poison damage to target (40 energy)
    - Shadow Step: Short-range teleport (50 energy)
- **Energy System**: 100 max energy, 1 energy/second regeneration
- **Energy HUD**: Visual energy bar for Rogues (toggleable)
- **Rogue Stats**:
    - 25% critical hit chance on all attacks
    - 50% reduced fall damage
    - 1.5x backstab damage from behind (automatic)
    - Works with Orc racial bonus for extreme backstab damage

### Added - Complete Armor System (6 Sets)
- **Apprentice Set** (Leather-tier):
    - Helmet: +10 mana, +5 energy, 1 armor, 1 toughness
    - Chestplate: +10 mana, +5 energy, 3 armor, 1 toughness
    - Leggings: +10 mana, +5 energy, 2 armor, 1 toughness
    - Boots: +10 mana, +5 energy, 1 armor, 1 toughness
- **Adept Set** (Chainmail-tier):
    - Helmet: +15 mana, +8 energy, 2 armor, 1 toughness
    - Chestplate: +15 mana, +8 energy, 5 armor, 1 toughness
    - Leggings: +15 mana, +8 energy, 4 armor, 1 toughness
    - Boots: +15 mana, +8 energy, 1 armor, 1 toughness
- **Mystic Set** (Iron-tier):
    - Helmet: +20 mana, +10 energy, 2 armor, 1 toughness
    - Chestplate: +20 mana, +10 energy, 6 armor, 1 toughness
    - Leggings: +20 mana, +10 energy, 5 armor, 1 toughness
    - Boots: +20 mana, +10 energy, 2 armor, 1 toughness
- **Archmage Set** (Gold-tier):
    - Helmet: +25 mana, +12 energy, 2 armor, 1 toughness
    - Chestplate: +25 mana, +12 energy, 5 armor, 1 toughness
    - Leggings: +25 mana, +12 energy, 3 armor, 1 toughness
    - Boots: +25 mana, +12 energy, 1 armor, 1 toughness
- **Exalted Set** (Diamond-tier):
    - Helmet: +30 mana, +15 energy, 3 armor, 2 toughness
    - Chestplate: +30 mana, +15 energy, 8 armor, 2 toughness
    - Leggings: +30 mana, +15 energy, 6 armor, 2 toughness
    - Boots: +30 mana, +15 energy, 3 armor, 2 toughness
- **Ethereal Set** (Netherite-tier):
    - Helmet: +40 mana, +20 energy, 3 armor, 3 toughness
    - Chestplate: +40 mana, +20 energy, 8 armor, 3 toughness
    - Leggings: +40 mana, +20 energy, 6 armor, 3 toughness
    - Boots: +40 mana, +20 energy, 3 armor, 3 toughness

### Added - Consumable Powders (15 types)
**Warrior Powders**:
- Berserker Blood: +50% damage, -25% defense (60s)
- Titan Powder: +4 max hearts (120s)
- Battle Dust: +30% attack speed (45s)
- Last Stand Powder: Death prevention (placeholder: +5 absorption hearts)

**Mage Powders**:
- Arcane Powder: +50% spell damage (60s)
- Spell Echo: Spell duplication (placeholder: +20% attack speed)
- Overcharge Dust: Enhanced spell power (placeholder: +50% spell damage)
- Time Distortion: Slow nearby enemies (placeholder: Speed II)

**Rogue Powders**:
- Shadow Blend: Extended invisibility (5 minutes, breaks on attack)
- Phantom Dust: Dodge chance (placeholder: Resistance II)
- Perfect Dodge: Auto-dodge (placeholder: Resistance II)

**Universal Powders**:
- Vampire Dust: Lifesteal (placeholder: Regeneration II)
- Fortune Dust: Fortune III on next 10 blocks mined
- Haste Dust: Haste III (60s)
- Luck Dust: Luck II (120s)

### Added - Weapon & Shield System (18 items)
**9 Themed Weapons + 9 Matching Shields**:
1. **Flame Series**: Fire Aspect II, Ignites targets
2. **Frost Series**: Slowness effect on hit
3. **Storm Series**: Lightning damage, Speed boost
4. **Shadow Series**: Weakness and Blindness
5. **Radiant Series**: Healing on hit, Regeneration
6. **Venom Series**: Poison damage over time
7. **Earth Series**: Mining speed, Resistance
8. **Arcane Series**: Mana regeneration boost
9. **Blood Series**: Lifesteal on hit

### Added - Weapon Synergy System
- Wearing matching armor set enhances weapon abilities
- Example: Flame Sword + Mystic Armor = Enhanced fire damage
- System detects armor set and weapon type
- Bonus effects trigger automatically during combat
- Stacks with existing class and race bonuses

### Changed - Spell System Integration
- **Spell Scrolls** now cost 75% normal mana (was 100%)
- Wearing full mana-boosting armor sets reduces spell costs further
- Mages with Archmage Set or higher: 50% spell cost reduction
- Maximum spell cost reduction: 62.5% with full Ethereal Set
- Spell scrolls remain consumable items (crafted, not unlimited)

### Changed - Backstab System Enhancement
- Base backstab multiplier: 1.5x (automatic, passive)
- Orc Rogue synergy: Additional +20% backstab damage
- Combined Orc Rogue backstab: 1.7x total multiplier
- Works on all melee attacks from behind (135-degree arc)
- Visual feedback: Critical hit particles on backstab
- Scales with all damage bonuses (race, level, gear)

### Technical - Energy System
- Created `RogueEnergyManager` for energy tracking
- Energy stored per-player with automatic saving
- Energy HUD overlay in top-right corner (toggleable)
- Energy regeneration every 20 ticks (1/second)
- Energy costs: Smoke Bomb (30), Poison Dagger (40), Shadow Step (50)
- Ability cycling with proper feedback messages

### Technical - Armor System
- 30 new armor piece items across 6 complete sets
- Custom armor materials extending vanilla materials
- Mixin system for applying mana/energy bonuses
- Armor detection on equip/unequip events
- Bonus persistence across sessions
- Full integration with existing class systems

### Technical - Weapon System
- 18 new weapon items (9 weapons + 9 shields)
- Custom weapon/shield materials with special properties
- Effect application on entity hit
- Synergy detection system comparing armor and weapon types
- Effect stacking with existing damage modifiers
- Visual particle effects for special abilities

### Fixed
- Backstab damage now properly multiplies base damage
- Energy regeneration works correctly in all scenarios
- Armor bonuses apply immediately on equip
- Spell costs calculate correctly with armor bonuses
- Weapon effects trigger reliably on hit

## [1.3.0] - 2025-10-04

### Added - Complete Progression System
- **Level System (1-50)**:
    - Exponential XP scaling (100 base XP, 1.15 multiplier per level)
    - Level 1→2: 100 XP, Level 49→50: ~6,000 XP
    - Total XP for level 50: ~140,000 XP
- **Automatic XP Rewards**:
    - Mining ores: 5-40 XP (Coal: 5, Iron: 10, Gold: 15, Diamond: 25, Ancient Debris: 40)
    - Killing mobs: 10-2000 XP (Zombie: 15, Skeleton: 20, Enderman: 45, Wither: 1500, Ender Dragon: 2000)
    - Woodcutting: 2 XP per log
    - Farming: 1-5 XP (Wheat: 1, Carrots/Potatoes: 2, Nether Wart: 5)
    - Fishing: 2-15 XP (Fish: 2, Treasure: 15, Junk: 1)
    - Quest completion: 200-2500 XP based on difficulty
- **Stat Bonuses** (apply automatically every 10 levels):
    - +1 Max Health per 10 levels (total +5 hearts at level 50)
    - +0.5 Attack Damage per 10 levels (total +2.5 attack at level 50)
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