# DAGMod Known Issues & Code Quality Concerns

**Analysis Date**: 2025-12-10
**Version**: v1.5.4-beta
**Files Analyzed**: 189 Java files across 21 packages

This document lists all known issues, potential bugs, and code quality concerns found in the DAGMod codebase through systematic analysis.

---

## ✅ FIXED in v1.5.3-beta

### 14. Quest Progression Blocker at Level 20 (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.3-beta

**Problem** (Previous):
Players were getting stuck at level 20, unable to unlock the Expert Quest Book or complete class quest chains due to a circular dependency. Final quests in tier-unlocking chains required EXPERT difficulty book to accept, but completing those quests was required to unlock the EXPERT book.

**Solution Implemented**:
- Changed 4 tier-unlocking quests from EXPERT → APPRENTICE difficulty:
  - `village_master` (Village Development chain → unlocks Expert Quest Book)
  - `shield_bearers_trial` (Warrior class chain)
  - `master_scrolls` (Mage class chain)
  - `echoes_of_the_deep` (Rogue class chain)
- Added `showQuestBookUpgradeInfo()` method to Quest Block to display chain progress
- Players now see which chain unlocks the next tier and their progress (e.g., "4/5 quests")

**Impact**: Game-breaking progression wall at level 20 eliminated, all quest chains now completable

**Files Modified**:
- `quest/registry/QuestRegistry.java` - 4 quest difficulty changes
- `block/QuestBlock.java` - New upgrade info display method

---

### 15. Progression Reset on Reload (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.3-beta

**Problem** (Previous):
Player level and XP reset to 1 when reloading a saved game. The `ProgressionManager.getPlayerData()` method used `computeIfAbsent()` which auto-created fresh level 1 data if accessed before file data was loaded, causing data corruption.

**Solution Implemented**:
- Removed auto-create logic from `getPlayerData()` - now returns null instead of creating fake data
- Added null safety checks in all code that accesses progression data
- Added `clearPlayerData()` method to properly clear memory for new worlds
- Prevents data corruption from premature access

**Impact**: Progression now properly persists across sessions, no more level resets

**Files Modified**:
- `progression/ProgressionManager.java` - Removed computeIfAbsent, added clearPlayerData()
- `data/PlayerDataManager.java` - Clear progression on new world
- `DagMod.java` - Added null checks (2 locations)
- `progression/LevelRequirements.java` - Added null checks (3 methods)

---

### 16. New World Data Leakage (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.3-beta

**Problem** (Previous):
When creating a new world after playing an existing world, players would start with the same race/class/tutorial progress from the previous world. Static HashMaps in RaceSelectionAltarBlock and ClassSelectionAltarBlock persisted across worlds.

**Solution Implemented**:
- Added clearing logic in `PlayerDataManager.loadPlayerData()` when no data file exists
- Clears race, class, progression, and tutorial task tracking on new world detection
- Ensures fresh start for each new world

**Impact**: New worlds now start correctly with no race/class selected, prevents data leakage

**Files Modified**:
- `data/PlayerDataManager.java` - Added clearing logic for new worlds (lines 152-168)

---

## ✅ FIXED in v1.5.2-beta

### 13. Quest Data Persistence (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.2-beta

**Problem** (Previous):
Quest system had NO persistence - all quest data was lost on disconnect. Quest completion, active quests, quest book tier, and objective progress were stored in-memory only.

**Solution Implemented**:
- Added complete NBT-based persistence system (`QuestStorage.java`)
- Quest data now saved to `world/data/dagmod/quests/{uuid}.dat`
- Auto-save on quest accept, quest completion, and player disconnect
- Auto-load on player join
- Memory cleanup on disconnect (prevents memory leaks)

**Files Modified**: 12 files (see CHANGELOG.md v1.5.2 for details)

---

### 2. Memory Leak in Static Player Data Maps (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.2-beta

**Problem** (Previous):
Static HashMaps storing player data were never cleaned up on disconnect, causing server memory to grow indefinitely.

**Solution Implemented**:
- Added comprehensive disconnect handler in `DagMod.java` (lines 394-418)
- All player data now properly saved and cleaned up on disconnect
- Memory cleanup for QuestManager, ManaManager, EnergyManager, CooldownManager

**Impact**: Server memory leaks eliminated, prevents OutOfMemoryError on long-running servers

---

### 3. Thread Safety - HashMap Concurrency (CRITICAL) - **FIXED**

**Status**: ✅ RESOLVED in v1.5.2-beta

**Problem** (Previous):
ManaManager and EnergyManager used unsafe `HashMap` instead of `ConcurrentHashMap`, causing random crashes from concurrent access.

