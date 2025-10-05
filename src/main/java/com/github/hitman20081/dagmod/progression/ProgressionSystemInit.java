package com.github.hitman20081.dagmod.progression;

/**
 * INTEGRATION GUIDE FOR PROGRESSION SYSTEM
 *
 * Follow these steps to integrate the progression system into your mod:
 *
 * ============================================================
 * STEP 1: SERVER-SIDE INITIALIZATION
 * ============================================================
 *
 * In your main mod class (DAGMod.java or similar), in the onInitialize() method:
 *
 * @Override
 * public void onInitialize() {
 *     // Your existing initialization...
 *
 *     // Register progression events
 *     ProgressionEvents.register();
 *
 *     // Register progression packets
 *     ProgressionPackets.registerServerPackets();
 *
 *     // Register test command (for development/testing)
 *     CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
 *         ProgressionTestCommand.register(dispatcher, registryAccess, environment);
 *     });
 * }
 *
 * ============================================================
 * STEP 2: CLIENT-SIDE INITIALIZATION
 * ============================================================
 *
 * Create a client initializer class (or use existing one):
 *
 * package com.github.hitman20081.dagmod;
 *
 * import net.fabricmc.api.ClientModInitializer;
 * import com.github.hitman20081.dagmod.progression.client.ClientProgressionData;
 * import com.github.hitman20081.dagmod.progression.client.ProgressionHUD;
 *
 * public class DAGModClient implements ClientModInitializer {
 *     @Override
 *     public void onInitializeClient() {
 *         // Register client packet handlers
 *         ClientProgressionData.registerClientPackets();
 *
 *         // Register HUD renderer
 *         ProgressionHUD.register();
 *     }
 * }
 *
 * ============================================================
 * STEP 3: UPDATE fabric.mod.json
 * ============================================================
 *
 * Add client entrypoint to your fabric.mod.json:
 *
 * {
 *   "entrypoints": {
 *     "main": [
 *       "com.github.hitman20081.dagmod.DAGMod"
 *     ],
 *     "client": [
 *       "com.github.hitman20081.dagmod.DAGModClient"
 *     ]
 *   }
 * }
 *
 * ============================================================
 * STEP 4: USAGE EXAMPLES
 * ============================================================
 *
 * Award XP from your quest system:
 *
 * public void onQuestComplete(ServerPlayerEntity player, Quest quest) {
 *     int xpReward = quest.getXPReward();
 *     int levelsGained = ProgressionManager.addXP(player, xpReward);
 *
 *     if (levelsGained > 0) {
 *         // Level up happened automatically!
 *         // ProgressionManager already handles:
 *         // - Sound effects
 *         // - Particles
 *         // - Healing
 *         // - Message
 *         // - Client sync
 *     }
 * }
 *
 * Award XP from mob kills (in a mixin or event handler):
 *
 * @Inject(method = "onDeath", at = @At("HEAD"))
 * private void onMobDeath(DamageSource source, CallbackInfo ci) {
 *     if (source.getAttacker() instanceof ServerPlayerEntity player) {
 *         LivingEntity entity = (LivingEntity)(Object)this;
 *
 *         int xp = calculateMobXP(entity);
 *         ProgressionManager.addXP(player, xp);
 *     }
 * }
 *
 * Check player level for gating:
 *
 * public boolean canAccessQuest(ServerPlayerEntity player, Quest quest) {
 *     PlayerProgressionData data = ProgressionManager.getPlayerData(player);
 *     return data.getCurrentLevel() >= quest.getRequiredLevel();
 * }
 *
 * ============================================================
 * STEP 5: FILE STRUCTURE CHECKLIST
 * ============================================================
 *
 * Ensure you have these files in the correct locations:
 *
 * src/main/java/com/github/hitman20081/dagmod/progression/
 * ├── PlayerProgressionData.java       ✓ Core data class
 * ├── ProgressionManager.java          ✓ Server-side manager
 * ├── ProgressionStorage.java          ✓ File I/O
 * ├── ProgressionPackets.java          ✓ Networking
 * ├── ProgressionEvents.java           ✓ Event handlers
 * └── ProgressionTestCommand.java      ✓ Test command
 *
 * src/main/java/com/github/hitman20081/dagmod/progression/client/
 * ├── ClientProgressionData.java       ✓ Client data storage
 * └── ProgressionHUD.java              ✓ HUD renderer
 *
 * ============================================================
 * STEP 6: TESTING
 * ============================================================
 *
 * In-game commands to test:
 *
 * /testprogression 500           - Add 500 XP (should level up)
 * /testprogression info          - View current progression
 * /testprogression setlevel 25   - Jump to level 25
 * /testprogression reset         - Reset to level 1
 * /testprogression curve         - View XP curve
 *
 * What to verify:
 * ✓ XP bar appears below hotbar
 * ✓ Bar fills as XP increases
 * ✓ Level text updates
 * ✓ Level-up sound plays
 * ✓ Level-up message appears
 * ✓ Data saves on disconnect
 * ✓ Data loads on reconnect
 * ✓ Works on dedicated server
 *
 * ============================================================
 * STEP 7: CONNECT TO EXISTING SYSTEMS
 * ============================================================
 *
 * Update your quest completion handler:
 *
 * In your QuestHandler class, add XP rewards:
 *
 * private void completeQuest(ServerPlayerEntity player, QuestData quest) {
 *     // Your existing quest completion logic...
 *
 *     // Award XP based on quest tier
 *     int xpReward = switch(quest.getTier()) {
 *         case NOVICE -> 200;
 *         case APPRENTICE -> 500;
 *         case JOURNEYMAN -> 1000;
 *         case EXPERT -> 1500;
 *         case MASTER -> 2500;
 *     };
 *
 *     ProgressionManager.addXP(player, xpReward);
 * }
 *
 * ============================================================
 * TROUBLESHOOTING
 * ============================================================
 *
 * HUD not showing:
 * - Check ClientProgressionData.registerClientPackets() is called
 * - Check ProgressionHUD.register() is called
 * - Verify client entrypoint in fabric.mod.json
 *
 * Data not saving:
 * - Check ProgressionEvents.register() is called
 * - Verify world/data/dagmod/progression/ folder exists
 * - Check server logs for save errors
 *
 * XP not syncing to client:
 * - Check ProgressionPackets.registerServerPackets() is called
 * - Use ProgressionManager.addXP() instead of PlayerProgressionData.addXP()
 * - Verify server→client packets in debug logs
 *
 * ============================================================
 * NEXT STEPS AFTER THIS WORKS
 * ============================================================
 *
 * 1. Create XPEventHandler.java for automatic XP from:
 *    - Mob kills
 *    - Mining/gathering
 *    - Crafting
 *    - Ability usage
 *
 * 2. Add level-based stat scaling:
 *    - HP increases per level
 *    - Stat bonuses every 5 levels
 *    - Connect to your race/class attribute system
 *
 * 3. Implement level gates:
 *    - Race quests unlock at levels 10, 20, 30, 40
 *    - Class abilities improve with level
 *    - Enchantments require minimum level
 *
 * 4. Add progression commands for admins:
 *    - /dagmod xp <player> <amount>
 *    - /dagmod level <player> <level>
 *    - /dagmod progression reset <player>
 *
 * ============================================================
 */
public class ProgressionSystemInit {
    // This class exists only for documentation
    // All actual initialization happens in the classes referenced above
}