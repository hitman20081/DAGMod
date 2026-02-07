# DAGMod Testing Checklist - v1.5.4

**Version**: v1.5.4-beta
**Test Date**: _____________
**Tester**: _____________
**Minecraft Version**: 1.21.11
**Fabric Loader**: 0.18.1

---

## ✅ **Test Status Key**
- ⬜ Not tested
- ✅ Passed
- ❌ Failed (note issue)
- ⚠️ Partial (note details)
- 🔄 Needs retest

---

## 🔥 **CRITICAL - Health Persistence Fix**

### **Test 1: Basic Health Persistence**
- ⬜ Start new world, create new player
- ⬜ Level up to level 10 (should have 9 extra hearts = 19 total)
- ⬜ Note current health: _____ hearts
- ⬜ Die (jump off cliff, /kill, etc.)
- ⬜ Respawn and check health
- ⬜ **EXPECTED**: Still have 19 hearts total
- ⬜ **RESULT**: _____ hearts
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 2: Multi-Level Health Persistence**
- ⬜ Level up to level 20 (should have 19 extra hearts = 29 total)
- ⬜ Note current health: _____ hearts
- ⬜ Die and respawn
- ⬜ **EXPECTED**: Still have 29 hearts
- ⬜ **RESULT**: _____ hearts
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 3: Level 50 Health Persistence**
- ⬜ Use `/testprogression setlevel 50` to reach max level
- ⬜ Note current health: _____ hearts (should be 59 total)
- ⬜ Die and respawn
- ⬜ **EXPECTED**: Still have 59 hearts
- ⬜ **RESULT**: _____ hearts
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 4: Attack Damage Persistence**
- ⬜ At level 20+, note attack damage in inventory screen: _____
- ⬜ Die and respawn
- ⬜ Check attack damage again
- ⬜ **EXPECTED**: Same attack damage as before death
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 5: Armor Persistence**
- ⬜ At level 20+, note armor value: _____
- ⬜ Die and respawn
- ⬜ Check armor value again
- ⬜ **EXPECTED**: Same armor as before death
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 6: Server Restart Persistence**
- ⬜ Level up to 10+
- ⬜ Log out of server
- ⬜ Restart server
- ⬜ Log back in
- ⬜ **EXPECTED**: Health/stats still correct
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🎓 **Tutorial NPC - Innkeeper Garrick**

### **Test 7: Garrick Spawning**
- ⬜ Run `/summon_garrick` command as OP
- ⬜ **EXPECTED**: Garrick spawns at player location
- ⬜ **RESULT**: _____ (spawned/error)
- ⬜ Verify Garrick has correct texture/model
- ⬜ Verify Garrick does not despawn after time
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 8: First Interaction with Garrick**
- ⬜ Create NEW player character (no previous data)
- ⬜ Right-click Garrick
- ⬜ **EXPECTED**: "✓ Quest System Unlocked!" message appears
- ⬜ **RESULT**: _____
- ⬜ Verify tutorial dialogue appears (3-step instructions)
- ⬜ Verify no debug messages appear
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 9: Repeat Interaction with Garrick**
- ⬜ Right-click Garrick again (after first interaction)
- ⬜ **EXPECTED**: No "unlocked" message (already unlocked)
- ⬜ **EXPECTED**: Dialogue appropriate to quest status
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 10: Garrick Persistence After Logout**
- ⬜ Interact with Garrick once
- ⬜ Log out and log back in
- ⬜ **EXPECTED**: Quest system still unlocked (no re-unlock needed)
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 11: Garrick Invulnerability**
- ⬜ Attack Garrick with sword
- ⬜ **EXPECTED**: No damage taken, cannot be killed
- ⬜ **RESULT**: _____
- ⬜ Try to push Garrick
- ⬜ **EXPECTED**: Does not get pushed
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🔒 **Quest System Gating**

### **Test 12: Quest Block Locked (New Player)**
- ⬜ Create NEW player (never met Garrick)
- ⬜ Right-click a Quest Block
- ⬜ **EXPECTED**: "🔒 This Quest Block is locked!" message
- ⬜ **EXPECTED**: Instructions to find Innkeeper Garrick
- ⬜ **EXPECTED**: Block does not open menu
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 13: Job Board Locked (New Player)**
- ⬜ Same new player, right-click a Job Board
- ⬜ **EXPECTED**: "🔒 This Job Board is locked!" message
- ⬜ **EXPECTED**: Instructions to find Innkeeper Garrick
- ⬜ **EXPECTED**: Block does not open menu
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 14: Quest Block Unlocked (After Meeting Garrick)**
- ⬜ Interact with Garrick to unlock quest system
- ⬜ Right-click Quest Block
- ⬜ **EXPECTED**: Quest menu opens normally
- ⬜ **EXPECTED**: No locked message
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 15: Job Board Unlocked (After Meeting Garrick)**
- ⬜ Right-click Job Board
- ⬜ **EXPECTED**: Job Board menu opens normally
- ⬜ **EXPECTED**: No locked message
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 16: Unlock Persists Across Sessions**
- ⬜ Unlock quest system by meeting Garrick
- ⬜ Log out and log back in
- ⬜ Try Quest Block and Job Board
- ⬜ **EXPECTED**: Both blocks still work (remain unlocked)
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🎮 **Party System (Regression Testing)**

