# DAGMod Known Issues & Code Quality Concerns

**Last Updated**: 2026-02-18
**Version**: v1.7.0

---

## Open Issues

### 1. Missing Permission Checks on Admin Commands (HIGH)

**Location**: Multiple command files
**Status**: Open

All admin/debug commands lack OP permission checks. Any player can execute:
- `/testprogression setlevel 200` — instantly reach max level
- `/resetclass <player>` — reset another player's class
- `/dragonrespawn reset/cancel` — manipulate boss timers
- `/merchant rotate` — force trade rotations
- `/resource mana/energy set` — set resource values
- `/cooldown clear` — clear all cooldowns

Every command file has a `// TODO: Re-add OP permission check using Fabric Permissions API` comment. This was removed during a refactor and never re-added.

**Impact**: Progression system, economy, and class balance completely bypassable on multiplayer servers.

### 2. TagCollectObjective Reliability (MEDIUM)

**Location**: `quest/objectives/TagCollectObjective.java`
**Status**: Open (documented since v1.5.1)

The `consumeItems()` method can fail to properly consume items when tags contain multiple item types or items are spread across inventory slots. Prefer `CollectObjective` (specific items) for new quests.

### 3. QuestBlock State Race Conditions (MEDIUM)

**Location**: `block/QuestBlock.java`
**Status**: Open

QuestBlock uses `HashMap` (not `ConcurrentHashMap`) for player state tracking (`playerMenuState`, `playerAvailableQuests`, `playerSelectedIndex`). This can cause:
- `ConcurrentModificationException` on busy servers
- Null dereference in `showConfirmAccept()` when `playerSelectedIndex.get()` returns null (unboxing NPE)
- Unbounded index increment in `/quest skip` (no max bounds check)

### 4. Null Safety in Quest Network Handler (MEDIUM)

**Location**: `networking/QuestNetworkHandler.java`
**Status**: Open

No null checks on quest objects during iteration in `handleQuestRequest()`. Corrupted quest data files can cause NPE during packet construction.

### 5. Hard-Coded Magic Numbers (LOW)

**Location**: Throughout codebase (~140+ values)
**Status**: Open

No configuration system exists. All gameplay-affecting values are hard-coded:
- Ability cooldowns (12 values across 3 classes)
- Damage/knockback values (15+ ability parameters)
- Resource caps (Mana: 100, Energy: 100)
- Status effect durations (25+ values)
- Level requirements for quest tiers (8 values)
- Race bonus percentages (mining, gathering, hunting, XP)
- Grave loot delay (5 minutes)

### 6. Missing Input Validation (LOW)

**Location**: Command handlers and packet codecs
**Status**: Open

- Quest IDs not validated for format/length in `/quest abandon` and `/partyquest start`
- Mana/Energy packet codecs don't validate bounds (NaN, Infinity, negative values)
- PartyCommand uses catch-all `Exception` instead of `CommandSyntaxException`

### 7. Consumable TODO Items (LOW)

**Location**: `item/ConsumableItem.java`
**Status**: Open — items work with placeholder effects

Several consumables use vanilla status effects as placeholders for their intended custom mechanics:
- **Spell Echo** — displays message only (TODO: spell doubling)
- **Overcharge Dust** — displays message only (TODO: 2x spell power)
- **Vampire Dust** — gives Regen II (TODO: actual lifesteal)
- **Phantom Dust** — gives Resistance III (TODO: dodge mechanic)
- **Perfect Dodge** — gives Resistance V (TODO: 100% dodge)
- **Last Stand Powder** — gives Absorption IV (TODO: totem-like revive)
- **Time Distortion** — gives Speed V (TODO: slow nearby enemies)

---

## Fixed Issues

### Fixed in v1.7.0
- Level cap increased from 50 to 200 with heart scaling rework

### Fixed in v1.6.6
- Death recovery system replaced non-functional datapack graves with Java-based lodestone grave system

### Fixed in v1.5.3
- Quest progression blocker at level 20 (circular dependency in quest book upgrade)
- Progression reset on reload (auto-create logic in getPlayerData)
- New world data leakage (static HashMaps persisted across worlds)

### Fixed in v1.5.2
- Quest data persistence (was memory-only, all lost on disconnect)
- Memory leak in static player data maps (no cleanup on disconnect)
- Thread safety: HashMap to ConcurrentHashMap in ManaManager, EnergyManager

---

## Testing Recommendations

1. **Permission Test**: Verify admin commands are restricted after permission system is added
2. **Concurrency Test**: 10+ players interacting with quest blocks simultaneously
3. **Data Persistence Test**: Force server crash during save and verify backup recovery
4. **Quest System Test**: Complete quests using TagCollectObjective types

---

**References**:
- `CLAUDE.md` — Development patterns and critical warnings
- `CHANGELOG.md` — Version history
- `ROADMAP.md` — Feature planning
- GitHub Issues: https://github.com/hitman20081/DAGMod/issues
