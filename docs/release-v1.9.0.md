# v1.9.0 Release Notes
**Released:** 2026-07-28

---

## What's New

### Village Inn as the Spawn Hub
World spawn now generates near a **Village Inn** rather than the Hall of Champions. The inn sits within a few hundred blocks of spawn and is your first stop for lodging, Innkeeper Garrick, and initial quests. Use the **Hall Locator** item to navigate to the Hall of Champions when you're ready.

### Hall of Champions is Now a Destination
The Hall of Champions has been moved much farther from spawn (~2000 block spacing, up from ~1000). It is a mid-to-late-game landmark — not something you walk into on day one.

### Innkeeper Garrick — Full Guild Registry
Innkeeper Garrick (found at the Village Inn) now handles **race and class selection** through interactive chat dialogue. He presents stat summaries for each option and hands out starter gear immediately on selection. Physical altars at the Hall of Champions remain functional as an alternative.

### Class Trainer NPC
A dedicated **Class Trainer NPC** at the Hall of Champions handles all class quest chains. Right-click to see your full class chain progress with clear status indicators. After Garrick's three tutorial tasks, he directs you to the Class Trainer.

### Blacksmith's Anvil
A permanent, indestructible **Blacksmith's Anvil** block is now available. Opens the standard anvil GUI for renaming, enchantment combining, and repair. Cannot be broken in Survival.

### Merchant Tier Gating
Advanced and legendary stock on Hall of Champions merchants is now **locked behind quest completion**. Merchants hint at what quests unlock their premium stock. Basic goods remain always available.

### Season System Operational
The four-season system is now fully working:
- Crop growth rates apply correctly per season (Spring fastest, Winter slowest)
- Sleeping through the night advances the season day counter
- Seasonal weather patterns and biome-specific effects fire correctly
- Season transition messages accurately describe each season's mechanics

### Dragon Realm Return Portal
Returning from the Dragon Realm now takes you to your **bed or respawn anchor** (if set in the overworld), or to configured world spawn. No longer hardcoded to 0, 64, 0.

---

## Bug Fixes

- **Season predicates** — All 20 predicate files used wrong key for MC 26.2 (`"type"` instead of `"condition"`), disabling all weather and biome effects. Corrected
- **Season gamerule name** — `randomTickSpeed` renamed to `random_tick_speed` (MC 26.2). Growth rates now apply correctly
- **Sleep detection** — Season day advancement now uses scoreboard stat comparison for reliable sleep detection
- **Blacksmith's Anvil hitbox** — Corrected to exact model geometry for both orientations
- **Blacksmith's Anvil rotation** — Fixed placement so the long axis faces correctly
- **Blacksmith's Anvil GUI** — Menu no longer closes immediately on open
- **Citrine quest gating** — Unlock conditions corrected
- **Class Trainer stale progress** — Quest progress is now refreshed before checking completion state
- **Quest Block texture** — Now displays the custom texture instead of vanilla bookshelf textures
- **NPCs now stationary** — All 16 NPC entity classes no longer wander

---

## Migration Notes

> **New world recommended.** Village Inn spawn hub and HoC rarity changes only affect newly generated chunks.
>
> **Updating an existing world:**
> 1. Back up your world folder
> 2. Use [MCA Selector](https://github.com/Querys/mcaselector) to delete unvisited chunks near spawn so they regenerate
> 3. Install the new jar — regenerated chunks will have the Village Inn near spawn
>
> No item IDs changed. Existing inventories and data carry over without loss.

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
5. Start a new world, or use MCA Selector to prune spawn chunks (see above)

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.8.3 | Gem tier system (Cut→Polished→Flawless→Grand), in-game enchantment descriptions, quest book navigation |
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix |
| v1.8.1 | MC 26.2 migration, brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain |
| v1.7.8 | Skeleton King boss encounter, boss rebalance |
