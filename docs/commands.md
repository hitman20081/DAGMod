# Commands

A complete reference for all DAGMod commands.

---

## Player Commands

These commands are available to all players.

### General

| Command | Description |
|---------|-------------|
| `/dagmod info` | Show your race, class, and synergy |
| `/playerdata info` | Show your race, class, level, XP, and tutorial progress |

### Quests

| Command | Description |
|---------|-------------|
| `/quest list` | List your active quests with objectives and progress |
| `/quest skip` | Skip to the next quest when browsing at a Quest Block |
| `/quest abandon <questId>` | Abandon a specific active quest |
| `/quest abandonall` | Abandon all active quests |

### Party

| Command | Description |
|---------|-------------|
| `/party create` | Create a new party (you become leader) |
| `/party invite <player>` | Invite a player to your party |
| `/party accept` | Accept a pending party invitation |
| `/party leave` | Leave your current party |
| `/party kick <player>` | Remove a player from the party (leader only) |
| `/party disband` | Disband the entire party (leader only) |
| `/party list` | Show party members, status, and XP bonus |
| `/party chat <message>` | Send a message to party members only |
| `/pc <message>` | Shortcut for party chat |
| `/party togglequests` | Toggle quest sharing on/off |

### Party Quests

| Command | Description |
|---------|-------------|
| `/partyquest start` | Start a party quest |
| `/partyquest progress` | Check current party quest progress |
| `/partyquest abandon` | Abandon the active party quest |
| `/partyquest list` | List available party quests |
| `/pq <subcommand>` | Shortcut — same subcommands as `/partyquest` |

### Class Resources

| Command | Description |
|---------|-------------|
| `/resource mana` | View your current mana (Mage only) |
| `/resource energy` | View your current energy (Rogue only) |
| `/cooldown info` | Show active ability cooldowns with time remaining |

### Synergies

| Command | Description |
|---------|-------------|
| `/synergy list` | List all 9 race+class synergy combinations |
| `/synergy check` | Show your current synergy bonus (if any) |

### Graves

| Command | Description |
|---------|-------------|
| `/grave status` | Show your grave location, dimension, item count, time since death, and loot protection remaining |

### Travel

| Command | Description |
|---------|-------------|
| `/travel <destination>` | Travel to a destination via the ship system |

---

## Admin / Debug Commands

These commands are intended for server operators and testing.

### Progression

| Command | Description |
|---------|-------------|
| `/testprogression <amount>` | Add XP |
| `/testprogression setlevel <level>` | Set level (1–200) |
| `/testprogression reset` | Reset all progression to level 1 |
| `/testprogression info` | Show detailed progression stats |
| `/testprogression curve` | Display the XP curve for every 5 levels |

### Class & Race

| Command | Description |
|---------|-------------|
| `/resetclass` | Reset your class selection |
| `/playerdata info <player>` | View another player's race, class, level, XP, and tutorial progress |

### Resources & Cooldowns

| Command | Description |
|---------|-------------|
| `/resource mana set <amount>` | Set your mana (0–100, Mage only) |
| `/resource energy set <amount>` | Set your energy (0–100, Rogue only) |
| `/cooldown clear` | Clear all your active ability cooldowns |

### Dragon System

| Command | Description |
|---------|-------------|
| `/locatedragon` | Find Dragon Guardian bosses within 500 blocks |
| `/locatewilddragon` | Find wild dragons within 500 blocks |
| `/dragonrespawn status` | Show active dragon respawn timers and time remaining |
| `/dragonrespawn reset` | Cancel and restart all dragon respawn timers |
| `/dragonrespawn cancel` | Cancel all active dragon respawn timers |

### Merchants

| Command | Description |
|---------|-------------|
| `/merchant status` | Show time until next trade rotation and current rotation index per merchant |
| `/merchant rotate` | Force an immediate trade rotation for all merchants |

### NPCs

| Command | Description |
|---------|-------------|
| `/summon_garrick` | Summon Innkeeper Garrick at your location |
