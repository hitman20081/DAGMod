# DAGMod Known Issues & Code Quality Concerns

**Last Updated**: 2026-03-03
**Version**: v1.7.4

---

## Open Issues

### 8. Block-Attached Entity at Invalid Position (LOW)

**Location**: Vanilla Minecraft worldgen (`BlockPos` / `class_2338` in production jar)
**Status**: Open — cosmetic/harmless, no gameplay impact

During chunk generation, repeated log warnings appear:

```
[Server thread/WARN]: Block-attached entity at invalid position BlockPos{x=..., y=-55, z=...}
```

**Observed pattern**:
- Three consecutive X positions followed by one at Z+4, Y-1 (e.g. X=-147,-146,-145 then X=-141 at Y=-56)
- Y range: y=-55 to y=-57 (deepslate layer, above bedrock)
- Same Z coordinates (e.g. z=14, z=18) appear across different world seeds with different X offsets
- Errors repeat each time the affected chunks are regenerated/re-entered

**Investigation findings**:
- Affected chunk NBT shows `block_entities: 0 entries` — no stored block entity data
- No entity data found in the `entities/` region folder for affected chunks
- No Ancient City or special structure present at reported coordinates — just solid blocks
- Errors are logged by vanilla Minecraft code, not DAGMod code
- Attempted fixes: `/setblock` to reset blocks (temporary), excluding `minecraft:deep_dark` from ore generation in `ModOreGeneration.java` — neither resolved the issue

**Working theory**: A worldgen interaction (possibly ore feature placement, jigsaw structure, or biome decoration) triggers a temporary invalid block entity state that vanilla detects and warns about. The entity is never actually stored, so the error is self-resolving.

**Next steps**: Capture a full stack trace from the warning to identify the exact vanilla callsite and what feature is running at that position during worldgen.

---

### 2. TagCollectObjective Reliability (MEDIUM)

**Location**: `quest/objectives/TagCollectObjective.java`
**Status**: Open (documented since v1.5.1)

The `consumeItems()` method can fail to properly consume items when tags contain multiple item types or items are spread across inventory slots. Prefer `CollectObjective` (specific items) for new quests.

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


## Fixed Issues

### Fixed in v1.7.4
- All consumables fully implemented with custom mechanics (lifesteal, dodge, void rescue, spell doubling, 2× spell power) — no longer placeholder status effects

### Fixed in v1.7.2
- Bone dungeon portal room never spawning (self-referential jigsaw `Target Name` bug)
- Stairway U-shape generation (stairway removed from `main` pool)
- Crossway over-spawning / square cluster formation (weight reduced 20→4 in `corridors` pool)
- Water flooding dungeon rooms (added `no_water` structure processor to all pieces; restricted biomes to desert/badlands)

### Fixed in v1.7.1
- Permission checks added to all admin/debug commands (`.requires(hasPermissionLevel(2))`)
- QuestBlock race conditions: HashMap → ConcurrentHashMap, null guards in `showConfirmAccept()`
- Null safety in QuestNetworkHandler: guard `getObjectives()` and `getActiveQuests()` against null
- PartyCommand: catch-all `Exception` → `CommandSyntaxException` in invite/kick handlers

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
