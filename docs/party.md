# Party System

DAGMod's party system allows up to 5 players to team up for shared XP, party chat, and cooperative quests. Parties provide XP bonuses that scale with group size and enable group-oriented content.

**Current Version:** v1.7.1

---

## Party Basics

| Feature | Details |
|---------|---------|
| Max party size | 5 players |
| Leadership | Creator becomes leader; transfers on leader departure |
| XP sharing | Enabled by default; 50-block radius |
| Party chat | Private channel via `/party chat` or `/pc` |
| Persistence | Memory-only; disbands when all members disconnect |

---

## Commands

| Command | Description | Who Can Use |
|---------|-------------|-------------|
| `/party create` | Create a new party (you become leader) | Any player not in a party |
| `/party invite <player>` | Invite a player to your party | Party leader only |
| `/party accept` | Accept a pending party invitation | Invited player |
| `/party leave` | Leave your current party | Any party member |
| `/party kick <player>` | Remove a player from the party | Party leader only |
| `/party disband` | Disband the entire party | Party leader only |
| `/party list` | Show party members, status, and XP bonus | Any party member |
| `/party chat <message>` | Send a message to party members only | Any party member |
| `/pc <message>` | Shortcut for party chat | Any party member |
| `/party togglequests` | Toggle quest sharing on/off | Any party member |

### Command Details

**Creating a Party:**
- You must not already be in a party
- You automatically become the party leader

**Inviting Players:**
- Only the leader can invite
- Target player must not already be in a party
- Target player receives an invite notification with accept instructions
- Multiple invites can be pending

**Leaving a Party:**
- If the leader leaves, leadership transfers to the next member
- If the leader is the only member, the party disbands

**Party List Output:**
```
=== Party Members (3/5) ===
XP Bonus: +10%
* PlayerName - Online     (leader shown with star)
  PlayerName - Online
  PlayerName - Online
```

---

## XP Sharing

### Party XP Bonuses

Parties receive XP bonuses that scale with the number of members:

| Party Size | XP Bonus |
|------------|----------|
| 1 player | +0% (no bonus) |
| 2 players | +5% |
| 3 players | +10% |
| 4 players | +15% |
| 5 players | +20% |

### How XP Sharing Works

1. **Earner gets full XP + party bonus** - When you earn XP, you receive the base amount plus the party bonus percentage
2. **Nearby members get 50% shared XP** - Party members within 50 blocks receive half the base XP, plus their own party bonus applied to the shared amount
3. **Must be in the same dimension** - XP sharing only works when members are in the same world
4. **XP bonus shown on action bar** - You'll see `+X XP (Party Bonus: +Y)` when earning XP with a party bonus

**Example (3-player party, +10% bonus):**
- Player A earns 100 base XP from mining
- Player A receives: 100 + 10 = **110 XP**
- Player B (within 50 blocks) receives: 50 + 5 = **55 Shared XP**
- Player C (within 50 blocks) receives: 50 + 5 = **55 Shared XP**

### XP Share Radius

Members must be within **50 blocks** of the XP earner to receive shared XP. Members beyond this radius or in different dimensions do not receive shared XP but still contribute to the party bonus percentage.

---

## Party Quests

Party quests are cooperative objectives designed for groups. They offer scaled difficulty and enhanced rewards.

### Difficulty Tiers

| Difficulty | Reward Multiplier | Description |
|------------|-------------------|-------------|
| Easy | 1.0x | Simple group objectives |
| Normal | 1.5x | Standard group challenges |
| Hard | 2.0x | Challenging group content |
| Expert | 2.5x | High-difficulty encounters |
| Legendary | 3.0x | Ultimate group challenges |

### Objective Types

Party quests can include these objective types:

- **Kill Entity** - Defeat specific mobs as a group
- **Kill Boss** - Take down a boss entity together
- **Collect Item** - Gather specific items cooperatively
- **Mine Block** - Mine specific blocks together
- **Reach Location** - Travel to a destination as a party
- **Survive Waves** - Survive waves of enemies together
- **Defeat All** - Clear all enemies in an area

### Managing Party Quests

- Use `/party togglequests` to enable or disable quest sharing
- Quest progress is tracked for the entire party
- All party members receive rewards on completion

---

## Party HUD

When in a party, the HUD displays party information:

- **Member names** with online/offline status
- **Party size** (e.g., 3/5)
- **Current XP bonus** percentage
- **Leader indicator** (star symbol)

---

## Tips & Strategies

**For Efficient Leveling:**
- Form a full 5-player party for the maximum +20% XP bonus
- Stay within 50 blocks of each other to benefit from shared XP
- The Human race's +25% XP bonus stacks with party bonuses

**For Boss Fights:**
- Coordinate class roles (Warrior tank, Mage support, Rogue DPS)
- Use `/pc` for quick tactical communication
- Accept party boss quests for enhanced rewards

**For General Play:**
- Party chat (`/pc`) keeps tactical discussion private
- Leadership auto-transfers if the leader disconnects
- Parties disband when all members go offline

---

## Related Guides

- [Synergies](./synergies.md) - Team composition recommendations
- [Classes](./classes-overview.md) - Class roles for party play
- [Quests](./quests.md) - Individual quest system
- [Bosses & Dungeons](./bosses_dungeons.md) - Group boss encounters
