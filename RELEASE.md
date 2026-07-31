# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.9.1 — Structure Spawning, Dimension Gates & World Polish
**Released:** 2026-07-31

> **Note for players upgrading from v1.9.0:** No new world required. All structure, quest, and progression changes apply immediately. Existing Hall of Champions and Village Inns retain their current locations — only newly generated chunks follow the updated biome rules.

---

## What's New in v1.9.1

### Dragon Realm Now Gated at Level 50
The Dragon Realm is no longer freely accessible. The `red_dragon_fury` quest (which rewards the Dragon Key) does not appear until level 50. Attempting to activate a portal or walk through one below level 50 is blocked with a clear message. Exiting the Dragon Realm is always allowed.

### Hall of Champions Block Protection
Survival players can no longer mine blocks inside the Hall of Champions. The structure is sacred ground — its blocks cannot be removed without Creative mode. A message is shown on attempt.

### Structures No Longer Spawn on Water or Lava
All DAGMod structure sets now use the `avoid_water` flag, preventing Inns, Armorers, merchants, castles, and other structures from generating on or near surface water lakes and lava pools.

### Hall of Champions Now in Flat, Landmark Biomes Only
The Hall of Champions is restricted to `meadow`, `savanna_plateau`, and `cherry_grove`. These are open, flat biomes the structure fits naturally and that feel worthy of a journey.

### Armorer Has Its Own Structure Set
The Armorer NPC has been moved out of the shared merchant pool and given a dedicated structure set. It now consistently generates near the Inn in the same biomes rather than as one of four random merchant spawns.

### Bone Realm Gated at Level 25
The `rumours_of_the_bone_king` quest (which rewards the Bone Dungeon Locator) now requires level 25 before it appears in the quest log, restoring the intended early-game dimension barrier.

### Hall Locator Moved to Garrick Reward
The Hall Locator compass is no longer handed out on first join. Garrick gives it to players when they complete all three tutorial tasks, making the Hall of Champions discovery feel earned.

### Guide Book Readability Fixed
All guide book headers and labels now use colors that are readable on the parchment background. Previous color codes (white, bright green, light red, light purple, yellow) were invisible or near-invisible on light pages.

---

## Bug Fixes

- **Castle exclusion zone out of range** — ExclusionZone codec enforces chunk_count [1:16]; both castle sets had a value of 25. Capped to 16
- **Duplicate structure set salt** — `castle_medieval` and `castle_pale_garden` shared the same salt value. `castle_pale_garden` given a unique salt
- **`getStructureWithPieceAt` MC 26.2 API** — Dropped `ResourceKey<Structure>` overload replaced with `Predicate<Holder<Structure>>` in `ProtectedStructureHandler`
- **River biome in village_npc tag** — Inns and NPC buildings no longer generate in rivers
- **Armorer biome mismatch** — Armorer was spawning in savanna biomes where the Inn never appears; now uses the same `#dagmod:has_structure/village_npc` biome tag as the Inn

---

## Also in v1.9.x (from v1.9.0)

- **Village Inn** — World spawn hub with guaranteed near-spawn generation
- **Innkeeper Garrick** — Race and class selection via chat dialogue; starter gear on selection
- **Class Trainer NPC** — Dedicated NPC for all class quest chains at the Hall of Champions
- **Blacksmith's Anvil** — Permanent, indestructible anvil block
- **Merchant tier gating** — Premium Hall of Champions stock locked behind quest completion
- **Season system fixes** — Crop growth, sleep detection, weather predicates, and announcements all corrected for MC 26.2
- **Dragon Realm return portal** — Returns to bed/respawn anchor instead of hardcoded 0, 64, 0

---

## Migration Notes

> No new world is required for v1.9.1. All changes apply to newly generated chunks and existing game systems immediately.
>
> If upgrading from v1.8.x, see the [v1.9.0 release notes](docs/release-v1.9.0.md) for world generation migration guidance.

---

## Requirements

| Component | Version |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.150.2+26.2 |

---

## Updating

1. Back up your world
2. Remove the old DAGMod `.jar` from your mods folder
3. Install the v1.9.1 `.jar`
4. Launch Minecraft 26.2

---

## Known Issues

- **Flawless and Grand gem textures** — Currently placeholder copies of the Cut tier texture. Unique art planned
- **Enchantment descriptions require F3+H** — Advanced tooltips must be enabled; on by default in Creative
- Harmless "Block-attached entity at invalid position" warnings in server logs during worldgen (vanilla issue, no gameplay impact)
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
| v1.9.0 | Village Inn spawn hub, Garrick guild registry, Class Trainer NPC, Blacksmith's Anvil, merchant tier gating, season system fixes |
| v1.8.3 | Gem tier system (Cut→Polished→Flawless→Grand), in-game enchantment descriptions, quest book navigation |
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix, Hall of Champions rarity increase |
| v1.8.1 | MC 26.2 migration (Fabric Loader 0.19.3, Fabric API 0.150.2+26.2), brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station, shield handle fixes |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
