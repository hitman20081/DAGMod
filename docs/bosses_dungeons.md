# Bosses and Dungeons

The Bone Realm features a hierarchy of undead bosses and procedurally generated dungeons with unique loot.

---

## Bosses

### Skeleton King
**Role:** Top-tier Bone Realm boss

| Stat | Value |
|------|-------|
| Health | 60 HP |
| Attack Damage | 8.0 |
| Armor | 20.0 |
| Armor Toughness | 5.0 |
| Movement Speed | 0.3 |
| Detection Range | 48 blocks |
| Knockback Resistance | 100% (immune) |
| Size | 2.0x (double normal skeleton) |
| XP on Kill | 100 |

- **Boss Bar:** Purple, notched. Sky darkens while the boss is active.
- **Equipment:** Full named Netherite armor set ("Crown of the Bone Sovereign", "Regalia of the Death Lord", "Royal Bone Greaves", "Sovereign's Marrow Treads") + Netherite sword. Equipment does not drop.
- **Behavior:** Uses standard Skeleton AI (ranged bow + melee combat). Cannot despawn.
- **On Death:** Spawns a locked boss chest with epic loot.

---

### Skeleton Lord
**Role:** Mid-tier boss. Summons Skeleton Summoners.

| Stat | Value |
|------|-------|
| Health | 45 HP |
| Attack Damage | 6.0 |
| Armor | 15.0 |
| Armor Toughness | 4.0 |
| Movement Speed | 0.28 |
| Detection Range | 40 blocks |
| Knockback Resistance | 100% (immune) |
| Size | 1.5x |
| XP on Kill | 50 |

- **Boss Bar:** Red, notched.
- **Equipment:** Full named Diamond armor set ("Helm/Chest/Leggings/Sabatons of the Bone Lord") + Diamond sword. Equipment does not drop.
- **Summoning:** Every 10-15 seconds, summons a Skeleton Summoner (max 3 within 40 blocks). Summoners inherit the Lord's current target.
- **On Death:** Spawns a locked boss chest.

---

### Skeleton Summoner (Field Captain)
**Role:** Elite mob summoned by the Skeleton Lord. Summons Bonelings.

| Stat | Value |
|------|-------|
| Health | 30 HP |
| Attack Damage | 4.0 |
| Armor | 8.0 |
| Armor Toughness | 2.0 |
| Movement Speed | 0.26 |
| Detection Range | 32 blocks |
| Knockback Resistance | 30% |
| Size | 1.1x |
| XP on Kill | 25 |

- **No boss bar.**
- **Equipment:** Iron helmet ("Summoner's Hood"), leather chestplate ("Summoner's Robes"), iron sword.
- **Summoning:** Every 6-12 seconds, summons a Boneling (max 4 within 24 blocks). Witch particles and evoker cast sound on summon.
- Cannot despawn.

---

### Boneling
**Role:** Weak swarm minion summoned by Skeleton Summoners.

| Stat | Value |
|------|-------|
| Health | 12 HP |
| Attack Damage | 2.5 |
| Armor | 0 |
| Movement Speed | 0.35 (fast) |
| Size | 0.7x (70% normal) |
| XP on Kill | 3 |

- **Temporary:** Self-destructs after 3 minutes with poof particles.
- **Death Effect:** Bone shatter particles + soul particles.
- **Ambient:** Ash particles every second while alive.

---

### Dragon Guardian
**Role:** Dragon Realm boss. Flying dragon that guards the Dragon Realm dimension.

| Stat | Value |
|------|-------|
| Health | 200 HP |
| Attack Damage | 12.0 |
| Armor | 12.0 |
| Armor Toughness | 4.0 |
| Flying Speed | 0.4 |
| Knockback Resistance | 80% |
| Detection Range | 48 blocks |
| XP on Kill | 50 |

- **Boss Bar:** Purple, notched (10 segments). Named "Dragon Guardian".
- **Spawn:** Triggered when a player enters the Dragon Realm for the first time (or after respawn timer expires). Spawns at arena center (0, surface+10, 0).
- **Combat Abilities:**
  - **Fire Breath:** Shoots fireballs at targets 4-20 blocks away. 2-second charge, 5-second cooldown.
  - **Swoop Attack:** Dives from above for melee damage. 6-second cooldown.
  - **Roar:** Intimidation display applying Slowness to all players within 10 blocks. 20-second cooldown.
  - **Melee Attack:** Flying melee strikes when close.
- **Drops:** Dragon Scale (3-7), Dragon Bone (2-4), Dragon Skin (1-2), Dragon Heart (1), King's Scale (1, boss exclusive).
- **Respawn Timer:** 30-minute timer starts on death. Graduated announcements:
  - Every 5 minutes (25m, 20m, 15m, 10m, 5m remaining)
  - Every minute for the last 5 minutes
  - 30-second warning
  - 10-second countdown (bold red text)
  - Boss respawns automatically when timer expires
- **Immune to fall damage.** Cannot despawn.

**Ambient Red Dragons:** In addition to the boss, red-variant dragons roam the Dragon Realm as ambient threats (max 5, 15% spawn chance per cycle). These are weaker than the Dragon Guardian boss and provide ongoing danger while exploring the dimension.

For more information on the Dragon Realm dimension, see [Dimensions](./dimensions.md).

---

## Boss Fight Strategy

The Bone Realm boss hierarchy creates escalating encounters:

1. **Bonelings** are weak individually but swarm in groups. Clear them quickly.
2. **Skeleton Summoners** create Bonelings constantly. Prioritize killing Summoners to stop the flood.
3. **Skeleton Lord** spawns Summoners, creating a chain of minions. Focus the Lord while managing adds.
4. **Skeleton King** is the ultimate challenge with high damage, heavy armor, and ranged attacks.

**Bone Realm Tips:**
- Bring good armor (Dragonscale or Obsidian recommended)
- Area-of-effect abilities (Whirlwind, Mana Burst) are excellent for clearing Bonelings
- Rogues can use backstab damage on bosses for high burst
- Warriors should use Battle Standard for sustain during long fights

**Dragon Guardian Tips:**
- Ranged attacks are essential against a flying boss
- Watch for the charge-up animation before fire breath and dodge sideways
- When the dragon roars, back away to avoid the Slowness debuff
- Bring fire resistance potions for fire breath damage
- Party up for this fight - a tank and ranged DPS combo works well

---

## Dungeons

### Bone Dungeons

Procedurally generated underground structures found throughout the world.

**Generation:**
- Uses Minecraft's jigsaw system for procedural layout
- 10 unique structure pieces including corridors, rooms, treasure rooms, and portal rooms
- Each dungeon layout is unique
- Generates naturally underground with natural terrain integration

**Features:**
- Custom loot chests with enchanted gear, diamonds, and rare items
- Boss spawner rooms with Bone Realm entities
- Portal rooms that connect to the Bone Realm dimension
- Locked treasure chests that require boss kills to open

**Loot:**
- Regular chests contain mid-tier loot (iron/diamond gear, enchanted books)
- Boss chests contain epic loot (see [Boss & Dungeon Loot](items/boss_dungeon_loot.md))
- Locked chests require defeating the boss that guards them
