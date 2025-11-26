package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.github.hitman20081.dagmod.bone_realm.chest.BoneRealmChestRegistry;
import com.github.hitman20081.dagmod.bone_realm.chest.BossDeathEventHandler;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.warrior.ShieldBashListener;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyManager;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyNetworking;
import com.github.hitman20081.dagmod.command.InfoCommand;
import com.github.hitman20081.dagmod.command.QuestCommand;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.effect.ModEffects;
import com.github.hitman20081.dagmod.event.DeathMessageHandler;
import com.github.hitman20081.dagmod.event.FortuneDustHandler;
import com.github.hitman20081.dagmod.event.ShadowBlendHandler;
import com.github.hitman20081.dagmod.item.ModItemGroups;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.networking.ModNetworking;
import com.github.hitman20081.dagmod.party.command.PartyCommand;
import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.github.hitman20081.dagmod.party.quest.PartyQuestRegistry;
import com.github.hitman20081.dagmod.potion.ModPotions;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.progression.ProgressionEvents;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.progression.ProgressionPackets;
import com.github.hitman20081.dagmod.progression.ProgressionTestCommand;
import com.github.hitman20081.dagmod.progression.PlayerProgressionData;
import com.github.hitman20081.dagmod.progression.ProgressionStorage;
import com.github.hitman20081.dagmod.progression.StatScalingHandler;
import com.github.hitman20081.dagmod.progression.XPEventHandler;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestUtils;
import com.github.hitman20081.dagmod.quest.registry.QuestRegistry;
import com.github.hitman20081.dagmod.race_system.PlayerTickHandler;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.github.hitman20081.dagmod.command.ResetClassCommand;
import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import com.github.hitman20081.dagmod.party.quest.PartyQuestRegistry;
import com.github.hitman20081.dagmod.party.quest.PartyQuestManager;
import com.github.hitman20081.dagmod.party.command.PartyQuestCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.WorldSavePath;

import static com.github.hitman20081.dagmod.potion.ModPotions.XP_POTION;

public class DagMod implements ModInitializer {
    public static final String MOD_ID = "dagmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModItems.registerModItems();

        ModItemGroups.registerItemGroups();


        ModEntities.initialize();

        ModEffects.registerEffects();
        ModPotions.registerPotion();

