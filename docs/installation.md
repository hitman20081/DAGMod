# Installation

Get started with DAGMod by following this comprehensive installation guide. Whether you're playing singleplayer or setting up a server, this guide will help you install and configure the mod properly.

**Current Version:** v1.5.4-beta (December 2025)

---

## What's New in v1.5.4-beta

**Minecraft Version Update:**
- ✅ **Updated to Minecraft 1.21.11** - Final obfuscated version before mappings change
- ✅ **Updated Fabric Loader to 0.18.1** - Latest stable loader
- ✅ **Updated Fabric API to 0.139.4+1.21.11** - Compatible with 1.21.11

See previous v1.5.3-beta changes below.

## What's New in v1.5.3-beta

**Critical Bug Fixes:**
- ✅ **Quest Progression Blocker** - Players can no longer get stuck at level 20 unable to progress
- ✅ **Quest Book Upgrade Info** - Quest Block now shows which chain unlocks the next quest book tier
- ✅ **Circular Dependency** - Final chain quests no longer require the tier they unlock

**Core Features:**
- **17 new class abilities** (6 Warrior, 4 Mage, 7 Rogue)
- **Dual ability system for Rogues** (7 total abilities)
- **Bone Dungeons** with procedural generation
- **Boss system** with Skeleton King hierarchy
- **Level 1-50 progression** with stat bonuses
- **64 total quests** including 40 race-specific epic storylines
- **Custom armor sets, weapons, and shields**
- **15 consumable powders**