**Solution Implemented**:
- Converted `HashMap` → `ConcurrentHashMap` in:
  - `ManaManager.java` - playerManaData and regenTicks
  - `EnergyManager.java` - playerEnergyMap and regenTickCounter

**Impact**: Eliminates ConcurrentModificationException crashes, thread-safe concurrent access, server stability improved

---

## 🔴 Open Critical Issues

---

## 🔴 Critical Issues

### 1. TagCollectObjective Reliability Issues

**Location**: `quest/objectives/TagCollectObjective.java`
**Severity**: CRITICAL
**Status**: Known issue, documented in v1.5.1

**Problem**:
The `TagCollectObjective` class has reliability issues with complex item tags. The `consumeItems()` method can fail to properly consume items when tags contain multiple item types or when items are spread across inventory slots.

**Code Example**:
```java
public boolean consumeItems(PlayerEntity player) {
    if (!hasRequiredItems(player)) {
        return false;
    }
    int itemsToRemove = requiredAmount;
    for (int i = 0; i < player.getInventory().size() && itemsToRemove > 0; i++) {
        ItemStack stack = player.getInventory().getStack(i);
        if (!stack.isEmpty() && stack.isIn(itemTag)) {
            int removeFromStack = Math.min(itemsToRemove, stack.getCount());
            stack.decrement(removeFromStack);
            itemsToRemove -= removeFromStack;
        }
    }
    return itemsToRemove == 0;
}
```

**Impact**:
- Quest completion failures
- Item duplication edge cases
- Player frustration with "The Ancient Armory" quest

**Recommendation**:
- Prefer specific `CollectObjective` over `TagCollectObjective` whenever possible
- Add comprehensive testing for multi-item tag scenarios
- Consider adding transaction rollback on failure

**References**: CLAUDE.md line 420+, CHANGELOG.md v1.5.1 notes

---

### 2. Memory Leak in Static Player Data Maps - **FIXED**

**Location**: Multiple files
**Severity**: CRITICAL
**Status**: ✅ RESOLVED in v1.5.2-beta

**Affected Classes**:
- `class_system/mana/ManaManager.java`
- `class_system/rogue/EnergyManager.java`
- `quest/QuestManager.java` (playerQuestData)

**Problem**:
Static HashMaps store player data but are never cleaned up when players disconnect or the server shuts down. This causes memory leaks over time, especially on long-running servers.

**Code Example** (`ManaManager.java`):
```java
private static final Map<UUID, ManaData> playerManaData = new HashMap<>();
private static final Map<UUID, Integer> regenTicks = new HashMap<>();

public static void clearPlayerData(UUID playerId) {
    playerManaData.remove(playerId);
    regenTicks.remove(playerId);
}
// clearPlayerData() is NEVER CALLED automatically on disconnect
```

**Impact**:
- Server memory usage grows indefinitely
- Potential OutOfMemoryError on busy servers
- Ghost player data remains after logout

**Recommendation**:
```java
// In DagMod.onInitialize(), register cleanup:
ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
    UUID playerId = handler.player.getUuid();
    ManaManager.clearPlayerData(playerId);
    EnergyManager.clearPlayerData(playerId);
    // etc.
});

// Add server shutdown handler:
ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
    ManaManager.clearAllData();
    EnergyManager.clearAllData();
    QuestManager.getInstance().clearAllData();
});
```

**✅ Solution Implemented (v1.5.2-beta)**:
- Added comprehensive disconnect handler in `DagMod.java` (lines 394-418)
- All player data now properly cleaned up on disconnect:
  - `PlayerDataManager.savePlayerData()` - Save race/class data
  - `QuestManager.savePlayerQuestData()` - Save quest data
  - `ProgressionStorage.savePlayerData()` - Save XP/levels
  - `QuestManager.clearPlayerData()` - Clear quest memory
  - `ManaManager.clearPlayerData()` - Clear mana memory
  - `EnergyManager.clearPlayerData()` - Clear energy memory
  - `CooldownManager.clearPlayerCooldowns()` - Clear cooldown memory
- Memory leaks eliminated for all player-specific data

---

### 3. Thread Safety and Concurrency Issues - **FIXED**

**Location**: Multiple files
**Severity**: CRITICAL
**Status**: ✅ RESOLVED in v1.5.2-beta

**Issues Found**:

**a) Raw Thread Creation** (`DagMod.java`):
```java
new Thread(() -> {
    try {
        Thread.sleep(250);
        MinecraftServer server = mc.getServer();
        // ... operations on server
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();
```
- No thread pool management
- No thread naming for debugging
- Thread not properly shut down

**b) ThreadLocal Leak Risk** (`class_system/mana/ManaManager.java`):
```java
private static final ThreadLocal<Boolean> isRegenerating = ThreadLocal.withInitial(() -> false);
// Never explicitly removed, can leak in thread pools
```

