package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.bone_realm.BoneRealmRegistry;
import com.github.hitman20081.dagmod.bone_realm.chest.BoneRealmChestRegistry;
import com.github.hitman20081.dagmod.bone_realm.chest.BossDeathEventHandler;
import com.github.hitman20081.dagmod.bone_realm.entity.BoneRealmEntityRegistry;
import com.github.hitman20081.dagmod.class_system.mana.ManaManager;
import com.github.hitman20081.dagmod.class_system.warrior.ShieldBashListener;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownManager;
import com.github.hitman20081.dagmod.class_system.warrior.CooldownNetworking;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyManager;
import com.github.hitman20081.dagmod.class_system.rogue.EnergyNetworking;
import com.github.hitman20081.dagmod.command.CooldownCommand;
import com.github.hitman20081.dagmod.command.DragonRespawnCommand;
import com.github.hitman20081.dagmod.command.GraveCommand;
import com.github.hitman20081.dagmod.command.InfoCommand;
import com.github.hitman20081.dagmod.command.LocateBoneDungeonCommand;
import com.github.hitman20081.dagmod.command.LocateDragonCommand;
import com.github.hitman20081.dagmod.command.LocateWildDragonCommand;
import com.github.hitman20081.dagmod.command.MerchantCommand;
import com.github.hitman20081.dagmod.command.PlayerDataCommand;
import com.github.hitman20081.dagmod.command.SeasonsCommand;
import com.github.hitman20081.dagmod.command.QuestCommand;
import com.github.hitman20081.dagmod.command.ResourceCommand;
import com.github.hitman20081.dagmod.command.SynergyCommand;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.effect.ModEffects;
import com.github.hitman20081.dagmod.entity.DragonSpawner;
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
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.github.hitman20081.dagmod.command.GarrickRegistryCommand;
import com.github.hitman20081.dagmod.command.ResetClassCommand;
import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import com.github.hitman20081.dagmod.party.command.PartyQuestCommand;
import com.github.hitman20081.dagmod.enchantment.CustomEnchantmentEffects;
import com.github.hitman20081.dagmod.enchantment.SoulBoundStorage;
import com.github.hitman20081.dagmod.grave.GraveManager;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelResource;

import com.github.hitman20081.dagmod.potion.ModPotions;
import static com.github.hitman20081.dagmod.potion.ModPotions.XP_POTION;

public class DagMod implements ModInitializer {
    public static final String MOD_ID = "dagmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private int tickCounter = 0;

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

            // Initialize rotating trade manager
            RotatingTradeManager.getInstance().initialize(server);

            // Load daily quest rotation
            com.github.hitman20081.dagmod.quest.daily.DailyQuestManager.getInstance().load(server);

            // Initialize grave system
            GraveManager.getInstance().initialize(server);
        });

        // Save rotating trade state on server stop
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            RotatingTradeManager.getInstance().shutdown();
            GraveManager.getInstance().shutdown();
        });

        BoneRealmRegistry.register(); // Register Bone realm

        // Register Dragon Realm
        com.github.hitman20081.dagmod.dragon_realm.DragonRealmRegistry.register();

        // Register Pale Garden
        com.github.hitman20081.dagmod.pale_garden.PaleGardenRegistry.register();

        // Register Bone Realm Entities
        BoneRealmEntityRegistry.register();

        // Register Bone Realm chests
        BoneRealmChestRegistry.register();

        // Register boss death handler
        BossDeathEventHandler.register();

        // Register boss room proximity spawn handler (Skeleton Lord)
        com.github.hitman20081.dagmod.bone_realm.BossRoomSpawnHandler.register();

        // Register tutorial mob kill tracking
        registerTutorialMobKillTracking();

        ModBlocks.initialize();

        // Register custom structure placement types (must be before world creation)
        com.github.hitman20081.dagmod.world.ModStructurePlacements.register();

        // Set world spawn near Hall of Champions on first world load
        com.github.hitman20081.dagmod.world.HallSpawnInitializer.register();

        // Prevent block breaking inside protected structures (survival only)
        com.github.hitman20081.dagmod.world.ProtectedStructureHandler.register();