### **Test 17: Party Creation**
- ⬜ Player 1: Run `/party create`
- ⬜ **EXPECTED**: "Party created! You are the party leader." message
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 18: Party Invitation**
- ⬜ Player 1 (leader): `/party invite <Player2>`
- ⬜ **EXPECTED**: Player 2 receives invitation
- ⬜ Player 2: `/party accept`
- ⬜ **EXPECTED**: Both players now in party
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 19: Party XP Sharing**
- ⬜ Player 1 and Player 2 in party
- ⬜ Player 1 kills a mob
- ⬜ **EXPECTED**: Both players receive XP
- ⬜ **RESULT**: _____
- ⬜ Note Player 1 XP gained: _____
- ⬜ Note Player 2 XP gained: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 20: Party Loot Distribution**
- ⬜ Player 1 and Player 2 in party
- ⬜ Kill a boss (Skeleton King, Wither, Dragon, etc.)
- ⬜ **EXPECTED**: Both players receive loot
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 21: Party Quest Sharing**
- ⬜ Player 1 and Player 2 in party
- ⬜ Player 1: Start a party quest
- ⬜ **EXPECTED**: Both players track same quest objectives
- ⬜ Player 2: Kill a mob for quest objective
- ⬜ **EXPECTED**: Progress updates for both players
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 22: Party Leave**
- ⬜ Player 2: `/party leave`
- ⬜ **EXPECTED**: Player 2 leaves party
- ⬜ **EXPECTED**: Player 1 still has party
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 📜 **Quest System (Regression Testing)**

### **Test 23: Quest Block Basic Flow**
- ⬜ Right-click Quest Block
- ⬜ Browse available quests
- ⬜ Accept a MAIN or SIDE quest
- ⬜ **EXPECTED**: Quest appears in active quests
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 24: Job Board Basic Flow**
- ⬜ Right-click Job Board
- ⬜ Browse available jobs
- ⬜ Accept a JOB category quest
- ⬜ **EXPECTED**: Job appears in active jobs
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 25: Quest Completion**
- ⬜ Complete a simple quest (collect items, kill mobs, etc.)
- ⬜ Return to Quest Block
- ⬜ **EXPECTED**: Turn-in screen appears immediately
- ⬜ Turn in quest
- ⬜ **EXPECTED**: Rewards received (XP, items)
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 26: Quest Book Tiers**
- ⬜ Complete a quest chain
- ⬜ **EXPECTED**: Quest book upgrades to next tier (Novice→Apprentice→Expert→Master)
- ⬜ **RESULT**: Current tier: _____
- ⬜ Verify higher difficulty quests now available
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 27: Level-Gated Quests**
- ⬜ At level 1, check available quests
- ⬜ **EXPECTED**: Only see quests for current level
- ⬜ Level up to 5+
- ⬜ **EXPECTED**: More quests become available
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## ⚔️ **Class & Race System (Regression Testing)**

### **Test 28: Race Selection**
- ⬜ New player: Find Race Selection Altar
- ⬜ Select a race (Human/Dwarf/Elf/Orc)
- ⬜ **EXPECTED**: Race bonuses apply immediately
- ⬜ **RESULT**: _____
- ⬜ Verify race-specific passive (mining speed, XP bonus, etc.)
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 29: Class Selection**
- ⬜ Find Class Selection Altar
- ⬜ Select a class (Warrior/Mage/Rogue)
- ⬜ **EXPECTED**: Class abilities appear in inventory
- ⬜ **EXPECTED**: Receive Novice Quest Book
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 30: Class Abilities Work**
- ⬜ Test at least one ability from selected class:
  - Warrior: Use Battle Standard or Whirlwind Axe
  - Mage: Use Arcane Orb or Temporal Crystal
  - Rogue: Use Void Blade or Vanish Cloak
- ⬜ **EXPECTED**: Ability activates with correct effects
- ⬜ **EXPECTED**: Cooldown/resource system works
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 31: Race/Class Synergy**
- ⬜ Verify race+class synergy is active (check with appropriate conditions):
  - Dwarf Warrior: Resistance underground
  - Elf Rogue: Invisibility in forest while sneaking
  - Orc Warrior: Berserker at low health
  - Human Mage: Random regeneration