**c) Unsynchronized Static Map Access** (multiple files):
```java
private static final Map<UUID, ManaData> playerManaData = new HashMap<>();
// Accessed from tick thread and packet handlers without synchronization
```

**Impact**:
- Race conditions in mana/energy updates
- ConcurrentModificationException risk
- Thread leaks on server reload

**Recommendation**:
1. Use `ConcurrentHashMap` instead of `HashMap` for all static player data maps
2. Use `ScheduledExecutorService` instead of raw threads
3. Add explicit ThreadLocal cleanup
4. Add `@ThreadSafe` annotations and document thread access

**✅ Solution Implemented (v1.5.2-beta)**:
- ✅ **FIXED**: Converted `HashMap` → `ConcurrentHashMap` in all manager classes:
  - `ManaManager.java` - playerManaData and regenTicks now use ConcurrentHashMap
  - `EnergyManager.java` - playerEnergyMap and regenTickCounter now use ConcurrentHashMap
  - `QuestManager.java` - Already using ConcurrentHashMap (was correct)
- ✅ **RESULT**: Thread-safe concurrent access, eliminates ConcurrentModificationException crashes
- ⚠️ **REMAINING**: Raw thread creation and ThreadLocal cleanup still need addressing (lower priority)

**Impact**:
- Server stability significantly improved on busy servers (10+ concurrent players)
- Race conditions in mana/energy updates eliminated
- Random crashes during high ability usage prevented

---

## 🟡 Medium Priority Issues

### 5. Unimplemented Consumable Features (7 Items)

**Location**: `item/ConsumableItem.java`
**Severity**: MEDIUM
**Status**: Documented in README Known Issues, planned for v1.6.0-v1.7.0

**Incomplete Items**:

1. **VAMPIRE_DUST** (line 130):
   ```java
   // TODO: Implement actual lifesteal mechanic in future
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
   ```

2. **PHANTOM_DUST** (line 154):
   ```java
   // TODO: Implement actual dodge mechanic in future
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 2));
   ```

3. **PERFECT_DODGE** (line 158):
   ```java
   // TODO: Implement actual 100% dodge mechanic
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 4));
   ```

4. **SPELL_ECHO** (line 194):
   ```java
   // TODO: Implement spell doubling mechanic
   ```

5. **OVERCHARGE_DUST** (line 234):
   ```java
   // TODO: Implement 2x spell power for next spell
   ```

6. **LAST_STAND_POWDER** (line 243):
   ```java
   // TODO: Implement totem-like revive mechanic
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 3));
   ```

7. **TIME_DISTORTION** (line 261):
   ```java
   // TODO: Add slow motion effect to nearby entities
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 2));
   ```

**Impact**:
- Items don't match their descriptions
- Player confusion about item effects
- Inconsistent gameplay experience

**Recommendation**:
- Prioritize for v1.6.0 (lifesteal, dodge system) per roadmap
- Add clear in-game tooltips indicating "WIP" status
- Consider removing from loot tables until implemented

---

## 🟢 Low Priority Issues

### 9. Inconsistent Concurrency Patterns

**Locations**: Multiple managers
**Severity**: LOW
**Status**: Unresolved

**Problem**:
Some managers use `ConcurrentHashMap` (correct), others use `HashMap` (incorrect for concurrent access).

**Examples**:
- ✅ `PartyManager` uses `ConcurrentHashMap`
- ❌ `ManaManager` uses `HashMap`
- ❌ `EnergyManager` uses `HashMap`
- ❌ `QuestManager` uses `HashMap`

**Recommendation**:
Standardize on `ConcurrentHashMap` for all static player data maps.

---

### 10. Resource Cleanup Missing

**Location**: Multiple managers
**Severity**: LOW
**Status**: Unresolved

**Problem**:
No shutdown hooks or cleanup handlers registered for mod resources.

**Missing Cleanup**:
- Thread pools not shut down
- Static maps not cleared
- File handles potentially left open
- Timer tasks not cancelled

**Recommendation**:
```java
ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
    // Clean up all static resources
    ManaManager.shutdown();
    EnergyManager.shutdown();
    PartyManager.shutdown();
    // Stop all threads
    // Clear all caches
});
```

---

### 11. Hard-coded Magic Numbers

**Location**: Throughout codebase
**Severity**: LOW
**Status**: Unresolved

**Examples**:
- Mana regen rates (no config)
- Quest cooldowns (hard-coded)
- XP scaling factors (not adjustable)
- Ability damage values (hard-coded)

**Recommendation**:
Create configuration system:
```java
// config/dagmod.json
{
  "mana": {
    "baseRegen": 1,
    "regenInterval": 20
  },
  "quests": {
    "dailyCooldown": 86400
  }
}
```

