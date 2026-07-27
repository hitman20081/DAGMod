# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.8.3 — Gem Tier System & Enchantment Descriptions
**Released:** 2026-07-21

---

## What's New in v1.8.3

### Gem Tier System

Gems now have four quality tiers — **Cut → Polished → Flawless → Grand** — across all 7 gem types (Citrine, Ruby, Sapphire, Tanzanite, Topaz, Zircon, Pink Garnet). Raw gems are crushed into powder at the Gem Crushing Station, cut into Cut gems at the Gem Cutting Station, then upgraded through tiers at the Gem Polishing Station using Diamond Powder as the catalyst.

Diamond Powder charge economy:
- 1 powder → 4× Cut→Polished upgrades
- 1 powder → 2× Polished→Flawless upgrades
- 1 powder → 1× Flawless→Grand upgrade

Item IDs use the `gem_cut_*`, `gem_polished_*`, `gem_flawless_*`, `gem_grand_*` naming scheme.

### In-Game Enchantment Descriptions

All 26 DAGMod custom enchantments now have descriptions visible directly in-game. Hold **F3+H** to enable advanced tooltips, then hover over any enchanted item to see what each enchantment does — no more hunting the GitHub wiki.

### Quest Book Objective Navigation

The Active Quests page in the quest book now shows **one quest at a time** with ◀ / ▶ navigation buttons. All 10 objectives are now readable regardless of quest length.

---

## Bug Fixes

- **Gem station inventory persistence** — Items placed in any gem crafting station (Cutting, Crushing, Polishing) were lost on logout. All three block entities now correctly save and restore their inventories
- **Gem Crushing Station hitbox** — The hitbox was much taller than the block model. Now matches the flat profile of the current model
- **Tanzanite recipe errors** — Three recipes still referenced the old `dagmod:tanzanite` ID. Updated to `dagmod:gem_cut_tanzanite`; redundant smelting and blasting recipes removed

---

## Migration Notes

> **Breaking change:** All bare gem item IDs have been renamed. If you have old `dagmod:ruby`, `dagmod:citrine`, `dagmod:sapphire`, `dagmod:tanzanite`, `dagmod:topaz`, `dagmod:zircon`, or `dagmod:pink_garnet` items in an existing world, those items will be lost after updating. Replace them with the `gem_cut_*` equivalents before updating, or start fresh.

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
3. Install the v1.8.3 `.jar`
4. Launch Minecraft 26.2

> **Note:** See the migration warning above regarding old gem item IDs.

---

## Known Issues

- **Flawless and Grand gem textures** — Currently placeholder copies of the Cut tier texture. Unique art is planned
- **Enchantment descriptions require F3+H** — Advanced tooltips must be enabled to see descriptions (on by default in Creative mode)
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
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix, Hall of Champions rarity increase |
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
