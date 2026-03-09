# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.6 — Hall of Champions Merchants
**Released:** 2026-03-09

---

## What's New in v1.7.6

### Hall of Champions Merchants

Nine merchant NPCs now live inside the Hall of Champions, giving players a dedicated trading hub accessible from early game.

| Merchant | Speciality |
|---|---|
| Alchemist | Potions, consumables, alchemical supplies |
| Armorer | Armor sets and defensive equipment |
| Enchantsmith | Enchanted gear and enchanting materials |
| Jeweler | Gems, gem-crafted items, and jewelry |
| Miner | Ores, mining supplies, raw materials |
| Lumberjack | Wood, tools, forestry goods |
| Voodoo Illusioner | Dark/mystical items and curiosities |
| Mystery Merchant | Rotating mid-tier weapons and rare equipment |
| Trophy Dealer | High-end boss drops and collectibles |

### Rotating Trade System

Several merchants (including the Mystery Merchant and Trophy Dealer) rotate their inventory on a configurable timer — default every 72 hours, adjustable between 12 and 168 hours. Each merchant type rotates independently, so there's always a reason to check back.

### Merchant Dialogue

Merchants greet players with context-aware messages when opening their trade screen.

### `/merchant` Admin Commands

Server operators can use `/merchant status` to inspect the current rotation state and time until next rotation, or `/merchant rotate` to force an immediate rotation for testing.

---

## Balance Changes

No balance changes this release.

---

## Bug Fixes

No bug fixes this release.

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
3. Install the v1.7.6 `.jar`
4. Launch Minecraft

### Migration Notes

No migration steps required for this update.

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
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
