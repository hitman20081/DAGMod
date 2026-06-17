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
- **High-end fire magic** (fireball scrolls, Meteor Storm, Phoenix potions) now requires brimstone materials

> **World note:** If you have `dagmod:sulfur` or `dagmod:potent_sulfur` in an existing world, those items will become invalid after updating. Pick them up before updating or expect them to disappear.

---

## Bug Fixes

- **MC 26.2: EntityType static fields removed** — All `EntityType.ZOMBIE`, `EntityType.SKELETON`, etc. replaced with registry lookups in `KillObjective`, `QuestRegistry`, and `JobRegistry`
- **MC 26.2: WeatheringCopperCollection type** — `Items.LIGHTNING_ROD`, `Items.COPPER_BLOCK`, `Blocks.COPPER_BLOCK` changed return type; replaced with registry lookups in NPC and merchant classes
- **MC 26.2: Removed Items statics** — `Items.WHITE_WOOL`, `Items.WHITE_BED`, `Items.RED_BED` replaced with registry lookups in `LumberjackNPC` and `VillageMerchantNPC`
- **MC 26.2: EntityType.LIGHTNING_BOLT removed** — Replaced with registry lookup in `SpellScrollItem`
- **MC 26.2: Options.hideGui removed** — HUD visibility check updated to `client.gui.hud.isHidden()` in `ProgressionHUD` and `PartyHUD`
- **MC 26.2: Minecraft.setScreen() removed** — `QuestBookClientHandler` updated to `setScreenAndShow()`
- **MC 26.2: LevelRenderer.setSectionRangeDirty() moved** — Dynamic lighting now uses `ClientLevel.setSectionRangeDirty()` (method moved from `LevelRenderer` to `ClientLevel` in MC 26.2)
- **MC 26.2: Enchantment entity predicate format** — `{"type": "#minecraft:tag"}` → `{"minecraft:entity_type": "#minecraft:tag"}` in 6 enchantments: `bane_of_white_walker`, `lights_blessing`, `rise_of_the_zombies`, `siphon_enchantment`, `summon_enchantment`, `xdamage_enchantment`
- **MC 26.2: Tree feature missing field** — `charred_tree` configured feature updated with the required `below_trunk_provider` field

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
