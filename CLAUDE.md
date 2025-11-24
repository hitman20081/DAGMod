# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DAGMod is a comprehensive fantasy RPG modification for Minecraft 1.21 using Fabric. It features a race/class system, quest chains, progression with 50 levels, custom abilities, procedurally generated dungeons, boss fights, and party mechanics.

**Minecraft Version**: 1.21.10
**Mod Loader**: Fabric
**Java Version**: 21
**Current Version**: v1.5.1-beta

## Build Commands

```bash
# Build the mod (output: build/libs/)
./gradlew build

# Run Minecraft client for testing
./gradlew runClient

# Run dedicated server for testing
./gradlew runServer

# Generate data (loot tables, recipes, etc.)
./gradlew runDatagen

# Clean build artifacts
./gradlew clean
```

## Development Workflow

The mod uses the standard Fabric development pattern:
- `src/main/java/` - Java source code
- `src/main/resources/` - Assets, data files, NBT structures
- `src/main/resources/data/` - Datapacks (functions, loot tables, advancements, recipes)
- Mixins are defined in `dagmod.mixins.json` and located in the `mixin` package

## Architecture Overview

### Core System Components

The mod is organized into several interconnected systems:

#### 1. Race & Class System
- **Location**: `race_system/` and `class_system/`
- Players select a race (Human, Dwarf, Elf, Orc) and class (Warrior, Mage, Rogue) via Altar blocks
- Selection data is stored in **PlayerDataManager** (persistent NBT files in `world/data/dagmod/players/{uuid}.dat`)
- Abilities are applied through **ClassAbilityManager** and **RaceAbilityManager**
- **Synergies** are handled by `RaceClassSynergyManager` (9 unique race+class combinations)
- **Critical**: Always call `ClassAbilityManager.applyClassAbilities()` and `RaceAbilityManager.applyRaceAbilities()` after race/class selection or on player load

#### 2. Quest System
- **Location**: `quest/`
- **Central Manager**: `QuestManager` (singleton pattern)
- Quest data flow:
  1. Quests registered in `QuestRegistry.registerQuests()`
  2. Player data stored in `QuestData` (per-player quest progress)
  3. Client-side state synchronized via `QuestSyncPacket` and stored in `ClientQuestData`
  4. Quest interactions happen through Quest Block (story/side quests) and Job Board (daily/jobs)
- **Quest Categories**: MAIN, SIDE, CLASS, JOB, DAILY
- **Quest Chains**: Managed by `QuestChain`, tracks sequential quests and unlocks quest book tiers
- Objectives: `CollectObjective`, `KillObjective`, `CraftObjective`, `DeliveryObjective`, `TagCollectObjective`
- Rewards: `ItemReward`, `XpReward`, `UnlockReward`
- **IMPORTANT**: Prefer `CollectObjective` (specific items) over `TagCollectObjective` (item tags). TagCollectObjective has reliability issues with complex tags like `ItemTags.LOGS` - see v1.5.1 changelog.

**Quest Reward Philosophy (v1.5.1+)**:
- **NOVICE tier**: Iron/Chainmail minimum (never stone/leather - backwards progression issue)
- **Class-specific items**: ONLY use in matching CLASS quests (e.g., mage scrolls only in Mage quests)
- **Universal items**: Custom foods, Fortune Dust, Cooldown Elixir safe for any quest
- **Race themes**: Use culturally appropriate custom foods (Dwarf: hearty, Elf: nature, Orc: meat, Human: versatile)
- See `CUSTOM_ITEM_CATEGORIES.md` for item categorization

**Tutorial System (v1.5.0+, bugs fixed v1.5.1)**:
- **Quest System Gating**: Quest Blocks and Job Boards are locked until player meets Innkeeper Garrick
- **Task-Based Tutorial**: 3 pre-quest tasks must be completed to earn Quest Book (MUST complete sequentially)
  - **Task 1**: Gather 10 Oak Logs → Garrick's First Note
  - **Task 2**: Defeat 5 hostile mobs → Garrick's Second Note (tracked via event, requires Task 1 complete)
  - **Task 3**: Bring 1 Iron Ingot → Garrick's Third Note (requires Task 2 complete)
- **Quest Book Requirement**: Players must have a Quest Book to access quests
- **Note Combination**: Quest Block combines 3 Quest Notes into Novice Quest Book (uses `hasItemInInventory()` helper)
- **Tutorial NPC**: `InnkeeperGarrickNPC` in `entity/` handles all tutorial dialogue with sequential enforcement
- **Task Tracking**: `PlayerDataManager` stores task completion flags and mob kill counter
- **Mob Kill Event**: Registered in `DagMod.registerTutorialMobKillTracking()` with Task 1 prerequisite check (line 584-586)
- **Sequential Enforcement**: Garrick's dialogue enforces strict task order (lines 98-118), only marks tasks complete when giving notes
- **Important**: The tutorial system uses simple task checks (NOT the formal quest system)
- **Bug Fix (v1.5.1)**: Removed auto-complete from `incrementTask2MobKills()` - only Garrick marks complete now

