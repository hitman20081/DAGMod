## What's New in v1.8.2

### Village NPC Structures

7 standalone NPC buildings now generate across plains, forest, and taiga biomes. Each is an independent structure locatable with `/locate structure dagmod:<name>`:

- `village_inn` — traveller's rest stop
- `village_tavern` — drinks and rumours
- `village_shop_1` / `village_shop_2` — general goods
- `village_traders_1` — roaming merchant stall
- `village_jeweler` — gems and fine wares
- `village_blacksmith` — weapons and repairs

Buildings share a placement grid (spacing ~256 blocks) so you'll find a mix of types within a reasonable exploration radius.

> **Note:** Structures only generate in newly explored chunks. Use `/locate structure dagmod:village_inn` (or any building name) to find the nearest one.

---

## Bug Fixes

- **Dynamic lighting terrain not updating** — Held-item lighting now correctly illuminates terrain blocks as you move. The chunk rebuild system was calling `setSectionRangeDirty` with raw block coordinates instead of section coordinates (1 section = 16 blocks), marking chunks near the world origin as dirty instead of chunks around the player
- **Village NPC structure set not loading** — The old jigsaw setup referenced a missing `village_docks.nbt`, causing the entire village structure set to silently fail on world init. `/locate structure dagmod:village_npc` would always return no results. Replaced with 7 individual structures

---

## Changes

- **Hall of Champions rarity** — Spacing increased 40→64 chunks, separation 12→20. Halls are now ~2.5× rarer (one per ~1024 blocks vs ~640 blocks)
- **Village exclusion zones** — Hall exclusion radius around villages reduced 15→8 chunks; bone dungeon exclusion 12→6 chunks

---

## Requirements

| Component | Version |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.150.2+26.2 |

---

## Updating

Your existing progress is safe — race, class, level, and quest data all persist across updates.

1. Back up your world
2. Remove the old DAGMod `.jar` from your mods folder
3. Install the v1.8.2 `.jar`
4. Launch Minecraft 26.2

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.8.1 | MC 26.2 migration, brimstone rename, all MC 26.2 API fixes |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
