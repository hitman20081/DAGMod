# v1.10.0 Release Notes
**Released:** 2026-08-07

---

## What's New

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


### Loot Tables for Medieval and Pale Garden Castle Chests
Both castle structures now have populated loot tables in their chests.

---
w
## Bug Fixes

- **Mana/Energy/Cooldown HUD bars overlapping vanilla UI** — all three class resource bars overlapped the vanilla air-bubble/mount-health row; raised from `screenHeight - 49` to `screenHeight - 65`

---

## Migration Notes

> No new world required. Existing Pale Garden and castle chunks keep their current terrain and layout — only newly generated chunks pick up the custom noise settings and multi-piece castle jigsaw.
>
> If upgrading from v1.8.x, see [v1.9.0 release notes](release-v1.9.0.md) for earlier world generation migration guidance.

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

## Previous Releases

| Version | Summary |
|---|---|
| v1.9.1 | Structure water/lava avoidance, dimension level gates (Dragon 50, Bone 25), Hall of Champions block protection & biome restriction, Armorer structure set |
| v1.9.0 | Village Inn spawn hub, Garrick guild registry, Class Trainer NPC, Blacksmith's Anvil, merchant tier gating, season system fixes |
| v1.8.3 | Gem tier system (Cut→Polished→Flawless→Grand), in-game enchantment descriptions, quest book navigation |
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix |
| v1.8.1 | MC 26.2 migration, brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain |
| v1.7.8 | Skeleton King boss encounter, boss rebalance |