See [CHANGELOG.md](https://github.com/hitman20081/DAGMod/blob/main/CHANGELOG.md) for complete details.

---

## System Requirements

### Minecraft Version
- **Minecraft 1.21.11**
- DAGMod is built for this specific version
- Earlier or later versions may not be compatible

### Required Dependencies
- **Fabric Loader** 0.18.1
- **Fabric API** 0.139.4+1.21.11

### Recommended System Specs
- **RAM:** 4GB minimum, 6-8GB recommended
- **CPU:** Dual-core 2.5GHz or better
- **Storage:** 500MB free space for mod and world saves
- **Java:** Java 21 or newer (Minecraft 1.21.11 requirement)

---

## Installation Methods

Choose the installation method that best fits your needs:
- [Client Installation (Singleplayer)](#client-installation-singleplayer)
- [Server Installation (Multiplayer)](#server-installation-multiplayer)
- [Development Setup](#development-setup-for-modders)

---

## Client Installation (Singleplayer)

### Step 1: Install Fabric Loader

**Option A: Using Fabric Installer (Recommended)**

1. Download the Fabric Installer from [fabricmc.net](https://fabricmc.net/use/installer/)
2. Run the installer
3. Select **Minecraft 1.21.11**
4. Select **Fabric Loader** 0.18.1
5. Click "Install"
6. Wait for installation to complete

**Option B: Using Minecraft Launcher Profiles**

1. Open the Minecraft Launcher
2. Go to "Installations"
3. Click "New Installation"
4. Select version: **fabric-loader-0.18.1-1.21.11**
5. Name it "DAGMod v1.5.4-beta"
6. Click "Create"

### Step 2: Install Fabric API

1. Download **Fabric API 0.139.4+1.21.11** from:
   - [Modrinth](https://modrinth.com/mod/fabric-api) (recommended)
   - [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
2. Locate your Minecraft mods folder:
   - **Windows:** `%appdata%\.minecraft\mods`
   - **macOS:** `~/Library/Application Support/minecraft/mods`
   - **Linux:** `~/.minecraft/mods`
3. Create the `mods` folder if it doesn't exist
4. Place the Fabric API `.jar` file in the mods folder

### Step 3: Install DAGMod

1. Download **DAGMod v1.5.4-beta** from:
   - [GitHub Releases](https://github.com/hitman20081/DAGMod/releases)
   - [Modrinth](https://modrinth.com/mod/dag-mod)
   - [CurseForge](https://curseforge.com/minecraft/mc-mods/dag-mod) [Not available yet] 
2. Place the DAGMod `.jar` file in your `mods` folder (same location as Fabric API)
3. **Do not extract or unzip the `.jar` file** - leave it as-is

### Step 4: Launch Minecraft

1. Open the Minecraft Launcher
2. Select the **Fabric Loader 1.21.11** profile (or "DAGMod v1.5.4-beta" if you named it)
3. Click "Play"
4. Wait for Minecraft to load
5. Check the main menu - you should see "X mods loaded" in the bottom-left
6. Click "Mods" to verify DAGMod is listed

### Step 5: Create or Load a World

**For New Worlds:**
1. Click "Singleplayer" → "Create New World"
2. Configure your world settings
3. Click "Create New World"
4. The Hall of Champions will generate naturally in your world
5. You'll receive a Hall Locator item to help find it

**For Existing Worlds:**
1. Click "Singleplayer"
2. Select your world
3. Click "Play"
4. DAGMod features will work in existing worlds
5. Hall of Champions will generate in newly explored chunks
6. Use the Hall Locator item or explore to find it

---

## Server Installation (Multiplayer)

### Step 1: Download Fabric Server

1. Download the Fabric Server Launcher from [fabricmc.net](https://fabricmc.net/use/server/)
2. Create a new folder for your server
3. Place the Fabric Server `.jar` in the folder
4. Run it once to generate server files

**Or use the Fabric Installer:**
1. Run the Fabric Installer
2. Select "Server" tab
2. Choose **Minecraft 1.21.11**
3. Choose **Fabric Loader 0.18.1**
5. Select your server folder
6. Click "Install"

### Step 2: Install Fabric API

1. Download **Fabric API 0.139.4+1.21.11**
2. Place it in the `mods` folder inside your server directory
3. If the `mods` folder doesn't exist, create it

### Step 3: Install DAGMod

1. Download **DAGMod v1.5.4-beta**
2. Place it in the `mods` folder (alongside Fabric API)

### Step 4: Configure Server

1. Edit `server.properties`:
   ```properties
   # Recommended settings for DAGMod
   difficulty=normal
   gamemode=survival
   pvp=true  # Optional, enables Rogue PvP potential
   spawn-protection=16
   ```

2. Accept the EULA:
   - Open `eula.txt`
   - Change `eula=false` to `eula=true`
   - Save the file

### Step 5: Start the Server

**Windows:**
```batch
java -Xmx4G -Xms4G -jar fabric-server-launch.jar nogui
```

**Linux/macOS:**
```bash
java -Xmx4G -Xms4G -jar fabric-server-launch.jar nogui
```

**Notes:**
- `-Xmx4G` sets maximum RAM (adjust as needed - 6-8GB recommended for DAGMod)
- `-Xms4G` sets initial RAM (match Xmx for best performance)
- Remove `nogui` if you want the server GUI

### Step 6: Connect to Your Server

1. Start Minecraft with the same mods (Fabric API + DAGMod v1.5.4-beta)
2. Go to "Multiplayer"
3. Click "Add Server"
4. Enter server address (e.g., `localhost` or your server IP)
5. Click "Done"
6. Select your server and click "Join Server"

---

## Verification & Testing

### Check Installation Success

**In-Game Verification:**
1. Press F3 to open debug screen
2. Check right side for mod list
3. DAGMod v1.5.4-beta should appear in the list

**Command Verification:**
1. Type `/dagmod` in chat
2. If installed correctly, you'll see available commands
3. Try `/dagmod info` to check it works (shows race, class, level, XP)

**World Verification:**
1. Explore your world or use the Hall Locator
2. Find the Hall of Champions structure
3. Interact with Race and Class Selection Altars
4. Verify tokens are given when interacting

**Feature Verification:**
- Gain XP from mining/combat
- Check progression HUD (shows level and XP)
- Receive quest blocks or find them in the world
- Explore to find Bone Dungeons generating underground

### Common Issues & Solutions

**Issue: "Incompatible mod set" error**
- **Cause:** Version mismatch
- **Solution:** Verify all mods are for the correct Minecraft version
- Check Fabric Loader version 0.18.1
- Check Fabric API version 0.139.4+1.21.11
- Check DAGMod is v1.5.4-beta

**Issue: Mods not loading**
- **Cause:** Incorrect folder or wrong Minecraft version
- **Solution:** 
  - Verify mods are in the correct `mods` folder
  - Launch the correct Fabric profile
  - Check `.jar` files aren't corrupted (redownload if needed)

**Issue: DAGMod features not working**
- **Cause:** Missing Fabric API
- **Solution:** Install Fabric API 0.139.4+1.21.11

**Issue: Hall of Champions not generating**
- **Cause:** World generated before mod installation
- **Solution:** 
  - Explore new chunks (structure generates naturally)
  - Use `/locate structure dagmod:hall_of_champions` command
  - Use Hall Locator item if available

**Issue: Server crashes on startup**
- **Cause:** Insufficient RAM or conflicting mods
- **Solution:**
  - Allocate more RAM (6-8GB recommended for DAGMod with all features)
  - Remove other mods to test for conflicts
  - Check server logs for specific errors

**Issue: Client can't connect to server**
- **Cause:** Version mismatch between client and server
- **Solution:**
  - Ensure both client and server have DAGMod v1.5.4-beta
  - Ensure both have same Fabric API version
  - Check server is running and accessible

---

## Updating DAGMod

### Client Update

1. **Backup your worlds** (important!)
   - Navigate to `.minecraft/saves`
   - Copy your world folders to a safe location
2. **Remove old DAGMod version** from mods folder
3. **Download DAGMod v1.5.4-beta** (or newer)
4. **Place new `.jar` in mods folder**
5. **Launch Minecraft**
6. **Load your world** - progress should be preserved

### Server Update

1. **Backup everything** (critical!)
   - Copy entire server folder to backup location
   - Especially backup the `world` folder
2. **Stop the server**
3. **Remove old DAGMod `.jar`** from mods folder
4. **Add new DAGMod v1.5.4-beta `.jar`** to mods folder
5. **Start the server**
6. **Verify update:** Check console logs for version number

**Note:** Player data (race/class/level/quests) persists across updates. Existing worlds are compatible with new versions.

---

## Configuration

### Current Configuration (v1.5.4-beta)


DAGMod configuration options:

**Location:** Configuration files stored in:
- `.minecraft/config/dagmod/` (client)
- `server_folder/config/dagmod/` (server)

### Future Configuration Options

Additional configuration planned for future versions as features expand.

---

## Recommended Additional Mods

While not extensively tested, DAGMod should be compatible with these popular Fabric mods:

### Performance (Likely Compatible)
- **Sodium** - Rendering optimization
- **Lithium** - Server/client optimization
- **FerriteCore** - Memory optimization

### Quality of Life (Likely Compatible)
- **Roughly Enough Items (REI)** - Recipe viewer
- **Jade** - Block/entity information HUD
- **Mod Menu** - In-game mod configuration

### Compatibility Notes
DAGMod is designed as a standalone mod and should work alongside most Fabric mods. However, extensive compatibility testing has not been completed.

**Potential Conflicts:**
- Mods that modify the same player attributes (health, speed, damage)
- Other race/class mods (e.g., Origins) may cause conflicts
- Mods that alter quest systems or progression
- Mods that modify XP systems

**Important:** Always test mod combinations in a backup world first. If you discover compatibility issues, please report them on [GitHub Issues](https://github.com/hitman20081/DAGMod/issues).

---

## Development Setup (For Modders)

Want to contribute or modify DAGMod? Here's how to set up a development environment.

### Prerequisites

- **Java Development Kit (JDK) 21+**
- **IntelliJ IDEA** or **Eclipse** with Fabric plugin
- **Git** for version control
- **Gradle** (included with project)

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hitman20081/DAGMod.git
   cd DAGMod
   ```

2. **Import into IDE:**
   - Open IntelliJ IDEA
   - File → Open
   - Select the DAGMod folder
   - Wait for Gradle sync to complete

3. **Generate Minecraft sources:**
   ```bash
   ./gradlew genSources
   ```

4. **Run in development:**
   ```bash
   ./gradlew runClient
   ```

### Development Configuration

**gradle.properties:**
```properties
minecraft_version=1.21.11
yarn_mappings=1.21.11+build.1
loader_version=0.18.1
fabric_version=0.139.4+1.21.11
```

**Project Structure:**
```
DAGMod/
├── src/main/java/com/github/hitman20081/dagmod/
│   ├── block/          - Custom blocks (Quest Block, Altars)
│   ├── class_system/   - RPG class mechanics and abilities
│   ├── race_system/    - Race selection and racial bonuses
│   ├── progression/    - Level system, XP, stat scaling
│   ├── data/           - Persistent data management
│   ├── command/        - Custom commands
│   ├── effect/         - Custom status effects
│   ├── entity/         - Custom entities (bosses)
│   ├── event/          - Event handlers
│   ├── gui/            - User interface screens
│   ├── item/           - Custom items (ability items, consumables)
│   ├── mixin/          - Mixins for modifying game behavior
│   ├── potion/         - Custom potions
│   └── quest/          - Quest system implementation
├── src/main/resources/
│   ├── fabric.mod.json
│   └── assets/
└── data/dag011/        - Datapack integration
    ├── structures/     - World-generated structures (Bone Dungeon)
    ├── worldgen/       - World generation configs
    ├── loot_table/     - Custom loot tables
    └── functions/      - Datapack functions
```

### Building from Source

```bash
./gradlew build
```

Built `.jar` file will be in `build/libs/`

---

## Troubleshooting

### Getting Help

If you encounter issues not covered in this guide:

1. **Check GitHub Issues:** [DAGMod Issues](https://github.com/hitman20081/DAGMod/issues)
2. **Search for similar problems** - someone may have solved it
3. **Create a new issue** with:
   - Minecraft version
   - DAGMod version (v1.5.4-beta)
   - Fabric Loader version
   - Fabric API version
   - Full error log (from `.minecraft/logs/latest.log`)
   - Steps to reproduce the problem

### Debug Mode

Enable debug logging for detailed information:

1. Navigate to `.minecraft/config/`
2. Find logging configuration (varies by launcher)
3. Set DAGMod logging to DEBUG level
4. Reproduce the issue
5. Check logs for detailed error messages

---

## Uninstallation

### Removing DAGMod

**Client:**
1. Navigate to `.minecraft/mods`
2. Delete `DAGMod-1.5.4-beta.jar`
3. Launch Minecraft

**Server:**
1. Stop the server
2. Navigate to server `mods` folder
3. Delete `DAGMod-1.5.4-beta.jar`
4. Restart server

**Note:** Player data (races/classes/levels/quests) will remain in save files but won't be accessible without the mod.

---

## FAQ

**Q: Can I install DAGMod on existing worlds?**
A: Yes! DAGMod works on existing worlds. The Hall of Champions will generate in newly explored chunks.

**Q: Do I need to install DAGMod on both client and server?**
A: Yes, for multiplayer. Both the server and all connecting clients must have the same version of DAGMod installed.

**Q: Can I play on servers without DAGMod?**
A: No, if you have DAGMod installed. Most servers won't allow clients with mods they don't have.

**Q: Will updates delete my progress?**
A: No. Quest progress, race/class selections, level/XP, and player data persist across updates.

**Q: How much RAM should I allocate?**
A: Minimum 4GB, recommended 6-8GB for smooth performance with all DAGMod features (dungeons, bosses, abilities, etc.).

**Q: What's new in v1.5.4-beta?**
A: Minecraft version update! Updated to Minecraft 1.21.11 (final obfuscated version before mappings change), Fabric Loader 0.18.1, and Fabric API 0.139.4+1.21.11. See v1.5.3-beta for previous critical bug fixes (quest progression blocker resolved).

---

**Installation complete!** Ready to begin your adventure?

**Next Steps:**
- Read [Getting Started](<./GETTING_STARTED.md>) for your first steps
- Review [Races](link) and [Classes](link) to plan your character
- Check [Quests](link) to see the 64 quests including epic race storylines
- Learn about the [17 new abilities](link) in Classes guide
- Explore [Progression](link) for the level 1-50 system

---

*Choose your race, master your class abilities, explore dungeons, face legendary bosses, and forge your legend in DAGMod!*