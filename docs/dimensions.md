# Dimensions

DAGMod adds custom dimensions with unique biomes, portals, and content. The Bone Realm and Dragon Realm are the primary adventure dimensions, each with boss encounters and exclusive loot.

**Current Version:** v1.6.5-beta

---

## Bone Realm

The Bone Realm is an underground dimension filled with undead enemies and procedurally generated dungeons.

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

1. **Skeleton King** (Epic Boss) - 60 HP, netherite armor, summons Skeleton Lords
2. **Skeleton Lord** (Mini-Boss) - 45 HP, diamond armor, summons Skeleton Summoners
3. **Skeleton Summoner** (Elite) - 30 HP, summons Bonelings
4. **Boneling** (Minion) - 12 HP, fast swarm mob, self-destructs after 3 minutes

### Bone Dungeon Loot

Bone Dungeons generate with three types of loot rooms (see [Boss & Dungeon Loot](./items/boss_dungeon_loot.md) for exact contents):
- **Regular Rooms** - Food, diamonds, emeralds, iron, tiered enchanted books
- **Portal Rooms** - Ender pearls, obsidian, ender eyes, diamonds
- **Treasure Rooms** - Same as regular rooms plus enchanted diamond armor and weapons

---

## Dragon Realm

The Dragon Realm is a dramatic dimension guarded by the Dragon Guardian boss.

### Portal Construction

| Component | Details |
|-----------|---------|
| Frame material | 3x3 Obsidian Portal Frame |
| Activation key | Dragon Key |
| Portal location | Craftable or found in dungeons |

### Biome: Burnt Lands

The Dragon Realm uses the `dagmod:burnt_lands` biome, featuring:
- Scorched, volcanic terrain
- Fire and lava hazards
- Dragon Guardian boss arena at coordinates (0, 64, 0)

### Dragon Guardian Boss

The Dragon Guardian is a flying boss that spawns when a player first enters the Dragon Realm.

| Stat | Value |
|------|-------|
| Health | 200 HP (100 hearts) |
| Attack Damage | 12.0 |
| Armor | 12.0 |
| Armor Toughness | 4.0 |
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
- After death, a respawn timer begins
- The boss will not respawn while the timer is active
- Re-entering the dimension after the timer expires triggers a new spawn

### Dragon Variants

In addition to the Dragon Guardian boss, dragon entities appear in several variants:

| Variant | Color | Location |
|---------|-------|----------|
| Red | Dark Red | Dragon Realm (boss variant) |
| Ice | Aqua | Overworld cold biomes |
| Lava | Gold | Overworld hot biomes |
| Earth | Green | Overworld mountain biomes |
| Wind | White | Overworld mountain peaks |

Wild dragons can be tamed as baby/juvenile dragons using raw meat (96 pieces) or Dragon Hearts (33% chance each).

---

## Additional Dimensions

DAGMod includes several additional themed dimensions:

| Dimension | Theme |
|-----------|-------|
| Badlands | Mesa/badlands terrain |
| Crimson Forest | Nether-style crimson environment |
| Deep Dark | Sculk-heavy underground darkness |
| Pale Garden | Eerie pale forest |
| Snowy Plains | Frozen tundra landscape |
| Swamp | Marshy wetlands |

These dimensions provide varied environments for exploration and are accessible through dimension-specific portals or commands.

---

## Portal Tips

- **Bring supplies** - Dimensions may have limited resources initially
- **Mark your portal** - Ensure you can find your way back
- **Prepare for combat** - Both primary dimensions feature hostile content
- **Party up** - Boss dimensions are easier with a [party](./party.md)
- **Check the respawn timer** - Don't enter the Dragon Realm expecting a boss fight if the timer is still active

---

## Related Guides

- [Bosses & Dungeons](./bosses_dungeons.md) - Full boss stats and strategies
- [Boss & Dungeon Loot](./items/boss_dungeon_loot.md) - Complete loot tables
- [Party System](./party.md) - Team up for dimension bosses
- [Getting Started](./getting_started.md) - Finding your first dungeon