#### 3. Progression System
- **Location**: `progression/`
- **Manager**: `ProgressionManager` (server-side, handles XP/levels)
- **Storage**: Files in `world/data/dagmod/progression/{uuid}.dat` via `ProgressionStorage`
- **Client Sync**: `ProgressionPackets` sends data to `ClientProgressionData`, rendered by `ProgressionHUD`
- 50 levels with exponential XP curve (see `LevelRequirements`)
- **Stat scaling**: Every 10 levels grants +1 HP, +0.5 Attack, +1 Armor (handled by `StatScalingHandler`)
- **XP Sources**: Registered in `XPEventHandler` (mining, combat, quests, farming)
- **Important**: Always use `ProgressionManager.addXP()` for XP changes (handles auto-sync)

#### 4. Party System
- **Location**: `party/`
- Multiplayer group system with shared XP and loot
- **Manager**: `PartyManager` (tracks party membership)
- **Party Quests**: `PartyQuestManager` handles multi-player quest instances
- Party quest objectives: KILL_ENTITY, KILL_BOSS, COLLECT_ITEM, REACH_LOCATION
- Loot distribution via `PartyLootHandler`
- Commands: `/party create`, `/party invite`, `/party leave`, etc.

#### 5. Class-Specific Systems

**Mage System**:
- **Mana Resource**: `ManaManager` (server) and `ClientManaData` (client)
- Mana regenerates automatically (tick-based, see `DagMod.onInitialize()` server tick handler)
- Abilities: Arcane Missiles, Mana Burst, Arcane Barrier, Time Warp
- **Cooldowns**: Managed by `MageCooldownManager`
- **Network Sync**: `ManaNetworking.ManaUpdatePayload`

**Warrior System**:
- Rage-based abilities with cooldown management (`CooldownManager`)
- Abilities: Battle Shout, Whirlwind, Shield Bash, Iron Skin, War Cry
- Shield Bash uses a special listener: `ShieldBashListener`

**Rogue System**:
- **Dual resource**: Energy (tome abilities) + Cooldown (item abilities)
- Energy system: `EnergyManager` and `EnergyNetworking`
- Backstab mechanic: Handled by `RogueCombatHandler` (1.5x damage from behind)
- Abilities: Blink Strike, Vanish, Assassinate, Poison Strike

#### 6. Bone Realm & Boss System
- **Location**: `bone_realm/`
- Custom dimension with boss hierarchy: Skeleton King → Lords → Summoners → Bonelings
- **Portal**: Built with Ancient Bone blocks, activated with Necrotic Key (see `BonePortalFrameDetector`)
- **Boss spawning**: Event-driven, tracked in `BossDeathEventHandler`
- **Locked chests**: Custom chest system with key requirements (`LockedBoneChestBlock`)
- **Dungeons**: Procedurally generated structures with custom loot tables

#### 7. World Generation & Structures
- **Location**: `src/main/resources/data/dag011/`
- **Active Structures** (v1.5.0+):
  - **Village NPC Structures**: Inn, Tavern, 2 Shops, Docks (tutorial starting point)
  - **Hall of Champions**: Race/Class selection altar structure
  - **Bone Dungeons**: 10-piece jigsaw dungeon system
  - **Skeleton Kingdom**: Large ancient ruins structure
  - **Houses**: NPC and Villager houses
  - **Castle Medieval** (1.1MB): Reserved for future dimension implementation
  - **Castle Pale Garden** (5.8MB): Reserved for Pale Garden dimension implementation
- **Removed Structures** (v1.5.0+):
  - ❌ Castle Fort (removed to reduce mod size)
  - ❌ Castle Large (removed to reduce mod size)
- **Structure Overlap Prevention**: All structure sets use exclusion zones (10-25 chunks)
- **Bundled Worlds**: `src/main/resources/bundled_worlds/bleakwind/` contains development world (139MB, needed for testing)

