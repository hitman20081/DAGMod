# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.8.2 — Village NPC Structures & Dynamic Lighting Fix
**Released:** 2026-06-17

---

## What's New in v1.8.2

### Village NPC Structures

7 standalone NPC buildings now generate across plains, forest, and taiga biomes. Each is an independent structure locatable with `/locate structure dagmod:<name>`:

- `village_inn` — traveller's rest stop
- `village_tavern` — drinks and rumours
- `village_shop_1` / `village_shop_2` — general goods
- `village_traders_1` — roaming merchant stall
- `village_jeweler` — gems and fine wares
- `village_blacksmith` — weapons and repairs

Buildings share a placement grid (spacing 16 chunks / 256 blocks) so you'll find a mix of types within a reasonable exploration radius.

---

## Bug Fixes

- **Dynamic lighting terrain not updating** — `setSectionRangeDirty` was receiving raw block coordinates instead of section coordinates (1 section = 16 blocks), so terrain chunks near the world origin were being marked dirty instead of chunks around the player. Lighting now correctly illuminates terrain as you move
- **Village NPC structure set not loading** — The old jigsaw setup referenced a missing `village_docks.nbt`, causing the entire structure set to silently fail on world init and preventing `/locate structure` from working at all

---

## Changes

- **Hall of Champions rarity** — Spacing increased 40→64 chunks, separation 12→20. Halls are now ~2.5× rarer (one per ~1024 blocks vs ~640 blocks). Reduces overcrowding and frees up world space for other structures
- **Village exclusion zones reduced** — Hall exclusion radius around villages reduced 15→8 chunks; bone dungeon exclusion 12→6 chunks

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

> **Note:** Village NPC structures only generate in newly explored chunks. Use `/locate structure dagmod:village_inn` (or any building name) to find the nearest one.

---

## Known Issues

- **Pre-existing chunk artifacts** — Chunks generated on older versions may show minor terrain or lighting artifacts at borders. Use [MCA Selector](https://github.com/Querys/mcaselector) to prune unvisited chunks if needed
- Harmless "Block-attached entity at invalid position" warnings in server logs during worldgen (vanilla Minecraft issue, no gameplay impact)
- See [GitHub Issues](https://github.com/hitman20081/DAGMod/issues) for anything else reported

---

## Links

- [GitHub](https://github.com/hitman20081/DAGMod)
- [Changelog](https://github.com/hitman20081/DAGMod/blob/main/CHANGELOG.md)
- [Wiki](https://github.com/hitman20081/DAGMod/blob/main/docs/Home.md)
- [Modrinth](https://modrinth.com/mod/dag-mod)

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.8.1 | MC 26.2 migration (Fabric Loader 0.19.3, Fabric API 0.150.2+26.2), brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station, shield handle fixes |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
