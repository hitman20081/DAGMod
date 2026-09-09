# v2.0.0 Release Notes
**Released:** 2026-09-08

> **Potentially save-breaking — a new world is recommended, though not strictly required.** The scope of dimension/NPC/economy changes is large enough that a fresh world is the safer choice if you can. One concrete gap on existing saves: the new starter Coin Pouch only auto-grants at the moment a player finishes Garrick's tutorial, so players who already finished it on an older save won't receive one automatically — see Migration Notes below for the workaround.

---

## What's New

### Starting Inn & Garrick as Guild Registry
The Village Inn is now the world spawn point. Innkeeper Garrick handles race and class selection entirely through dialogue — no altars required near spawn, though the physical Race/Class Selection Altars still work as a standalone alternate path if you prefer them. The Armorer and Jeweler now live in the Inn too, reachable from day one.

### Hall of Champions Is Now a True Singleton
Only one Hall of Champions generates per world, genuinely far from spawn. Garrick hands out the Hall Locator immediately on finishing his tutorial and points new players toward it — the "earned destination" now comes from real travel distance plus the existing level-10 class quest gate, not a compass/gear-repair/book-progression system. (That heavier design was fully scoped at one point but never built, and is now formally dropped rather than left as a stale TODO — see `ROADMAP.md` if you're curious what was considered.)

### Coin Pouch Economy
A new single-balance currency item replaces physical coin-stack handling:
- Granted automatically the moment you finish Garrick's tutorial
- Lives in its own dedicated inventory slot next to your offhand/shield slot — can't be dropped, moved out of its slot, or lost to death
- Right-click to withdraw, drop coins on it to deposit, scroll to pick which of the 4 tiers (Copper/Silver/Gold/Platinum) a withdrawal mints
- Every merchant trade in the game now costs coins instead of emeralds, and merchants mint straight into your balance during a trade — no manual withdrawal needed

### Off-Hand Weapon Combat
Warriors can equip a sword or dagger off-hand; Rogues a dagger (full damage) or sword (half damage). Right-click an enemy to trigger a 1-second-cooldown off-hand strike.

### Pale Garden Castle & Spider Queen Lair Rework
The castle is now a true singleton with terrain smoothed around it. The Spider Queen Lair has a real walkable staircase entrance and a dynamically-carved shaft up to daylight, replacing the old sealed room with no way in.

---

## Balance Changes

- **Quest XP now feeds your actual level** — solo quest completion previously granted vanilla enchanting XP instead of progress toward the mod's own 200-level system (the one that drives your HP/attack/armor bonuses and dimension gates). This was a bug, not a design choice — party quests already worked correctly. Fixed for MAIN/SIDE/CLASS/JOB quests across the board.
- **Dragon Guardian armor raised 16 → 28** — it was weaker than Bone Realm's Skeleton King despite Dragon Realm requiring double the level (50 vs 25) to reach.
- **Pale Garden now requires level 35** to open a portal — previously had no level gate at all, unlike Bone Realm (25) and Dragon Realm (50).

---

## Bug Fixes

- **8 merchant trades** had two coin types stacked as their cost instead of coin + a real material, breaking the pattern used everywhere else in the game
- **Merchant coin auto-top-up** stopped finding the Coin Pouch after it moved to its own dedicated slot — fixed to look in the right place
- **Bone Realm, Dragon Realm, and Pale Garden could generate holes straight through to the void** near the bottom of the world in some chunks — each dimension's floor is now forced solid (affects newly-generated chunks only, not existing holes)
- **Bone Realm was pitch black** regardless of its lighting setting — the actual brightness control in this Minecraft version turned out to be a different, previously-unset value
- **Mage Night Vision** no longer visibly counts down or pulses the "about to expire" warning
- **Healing station beam** no longer surrounds itself in a large cloud of particles when you get close

---

## Migration Notes

> A new world is recommended but not strictly required — see the save-breaking note at the top of this page. If you stay on an existing save, manually `/give <player> dagmod:coin_pouch` to any player who already finished Garrick's tutorial before upgrading, since the automatic grant only fires at tutorial completion.

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
3. Install the v2.0.0 `.jar`
4. Launch Minecraft 26.2

---

## Previous Releases

| Version | Summary |
|---|---|
| v1.10.0 | 7 race/class enchantments, Pale Garden castle rebuilt as multi-piece jigsaw, Pale Garden's own terrain noise settings, castle chest loot tables |
| v1.9.1 | Structure water/lava avoidance, dimension level gates (Dragon 50, Bone 25), Hall of Champions block protection & biome restriction, Armorer structure set |
| v1.9.0 | Village Inn spawn hub, Garrick guild registry, Class Trainer NPC, Blacksmith's Anvil, merchant tier gating, season system fixes |
| v1.8.3 | Gem tier system (Cut→Polished→Flawless→Grand), in-game enchantment descriptions, quest book navigation |
| v1.8.2 | Village NPC structures (7 buildings), dynamic lighting chunk fix |
| v1.8.1 | MC 26.2 migration, brimstone rename |
| v1.8.0 | MC 26.1.2 migration, Gem Powder System, Gem Crushing Station |
| v1.7.10 | Quest book overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting |
| v1.7.9 | Seasons setup system, Skeleton Kingdom structure chain |
| v1.7.8 | Skeleton King boss encounter, boss rebalance |
