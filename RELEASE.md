# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.8 — Skeleton King Boss Encounter & Boss Rebalance
**Released:** 2026-03-31

---

## What's New in v1.7.8

### Skeleton King Boss Encounter

The Skeleton King now has a full boss encounter system:

- **Proximity trigger** — A custom spawn trigger block in the throne room detects nearby survival players (12-block radius) and spawns the King automatically
- **Room sealing** — On spawn, all level-0 light blocks within a 70×30×70 area of the throne are replaced with barrier blocks, locking players inside the throne room for the duration of the fight
- **Room unsealing** — Barrier blocks are removed on the King's death

### Skeleton Throne Room Structure

A new Jigsaw structure spawns once in the Bone Realm dimension using `concentric_rings` placement. It consists of three NBT pieces: the throne room, a hallway, and a teleport room.

### King's Recall Stone

On the Skeleton King's death, every player within 25 blocks receives a King's Recall Stone. Right-clicking the stone teleports the player directly to the overworld at the equivalent X/Z coordinates above ground — no portal is created. The stone is consumed on use.

### One Chest Per Player

On the King's death, each nearby player (within 25 blocks) receives one Skeleton King Chest Key, and one locked chest spawns per player in a centred row near the death position.

### Seasons Datapack

A four-season cycle system is now bundled with the mod. Each season lasts 20 Minecraft days (configurable) and affects crop growth rates, weather patterns, temperature, player status effects, and animal behaviour. See admin commands via `/function seasons:commands/help`.

---

## Balance Changes

All bosses were critically undertuned and have been significantly buffed:

| Boss | HP | Attack | Armor | Toughness |
|---|---|---|---|---|
| Skeleton Summoner | 30 → **120** | 4 → **6** | 8 → **10** | 2 → **3** |
| Skeleton Lord | 45 → **200** | 6 → **10** | 15 → **18** | 4 → **6** |
| Skeleton King | 60 → **300** | 8 → **13** | 20 → **22** | 5 → **8** |
| Wild Dragon | 80 → **160** | 7 → **10** | 2 → **6** | 6 → **8** |
| Dragon Guardian | 300 → **400** | 12 → **16** | 12 → **16** | 10 → **12** |

The Skeleton King is now on par with the Dragon Guardian as a proper end-game boss fight.

---

## Bug Fixes

- **Barrier seal not reaching doorways** — Seal scan range expanded from ±20/±4 to ±35/±15 blocks; previous range missed doorways placed 30+ blocks from the trigger
- **Recall Stone teleporting below bedrock** — Destination chunk is now force-generated before querying the heightmap; fallback to Y=64 if chunk returns world bottom
- **Locked chests relocking on world reload** — `LockedBoneChestBlockEntity` now persists the `unlocked` flag via `writeData`/`readData`; chests stay open after logging out
- **Seasons toggle commands always enabling** — Race condition fixed; all three toggle functions now use a temp variable to snapshot state before modifying it
- **Seasons help listing a non-existent command** — Removed `/function seasons:commands/set_speed` (file never existed)
- **Seasons setup not initialising display toggle** — `setup.mcfunction` now sets `#enable_display` to 1 on initialisation

---

## Requirements

| Component | Version |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.141.1+1.21.11 |

---

## Updating

Your existing progress is safe — race, class, level, and quest data all persist across updates.

1. Back up your world
2. Remove the old DAGMod `.jar` from your mods folder
3. Install the v1.7.8 `.jar`
4. Launch Minecraft

### Migration Notes

- **Skeleton Throne Room** — The structure uses `concentric_rings` placement and will only spawn in newly generated areas of the Bone Realm. Existing worlds will need to explore new chunks in the Bone Realm to find it, or use `/locate structure dagmod:skeleton_throne_room`
- **Boss health increases** — Any currently spawned boss entities will retain their old health values until they are killed and respawned

---

## Known Issues

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
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