- ⬜ **EXPECTED**: Synergy effect triggers
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🌍 **World Content (Regression Testing)**

### **Test 32: Bone Realm Portal**
- ⬜ Build portal with Ancient Bone blocks
- ⬜ Use Necrotic Key to activate
- ⬜ **EXPECTED**: Portal activates and allows travel
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 33: Boss Fights**
- ⬜ Fight Skeleton King (or other boss)
- ⬜ **EXPECTED**: Boss has correct health/attacks
- ⬜ **EXPECTED**: Boss drops loot on death
- ⬜ **EXPECTED**: Chest spawns with key
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 34: Hall of Champions**
- ⬜ Use Hall Locator to find Hall
- ⬜ **EXPECTED**: Structure generates correctly
- ⬜ **EXPECTED**: Contains Race and Class altars
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🛠️ **Commands (Regression Testing)**

### **Test 35: Info Command**
- ⬜ Run `/info`
- ⬜ **EXPECTED**: Shows race, class, level, stats
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 36: Quest Command**
- ⬜ Run `/quest list`
- ⬜ **EXPECTED**: Shows available quests
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 37: Progression Test Command**
- ⬜ Run `/testprogression info`
- ⬜ **EXPECTED**: Shows level/XP info
- ⬜ Run `/testprogression 500` (add XP)
- ⬜ **EXPECTED**: XP increases, possible level up
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 38: Reset Class Command**
- ⬜ Run `/resetclass`
- ⬜ **EXPECTED**: Class is reset
- ⬜ **EXPECTED**: Can select new class
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 39: Travel Command**
- ⬜ Run `/travel <destination>`
- ⬜ **EXPECTED**: Player teleports to destination
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 40: Summon Garrick Command**
- ⬜ Run `/summon_garrick` as non-OP player
- ⬜ **EXPECTED**: Permission denied
- ⬜ Run `/summon_garrick` as OP
- ⬜ **EXPECTED**: Garrick spawns
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

---

## 🌐 **Multiplayer Testing**

### **Test 41: Multiple Players - Quest System**
- ⬜ Player 1 and Player 2 both unlock quest system with Garrick
- ⬜ **EXPECTED**: Each player tracks separately
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 42: Multiple Players - Health Persistence**
- ⬜ Both players level up
- ⬜ Both players die
- ⬜ **EXPECTED**: Both players keep their extra hearts
- ⬜ **RESULT**: _____
- **Status**: ⬜ Pass / ⬜ Fail

### **Test 43: Server Performance**
- ⬜ Test with 2-4 players active
- ⬜ **EXPECTED**: No lag, smooth gameplay
- ⬜ **RESULT**: _____
- ⬜ Check console for errors
- **Status**: ⬜ Pass / ⬜ Fail

---

## 📊 **Test Summary**

### **Critical Tests (Must Pass)**
- ⬜ Test 1-6: Health/Stats Persistence
- ⬜ Test 8: Garrick First Interaction
- ⬜ Test 12-15: Quest System Gating

### **High Priority Tests (Should Pass)**
- ⬜ Test 7, 9-11: Garrick NPC Functionality
- ⬜ Test 16: Unlock Persistence
- ⬜ Test 17-22: Party System
- ⬜ Test 23-27: Quest System

### **Medium Priority Tests (Nice to Pass)**
- ⬜ Test 28-31: Race/Class System
- ⬜ Test 32-34: World Content
- ⬜ Test 35-40: Commands

### **Low Priority Tests (Optional)**
- ⬜ Test 41-43: Multiplayer

---

## 🐛 **Issues Found**

### Issue #1
- **Test**: _____
- **Description**: _____
- **Severity**: Critical / High / Medium / Low
- **Steps to Reproduce**: _____
- **Expected**: _____
- **Actual**: _____

### Issue #2
- **Test**: _____
- **Description**: _____
- **Severity**: Critical / High / Medium / Low
- **Steps to Reproduce**: _____
- **Expected**: _____
- **Actual**: _____

### Issue #3
- **Test**: _____
- **Description**: _____
- **Severity**: Critical / High / Medium / Low
- **Steps to Reproduce**: _____
- **Expected**: _____
- **Actual**: _____

---

## ✅ **Final Sign-Off**

- **Total Tests**: 43
- **Tests Passed**: _____
- **Tests Failed**: _____
- **Tests Skipped**: _____
- **Pass Rate**: _____ %

**Ready for Release**: ⬜ Yes / ⬜ No (blockers: _____)

**Tester Signature**: _____________________
**Date**: _____________________

---

**Notes**:
- Critical tests MUST pass before release
- High priority tests should have 95%+ pass rate
- Document all failures in Issues Found section
- Retest failed tests after fixes
