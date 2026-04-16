# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.10 — Quest System Overhaul, Job Board Expansion & Dynamic Lighting
**Released:** 2026-04-13

---

## What's New in v1.7.10

### Dynamic Held-Item Lighting

No more stopping to place torches every few steps. Hold any light-emitting item and it illuminates the area around you in real time:

- **Torch** — radius 14 blocks
- **Lantern / Sea Lantern / Shroomlight / Jack o'Lantern / Campfire / Glowstone / Nether Star** — radius 15 blocks (full range)
- **Lava Bucket / End Rod** — radius 12 blocks
- **Soul Torch / Soul Lantern / Soul Campfire** — radius 10 blocks (dim blue glow)
- **Blaze Rod / Fire Charge** — radius 8 / 10 blocks
- **Glow Berries** — radius 5 blocks
- **Redstone Torch / Glowstone Dust** — radius 5 / 4 blocks

Both main hand and off hand are checked; whichever is brighter wins. Brightness falls off smoothly to zero at the edge of the radius. Self-contained — no external mod required.

---

### Quest Book Upgrade System Rework

The quest book upgrade system has been completely overhauled to work correctly:

- **Chain completion is now the sole upgrade gate** — The old threshold-based path (5/15/25 quests completed) has been removed entirely. It was never reliable, using a stub approximation instead of real tracking
- **Physical book swap on upgrade** — When a chain completes and your book upgrades, the old book is automatically removed from your inventory and replaced with the new tier. You will never accumulate outdated books
- **Adventurer's Path now 4 quests** — `The Beginning` (gather 5 logs) has been added as the second quest in the chain: `Garrick's Welcome` → `The Beginning` → `Equip Yourself` → `Ready for Adventure`
- **First quest auto-assigned** — When you combine Garrick's 3 Notes into a Novice Quest Book at the Quest Block, `Garrick's Welcome` is automatically started. No need to find and accept it manually

### Job Board Expansion

The Job Board has grown from 4 jobs to **19 jobs** across all accessible tiers:

**NOVICE** (available from the start)
- Gather Cobblestone, Hunt Zombies, Collect Wheat *(existing)*
- **New:** Chop Wood, Catch Fish, Hunt Spiders, Gather Wool, Collect Sand, Skeleton Patrol

**APPRENTICE** (level 10+, Apprentice book required)
- Mine Raw Iron *(existing)*
- **New:** Mine Coal, Hunt Creepers, Collect Leather, Mine Raw Gold, Gather Pumpkins

**EXPERT** (level 25+, Expert book required)
- Deep Miner *(existing)*
- **New:** Hunt Endermen, Mine Diamonds, Hunt Blazes, Collect Obsidian

### Early-Game Quest Flexibility

Six quests that previously required specific biome-locked wood types now accept any equivalent material:

- `The Beginning`, `Wanderer` — any logs
- `Seedling`, `Roots Run Deep` — any saplings / any logs
- `Village Founder`, `Village Builder` — any planks
- Garrick's tutorial Task 1 — any logs (was Oak only)

---

## Bug Fixes

- **Pre-placed Skeleton King throne room chests always empty** — Chests placed inside the Throne Room NBT structure were never saving their loot table tag. `LockedBoneChestBlockEntity` now auto-assigns the correct loot table on chunk load for any unopened chest missing one
- **Quest item consumption not syncing to client** — `consumeItems()` was calling `stack.decrement()` directly on the `ItemStack` object, which never triggered `markDirty()` on the inventory. Items disappeared server-side but reappeared visually until the next forced sync. Fixed across `CollectObjective`, `TagCollectObjective`, `InnkeeperGarrickNPC`, and `QuestBlock`
- **Quest book upgrade conflict** — Two upgrade paths competed silently; the threshold path could fire before chain completion, or not at all. Fully removed in favour of chain-based upgrades
- **`The Beginning` quest not counting toward Adventurer's Path** — The quest was re-enabled but not added to the chain definition, so completing it never progressed the chain

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
3. Install the v1.7.10 `.jar`
4. Launch Minecraft

### Migration Notes

- **Existing Skeleton King throne room chests** — If you have an existing world with pre-placed throne room chests that are empty, they will automatically receive their loot table the next time those chunks are loaded. No commands needed
- **Quest book upgrade** — Players who were waiting on the old threshold upgrade prompt will not see it anymore. If your chain quests are all completed, the upgrade will trigger on your next quest turn-in. If not, complete the remaining chain quests to earn the book
- **Active quests** — All active and completed quest data is preserved. No resets

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
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain, jigsaw anchor fix |
| v1.7.8 | Skeleton King boss encounter, boss rebalance, seasons datapack |
| v1.7.7 | Quest system overhaul, class chain expansion to 5 quests, dragon crash fix |
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
