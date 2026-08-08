# Dimensions

DAGMod adds custom dimensions with unique biomes, portals, and content. The Bone Realm and Dragon Realm are the primary adventure dimensions, each with boss encounters and exclusive loot.

## Bone Realm

The Bone Realm is an underground dimension filled with undead enemies and procedurally generated dungeons.

> **Level requirement:** The `rumours_of_the_bone_king` quest (which rewards the Bone Dungeon Locator) requires **level 25** before it appears. You cannot obtain a locator or access a Bone Dungeon portal before reaching that level.

### Portal Construction

| Component | Details |
|-----------|---------|
| Frame material | 3x3 Ancient Bone Block frame |
| Activation key | Necrotic Key |
| Key source | 5% drop from Skeleton King |
| Portal location | Found in Bone Dungeon portal rooms |

### Biome: Ossuary Depths

The Bone Realm uses the `dagmod:ossuary_depths` biome, featuring:
- Dark, cavernous terrain
- Bone-themed block palette
- Hostile undead mob spawning
- Custom ambient atmosphere

### Boss Hierarchy

The Bone Realm features a tiered boss system (see [Bosses & Dungeons](./bosses_dungeons.md) for full details):

1. **Skeleton King** (Epic Boss) - 300 HP, netherite armor, throne room encounter with sealed exit
2. **Skeleton Lord** (Mini-Boss) - 200 HP, diamond armor, summons Skeleton Summoners
3. **Skeleton Summoner** (Elite) - 120 HP, summons Bonelings
4. **Boneling** (Minion) - 12 HP, fast swarm mob, self-destructs after 3 minutes

### Skeleton Throne Room

A unique structure that spawns once per world in the Bone Realm. Locate it with `/locate structure dagmod:skeleton_throne_room`. The Skeleton King spawns automatically when a survival player enters the throne room, and the exits seal until the King is defeated.

### Bone Dungeon Loot

Bone Dungeons generate with three types of loot rooms (see [Boss & Dungeon Loot](./items-boss_dungeon_loot.md) for exact contents):
- **Regular Rooms** - Food, diamonds, emeralds, iron, tiered enchanted books
- **Portal Rooms** - Ender pearls, obsidian, ender eyes, diamonds
- **Treasure Rooms** - Same as regular rooms plus enchanted diamond armor and weapons

---

## Dragon Realm

The Dragon Realm is a dramatic dimension guarded by the Dragon Guardian boss.

> **Level requirement:** You must reach **level 50** to access the Dragon Realm. The `red_dragon_fury` quest (which rewards the Dragon Key) does not appear below level 50. Attempting to activate a portal or walk through one below this level is blocked. Creative mode bypasses the gate. Exiting the Dragon Realm is always permitted regardless of level.

### Portal Construction

| Component | Details |
|-----------|---------|
| Frame material | 3x3 Obsidian Portal Frame |
| Activation key | Dragon Key |
| Key source | Reward from completing `red_dragon_fury` quest (requires level 50) |
| Portal location | Build anywhere with the frame; activate with the Dragon Key |

### Biome: Burnt Lands

The Dragon Realm uses the `dagmod:burnt_lands` biome, featuring:
- Scorched, volcanic terrain
- Fire and lava hazards
- Dragon Guardian boss arena at coordinates (0, 64, 0)

### Dragon Guardian Boss

The Dragon Guardian is a flying boss that spawns when a player first enters the Dragon Realm.

| Stat | Value |
|------|-------|
| Health | 400 HP (200 hearts) |
| Attack Damage | 16.0 |
| Armor | 16.0 |
| Armor Toughness | 12.0 |
| Flying Speed | 0.4 |
| Knockback Resistance | 80% |
| Detection Range | 48 blocks |
| XP on Kill | 50 |

**Boss Bar:** Purple, notched (10 segments). Named "Dragon Guardian".

**Combat Abilities:**
- **Fire Breath** - Shoots fireballs at targets 4-20 blocks away (5-second cooldown)
- **Swoop Attack** - Dives from above for melee damage (6-second cooldown)
- **Roar** - Applies Slowness to all players within 10 blocks (20-second cooldown)
- **Melee Attack** - Flying melee strikes when close

**Behavior:**
- Spawns at arena center (0, Y+10, 0) above the surface
- Perches on high points when not in combat
- Cannot despawn; immune to fall damage
- Global announcement when it spawns and when slain

