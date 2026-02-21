# Custom Blocks

This document details the custom blocks introduced in DAGMod, including their functionality and usage.

---

## Quest Block

The Quest Block is the primary interface for accepting, tracking, and completing quests.

**Tutorial Gating:** Before using the Quest Block, players must:
1. Meet Innkeeper Garrick (the tutorial NPC)
2. Complete Garrick's 3 tutorial tasks to receive 3 Quest Notes
3. Combine the Quest Notes at the Quest Block to receive the Novice Quest Book

**Menu System:** Right-clicking the Quest Block opens a multi-step chat menu:

| Menu | Description |
|------|-------------|
| Main Menu | Shows quest progress, active/completed quest counts, and quest book tier |
| Browse Quests | Displays available quests one at a time with name, difficulty, description, objectives, and rewards |
| Confirm Accept | Confirmation step before accepting a quest |
| Active Quests | Shows currently accepted quests with progress |
| Turn In Quests | Allows turning in completed quests for rewards |
| Upgrade Menu | Upgrades quest book tier when a quest chain unlock is available |

**Quest Categories:** The Quest Block shows MAIN, SIDE, and CLASS quests only. For JOB and DAILY quests, use the Job Board.

**Quest Book Tiers:** Completing quest chains unlocks higher-tier quest books, granting access to harder quests with better rewards.

---

## Race Selection Altar

The Race Selection Altar is a one-time-use block where players choose their race.

**How It Works:**
1. Right-click the altar with an empty hand
2. Receive a Race Selection Tome and 4 race tokens (Human, Dwarf, Elf, Orc)
3. Right-click while holding a token to lock in that race permanently
4. Unused tokens are removed from inventory upon selection

**Race-Specific Starter Gear:**

| Race | Starter Items | Racial Bonus |
|------|--------------|--------------|
| Human | Iron Pickaxe, Iron Axe, Fishing Rod, 8 Bread | +25% XP from all sources |
| Dwarf | 2x Iron Pickaxe, 32 Torches, 8 Cooked Beef | +20% mining speed, ore bonuses |
| Elf | 2x Iron Axe, Iron Bow, 16 Arrows, 8 Apples | Enhanced woodcutting, +15% speed, +0.5 reach |
| Orc | Iron Sword, Iron Bow, Fishing Rod, 16 Arrows, 8 Cooked Porkchops | +15% melee damage, hunting bonuses |

**Resetting Your Race:**
- **Race Reset Crystal** or **Potion of Racial Rebirth** -- resets race only
- **Character Reset Crystal** or **Total Rebirth Potion** -- resets both race and class

---

## Class Selection Altar

The Class Selection Altar lets players choose their combat class.

**How It Works:**
1. Right-click the altar with an empty hand
2. Receive a Class Selection Tome and 3 class tokens (Warrior, Mage, Rogue)
3. Right-click while holding a token to select that class
4. Unused tokens are removed from inventory

**Class-Specific Starter Gear:**

| Class | Starter Items |
|-------|--------------|
| Warrior | Iron Sword, Shield, Full Chainmail Armor, 32 Cooked Beef, 3x Healing Potions |
| Mage | Apprentice Wand, Iron Sword, Full Leather Armor, 2x Healing Potions, 1x Regeneration Potion, Protection I Book, Power I Book, 16 Lapis, 32 Bread |
| Rogue | Iron Sword, Bow, 32 Arrows, Full Leather Armor, 32 Cooked Chicken |

**Resetting Your Class:**
- **Class Reset Crystal** -- resets class selection
- **Free reset** -- available every 10 quests completed (sneak + right-click the altar to confirm)
- **Character Reset Crystal** or **Total Rebirth Potion** -- resets both race and class

---

## Job Board

The Job Board is a wall-mounted block that provides access to JOB and DAILY category quests.

**How It Works:**
- Same tutorial gating as the Quest Block (must have met Garrick and obtained a Quest Book)
- Right-click to open the job menu
- Browse available jobs and daily quests
- Accept jobs and turn them in for payment
- Sneak + right-click to skip through available jobs

**Menu System:**

| Menu | Description |
|------|-------------|
| Main Menu | Shows active jobs, completed jobs count, and available jobs |
| Browse Jobs | Displays individual jobs with [DAILY JOB] tags, objectives, and payment |
| Confirm Accept | Confirms job acceptance |
| Active Jobs | Shows ongoing jobs with progress |
| Turn In Jobs | Collects payment for completed jobs |

**Note:** The Job Board uses "Payment" instead of "Rewards" in its interface. Jobs are repeatable tasks designed for steady income and XP gain.

---

## Hall Respawn Block

The Hall Respawn Block sets the player's vanilla spawn point at the Hall of Champions. It works the same way as beds and respawn anchors — when you die without another spawn point set, you respawn near this block.

**How It Works:**
1. Right-click the block with an empty hand
2. Your spawn point is set to a random safe position 5-10 blocks from the block
3. Confirmation message: "Respawn point set at the Hall of Champions!" (gold text)
4. Level-up sound plays and totem particles appear

**Behavior:**
- Uses Minecraft's built-in spawn point system (`setSpawnPoint()`)
- Sleeping in a bed will override the Hall spawn point (normal vanilla behavior)
- If your bed is destroyed, you'll need to revisit the Hall and click the block again
- Multiple players can use the same block — each gets a unique random spawn offset

**Block Properties:**

| Property | Value |
|----------|-------|
| Strength | 50.0 (nearly indestructible) |
| Blast Resistance | 1200 |
| Luminance | 12 (glows) |
| Tool Required | Yes |

---

## Block Locations

All custom blocks are found in the **Hall of Champions**, a structure that serves as the central hub for character creation and quest management.

| Block | Location |
|-------|----------|
| Race Selection Altar | Hall of Champions - Race wing |
| Class Selection Altar | Hall of Champions - Class wing |
| Quest Block | Hall of Champions - Main hall |
| Job Board | Hall of Champions - Wall-mounted near quest area |
| Hall Respawn Block | Hall of Champions - Central area |
