package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.command.InfoCommand;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.effect.ModEffects;
import com.github.hitman20081.dagmod.event.DeathMessageHandler;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.networking.ModNetworking;
import com.github.hitman20081.dagmod.potion.ModPotions;
import com.github.hitman20081.dagmod.entity.ModEntities;
import com.github.hitman20081.dagmod.block.ModBlocks;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.quest.registry.QuestRegistry;
import com.github.hitman20081.dagmod.race_system.PlayerTickHandler;
import com.github.hitman20081.dagmod.race_system.RaceAbilityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
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

        ModBlocks.initialize();

        // Initialize Networking
        ModNetworking.initialize();

        // Initialize Quest System
        LOGGER.info("Initializing Quest System for " + MOD_ID);
        QuestRegistry.registerQuests();
        QuestManager.getInstance();
        LOGGER.info("Quest System initialized successfully!");

        // Register race/class synergy ticker
        PlayerTickHandler.register();
        LOGGER.info("Race/Class synergy system initialized!");

        // Register command for class reset
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ResetClassCommand.register(dispatcher, registryAccess, environment);
        });

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ResetClassCommand.register(dispatcher, registryAccess, environment);
            InfoCommand.register(dispatcher, registryAccess, environment);
        });

        // Apply class abilities when player respawns (including after death)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ClassAbilityManager.applyClassAbilities(newPlayer);

            BlockPos hallPos = PlayerDataManager.loadHallLocation(newPlayer.getServer());

            if (hallPos != null) {
                // Schedule teleport for 5 ticks later (0.25 seconds)
                new Thread(() -> {
                    try {
                        Thread.sleep(250); // Wait 250ms
                        newPlayer.getServer().execute(() -> {
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
                    player.giveItemStack(new ItemStack(ModItems.HALL_LOCATOR));
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
        // Then replace the placeholder with:
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                String playerClass = ClassSelectionAltarBlock.getPlayerClass(player.getUuid());

                if ("Mage".equals(playerClass)) {
                    // Give Mages permanent Night Vision
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
            }
        });
        // Rogue critical hit and backstab system
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                if (entity instanceof LivingEntity target) {
                    // Note: We can't directly modify damage here, but we can track it
                    // The actual damage modification happens in a mixin or damage event

                    // For now, we'll handle this differently - see the mixin approach below
                }
            }
            return ActionResult.PASS;
        });

        // Rogue fall damage reduction
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && source.getName().equals("fall")) {
                float modifiedDamage = RogueCombatHandler.modifyFallDamage(player, amount);
                // Unfortunately, we can't modify damage in ALLOW_DAMAGE
                // We need to use a mixin for proper damage modification
            }
            return true;
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

    // Helper method for updating quest progress - THIS GOES OUTSIDE onInitialize()
    public static void updatePlayerQuestProgress(ServerPlayerEntity player) {
        QuestManager.getInstance().updateQuestProgress(player);
    }

}