# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.5 — Bone Realm Polish & Boss Room Spawning
**Released:** 2026-03-06

---

## What's New in v1.7.5

### Skeleton Lord Boss Spawning

The Skeleton Lord now spawns automatically when a survival player enters the bone dungeon boss room — no command blocks required. A custom Boss Spawn Trigger block placed in the boss room NBT detects player proximity (12-block radius) and spawns the Skeleton Lord with a wither spawn sound and soul fire particles. The trigger block is removed after firing so the boss only spawns once per room.

### Necrotic Key in Bone Realm Chest

The Necrotic Key is now a guaranteed drop in the Bone Realm locked chest, giving players a reliable way to obtain it after defeating the Skeleton Lord.

### Hall of Champions Locator Improved

The Hall of Champions locator now works like the Bone Dungeon locator — it searches asynchronously and displays the exact coordinates and distance directly in chat. No more telling players to type a command.

> *Lost your locator? Use: `/locate structure dagmod:hall_of_champions`*

---

## Balance Changes

No balance changes this release.

---

## Bug Fixes

**Dragon Key recipe** — Fixed "Invalid pattern: each row must be the same width" error. The third row of the recipe was 1 character instead of 3.

**Bone Realm portal height** — Portals now generate at surface level (~Y=110) instead of deep underground. The destination Y is now set to 128 before ground-searching, so the portal lands on actual terrain.

**Duplicate overworld portal on return** — Returning through the Bone Realm portal no longer creates a second overworld portal. The search range was expanded from ±20 to ±256 blocks vertically so the original portal is always found regardless of height differences between dimensions.

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
3. Install the v1.7.5 `.jar`
4. Launch Minecraft

### Migration Notes

**Bone Realm portals**: Delete any existing Bone Realm portals and re-enter to regenerate them at the correct height. Old portals created before this update will remain where they are.

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
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