// Register ore generation in overworld
        com.github.hitman20081.dagmod.world.ModOreGeneration.register();

        // Register block entities
        com.github.hitman20081.dagmod.block.entity.ModBlockEntities.registerBlockEntities();

        // Register screen handlers
        com.github.hitman20081.dagmod.screen.ModScreenHandlers.registerScreenHandlers();

        // Register mod recipes
        com.github.hitman20081.dagmod.recipe.ModRecipes.registerRecipes();

        // Initialize Networking
        ModNetworking.initialize();

        // Register mana payloads
        com.github.hitman20081.dagmod.class_system.mana.ManaNetworking.registerPayloads();
        LOGGER.info("Mana system networking initialized!");

        // Register warrior ability systems
        registerWarriorSystems();

        // ===== ROGUE SYSTEM INITIALIZATION =====

        // Register energy networking packets
        PayloadTypeRegistry.clientboundPlay().register(
                EnergyNetworking.EnergySyncPayload.ID,
                EnergyNetworking.EnergySyncPayload.CODEC
        );

        ShadowBlendHandler.register();

        FortuneDustHandler.register();

        // Register custom enchantment effects (Midas Touch, Mud Collector, Tunneling, Lucky Looter)
        CustomEnchantmentEffects.register();
        com.github.hitman20081.dagmod.enchantment.HeartArmorHandler.register();

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
            GarrickRegistryCommand.register(dispatcher, registryAccess, environment);
            ProgressionTestCommand.register(dispatcher, registryAccess, environment);
            PartyCommand.register(dispatcher, registryAccess, environment);
            PartyQuestCommand.register(dispatcher, registryAccess, environment);
            LocateDragonCommand.register(dispatcher, registryAccess, environment);
            LocateBoneDungeonCommand.register(dispatcher, registryAccess, environment);
            LocateWildDragonCommand.register(dispatcher, registryAccess, environment);
            GraveCommand.register(dispatcher, registryAccess, environment);
            DragonRespawnCommand.register(dispatcher, registryAccess, environment);
            MerchantCommand.register(dispatcher, registryAccess, environment);
            ResourceCommand.register(dispatcher, registryAccess, environment);
            CooldownCommand.register(dispatcher, registryAccess, environment);
            SynergyCommand.register(dispatcher, registryAccess, environment);
            PlayerDataCommand.register(dispatcher, registryAccess, environment);
            SeasonsCommand.register(dispatcher, registryAccess, environment);

            // Ship Travel Command
            dispatcher.register(Commands.literal("travel")
                    .then(Commands.argument("destination",
                                    com.mojang.brigadier.arguments.StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                String destination = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "destination");

                                com.github.hitman20081.dagmod.travel.ShipTravelManager
                                        .travelToDestination(player, destination);

                                return 1;
                            })
                    )
            );

            // Summon Innkeeper Garrick Command (for testing/structure blocks)
            dispatcher.register(Commands.literal("summon_garrick")
                    // TODO: Re-add OP permission check using Fabric Permissions API
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();

                        // Spawn Garrick at player's location
                        com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC garrick =
                            new com.github.hitman20081.dagmod.entity.InnkeeperGarrickNPC(
                                ModEntities.INNKEEPER_GARRICK,
                                player.level()
                            );

                        garrick.snapTo(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            player.getYRot(),
                            0.0F
                        );

                        player.level().addFreshEntity(garrick);

                        player.sendSystemMessage(
                            Component.literal("Innkeeper Garrick summoned!").withStyle(ChatFormatting.GREEN));

                        return 1;
                    })
            );
        });

        // Apply class abilities when player respawns (including after death)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // Restore Soul Bound enchanted items to original slots
            java.util.List<ItemStack> soulItems = SoulBoundStorage.retrieve(newPlayer.getUUID());
            java.util.List<Integer> soulSlots = SoulBoundStorage.retrieveSlots(newPlayer.getUUID());
            if (soulItems != null) {
                for (int i = 0; i < soulItems.size(); i++) {
                    ItemStack item = soulItems.get(i);
                    if (soulSlots != null && i < soulSlots.size()) {
                        int slot = soulSlots.get(i);
                        if (newPlayer.getInventory().getItem(slot).isEmpty()) {
                            newPlayer.getInventory().setItem(slot, item);
                        } else {
                            newPlayer.addItem(item);
                        }
                    } else {
                        newPlayer.addItem(item);
                    }
                }
                newPlayer.sendSystemMessage(Component.literal("Your Soul Bound items have been preserved!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }

            ClassAbilityManager.applyClassAbilities(newPlayer);

            // Reapply progression stats (fixes health/attack/armor reset on death)
            PlayerProgressionData progressionData = ProgressionManager.getPlayerData(newPlayer);
            if (progressionData != null) {
                StatScalingHandler.applyLevelStats(newPlayer, progressionData.getCurrentLevel());
            }

            // Hall of Champions respawn is now handled by vanilla spawn point system
            // (set via HallRespawnBlock interaction)
        });

        // Load player data when entity loads (login, respawn, dimension change)
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player) {
                // Load persistent data first
                PlayerDataManager.loadPlayerData(player);

                // Load quest data
                QuestManager.getInstance().loadPlayerQuestData(player);

                // Load daily streak data
                com.github.hitman20081.dagmod.quest.daily.DailyStreakManager.load(
                        player.level().getServer(), player.getUUID());

                // Then apply abilities (already loaded by loadPlayerData, but this ensures sync)
                ClassAbilityManager.applyClassAbilities(player);
                RaceAbilityManager.applyRaceAbilities(player);
            }
        });

        // Give starter items ONLY on first join (not on dimension change or respawn)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;

            // Only give items to completely new players (no race/class data)
            if (!PlayerDataManager.hasPlayerData(player)) {
                player.addItem(QuestUtils.createWelcomeBook());

                player.sendSystemMessage(Component.literal("═══════════════════════════════")
                        .withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("Welcome to DAGMod!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal("═══════════════════════════════")
                        .withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("Find the Inn and speak to Innkeeper Garrick to begin.")
                        .withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("═══════════════════════════════")
                        .withStyle(ChatFormatting.GOLD));
            }
        });

        // Hook into entity death events for kill objectives
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Check if a player killed this entity
            if (damageSource.getEntity() instanceof ServerPlayer player) {
                // Update kill objectives for this player
                QuestManager.getInstance().updateKillProgress(player, entity.getType());

                // --- PARTY QUEST OBJECTIVE TRACKING ---
                com.github.hitman20081.dagmod.party.quest.PartyQuestData quest = com.github.hitman20081.dagmod.party.quest.PartyQuestManager.getInstance().getActiveQuest(player);
                if (quest != null) {
                    String mobType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();

                    // Handle KILL_ENTITY objectives
                    for (var objective : quest.getTemplate().getObjectives()) {
                        if (objective.getType() == com.github.hitman20081.dagmod.party.quest.PartyQuestObjectiveType.KILL_ENTITY) {
                            boolean isHostile = entity instanceof net.minecraft.world.entity.monster.Monster;
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
            if (entity instanceof ServerPlayer deadPlayer) {
                DeathMessageHandler.sendDeathMessage(deadPlayer);
            }
        });

        // Combined server tick events: Mana regeneration + Night Vision for Mages + Custom Armor Set Bonuses
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Tick party quest manager for timeouts
            PartyQuestManager.getInstance().tick();

            // Tick rotating trade manager for rotation checks
            RotatingTradeManager.getInstance().tick();

            // Wild Dragon Spawning (Overworld + Dragon Realm)
            tickCounter++;
            if (tickCounter >= DragonSpawner.getSpawnInterval()) {
                tickCounter = 0;
                for (ServerLevel world : server.getAllLevels()) {
                    DragonSpawner.trySpawnDragon(world);
                    DragonSpawner.trySpawnDragonInDragonRealm(world);
                }
            }

            // Tick Dragon Guardian respawn timer
            com.github.hitman20081.dagmod.dragon_realm.boss.DragonRespawnTimerManager.get(server).tick(server);


            // Update Solar Mending counter once per tick (before player loop)
            com.github.hitman20081.dagmod.class_system.armor.SolarMendingHandler.serverTick();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());

                // Mana regeneration for all players (ManaManager checks if they're Mage)
                ManaManager.tick(player);

                // Night Vision for Mages
                if ("Mage".equals(playerClass)) {
                    if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.NIGHT_VISION,
                                300, // 15 seconds
                                0,
                                true,  // ambient
                                false, // no particles
                                false  // no icon
                        ));
                    }
                }

                // Sync Warrior cooldowns to client once per second
                if ("Warrior".equals(playerClass) && player.level().getGameTime() % 20 == 0) {
                    CooldownNetworking.syncCooldownsToClient(player);
                }

                // Custom armor set bonuses (Dragonscale, Crystalforge, Inferno, Nature's Guard, Shadow, Fortuna)
                com.github.hitman20081.dagmod.class_system.armor.CustomArmorSetBonus.applySetBonuses(player);

                // Auto-enchant DAGMod armor pieces when first equipped
                com.github.hitman20081.dagmod.class_system.armor.ArmorEnchantmentHandler.tick(player);

                // Solar Mending: repair Solarweave armor in direct sunlight
                com.github.hitman20081.dagmod.class_system.armor.SolarMendingHandler.tick(player);
            }
        });

        // ========== PLAYER DISCONNECT HANDLER - SAVE ALL DATA ==========
        // This is CRITICAL for data persistence - saves all player progress on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.player;
            UUID playerId = player.getUUID();

            LOGGER.info("Saving data for disconnecting player: " + player.getName().getString());

            // Save race/class data
            PlayerDataManager.savePlayerData(player);

            // Save quest data
            QuestManager.getInstance().savePlayerQuestData(player);

            // Note: Progression data is saved/unloaded by ProgressionEvents.java

            // Unload daily streak data
            com.github.hitman20081.dagmod.quest.daily.DailyStreakManager.unload(playerId);

            // Clean up memory (prevent memory leaks)
            QuestManager.getInstance().clearPlayerData(playerId);
            ManaManager.clearPlayerData(playerId);
            EnergyManager.clearPlayerData(playerId);
            CooldownManager.clearPlayerCooldowns(playerId);

            LOGGER.info("Successfully saved and cleaned up data for: " + player.getName().getString());
        });

        // Register grave right-click interaction
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide()) return net.minecraft.world.InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return net.minecraft.world.InteractionResult.PASS;

            BlockPos clickedPos = hitResult.getBlockPos();
            if (world.getBlockState(clickedPos).getBlock() != net.minecraft.world.level.block.Blocks.LODESTONE) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            boolean handled = GraveManager.getInstance().collectGrave(serverPlayer, clickedPos);
            if (handled) {
                return net.minecraft.world.InteractionResult.SUCCESS;
            }

            return net.minecraft.world.InteractionResult.PASS;
        });

        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    Potions.LUCK,
                    Ingredient.of(Items.EXPERIENCE_BOTTLE),
                    BuiltInRegistries.POTION.wrapAsHolder(XP_POTION)
            );
            // Gem powder potions — brewed from Awkward Potion
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.AMETHYST_POWDER), ModPotions.AMETHYST_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.CITRINE_POWDER), ModPotions.CITRINE_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.DIAMOND_POWDER), ModPotions.DIAMOND_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.EMERALD_POWDER), ModPotions.EMERALD_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.QUARTZ_POWDER), ModPotions.QUARTZ_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.RUBY_POWDER), ModPotions.RUBY_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.SAPPHIRE_POWDER), ModPotions.SAPPHIRE_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(ModItems.TOPAZ_POWDER), ModPotions.TOPAZ_POTION);
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

        // Register cooldown sync packet
        CooldownNetworking.registerPayloads();

        LOGGER.info("Warrior Ability Systems registered successfully");
    }

    // Helper method for updating quest progress - THIS GOES OUTSIDE onInitialize()
    public static void updatePlayerQuestProgress(ServerPlayer player) {
        QuestManager.getInstance().updateQuestProgress(player);
    }

    /**
     * Copy bundled Bleakwind world data from mod resources to the server's dimension folder
     */
    private static void copyBundledBleakwindWorld(MinecraftServer server) {
        try {
            // The dimension folder path should match your dimension JSON location
            Path dimensionPath = server.getWorldPath(LevelResource.ROOT)
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
            if (entity.level().isClientSide()) {
                return;
            }

            // Check if killer is a player
            if (!(damageSource.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
                return;
            }

            // Only count hostile mobs (monsters)
            if (entity instanceof net.minecraft.world.entity.monster.Monster) {
                // Only track if player has completed Task 1, hasn't completed task 2 yet, and has met Garrick
                if (!PlayerDataManager.isTask2Complete(player.getUUID())
                        && PlayerDataManager.hasMetGarrick(player)
                        && PlayerDataManager.isTask1Complete(player.getUUID())) {
                    PlayerDataManager.incrementTask2MobKills(player);

                    // Send feedback to player
                    int kills = PlayerDataManager.getTask2MobKills(player.getUUID());
                    player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("✓ Tutorial Progress: " + kills + "/5 hostile mobs defeated")
                            .withStyle(net.minecraft.ChatFormatting.GRAY),
                        true // Action bar
                    );

                    // Notify when complete
                    if (kills >= 5) {
                        player.sendSystemMessage(
                            net.minecraft.network.chat.Component.literal("✓ Task Complete! Return to Innkeeper Garrick")
                                .withStyle(net.minecraft.ChatFormatting.GREEN, net.minecraft.ChatFormatting.BOLD));
                    }
                }
            }
        });
    }


} // This is the final closing brace of the DagMod class