**Drops:**
- Dragon Scale (3-7)
- Dragon Bone (2-4)
- Dragon Skin (1-2)
- Dragon Heart (1, always)
- King's Scale (1, always - boss exclusive)

**Respawn:**
- 30-minute respawn timer starts on death
- Graduated announcements: every 5 minutes, then every minute for the last 5, then 30 seconds, then a 10-second countdown (bold red)
- Boss respawns automatically when the timer expires (no re-entry required)

### Dragon Variants

The Dragon Realm features both the Dragon Guardian boss and ambient red dragon threats:

| Variant | Color | Location | Spawning |
|---------|-------|----------|----------|
| Red (Ambient) | Dark Red | Dragon Realm (roaming) | Natural, max 5 per realm |
| Red (Boss) | Dark Red | Dragon Realm (arena at 0, 64, 0) | 30-minute respawn timer |
| Ice | Aqua | Overworld cold biomes | Natural, max 8 total wild |
| Lava | Gold | Overworld hot biomes | Natural, max 8 total wild |
| Earth | Green | Overworld mountain biomes | Natural, max 8 total wild |
| Wind | White | Overworld mountain peaks | Natural, max 8 total wild |

**Ambient Red Dragons:** Red-variant dragons spawn throughout the Dragon Realm as ambient threats (15% chance per cycle, cap of 5). These are separate from the Dragon Guardian boss and provide ongoing danger while exploring.

**Wild Dragons (Overworld):** Spawn with a 25% chance per cycle, capped at 8 total across the overworld. Can be tamed as baby/juvenile dragons using raw meat (96 pieces) or Dragon Hearts (33% chance each).

---

## Pale Garden

The Pale Garden is an eerie, pale-forested dimension generated with its own custom terrain noise settings *(as of v1.10.0)*, independent of overworld worldgen.

### Portal Construction

| Component | Details |
|-----------|---------|
| Frame material | 7x7 Pale Heartstone frame with a 5x5 opening |
| Activation key | Pale Garden Key |
| Key source | Crafted from 4 Pale Oak Logs, 4 Amethyst Shards, and 1 Pale Heartstone |
| Portal location | Build a frame anywhere and activate with the key, or find one already built inside a Pale Garden castle |

> **Castle portals:** Pale Garden castles generate with a pre-built (inactive) Pale Heartstone frame. Stepping through a linked portal into the Pale Garden will automatically detect and activate that frame instead of requiring you to craft and use a key on it yourself.

> **Return trip:** The game remembers the specific Overworld portal you entered from and sends you back through that same one — it won't strand you at an unrelated spot or spawn a duplicate portal near your Pale Garden coordinates.

### Pale Garden Castle

A multi-piece jigsaw structure *(rebuilt from a single static structure in v1.10.0)* that generates rarely in the Pale Garden (structure spacing 500/499 chunks). Its chests are populated with loot.

---

## Additional Dimensions

DAGMod includes several additional themed dimensions:

| Dimension | Theme |
|-----------|-------|
| Badlands | Mesa/badlands terrain |
| Crimson Forest | Nether-style crimson environment |
| Deep Dark | Sculk-heavy underground darkness |
| Snowy Plains | Frozen tundra landscape |
| Swamp | Marshy wetlands |

These dimensions provide varied environments for exploration and are accessible through dimension-specific portals or commands.

---

## Portal Tips

- **Bring supplies** - Dimensions may have limited resources initially
- **Set your respawn point before entering the Dragon Realm** - The return portal will drop you at your bed or respawn anchor; without one set it returns you to world spawn
- **Prepare for combat** - Both primary dimensions feature hostile content
- **Party up** - Boss dimensions are easier with a [party](./party.md)
- **Check the respawn timer** - Don't enter the Dragon Realm expecting a boss fight if the timer is still active
- **Hall of Champions blocks are protected** - Survival players cannot mine blocks inside the Hall of Champions structure. Creative mode is required for any modifications

---

## Related Guides

- [Bosses & Dungeons](./bosses_dungeons.md) - Full boss stats and strategies
- [Boss & Dungeon Loot](./items-boss_dungeon_loot.md) - Complete loot tables
- [Party System](./party.md) - Team up for dimension bosses
- [Getting Started](./getting_started.md) - Finding your first dungeon