**Structure Organization**:
```
data/dag011/
├── structure/              # NBT structure files
│   ├── bone_dungeon/       # 12 dungeon pieces
│   ├── castle/             # 2 castles (medieval, pale_garden)
│   ├── house/              # 2 house variants
│   ├── skeleton_kingdom/   # 50+ ancient ruin pieces
│   ├── spawn/              # Hall of Champions
│   └── village_npc/        # 5 village buildings
├── worldgen/
│   ├── structure/          # Structure definitions
│   ├── structure_set/      # Placement rules with exclusion zones
│   └── template_pool/      # Jigsaw pool configurations
└── tags/worldgen/biome/    # Biome spawn conditions
```

### Data Persistence Patterns

All player data uses NBT files stored in `world/data/dagmod/`:

```
world/data/dagmod/
├── players/           # Race, class data (PlayerDataManager)
│   └── {uuid}.dat
├── progression/       # Level, XP data (ProgressionStorage)
│   └── {uuid}.dat
├── world/             # World-specific data
│   └── hall_location.dat
```

**Critical Rules**:
1. Always save player data on disconnect/dimension change (registered in `ProgressionEvents`)
2. Always load player data on join/entity load (see `DagMod.onInitialize()` event handlers)
3. Use `PlayerDataManager.savePlayerData()` and `loadPlayerData()` for race/class
4. Use `ProgressionManager.addXP()` for progression (auto-saves and syncs to client)

**PlayerDataManager API** (v1.5.0+):
```java
// Race & Class
PlayerDataManager.savePlayerData(ServerPlayerEntity player)
PlayerDataManager.loadPlayerData(ServerPlayerEntity player)

// NPC Interaction Tracking
PlayerDataManager.hasMetGarrick(ServerPlayerEntity player)
PlayerDataManager.markMetGarrick(ServerPlayerEntity player)

// Tutorial Task Tracking
PlayerDataManager.isTask1Complete(UUID playerId)
PlayerDataManager.markTask1Complete(ServerPlayerEntity player)
PlayerDataManager.isTask2Complete(UUID playerId)
PlayerDataManager.markTask2Complete(ServerPlayerEntity player)
PlayerDataManager.getTask2MobKills(UUID playerId)
PlayerDataManager.incrementTask2MobKills(ServerPlayerEntity player)
PlayerDataManager.isTask3Complete(UUID playerId)
PlayerDataManager.markTask3Complete(ServerPlayerEntity player)
PlayerDataManager.hasCompletedAllTasks(ServerPlayerEntity player)
```

All methods auto-save to NBT when using the `ServerPlayerEntity` overload.

### Client-Server Networking

The mod uses Fabric's networking API with custom payloads:

**Server → Client Packets**:
- `QuestSyncPacket` - Quest updates
- `ManaNetworking.ManaUpdatePayload` - Mana changes
- `EnergyNetworking.EnergySyncPayload` - Rogue energy
- `ProgressionPackets` - XP/Level sync

**Client → Server Packets**:
- `QuestRequestPacket` - Quest interactions
- Ability trigger packets (class-specific)

**Pattern**: All packets registered in respective `*Networking` classes, receivers in main initializers.

### Mixin Integration Points

Mixins modify vanilla behavior for class/race bonuses:

- `MageStatusEffectMixin` - Potion/enchantment bonuses for Mages
- `RogueDamageMixin` - Backstab damage calculation
- `RaceMiningMixin` - Mining speed/drops for Dwarves
- `RaceHuntingMixin` - Bonus drops for Orcs
- `HumanExperienceMixin` - +25% XP for Humans
- `LivingEntityMixin` - Death events for quest tracking

**Important**: When adding new mixins, update `dagmod.mixins.json`.

### Command System

Commands are registered in `DagMod.onInitialize()` via `CommandRegistrationCallback.EVENT`:

- `/info` - Player stats (class, race, level)
- `/quest` - Quest management
- `/resetclass` - Reset class selection
- `/testprogression` - Dev testing for progression
- `/party` - Party system commands
- `/partyquest` - Party quest commands
- `/travel` - Ship travel system (destination-based teleport)

### Adding New Content

**New Quest**:
1. Create quest in `QuestRegistry.registerQuests()`
2. Define objectives (Collect/Kill/Craft/Delivery) - prefer `CollectObjective` over `TagCollectObjective`
3. Add rewards (Item/XP/Unlock) - respect tier minimums (Novice = Iron, Apprentice = Diamond, etc.)
4. Set difficulty tier and category (MAIN, SIDE, CLASS, JOB, DAILY)
5. Optionally add to a `QuestChain` for sequential progression
6. **Class-specific items**: Only reward in matching CLASS quests (check `CUSTOM_ITEM_CATEGORIES.md`)
7. **Custom foods**: Safe for all quests, prefer race-themed items for race quests

**New Ability**:
1. Create class in `class_system/{class_name}/` implementing appropriate ability interface
2. Register item in `ModItems`
3. Add cooldown/resource handling in respective manager
4. Implement `use()` method with player capability checks
5. Add client-side rendering if needed (particles, HUD elements)

