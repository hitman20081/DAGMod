# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.9 — Seasons Setup System & Skeleton Kingdom Structure Chain
**Released:** 2026-04-02

---

## What's New in v1.7.9

### Seasons Setup System

The seasons system now requires manual operator setup before it activates:

- Server operators run `/seasons` to open an interactive clickable configuration menu
- Configure season length (7, 14, 20, or 28 days) and toggle weather effects, crop growth, temperature, and season display independently
- Settings persist across server restarts — no more resetting to defaults on reload
- On first load, all players see a notice directing operators to `/seasons` to configure
- All configuration changes take effect immediately and refresh the menu automatically

### Skeleton Kingdom Structure Chain

The Skeleton Kingdom jigsaw structure now generates a full connected sequence:

- **City Center** → **Entry Room** → **Hallway** → **Throne Room**
- Fixed jigsaw anchor mismatch (`minecraft:city_anchor` → `dagmod:city_anchor`) that previously caused the entire structure to fail generation
- Three new template pool JSONs added: `entry_room`, `hallway`, `throne_room`

---

## Bug Fixes

- **Seasons settings resetting on restart** — `load.mcfunction` previously overwrote all config on every load; now uses `unless score` guards to preserve saved settings
- **Skeleton Kingdom failing to generate** — `start_jigsaw_name` in `skeleton_kingdom.json` didn't match the jigsaw block name in the city center NBT; corrected to `dagmod:city_anchor`
- **City center referencing non-existent NBTs** — Removed `city_center_2` and `city_center_3` from the city center pool (files never existed)

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
3. Install the v1.7.9 `.jar`
4. Launch Minecraft

### Migration Notes

- **Seasons** — Seasons will not auto-start on existing worlds. A server operator must run `/seasons` and click **Start Seasons** to activate the system. Previous unconfigured worlds will show a notice to all players on load.
- **Skeleton Kingdom** — The structure fix only applies to newly generated chunks. Existing worlds can use `/locate structure dagmod:skeleton_kingdom` to find a newly generated instance.

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
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
