## What's New in v1.8.3

### Gem Tier System

All 7 gem types — Citrine, Ruby, Sapphire, Tanzanite, Topaz, Zircon, Pink Garnet — now have four quality tiers: **Cut → Polished → Flawless → Grand**. Item IDs follow the `gem_cut_*`, `gem_polished_*`, `gem_flawless_*`, `gem_grand_*` naming scheme.

Upgrade gems at the **Gem Polishing Station** using Diamond Powder:
- 1 powder → 4× Cut→Polished
- 1 powder → 2× Polished→Flawless
- 1 powder → 1× Flawless→Grand

### In-Game Enchantment Descriptions

All 26 DAGMod custom enchantments now display a description tooltip in-game. Press **F3+H** to enable advanced tooltips, then hover over any enchanted item. No more hunting the GitHub wiki to find out what an enchantment does.

### Quest Book Navigation

The Active Quests page in the quest book now shows **one quest at a time** with ◀ / ▶ navigation buttons at the bottom. All objectives for the selected quest are always fully visible, regardless of how many objectives the quest has.

---

## Bug Fixes

- **Gem station inventory persistence** — Items in the Gem Cutting Station, Gem Crushing Station, and Gem Polishing Station were lost on logout. All three now correctly save and load their inventories
- **Gem Crushing Station hitbox** — The collision box was oversized (matching an older, taller model). Now correctly matches the current flat block model
- **Tanzanite recipe errors on load** — Three recipe files still referenced the removed `dagmod:tanzanite` ID, causing errors on world load. Updated to `dagmod:gem_cut_tanzanite`; redundant smelting and blasting recipes removed

---

## Migration Notes

> **Breaking change:** All bare gem item IDs have been renamed. If you have `dagmod:ruby`, `dagmod:citrine`, `dagmod:sapphire`, `dagmod:tanzanite`, `dagmod:topaz`, `dagmod:zircon`, or `dagmod:pink_garnet` items in an existing world, those items will be lost after updating. Replace them with `gem_cut_*` equivalents before updating, or start fresh.

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

> See migration notes above regarding gem item IDs before updating an existing world.

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix, Hall of Champions rarity increase |
| v1.8.1 | MC 26.2 migration, brimstone rename, all MC 26.2 API fixes |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
