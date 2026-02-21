# Progression System

This document details the progression system in DAGMod, including leveling, experience gain, stat scaling, and level gates.

---

## Leveling

**Level Cap:** 200

DAGMod uses an exponential XP curve with a gentle 5% multiplier, designed for extended but reachable progression.

**XP Formula:** `100 * 1.05^(level - 2)`

| Level | XP Required | Cumulative XP |
|-------|------------|---------------|
| 1 -> 2 | 100 | 100 |
| 10 -> 11 | ~155 | ~1,258 |
| 20 -> 21 | ~253 | ~3,307 |
| 50 -> 51 | ~1,005 | ~18,279 |
| 100 -> 101 | ~11,467 | ~218,364 |
| 150 -> 151 | ~130,880 | ~2,615,609 |
| 199 -> 200 | ~1,193,913 | ~24,070,261 |

**Total XP to Max Level:** ~24,070,000 XP

**Level-Up Effects:**
- Message displaying stat bonuses gained
- Level-up sound effect
- Heart particle effect
- Player healed to full health

---

## Experience Gain

XP is earned from three sources: gathering/mining, combat, and quests.

### Mining & Gathering XP

| Block | XP |
|-------|-----|
| Coal Ore | 5 |
| Copper Ore | 8 |
| Iron Ore | 10 |
| Gold Ore | 12 |
| Lapis Lazuli Ore | 12 |
| Redstone Ore | 12 |
| Diamond Ore | 25 |
| Emerald Ore | 30 |
| Ancient Debris | 40 |
| All Log Types | 2 |
| Wheat / Carrots / Potatoes / Beetroots | 1 |
| Nether Wart | 3 |

### Combat XP

| Mob | XP |
|-----|-----|
| **Bosses** | |
| Ender Dragon | 2,000 |
| Wither | 1,500 |
| Warden | 1,000 |
| Elder Guardian | 500 |
| **Dangerous Mobs** | |
| Ravager | 45 |
| Shulker | 40 |
| Wither Skeleton | 35 |
| Piglin Brute | 35 |
| Blaze | 30 |
| Vindicator / Evoker | 30 |
| Zoglin | 30 |
| Ghast | 28 |
| Enderman / Guardian | 25 |
| Hoglin | 22 |
| **Standard Hostiles** | |
| Phantom | 20 |
| Pillager / Witch | 20 |
| Creeper | 18 |
| Piglin | 18 |
| Zombie / Skeleton | 15 |
| Spider | 12 |
| Default Hostile | 10 |
| **Passive Mobs** | 0 |

**Note:** Passive mobs give 0 XP to discourage animal farming for progression.

### Quest Completion XP

| Quest Tier | XP Reward |
|------------|-----------|
| Novice | 200 |
| Apprentice | 500 |
| Expert | 1,500 |
| Master | 2,500 |

### Racial XP Bonus

Humans receive **+25% XP** from all sources, applied before XP is added to the progression system.

---

## Stat Scaling

Players gain permanent stat bonuses as they level up:

| Stat | Rate | Max Bonus (Level 200) |
|------|------|----------------------|
| Health (HP) | +1 heart (2 HP) every 10 levels | +40 HP (30 total hearts) |
| Attack Damage | +1 every 5 levels | +40 |
| Armor | +1 every 10 levels | +20 |

**Examples:**
- Level 10: +2 HP (11 hearts), +2 Attack, +1 Armor
- Level 50: +10 HP (15 hearts), +10 Attack, +5 Armor
- Level 100: +20 HP (20 hearts), +20 Attack, +10 Armor
- Level 200: +40 HP (30 hearts), +40 Attack, +20 Armor

---

## Level Gates

Certain quest tiers are locked behind level requirements:

| Level | Unlock |
|-------|--------|
| 1 | Novice quests available |
| 5 | Apprentice quests unlocked |
| 15 | Expert quests unlocked |
| 25 | Master quests unlocked |

Players must reach the required level AND have the appropriate quest book tier to access higher-difficulty quests.

---

## Data Persistence

- Progression data saves to `world/data/dagmod/progression/{uuid}.dat` (NBT format)
- Data loads automatically on player join
- Data saves on player disconnect and server shutdown
- Progress syncs to the client for HUD display

---

## Commands

| Command | Description |
|---------|-------------|
| `/testprogression 500` | Add 500 XP |
| `/testprogression setlevel 100` | Set level to 100 |
| `/testprogression reset` | Reset all progression |
| `/testprogression info` | Show current stats |
| `/info` | Show player info including level |
