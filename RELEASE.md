# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.8.0 — MC 26.1.2 Migration, Gem Powder System & Shield Fixes
**Released:** 2026-06-04

---

## What's New in v1.8.0

### Minecraft 26.1.2 Migration

DAGMod has been fully ported to **Minecraft 26.1.2** (Mojang official mappings). This is a required update — v1.7.x is not compatible with 26.1.2 clients or servers.

Updated dependencies:
- Fabric Loader 0.19.2
- Fabric API 0.149.1+26.1.2
- Java 25 (temurin-25.0.3)

---

### Potent Sulfur Powder

A new high-tier crafting material is now available:

- **Recipe**: 9× Sulfur Powder → 1 Potent Sulfur Powder (crafting table)
- Used as a premium component in fire-themed crafting recipes (Inferno armor, fire weapons)
- Sulfur block and item now have complete registration, models, and textures

---

### Gem Powder System

All 8 gem types can now be processed into powder using the new **Gem Crushing Station**:

- **Gem Crushing Station** — Place a Crushing Hammer in the tool slot, then insert a raw gem to grind it into powder. Faces the player on placement like a furnace
- **Crushing Hammer** — New craftable tool (iron ingots + stick) required to run the station
- **Ruby, Sapphire, and Topaz Powder** — The final 3 gem powder forms, completing the full set

Each powder can be brewed with an Awkward Potion at a brewing stand:

| Gem Powder | Potion of... | Effects |
|---|---|---|
| Amethyst | Revival | Regeneration II (45s) + Absorption (2min) |
| Citrine | Clarity | Night Vision (5min) + Haste (3min) |
| Diamond | Fortitude | Resistance (5min) + Health Boost (5min) |
| Emerald | Growth | Strength (3min) + Regeneration (3min) |
| Quartz | Swiftness | Haste II (3min) + Jump Boost (3min) |
| Ruby | Fury | Strength II (30s) + Fire Resistance (3min) |
| Sapphire | the Deep | Speed II (3min) + Water Breathing (3min) |
| Topaz | Fortune | Luck (5min) + Haste II (2min) |

---

### Bone Dungeon Improvements

- Portal room spawn reliability increased — the portal room was previously inconsistent in certain dungeon seeds
- Treasure density in portal rooms and surrounding chambers increased

---

### Locked Bone Chest Texture

The locked chest in the Bone Realm and bone dungeons now correctly displays the custom `bone_realm_locked_chest` texture in-world. The chest renderer mixin was ported to MC 26.1.2's new `extractRenderState`/`submit` rendering pipeline.

---

## Bug Fixes

- **All 9 custom shield handles pointed outward** — Custom shield `items/*.json` definitions were missing `"transformation": {"scale": [1, -1, -1]}`. Fixed for Inferno, Celestial, Crystal, Dragonbone, Frost, Nature, Shadow, Solar, and Stormguard shields
- **Vanilla shield handle also broken** — The mod was overriding `assets/minecraft/items/shield.json` without the orientation transformation. Override removed; vanilla's correct definition now applies
- **Startup model/texture warnings** — Corrected a wrong model reference in `boss_spawn_trigger.json` and replaced a deleted texture reference on the Inferno shield with a valid atlas texture
- **Seasons predicates** — Fixed three consecutive seasons predicate parsing failures (array format, missing `clock` field, unparseable file)
- **Dynamic lighting terrain not updating** — After the 26.1.2 migration, `scheduleBlockRenders` had been removed without a replacement; terrain blocks were not picking up the held-item light boost (entity rendering still worked). Fixed using `LevelRenderer.setSectionRangeDirty()`. A second fix addressed stale light remaining after swapping away from a light source while standing still
- **Gem Crushing Station wrong texture** — Block model referenced a nonexistent texture name; corrected to `gem_crushing_station_texture`
- **Citrine Powder missing from Creative Tab** — Was omitted from the item tab registration

---

## Requirements

| Component | Version |
|---|---|
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.2+ |
| Fabric API | 0.149.1+26.1.2 |

---

## Updating

Your existing progress is safe — race, class, level, and quest data all persist across updates.

1. Back up your world
2. Remove the old DAGMod `.jar` from your mods folder
3. Install the v1.8.0 `.jar` and ensure your Fabric Loader is updated to 0.19.2+
4. Launch Minecraft 26.1.2

### Migration Notes

- **Minecraft version change** — This update requires Minecraft 26.1.2. You must update your client and server
- **No world regen required** — Existing worlds load without issue. Race, class, level, and quest data are fully preserved
- **Chunk artifacts** — Existing pre-generated chunks may show minor terrain or lighting inconsistencies at chunk borders. If you notice issues, use [MCA Selector](https://github.com/Querys/mcaselector) to prune and regenerate affected unvisited chunks
- **Dynamic Lighting** — Terrain lighting (`LevelRenderer.setSectionRangeDirty()`) and stale-light clearing are fully fixed in this release

---

## Known Issues

- **MC 26.1.2 chunk artifacts** — Pre-existing chunks may have minor artifacts; recommend MCA Selector chunk pruning for affected areas
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
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