        // Copy bundled Bleakwind world data on server start
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            copyBundledBleakwindWorld(server);
        });

        BoneRealmRegistry.register(); // Register Bone realm

        // Register Bone Realm Entities
        BoneRealmEntityRegistry.register();

        // Register Bone Realm chests
        BoneRealmChestRegistry.register();

        // Register boss death handler
        BossDeathEventHandler.register();

        // Register tutorial mob kill tracking
        registerTutorialMobKillTracking();

        ModBlocks.initialize();

        // Initialize Networking
        ModNetworking.initialize();

        // Register mana payloads
        com.github.hitman20081.dagmod.class_system.mana.ManaNetworking.registerPayloads();
        LOGGER.info("Mana system networking initialized!");

        // Register warrior ability systems
        registerWarriorSystems();

        // ===== ROGUE SYSTEM INITIALIZATION =====

        // Register energy networking packets
        PayloadTypeRegistry.playS2C().register(
                EnergyNetworking.EnergySyncPayload.ID,
                EnergyNetworking.EnergySyncPayload.CODEC
        );

        ShadowBlendHandler.register();

        FortuneDustHandler.register();

        // Register Party Quest block break handler
        com.github.hitman20081.dagmod.event.PartyQuestBlockBreakHandler.register();

        // Initialize energy management system
        EnergyManager.initialize();

        // Register Rogue combat handler (backstab + poison dagger)
        RogueCombatHandler.register();

        LOGGER.info("Rogue ability system initialized!");

        // Initialize Quest System
        LOGGER.info("Initializing Quest System for " + MOD_ID);
        QuestRegistry.registerQuests();
        QuestManager.getInstance();
        LOGGER.info("Quest System initialized successfully!");

        // Register party quest system
        PartyQuestRegistry.registerQuests();

        // Register race/class synergy ticker
        PlayerTickHandler.register();
        LOGGER.info("Race/Class synergy system initialized!");

        // Register progression system events
        ProgressionEvents.register();

        // Register progression packets
        ProgressionPackets.registerServerPackets();

        // Register XP Event Handler
        XPEventHandler.register();

        // Register all commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            InfoCommand.register(dispatcher, registryAccess, environment);
            QuestCommand.register(dispatcher);
            ResetClassCommand.register(dispatcher, registryAccess, environment);
            ProgressionTestCommand.register(dispatcher, registryAccess, environment);
            PartyCommand.register(dispatcher, registryAccess, environment);
            PartyQuestCommand.register(dispatcher, registryAccess, environment);

            // Ship Travel Command
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("travel")
                    .then(net.minecraft.server.command.CommandManager.argument("destination",
                                    com.mojang.brigadier.arguments.StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                String destination = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "destination");

                                com.github.hitman20081.dagmod.travel.ShipTravelManager
                                        .travelToDestination(player, destination);

                                return 1;
                            })
                    )
            );

            // Summon Innkeeper Garrick Command (for testing/structure blocks)
            dispatcher.register(CommandManager.literal("summon_garrick")
                    .requires(source -> source.hasPermissionLevel(2)) // Requires OP
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                        // Spawn Garrick at player's location
                        com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC garrick =
                            new com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC(
                                ModEntities.INNKEEPER_GARRICK,
                                player.getEntityWorld()
                            );

                        garrick.refreshPositionAndAngles(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            player.getYaw(),
                            0.0F
                        );

                        player.getEntityWorld().spawnEntity(garrick);

                        player.sendMessage(
                            Text.literal("Innkeeper Garrick summoned!").formatted(Formatting.GREEN),
                            false
                        );

                        return 1;
                    })
            );
        });

        // Apply class abilities when player respawns (including after death)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ClassAbilityManager.applyClassAbilities(newPlayer);

            // Reapply progression stats (fixes health/attack/armor reset on death)
            PlayerProgressionData progressionData = ProgressionManager.getPlayerData(newPlayer);
            StatScalingHandler.applyLevelStats(newPlayer, progressionData.getCurrentLevel());

            BlockPos hallPos = PlayerDataManager.loadHallLocation(newPlayer.getEntityWorld().getServer());

            if (hallPos != null) {
                // Schedule teleport for 5 ticks later (0.25 seconds)
                new Thread(() -> {
                    try {
                        Thread.sleep(250); // Wait 250ms
                        newPlayer.getEntityWorld().getServer().execute(() -> {
                            LOGGER.info("Executing delayed Hall teleport to: " + hallPos);
                            newPlayer.teleport(
                                    hallPos.getX() + 0.5,
                                    hallPos.getY() + 1.0,
                                    hallPos.getZ() + 0.5,
                                    true
                            );

                            newPlayer.sendMessage(
                                    Text.literal("You have respawned at the Hall of Champions")
                                            .formatted(Formatting.GOLD),
                                    false
                            );
                        });
                    } catch (InterruptedException e) {
                        LOGGER.error("Hall teleport interrupted", e);
                    }
                }).start();
            }
        });

        // Load player data when entity loads (login, respawn, dimension change)
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                // Load persistent data first
                PlayerDataManager.loadPlayerData(player);

                // Load quest data
                QuestManager.getInstance().loadPlayerQuestData(player);

                // Then apply abilities (already loaded by loadPlayerData, but this ensures sync)
                ClassAbilityManager.applyClassAbilities(player);
                RaceAbilityManager.applyRaceAbilities(player);
            }
        });

        // Give starter items ONLY on first join (not on dimension change or respawn)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            // Only give items to completely new players (no race/class data)
            if (!PlayerDataManager.hasPlayerData(player)) {
                // Check if player already has a Hall Locator (double-check safety)
                if (!hasHallLocator(player)) {
                    player.giveItemStack(new ItemStack(ModItems.HALL_LOCATOR));
                    player.giveItemStack(QuestUtils.createWelcomeBook());
                    // NOTE: Removed NOVICE_QUEST_BOOK - it will be given on class selection instead

                    player.sendMessage(Text.literal("═══════════════════════════════")
                            .formatted(Formatting.GOLD), false);
                    player.sendMessage(Text.literal("Welcome to DAGMod!")
                            .formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.BOLD), false);
                    player.sendMessage(Text.literal("═══════════════════════════════")
                            .formatted(Formatting.GOLD), false);
                    player.sendMessage(Text.literal("You've been given a Hall Locator!")
                            .formatted(Formatting.YELLOW), false);
                    player.sendMessage(Text.literal("Right-click it to find the Hall of Champions.")
                            .formatted(Formatting.GRAY), false);
                    player.sendMessage(Text.literal("═══════════════════════════════")
                            .formatted(Formatting.GOLD), false);
                }
            }
        });

        // Hook into entity death events for kill objectives
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Check if a player killed this entity
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                // Update kill objectives for this player
                QuestManager.getInstance().updateKillProgress(player, entity.getType());

                // --- PARTY QUEST OBJECTIVE TRACKING ---
                com.github.hitman20081.dagmod.party.quest.PartyQuestData quest = com.github.hitman20081.dagmod.party.quest.PartyQuestManager.getInstance().getActiveQuest(player);
                if (quest != null) {
                    String mobType = Registries.ENTITY_TYPE.getId(entity.getType()).toString();

                    // Handle KILL_ENTITY objectives
                    for (var objective : quest.getTemplate().getObjectives()) {
                        if (objective.getType() == com.github.hitman20081.dagmod.party.quest.PartyQuestObjectiveType.KILL_ENTITY) {
                            boolean isHostile = entity instanceof net.minecraft.entity.mob.Monster;
                            if (objective.getTarget().equals(mobType) || (objective.getTarget().equals("hostile") && isHostile)) {
                                com.github.hitman20081.dagmod.party.quest.PartyQuestManager.getInstance().updateObjective(
                                        quest.getPartyId(),
                                        objective.getId(),
                                        1
                                );
                            }
                        }
                    }

                    // Handle KILL_BOSS objectives
                    if (com.github.hitman20081.dagmod.party.PartyLootHandler.isBossEntity(entity)) {
                        for (var objective : quest.getTemplate().getObjectives()) {
                            if (objective.getType() == com.github.hitman20081.dagmod.party.quest.PartyQuestObjectiveType.KILL_BOSS) {
                                if (objective.getTarget().contains(mobType)) {
                                    com.github.hitman20081.dagmod.party.quest.PartyQuestManager.getInstance().updateObjective(
                                            quest.getPartyId(),
                                            objective.getId(),
                                            1
                                    );
                                }
                            }
                        }
                    }
                }
            }

            // Add death message for players
            if (entity instanceof ServerPlayerEntity deadPlayer) {
                DeathMessageHandler.sendDeathMessage(deadPlayer);
            }
        });

        // Combined server tick events: Mana regeneration + Night Vision for Mages + Custom Armor Set Bonuses
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Tick party quest manager for timeouts
            PartyQuestManager.getInstance().tick();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                // Mana regeneration for all players (ManaManager checks if they're Mage)
                ManaManager.tick(player);

                // Night Vision for Mages
                if ("Mage".equals(playerClass)) {
                    if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                        player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.NIGHT_VISION,
                                300, // 15 seconds
                                0,
                                true,  // ambient
                                false, // no particles
                                false  // no icon
                        ));
                    }
                }

                // Custom armor set bonuses (Dragonscale, Crystalforge, Inferno, Nature's Guard, Shadow, Fortuna)
                com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus.applySetBonuses(player);
            }
        });

        // ========== PLAYER DISCONNECT HANDLER - SAVE ALL DATA ==========
        // This is CRITICAL for data persistence - saves all player progress on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID playerId = player.getUuid();

            LOGGER.info("Saving data for disconnecting player: " + player.getName().getString());

            // Save race/class data
            PlayerDataManager.savePlayerData(player);

            // Save quest data
            QuestManager.getInstance().savePlayerQuestData(player);

            // Save progression data (XP, levels, stats)
            ProgressionStorage.savePlayerData(ProgressionManager.getPlayerData(player));

            // Clean up memory (prevent memory leaks)
            QuestManager.getInstance().clearPlayerData(playerId);
            ManaManager.clearPlayerData(playerId);
            EnergyManager.clearPlayerData(playerId);
            CooldownManager.clearPlayerCooldowns(playerId);

            LOGGER.info("Successfully saved and cleaned up data for: " + player.getName().getString());
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.LUCK,
                    // Ingredient
                    Items.EXPERIENCE_BOTTLE,
                    // Output potion.
                    Registries.POTION.getEntry(XP_POTION)
            );
        });

        // Register party quest system
        PartyQuestRegistry.registerQuests();

        // Register party quest commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PartyQuestCommand.register(dispatcher, registryAccess, environment);
        });

        // Tick quest manager for timeouts
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            PartyQuestManager.getInstance().tick();
        });
    } // Make sure this closing brace for onInitialize() is here

    /**
     * Register warrior ability systems
     */
    private void registerWarriorSystems() {
        LOGGER.info("Registering Warrior Ability Systems");

        // Register Shield Bash listener
        ShieldBashListener.register();

        // NOTE: Cooldown clearing is now handled in the main disconnect handler (line ~396)

        LOGGER.info("Warrior Ability Systems registered successfully");
    }

    // Helper method for updating quest progress - THIS GOES OUTSIDE onInitialize()
    public static void updatePlayerQuestProgress(ServerPlayerEntity player) {
        QuestManager.getInstance().updateQuestProgress(player);
    }

    // Helper method to check if player has Hall Locator in inventory
    private static boolean hasHallLocator(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == ModItems.HALL_LOCATOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copy bundled Bleakwind world data from mod resources to the server's dimension folder
     */
    private static void copyBundledBleakwindWorld(MinecraftServer server) {
        try {
            // The dimension folder path should match your dimension JSON location
            Path dimensionPath = server.getSavePath(WorldSavePath.ROOT)
                    .resolve("dimensions/dagmod/bleakwind");

            Path regionPath = dimensionPath.resolve("region");

            // Only copy if the dimension hasn't been generated yet
            if (!Files.exists(regionPath) || isDirectoryEmpty(regionPath)) {
                LOGGER.info("Copying bundled Bleakwind world data...");

                // Copy region files (terrain and blocks)
                copyResourceFolder("/bundled_worlds/bleakwind/region", regionPath);

                // Copy entities
                copyResourceFolder("/bundled_worlds/bleakwind/entities",
                        dimensionPath.resolve("entities"));

                // Copy POI (points of interest)
                copyResourceFolder("/bundled_worlds/bleakwind/poi",
                        dimensionPath.resolve("poi"));

                // Copy data (structures, etc.)
                copyResourceFolder("/bundled_worlds/bleakwind/data",
                        dimensionPath.resolve("data"));

                LOGGER.info("Bleakwind world data copied successfully!");
            } else {
                LOGGER.info("Bleakwind world already exists, skipping copy.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy Bleakwind world data", e);
        }
    }

    private static boolean isDirectoryEmpty(Path path) throws IOException {
        if (!Files.exists(path)) return true;
        try (var stream = Files.list(path)) {
            return !stream.findAny().isPresent();
        }
    }

    /**
     * Copy a folder from mod resources to filesystem
     */
    private static void copyResourceFolder(String resourcePath, Path destination) throws IOException {
        Files.createDirectories(destination);

        // Get resource as URL from classpath
        var resource = DagMod.class.getResource(resourcePath);
        if (resource == null) {
            LOGGER.warn("Resource not found: " + resourcePath);
            return;
        }

        try {
            // Handle both JAR and IDE (file system) scenarios
            if (resource.toURI().getScheme().equals("jar")) {
                copyFromJar(resourcePath, destination);
            } else {
                copyFromFileSystem(Paths.get(resource.toURI()), destination);
            }
        } catch (URISyntaxException e) {
            throw new IOException("Invalid resource URI", e);
        }
    }

    private static void copyFromJar(String resourcePath, Path destination) throws IOException {
        try {
            // Get the JAR file system
            URI uri = DagMod.class.getResource(resourcePath).toURI();
            Map<String, String> env = new HashMap<>();

            // Get or create the filesystem - handle case where it already exists
            FileSystem fs;
            try {
                // Try to get existing filesystem first
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                // If it doesn't exist, create it
                fs = FileSystems.newFileSystem(uri, env);
            }

            // Don't use try-with-resources since we didn't create it (or it may be shared)
            // The filesystem will be closed when the JVM shuts down
            Path source = fs.getPath(resourcePath);
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path target = destination.resolve(source.relativize(file).toString());
                    Files.createDirectories(target.getParent());
                    Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (URISyntaxException e) {
            throw new IOException("Invalid resource URI", e);
        }
    }

    private static void copyFromFileSystem(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = destination.resolve(source.relativize(file));
                Files.createDirectories(target.getParent());
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Register tutorial mob kill tracking for Garrick's Task 2
     */
    private static void registerTutorialMobKillTracking() {
        net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Only process on server side
            if (entity.getEntityWorld().isClient()) {
                return;
            }

            // Check if killer is a player
            if (!(damageSource.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player)) {
                return;
            }

            // Only count hostile mobs (monsters)
            if (entity instanceof net.minecraft.entity.mob.HostileEntity) {
                // Only track if player has completed Task 1, hasn't completed task 2 yet, and has met Garrick
                if (!PlayerDataManager.isTask2Complete(player.getUuid())
                        && PlayerDataManager.hasMetGarrick(player)
                        && PlayerDataManager.isTask1Complete(player.getUuid())) {
                    PlayerDataManager.incrementTask2MobKills(player);

                    // Send feedback to player
                    int kills = PlayerDataManager.getTask2MobKills(player.getUuid());
                    player.sendMessage(
                        net.minecraft.text.Text.literal("✓ Tutorial Progress: " + kills + "/5 hostile mobs defeated")
                            .formatted(net.minecraft.util.Formatting.GRAY),
                        true // Action bar
                    );

                    // Notify when complete
                    if (kills >= 5) {
                        player.sendMessage(
                            net.minecraft.text.Text.literal("✓ Task Complete! Return to Innkeeper Garrick")
                                .formatted(net.minecraft.util.Formatting.GREEN, net.minecraft.util.Formatting.BOLD),
                            false
                        );
                    }
                }
            }
        });
    }


} // This is the final closing brace of the DagMod class