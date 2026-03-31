# Bosses and Dungeons

The Bone Realm features a hierarchy of undead bosses and procedurally generated dungeons with unique loot.

---

## Bosses

### Skeleton King
**Role:** Top-tier Bone Realm boss — on par with the Dragon Guardian in difficulty

| Stat | Value |
|------|-------|
| Health | 300 HP |
| Attack Damage | 13.0 |
| Armor | 22.0 |
| Armor Toughness | 8.0 |
| Movement Speed | 0.3 |
| Detection Range | 48 blocks |
| Knockback Resistance | 100% (immune) |
| Size | 2.0x (double normal skeleton) |
| XP on Kill | 100 |

- **Boss Bar:** Purple, notched. Sky darkens while the boss is active.
- **Equipment:** Full named Netherite armor set ("Crown of the Bone Sovereign", "Regalia of the Death Lord", "Royal Bone Greaves", "Sovereign's Marrow Treads") + Netherite sword. Equipment does not drop.
- **Behavior:** Uses standard Skeleton AI (ranged bow + melee combat). Cannot despawn.
- **Spawn:** Triggered automatically when a survival player enters the Skeleton Throne Room (12-block proximity to the trigger block on the throne).
- **Room Seal:** On spawn, the throne room doorways are sealed with barrier blocks, locking all players inside for the duration of the fight.
- **On Death:** Barrier blocks are removed. Every player within 25 blocks receives one **King's Recall Stone** and one **Skeleton King Chest Key**. One locked chest spawns per nearby player.

---

### Skeleton Lord
**Role:** Mid-tier boss. Summons Skeleton Summoners.

| Stat | Value |
|------|-------|
| Health | 200 HP |
| Attack Damage | 10.0 |
| Armor | 18.0 |
| Armor Toughness | 6.0 |
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
| Health | 120 HP |
| Attack Damage | 6.0 |
| Armor | 10.0 |
| Armor Toughness | 3.0 |
| Movement Speed | 0.26 |
| Detection Range | 32 blocks |
| Knockback Resistance | 50% |
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
| Health | 400 HP |
| Attack Damage | 16.0 |
| Armor | 16.0 |
| Armor Toughness | 12.0 |
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
- **Skeleton King:** The room seals on spawn — you cannot leave until the King is dead. Ensure your party is ready before triggering the fight. On death, you receive a King's Recall Stone to teleport back to the overworld.

**Dragon Guardian Tips:**
- Ranged attacks are essential against a flying boss
- Watch for the charge-up animation before fire breath and dodge sideways
- When the dragon roars, back away to avoid the Slowness debuff
- Bring fire resistance potions for fire breath damage
- Party up for this fight - a tank and ranged DPS combo works well

---

## Dungeons

### Skeleton Throne Room

A dedicated boss structure that spawns once per Bone Realm world using `concentric_rings` placement.

**Generation:**
- Spawns exclusively in the `dagmod:ossuary_depths` biome (Bone Realm)
- Guaranteed single spawn per world
- Locate with `/locate structure dagmod:skeleton_throne_room`

**Layout (3 Jigsaw pieces):**
- **Throne Room** — 35×35 main arena with the Skeleton King spawn trigger on the throne
- **Hallway** — Connecting corridor between the throne room and teleport room
- **Teleport Room** — Exit chamber for post-boss travel

**Features:**
- Proximity trigger on the throne automatically spawns the Skeleton King when a survival player enters (12-block radius)
- Doorways seal with barrier blocks for the duration of the fight
- One locked chest per player spawns on King death, along with a King's Recall Stone per player

---

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
