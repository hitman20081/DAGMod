# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.8.1 — MC 26.2 Migration & Brimstone Rename
**Released:** 2026-06-16

---

## What's New in v1.8.1

### Minecraft 26.2 Migration

DAGMod has been fully ported to **Minecraft 26.2** ("Chaos Cubed"). This is a required update — v1.8.0 is not compatible with MC 26.2 clients or servers.

Updated dependencies:
- Fabric Loader 0.19.3
- Fabric API 0.150.2+26.2
- Fabric Loom 1.17.11

---

### Brimstone Rename

The old `dagmod:sulfur` and `dagmod:potent_sulfur` blocks and items have been repurposed and renamed:

- `dagmod:sulfur` → **`dagmod:brimstone`**
- `dagmod:potent_sulfur` → **`dagmod:pure_brimstone`**

Recipes updated:
- **Inferno armor and fire weapons** now use vanilla `minecraft:sulfur` and `minecraft:potent_sulfur`
- **High-end fire magic** (fireball scrolls, Meteor Storm, Phoenix potions) now requires the new brimstone materials

---

## Bug Fixes

- **MC 26.2 API: EntityType static fields removed** — All `EntityType.ZOMBIE`, `EntityType.SKELETON`, etc. static references replaced with registry lookups across `KillObjective`, `QuestRegistry`, and `JobRegistry`
- **MC 26.2 API: WeatheringCopperCollection type** — `Items.LIGHTNING_ROD`, `Items.COPPER_BLOCK`, and `Blocks.COPPER_BLOCK` changed type in MC 26.2; replaced with registry lookups in all NPC and merchant classes
- **MC 26.2 API: Removed Items statics** — `Items.WHITE_WOOL`, `Items.WHITE_BED`, `Items.RED_BED` removed; replaced with registry lookups in `LumberjackNPC` and `VillageMerchantNPC`
- **MC 26.2 API: EntityType.LIGHTNING_BOLT removed** — Replaced with registry lookup and unchecked cast in `SpellScrollItem`
- **MC 26.2 API: Options.hideGui removed** — HUD hide check updated to `client.gui.hud.isHidden()` in `ProgressionHUD` and `PartyHUD`
- **MC 26.2 API: Minecraft.setScreen() removed** — `QuestBookClientHandler` updated to `setScreenAndShow()`
- **MC 26.2 API: LevelRenderer.setSectionRangeDirty() moved** — Dynamic lighting now correctly calls `ClientLevel.setSectionRangeDirty()` (method moved from `LevelRenderer` to `ClientLevel` in MC 26.2)
- **MC 26.2 data: Enchantment entity predicate format** — Entity type tag predicates inside enchantment JSON effects changed from `{"type": "#minecraft:tag"}` to `{"minecraft:entity_type": "#minecraft:tag"}`. Fixed in 6 enchantments: `bane_of_white_walker`, `lights_blessing`, `rise_of_the_zombies`, `siphon_enchantment`, `summon_enchantment`, `xdamage_enchantment`
- **MC 26.2 data: Tree feature missing field** — `charred_tree` configured feature updated with the new required `below_trunk_provider` field

---

## Requirements

| Component | Version |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.150.2+26.2 |

---

## Updating

Your existing progress is safe — race, class, level, and quest data all persist across updates.

1. Back up your world
2. Remove the old DAGMod `.jar` from your mods folder
3. Install the v1.8.1 `.jar` and update Fabric Loader to 0.19.3+
4. Launch Minecraft 26.2

### Migration Notes

- **Minecraft version change** — This update requires Minecraft 26.2. You must update your client and server
- **Brimstone rename** — If you have `dagmod:sulfur` or `dagmod:potent_sulfur` blocks or items in an existing world, they will become air/missing items after updating. Pick them up before updating or expect them to disappear
- **No world regen required** — All other progress (race, class, level, quests) carries over without issue

---

## Known Issues

- **Pre-existing chunk artifacts** — Chunks generated on older versions may show minor terrain or lighting artifacts at borders. Use [MCA Selector](https://github.com/Querys/mcaselector) to prune unvisited chunks if needed
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
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station, shield handle fixes |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
