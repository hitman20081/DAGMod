## What's New in v1.8.0

### Class Resource HUDs

All three classes now have a compact resource bar displayed to the right of the hotbar (same slot, mutually exclusive):

- **Mage** — Blue mana bar showing `current/max` in white text. Max mana and regen both scale with player level
- **Rogue** — Gold energy bar showing `current/max`. Max energy and regen both scale with player level
- **Warrior** — Six color-coded ability boxes (one per ability) showing cooldown state. Each box is bright when ready and darkens with a second countdown when cooling down

#### Level Scaling

| Class | Resource | Formula |
|---|---|---|
| Mage | Max mana | `100 + (level − 1) × 2` |
| Mage | Mana regen | `2 + level × 0.04` /sec |
| Rogue | Max energy | `100 + (level − 1) × 2` |
| Rogue | Energy regen | `5 + level × 0.05` /sec |
| Warrior | Cooldown multiplier | `max(0.6, 1.0 − level × 0.002)` |

Warriors at Level 200 have all cooldowns reduced to 60% of their base values.

---

### New Mage Spell Scrolls

Seven new high-tier spell scrolls are now available for Mage players:

| Scroll | Description |
|---|---|
| Gravity Well | Pulls nearby enemies toward a focal point |
| Chain Lightning | Arcs lightning between up to 5 targets |
| Ice Wall | Summons a wall of ice blocks |
| Meteor Storm | Calls down a barrage of meteors |
| Life Drain | Drains HP from enemies to heal the caster |
| Dimensional Rift | Tears open a rift that teleports enemies |
| Polymorph | Transforms a target into a harmless creature |

---

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

### Bone Dungeon Improvements

- Portal room spawn reliability increased — the portal room was previously inconsistent in certain dungeon seeds
- Treasure density in portal rooms and surrounding chambers increased

---

### Locked Bone Chest Texture

The locked chest in the Bone Realm and bone dungeons now correctly displays the custom `bone_realm_locked_chest` texture in-world. The chest renderer mixin was ported to MC 26.1.2's new rendering pipeline.

---

## Bug Fixes

- **All 9 custom shield handles pointed outward** — Custom shield `items/*.json` definitions were missing `"transformation": {"scale": [1, -1, -1]}`. Fixed for Inferno, Celestial, Crystal, Dragonbone, Frost, Nature, Shadow, Solar, and Stormguard shields
- **Vanilla shield handle also broken** — The mod was overriding `assets/minecraft/items/shield.json` without the orientation transformation. Override removed; vanilla's correct definition now applies
- **Startup model/texture warnings** — Corrected a wrong model reference in `boss_spawn_trigger.json` and replaced a deleted texture reference on the Inferno shield with a valid atlas texture
- **Seasons predicates** — Fixed three consecutive seasons predicate parsing failures (array format, missing `clock` field, unparseable file)
- **Dynamic lighting terrain not updating** — After the 26.1.2 migration, the chunk rebuild call had been removed without a replacement; terrain blocks were not picking up held-item light. Fixed using `LevelRenderer.setSectionRangeDirty()`. A second fix addressed stale light remaining after swapping away from a light source while standing still
- **Shield Bash cooldown incorrect** — Base cooldown was 15 s; corrected to 20 s
- **Warrior cooldown sync thread safety** — `CooldownManager` internal map replaced with `ConcurrentHashMap` to prevent rare concurrent modification crashes

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
- **No world regen required** — Existing worlds load without issue. All player data is preserved
- **Chunk artifacts** — Pre-generated chunks may show minor terrain or lighting inconsistencies. Use [MCA Selector](https://github.com/Queryz/mcaselector) to prune and regenerate unvisited chunks if needed
- **Dynamic Lighting** — Held-item dynamic lighting is fully operational in v1.8.0. Terrain chunk rebuilds and stale-light clearing were both fixed as part of the 26.1.2 migration work

---

## Known Issues

- **MC 26.1.2 chunk artifacts** — Pre-existing chunks may have minor artifacts; MCA Selector chunk pruning recommended for clean terrain in old areas
- Harmless "Block-attached entity at invalid position" warnings in server logs during worldgen (vanilla Minecraft issue, no gameplay impact)
- See https://github.com/hitman20081/DAGMod/issues for anything else reported

---

## Links

- https://github.com/hitman20081/DAGMod
- https://github.com/hitman20081/DAGMod/blob/main/CHANGELOG.md
- https://github.com/hitman20081/DAGMod/blob/main/docs/Home.md
- https://modrinth.com/mod/dag-mod

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | `/seasons` setup command, seasons manual activation, Skeleton Kingdom structure chain, jigsaw fixes |
| v1.7.8 | Skeleton King boss encounter, Skeleton Throne Room structure, Seasons system, major boss stat rebalance |
| v1.7.7 | Class quest chain expansion to 5 quests, per-quest level gates, enchanted book reward fixes, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