**New Boss**:
1. Create entity class extending appropriate base (see `SkeletonKingEntity`)
2. Register in `BoneRealmEntityRegistry` (or `ModEntities`)
3. Add renderer in client initializer
4. Create loot table in `data/dag005/loot_table/boss/`
5. Add death handler in `BossDeathEventHandler` if special behavior needed

### Important Initialization Order

In `DagMod.onInitialize()`:
1. Items, Blocks, Entities (registry objects)
2. Effects, Potions
3. Dimension registration (`BoneRealmRegistry`)
4. Networking (`ModNetworking.initialize()`)
5. Class/Race systems (Mana, Energy, Warriors)
6. Quest system (`QuestRegistry.registerQuests()`)
7. Progression (`ProgressionEvents`, `ProgressionPackets`, `XPEventHandler`)
8. Commands
9. Event handlers (respawn, login, death, tick)

In `DagModClient.onInitializeClient()`:
1. Progression HUD first (`ClientProgressionData`, `ProgressionHUD`)
2. Quest networking
3. Mana/Energy client handlers
4. Entity renderers
5. Block entity renderers

### Testing & Debugging

**Test Commands**:
```
/testprogression 500              # Add XP
/testprogression setlevel 25      # Set level
/testprogression reset            # Reset progression
/testprogression info             # Show stats
/quest list                       # List available quests
/resetclass                       # Reset class selection
```

**Common Debug Locations**:
- Quest completion: `QuestManager.turnInQuest()`
- XP gain: `ProgressionManager.addXP()`
- Ability usage: `{Class}AbilityManager` classes
- Data persistence: `PlayerDataManager`, `ProgressionStorage`

### Known Patterns

1. **Singleton Managers**: `QuestManager`, `ProgressionManager`, `PartyQuestManager` use getInstance()
2. **Registry Pattern**: All items, blocks, entities registered in static `Mod*` classes
3. **Event-Driven**: Heavy use of Fabric event callbacks (tick, death, login, disconnect)
4. **Client-Server Split**: All game logic on server, client only renders (HUD, particles)
5. **NBT Storage**: All persistent data uses NBT format with dedicated storage classes
6. **Packet Sync**: Server is authoritative, clients receive state via custom packets

### Critical Don'ts

- Don't modify player data without saving it (`PlayerDataManager.savePlayerData()`)
- Don't add XP/levels directly to `PlayerProgressionData` - use `ProgressionManager.addXP()`
- Don't forget to register networking packets on both client AND server
- Don't skip ability prerequisite checks (class, level, cooldown, resource cost)
- Don't create quest objectives without implementing both `updateProgress()` and `isCompleted()`
- Don't forget to sync client data after server-side changes (quests, mana, progression)
- Don't add large NBT structures (>5MB) without optimization - split into jigsaw pieces instead
- Don't include development worlds or test data in `src/main/resources/` for distribution builds
- **Don't use `TagCollectObjective` with complex tags** - prefer `CollectObjective` with specific items (v1.5.1: ItemTags.LOGS unreliable)
- **Don't reward stone/leather gear in NOVICE quests** - players start with iron/chainmail (backwards progression)
- **Don't reward class-specific items** in non-CLASS quests - only give mage scrolls in Mage CLASS quests, etc.
- **Don't use `.contains()` for ItemStack detection** - unreliable, use custom helper methods instead (see QuestBlock.hasItemInInventory())

### Mod Size Optimization

**Current Size** (v1.5.1): ~83MB compiled jar
- NBT Structures: ~23MB (largest: castle_pale_garden 5.8MB, castle_medieval 1.1MB)
- Bundled Worlds: 139MB (development only, kept for testing)
- Java Code: <5MB

**Best Practices**:
1. **Large Structures**: Split structures >5MB into smaller jigsaw pieces when possible
2. **Development Data**: Keep `bundled_worlds/` out of distribution builds if possible
3. **NBT Optimization**: Remove unnecessary air blocks and block entity data from structures
4. **Reserved Structures**: Castle Medieval and Pale Garden are reserved for dimension implementations

### File Locations Reference

- Main mod class: `DagMod.java`
- Client initializer: `DagModClient.java`
- Items: `item/ModItems.java`
- Blocks: `block/ModBlocks.java`
- Entities: `entity/ModEntities.java`
- Quest registry: `quest/registry/QuestRegistry.java`
- Party quest registry: `party/quest/PartyQuestRegistry.java`
- Progression integration guide: `progression/ProgressionSystemInit.java` (documentation file)
