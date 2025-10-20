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
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.networking.ModNetworking;
import com.github.hitman20081.dagmod.potion.ModPotions;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.progression.ProgressionEvents;
import com.github.hitman20081.dagmod.progression.ProgressionPackets;
import com.github.hitman20081.dagmod.progression.ProgressionTestCommand;
import com.github.hitman20081.dagmod.progression.XPEventHandler;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.QuestUtils;
import com.github.hitman20081.dagmod.quest.registry.QuestRegistry;
import com.github.hitman20081.dagmod.race_system.PlayerTickHandler;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.github.hitman20081.dagmod.command.ResetClassCommand;
import com.github.hitman20081.dagmod.class_system.ClassAbilityManager;
import com.github.hitman20081.dagmod.class_system.MagePotionHandler;
import com.github.hitman20081.dagmod.class_system.RogueCombatHandler;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;

import static com.github.hitman20081.dagmod.potion.ModPotions.XP_POTION;

public class DagMod implements ModInitializer {
    public static final String MOD_ID = "dagmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModItems.registerModItems();

        ModEffects.registerEffects();
        ModPotions.registerPotion();

        BoneRealmRegistry.register(); // Register Bone realm

        // Register Bone Realm Entities
        BoneRealmEntityRegistry.register();

        // Register Bone Realm chests
        BoneRealmChestRegistry.register();

        // Register boss death handler
        BossDeathEventHandler.register();

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

        // Register race/class synergy ticker
        PlayerTickHandler.register();
        LOGGER.info("Race/Class synergy system initialized!");

        // Register progression system events
        ProgressionEvents.register();

        // Register progression packets
        ProgressionPackets.registerServerPackets();

        // Register XP Event Handler
        XPEventHandler.register();

        // Register command for class reset
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ResetClassCommand.register(dispatcher, registryAccess, environment);
        });

        // Quest Command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            QuestCommand.register(dispatcher);
        });

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ResetClassCommand.register(dispatcher, registryAccess, environment);
            InfoCommand.register(dispatcher, registryAccess, environment);
        });

        // Progression Test Command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProgressionTestCommand.register(dispatcher, registryAccess, environment);
        });

        // Apply class abilities when player respawns (including after death)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ClassAbilityManager.applyClassAbilities(newPlayer);

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

        // Apply class abilities when player logs in
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                // Load persistent data first
                PlayerDataManager.loadPlayerData(player);

                // Give Hall Locator to new players
                if (!PlayerDataManager.hasPlayerData(player)) {
                    // Check if player already has a Hall Locator in their inventory
                    if (!hasHallLocator(player)) {
                        player.giveItemStack(new ItemStack(ModItems.HALL_LOCATOR));
                        player.giveItemStack(QuestUtils.createWelcomeBook());
                        player.giveItemStack(new ItemStack(ModItems.NOVICE_QUEST_BOOK));
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

                // Then apply abilities (already loaded by loadPlayerData, but this ensures sync)
                ClassAbilityManager.applyClassAbilities(player);
                RaceAbilityManager.applyRaceAbilities(player);
            }
        });

        // Hook into entity death events for kill objectives
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Check if a player killed this entity
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                // Update kill objectives for this player
                QuestManager.getInstance().updateKillProgress(player, entity.getType());
            }

            // Add death message for players
            if (entity instanceof ServerPlayerEntity deadPlayer) {
                DeathMessageHandler.sendDeathMessage(deadPlayer);
            }
        });

        // Combined server tick events: Mana regeneration + Night Vision for Mages + Custom Armor Set Bonuses
        ServerTickEvents.END_SERVER_TICK.register(server -> {
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
    } // Make sure this closing brace for onInitialize() is here

    /**
     * Register warrior ability systems
     */
    private void registerWarriorSystems() {
        LOGGER.info("Registering Warrior Ability Systems");

        // Register Shield Bash listener
        ShieldBashListener.register();

        // Clear cooldowns on player disconnect (cooldowns reset on logout)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            CooldownManager.clearPlayerCooldowns(handler.player.getUuid());
        });

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


} // This is the final closing brace of the DagMod class