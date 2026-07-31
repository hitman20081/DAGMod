# v1.9.1 Release Notes
**Released:** 2026-07-31

---

## What's New

### Dragon Realm Gated at Level 50
The Dragon Realm is no longer freely accessible. The `red_dragon_fury` quest (which rewards the Dragon Key) does not appear until level 50. Attempting to activate a portal or walk through one below level 50 is blocked with a clear message. Exiting the Dragon Realm is always allowed.

### Hall of Champions Block Protection
Survival players can no longer mine blocks inside the Hall of Champions. The structure is sacred ground — its blocks cannot be removed without Creative mode.

### Structures No Longer Spawn on Water or Lava
All DAGMod structure sets now use the `avoid_water` flag. Inns, Armorers, merchants, castles, and other structures no longer generate on or near surface water lakes and lava pools.

### Hall of Champions Now in Flat, Landmark Biomes Only
The Hall of Champions is restricted to `meadow`, `savanna_plateau`, and `cherry_grove` — open, flat biomes that fit the large structure footprint and feel worthy of a journey.

### Armorer Has Its Own Structure Set
The Armorer NPC has been moved out of the shared merchant pool and given a dedicated structure set. It now consistently generates near the Inn in the same biomes rather than as one of four random merchant spawns.

### Bone Realm Gated at Level 25
The `rumours_of_the_bone_king` quest (which rewards the Bone Dungeon Locator) now requires level 25 before it appears in the quest log.

### Hall Locator Moved to Garrick Reward
The Hall Locator compass is no longer handed out on first join. Garrick gives it when players complete all three tutorial tasks.

### Guide Book Readability Fixed
All guide book headers and labels now use colors readable on the parchment background. Previous bright codes were invisible or near-invisible on light pages.

---

## Bug Fixes

- **Castle exclusion zone out of range** — `ExclusionZone.CODEC` enforces chunk_count [1:16]; both castle sets had 25. Capped to 16
- **Duplicate structure set salt** — `castle_medieval` and `castle_pale_garden` shared salt `1649512345`. `castle_pale_garden` given unique salt `1649512346`
- **`getStructureWithPieceAt` MC 26.2 API** — Dropped `ResourceKey<Structure>` overload replaced with `Predicate<Holder<Structure>>` in `ProtectedStructureHandler`
- **River biome in village_npc tag** — Inns and NPC buildings no longer generate in rivers
- **Armorer biome mismatch** — Armorer was spawning in savanna biomes where the Inn never appears; now uses `#dagmod:has_structure/village_npc`

---

## Migration Notes

> No new world required. All changes apply to newly generated chunks and existing game systems immediately.
>
> If upgrading from v1.8.x, see [v1.9.0 release notes](release-v1.9.0.md) for world generation migration guidance.

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

## Previous Releases

| Version | Summary |
|---|---|
| v1.9.0 | Village Inn spawn hub, Garrick guild registry, Class Trainer NPC, Blacksmith's Anvil, merchant tier gating, season system fixes |
| v1.8.3 | Gem tier system (Cut→Polished→Flawless→Grand), in-game enchantment descriptions, quest book navigation |
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix |
| v1.8.1 | MC 26.2 migration, brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain |
| v1.7.8 | Skeleton King boss encounter, boss rebalance |
