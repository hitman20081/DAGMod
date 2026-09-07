# DAGMod Development Roadmap

**Current Version**: v1.10.0
**Last Updated**: 2026-08-07

This document tracks the development progress of DAGMod features, comparing planned features against implemented functionality.

---

## 🔧 **Unreleased — Working Tree (pending next version number)**

Built since v1.10.0, not yet released. Not yet assigned a version — fold into the next release per `release_instructions.md` once a version number is picked.

- ✅ **Pale Garden Castle & Spider Queen Lair rework**
  - Castle converted to a true singleton (`concentric_rings, count: 1`) at a fixed elevation, with terrain flattened/feathered around it and a per-piece bounding-box check so unbuilt sections of the (still in-progress) castle NBT read as ordinary ground instead of a pit
  - Spider Queen Lair redesigned: walkable staircase entrance (fixed headroom-pinch bug), plus a `LairShaftHandler` that dynamically carves + ladders a shaft from the stairs up to daylight/an existing cavern regardless of local terrain height — replaces the original sealed, no-entrance room
  - Pale Garden portal linking, singleton-castle search, and teleport-target fixes
  - **Divergence from the roadmap**: the current [Pale Abyss Design](#pale-abyss-design) (v2.2.0) describes a **separate** dimension with a two-layer surface/cavern design, a Brood Warden rename, biological crafting, a multi-phase boss fight, and a craftable portal key. What's actually built lives **inside the existing Pale Garden dimension** instead, and is a single-phase boss + placeholder room/entrance only — none of the mob hierarchy, materials, crafting, or multi-phase fight from that design doc exist yet. Needs a decision before v2.2.0 planning: keep it in Pale Garden and rewrite the design doc, or still split it out into its own dimension later.
  - Castle NBT itself still has incomplete/unbuilt sections — flagged as a manual authoring task, not a code fix

- ✅ **Off-hand weapon combat system** (Warrior/Rogue) — not previously on the roadmap
  - Warriors can equip a sword or dagger off-hand; Rogues a dagger (full damage) or sword (halved damage) off-hand; shields unaffected for every class
  - Right-click-on-entity triggers an off-hand strike, own 1s cooldown, damage read directly from the weapon's own attack-damage component
  - New `#dagmod:offhand_daggers` / `#dagmod:offhand_swords` item tags for extensibility

- ✅ **Dodge feedback fix** — Phantom Dust/Perfect Dodge/Shadow Step dodges now play a sound (`ENDERMAN_TELEPORT`) in addition to the existing particles + action-bar text

---

## 📌 **Semantic Versioning**

DAGMod follows **Semantic Versioning 2.0.0** (https://semver.org/):

**Format**: `MAJOR.MINOR.PATCH`

- **MAJOR** (x.0.0) - Incompatible API changes, major milestones, game-changing features
- **MINOR** (0.y.0) - New features added in a backwards-compatible manner
- **PATCH** (0.0.z) - Backwards-compatible bug fixes only

**Examples**:
- `1.8.0` → New features (spell scrolls, crafting system)
- `1.8.1` → Bug fixes for v1.8.0 (no new features)
- `2.0.0` → Major content milestone
- `3.0.0` → Major endgame expansion

---

## ✅ **Fully Implemented Features**

### **Core Systems**
- ✅ **Race System** (4 races: Human, Dwarf, Elf, Orc)
  - Race selection altars
  - Passive bonuses (mining, movement, damage, XP)
  - Race-specific gathering bonuses
  - 9 unique race+class synergies

- ✅ **Class System** (3 classes: Warrior, Mage, Rogue)
  - Class selection altars
  - Warrior: 5 abilities (Battle Standard, Whirlwind, Iron Talisman, Shield Bash, War Cry)
  - Mage: 4 core abilities + wands/scrolls (Arcane Missiles, Mana Burst, Time Warp, Arcane Barrier)
  - Rogue: 7 abilities (dual energy/cooldown system + backstab passive)
  - Class-specific items and mechanics

- ✅ **Progression System**
  - 200 levels with gentle exponential XP scaling (1.05x per level)
  - Multiple XP sources (mining, combat, quests, farming, fishing)
  - +1 heart (2 HP) every 10 levels (30 hearts at level 200)
  - +1 attack every 5 levels (+40 at level 200)
  - +1 armor every 10 levels (+20 at level 200)
  - Visual XP bar HUD
  - Level requirements for quest tiers
  - **Fixed in v1.4.5**: Health/stats persistence on death
  - **Reworked in v1.7.0**: Level cap 50→200, heart scaling rework

- ✅ **Quest System**
  - 64+ quests across multiple categories (MAIN, SIDE, CLASS, JOB, DAILY)
  - Quest Block for story/side/race quests
  - Job Board for jobs and daily quests
  - 4 difficulty tiers (Novice, Apprentice, Expert, Master)
  - Quest chains with sequential progression
  - Multiple objective types (collect, kill, craft, delivery, tag-collect)
  - Quest book tier system
  - 40 race-specific quest chains
  - **Added in v1.4.5**: Tutorial gating via Innkeeper Garrick NPC
  - **Added in v1.9.0**: Daily quest system — server-wide rotating pool of 5 quests per UTC day, 3 completions per player per day, streak-based XP multipliers (up to ×1.5), level scaling (up to ×2.0)

- ✅ **Party System** (v1.4.4-v1.4.5)
  - Party creation and invitations
  - Shared XP distribution (`PartyXPHandler`)
  - Boss loot distribution (`PartyLootHandler`)
  - Party quests with shared objectives (`PartyQuestManager`)
  - Party quest difficulties (EASY, MEDIUM, HARD, ELITE)
  - Party commands (/party create, invite, leave, kick, disband, list)
  - Party HUD display

### **World Content**
- ✅ **Bone Realm Dimension**
  - Custom dimension with portal system (Ancient Bone blocks + Necrotic Key)
  - 4-tier boss hierarchy (Skeleton King → Lords → Summoners → Bonelings)
  - Locked treasure chests with key system
  - Boss-specific loot tables

- ✅ **Bone Dungeons**
  - Procedurally generated underground structures
  - 10 unique room types
  - Custom loot tables with themed rewards
  - Natural terrain integration

- ✅ **Hall of Champions**
  - Surface structure with Race & Class Selection Altars
  - Hall Locator item for navigation
  - Respawn point integration
  - Nine merchant NPCs inside the hall: Alchemist, Armorer, Enchantsmith, Jeweler, Miner, Lumberjack, Voodoo Illusioner, Mystery Merchant, Trophy Dealer — added v1.7.6

### **Items & Equipment**
- ✅ **50+ Custom Items**
  - 15 consumable powders (Shadow Blend, Fortune Dust, Cooldown Elixir, etc.)
  - 6 themed armor sets with progressive bonuses
  - 9 themed weapons and 9 shields
  - Wands and spell scrolls for Mages
  - Class ability items
  - Fantasy foods and potions

- ✅ **Dragon Realm Exclusive Materials** (v1.5.5+)
  - Mythril system (ore, ingot, nugget, tools, armor)
  - 8 Dragon Realm gems (Citrine, Pink Garnet, Ruby, Sapphire, Tanzanite, Zircon, Topaz, Silmaril)
  - 21 gem-related blocks (gem blocks, raw gem blocks, ores, deepslate ores)
  - Full mining → smelting progression
  - Prepared for future gem socket system (v2.2.0)

- ✅ **Custom Armor Sets**
  - Dragonscale, Crystalforge, Inferno, Nature's Guard, Shadow, Fortuna
  - Set bonuses when wearing full sets
  - Unique textures and models

### **NPCs & Interactions**
- ✅ **Innkeeper Garrick Tutorial NPC** (v1.4.5)
  - Tutorial quest giver
  - Quest system introduction
  - Unlocks Quest Blocks and Job Boards on interaction
  - `/summon_garrick` command for testing
  - Persistent interaction tracking

- ✅ **Simple NPC System**
  - Base NPC entity framework
  - Dialogue system
  - Custom renderers

- ✅ **Class Trainer NPC** (v1.9.0)
  - Fixed-position NPC dedicated to class quest chain progression
  - Interactive progress display with per-quest status indicators (complete / turn-in / in progress / available / locked)
  - Bypasses quest book tier check — class progression gated by level only
  - Garrick tutorial handoff directs players to Class Trainer on completion

- ✅ **Merchant NPC Rotating Trade System** (v1.6.0)
  - 8 merchant NPCs with rotating inventories (Armorer, Mystery Merchant, Enchantsmith, Voodoo Illusioner, Trophy Dealer, Miner, Hunter, Lumberjack)
  - Static trades always available for currency building
  - Premium rotating trades that cycle every 72 hours (real-world time)
  - Rotation persists across server restarts via NBT storage
  - Configurable rotation interval (12h-168h range)
  - Merchant dialogue system with greetings and rotation hints

- ✅ **Expanded Merchant Roster** (v1.6.5)
  - 5 new specialized merchant NPCs (13 total): Baker, Blacksmith, Jeweler, Alchemist, Village Merchant
  - Baker: Food and baked goods (8 trades)
  - Blacksmith: Buys all raw ores for emeralds, sells repair materials (21 trades)
  - Jeweler: Buys processed gems, sells gem tools and Silmaril (12 trades)
  - Alchemist: All brewing equipment and potion ingredients (21 trades)
  - Village Merchant: General store with everyday supplies (19 trades)
  - Merchant trade overlap cleanup for clear specialization

### **QoL Features**
- ✅ **Ship Travel System** (`ShipTravelManager`)
  - Fast travel via `/travel` command
  - Destination-based teleportation
  - Ship Helm block (v1.4.5)

- ✅ **Death Recovery System** (Java-based, v1.6.6)
  - Lodestone grave blocks placed at death location
  - All non-soulbound items stored in grave, persisted to disk
  - Right-click grave to recover items; 5-minute loot delay for non-owners
  - One grave per player with automatic old-grave cleanup
  - Replaces old non-functional datapack grave system

- ✅ **Enhanced Consumables** (fully implemented v1.7.4)
  - Lifesteal System (Vampire Dust) — 10% lifesteal via mixin, capped at 2.5 hearts/hit
  - Dodge/Evasion System (Phantom Dust 50%, Perfect Dodge 100%) — per-hit roll via mixin
  - Spell Modification System (Spell Echo casts twice, Overcharge 2× power) — integrated into all 4 mage abilities
  - Death Prevention System (Last Stand Powder) — intercepts lethal hits + void death with surface teleport
  - Time Distortion AoE — Speed II self + Slowness IV on enemies within 10 blocks

---

## 🟡 **Partially Implemented Features**

### **Custom Enchantments Discovery System**
- ✅ **Framework + race/class enchantments implemented** — see v1.10.0 below
- ❌ **Discovery system** — deferred; the 7 race/class enchantments ship through normal
  enchanting table/anvil/loot, same as the other 26 custom enchantments

---

## ❌ **Planned Features**

---

### 🔷 PHASE 1 — Core System Completion (v1.8.0–v1.10.0)
> Finish the half-built systems before expanding the world. Each update completes a core pillar of the existing game.

---

### **v1.8.0 - Spells & Crafting**
Priority: **MEDIUM**

- ✅ **7 New Spell Scrolls**
  - Gravity Well — pulls all enemies within 15 blocks toward the caster
  - Chain Lightning — bounces lightning across up to 4 enemies (8-block bounce range)
  - Ice Wall — places a 5×3 frosted ice barrier in front of the caster
  - Meteor Storm — rains 5 fireballs onto the targeted area
  - Life Drain — AoE drain (12 blocks); heals caster 2 HP per enemy hit, capped at 10 HP
  - Dimensional Rift — horizontal teleport 15 blocks forward; no wall required (unlike Blink)
  - Polymorph — applies Slowness VI + Weakness VI + Blindness to nearest mob for 8 seconds

- ✅ **Spell Scroll Crafting**
  - Shapeless crafting recipes for all 7 new scrolls (2× paper + thematic powder + rare ingredient)
  - Fixed broken fireball_scroll recipe (referenced nonexistent `potent_sulfur`; corrected to `potent_sulfur_powder`)

---

### **v1.9.0 - Class Trainer & Daily Quests** ✅ Complete
Priority: **MEDIUM**

- ✅ **Class Trainer NPC** — Dedicated NPC for class quest chain interaction (see NPCs section)
- ✅ **Class quest system separation** — Class quests removed from Quest Block; Quest Block now shows MAIN/SIDE only
- ✅ **Onboarding improvements** — Custom quest block texture, welcome book rewrite (8 pages), village NPC spawn density reduction, Hall of Champions ocean biome exclusion
- ✅ **Daily Quest System**
  - Server-wide rotating pool of 5 quests per UTC day
  - 3 completions per player per day
  - Streak-based XP multipliers (3d ×1.1, 7d ×1.25, 14d ×1.5)
  - Level scaling multipliers (50+ ×1.3, 100+ ×1.6, 150+ ×2.0)
  - Job Board main menu shows streak, pool status, and midnight reset countdown
- ✅ **Bug fixes** — All custom items (mythril, armor sets, special weapons) now enchantable; Elf quest grind reduced

---

### **v1.10.0 - Custom Enchantments** ✅ Complete
Priority: **MEDIUM**

- ✅ **Race-Specific Enchantments**
  - Dwarf: Deep Striker (bonus damage underground)
  - Elf: Forest's Blessing (bonus attack damage in forest biomes)
  - Orc: Berserker's Fury (damage scales with missing health)
  - Human: Versatile (bonus XP from all sources — stacks additively with the existing +25% Human race passive)

- ✅ **Class-Specific Enchantments**
  - Warrior: Immovable (knockback resistance)
  - Mage: Arcane Amplification (boosts the 4 core mage abilities' power)
  - Rogue: Shadow Step (passive chance to dodge a hit entirely)

- ✅ **Race/class apply-time gating** — all 7 enchantments can only be applied via the
  enchanting table or anvil by a player of the matching race/class; if race/class is later
  reset, the enchantment stays on the item but does nothing until they match again

---

### 🔷 PHASE 2 — The Overhaul & World Expansion (v2.0.0–v2.2.0)
> v2.0.0 is a save-breaking overhaul that restructures the entire new player experience and progression spine. Existing worlds will not be compatible.

---

### **v2.0.0 - The Overhaul** ⚠️ Save-Breaking 🟡 Partially Implemented
Priority: **HIGH** (Major milestone)

> Turns DAGMod from a feature list into a coherent narrative arc. The overworld becomes a story with a beginning, middle, and earned endpoint. All future content is designed around this spine.

> See full design document in the [2.0 Overhaul Design](#20-overhaul-design) section below.

> **Status check (2026-08-07)**: the onboarding half of this milestone (Inn as spawn hub, Garrick handling race/class selection) already shipped quietly in **v1.9.1** and was never reflected here. The progression half (broken gear, champion books, quest-gated dimension access, HoC as a true earned singleton) has **not** been started. Verified against `HallSpawnInitializer.java`, `InnkeeperGarrickNPC.java`, and the `hall_spawn`/`village_inn_set` structure_set placement configs — see per-item notes below.

- ✅ **Starting Inn replaces early-game HoC dependency** — complete as of 2026-08-10
  - ✅ Village Inn structure generates and IS the world spawn point (`HallSpawnInitializer` locates it and calls `/setworldspawn` on first server start — v1.9.1)
  - ✅ Race and class selection handled entirely through Garrick (no altars needed near spawn — see below)
  - ✅ Job Board accessible from the Inn from day one — confirmed `dagmod:job_board` and `dagmod:quest_block` are real placed blocks in `village_inn.nbt`
  - ✅ Armorer and Jeweler NPC in the Inn — added as baked entities in Garrick's own room (open floor space, no architecture changes to the hand-built structure); not a literal separate "basement" as the original design doc envisioned, just present and reachable in the Inn from day one

- ✅ **Garrick as Guild Registry (Race & Class Selection)** — mostly done, v1.9.1
  - ✅ Race and class selection moved to Garrick NPC dialogue (`InnkeeperGarrickNPC.showRaceMenu`/`showClassMenu`, chat-menu confirmed, same system as quest menus)
  - ✅ Voodoo Illusioner takes over reset mechanics (race/class reset applied via NPC, not an altar)
  - ❓ Stat summaries in-dialogue, starter gear handed out on selection — not specifically re-verified this pass, likely present given the menu flow but worth a quick confirm
  - **Note**: the physical Race/Class Selection Altars still exist and still work as a direct, standalone interaction path (right-click with a class token still selects a class) — they haven't been removed or repurposed, just made non-mandatory now that Garrick offers the same thing

- ✅ **Hall of Champions as Earned Destination** — complete as of 2026-08-10, definition revised from the original design
  - ✅ True one-per-world singleton (`hall_spawn.json`: `minecraft:concentric_rings, count: 1`, same pattern as `castle_pale_garden`) — was `random_spread` before, meaning more than one could theoretically exist far apart
  - **Revised "earned"**: not gated behind Master book completion + a Garrick compass grant as originally designed. Garrick still hands out the Hall Locator immediately at character registration, and his dialogue actively points new players toward HoC early — because the Class Trainer (which grants class ability quests) only exists there, so locking HoC away would block class progression entirely. Decided the combination of (a) real distance to travel there now that it's a true singleton, and (b) the first class quest itself being level-10-gated regardless of arrival time, already produces a meaningful "earned" journey without needing the literal compass mechanic.
  - **Dropped, not deferred**: Champion Registration Altars and the Garrick-compass-at-Master-book trigger are no longer planned — both were downstream of Champion Book Progression, which doesn't exist and isn't needed to gate HoC access under the revised definition.
  - Known tradeoff: switching to vanilla `concentric_rings` lost this mod's custom `avoid_water` check (bone dungeon exclusion is preserved — `bone_dungeon_set` independently excludes itself from spawning near `hall_spawn`)

- ❌ **Broken Gear Progression Loop** — not started, no code or items found
- ❌ **Champion Book Progression** — not started; Master Quest Book is still the top tier

- 🟡 **Dimension Access Gating**
  - ✅ Simple level gates exist: Bone Dungeon locator gated at level 25, Dragon Realm gated at level 50
  - ❌ Not gated by champion quest completion or a Bone-Dungeon-in-the-Nether unlock chain — just flat level checks, not the quest-driven gating this item describes

- ❌ **Content Scaling Pass** — not done as a dedicated pass
- ❌ **Major Polish & Balance Pass** — ongoing by nature; treat as a pre-release checklist item rather than a discrete task

---

### **v2.1.0 - Economy Foundation**
Priority: **MEDIUM**

- ❌ **Race Quest Chain Expansions**
  - Additional quest chains for each race
  - Race-specific storylines and legendary rewards

- ❌ **Additional Boss Encounters**
  - Additional mini-bosses in Bone Dungeons
  - Enhanced loot tables for new bosses

- ❌ **Coin Currency System**
  - 4 coin tiers: Copper → Silver → Gold → Platinum (100:1 ratio each)
  - Add in coin purse (using bundle idea)
  - Replaces/supplements emerald-based trading with all merchant NPCs
  - Foundation that all future economy features are built upon
  - Bounty rewards, quest rewards, and loot all pay out in coins

- ❌ **Bounty System**
  - Bounty Hunter NPC in the Hall of Champions (accept and turn-in bounties)
  - Dynamically generated bounties — up to 7 days active before reset, not daily
  - First-come-first-served claim; locked to claiming player for up to 7 days
  - Bounty tiers: Common, Rare, Legendary, Seasonal
  - Rewards: coins + Bounty Tokens + unique items
  - Ties into Title System — bounty completions unlock Hunter titles

---

### **v2.2.0 - The Pale Abyss**
Priority: **LOW** (Long-term)

> See full design document in the [Pale Abyss Design](#pale-abyss-design) section below.

- ❌ **Pale Abyss Dimension**
  - Two-layer design: Pale Garden surface (pale oak forest, eerie and quiet) → deep cavern system below
  - Surface layer becomes increasingly webbed and corrupted the deeper players explore
  - Massive cavern system housing the Spider Queen's lair at the base
  - Level gate: ~Level 50-70
  - Portal located in the Hall of Champions

- ❌ **Pale Abyss Portal Key**
  - Craftable key using overworld and Pale Abyss materials only
  - Poison Sac — quest reward from intro quest chain
  - Pale Silk — rare drop in overworld cave biomes
  - Venom Crystal — early Pale Abyss exploration drop
  - Additional Pale Abyss harvested materials (TBD)

- ❌ **Introduction Quest Chain**
  - **"Strange Webs"** — Investigate unusual webbing in cave biomes; find a Pale Stalker that has migrated to the overworld
  - **"The Source"** — Kill overworld spiders and collect Poison Sacs (quest item only, not a random drop)
  - **"Something Bigger"** — A dangerous encounter hints at something much larger lurking elsewhere
  - **"The Pale Gate"** — Craft the portal key and open the Pale Abyss portal

- ❌ **Mob Hierarchy**
  - **Cave Crawler** — Fast, weak minion; dangerous in swarms
  - **Pale Stalker** — Camouflages against pale oak trees on the surface layer
  - **Venom Weaver** — Ranged web-slinger; slows and poisons players
  - **Brood Warden** — Renamed current Spider Queen mob; lays hatching eggs; roams surface and upper caves
  - **The Spider Queen** — Massive boss in the deepest cavern chamber; multi-phase fight

- ❌ **Spider Queen Boss Fight**
  - **Phase 1** — Cavern floor combat; summons Brood Wardens and Cave Crawlers
  - **Phase 2** (below 60% health) — Retreats to cavern ceiling; drops web traps and egg sacs; players must dodge falling hazards
  - **Phase 3** (below 30% health) — Full enrage; returns to floor; web AoE roots all players

- ❌ **Exclusive Harvested Materials**
  - No metal ore — all materials harvested from mobs (unique biological crafting identity)
  - **Chitin Fragment** — Common; Cave Crawlers
  - **Pale Silk** — Common; Pale Stalkers and environment webs
  - **Venom Sac** — Mid-tier; Venom Weavers
  - **Carapace Plate** — Mid-tier; Brood Wardens
  - **Compound Eye** — Rare; Brood Wardens
  - **Queen's Fang** — Legendary; Spider Queen only
  - **Queen's Silk** — Legendary; Spider Queen only

- ❌ **Biological Crafting System**
  - **Chitin Armor Set** — Chitin Fragments + Carapace Plates; lightweight, high evasion bonuses
  - **Venom Weapons** — Venom Sac coating; apply poison on hit
  - **Queen's Silk Armor** — Legendary tier; unique set bonuses
  - **Compound Eye Trinket** — Ties into v2.3.0 Jewelry system; enhanced detection abilities
  - **Queen's Fang Dagger** — Legendary weapon; guaranteed poison + bonus damage

---

### **v2.3.0 - Economy & Trading**
Priority: **LOW** (Long-term)
> Builds directly on the Coin Currency foundation from v2.1.0.

- ❌ **Gem Socket System**
  - Add sockets to gear (1-3 sockets based on item tier)
  - Dragon Realm gems (Citrine, Pink Garnet, Ruby, Sapphire, Tanzanite, Zircon, Topaz, Silmaril)
  - Gem stat bonuses:
    - Ruby → +Attack Damage / Strength
    - Sapphire → +Max Health
    - Tanzanite → +Movement Speed
    - Citrine → +Attack Speed
    - Topaz → +Armor / Defense
    - Zircon → +Luck (Fortune bonus)
    - Pink Garnet → TBD (playtesting)
    - Silmaril → Legendary tier (multiple bonuses)
  - Socket insertion mechanic (Smithing Table or custom Gem Socketing Station)
  - Gem removal/replacement system
  - Visual indicators for socketed items
  - Component-based implementation (MC 1.21.11 item components)

- ❌ **Transmog System**
  - Keep stats, change appearance
  - Appearance library/collection
  - Transmog station block
  - Transmog costs (coins/materials)

- ❌ **Item Reforging**
  - Reroll gear stats
  - Stat ranges and quality tiers
  - Reforge costs scale with quality
  - Lock stats during reforge

- ❌ **Auction House**
  - Player marketplace running on coin currency
  - Bidding system
  - Search and filter
  - Auction duration and fees
  - Buy-out prices

---

### 🔷 PHASE 3 — Depth & Social Systems (v2.4.0–v2.6.0)
> Deepen the RPG systems and introduce the permanent social endgame structures.

---

### **v2.4.0 - Jewelry & Trinkets**
Priority: **LOW** (Long-term)
> Builds on the Gem Socket system from v2.3.0.

- ❌ **Jewelry Slots**
  - 2 Ring slots
  - 1 Necklace/Amulet slot
  - 1 Bracelet/Charm slot (optional)
  - Custom inventory GUI integration
  - Equipment slot renderer

- ❌ **Craftable Jewelry**
  - Ring crafting (various tiers: Iron, Gold, Diamond, Mythril, Legendary)
  - Necklace/Amulet crafting
  - Gem-socketed jewelry (combines with v2.2.0 socket system)
  - Unique jewelry effects (speed boost, regeneration, mana regen, etc.)
  - Class-specific jewelry (Mage rings, Warrior amulets, Rogue charms)
  - Race-specific jewelry bonuses
  - Pale Abyss Compound Eye as a rare trinket slot item

- ❌ **Jewelry Stats & Effects**
  - Passive stat bonuses (health, damage, armor, etc.)
  - Unique effects (water breathing, fire resistance, night vision, etc.)
  - Set bonuses for wearing matching jewelry
  - Enchantable jewelry
  - Jewelry durability (optional - may not degrade)

- ❌ **Integration with Existing Systems**
  - Jewelry as quest and bounty rewards
  - Boss-specific legendary jewelry drops
  - Jewelry upgrade paths

---

### **v2.5.0 - Guild System**
Priority: **LOW** (Long-term)
> Releasing after the economy is established means the guild bank, trading, and social features have a full foundation to build on.

> See full design document in the [Guild System Design](#guild-system-design) section below.

- ❌ **Guild Creation & Management**
  - Create/join/leave guilds
  - Guild ranks and permissions
  - Guild chat channel
  - Guild MOTD (Message of the Day)

- ❌ **Guild Progression**
  - Guild levels and XP
  - Guild perks unlock system
  - Guild quests — all exclusive to Bleakwind, all require full guild cooperation (no solo completion)
  - Guild reputation

- ❌ **Guild Features**
  - Guild bank/storage (operates on coin currency from v2.0.0)
  - Guild halls/headquarters
  - Guild vs Guild events
  - Guild leaderboards

---

### **v2.6.0 - The Labyrinth**
Priority: **MEDIUM**
> A story-driven update that bridges the v2.x world-building phase and the v3.0 endgame expansion.

- ❌ **Story Hook**
  - Player is sent on a search and retrieve quest (e.g. "Search the Ruins for a lost artifact")
  - Upon finding the artifact, the Dark Mage ambushes the player and teleports them into the Labyrinth
  - Sets up a mystery/revenge arc — escape the Labyrinth, hunt down the Dark Mage

- ❌ **Dark Mage Boss**
  - New hostile mob — triggers the ambush and teleport on quest completion
  - Custom combat mechanics and spellcasting behaviour
  - Defeating the Dark Mage is the only way to escape the Labyrinth

- ❌ **Labyrinth Dimension**
  - New custom dimension — a sprawling, procedurally generated maze
  - Player arrives with no warning — survival and navigation are immediate challenges
  - Navigate through the labyrinth to locate the Dark Mage's lair
  - Custom loot, traps, and hazards throughout
  - Escape portal opens upon defeating the Dark Mage

---

### 🔷 PHASE 4 — Endgame (v3.0.0+)
> The full endgame experience. Every prior phase feeds into this.

---

### **v3.0.0 - Endgame Expansion**
Priority: **VERY LOW** (Long-term vision - Major expansion)

- ❌ **Raid Bosses**
  - Multi-player required encounters (4-10 players)
  - Complex boss mechanics
  - Enrage timers
  - Epic loot tables
  - Weekly lockouts

- ❌ **Dynamic Difficulty System**
  - Adaptive mob scaling with player level
  - Hardcore mode toggle (permadeath or severe penalties)
  - Nightmare dungeons (increased difficulty, better loot)
  - Difficulty multipliers

- ❌ **Endless Dungeon Mode**
  - Wave survival system
  - Progressive difficulty scaling
  - Wave leaderboards
  - Endless-specific rewards

- ❌ **Prestige System**
  - Reset to level 1 with permanent bonuses
  - Prestige levels (1-10+)
  - Unique prestige perks
  - Prestige-only cosmetics

- ❌ **Seasonal Events**
  - Limited-time content
  - Seasonal quests and bosses
  - Exclusive seasonal rewards
  - Event calendar

---

### **vTBD - The Unholy Realm**
Priority: **VERY LOW** (Long-term — targeted near v3.0.0–v4.0.0)

> See full design document in the [Unholy Realm Design](#unholy-realm-design) section below.
> **Note**: The Paladin class (4th class) releases alongside this update — not before.

- ❌ **Unholy Realm Dimension**
  - Dark corrupted dimension — cracked terrain, dead twisted trees, pools of dark liquid
  - Purplish-black sky with no natural light
  - Ambient Wither and Weakness effects without protection
  - Desecrated temple and collapsed cathedral structures throughout
  - Level gate: ~Level 80-100

- ❌ **Unholy Sigil (Portal Key)**
  - Craftable item required to open the portal in the Hall of Champions
  - Recipe requires materials from multiple progression layers:
    - Skeleton King drop (Bone Realm cleared)
    - Dragon Heart (Dragon Realm cleared)
    - Cursed Soul Shard (rare overworld drop)
    - Blessed Ingot (crafted from Unholy Realm materials + gold)

- ❌ **Paladin Class** (4th class — releases with this update)
  - Class selection altar integration
  - 5 abilities: Holy Strike, Divine Shield, Consecrate, Lay on Hands, Aura of Light
  - Passive bonus damage against all Unholy mobs
  - Race synergies:
    - Human (Devout Champion): Enhanced healing, extended Divine Shield duration
    - Dwarf (Ironclad Devotion): Bonus armor while abilities active, Consecrate slows enemies
    - Elf (Radiant Sentinel): Extended aura range, Holy Strike blinds Unholy mobs briefly
    - Orc (Wrathful Consecration): Holy Strike deals bonus damage, reduced support effectiveness

- ❌ **Unholy Mob Hierarchy**
  - **Shade** — Fast, weak minion; dangerous in swarms
  - **Unholy Priest** — Support elite; heals nearby Unholy mobs
  - **Cursed Knight** — Tanky melee elite; applies Wither on hit
  - **Lich Lord** — Mini-boss; spellcaster that summons Shades, has phase mechanic
  - **The Archlich** — Final boss; invulnerable until all Phylacteries destroyed
  - All Unholy mobs: hurt by Holy effects, resistant to Poison and Weakness, emit passive debuff auras

- ❌ **Phylactery Shrine System**
  - 5 Phylactery Shrines scattered across the Unholy Realm
  - The Archlich regenerates health while any Phylactery remains intact — cannot be damaged
  - Each Shrine is uniquely designed and guarded:
    - **Shrine of Souls** — Guarded by a Lich Lord; surrounded by Shade swarms
    - **Shrine of Wrath** — Guarded by 3 Cursed Knights; Wither aura, ranged attacks disabled
    - **Shrine of Ruin** — Unholy Priest keeps regenerating the Shrine's health
    - **Shrine of Decay** — Lich Lord guarded by lingering damage zone
    - **Shrine of Oblivion** — The Archlich watches but cannot intervene; pure navigation challenge
  - Destroying each Shrine triggers a realm-wide announcement
  - Archlich grows more aggressive with each Shrine destroyed
  - Once all Shrines are destroyed, the Archlich becomes vulnerable

- ❌ **Exclusive Materials**
  - **Soulsteel** — Unholy Realm metal (equivalent to Mythril); passive Wither effect on weapons
  - **Voidstone** — Common crafting material
  - **Cursed Amethyst** — Mid-tier; used in potions and enchanting
  - **Soulfire Crystal** — Rare; used in top-tier Holy weapons
  - **Phylactery Shard** — Drops from Lich Lords; lore item / locator crafting

- ❌ **Holy Enchantments**
  - **Holy Wrath** — Bonus damage against all Unholy mobs (Smite equivalent)
  - **Consecrated** — Chance to apply Holy Burn on hit (damage over time vs Unholy only)
  - **Purifying Strike** — Reduces Unholy mob healing (counters Priest aura)
  - **Blessed** — Passive aura that weakens nearby Unholy mobs' damage output
  - **Soulbane** — Extra damage specifically to Liches (rare, high-tier)

- ❌ **Holy Potions & Consumables**
  - **Holy Water Flask** — Splash potion; damages all Unholy mobs in radius
  - **Purified Light Potion** — Stronger splash; damages and slows Unholy mobs
  - **Consecrated Oil** — Weapon coating; next few hits deal bonus Holy damage
  - **Blessed Incense** — Thrown lingering cloud; Unholy mobs take damage while inside
  - **Vial of Sacred Flame** — Ignites Unholy mobs with Holy Fire (bypasses fire resistance)
  - All Holy potions require Unholy Realm materials to craft

- ❌ **Guild Mission Integration**
  - Unholy Realm content available as high-tier guild missions
  - Archlich encounter naturally suited to full guild groups
  - Phylactery Shrine system rewards coordinated multi-player strategies

---

### 🔷 ONGOING — Quality of Life & Future Features
> Not tied to a specific version. Slotted into releases as development allows.

---

### **Future Quality of Life**
Priority: **AS NEEDED**

- ❌ **Waypoint System**
  - Discoverable waypoints in world
  - Fast travel between waypoints
  - Cooldown/cost system
  - Waypoint sharing in parties

- ❌ **Auto-Sort Inventory**
  - One-click inventory organization
  - Customizable sort rules
  - Quick deposit to storage
  - Sort by type/rarity/value

- ❌ **Skill Loadouts**
  - Save ability configurations
  - Switch loadouts on the fly
  - Loadout presets (PvE, PvP, Solo, Party)
  - Keybind per loadout

- ❌ **Quest Markers**
  - Show quest locations on compass
  - Waypoint markers for objectives
  - Distance indicators
  - Quest path highlighting

### **Future RPG Features**
Priority: **LOW**

- ❌ **Title System**
  - Earn titles from achievements and bounty completions
  - Display titles above player name
  - Title-based stat bonuses
  - Rare/legendary titles

- ❌ **Reputation Factions**
  - Multiple NPC factions
  - Earn favor through quests/actions
  - Reputation tiers (Hostile → Exalted)
  - Reputation-gated rewards and quests

- ❌ **Collection Book**
  - Track discovered items/mobs
  - Collection completion rewards
  - Collection milestones
  - Rare/legendary collection entries

- ❌ **Achievement System**
  - Integrated with Minecraft achievements
  - Custom DAGMod achievements
  - Hidden achievements
  - Achievement-based rewards

- ❌ **Leaderboards**
  - Server rankings
  - Multiple categories (level, quests, kills, boss kills, bounties, etc.)
  - Seasonal leaderboards
  - Rewards for top players

### **Future Cosmetic Features**
Priority: **VERY LOW**

- ❌ **Mounts System**
  - Rideable creatures
  - Mount speeds and abilities
  - Mount collection
  - Mount customization

- ❌ **Pet Companions**
  - Non-combat followers
  - Pet abilities and bonuses
  - Pet leveling
  - Pet collection

- ❌ **Player Housing**
  - Personal base with storage
  - Furniture and decorations
  - Storage upgrades
  - Housing customization

- ❌ **Character Customization**
  - Enhanced appearance options
  - Cosmetic armor slots
  - Dye system for armor
  - Skin overlays

### **Future World Features**
Priority: **LOW**

- 🟡 **NPC Vendors** (Partially complete)
  - ✅ 13 merchant NPCs with trading (v1.6.0-v1.6.5)
  - ✅ Quest giver NPC (Innkeeper Garrick, v1.5.0)
  - ❌ Reputation-locked vendors

- ❌ **Mining Expeditions**
  - Deep-dive mining events
  - Progressive depth challenges
  - Rare ore discoveries at depth
  - Mining party bonuses

- ❌ **Cooking System Overhaul**
  - Food combination mechanics
  - Recipe discovery
  - Cooking stations
  - Complex buff stacking

- ❌ **Fishing Overhaul**
  - Special rare fish
  - Fish-based buffs and consumables
  - Fishing quests
  - Fishing tournaments

- ❌ **Dynamic Weather Effects**
  - Environmental challenges
  - Weather-based buffs/debuffs
  - Seasonal weather patterns
  - Weather-triggered events

---

## 🚫 **Scrapped/Deprecated Features**

*(None currently - all planned features remain on roadmap)*

---

## 📊 **Feature Comparison Matrix**

| Feature Category | Discussed | Implemented | In Progress | Planned |
|-----------------|-----------|-------------|-------------|---------|
| Social/Multiplayer | 6 | 1 (Party) | 0 | 5 (Guilds, etc.) |
| Endgame Systems | 5 | 0 | 0 | 5 (Raids, Prestige, etc.) |
| Consumables | 5 | 5 | 0 | 0 |
| RPG Enhancement | 7 | 0 | 0 | 7 |
| Crafting/Economy | 5 | 0 | 0 | 5 |
| Quality of Life | 6 | 2 (Travel, Graves) | 0 | 4 |
| Progression | 4 | 1 (Achievements) | 0 | 3 |
| Cosmetic | 4 | 0 | 0 | 4 |
| NPC/World | 4 | 2 (Garrick, 13 Merchants) | 0 | 2 |

---

## 🗓️ **Development Timeline**

### **Released**
- ✅ **v1.4.5-beta** (Oct 2024) - Job Board, quest categorization
- ✅ **v1.5.0-beta** (Jan 2025) - Garrick NPC, quest gating, health persistence fix
- ✅ **v1.5.1-beta** (Jan 2025) - Patch: Quest reward fixes, backward compatibility
- ✅ **v1.5.2-beta** (Jan 2025) - CRITICAL: Quest persistence, memory leaks, thread safety
- ✅ **v1.5.3-beta** (Dec 2025) - CRITICAL: Progression reset fix, new world data leakage fix, quest level 20 blocker fix
- ✅ **v1.6.0-beta** (Dec 2025-Feb 2026) - Rotating trade system, enhanced consumables, Dragon Realm, custom enchantments
- ✅ **v1.6.5-beta** (Feb 2026) - 5 new merchant NPCs (13 total), quest progression fixes, loot table fixes
- ✅ **v1.6.6-beta** (Feb 2026) - Java-based death recovery (grave) system, replaces datapack graves
- ✅ **v1.7.0** (Feb 2026) - Level cap 50→200, heart scaling rework (+1 heart/10 levels)
- ✅ **v1.7.1** (Feb 2026) - Gem ore worldgen in Overworld (6 custom ores with tiered rarity)
- ✅ **v1.7.2** (Feb 2026) - Bone dungeon overhaul, gem ore worldgen, NPC merchant structures
- ✅ **v1.7.3** (Feb 2026) - Red Dragon quest entity, gem ore mining tiers, dragon spawn timing rework, Dragon Key recipe unlock system
- ✅ **v1.7.4** (Mar 2026) - Real consumable mechanics (lifesteal, dodge, spell mods, last stand + void rescue, grave void fix), dragon recipe rework, dragon stat buffs, Master Trader quest fix
- ✅ **v1.7.5** (Mar 2026) - Skeleton Lord auto-spawning in boss room, Boss Spawn Trigger block, Necrotic Key in loot table, Hall of Champions locator chat coordinates, Bone Realm portal height fixes
- ✅ **v1.7.6** (Mar 2026) - Hall of Champions merchants (9 NPCs), rotating trade system per-merchant, merchant dialogue, `/merchant` admin commands
- ✅ **v1.7.7** (Mar 2026) - Class quest chains rebuilt (all 3 classes, 5 quests each, level-gated 10/25/50/75/100), per-quest level requirement system, enchanted book reward fixes, race quest polish, WildDragonEntity crash fixes
- ✅ **v1.7.8** (Mar 2026) - Skeleton King boss encounter (throne room, barrier-sealed room, party-scaled), Skeleton Throne Room structure, King's Recall Stone, per-player chest rewards, Seasons datapack, boss stat overhaul
- ✅ **v1.7.9** (Apr 2026) - `/seasons` command with interactive setup menu, seasons first-run notice, manual operator activation, season settings persistence, Skeleton Kingdom structure chain, jigsaw anchor fixes
- ✅ **v1.7.10** (Apr 2026) - Quest book upgrade overhaul, Job Board expanded to 19 jobs, dynamic held-item lighting
- ✅ **v1.8.0** (May 2026) - Minecraft 26.1.2 migration, Potent Sulfur Powder crafting material, bone dungeon portal room improvements, shield orientation fixes
- ✅ **v1.8.1** (Jun 2026) - Minecraft 26.2 migration, brimstone rename, MC 26.2 API fixes
- ✅ **v1.8.2** (Jun 2026) - Village NPC structures (7 individual structures), dynamic lighting terrain fix, Hall of Champions rarity increase
- ✅ **v1.8.3** (Jul 2026) - Gem tier system (Cut/Polished/Flawless/Grand), in-game enchantment descriptions, quest book objective navigation, gem station inventory persistence
- ✅ **v1.9.0** (Jul 2026) - Class Trainer NPC, class quest separation from Quest Block, daily quest system (server-wide rotation, streak multipliers, UTC reset), Garrick handoff, HoC land-biome tag, all custom items now enchantable, Elf quest balance fixes
- ✅ **v1.9.1** (Jul 2026) - Blacksmith's Anvil block, Garrick guild registry, Voodoo Illusioner reset handling, Dragon Realm level-50 gate, merchant tier gating, all NPCs stationary, Village Inn as spawn hub, HoC moved to mid/late-game
- ✅ **v1.10.0** (Aug 2026) - 7 race/class-specific enchantments (Deep Striker, Forest's Blessing, Berserker's Fury, Versatile, Immovable, Arcane Amplification, Shadow Step), race/class apply-time gating on the enchanting table and anvil

### **Phase 1** — Core System Completion
- ✅ **v1.9.0** - Class Trainer NPC, class quest separation, daily quest system, enchanting bug fix, quest balance
- ✅ **v1.10.0** - Custom race/class enchantments, race/class apply-time gating

### **Phase 2** — The Overhaul & World Expansion
- 🟡 **v2.0.0** (Major — ⚠️ Save-Breaking) — Partially underway: Inn-as-spawn-hub and Garrick guild registry (race/class via dialogue) already shipped in v1.9.1. Still needed: HoC as a true earned singleton, broken gear loop, champion book progression, quest-gated dimension access, content scaling pass
- 🎯 **v2.1.0** - Economy Foundation: Race quest expansions, additional bosses, Coin Currency, Bounty System
- 🎯 **v2.2.0** - The Pale Abyss dimension + Spider Queen rework
- 🎯 **v2.3.0** - Economy & Trading (Gem Sockets, Transmog, Reforging, Auction House)

### **Phase 3** — Depth & Social Systems
- 🎯 **v2.4.0** - Jewelry & Trinkets system
- 🎯 **v2.5.0** - Guild System (Bleakwind, permanent endgame social)
- 🎯 **v2.6.0** - The Labyrinth dimension + Dark Mage boss

### **Phase 4** — Endgame
- 🎯 **v3.0.0** (Major) - Endgame expansion (raids, prestige, seasonal events)
- 🎯 **vTBD** - The Unholy Realm + Paladin class (4th class) — targeted near v3.0.0–v4.0.0
- 🎯 Future major expansions as needed

---

## 📋 **Design Documents**

### 2.0 Overhaul Design

#### Overview
v2.0.0 restructures the entire new player experience around a narrative arc. The current model (spawn → find HoC → overwhelmed by every system at once) is replaced with a guided journey: Inn → Garrick → questing → master book → compass → Hall of Champions. The HoC becomes an earned destination and a reward, not a starting zone.

This is a **save-breaking** update. Existing worlds will not be compatible. The 1.x branch will continue to receive bug fixes and balance patches while 2.0 is in development.

---

#### New Player Flow

1. **Spawn** — Player receives a book: *"Find the local Innkeeper Garrick to begin your journey."*
2. **The Inn** — Small structure generates near spawn. Garrick is inside, along with gear displays showing broken HoC items (aspirational rewards) and a strange compass.
3. **Garrick — Guild Registry** — Garrick handles race and class selection via dialogue (guild ledger framing). No physical altars near spawn. Race abilities and starter gear granted on selection. Class abilities and starter gear granted on class selection.
4. **Tutorial** — Garrick gives the 3 tutorial quest notes. Player completes them, gets the Novice Quest Book. Garrick provides flavor text at each book advancement.
5. **Garrick Milestone Dialogue** — At Master book: *"These are relics from a better time... For now see if you can get these fixed up."* Broken gear given. Armorer and Jeweler in Inn basement handle repairs using gems from the Job Board.
6. **Garrick Sends You** — On finishing the master questline: *"Take this. It might just get you where you need to go."* Compass given → HoC spawns ~5,000 blocks away. One per world.
7. **Hall of Champions** — The wow moment. Full merchant roster, Class Trainer, Champion Registration points (repurposed altars). A guide NPC takes over from Garrick: *"So another one has found their way to this sacred place."*
8. **Champion Progression** — Master Quest Book replaced with Novice Champion Book. Champion quests gate dimension access.

---

#### Garrick as Guild Registry

Race and class selection are moved from physical altars to Garrick NPC dialogue. This removes the multi-stop early game (find Garrick → find race altar → find class altar → come back) and makes the first interaction feel like an event rather than a tutorial checklist.

- Race selection presented as a chat menu with race summaries
- Class selection follows immediately: *"Now, how do you fight?"*
- Starter gear given on the spot for each selection
- **Voodoo Illusioner** takes over reset mechanics — applying Race Reset and Class Reset items is done by talking to the Illusioner rather than clicking an altar

---

#### Race/Class Altars in HoC

The physical Race and Class Selection Altars are removed from the early game. In the Hall of Champions they are repurposed as:
- **Champion Registration Altars** — interact to advance your champion book tier when requirements are met
- Or retained as decorative lore pieces reinforcing the "sacred hall" atmosphere
- Their visual presence in HoC still carries weight — players will recognize them and understand their significance after the journey to get there

---

#### Broken Gear Loop

| Stage | Item | Power Level |
|-------|------|-------------|
| Garrick reward | Broken HoC gear | Stronger than iron |
| After repair | Repaired HoC gear | Weaker than full HoC original |
| Full HoC original | Merchant trade in HoC | Best in class |

- Repair cost: broken item + gem materials (sourced from Job Board jobs)
- Once a piece is repaired, the Armorer permanently offers the repaired variant for trade (handles lost gear)
- This gives the Job Board a concrete purpose beyond income and introduces players to the gem economy before they reach HoC

---

#### Champion Book Progression

| Book | Unlocks | Quest Focus |
|------|---------|-------------|
| Novice Champion | First dimension access | Overworld boss kills, dungeon clears |
| Advanced Champion | Bone Realm access | Ossuary Depths, Skeleton King progression |
| Final Champion | Dragon Realm access | Dragon Realm bosses, endgame content |

- Some HoC vendor trades locked behind champion book tier (visible but requires book to trade)
- Dimension portals physically locked until the appropriate champion book is held

---

#### Dimension Gating

**Bone Realm:**
- Requires completing Novice Champion quests
- Bone Dungeon locator given → a Bone Dungeon in the Nether must be completed to unlock access
- Dragon Nether connection adds natural MC progression integration

**Dragon Realm:**
- Requires completing Advanced Champion quests
- Dragon Eye compass + boss-specific drop (Dragon Egg/Heart/Head) required to unlock portal

---

#### Open Questions / To Be Decided
- [ ] Exact Inn structure design and spawn conditions
- [ ] Whether compass click at >5k blocks teleports player vs spawns a new marker
- [ ] Warp Scroll vendor for HoC fast travel (vs waypoints mod compat)
- [ ] How many champion quests per tier before book advancement
- [ ] Specific broken gear item list and repair costs per piece
- [ ] Final placement and function of repurposed altars in HoC

---

### Pale Abyss Design

#### Overview
The Pale Abyss is a mid-level dimension accessible via a portal in the Hall of Champions. It features a two-layer design — a Pale Garden-style surface of corrupted pale oak forest that descends into massive cavern systems below. The current oversized Spider Queen mob is renamed to the Brood Warden and repositioned as a mid-tier elite. The actual Spider Queen boss is significantly larger and lives in the deepest cavern chamber. All dimension materials are harvested from mobs rather than mined as ores, giving it a unique biological crafting identity.

---

#### Access & Level Gate
- Portal permanently located in the **Hall of Champions**
- Opened using a craftable portal key requiring overworld + Pale Abyss materials only
- Recommended level gate: **~Level 50-70**
- Sits between the Bone Realm and Unholy Realm in the progression ladder
- No Bone Realm or Dragon Realm materials required

---

#### Portal Key Crafting
| Component | Source |
|---|---|
| Poison Sac | Quest reward — harvested from overworld spiders via intro quest chain |
| Pale Silk | Rare drop in overworld cave biomes |
| Venom Crystal | Early Pale Abyss exploration drop |
| Additional harvested materials | TBD |

---

#### Introduction Quest Chain
- **"Strange Webs"** — Investigate unusual webbing in cave biomes; encounter a Pale Stalker that has migrated to the overworld
- **"The Source"** — Kill overworld spiders and collect Poison Sacs (quest item only, not a random drop)
- **"Something Bigger"** — A dangerous encounter hints at something much larger lurking elsewhere
- **"The Pale Gate"** — Craft the portal key and open the Pale Abyss portal

---

#### Dimension Design
- **Surface Layer** — Pale Garden aesthetic; pale oak forest, eerie and quiet. Increasingly webbed and corrupted as players explore deeper
- **Cavern Layer** — Massive underground cave networks; near-zero visibility; bioluminescent spider eggs provide the only light
- **Queen's Lair** — The deepest chamber; the Spider Queen's arena

---

#### Mob Hierarchy

| Tier | Mob | Role |
|---|---|---|
| Minion | Cave Crawler | Fast, weak; dangerous in swarms |
| Minion | Pale Stalker | Camouflages against pale oak trees on the surface layer |
| Elite | Venom Weaver | Ranged web-slinger; slows and poisons players |
| Elite | Brood Warden | Renamed current Spider Queen; lays hatching eggs; roams surface and upper caves |
| Boss | The Spider Queen | Massive; lives in deepest cavern chamber; multi-phase fight |

**Note**: The current oversized Spider Queen mob is renamed to **Brood Warden** and repositioned as a mid-tier elite.

---

#### Spider Queen Boss Fight
- **Phase 1** — Cavern floor combat; summons Brood Wardens and Cave Crawlers
- **Phase 2** (below 60% health) — Retreats to cavern ceiling; drops web traps and egg sacs
- **Phase 3** (below 30% health) — Full enrage; returns to floor; web AoE roots all players

---

#### Exclusive Harvested Materials

| Material | Source | Tier |
|---|---|---|
| Chitin Fragment | Cave Crawlers | Common |
| Pale Silk | Pale Stalkers + environment webs | Common |
| Venom Sac | Venom Weavers | Mid-tier |
| Carapace Plate | Brood Wardens | Mid-tier |
| Compound Eye | Brood Wardens (rare) | Rare |
| Queen's Fang | Spider Queen only | Legendary |
| Queen's Silk | Spider Queen only | Legendary |

---

#### Biological Crafting System

| Item | Materials | Notes |
|---|---|---|
| Chitin Armor Set | Chitin Fragments + Carapace Plates | Lightweight; high evasion bonuses |
| Venom Weapons | Venom Sac coating | Apply poison on hit |
| Queen's Silk Armor | Queen's Silk | Legendary tier; unique set bonuses |
| Compound Eye Trinket | Compound Eye | Ties into v2.3.0 jewelry system; enhanced detection |
| Queen's Fang Dagger | Queen's Fang | Legendary; guaranteed poison + bonus damage |

---

#### Open Questions / To Be Decided
- [ ] Exact portal key recipe and final material names
- [ ] Full Brood Warden stats and abilities post-rename
- [ ] Whether Pale Stalker spawns in the overworld naturally or only in the dimension
- [ ] Respawn timer for the Spider Queen after defeat
- [ ] Whether the Compound Eye trinket ties directly into the v2.3.0 Jewelry system

---

### Guild System Design

#### Overview
The Guild System is a permanent endgame gameplay mechanic tied to the Bleakwind dimension. Once a player reaches a sufficient level, they gain access to guild quests that offer significantly better rewards than standard content. All guild quests require full guild cooperation — no quest can be completed solo, and parties are not a substitute for guilds.

---

#### Core Concepts

**Guilds vs Parties — Key Distinction**

| | Party | Guild |
|---|---|---|
| Duration | Temporary | Permanent |
| Purpose | General grouping | Endgame progression |
| Quest Access | Normal quests only | Guild-exclusive quests |
| Location | Anywhere | Bleakwind (guild quests) |
| Significance | Convenience mechanic | Endgame identity |

**Guilds**
- Permanent player organizations — a long-term commitment, not a temporary grouping
- Represent endgame identity and progression
- Unlock access to Bleakwind guild quests upon joining
- Guild bank operates on the coin currency established in v2.0.0
- Designed around coordinated, multi-player effort — teamwork is built into quest design, not optional

**Parties**
- Temporary groupings for general gameplay convenience
- **NOT** interchangeable with Guilds — must be clearly distinguished in UI and mechanics
- Do not grant access to guild quests under any circumstance
- Can be formed and disbanded freely with no lasting consequence
- No endgame significance

---

#### Bleakwind Dimension
- Bleakwind is a custom generated dimension within the Minecraft world
- Serves as the **dedicated and exclusive realm** for all guild quests
- Accessible only after a player has leveled up sufficiently and joined a guild
- Acts as a natural social hub where guild activity is concentrated

---

#### Guild Quest Rules
- All guild quests take place **exclusively in Bleakwind** — no exceptions
- **No guild quest can be completed solo** — cooperation is a hard requirement by design
- Quests must be built around requiring multiple roles or coordinated effort
- Guild quests should feel like **events**, not routine tasks
- Guild quests offer superior rewards compared to standard or party-based content

---

#### Open Questions / To Be Decided
- [ ] Minimum and maximum guild size
- [ ] Guild quest tier structure (difficulty tiers, reward scaling)
- [ ] How players create or join a guild (application system, invite-only, open join?)
- [ ] Guild ranking or internal progression system (officer roles, ranks, etc.)
- [ ] Consequences for leaving or being kicked from a guild
- [ ] Whether guilds can compete against each other in Bleakwind (GvG events)
- [ ] Whether Bleakwind has non-guild content or is exclusively a guild realm

---

### Unholy Realm Design

#### Overview
The Unholy Realm is a high-level endgame dimension accessible via a portal in the Hall of Champions, opened using the craftable Unholy Sigil. It is home to a hierarchy of Unholy mobs culminating in the Archlich — invulnerable until all 5 Phylactery Shrines are destroyed. The Paladin class releases alongside this update. Content is designed for groups and can serve as high-tier guild mission content.

---

#### Access & Level Gate
- Portal permanently located in the **Hall of Champions**
- Opened using the **Unholy Sigil** — requires materials from multiple progression layers
- Recommended level gate: **~Level 80-100**

---

#### Unholy Sigil Crafting
| Component | Source |
|---|---|
| Skeleton King drop | Bone Realm — proves Bone Realm cleared |
| Dragon Heart | Dragon Realm — proves Dragon Realm cleared |
| Cursed Soul Shard | Rare overworld drop from dark structures |
| Blessed Ingot | Crafted from Unholy Realm materials + gold |

---

#### Atmosphere & World Design
- Dark, corrupted dimension — cracked terrain, dead twisted trees, pools of dark liquid
- Purplish-black sky with no natural light
- Ambient **Wither** and **Weakness** effects applied to players without Holy protection
- Structures resemble desecrated temples and collapsed cathedrals

---

#### Mob Hierarchy

| Tier | Mob | Role |
|---|---|---|
| Minion | Shade | Fast, weak; dangerous in swarms |
| Elite | Unholy Priest | Heals nearby Unholy mobs; priority target |
| Elite | Cursed Knight | Tanky melee; applies Wither on hit |
| Mini-boss | Lich Lord | Spellcaster; summons Shades; drops Phylactery Shards |
| Final Boss | The Archlich | Invulnerable until all Phylacteries destroyed; multi-phase |

**Shared Unholy Mob Traits**: Hurt by Holy effects, resistant to Poison and Weakness, emit passive debuff auras.

---

#### Phylactery Shrine System

| Shrine | Guardian | Unique Hazard |
|---|---|---|
| Shrine of Souls | Lich Lord | Constant Shade swarms |
| Shrine of Wrath | Cursed Knight ×3 | Wither aura; ranged attacks disabled |
| Shrine of Ruin | Unholy Priest + minions | Priest regenerates Shrine health |
| Shrine of Decay | Lich Lord | Lingering damage zone |
| Shrine of Oblivion | Archlich watches; cannot act | Navigation/puzzle challenge |

- Each Shrine destruction triggers a realm-wide announcement
- Archlich grows more aggressive with each Shrine destroyed
- All 5 destroyed → Archlich becomes vulnerable

---

#### The Archlich Boss Fight
- **Phase 1** — Spellcasting and Shade summoning
- **Phase 2** (below 50% health) — Soul Drain, mass Wither pulse, faster casting
- **Phase 3** (below 25% health) — Enrage; increased speed, damage, and spawn rate

---

#### Exclusive Materials

| Material | Tier | Use |
|---|---|---|
| Voidstone | Common | Basic recipes |
| Cursed Amethyst | Mid-tier | Holy potions and enchanting |
| Soulsteel | Metal | High-tier gear; passive Wither on weapons |
| Soulfire Crystal | Rare | Top-tier Holy weapons |
| Phylactery Shard | Boss drop | Lore / locator crafting |

---

#### Holy Enchantments & Consumables

**Enchantments**
- **Holy Wrath** — Bonus damage vs all Unholy mobs
- **Consecrated** — Holy Burn on hit (DoT vs Unholy only)
- **Purifying Strike** — Reduces Unholy mob healing
- **Blessed** — Passive aura weakening nearby Unholy mobs
- **Soulbane** — Extra damage vs Liches specifically (rare, high-tier)

**Potions & Consumables**
- **Holy Water Flask** — Splash; damages all Unholy mobs in radius
- **Purified Light Potion** — Splash; damages and slows Unholy mobs
- **Consecrated Oil** — Weapon coating; bonus Holy damage on next hits
- **Blessed Incense** — Lingering cloud; damages Unholy mobs inside
- **Vial of Sacred Flame** — Holy Fire; bypasses fire resistance

---

#### Paladin Class

| Ability | Effect |
|---|---|
| Holy Strike | Melee + bonus Holy damage; extra vs Unholy |
| Divine Shield | Brief invulnerability; reflects damage |
| Consecrate | Blesses ground; damages Unholy mobs on it |
| Lay on Hands | Powerful single-target heal; long cooldown |
| Aura of Light | Passive aura weakening nearby Unholy mobs |

| Race | Synergy | Effect |
|---|---|---|
| Human | Devout Champion | Enhanced healing; longer Divine Shield |
| Dwarf | Ironclad Devotion | Bonus armor while active; Consecrate slows |
| Elf | Radiant Sentinel | Extended aura range; Holy Strike blinds briefly |
| Orc | Wrathful Consecration | Holy Strike bonus damage; reduced support |

---

#### Open Questions / To Be Decided
- [ ] Exact level gate (80, 90, or 100?)
- [ ] Unholy Sigil exact crafting recipe and final material names
- [ ] Soulsteel gear stats and set bonuses
- [ ] Archlich respawn timer after defeat
- [ ] Whether Holy potions can be used outside the Unholy Realm

---

## 📝 **Notes & Guidelines**

### **Version Numbering (Semantic Versioning)**

Following **SemVer 2.0.0** format: `MAJOR.MINOR.PATCH`

**MAJOR** (x.0.0): Major content milestones, game-changing features, major expansions
**MINOR** (0.y.0): New features, content, systems added in backwards-compatible manner
**PATCH** (0.0.z): Bug fixes ONLY — no new features

### **Priority Levels**
- **HIGH** = Critical for player experience, fixes placeholder content
- **MEDIUM** = Significant content addition, enhances existing systems
- **LOW** = Nice-to-have, long-term vision
- **VERY LOW** = Aspirational, may be cut or significantly delayed

### **Implementation Status**
- ✅ = Fully implemented and released
- 🟡 = Partially implemented, needs completion
- ❌ = Not started, in planning
- 🚫 = Scrapped, removed from roadmap

### **Community Feedback**
This roadmap is a living document updated based on player feedback, development discoveries, and community input.

Submit feedback at: https://github.com/hitman20081/DAGMod/issues

---

**Last Updated**: 2026-08-07
**Maintained By**: hitman20081
**Current Version**: v1.10.0
**License**: See LICENSE file