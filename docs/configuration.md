# Configuration Options

DAGMod does not have a general configuration file system. Most values are hard-coded in the source, with the exception of the Seasons system which has full in-game configuration.

---

## Seasons Configuration

The Seasons system is the only subsystem with live, persistent configuration. All settings are changed via the `/seasons` command and persist across server restarts.

### Setup

On first load, all players receive a notice that seasons are not yet configured. A server operator must run `/seasons` to open the interactive setup menu.

### Options

| Setting | Default | Options | Command |
|---------|---------|---------|---------|
| Season Length | 20 days | 7, 14, 20, 28 Minecraft days | `/seasons length <value>` |
| Weather Effects | On | On / Off | `/seasons weather <on\|off>` |
| Crop Growth | On | On / Off | `/seasons growth <on\|off>` |
| Temperature Effects | On | On / Off | `/seasons temperature <on\|off>` |
| Season Display | On | On / Off | `/seasons display <on\|off>` |

### Persistence

Settings are stored in scoreboard objectives (`seasons_config`) and survive server restarts. The `#seasons_initialized` score tracks whether setup has been completed — when set to 1, the season cycle starts automatically on server load.

---

## Other Server Administration

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
