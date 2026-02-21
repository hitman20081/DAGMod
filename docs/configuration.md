# Configuration Options

DAGMod does not currently have a configuration file system. All values are hard-coded in the source.

---

## Current Behavior

All game values (XP rates, ability cooldowns, stat bonuses, merchant rotation timers, etc.) are set directly in the Java source code. There are no configuration files for players or server administrators to modify.

---

## Server Administration

Server administrators can use the following commands to adjust player state:

| Command | Description |
|---------|-------------|
| `/testprogression <amount>` | Add XP to a player |
| `/testprogression setlevel <level>` | Set a player's level |
| `/testprogression reset` | Reset a player's progression |
| `/resetclass` | Reset a player's class selection |
| `/quest list` | List available quests |
| `/party create <name>` | Create a party |
| `/info` | Show player stats |

---

## Data Storage

Player data is stored server-side in NBT files under `world/data/dagmod/`:

| Data Type | Location |
|-----------|----------|
| Quest progress | `world/data/dagmod/quests/{uuid}.dat` |
| Progression (XP/level) | `world/data/dagmod/progression/{uuid}.dat` |
| Player data (race/class) | `world/data/dagmod/players/{uuid}.dat` |

Mana, energy, and party data are stored in server memory only and reset on server restart.

---

## Planned

A configuration system is planned for a future release to allow customization of:
- XP rates and level scaling
- Ability cooldowns and resource costs
- Merchant rotation timers
- Boss stats and loot tables
- Quest rewards and requirements