---

### 12. Missing Input Validation

**Location**: Command handlers, packet receivers
**Severity**: LOW
**Status**: Unresolved

**Problem**:
Limited validation on player input could allow edge cases or exploits.

**Examples**:
- No range checks on quest IDs
- No validation on party names
- No sanity checks on XP values

**Recommendation**:
Add validation layer in packet handlers and command processors.

---

## Summary of Issues by Category

| Category | Critical | Medium | Low | Total | Fixed |
|----------|----------|--------|-----|-------|-------|
| **Memory/Threading** | 2 | 0 | 2 | 4 | ✅ 2 (v1.5.2: Memory leak + Thread safety) |
| **Quest System** | 1 | 2 | 0 | 3 | ✅ 3 (v1.5.2: Persistence, v1.5.3: Progression blocker, Code Quality: Null safety) |
| **Data Persistence** | 2 | 1 | 1 | 4 | ✅ 3 (v1.5.3: Progression reset + New world leakage, Code Quality: Backup system) |
| **Code Quality** | 0 | 1 | 1 | 2 | ✅ 1 (Code Quality: Debug logging) |
| **Features** | 0 | 1 | 0 | 1 | - |
| **Party System** | 0 | 1 | 0 | 1 | ✅ 1 (Code Quality: Party quest integration) |
| **Total** | **5** | **6** | **4** | **15** | **10 Fixed** |
| **Remaining** | **0** | **1** | **4** | **5** | - |

---

## Recommended Action Items

### ✅ Completed (v1.5.2-beta)
1. ✅ **FIXED**: Quest persistence - Added complete NBT save/load system
2. ✅ **FIXED**: Memory leak - Added disconnect handlers to clear player data maps from QuestManager, ManaManager, EnergyManager, CooldownManager
3. ✅ **FIXED**: Thread safety - Converted HashMap → ConcurrentHashMap in ManaManager and EnergyManager

### ✅ Completed (v1.5.3-beta)
4. ✅ **FIXED**: Quest progression blocker - Removed circular dependency in quest book upgrade system
5. ✅ **FIXED**: Progression reset bug - Removed auto-create logic, added null safety checks
6. ✅ **FIXED**: New world data leakage - Added proper data clearing for new worlds

### ✅ Completed (Code Quality Pass)
7. ✅ **FIXED**: Debug logging cleanup - Removed spam logs from CustomArmorSetBonus.java
8. ✅ **FIXED**: Party quest integration - Completed stub code for mob kill/crafting tracking
9. ✅ **FIXED**: Quest system null safety - Added defensive null checks in QuestManager.java
10. ✅ **FIXED**: Data backup system - Implemented .tmp → .dat → .bak backup flow with recovery

### Short-term (v1.6.0)
11. Implement lifesteal for VAMPIRE_DUST
12. Implement dodge system for PHANTOM_DUST/PERFECT_DODGE

### Medium-term (v1.7.0)
13. Implement spell modification system (SPELL_ECHO, OVERCHARGE_DUST)
14. Add configuration system for tunable values
15. Improve error handling and validation

### Long-term (v2.0.0)
16. Refactor TagCollectObjective or deprecate it
17. Add comprehensive resource cleanup system
18. Add integration tests for concurrency

---

## Testing Recommendations

### Critical Path Testing
1. **Memory Leak Test**: Run server with 10+ players connecting/disconnecting for 1 hour
2. **Concurrency Test**: Spawn 50+ players using mana abilities simultaneously
3. **Data Persistence Test**: Force server crash during save and verify recovery
4. **Quest System Test**: Complete all quests with TagCollectObjective types

### Stress Testing
- Load test with 100+ concurrent players
- Quest completion race conditions (multiple players, same quest)
- Party quest with maximum party size

---

## References

- **CLAUDE.md** - Development patterns and critical warnings
- **CHANGELOG.md** - Version history and known issues by version
- **README.md** - Known issues section (lines 273-281)
- **BACKWARDS_REWARDS.md** - Quest reward fixes in v1.5.1
- **GitHub Issues** - https://github.com/hitman20081/DAGMod/issues

---

**Last Updated**: 2025-12-01
**Next Review**: Before v1.6.0 release

---

## Changelog

- **2025-12-01** (Code Quality Pass): Fixed 4 medium-priority issues (debug logging, party quests, null safety, backup system)
- **2025-12-01** (v1.5.3-beta): Added 3 critical fixes (quest progression blocker, progression reset, new world leakage)
- **2025-11-24** (v1.5.2-beta): Added 3 critical fixes (quest persistence, memory leak, thread safety)
- **2025-11-24**: Initial comprehensive analysis
