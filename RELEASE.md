# DAGMod Release Notes

<!-- =========================================================
  HOW TO UPDATE THIS FILE FOR A NEW RELEASE:
  1. Replace the version, title, and date at the top
  2. Update the "major jump" warning if there's a Modrinth version gap
  3. Replace all sections below with the new release content
  4. Move the old release header + summary to ## Previous Releases at the bottom
  ========================================================= -->

## v1.7.7 — Quest System Overhaul & Dragon Crash Fix
**Released:** 2026-03-27

---

## What's New in v1.7.7

### Class Quest Chains Rebuilt

All three class chains have been expanded from 3 quests to 5, with precise level gates at **10 / 25 / 50 / 75 / 100**. Each quest rewards a dedicated class ability item:

**Warrior** — Trial of Fury → Battle Hardened → Whirlwind Mastery → Iron Skin Trial → War Cry

**Mage** — First Spark → Temporal Mastery → Surge of Power → Archmage's Aegis → The Archmage's Trial

**Rogue** — Shadow's Calling → Step Between Shadows → Toxin and Shadow → The Perfect Kill → Into the Dark

### Per-Quest Level Requirements

The quest system now supports per-quest level overrides. Individual quests can specify an exact minimum level independent of their difficulty tier, enabling precise level-gating across all quest chains.

### Warrior Class Tip

Players who select the Warrior class now receive a Shield Bash tip on selection, explaining how to activate the ability.

---

## Balance Changes

- **Ender Dragon objectives removed** — All Kill Ender Dragon objectives replaced with DAGMod boss kills (Wild Dragon, Skeleton Lord)
- **END-dimension collect objectives removed** — Objectives requiring End travel removed and replaced with obtainable alternatives
- **Race quest polish** — Four race quest rewards and objectives adjusted for theme and quality:
  - Dwarf "Deep Delving": TORCH×64 replaced with DIAMOND×4
  - Elf "Bowmaster's Trial": Collect ARROW×64 objective replaced with FLINT×48
  - Elf "Forest Lord": ELYTRA reward removed; NETHER_STAR reward bumped to ×2
  - Orc "Warlord": DRAGON_EGG reward replaced with NETHERITE_SWORD

---

## Bug Fixes

- **Tamed dragon server crash** — Fixed two `NullPointerException` crashes in `WildDragonEntity` where `AttackWithOwnerGoal` and `TrackOwnerAttackerGoal` both called `canTarget()` before null-checking the target
- **Enchanted book rewards** — All generic blank enchanted book rewards replaced with real enchantments (Fortune III, Mending + Unbreaking III, Power V + Looting III, etc.)
- **Enchanted book collect objectives** — Quests requiring blank enchanted book turn-ins had those objectives removed and replaced with appropriate alternatives

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
3. Install the v1.7.7 `.jar`
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
| v1.7.6 | Hall of Champions merchants with rotating trade system |
| v1.7.5 | Skeleton Lord auto-spawning, Necrotic Key loot, Hall of Champions locator improvements, Bone Realm portal height fixes |
| v1.7.4 | Real consumables, dragon stat & recipe overhaul, grave void death fix |
| v1.5.3-beta | Quest progression circular dependency fix |
