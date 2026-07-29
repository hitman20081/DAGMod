# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.9.0 — Village Inn Hub, Season Fixes, Class Trainer & More
**Released:** 2026-07-28

> **Note for players upgrading from v1.8.x:** A new world is recommended but not required. Existing worlds will not have the Village Inn placed near spawn unless you delete and regenerate spawn-area chunks (use [MCA Selector](https://github.com/Querz/mcaselector) to prune unvisited chunks). The Hall of Champions will also not relocate in existing worlds. All other changes apply immediately on update. **Back up your world before updating.**

---

## What's New in v1.9.0

### Village Inn as the Spawn Hub
World spawn now generates near a **Village Inn** rather than the Hall of Champions. The inn sits within a few hundred blocks of spawn — it's your first stop for lodging, Innkeeper Garrick, and initial quests. Use the **Hall Locator** item Garrick provides to navigate to the Hall of Champions from there.

### Hall of Champions is Now a Destination
The Hall of Champions has been moved much farther from spawn (~2000 block spacing, up from ~1000). It's now a mid-to-late-game landmark worth the journey — not something you stumble into on day one.

### Innkeeper Garrick — Full Guild Registry
Innkeeper Garrick (at the Village Inn) now handles **race and class selection** through interactive chat dialogue. He presents stat summaries for each option and hands out starter gear immediately on selection. The physical altars in the Hall of Champions remain functional as an alternative.

### Class Trainer NPC
A dedicated **Class Trainer NPC** at the Hall of Champions handles all class quest chains. Right-click to see your full class chain progress with clear status indicators (complete, ready to turn in, in progress, available, locked). After Garrick's three tutorial tasks, he directs you to the Class Trainer.

### Blacksmith's Anvil
A permanent, indestructible **Blacksmith's Anvil** block (`dagmod:blacksmith_anvil`) is now available. Opens the standard anvil GUI for renaming, enchantment combining, and repair. Cannot be broken in Survival. Rotates correctly on placement.

### Merchant Tier Gating
Advanced and legendary stock on Hall of Champions merchants is now **locked behind quest completion**. Basic goods remain always accessible. Merchants hint at what quests unlock their premium stock.

### Season System Operational
The four-season system (Spring, Summer, Fall, Winter) is now fully working:
- **Crop growth** — Correct growth rates apply each season (Spring fastest, Winter slowest)
- **Sleep advances seasons** — Sleeping through the night now counts toward the day total
- **Weather** — Seasonal weather patterns and biome-specific effects fire correctly
- **Accurate announcements** — Season transition messages now describe each season's actual mechanics

### Dragon Realm Return Portal Fixed
Returning from the Dragon Realm now takes you to your **bed or respawn anchor** if set, or to the configured world spawn — no longer hardcoded to coordinates 0, 64, 0.

---

## Bug Fixes

- **Season predicates** — All 20 predicate files were using the wrong key for MC 26.2 (`"type"` instead of `"condition"`), silently disabling all weather, biome checks, and random effects. Corrected
- **Season gamerule name** — `randomTickSpeed` renamed to `random_tick_speed` (MC 26.2 snake_case). Growth rates now actually apply
- **Sleep detection** — Sleep-based season advancement now uses scoreboard stat comparison instead of a broken day-time query
- **Blacksmith's Anvil hitbox** — Corrected to exact model geometry for both orientations
- **Blacksmith's Anvil rotation** — Fixed placement direction so the long axis faces correctly
- **Blacksmith's Anvil GUI** — Menu no longer closes immediately on open (stillValid override)
- **Citrine quest gating** — Unlock conditions corrected
- **Class Trainer stale objective progress** — Quest progress is now refreshed before checking completion state
- **Quest Block texture** — Now displays custom texture correctly instead of vanilla bookshelf/oak_planks
- **All NPCs now stationary** — 16 NPC entity classes no longer wander; they stand and look at nearby players

---

## Migration Notes

> **New world recommended.** The Village Inn spawn hub and Hall of Champions rarity changes only affect newly generated chunks. Existing worlds keep the Hall at their original location.
>
> **If you want to update an existing world without starting over:**
> 1. Back up your world folder
> 2. Use [MCA Selector](https://github.com/Querys/mcaselector) to delete unvisited chunks around spawn so they regenerate under the new rules
> 3. Install the new jar and load the world — regenerated chunks will have the Village Inn near spawn
>
> No item IDs changed in this release. Existing inventories, chests, and data will carry over without loss.

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
3. Install the v1.9.0 `.jar`
4. Launch Minecraft 26.2
5. Start a new world, or use MCA Selector to prune spawn chunks in an existing world (see Migration Notes)

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
