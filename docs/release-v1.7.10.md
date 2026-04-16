## What's New in v1.7.10

### Skeleton King Chest System Rework

The post-fight reward system for the Skeleton King has been redesigned around pre-placed chests in the throne room.

**Pre-Placed Chests**
Locked chests are now built directly into the Skeleton Throne Room structure. After killing the King, players explore the room and choose which chests to open — rather than chests appearing at the death position.

**Two Keys Per Player**
Every player within 25 blocks of the King's death now receives **2 Skeleton King Chest Keys**, up from 1. With more chests available than keys, players have a meaningful choice about which rewards to go for.

**King's Recall Stone**
Unchanged — every nearby player still receives one Recall Stone on the King's death. Right-click to teleport directly back to the overworld.

---
## Bug Fixes

- **Pre-placed throne room chests always empty** — Locked chests placed inside NBT structures are saved without a loot table tag. The `LockedBoneChestBlockEntity` now automatically assigns the `dagmod:chests/skeleton_king_chest` loot table when loading any unopened Skeleton King chest that is missing one. The fix is self-healing: existing worlds with empty chests will populate correctly the next time those chunks reload. Chests that have already been opened are not affected.

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
2. Remove the old DAGMod .jar from your mods folder
3. Install the v1.7.10 .jar
4. Launch Minecraft

### Migration Notes

- **Existing empty throne room chests** — If you have already generated the Skeleton Throne Room and found the chests empty, they will automatically populate the next time the chunk unloads and reloads (walk ~200 blocks away, then return). No commands required.
- **Key count change** — Players now receive 2 keys instead of 1. This only applies to kills after updating; any keys already in inventory are unaffected.

---
## Known Issues

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
| v1.7.9 | `/seasons` setup command, seasons manual activation, Skeleton Kingdom structure chain, jigsaw fixes |
| v1.7.8 | Skeleton King boss encounter, Skeleton Throne Room structure, Seasons system, major boss stat rebalance |
| v1.7.7 | Class quest chain expansion to 5 quests, per-quest level gates, enchanted book reward fixes, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
