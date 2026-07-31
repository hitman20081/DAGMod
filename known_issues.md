# DAGMod Known Issues & Code Quality Concerns

**Last Updated**: 2026-07-31
**Version**: v1.9.1

---

## Open Issues

### 12. Flawless and Grand Gem Textures Are Placeholders (LOW)

**Location**: `src/main/resources/assets/dagmod/textures/item/`
**Status**: Open — cosmetic only, no gameplay impact

The `gem_flawless_*` and `gem_grand_*` textures are currently copies of the Cut tier textures. All tiers are fully functional, but Flawless and Grand gems share the same appearance as Cut gems in inventory.

**Resolution**: Draw unique textures for the Flawless and Grand tiers and replace the placeholder PNGs.

---

### 11. Enchantment Descriptions Require Advanced Tooltips (LOW)

**Location**: `mixin/EnchantmentDescriptionMixin.java`
**Status**: Open — by design, but not obvious to players

In-game enchantment descriptions added in v1.8.3 only display when advanced tooltips are active. Players must press **F3+H** to enable advanced tooltips; they are on by default in Creative mode only.

**Resolution**: Document in the wiki. No code change planned — gating behind advanced mode prevents tooltip overflow on heavily enchanted armor.

---

### 10. MC 26.1.2 World Chunk Artifacts (LOW)

**Location**: World level files
**Status**: Open — existing worlds generated on older versions may have visual or structural artifacts after the 26.1.2 migration

The Minecraft 26.1.2 migration does not require a world reset, but pre-existing chunks may show terrain or lighting inconsistencies at chunk borders or in previously-loaded areas.

**Recommended mitigation**: Use [MCA Selector](https://github.com/Querz/mcaselector) to prune or reset affected/unvisited chunks. This forces those regions to regenerate cleanly under the new version.

---

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

### Fixed in v1.8.0
- **Gem Crushing Station wrong texture reference** — Model referenced `gem_polishing_station_texture`; corrected to `gem_crushing_station_texture`
- **Citrine Powder missing from Creative Tab** — CITRINE_POWDER omitted from item tab registration
- **Dynamic lighting broken after MC 26.1.2 migration** — `scheduleBlockRenders` was removed in MC 26.x and never replaced, so terrain blocks were not updating light when the player moved or changed held items (entity rendering still worked since entities re-render every frame). Fixed by calling `LevelRenderer.setSectionRangeDirty()` on the sections within the light radius. A second fix addressed stale light persisting after the light source was removed from hand — the old clearing condition incorrectly required the player to have moved; removed the position check so sections are always dirtied when the previous radius was non-zero

### Fixed in v1.7.10
- `TagCollectObjective` and `CollectObjective` item consumption — `consumeItems()` was calling `stack.decrement()` directly on the `ItemStack` object, which never triggered `markDirty()` on the inventory. Items were removed server-side but the client was never synced, causing items to appear unconsumed. Fixed by replacing `stack.decrement()` with `player.getInventory().removeStack(i, amount)` and adding an explicit `markDirty()` call after the loop in both classes

### Fixed in v1.7.7
- `WildDragonEntity` server crash — NPE in `AttackWithOwnerGoal` and `TrackOwnerAttackerGoal` when `canTarget()` was called before null-checking the target
- All blank enchanted book rewards replaced with real enchantments via `EnchantedBookReward`
- Enchanted book collect objectives removed from quests (Rune of Power, Path of Destiny)
- All Kill Ender Dragon objectives removed; replaced with DAGMod boss kills
- END-dimension collect objectives removed from all quests

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

---

**References**:
- `CLAUDE.md` — Development patterns and critical warnings
- `CHANGELOG.md` — Version history
- `ROADMAP.md` — Feature planning
- GitHub Issues: https://github.com/hitman20081/DAGMod/issues
