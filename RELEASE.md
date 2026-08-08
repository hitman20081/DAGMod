# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.10.0 — Race/Class Enchantments & Pale Garden Castle Overhaul
**Released:** 2026-08-04

> **Note for players upgrading from v1.9.1:** No new world required for existing content. Pale Garden chunks that were already generated under the old `minecraft:overworld` noise settings will not retroactively pick up the new custom terrain — only newly generated Pale Garden chunks do. Existing castle structures already on disk are unaffected; only newly generated castles use the multi-piece jigsaw layout and the much wider spacing.

---

## What's New in v1.10.0

### 7 Race/Class-Specific Enchantments
Every race and class now has a dedicated enchantment, obtainable only via the enchanting table or anvil by a matching player:

- **Dwarf — Deep Striker** (weapon, max level 5): +10% attack damage per level while fighting below sea level
- **Elf — Forest's Blessing** (weapon, max level 5): +10% attack damage per level while standing in a forest-tagged biome
- **Orc — Berserker's Fury** (weapon, max level 5): up to +8% attack damage per level, scaling with missing health
- **Human — Versatile** (helmet, max level 5): +5% bonus XP per level, stacking additively on the existing +25% Human race passive
- **Warrior — Immovable** (armor, max level 5): +2% knockback resistance per level
- **Mage — Arcane Amplification** (helmet, max level 5): +10% power per level to all 4 core mage abilities, stacking with Overcharge Dust
- **Rogue — Shadow Step** (armor, max level 5): +5% per level passive chance to dodge a hit entirely, independent of the timed Phantom Dust/Perfect Dodge buffs

If you later reset your race or class, the enchantment stays on the item but stops doing anything until you match again.

### Pale Garden Castle Rebuilt as a Multi-Piece Structure
The Pale Garden castle (and the Medieval castle) no longer spawn as a single static NBT structure — both now assemble from multiple connected jigsaw pieces, giving each spawn more layout variety. Castles are also now a genuinely rare landmark: spacing was raised from 8/2 to 500/499 chunks.

### Pale Garden Has Its Own Terrain
The Pale Garden dimension now generates with dedicated noise settings instead of reusing `minecraft:overworld`, giving it terrain shaping independent of overworld worldgen changes.

### Loot Tables for Medieval and Pale Garden Castle Chests
Both castle structures now have populated loot tables in their chests.

---

## Bug Fixes

- **Pale Garden portal failing to link inside castles** — Chunks near the target weren't force-generated before the portal search ran, so freshly-generated castle portal frames weren't found. `PaleGardenTeleporter` now force-generates the surrounding chunks first, then checks for an active portal, then an inactive Pale Heartstone frame to activate, before falling back to building a new one from scratch
- **Pale Garden return portal creating a stray new portal instead of linking back** — Returning to the Overworld reused your current Pale Garden coordinates as the target, which has nothing to do with where you actually entered from, so it silently built a new portal a short distance from your real one instead of reusing it. The teleporter now remembers the Overworld portal you entered from (persisted per-player) and links the return trip directly back to it
- **`pale_heartstone` item rendering with the wrong model** — was using the `ancient_bone_block` model instead of its own; the `pale_garden_key` item texture was also corrected
- **Mana/Energy/Cooldown HUD bars overlapping vanilla UI** — all three class resource bars overlapped the vanilla air-bubble/mount-health row; raised from `screenHeight - 49` to `screenHeight - 65`

---

## Also in v1.9.x (from v1.9.1 and v1.9.0)

- **Dragon Realm gated at level 50, Bone Realm gated at level 25** — both dimension keys withheld until the level requirement is met
- **Hall of Champions block protection** and **flat, landmark-only biome restriction** (`meadow`, `savanna_plateau`, `cherry_grove`)
- **Structures avoid water and lava**; **Armorer** has its own dedicated structure set
- **Village Inn** spawn hub, **Innkeeper Garrick** race/class selection, **Class Trainer NPC**, **Blacksmith's Anvil**, merchant tier gating
- **Season system fixes** for MC 26.2; **Dragon Realm return portal** honors bed/respawn anchor

---

## Migration Notes

> No new world is required for v1.10.0. Existing Pale Garden and castle chunks keep their current terrain and layout — only newly generated chunks pick up the custom noise settings and multi-piece castle jigsaw.
>
> If upgrading from v1.8.x, see the [v1.9.0 release notes](docs/release-v1.9.0.md) for earlier world generation migration guidance.

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
3. Install the v1.10.0 `.jar`
4. Launch Minecraft 26.2

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
| v1.9.1 | Structure water/lava avoidance, dimension level gates (Dragon 50, Bone 25), Hall of Champions block protection & biome restriction, Armorer structure set, guide book readability |
| v1.9.0 | Village Inn spawn hub, Garrick guild registry, Class Trainer NPC, Blacksmith's Anvil, merchant tier gating, season system fixes |
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
