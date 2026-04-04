# Seasons

DAGMod includes a four-season cycle system that changes the world based on the time of year.

---

## Overview

The seasons cycle through **Spring → Summer → Autumn → Winter** and back. Each season lasts a configurable number of Minecraft days and affects:

- **Weather** — Rainfall and storm frequency change per season
- **Crop Growth** — Growth rates increase in Summer and decrease in Winter
- **Temperature** — Status effects are applied based on the season (e.g. cold slowness in Winter)
- **Display** — The current season and day are shown in chat

---

## Setup (Server Operators)

Seasons must be manually configured before they start. On first load, all players see:

> **[Seasons]** The seasons system is installed but not yet configured.
> Server operators: type `/seasons` to open the setup menu.

To configure:

1. Run `/seasons` to open the interactive menu
2. Set the season length and toggle any effects you want
3. Click **Start Seasons** — the cycle begins immediately and persists across restarts

---

## Commands

| Command | Description |
|---------|-------------|
| `/seasons` | Open the interactive configuration menu |
| `/seasons setup` | Show current configuration status |
| `/seasons start` | Start the seasons cycle |
| `/seasons reset` | Reset to Spring Day 1 |
| `/seasons length <7\|14\|20\|28>` | Set the number of days per season |
| `/seasons weather <on\|off>` | Toggle weather effects |
| `/seasons growth <on\|off>` | Toggle crop growth changes |
| `/seasons temperature <on\|off>` | Toggle temperature status effects |
| `/seasons display <on\|off>` | Toggle the season display in chat |

All commands require operator permission (level 2+).

---

## Configuration Options

| Setting | Default | Description |
|---------|---------|-------------|
| Season Length | 20 days | How many Minecraft days each season lasts |
| Weather Effects | On | Enables season-specific weather patterns |
| Crop Growth | On | Boosts growth in Summer, reduces in Winter |
| Temperature | On | Applies status effects based on season |
| Season Display | On | Shows current season and day in chat |

---

## Season Effects

| Season | Weather | Crop Growth | Temperature Effect |
|--------|---------|-------------|-------------------|
| Spring | Light rain | Normal | None |
| Summer | Clear skies | Boosted | None |
| Autumn | Occasional rain | Slightly reduced | None |
| Winter | Snow / storms | Reduced | Slowness / weakness |

---

## Persistence

All settings are stored in scoreboard objectives (`seasons_config`) and survive server restarts. Once the system is started (`#seasons_initialized = 1`), the season cycle resumes automatically every time the server loads — no operator action required after initial setup.

---

## Navigation

* [Home](Home)
* [Commands](commands)
* [Configuration](configuration)
