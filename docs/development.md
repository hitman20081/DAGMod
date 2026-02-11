# Development Setup

This guide provides instructions for modders who wish to contribute to or modify DAGMod.

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java Development Kit (JDK) | 21 |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.141.1+1.21.11 |

---

## Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hitman20081/DAGMod.git
   cd DAGMod
   ```

2. **Import into your IDE:**
   - **IntelliJ IDEA** (recommended): Open as Gradle project. IntelliJ will auto-detect the Fabric setup.
   - **Eclipse**: Run `./gradlew eclipse` then import as existing project.
   - **VS Code**: Install the Java Extension Pack, then open the project folder.

3. **Generate Minecraft sources:**
   ```bash
   ./gradlew genSources
   ```

4. **Verify the setup:**
   ```bash
   ./gradlew build
   ```

---

## Building from Source

| Command | Description |
|---------|-------------|
| `./gradlew build` | Build the mod (output: `build/libs/`) |
| `./gradlew runClient` | Launch Minecraft client for testing |
| `./gradlew runServer` | Launch dedicated server for testing |
| `./gradlew runDatagen` | Generate loot tables, recipes, etc. |
| `./gradlew clean` | Clean build artifacts |

The built JAR file is located at `build/libs/dagmod-<version>.jar`.

---

## Project Structure

```
com.github.hitman20081.dagmod/
├── DagMod.java              # Server-side entry point (ModInitializer)
├── DagModClient.java        # Client-side entry point
├── block/                   # Custom blocks (Quest, Altars, Job Boards)
├── bone_realm/              # Bone dimension: entities, portal, chests
├── dragon_realm/            # Dragon dimension: boss, portal
├── class_system/            # Class abilities and resources
│   ├── mage/               # Mage abilities
│   ├── mana/               # Mana resource system
│   ├── rogue/              # Rogue abilities + Energy system
│   └── warrior/            # Warrior abilities + Cooldown system
├── race_system/             # Race abilities and synergies
├── progression/             # XP/Level system (50 levels)
├── quest/                   # Quest system
│   ├── objectives/         # CollectObjective, KillObjective, etc.
│   └── registry/           # QuestRegistry.java
├── party/                   # Multiplayer party system
├── entity/                  # Custom entities (NPCs, dragons)
├── item/                    # Custom items (ModItems.java)
├── enchantment/             # Custom enchantment effects
├── mixin/                   # Vanilla behavior modifications
└── networking/              # Client-server packets
```

---

## Development Configuration

### Mixin Development

- All mixins are registered in `src/main/resources/dagmod.mixins.json`
- Server mixins run on both dedicated servers and singleplayer
- Client mixins only load on the client side
- Mixins **cannot** have non-private static methods -- use a separate helper class
- All mixins use `JAVA_21` compatibility level

### Data Persistence

- Always call `savePlayerData()` after modifying player data
- Never access progression data before it's loaded (returns null)
- Use `QuestManager.getInstance().savePlayerQuestData()` for quest changes

### Client-Server Model

- Server is authoritative for all game logic
- Client receives state via custom packets (`*Networking` classes)
- HUD rendering reads from `Client*Data` classes

### Adding New Content

**New Quest:** Define in `QuestRegistry.registerQuests()`, add objectives, set rewards and tier.

**New Ability:** Create class in `class_system/{class}/`, register item in `ModItems`, implement `use()` with prereq checks.

**New Boss:** Extend appropriate base, register in entity registry, add renderer, create loot table in `data/dagmod/loot_table/boss/`.

**New Item:** Add to `ModItems`, create texture in `assets/dagmod/textures/item/`, add model JSON.

---

## Test Commands

| Command | Description |
|---------|-------------|
| `/testprogression 500` | Add 500 XP |
| `/testprogression setlevel 25` | Set level to 25 |
| `/testprogression reset` | Reset progression |
| `/testprogression info` | Show stats |
| `/quest list` | List available quests |
| `/resetclass` | Reset class selection |
| `/info` | Show player stats |
| `/party create <name>` | Create a party |
